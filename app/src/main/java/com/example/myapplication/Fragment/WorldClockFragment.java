package com.example.myapplication.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.WorldClockAdapter;
import com.example.myapplication.Model.WorldClock;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class WorldClockFragment extends Fragment {
    private static final String[] ZONES = {"Asia/Ho_Chi_Minh", "Asia/Tokyo", "Asia/Seoul", "Asia/Singapore", "Europe/London", "Europe/Paris", "America/New_York", "America/Los_Angeles", "Australia/Sydney"};
    private static final String[] CITIES = {"Vietnam", "Tokyo", "Seoul", "Singapore", "London", "Paris", "New York", "Los Angeles", "Sydney"};
    private WorldClockAdapter adapter;
    private final android.os.Handler clockHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            showWorldClocks();
            clockHandler.postDelayed(this, 1000);
        }
    };

    private void startTicking() {
        clockHandler.removeCallbacks(tick);
        clockHandler.post(tick);
    }

    private void stopTicking() {
        clockHandler.removeCallbacks(tick);
    }

    // MainActivity switches tabs with show/hide, so onResume alone is not enough
    // to know when this screen appears or disappears.
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) stopTicking(); else startTicking();
    }

    @Override
    public void onPause() {
        stopTicking();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        stopTicking();
        super.onDestroyView();
    }

    public WorldClockFragment() {
        super(R.layout.fragment_world_clock);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new WorldClockAdapter();
        RecyclerView list = view.findViewById(R.id.rv_world_clocks);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        view.findViewById(R.id.btn_manage_world).setOnClickListener(v -> showWorldPicker());
        showWorldClocks();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null && !isHidden()) startTicking();
    }

    private SharedPreferences preferences() {
        return requireContext().getSharedPreferences("world_clocks", 0);
    }

    private LinkedHashSet<String> selectedZones() {
        Set<String> saved = preferences().getStringSet("zones", null);
        LinkedHashSet<String> zones = new LinkedHashSet<>();
        zones.add(ZONES[0]);
        if (saved != null) zones.addAll(saved);
        return zones;
    }

    private void showWorldClocks() {
        Set<String> selected = selectedZones();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        ArrayList<WorldClock> clocks = new ArrayList<>();
        for (int i = 0; i < ZONES.length; i++) {
            if (!selected.contains(ZONES[i])) continue;
            format.setTimeZone(TimeZone.getTimeZone(ZONES[i]));
            clocks.add(new WorldClock(CITIES[i], format.format(new Date()), ZONES[i].replace('_', ' ')));
        }
        adapter.setClocks(clocks);
    }

    private void showWorldPicker() {
        LinkedHashSet<String> selected = selectedZones();
        boolean[] checked = new boolean[ZONES.length];
        for (int i = 0; i < ZONES.length; i++) checked[i] = selected.contains(ZONES[i]);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Choose cities")
                .setMultiChoiceItems(CITIES, checked, (d, which, isChecked) -> {
                    if (which == 0 && !isChecked) {
                        ((AlertDialog) d).getListView().setItemChecked(0, true);
                        Toast.makeText(requireContext(), "Vietnam is always shown", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isChecked && selected.size() == 5) {
                        ((AlertDialog) d).getListView().setItemChecked(which, false);
                        Toast.makeText(requireContext(), "You can keep up to five cities", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isChecked) selected.add(ZONES[which]); else selected.remove(ZONES[which]);
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (d, w) -> {
                    preferences().edit().putStringSet("zones", new LinkedHashSet<>(selected)).apply();
                    showWorldClocks();
                })
                .create();
        dialog.show();
    }
}
