package com.example.myapplication.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.DAO.AlarmDao;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Util.AlarmScheduler;
import java.util.Calendar;

public class CrudFragment extends Fragment {
 private int id; private Alarm alarm; private AlarmDao dao; private NumberPicker hour, minute; private EditText name; private CheckBox[] days; private SeekBar volume; private SwitchCompat random, loop, vibrate; private Spinner challenge; private TextView melody; private MediaPlayer preview; private final Handler previewHandler=new Handler(Looper.getMainLooper()); private final Runnable stopPreview=this::stopPreview;
 public CrudFragment(){super(R.layout.fragment_add_edit_alarm);} public static CrudFragment newInstance(int id){ CrudFragment f=new CrudFragment(); Bundle b=new Bundle(); b.putInt("id",id); f.setArguments(b); return f; }
 private final ActivityResultLauncher<Intent> musicPicker=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{if(result.getData()!=null&&result.getData().getData()!=null){Uri uri=result.getData().getData();try{requireContext().getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}alarm.setMusicUri(uri.toString());melody.setText("Sound  Device audio     Change");}});
 @Override public void onCreate(@Nullable Bundle b){super.onCreate(b); id=getArguments()==null?-1:getArguments().getInt("id",-1);}
 @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){ dao=new AlarmDao(requireContext()); hour=v.findViewById(R.id.picker_hour);minute=v.findViewById(R.id.picker_minute);name=v.findViewById(R.id.et_name);melody=v.findViewById(R.id.row_melody);volume=v.findViewById(R.id.seek_volume);random=v.findViewById(R.id.switch_random);loop=v.findViewById(R.id.switch_loop);vibrate=v.findViewById(R.id.switch_vibrate);challenge=v.findViewById(R.id.spinner_challenge); days=new CheckBox[]{v.findViewById(R.id.day_mon),v.findViewById(R.id.day_tue),v.findViewById(R.id.day_wed),v.findViewById(R.id.day_thu),v.findViewById(R.id.day_fri),v.findViewById(R.id.day_sat),v.findViewById(R.id.day_sun)}; hour.setMinValue(0);hour.setMaxValue(23);minute.setMinValue(0);minute.setMaxValue(59);style(hour);style(minute);for(CheckBox day:days)day.setTextColor(Color.WHITE);challenge.post(()->{if(challenge.getSelectedView() instanceof TextView)((TextView)challenge.getSelectedView()).setTextColor(Color.WHITE);}); Calendar c=Calendar.getInstance(); hour.setValue(c.get(Calendar.HOUR_OF_DAY));minute.setValue(c.get(Calendar.MINUTE)); alarm=id<0?new Alarm():dao.getAlarmbyId(id); bind(); v.findViewById(R.id.btn_close).setOnClickListener(x->getParentFragmentManager().popBackStack());v.findViewById(R.id.btn_save).setOnClickListener(x->save());v.findViewById(R.id.btn_delete).setOnClickListener(x->delete());melody.setOnClickListener(x->sounds()); }
 private void style(NumberPicker p){paint(p);p.setOnValueChangedListener((picker,oldValue,newValue)->picker.post(()->paint(picker)));}
 private void paint(NumberPicker p){for(int i=0;i<p.getChildCount();i++)if(p.getChildAt(i) instanceof EditText){EditText input=(EditText)p.getChildAt(i);input.setTextColor(Color.WHITE);input.setTextSize(27);input.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);}try{java.lang.reflect.Field f=NumberPicker.class.getDeclaredField("mSelectorWheelPaint");f.setAccessible(true);((Paint)f.get(p)).setColor(Color.WHITE);p.invalidate();}catch(Exception ignored){}}
 private void bind(){if(id>=0){getView().findViewById(R.id.btn_delete).setVisibility(View.VISIBLE); ((android.widget.TextView)getView().findViewById(R.id.tv_title)).setText("Edit alarm");} hour.setValue(alarm.getHour());minute.setValue(alarm.getMinute());name.setText(alarm.getLabel());volume.setProgress(alarm.getVolume());random.setChecked(alarm.getRandomMusic());loop.setChecked(alarm.getLoop());vibrate.setChecked(alarm.getVibrate());challenge.setSelection(alarm.getDismissMode());showMelody();String r=alarm.getRepeatDays()==null?"":alarm.getRepeatDays();for(int i=0;i<7;i++)days[i].setChecked(r.contains(String.valueOf(i)));}
 private void sounds(){String[] s={"Aurora","Daybreak","Pulse","Breeze","Orbit"};final int[] selected={alarm.getMusicId()};new AlertDialog.Builder(requireContext()).setTitle("Preview alarm sounds").setSingleChoiceItems(s,selected[0],(d,w)->{selected[0]=w;play(w);}).setNeutralButton("Device audio",(d,w)->pickMusic()).setNegativeButton("Cancel",(d,w)->stopPreview()).setPositiveButton("Use sound",(d,w)->{alarm.setMusicUri(null);alarm.setMusicId(selected[0]);showMelody();stopPreview();}).setOnDismissListener(d->stopPreview()).show();}
 private void showMelody(){String[] s={"Aurora","Daybreak","Pulse","Breeze","Orbit"};melody.setText(alarm.getMusicUri()!=null&&!alarm.getMusicUri().isEmpty()?"Sound  Device audio     Change":"Sound  " + s[Math.max(0,Math.min(alarm.getMusicId(),4))] + "                         Change");}
 private void pickMusic(){Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);intent.setType("audio/*");intent.addCategory(Intent.CATEGORY_OPENABLE);intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);musicPicker.launch(intent);}
 private void play(int index){stopPreview();try{int[] sounds={R.raw.alarm1,R.raw.alarm2,R.raw.alarm3,R.raw.alarm4,R.raw.alarm5};preview=MediaPlayer.create(requireContext(),sounds[index]);if(preview!=null){preview.setVolume(.45f,.45f);preview.start();previewHandler.postDelayed(stopPreview,Math.min(40000L,Math.max(0,preview.getDuration())));}}catch(Exception ignored){}}
 private void stopPreview(){previewHandler.removeCallbacks(stopPreview);if(preview!=null){try{preview.stop();}catch(Exception ignored){}preview.release();preview=null;}}
 private void save(){alarm.setHour(hour.getValue());alarm.setMinute(minute.getValue());alarm.setLabel(name.getText().toString().trim().isEmpty()?"Alarm":name.getText().toString().trim());alarm.setVolume(volume.getProgress());alarm.setRandomMusic(random.isChecked());alarm.setLoop(loop.isChecked());alarm.setVibrate(vibrate.isChecked());alarm.setDismissMode(challenge.getSelectedItemPosition());alarm.setEnabled(true);StringBuilder r=new StringBuilder();for(int i=0;i<7;i++)if(days[i].isChecked())r.append(i);alarm.setRepeatDays(r.toString());if(id<0)alarm.setId((int)dao.insertAlarm(alarm));else dao.updateAlarm(alarm);AlarmScheduler.schedule(requireContext(),alarm);getParentFragmentManager().popBackStack();}
 private void delete(){AlarmScheduler.cancel(requireContext(),id);dao.deleteAlarm(id);getParentFragmentManager().popBackStack();}
 @Override public void onDestroyView(){stopPreview();super.onDestroyView();}
}
