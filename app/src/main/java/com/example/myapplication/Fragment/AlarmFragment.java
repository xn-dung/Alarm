package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Adapter.AlarmAdapter;
import com.example.myapplication.DAO.AlarmDao;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Util.AlarmScheduler;
import java.util.List;
import java.util.Locale;

public class AlarmFragment extends Fragment {
    private AlarmDao dao;
    private AlarmAdapter adapter;
    public AlarmFragment() { super(R.layout.fragment_alarm); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        dao = new AlarmDao(requireContext());
        adapter = new AlarmAdapter(a -> edit(a.getId()), this::setEnabled);
        RecyclerView list = view.findViewById(R.id.rv_alarms);
        list.setLayoutManager(new LinearLayoutManager(requireContext())); list.setAdapter(adapter);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> edit(-1));
        load();
    }
    @Override public void onResume() { super.onResume(); if (adapter != null) load(); }
    private void load() {
        List<Alarm> alarms = dao.getAllAlarms();
        adapter.setAlarmList(alarms);
        View root = getView();
        if (root == null) return;
        TextView time = root.findViewById(R.id.tv_next_time);
        TextView detail = root.findViewById(R.id.tv_next_detail);
        Alarm next = null;
        for (Alarm alarm : alarms) if (alarm.getEnabled()) { next = alarm; break; }
        if (next == null) {
            time.setText("No alarm set");
            detail.setText("Add one and let us wake you gently");
        } else {
            time.setText(String.format(Locale.getDefault(), "%02d:%02d", next.getHour(), next.getMinute()));
            detail.setText(next.getLabel() == null || next.getLabel().isEmpty() ? "Alarm is ready" : next.getLabel());
        }
    }
    private void edit(int id) { getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, CrudFragment.newInstance(id)).addToBackStack(null).commit(); }
    private void setEnabled(Alarm alarm) { dao.updateAlarm(alarm); if (alarm.getEnabled()) AlarmScheduler.schedule(requireContext(), alarm); else AlarmScheduler.cancel(requireContext(), alarm.getId()); }
}
