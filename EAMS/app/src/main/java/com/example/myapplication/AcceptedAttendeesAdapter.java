// AcceptedAttendeesAdapter.java
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class AcceptedAttendeesAdapter extends ArrayAdapter<Attendee> {
    private Context context;
    private List<Attendee> acceptedAttendees;

    public AcceptedAttendeesAdapter(Context context, List<Attendee> acceptedAttendees) {
        super(context, 0, acceptedAttendees);
        this.context = context;
        this.acceptedAttendees = acceptedAttendees;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Attendee attendee = acceptedAttendees.get(position);
        TextView attendeeTextView = convertView.findViewById(android.R.id.text1);
        //nameTextView.setText(attendee.getEmail());
        attendeeTextView.setText(attendee.getLastName() + attendee.getFirstName());

        // Set up click listener to show dialog when the attendee's name is clicked
        attendeeTextView.setOnClickListener(v -> showAttendeeDialog(attendee));

        return convertView;
    }

    // Method to display a dialog with attendee information
    private void showAttendeeDialog(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Attendee Information")
                .setMessage("Name: " + attendee.getFirstName() + " " + attendee.getLastName() + "\n" +
                        "Phone: " + attendee.getPhoneNumber() + "\n" +
                        "Address: " + attendee.getAddress())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public List<Attendee> getAcceptedAttendees(){
        return acceptedAttendees;
    }
}
