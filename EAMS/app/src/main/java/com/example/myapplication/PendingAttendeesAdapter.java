// PendingAttendeesAdapter.java
package com.example.myapplication;

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

        // Display attendee's full name
        //attendeeNameView.setText(attendee.getEmail());
        attendeeNameView.setText("kiminonawa@gmail.com");

        attendeeNameView.setOnClickListener(v -> showAttendeeDialog(attendee));

        acceptButton.setOnClickListener(v -> {
            // Move to accepted list
            acceptedAttendees.add(attendee);
            acceptedAdapter.notifyDataSetChanged();
            remove(attendee);
            notifyDataSetChanged();
        });

        rejectButton.setOnClickListener(v -> {
            remove(attendee);
            notifyDataSetChanged();
        });

        return convertView;
    }

    // Method to display a dialog with attendee information
    private void showAttendeeDialog(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Attendee Information")
                .setMessage("Details for " + attendee.getFirstName() + " " + attendee.getLastName() + "\n" +
                        "Phone: " + attendee.getPhoneNumber() + "\n" +
                        "Address: " + attendee.getAddress())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}