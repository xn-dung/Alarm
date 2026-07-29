package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.RecentTimerAdapter;
import com.example.myapplication.Database.TimerRecentDatabase;
import com.example.myapplication.Model.RecentTimer;
import com.example.myapplication.R;
import com.example.myapplication.Util.TimerScheduler;

import java.util.List;
import java.util.Locale;

public class TimerFragment extends Fragment {
    private CountDownTimer timer;
    private long remaining;
    private TextView display;
    private TextView melody;
    private TextView recentTitle;
    private EditText name;
    private Button start;
    private Button cancel;
    private int sound;
    private Uri customSound;
    private MediaPlayer player;
    private boolean ringing;
    private TimerRecentDatabase database;
    private NumberPicker hours;
    private NumberPicker minutes;
    private NumberPicker seconds;
    private RecyclerView recentList;
    private RecentTimerAdapter recentAdapter;

    public TimerFragment() {
        super(R.layout.fragment_timer);
    }

    private final ActivityResultLauncher<Intent> picker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null || result.getData().getData() == null) return;
                customSound = result.getData().getData();
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(customSound, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignored) {
                }
                melody.setText("Device audio");
            }
    );

    @Override
    public void onViewCreated(View view, android.os.Bundle state) {
        hours = view.findViewById(R.id.picker_hours);
        minutes = view.findViewById(R.id.picker_minutes);
        seconds = view.findViewById(R.id.picker_seconds);
        display = view.findViewById(R.id.tv_countdown);
        melody = view.findViewById(R.id.tv_timer_melody);
        recentTitle = view.findViewById(R.id.tv_recent_title);
        recentList = view.findViewById(R.id.rv_recent_timers);
        name = view.findViewById(R.id.et_timer_name);
        database = new TimerRecentDatabase(requireContext());
        recentAdapter = new RecentTimerAdapter(this::useRecent);
        recentList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recentList.setAdapter(recentAdapter);
        start = view.findViewById(R.id.btn_start);
        cancel = view.findViewById(R.id.btn_cancel);

        for (NumberPicker picker : new NumberPicker[]{hours, minutes, seconds}) {
            picker.setMinValue(0);
            picker.setMaxValue(picker == hours ? 23 : 59);
            picker.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
            style(picker);
        }

        showRecent();
        view.findViewById(R.id.row_timer_melody).setOnClickListener(v -> sounds());
        start.setOnClickListener(v -> toggleTimer());
        cancel.setOnClickListener(v -> cancelTimer());
    }

    private void toggleTimer() {
        if (ringing) { // "Stop": silence the finished timer and reset
            cancelTimer();
            return;
        }
        if (timer != null) { // pause, keep the remaining time
            timer.cancel();
            timer = null;
            TimerScheduler.cancel(requireContext());
            start.setText("Resume");
            return;
        }
        if (remaining <= 0) { // fresh start; a paused timer resumes with its own remaining
            remaining = (hours.getValue() * 3600L + minutes.getValue() * 60L + seconds.getValue()) * 1000L;
            if (remaining <= 0) return;
            database.save(remaining / 1000L, name.getText().toString().trim());
            showRecent();
        }
        run();
    }

    private void cancelTimer() {
        if (timer != null) timer.cancel();
        timer = null;
        remaining = 0;
        TimerScheduler.cancel(requireContext());
        stopPlayer();
        showPickers(true);
        start.setText("Start");
    }

    private void showPickers(boolean picking) {
        requireView().findViewById(R.id.layout_pickers).setVisibility(picking ? View.VISIBLE : View.GONE);
        requireView().findViewById(R.id.countdown_container).setVisibility(picking ? View.GONE : View.VISIBLE);
    }

    private void showRecent() {
        List<RecentTimer> entries = database.recent();
        recentTitle.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
        recentList.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
        recentAdapter.setTimers(entries);
    }

    private void useRecent(RecentTimer timer) {
        if (this.timer != null || ringing) return;
        remaining = 0;
        long value = timer.getSeconds();
        hours.setValue((int) (value / 3600));
        minutes.setValue((int) ((value / 60) % 60));
        seconds.setValue((int) (value % 60));
        name.setText(timer.getName());
    }

    private void style(NumberPicker picker) {
        picker.postDelayed(() -> applyPickerColor(picker), 80);
        picker.setOnValueChangedListener((view, oldValue, newValue) -> view.postDelayed(() -> applyPickerColor(view), 40));
        picker.setOnScrollListener((view, scrollState) -> {
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                view.postDelayed(() -> applyPickerColor(view), 80);
            }
        });
    }

    private void applyPickerColor(NumberPicker picker) {
        for (int i = 0; i < picker.getChildCount(); i++) {
            if (!(picker.getChildAt(i) instanceof EditText)) continue;
            EditText input = (EditText) picker.getChildAt(i);
            input.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            input.setTextSize(27);
            input.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            input.setAlpha(1f);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            picker.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        }
        picker.invalidate();
    }

    private void sounds() {
        String[] names = {"Aurora", "Daybreak", "Pulse", "Breeze", "Orbit"};
        int[] choice = {sound};
        new AlertDialog.Builder(requireContext())
                .setTitle("Timer sound")
                .setSingleChoiceItems(names, sound, (dialog, which) -> {
                    choice[0] = which;
                    preview(which);
                })
                .setNeutralButton("Device audio", (dialog, which) -> pickMusic())
                .setNegativeButton("Cancel", (dialog, which) -> stopPlayer())
                .setPositiveButton("Use sound", (dialog, which) -> {
                    sound = choice[0];
                    customSound = null;
                    melody.setText(names[sound]);
                })
                // Also stops the preview when the dialog is dismissed by tapping
                // outside; guarded so it never silences a finished timer's ring.
                .setOnDismissListener(dialog -> {
                    if (!ringing) stopPlayer();
                })
                .show();
    }

    private void pickMusic() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        picker.launch(intent);
    }

    private AudioAttributes attributes() {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }

    private void preview(int index) {
        stopPlayer();
        int[] ids = {R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5};
        player = MediaPlayer.create(requireContext(), ids[index], attributes(), AudioManager.AUDIO_SESSION_ID_GENERATE);
        if (player == null) return;
        player.setVolume(.45f, .45f);
        player.start();
    }

    private void run() {
        TimerScheduler.schedule(requireContext(), remaining);
        showPickers(false);
        timer = new CountDownTimer(remaining, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                remaining = millisUntilFinished;
                display.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", remaining / 3600000, (remaining / 60000) % 60, (remaining / 1000) % 60));
            }

            @Override
            public void onFinish() {
                timer = null;
                remaining = 0;
                display.setText("00:00:00");
                start.setText("Stop");
                ring();
            }
        }.start();
        start.setText("Pause");
    }

    private void ring() {
        stopPlayer();
        try {
            if (customSound != null) {
                player = MediaPlayer.create(requireContext(), customSound, null, attributes(), AudioManager.AUDIO_SESSION_ID_GENERATE);
            } else {
                int[] ids = {R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5};
                player = MediaPlayer.create(requireContext(), ids[sound], attributes(), AudioManager.AUDIO_SESSION_ID_GENERATE);
            }
            // Fall back to the built-in sound if the picked device audio failed to open
            if (player == null && customSound != null) {
                int[] ids = {R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5};
                player = MediaPlayer.create(requireContext(), ids[sound], attributes(), AudioManager.AUDIO_SESSION_ID_GENERATE);
            }
            if (player != null) {
                player.setLooping(true);
                player.start();
                ringing = true;
            }
        } catch (Exception ignored) {
        }
    }

    private void stopPlayer() {
        ringing = false;
        if (player == null) return;
        try {
            player.stop();
        } catch (Exception ignored) {
        }
        player.release();
        player = null;
    }

    @Override
    public void onDestroyView() {
        if (timer != null) timer.cancel();
        stopPlayer();
        super.onDestroyView();
    }
}
