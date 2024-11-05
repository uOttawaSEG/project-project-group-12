package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<String> eventList;

    public EventAdapter(List<String> eventList) {
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        View eventSampleLayout = view.findViewById(R.id.eventSample);
        return new EventViewHolder(eventSampleLayout);

    }



    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Bind data to each item
        String event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String event) {
            // Bind data to views in event_item
        }
    }
}
