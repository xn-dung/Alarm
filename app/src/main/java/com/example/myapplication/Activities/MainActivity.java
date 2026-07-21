package com.example.myapplication.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.Fragment.AlarmFragment;
import com.example.myapplication.Fragment.StopwatchFragment;
import com.example.myapplication.Fragment.TimerFragment;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_alarm) show(new AlarmFragment());
            else if (item.getItemId() == R.id.nav_timer) show(new TimerFragment());
            else show(new StopwatchFragment());
            return true;
        });
        if (savedInstanceState == null) {
            show(new AlarmFragment());
            nav.setSelectedItemId(R.id.nav_alarm);
        }
    }
    private void show(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
