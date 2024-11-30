// PendingAttendeesAdapter.java
package com.example.myapplication;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.example.myapplication.Attendee;
import com.example.myapplication.Event;

import java.util.List;

public class PendingAttendeesAdapter extends ArrayAdapter<Attendee> {
    private Context context;
    private List<Attendee> attendees;
    private List<Attendee> acceptedAttendees;
    private ArrayAdapter<Attendee> acceptedAdapter;

    public PendingAttendeesAdapter(Context context, List<Attendee> attendees, List<Attendee> acceptedAttendees, ArrayAdapter<Attendee> acceptedAdapter) {
        super(context, 0, attendees);
        this.context = context;
        this.attendees = attendees;
        this.acceptedAttendees = acceptedAttendees;
        this.acceptedAdapter = acceptedAdapter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pending_attendee_item, parent, false);
        }

        Attendee attendee = attendees.get(position);
        TextView attendeeNameView = convertView.findViewById(R.id.attendeeName);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);
        Button rejectButton = convertView.findViewById(R.id.rejectButton);

        String fullName = attendee.getFirstName() + " " + attendee.getLastName();
        attendeeNameView.setText(fullName);

        // Handle Accept button
        acceptButton.setOnClickListener(v -> {
            // Move to accepted list
            acceptedAttendees.add(attendee);
            acceptedAdapter.notifyDataSetChanged();
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            // Update Firebase status
            updateAttendeeStatusInFirebase(attendee, "Approved");
        });

        // Handle Reject button
        rejectButton.setOnClickListener(v -> {
            // Mark as rejected and remove
            attendee.setStatus("Rejected");
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            // Update Firebase status
            updateAttendeeStatusInFirebase(attendee, "Rejected");
        });

        return convertView;
    }

    // Update the attendee's status in Firebase
    private void updateAttendeeStatusInFirebase(Attendee attendee, String status) {
        // Assuming you have an event ID (from context, Intent, or another source)
        String eventId = "your_event_id";  // You need to set this appropriately
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);

        // Update the status of the attendee in the Firebase database
        eventRef.child("pendingAttendeesList").orderByChild("uid").equalTo(attendee.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the attendee is in the pending list, then remove from it
                if (dataSnapshot.exists()) {
                    eventRef.child("pendingAttendeesList").child(attendee.getUid()).removeValue();
                }

                // Now update the status in the accepted list
                if ("Approved".equals(status)) {
                    eventRef.child("acceptedAttendeesList").child(attendee.getUid()).setValue(attendee);
                } else {
                    eventRef.child("rejectedAttendeesList").child(attendee.getUid()).setValue(attendee);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to update attendee status", databaseError.toException());
            }
        });
    }

    public List<Attendee> getPendingAttendees() {
        return attendees;

    }}

