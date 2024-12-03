
package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

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
    private Event event;


    public PendingAttendeesAdapter(Context context, List<Attendee> attendees, List<Attendee> acceptedAttendees,
                                   ArrayAdapter<Attendee> acceptedAdapter) {
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

        // Display attendee's full name
        String fullName = attendee.getFirstName() + " " + attendee.getLastName();
        attendeeNameView.setText(fullName);

        attendeeNameView.setOnClickListener(v -> showAttendeeDialog(attendee));

        acceptButton.setOnClickListener(v -> {
            String attendeeId = attendee.getUid();  // Get attendee UID
            if (event == null || attendeeId == null || attendeeId.isEmpty()) {
                Log.e("PendingAttendeesAdapter", "Event or Attendee ID is null or empty");
                return;
            }

            // Move to accepted list
            acceptedAttendees.add(attendee);
            acceptedAdapter.notifyDataSetChanged();
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
            eventRef.child("registrations").child(attendeeId).child("status").setValue("Accepted")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("PendingAttendeesAdapter", "Attendee accepted.");
                        } else {
                            Log.e("PendingAttendeesAdapter", "Failed to update status", task.getException());
                        }
                    });
        });

        rejectButton.setOnClickListener(v -> {
            String attendeeId = attendee.getUid();  // Get attendee UID
            if (event == null || attendeeId == null || attendeeId.isEmpty()) {
                Log.e("PendingAttendeesAdapter", "Event or Attendee ID is null or empty");
                return;
            }

            // Remove from the list
            attendees.remove(attendee);
            remove(attendee);
            notifyDataSetChanged();

            // Update the attendee's status to "Rejected"
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
            eventRef.child("registrations").child(attendeeId).child("status").setValue("Rejected")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("PendingAttendeesAdapter", "Attendee rejected.");
                        } else {
                            Log.e("PendingAttendeesAdapter", "Failed to update status", task.getException());
                        }
                    });
        });

        return convertView;
    }

    private void showAttendeeDialog(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Attendee Information")
                .setMessage("Details for " + attendee.getFirstName() + " " + attendee.getLastName() + "\n" +
                        "Phone: " + attendee.getPhoneNumber() + "\n" +
                        "Address: " + attendee.getAddress())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public List<Attendee> getPendingAttendees() {
        return attendees;
    }
}




