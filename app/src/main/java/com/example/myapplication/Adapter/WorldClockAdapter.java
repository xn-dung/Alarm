package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.WorldClock;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class WorldClockAdapter extends RecyclerView.Adapter<WorldClockAdapter.ClockViewHolder> {
    private final List<WorldClock> clocks = new ArrayList<>();

    @NonNull
    @Override
    public ClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_world_clock, parent, false);
        return new ClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClockViewHolder holder, int position) {
        WorldClock clock = clocks.get(position);
        holder.city.setText(clock.getCity());
        holder.time.setText(clock.getTime());
        holder.zone.setText(clock.getZone());
    }

    @Override
    public int getItemCount() {
        return clocks.size();
    }

    public void setClocks(List<WorldClock> items) {
        clocks.clear();
        clocks.addAll(items);
        notifyDataSetChanged();
    }

    static class ClockViewHolder extends RecyclerView.ViewHolder {
        private final TextView city;
        private final TextView time;
        private final TextView zone;

        ClockViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.tv_city);
            time = itemView.findViewById(R.id.tv_world_time);
            zone = itemView.findViewById(R.id.tv_zone);
        }
    }
}
