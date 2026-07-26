package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DAO.AlarmDao;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Util.AlarmScheduler;

import java.util.Calendar;
import java.util.Locale;

public class CrudFragment extends Fragment {
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

    public CrudFragment() {
        super(R.layout.fragment_add_edit_alarm);
    }

    public static CrudFragment newInstance(int id) {
        CrudFragment fragment = new CrudFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final ActivityResultLauncher<Intent> musicPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null || result.getData().getData() == null) return;
                Uri uri = result.getData().getData();
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignored) {
                }
                alarm.setMusicUri(uri.toString());
                melody.setText("Sound  Device audio     Change");
            }
    );

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        id = getArguments() == null ? -1 : getArguments().getInt("id", -1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        dao = new AlarmDao(requireContext());
        hour = view.findViewById(R.id.picker_hour);
        minute = view.findViewById(R.id.picker_minute);
        name = view.findViewById(R.id.et_name);
        melody = view.findViewById(R.id.row_melody);
        volume = view.findViewById(R.id.seek_volume);
        random = view.findViewById(R.id.switch_random);
        loop = view.findViewById(R.id.switch_loop);
        vibrate = view.findViewById(R.id.switch_vibrate);
        challenge = view.findViewById(R.id.spinner_challenge);
        days = new CheckBox[]{
                view.findViewById(R.id.day_mon), view.findViewById(R.id.day_tue),
                view.findViewById(R.id.day_wed), view.findViewById(R.id.day_thu),
                view.findViewById(R.id.day_fri), view.findViewById(R.id.day_sat),
                view.findViewById(R.id.day_sun)
        };

        hour.setMinValue(0);
        hour.setMaxValue(23);
        hour.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
        minute.setMinValue(0);
        minute.setMaxValue(59);
        minute.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
        style(hour);
        style(minute);

        for (CheckBox day : days) day.setTextColor(Color.WHITE);
        challenge.post(() -> {
            if (challenge.getSelectedView() instanceof TextView) {
                ((TextView) challenge.getSelectedView()).setTextColor(Color.WHITE);
            }
        });

        Calendar calendar = Calendar.getInstance();
        hour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minute.setValue(calendar.get(Calendar.MINUTE));
        alarm = id < 0 ? new Alarm() : dao.getAlarmbyId(id);
        bind();

        view.findViewById(R.id.btn_close).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btn_save).setOnClickListener(v -> save());
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> delete());
        melody.setOnClickListener(v -> sounds());
    }

    private void style(NumberPicker picker) {
        picker.postDelayed(() -> paint(picker), 80);
        picker.setOnValueChangedListener((view, oldValue, newValue) -> view.postDelayed(() -> paint(view), 40));
        picker.setOnScrollListener((view, scrollState) -> {
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                view.postDelayed(() -> paint(view), 80);
            }
        });
    }

    private void paint(NumberPicker picker) {
        for (int i = 0; i < picker.getChildCount(); i++) {
            if (!(picker.getChildAt(i) instanceof EditText)) continue;
            EditText input = (EditText) picker.getChildAt(i);
            input.setTextColor(Color.WHITE);
            input.setTextSize(27);
            input.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            input.setAlpha(1f);
        }
        try {
            java.lang.reflect.Field field = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
            field.setAccessible(true);
            ((Paint) field.get(picker)).setColor(Color.WHITE);
            picker.invalidate();
        } catch (Exception ignored) {
        }
    }

    private void bind() {
        if (id >= 0) {
            getView().findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.tv_title)).setText("Edit alarm");
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
        new AlertDialog.Builder(requireContext())
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
            preview = MediaPlayer.create(requireContext(), sounds[index]);
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
        AlarmScheduler.schedule(requireContext(), alarm);
        getParentFragmentManager().popBackStack();
    }

    private void delete() {
        AlarmScheduler.cancel(requireContext(), id);
        dao.deleteAlarm(id);
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        stopPreview();
        super.onDestroyView();
    }
}
