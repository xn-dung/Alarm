package com.example.myapplication.Fragment;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.EditText;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import java.util.Locale;
public class TimerFragment extends Fragment { private CountDownTimer timer; private long remaining; private TextView display; private Button start,cancel; public TimerFragment(){super(R.layout.fragment_timer);} @Override public void onViewCreated(View v,android.os.Bundle b){NumberPicker h=v.findViewById(R.id.picker_hours),m=v.findViewById(R.id.picker_minutes),s=v.findViewById(R.id.picker_seconds);display=v.findViewById(R.id.tv_countdown);start=v.findViewById(R.id.btn_start);cancel=v.findViewById(R.id.btn_cancel);h.setMinValue(0);h.setMaxValue(23);m.setMinValue(0);m.setMaxValue(59);s.setMinValue(0);s.setMaxValue(59);style(h);style(m);style(s);start.setOnClickListener(x->{if(timer==null){remaining=(h.getValue()*3600L+m.getValue()*60L+s.getValue())*1000;if(remaining>0)run();}else{timer.cancel();timer=null;start.setText("Resume");}});cancel.setOnClickListener(x->{if(timer!=null)timer.cancel();timer=null;display.setVisibility(View.GONE);start.setText("Start");});} private void style(NumberPicker picker){for(int i=0;i<picker.getChildCount();i++)if(picker.getChildAt(i) instanceof EditText){EditText input=(EditText)picker.getChildAt(i);input.setTextColor(Color.WHITE);input.setTextSize(25);input.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);}} private void run(){display.setVisibility(View.VISIBLE);timer=new CountDownTimer(remaining,250){public void onTick(long x){remaining=x;display.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",x/3600000,(x/60000)%60,(x/1000)%60));}public void onFinish(){timer=null;start.setText("Start");try{MediaPlayer player=MediaPlayer.create(requireContext(),R.raw.alarm1);if(player!=null)player.start();}catch(Exception ignored){}}}.start();start.setText("Pause");}}
