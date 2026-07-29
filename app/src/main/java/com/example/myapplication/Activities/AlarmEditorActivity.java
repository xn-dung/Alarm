package com.example.myapplication.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.DAO.AlarmDao;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Util.AlarmScheduler;
import com.example.myapplication.Util.NumberPickerStyler;

import java.util.Calendar;
import java.util.Locale;

public class AlarmEditorActivity extends AppCompatActivity {
    public static final String EXTRA_ALARM_ID = "alarm_id";

    private int id;
    private Alarm alarm;
    private AlarmDao dao;
    private NumberPicker hour;
    private NumberPicker minute;
    private EditText name;
    private CheckBox[] days;
    private SeekBar volume;
    private SwitchCompat random;
    private SwitchCompat loop;
    private SwitchCompat vibrate;
    private Spinner challenge;
    private TextView melody;
    private MediaPlayer preview;
    private final Handler previewHandler = new Handler(Looper.getMainLooper());
    private final Runnable stopPreview = this::stopPreview;

    private final ActivityResultLauncher<Intent> musicPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null || result.getData().getData() == null) return;
                Uri uri = result.getData().getData();
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignored) {
                }
                alarm.setMusicUri(uri.toString());
                melody.setText("Sound  Device audio     Change");
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_alarm_editor);
        id = getIntent().getIntExtra(EXTRA_ALARM_ID, -1);
        dao = new AlarmDao(this);
        hour = findViewById(R.id.picker_hour);
        minute = findViewById(R.id.picker_minute);
        name = findViewById(R.id.et_name);
        melody = findViewById(R.id.row_melody);
        volume = findViewById(R.id.seek_volume);
        random = findViewById(R.id.switch_random);
        loop = findViewById(R.id.switch_loop);
        vibrate = findViewById(R.id.switch_vibrate);
        challenge = findViewById(R.id.spinner_challenge);
        days = new CheckBox[]{
                findViewById(R.id.day_mon), findViewById(R.id.day_tue),
                findViewById(R.id.day_wed), findViewById(R.id.day_thu),
                findViewById(R.id.day_fri), findViewById(R.id.day_sat),
                findViewById(R.id.day_sun)
        };

        hour.setMinValue(0);
        hour.setMaxValue(23);
        hour.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
        minute.setMinValue(0);
        minute.setMaxValue(59);
        minute.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
        NumberPickerStyler.apply(hour);
        NumberPickerStyler.apply(minute);

        int primaryTextColor = ContextCompat.getColor(this, R.color.text_primary);
        for (CheckBox day : days) day.setTextColor(primaryTextColor);
        challenge.post(() -> {
            if (challenge.getSelectedView() instanceof TextView) {
                ((TextView) challenge.getSelectedView()).setTextColor(
                        ContextCompat.getColor(this, R.color.text_primary));
            }
        });

        Calendar calendar = Calendar.getInstance();
        hour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minute.setValue(calendar.get(Calendar.MINUTE));
        alarm = id < 0 ? new Alarm() : dao.getAlarmbyId(id);
        if (alarm == null) {
            id = -1;
            alarm = new Alarm();
        }
        bind();

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> save());
        findViewById(R.id.btn_delete).setOnClickListener(v -> delete());
        melody.setOnClickListener(v -> sounds());
    }

    private void bind() {
        if (id >= 0) {
            findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_title)).setText("Edit alarm");
        }
        hour.setValue(alarm.getHour());
        minute.setValue(alarm.getMinute());
        name.setText(alarm.getLabel());
        volume.setProgress(alarm.getVolume());
        random.setChecked(alarm.getRandomMusic());
        loop.setChecked(alarm.getLoop());
        vibrate.setChecked(alarm.getVibrate());
        challenge.setSelection(alarm.getDismissMode());
        showMelody();

        String repeat = alarm.getRepeatDays() == null ? "" : alarm.getRepeatDays();
        for (int i = 0; i < 7; i++) days[i].setChecked(repeat.contains(String.valueOf(i)));
    }

    private void sounds() {
        String[] sounds = {"Aurora", "Daybreak", "Pulse", "Breeze", "Orbit"};
        int[] selected = {alarm.getMusicId()};
        new AlertDialog.Builder(this)
                .setTitle("Preview alarm sounds")
                .setSingleChoiceItems(sounds, selected[0], (dialog, which) -> {
                    selected[0] = which;
                    play(which);
                })
                .setNeutralButton("Device audio", (dialog, which) -> pickMusic())
                .setNegativeButton("Cancel", (dialog, which) -> stopPreview())
                .setPositiveButton("Use sound", (dialog, which) -> {
                    alarm.setMusicUri(null);
                    alarm.setMusicId(selected[0]);
                    showMelody();
                    stopPreview();
                })
                .setOnDismissListener(dialog -> stopPreview())
                .show();
    }

    private void showMelody() {
        String[] sounds = {"Aurora", "Daybreak", "Pulse", "Breeze", "Orbit"};
        if (alarm.getMusicUri() != null && !alarm.getMusicUri().isEmpty()) {
            melody.setText("Sound  Device audio     Change");
            return;
        }

        int index = Math.max(0, Math.min(alarm.getMusicId(), 4));
        melody.setText("Sound  " + sounds[index] + "                         Change");
    }

    private void pickMusic() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        musicPicker.launch(intent);
    }

    private void play(int index) {
        stopPreview();
        try {
            int[] sounds = {R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5};
            preview = MediaPlayer.create(this, sounds[index]);
            if (preview == null) return;
            preview.setVolume(.45f, .45f);
            preview.start();
            previewHandler.postDelayed(stopPreview, Math.min(40000L, Math.max(0, preview.getDuration())));
        } catch (Exception ignored) {
        }
    }

    private void stopPreview() {
        previewHandler.removeCallbacks(stopPreview);
        if (preview == null) return;
        try {
            preview.stop();
        } catch (Exception ignored) {
        }
        preview.release();
        preview = null;
    }

    private void save() {
        alarm.setHour(hour.getValue());
        alarm.setMinute(minute.getValue());
        String label = name.getText().toString().trim();
        alarm.setLabel(label.isEmpty() ? "Alarm" : label);
        alarm.setVolume(volume.getProgress());
        alarm.setRandomMusic(random.isChecked());
        alarm.setLoop(loop.isChecked());
        alarm.setVibrate(vibrate.isChecked());
        alarm.setDismissMode(challenge.getSelectedItemPosition());
        alarm.setEnabled(true);

        StringBuilder repeat = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (days[i].isChecked()) repeat.append(i);
        }
        alarm.setRepeatDays(repeat.toString());

        if (id < 0) alarm.setId((int) dao.insertAlarm(alarm));
        else dao.updateAlarm(alarm);
        AlarmScheduler.schedule(this, alarm);
        setResult(RESULT_OK);
        finish();
    }

    private void delete() {
        AlarmScheduler.cancel(this, id);
        dao.deleteAlarm(id);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        stopPreview();
        super.onDestroy();
    }
}
