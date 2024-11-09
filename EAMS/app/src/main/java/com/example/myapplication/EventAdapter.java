package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private DatabaseReference eventsDatabaseReference;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Listen and update event list
        eventsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear(); //It refreshes the list
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log error
                Log.e("FirebaseData", "failed in reading events", databaseError.toException());
            }
        });
    }

        @NonNull
        @Override//create view holder
        public EventViewHolder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
            return new EventViewHolder(view);
        }

        @Override // send data to view holder
        public void onBindViewHolder (@NonNull EventViewHolder holder,int position){
            Event event = eventList.get(position);
            holder.bind(event);
        }

        @Override
        public int getItemCount () {
            return eventList.size();
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {
            private TextView eventName;
            private TextView eventStartTime;
            private Button editBtn, infoBtn, updateBtn, deleteBtn;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.eventName);
                eventStartTime = itemView.findViewById(R.id.startTime);
                editBtn = itemView.findViewById(R.id.editBtn);
                infoBtn = itemView.findViewById(R.id.infoBtn);
                updateBtn = itemView.findViewById(R.id.updateBtn);
                deleteBtn = itemView.findViewById(R.id.deleteBtn);



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

                //Edit button
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fadeInView(deleteBtn);
                        fadeInView(updateBtn);
                        int position = getAdapterPosition();

                        deleteBtn.setOnClickListener(v1 -> {
                            eventList.remove(position);
                            notifyDataSetChanged();
                        });

                        updateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, EventCreationPage.class);
                                eventList.remove(position);
                                notifyDataSetChanged();
                                context.startActivity(intent);
                            }
                        });
                    }
                });
            }


            //bind event data to event_item
            public void bind(Event event) {
                eventName.setText(event.getTitle());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm", Locale.ENGLISH);
                String fullStartDateTime = dateFormat.format(event.getStartTime());
                eventStartTime.setText(fullStartDateTime);
            }
            private void fadeInView(Button randomField){
                randomField.setVisibility(View.VISIBLE);
                randomField.setAlpha(0f);
                randomField.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null);
            }
        }
    }
