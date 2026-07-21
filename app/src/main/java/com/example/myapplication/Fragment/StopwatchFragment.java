package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import java.util.Locale;

public class StopwatchFragment extends Fragment {
    private TextView tvStopwatch;
    private Button btnStartPause, btnReset;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = false;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updateTime % 1000) / 10;
            tvStopwatch.setText(String.format(Locale.getDefault(), "%02d:%02d.%02d", mins, secs, milliseconds));
            handler.postDelayed(this, 10);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        tvStopwatch = view.findViewById(R.id.tv_stopwatch);
        btnStartPause = view.findViewById(R.id.btn_start_pause);
        btnReset = view.findViewById(R.id.btn_reset);

        btnStartPause.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
                btnStartPause.setText("Pause");
                isRunning = true;
            } else {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
                btnStartPause.setText("Start");
                isRunning = false;
            }
        });

        btnReset.setOnClickListener(v -> {
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updateTime = 0L;
            tvStopwatch.setText("00:00.00");
            if (isRunning) {
                handler.removeCallbacks(updateTimerThread);
                btnStartPause.setText("Start");
                isRunning = false;
            }
        });
        return view;
    }
}