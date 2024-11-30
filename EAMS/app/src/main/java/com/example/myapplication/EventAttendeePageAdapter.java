package com.example.myapplication; // EventAttendeePageAdapter.java

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Attendee;
import com.example.myapplication.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventAttendeePageAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;
    private Attendee attendee;
    private String uid;
    private List<Event> appliedEvents = new ArrayList<>();
    private List<Event> notAppliedEvents = new ArrayList<>();

    // 构造函数
    public EventAttendeePageAdapter(Context context, List<Event> events, Attendee attendee, String uid) {
        super(context, R.layout.activity_attendee_page, events);
        this.context = context;
        this.events = events;
        this.attendee = attendee;
        this.uid = uid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        ViewHolder holder;

        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            listItem = inflater.inflate(R.layout.event_attendee_page_item, parent, false);

            holder = new ViewHolder();
            holder.eventTitleTextView = listItem.findViewById(R.id.eventTitleTextView);
            holder.descriptionTextView = listItem.findViewById(R.id.eventDescriptionTextView);
            holder.addressEventTextView = listItem.findViewById(R.id.addressEventTextVIew);
            holder.startTimeTextView = listItem.findViewById(R.id.startTime);
            holder.JoinView = listItem.findViewById(R.id.JoinEvent);
            holder.LeaveEvent = listItem.findViewById(R.id.LeaveEvent);
            holder.statusTextView = listItem.findViewById(R.id.EventStatus);

            listItem.setTag(holder);
        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        // 获取当前事件
        Event event = events.get(position);

        holder.descriptionTextView.setText(event.getDescription());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.addressEventTextView.setText(event.getEventAddress());
        holder.startTimeTextView.setText(event.getStartTime().toString());
        holder.statusTextView.setText("Status: " + event.getStatus());

        // show join or leave
        if ("Not Applied".equals(event.getStatus())) {
            holder.LeaveEvent.setVisibility(View.GONE);
            holder.JoinView.setVisibility(View.VISIBLE);
        } else {
            holder.JoinView.setVisibility(View.GONE);
            holder.LeaveEvent.setVisibility(View.VISIBLE);
        }

        // join btn
        holder.JoinView.setOnClickListener(v -> {
            if ("Not Applied".equals(event.getStatus())) {
                event.setStatus("Pending");
                updateEventStatusInFirebase(event);

                addToListBasedOnAutoAccept(event);

                notAppliedEvents.remove(event);
                appliedEvents.add(event);

                // refresh
                notifyDataSetChanged();

                Toast.makeText(context, "You have applied for the event!", Toast.LENGTH_SHORT).show();
            }
        });

        // leave btn
        holder.LeaveEvent.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            long eventStartTime = event.getStartTime().getTime();

            // cant leave within 24h
            if (eventStartTime - currentTime <= 24 * 60 * 60 * 1000) {
                Toast.makeText(context, "Cannot leave the event within 24 hours of the start time.", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Pending".equals(event.getStatus())) {
                leaveEvent(event);
                appliedEvents.remove(event);
                notAppliedEvents.add(event);


                notifyDataSetChanged();

                Toast.makeText(context, "You have left the event.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Leaving the event failed.", Toast.LENGTH_SHORT).show();
            }
        });

        return listItem;
    }


    private void addToListBasedOnAutoAccept(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event eventFromDb = dataSnapshot.getValue(Event.class);
                if (eventFromDb != null) {
                    List<Attendee> pendingAttendeesList = eventFromDb.getPendingAttendeesList() != null ? eventFromDb.getPendingAttendeesList() : new ArrayList<>();
                    List<Attendee> acceptedAttendeesList = eventFromDb.getAcceptedAttendeesList() != null ? eventFromDb.getAcceptedAttendeesList() : new ArrayList<>();

                    if ("On".equals(event.getAutoAccept())) {
                        acceptedAttendeesList.add(attendee);
                        event.setStatus("Approved");
                        Toast.makeText(context, "You have been added to the accepted list!", Toast.LENGTH_SHORT).show();
                    } else {
                        pendingAttendeesList.add(attendee);
                        event.setStatus("Pending");
                        Toast.makeText(context, "Your registration is pending!", Toast.LENGTH_SHORT).show();
                    }

                    Event updatedEvent = new Event(
                            eventFromDb.getTitle(),
                            eventFromDb.getDescription(),
                            eventFromDb.getEventAddress(),
                            eventFromDb.getStartTime(),
                            eventFromDb.getEndTime(),
                            eventFromDb.getEventId(),
                            (ArrayList<Attendee>) pendingAttendeesList,
                            (ArrayList<Attendee>) acceptedAttendeesList,
                            eventFromDb.getAutoAccept(),
                            eventFromDb.getOrganizerUId()
                    );

                    eventRef.setValue(updatedEvent).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("EventAttendeePageAdapter", "Event updated successfully.");
                        } else {
                            Log.d("EventAttendeePageAdapter", "Failed to update event.");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch event data: " + databaseError.getMessage());
            }
        });
    }

    //
    private void leaveEvent(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event eventFromDb = dataSnapshot.getValue(Event.class);
                if (eventFromDb != null) {
                    if ("Pending".equals(event.getStatus())) {
                        List<Attendee> pendingAttendeesList = eventFromDb.getPendingAttendeesList();
                        if (pendingAttendeesList != null && pendingAttendeesList.contains(attendee)) {
                            pendingAttendeesList.remove(attendee);
                            eventRef.child("pendingAttendeesList").setValue(pendingAttendeesList);
                        }
                    } else if ("Approved".equals(event.getStatus())) {
                        List<Attendee> acceptedAttendeesList = eventFromDb.getAcceptedAttendeesList();
                        if (acceptedAttendeesList != null && acceptedAttendeesList.contains(attendee)) {
                            acceptedAttendeesList.remove(attendee);
                            eventRef.child("acceptedAttendeesList").setValue(acceptedAttendeesList);
                        }
                    }

                    event.setStatus("Not Applied");
                    updateEventStatusInFirebase(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch event data: " + databaseError.getMessage());
            }
        });
    }

    // update to Firebase
    private void updateEventStatusInFirebase(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
        eventRef.child("status").setValue(event.getStatus())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Event status updated to: " + event.getStatus());
                    } else {
                        Log.e("Firebase", "Failed to update event status", task.getException());
                    }
                });
    }

    private static class ViewHolder {
        TextView eventTitleTextView, descriptionTextView, addressEventTextView, startTimeTextView, statusTextView;
        Button JoinView, LeaveEvent;
    }
}


