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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

// PendingAttendeesAdapter.java
public class PendingAttendeesAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> attendees;
    // So we can move accepted users to accepted list
    private List<String> acceptedAttendees;
    private ArrayAdapter<String> acceptedAdapter;

    public PendingAttendeesAdapter(Context context, List<String> attendees, List<String> acceptedAttendees, ArrayAdapter<String> acceptedAdapter) {
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

        String attendeeName = attendees.get(position);
        TextView attendeeNameView = convertView.findViewById(R.id.attendeeName);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);
        Button rejectButton = convertView.findViewById(R.id.rejectButton);

        attendeeNameView.setText(attendeeName);

        // Set up the dialog for when the attendee name is clicked
        attendeeNameView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Attendee Information")
                    .setMessage("Details for " + attendeeName)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Set up accept and reject button actions
        acceptButton.setOnClickListener(v -> {

            // Move to accepted list
            acceptedAttendees.add(attendeeName);
            acceptedAdapter.notifyDataSetChanged();

            // Remove the attendee from the list
            remove(attendeeName);

            // Notify the adapter to refresh the list
            notifyDataSetChanged();

        });

        rejectButton.setOnClickListener(v -> {
            remove(attendeeName);

            notifyDataSetChanged();
        });

        return convertView;
    }
}


