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

    public EventAttendeePageAdapter(Context context, List<Event> events, Attendee attendee, String uid) {
        super(context, R.layout.activity_attendee_page, events);
        this.context = context;
        this.events = events;
        this.attendee = attendee;
        this.uid = uid; // Store the Attendee object
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

            listItem.setTag(holder);

        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        Event event = events.get(position);
        holder.descriptionTextView.setText(event.getDescription());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.addressEventTextView.setText(event.getEventAddress());
        holder.startTimeTextView.setText(event.getStartTime().toString());

        // Set the Join button's click listener
        holder.JoinView.setOnClickListener(v -> {
            addToListBasedOnAutoAccept(event);
            events.remove(position);
            notifyDataSetChanged();
        });

        return listItem;
    }

    private void addToListBasedOnAutoAccept(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());

        // Fetch the current event data to get the existing lists of attendees
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the event from the database
                Event eventFromDb = dataSnapshot.getValue(Event.class);
                if (eventFromDb != null) {
                    // Create temporary lists if the event was not retrieved
                    List<Attendee> pendingAttendeesList = eventFromDb.getPendingAttendeesList() != null ? eventFromDb.getPendingAttendeesList() : new ArrayList<>();
                    List<Attendee> acceptedAttendeesList = eventFromDb.getAcceptedAttendeesList() != null ? eventFromDb.getAcceptedAttendeesList() : new ArrayList<>();

                    // Check the auto-accept status and add the attendee accordingly
                    if ("On".equals(event.getAutoAccept())) {
                        // Log the action and add attendee to the accepted list in Firebase
                        Log.d("EventAttendeePageAdapter", "Auto accept is ON. Adding attendee to accepted list. Event ID: " + event.getEventId());
                        acceptedAttendeesList.add(attendee);  // Add the second attendee to the accepted list
                        Toast.makeText(context, "You have been added to the accepted list!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add attendee to the pending list in Firebase
                        Log.d("EventAttendeePageAdapter", "Auto accept is OFF. Adding attendee to pending list. Event ID: " + event.getEventId());
                        pendingAttendeesList.add(attendee);  // Add the first attendee to the pending list
                        Toast.makeText(context, "Your registration is pending!", Toast.LENGTH_SHORT).show();
                    }

                    // Reconstruct the event object with updated lists
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

                    // Update the event in the database
                    eventRef.setValue(updatedEvent).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Event updated successfully
                            Log.d("EventAttendeePageAdapter", "Event updated successfully. Event ID: " + event.getEventId());
                        } else {
                            // Handle the failure of updating the event
                            Log.d("EventAttendeePageAdapter", "Failed to update event. Event ID: " + event.getEventId());
                        }
                    });
                } else {
                    Log.d("EventAttendeePageAdapter", "Event not found. Event ID: " + event.getEventId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("EventAttendeePageAdapter", "Failed to fetch event data: " + databaseError.getMessage());
            }
        });
    }


    private static class ViewHolder {
        TextView eventTitleTextView, descriptionTextView, addressEventTextView, startTimeTextView;
        Button JoinView;
    }
}
