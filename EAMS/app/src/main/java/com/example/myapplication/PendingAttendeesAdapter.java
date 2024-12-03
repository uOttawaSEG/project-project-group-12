// PendingAttendeesAdapter.java
package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.Collection;
import java.util.List;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PendingAttendeesAdapter extends ArrayAdapter<Attendee> {
    private Context context;
    private List<Attendee> attendees;
    private List<Attendee> acceptedAttendees;
    private ArrayAdapter<Attendee> acceptedAdapter;
    private String currentEventId;

    public PendingAttendeesAdapter(Context context, List<Attendee> attendees, List<Attendee> acceptedAttendees, ArrayAdapter<Attendee> acceptedAdapter, String currentEventId) {
        super(context, 0, attendees);
        this.context = context;
        this.attendees = attendees;
        this.acceptedAttendees = acceptedAttendees;
        this.acceptedAdapter = acceptedAdapter;
        this.currentEventId = currentEventId;
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

        // Display attendee's full name
        String fullName = attendee.getFirstName() + " " + attendee.getLastName();
        attendeeNameView.setText(fullName);

        acceptButton.setOnClickListener(v -> {
            // Move to accepted list
            acceptedAttendees.add(attendee);
            acceptedAdapter.notifyDataSetChanged();
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            // Update status in Firebase to "Accepted"
            updateEventRegistrationStatus(attendee, "Accepted");
        });

        rejectButton.setOnClickListener(v -> {
            // Move to rejected list or simply remove from pending
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            // Update status in Firebase to "Rejected"
            updateEventRegistrationStatus(attendee, "Rejected");
        });

        return convertView;
    }

    private void updateEventRegistrationStatus(Attendee attendee, String status) {
        // Get the current event ID passed to the adapter
        String eventId = currentEventId;
        String attendeeId = attendee.getUid();

        // Now update the status for this event in Firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registrationRef = database.child("events")
                .child(eventId)
                .child("registrations")
                .child(attendeeId);

        // Update status in Firebase
        registrationRef.child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Attendee status updated to " + status);
                    } else {
                        Log.e("Firebase", "Failed to update attendee status");
                    }
                });
    }

    public List<Attendee> getPendingAttendees() {
        return attendees;
    }
}

