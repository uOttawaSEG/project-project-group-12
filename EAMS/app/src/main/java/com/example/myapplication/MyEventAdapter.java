package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;



public class MyEventAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> mEventList;

    public MyEventAdapter(Context context, List<Event> eventList) {
        super(context, 0, eventList);
        this.mContext = context;
        this.mEventList = eventList;

        // Sort the list by newest to oldest
        sortEventsByNewest();
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
            long currentTimeMillis = System.currentTimeMillis();
            long eventStartTimeMillis = event.getStartTime().getTime();

            if (eventStartTimeMillis - currentTimeMillis > 24 * 60 * 60 * 1000) {
                new AlertDialog.Builder(mContext)
                        .setMessage("Are you sure you want to leave this event?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            mEventList.remove(position);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setMessage("You cannot leave this event as it starts in less than 24 hours.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        // Set up the clickable text view for the event details popup
        titleTextView.setOnClickListener(v -> {
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

    // sort the event list from newest to oldest
    private void sortEventsByNewest() {
        Collections.sort(mEventList, (event1, event2) -> {
            return Long.compare(event2.getStartTime().getTime(), event1.getStartTime().getTime());
        });
    }
}

