package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    //Had to make it an instance variable for the fadein/fadeout of update/delete buttons
    boolean buttonsVisible = false;
    private String uid;


    public EventAdapter(Context context, List<Event> eventList, String uid) {
        this.context = context;
        this.eventList = eventList;
        this.uid = uid;
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


        public String getUidEvent() {
        return uid;
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
                            intent.putExtra("eventID", event.getEventId());
                            intent.putExtra("title", event.getTitle());
                            intent.putExtra("description", event.getDescription() + " Location: " + event.getEventAddress());
                            intent.putExtra("uid", getUidEvent());

                            // Define the date format for the month and day of the week
                            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH); // "MMMM" gives full month name
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);   // "EEEE" gives full day name
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH); // "HH:mm" for hours and minutes

                            // Get the formatted strings for start time
                            String monthStart = monthFormat.format(event.getStartTime());
                            String dayOfWeekStart = dayFormat.format(event.getStartTime());
                            String startTime = timeFormat.format(event.getStartTime());

                            // Get the formatted strings for end time
                            String monthEnd = monthFormat.format(event.getEndTime());
                            String dayOfWeekEnd = dayFormat.format(event.getEndTime());
                            String endTime = timeFormat.format(event.getEndTime());

                            // Check if the start and end date are the same
                            if (dayOfWeekStart.equals(dayOfWeekEnd) && monthStart.equals(monthEnd) && event.getStartTime().getDate() == event.getEndTime().getDate()) {
                                // If they are the same day, just show start time - end time
                                intent.putExtra("event_date", dayOfWeekStart + ", " + monthStart + " " + event.getStartTime().getDate() + " " + startTime + " - " + endTime);
                            } else {
                                // If they are different days, show both start and end
                                intent.putExtra("event_date", dayOfWeekStart + ", " + monthStart + " " + event.getStartTime().getDate() + " " +
                                        startTime + " - " + dayOfWeekEnd + ", " + monthEnd + " " + event.getEndTime().getDate() + " " + endTime);
                            }
                            context.startActivity(intent);
                        }
                    }
                });



                //Edit button
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(buttonsVisible){
                            fadeOutView(deleteBtn);
                            fadeOutView(updateBtn);
                        }
                        else {
                            fadeInView(deleteBtn);
                            fadeInView(updateBtn);
                            int position = getAdapterPosition();

                            deleteBtn.setOnClickListener(v1 -> {
                                // Get the event ID
                                String eventId = eventList.get(position).getEventId();
                                DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("events").child(eventId);

                                // Fetch the organizerId stored for the event
                                eventReference.child("organizerUId").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Retrieve the organizerId from the event
                                        String organizerId = task.getResult().getValue(String.class);

                                        // Compare the organizerId with the current user's UID (getOrganizerId)
                                        if (organizerId != null && organizerId.equals(getUidEvent())) {
                                            // If the user is the organizer, remove the event
                                            eventReference.removeValue();

                                        } else {
                                            // If the user is not the organizer, show a builder with an error message
                                            new AlertDialog.Builder(v1.getContext())
                                                    .setTitle("Error")
                                                    .setMessage("You can't delete events you didn't create.")
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        }
                                    }
                                });
                            });

                            updateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, EventCreationPage.class);
                                    intent.putExtra("event_title", eventList.get(position).getTitle());
                                    intent.putExtra("event_description", eventList.get(position).getDescription());
                                    intent.putExtra("event_location", eventList.get(position).getEventAddress());
                                    intent.putExtra("startTime", eventList.get(position).getStartTime());
                                    intent.putExtra("endTime", eventList.get(position).getEndTime());
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventList.get(position).getEventId());
                                    databaseReference.removeValue();
                                    context.startActivity(intent);
                                }
                            });
                        }
                        //switches to the other state
                        buttonsVisible = !buttonsVisible;
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

            private void fadeOutView(Button randomField){
                randomField.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                randomField.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }
