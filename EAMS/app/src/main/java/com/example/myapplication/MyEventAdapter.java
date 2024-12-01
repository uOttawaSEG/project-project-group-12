package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MyEventAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> mEventList;

    public MyEventAdapter(Context context, List<Event> eventList) {
        super(context, 0, eventList);
        this.mContext = context;
        this.mEventList = eventList;
    }

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
}
