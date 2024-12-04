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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView statusTextView = convertView.findViewById(R.id.eventStatus);

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event updatedEvent = dataSnapshot.getValue(Event.class);
                if (updatedEvent != null) {
                    List<Attendee> pendingAttendeesList = updatedEvent.getPendingAttendeesList();
                    List<Attendee> acceptedAttendeesList = updatedEvent.getAcceptedAttendeesList();


                    if (pendingAttendeesList == null) pendingAttendeesList = new ArrayList<>();
                    if (acceptedAttendeesList == null) acceptedAttendeesList = new ArrayList<>();


                    String fullName = attendee.getFirstName() + " " + attendee.getLastName();
                    boolean isInPending = false;
                    boolean isInAccepted = false;


                    for (Attendee a : pendingAttendeesList) {
                        String listFullName = a.getFirstName() + " " + a.getLastName();
                        if (listFullName.equals(fullName)) {
                            isInPending = true;
                            break;
                        }
                    }
                    for (Attendee a : acceptedAttendeesList) {
                        String listFullName = a.getFirstName() + " " + a.getLastName();
                        if (listFullName.equals(fullName)) {
                            isInAccepted = true;
                            break;
                        }
                    }


                    if (isInAccepted) {
                        statusTextView.setText("Approved");
                    } else if (isInPending) {
                        statusTextView.setText("Pending");
                    } else {
                        statusTextView.setText("Rejected");
                    }
                } else {
                    statusTextView.setText("Event data not available");
                    Log.e("MyEventAdapter", "Event not found in database: " + event.getEventId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyEventAdapter", "Failed to fetch event data: " + databaseError.getMessage());
            }
        });


        // Set the 'Leave' button and the popup dialog functionality
        Button leaveButton = convertView.findViewById(R.id.leave_button_my);
        leaveButton.setOnClickListener(v -> {

            // Show a confirmation dialog when "Leave" button is clicked
            new AlertDialog.Builder(mContext)
                    .setMessage("Are you sure you want to leave this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Check if the event starts in less than 24 hours
                        long currentTimeMillis = System.currentTimeMillis();
                        long eventStartTimeMillis = event.getStartTime().getTime(); // Ensure startTime is Date type

                        if (eventStartTimeMillis - currentTimeMillis <= 24 * 60 * 60 * 1000) {
                            Toast.makeText(mContext, "Cannot leave: Event starts in less than 24 hours.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Handle leaving event logic here
                        removeAttendeeFromPendingList(mEventList.get(position));

                        mEventList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Set an OnClickListener for the title to show event details
        titleTextView.setOnClickListener(v -> {
            String formattedTime = formatEventTime(event.getStartTime(), event.getEndTime());
            new AlertDialog.Builder(mContext)
                    .setTitle(event.getTitle())
                    .setMessage("Description: " + event.getDescription() + "\n\n" +
                            "Address: " + event.getEventAddress() + "\n\n" +
                            "Time: " + formattedTime)
                    .setPositiveButton("OK", null)
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

    private String formatEventTime(Date startTime, Date endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(startTime) + " - " + dateFormat.format(endTime);
    }
}
