package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyEventAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> mEventList;
    private String uid;
    private Attendee attendee;

    public MyEventAdapter(Context context, List<Event> eventList, String uid, Attendee attendee) {
        super(context, 0, eventList);
        this.mContext = context;
        this.mEventList = eventList;
        this.uid = uid;
        this.attendee = attendee;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.event_item_my, parent, false);
        }

        // Get the current event
        Event event = mEventList.get(position);

        // Set up the text views
        TextView titleTextView = convertView.findViewById(R.id.event_title_my);
        titleTextView.setText(event.getTitle());

        // Set the 'Leave' button and the popup dialog functionality
        Button leaveButton = convertView.findViewById(R.id.leave_button_my);
        leaveButton.setOnClickListener(v -> {
            // Show a confirmation dialog when "Leave" button is clicked
            new AlertDialog.Builder(mContext)
                    .setMessage("Are you sure you want to leave this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Handle leaving event logic here
                        // For example, remove the event from the list or update the status
                        removeAttendeeFromPendingList(mEventList.get(position));

                        mEventList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Set up the clickable text view for the event details popup
        titleTextView.setOnClickListener(v -> {
            // Show event details in a popup dialog
            String details = "Description: " + event.getDescription() +
                    "\nAddress: " + event.getEventAddress() +
                    "\nStart Time: " + event.getStartTime().toString() +
                    "\nEnd Time: " + event.getEndTime().toString();

            new AlertDialog.Builder(mContext)
                    .setTitle(event.getTitle())
                    .setMessage(details)
                    .setPositiveButton("Close", null)
                    .show();
        });


        return convertView;
    }

    private void removeAttendeeFromPendingList(Event event) {
        if (event == null || event.getEventId() == null) {
            Log.e("EventError", "Event or Event ID is null");
            return; // Exit the method to avoid further issues
        }

        if (uid == null) {
            Log.e("UIDError", "UID is null");
            return; // Exit the method to avoid further issues
        }
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
        DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        // Fetch the current event data to get the existing lists of attendees
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the event from the database
                Event eventFromDb = dataSnapshot.getValue(Event.class);
                if (eventFromDb != null) {
                    // Get the existing lists of attendees or create new ones if they are null
                    List<Attendee> pendingAttendeesList = eventFromDb.getPendingAttendeesList() != null ?
                            new ArrayList<>(eventFromDb.getPendingAttendeesList()) : new ArrayList<>();
                    List<Attendee> acceptedAttendeesList = eventFromDb.getAcceptedAttendeesList() != null ?
                            new ArrayList<>(eventFromDb.getAcceptedAttendeesList()) : new ArrayList<>();

                    pendingAttendeesList.remove(attendee);

                    // Reconstruct the event object with the updated lists
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
                            Log.d("EventAttendeePageAdapter", "Event updated successfully. Event ID: " + event.getEventId());
                        } else {
                            Log.d("EventAttendeePageAdapter", "Failed to update event. Event ID: " + event.getEventId());
                        }
                    });

                    // Now update the Attendee object in the "users" node
                    attendeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Retrieve the Attendee from the database
                            Attendee currentAttendee = dataSnapshot.getValue(Attendee.class);
                            if (currentAttendee != null) {
                                // Remove the eventId from the attendee's list of eventIds
                                currentAttendee.removeEventId(event.getEventId());

                                // Update the Attendee object in the database
                                attendeeRef.setValue(currentAttendee).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("EventAttendeePageAdapter", "Attendee updated successfully after removing event ID.");
                                    } else {
                                        Log.d("EventAttendeePageAdapter", "Failed to update Attendee after removing event ID.");
                                    }
                                });
                            } else {
                                Log.d("EventAttendeePageAdapter", "Attendee not found. UID: " + uid);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("EventAttendeePageAdapter", "Failed to fetch Attendee data: " + databaseError.getMessage());
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
}
