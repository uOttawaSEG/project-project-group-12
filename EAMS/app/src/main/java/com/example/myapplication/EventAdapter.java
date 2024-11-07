package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView eventName;
        private TextView organizerName;
        private Button editBtn;
        private Button infoBtn;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            organizerName = itemView.findViewById(R.id.organizerName);
            editBtn = itemView.findViewById(R.id.editBtn);
            infoBtn = itemView.findViewById(R.id.infoBtn);


            // To send info to Request Page
            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = eventList.get(position);
                        Intent intent = new Intent(context, EventRequestPage.class);
                        intent.putExtra("event_title", event.getTitle());
                        intent.putExtra("event_description", event.getDescription() + " at " + event.getEventAddress());

                        // Define the date format for the month and day of the week
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH); // "MMMM" gives full month name
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);   // "EEEE" gives full day name
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH); // "HH:mm" for hours and minutes

                        // Get the formatted strings
                        String month = monthFormat.format(event.getStartTime());
                        String dayOfWeek = dayFormat.format(event.getStartTime());
                        String startTime = timeFormat.format(event.getStartTime());
                        String endTime = timeFormat.format(event.getEndTime());

                        // Put the formatted strings in
                        intent.putExtra("event_date", dayOfWeek + ", " + month + " " + event.getStartTime().getDate() + " " +
                                startTime + " - " + endTime);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Event event) {
            eventName.setText(event.getTitle());
            organizerName.setText(event.getDescription());
        }
    }
}
