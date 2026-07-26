package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.RecentTimer;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentTimerAdapter extends RecyclerView.Adapter<RecentTimerAdapter.TimerViewHolder> {
    private final List<RecentTimer> timers = new ArrayList<>();
    private final OnTimerClick listener;

    public RecentTimerAdapter(OnTimerClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_timer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        RecentTimer timer = timers.get(position);
        long seconds = timer.getSeconds();
        holder.time.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60));
        holder.name.setText(timer.getName() == null || timer.getName().trim().isEmpty() ? "Timer" : timer.getName());
        holder.itemView.setOnClickListener(v -> listener.onTimerClick(timer));
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void setTimers(List<RecentTimer> items) {
        timers.clear();
        timers.addAll(items);
        notifyDataSetChanged();
    }

    static class TimerViewHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView name;

        TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_recent_time);
            name = itemView.findViewById(R.id.tv_recent_name);
        }
    }

    public interface OnTimerClick {
        void onTimerClick(RecentTimer timer);
    }
}
