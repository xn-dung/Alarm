package com.example.myapplication.Fragment;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import java.util.Locale;
public class TimerFragment extends Fragment {
 private CountDownTimer timer; private long remaining; private TextView display,melody; private Button start,cancel; private int sound=0; private Uri customSound; private MediaPlayer player;
 public TimerFragment(){super(R.layout.fragment_timer);}
 private final ActivityResultLauncher<Intent> picker=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->{if(r.getData()!=null&&r.getData().getData()!=null){customSound=r.getData().getData();try{requireContext().getContentResolver().takePersistableUriPermission(customSound,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}melody.setText("Device audio");}});
 @Override public void onViewCreated(View v,android.os.Bundle b){NumberPicker h=v.findViewById(R.id.picker_hours),m=v.findViewById(R.id.picker_minutes),s=v.findViewById(R.id.picker_seconds);display=v.findViewById(R.id.tv_countdown);melody=v.findViewById(R.id.tv_timer_melody);start=v.findViewById(R.id.btn_start);cancel=v.findViewById(R.id.btn_cancel);for(NumberPicker p:new NumberPicker[]{h,m,s}){p.setMinValue(0);p.setMaxValue(p==h?23:59);style(p);}v.findViewById(R.id.row_timer_melody).setOnClickListener(x->sounds());start.setOnClickListener(x->{if(timer==null){remaining=(h.getValue()*3600L+m.getValue()*60L+s.getValue())*1000;if(remaining>0)run();}else{timer.cancel();timer=null;start.setText("Resume");}});cancel.setOnClickListener(x->{if(timer!=null)timer.cancel();timer=null;display.setVisibility(View.GONE);start.setText("Start");});}
 private void style(NumberPicker p){applyPickerColor(p);p.setOnValueChangedListener((picker,oldValue,newValue)->picker.post(()->applyPickerColor(picker)));}
 private void applyPickerColor(NumberPicker p){for(int i=0;i<p.getChildCount();i++)if(p.getChildAt(i) instanceof EditText){EditText e=(EditText)p.getChildAt(i);e.setTextColor(Color.WHITE);e.setTextSize(25);e.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);}try{java.lang.reflect.Field field=NumberPicker.class.getDeclaredField("mSelectorWheelPaint");field.setAccessible(true);Paint paint=(Paint)field.get(p);paint.setColor(Color.WHITE);p.invalidate();}catch(Exception ignored){}}
 private void sounds(){String[] names={"Aurora","Daybreak","Pulse","Breeze","Orbit"};final int[] choice={sound};new AlertDialog.Builder(requireContext()).setTitle("Timer sound").setSingleChoiceItems(names,sound,(d,w)->{choice[0]=w;preview(w);}).setNeutralButton("Device audio",(d,w)->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);picker.launch(i);}).setNegativeButton("Cancel",null).setPositiveButton("Use sound",(d,w)->{sound=choice[0];customSound=null;melody.setText(names[sound]);}).show();}
 private void preview(int index){stopPlayer();int[] ids={R.raw.alarm1,R.raw.alarm2,R.raw.alarm3,R.raw.alarm4,R.raw.alarm5};player=MediaPlayer.create(requireContext(),ids[index]);if(player!=null){player.setVolume(.45f,.45f);player.start();}}
 private void run(){display.setVisibility(View.VISIBLE);timer=new CountDownTimer(remaining,250){public void onTick(long x){remaining=x;display.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",x/3600000,(x/60000)%60,(x/1000)%60));}public void onFinish(){timer=null;start.setText("Start");ring();}}.start();start.setText("Pause");}
 private void ring(){stopPlayer();try{if(customSound!=null)player=MediaPlayer.create(requireContext(),customSound);else{int[] ids={R.raw.alarm1,R.raw.alarm2,R.raw.alarm3,R.raw.alarm4,R.raw.alarm5};player=MediaPlayer.create(requireContext(),ids[sound]);}if(player!=null){player.setLooping(true);player.start();}}catch(Exception ignored){}}
 private void stopPlayer(){if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}}
 @Override public void onDestroyView(){if(timer!=null)timer.cancel();stopPlayer();super.onDestroyView();}
}
