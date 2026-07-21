package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter
        extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private final List<Alarm> alarmList = new ArrayList<>();

    private final OnAlarmClickListener onAlarmClickListener;
    private final OnAlarmEnabledChangeListener onEnabledChangeListener;

    public AlarmAdapter(
            OnAlarmClickListener onAlarmClickListener,
            OnAlarmEnabledChangeListener onEnabledChangeListener
    ) {
        this.onAlarmClickListener = onAlarmClickListener;
        this.onEnabledChangeListener = onEnabledChangeListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);

        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AlarmViewHolder holder,
            int position
    ) {
        Alarm alarm = alarmList.get(position);

        String formattedTime = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                alarm.getHour(),
                alarm.getMinute()
        );

        holder.tvTime.setText(formattedTime);

        String label = alarm.getLabel();

        if (label == null || label.trim().isEmpty()) {
            holder.tvLabel.setText("Báo thức");
        } else {
            holder.tvLabel.setText(label);
        }

        /*
         * RecyclerView tái sử dụng item.
         * Phải xóa listener cũ trước khi gọi setChecked().
         */
        holder.switchEnabled.setOnCheckedChangeListener(null);
        holder.switchEnabled.setChecked(alarm.getEnabled());

        holder.switchEnabled.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    alarm.setEnabled(isChecked);

                    if (onEnabledChangeListener != null) {
                        onEnabledChangeListener.onEnabledChanged(alarm);
                    }
                }
        );

        holder.itemView.setOnClickListener(view -> {
            if (onAlarmClickListener != null) {
                onAlarmClickListener.onAlarmClick(alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void setAlarmList(List<Alarm> alarms) {
        alarmList.clear();

        if (alarms != null) {
            alarmList.addAll(alarms);
        }

        notifyDataSetChanged();
    }

    public Alarm getAlarmAt(int position) {
        return alarmList.get(position);
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTime;
        private final TextView tvLabel;
        private final SwitchCompat switchEnabled;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time);
            tvLabel = itemView.findViewById(R.id.tv_label);
            switchEnabled = itemView.findViewById(
                    R.id.switch_enabled
            );
        }
    }

    public interface OnAlarmClickListener {
        void onAlarmClick(Alarm alarm);
    }

    public interface OnAlarmEnabledChangeListener {
        void onEnabledChanged(Alarm alarm);
    }
}