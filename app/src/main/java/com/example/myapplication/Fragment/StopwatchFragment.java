package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import java.util.Locale;

public class StopwatchFragment extends Fragment {
    private TextView tvStopwatch;
    private TextView lapsTitle;
    private Button btnStartPause, btnReset, btnLap;
    private LinearLayout lapList;
    private ScrollView lapScroll;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private long lastLapTime = 0L;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = false;
    private int lapCount = 0;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            tvStopwatch.setText(formatTime(updateTime));
            handler.postDelayed(this, 10);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        tvStopwatch = view.findViewById(R.id.tv_stopwatch);
        lapsTitle = view.findViewById(R.id.tv_laps_title);
        btnStartPause = view.findViewById(R.id.btn_start_pause);
        btnReset = view.findViewById(R.id.btn_reset);
        btnLap = view.findViewById(R.id.btn_lap);
        lapList = view.findViewById(R.id.lap_list);
        lapScroll = view.findViewById(R.id.lap_scroll);

        btnStartPause.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
                btnStartPause.setText("Pause");
                btnLap.setEnabled(true);
                isRunning = true;
            } else {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                timeSwapBuff += timeInMilliseconds;
                updateTime = timeSwapBuff;
                handler.removeCallbacks(updateTimerThread);
                btnStartPause.setText("Start");
                btnLap.setEnabled(false);
                isRunning = false;
            }
        });

        btnLap.setOnClickListener(v -> recordLap());

        btnReset.setOnClickListener(v -> {
            handler.removeCallbacks(updateTimerThread);
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updateTime = 0L;
            lastLapTime = 0L;
            lapCount = 0;
            tvStopwatch.setText("00:00.00");
            btnStartPause.setText("Start");
            btnLap.setEnabled(false);
            isRunning = false;
            lapList.removeAllViews();
            lapsTitle.setVisibility(View.GONE);
            lapScroll.setVisibility(View.GONE);
        });
        return view;
    }

    private void recordLap() {
        if (!isRunning) return;

        long totalTime = timeSwapBuff + (SystemClock.uptimeMillis() - startTime);
        long lapTime = totalTime - lastLapTime;
        lastLapTime = totalTime;
        lapCount++;

        View item = getLayoutInflater().inflate(R.layout.item_stopwatch_lap, lapList, false);
        ((TextView) item.findViewById(R.id.tv_lap_number)).setText(
                String.format(Locale.getDefault(), "Lap %d", lapCount)
        );
        ((TextView) item.findViewById(R.id.tv_lap_time)).setText(formatTime(lapTime));
        lapList.addView(item);

        lapsTitle.setVisibility(View.VISIBLE);
        lapScroll.setVisibility(View.VISIBLE);
        lapScroll.post(() -> lapScroll.fullScroll(View.FOCUS_DOWN));
    }

    private String formatTime(long millisecondsValue) {
        int seconds = (int) (millisecondsValue / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        int hundredths = (int) (millisecondsValue % 1000) / 10;
        return String.format(
                Locale.getDefault(),
                "%02d:%02d.%02d",
                minutes,
                seconds,
                hundredths
        );
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(updateTimerThread);
        super.onDestroyView();
    }
}
