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

            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = eventList.get(position);
                        Intent intent = new Intent(context, EventRequestPage.class);
                        intent.putExtra("event_title", event.getTitle());
                        intent.putExtra("event_description", event.getDescription() + " at " + event.getEventAddress());
                        intent.putExtra("event_date", event.getStartTime().toString() + " : " + event.getEndTime().toString());
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
