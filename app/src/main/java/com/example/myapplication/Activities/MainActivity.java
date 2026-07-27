package com.example.myapplication.Activities;

import android.os.Bundle;
import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.Fragment.AlarmFragment;
import com.example.myapplication.Fragment.StopwatchFragment;
import com.example.myapplication.Fragment.TimerFragment;
import com.example.myapplication.Fragment.WorldClockFragment;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_alarm) show("alarm");
            else if (item.getItemId() == R.id.nav_world) show("world");
            else if (item.getItemId() == R.id.nav_timer) show("timer");
            else show("stopwatch");
            return true;
        });
        if (savedInstanceState == null) nav.setSelectedItemId(R.id.nav_alarm);
        requestAlarmPermissions();
    }
    private void requestAlarmPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 9);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!manager.canScheduleExactAlarms()) startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
        }
    }
    // Show/hide keeps each tab's fragment alive so a running timer or stopwatch
    // survives tab switches (a replaced TimerFragment would cancel its countdown).
    private void show(String tag) {
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction t = fm.beginTransaction();
        for (String other : new String[]{"alarm", "world", "timer", "stopwatch"}) {
            androidx.fragment.app.Fragment f = fm.findFragmentByTag(other);
            if (f != null && !other.equals(tag)) t.hide(f);
        }
        androidx.fragment.app.Fragment target = fm.findFragmentByTag(tag);
        if (target == null) {
            if (tag.equals("alarm")) target = new AlarmFragment();
            else if (tag.equals("world")) target = new WorldClockFragment();
            else if (tag.equals("timer")) target = new TimerFragment();
            else target = new StopwatchFragment();
            t.add(R.id.fragment_container, target, tag);
        }
        t.show(target).commit();
    }
}
