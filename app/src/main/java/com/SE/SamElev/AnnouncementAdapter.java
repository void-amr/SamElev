package com.SE.SamElev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> announcements;

    public AnnouncementAdapter() {
        this.context = null;
        this.announcements = new ArrayList<>();
    }

    public AnnouncementAdapter(Context context, ArrayList<String> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    // Setter methods for initialization if default constructor is used
    public void setContext(Context context) {
        this.context = context;
    }

    public void setAnnouncements(ArrayList<String> announcements) {
        this.announcements = announcements;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_announcement_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String announcement = announcements.get(position);
        holder.announcementText.setText(announcement);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView announcementText;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            announcementText = itemView.findViewById(R.id.announcementText);
        }
    }
}
