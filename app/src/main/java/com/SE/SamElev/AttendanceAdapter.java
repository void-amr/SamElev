package com.SE.SamElev;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SE.SamElev.AttendanceDay;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<AttendanceDay> attendanceDays;

    public AttendanceAdapter(List<AttendanceDay> attendanceDays) {
        this.attendanceDays = attendanceDays;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_day, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceDay day = attendanceDays.get(position);

        // Set employee name
        holder.tvName.setText(day.getDate());

        // Set attendance color
        if (day.isPresent()) {
            holder.attendanceIndicator.setBackgroundColor(Color.GREEN); // Present
        } else {
            holder.attendanceIndicator.setBackgroundColor(Color.RED); // Absent
        }
    }

    @Override
    public int getItemCount() {
        return attendanceDays.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        View attendanceIndicator;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDate); // Reuse date TextView for name
            attendanceIndicator = itemView.findViewById(R.id.attendanceIndicator);
        }
    }
}
