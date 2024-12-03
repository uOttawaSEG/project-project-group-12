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

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MyEventAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> mEventList;
    private DatabaseReference userEventListRef;

    public MyEventAdapter(Context context, List<Event> eventList) {
        super(context, 0, eventList);
        this.mContext = context;
        this.mEventList = eventList;

        // Initialize the user event list reference
        userEventListRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("eventList");

        // Set up the real-time listener for user event list
        userEventListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Create a list to hold the updated events
                List<Event> updatedEventList = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    String eventId = eventSnapshot.getKey();
                    DatabaseReference eventRef = FirebaseDatabase.getInstance()
                            .getReference("events")
                            .child(eventId);
                    eventRef.get().addOnCompleteListener(eventTask -> {
                        if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                            Event event = eventTask.getResult().getValue(Event.class);
                            if (event != null) {
                                updatedEventList.add(event);
                                // Update the adapter's list and refresh the view
                                mEventList.clear();
                                mEventList.addAll(updatedEventList);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Failed to load event list.", Toast.LENGTH_SHORT).show();
            }
        });
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
        TextView statusTextView = convertView.findViewById(R.id.event_status_my);
        titleTextView.setText(event.getTitle());

        DatabaseReference registrationRef = FirebaseDatabase.getInstance()
                .getReference("events")
                .child(event.getEventId())
                .child("registrations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); // 当前用户 ID

        // Fetch and display the registration status
        registrationRef.child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String status = task.getResult().getValue(String.class);
                statusTextView.setText("Status: " + (status != null ? status : "Unknown"));
            } else {
                statusTextView.setText("Status: Unknown");
            }
        });

        // Set the 'Leave' button and the popup dialog functionality
        Button leaveButton = convertView.findViewById(R.id.leave_button_my);
        leaveButton.setOnClickListener(v -> {
            long currentTimeMillis = System.currentTimeMillis();
            long eventStartTimeMillis = event.getStartTime().getTime();

            // check if starttime is more than hours from now.
            if (eventStartTimeMillis - currentTimeMillis > 24 * 60 * 60 * 1000) {
                registrationRef.child("status").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String status = task.getResult().getValue(String.class);

                        if ("Rejected".equalsIgnoreCase(status)) {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("You cannot leave this event as your status is 'Rejected'.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else if ("Pending".equalsIgnoreCase(status) || "Approved".equalsIgnoreCase(status)) {
                            // if status is pending or approved, remove them from pending/approved list.
                            new AlertDialog.Builder(mContext)
                                    .setMessage("Are you sure you want to leave this event?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                                                .getReference("events")
                                                .child(event.getEventId());

                                        if ("Pending".equalsIgnoreCase(status)) {
                                            eventRef.child("pendingList")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .removeValue();
                                        } else if ("Approved".equalsIgnoreCase(status)) {
                                            eventRef.child("approvedList")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .removeValue();
                                        }

                                        // delete the user from user event list
                                        registrationRef.removeValue().addOnCompleteListener(removeTask -> {
                                            if (removeTask.isSuccessful()) {
                                                DatabaseReference userEventListRef = FirebaseDatabase.getInstance()
                                                        .getReference("users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("eventList")
                                                        .child(event.getEventId());

                                                // delete it from the UI
                                                userEventListRef.removeValue().addOnCompleteListener(userEventListTask -> {
                                                    if (userEventListTask.isSuccessful()) {
                                                        // refresh
                                                        mEventList.remove(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(mContext, "You have successfully left the event.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(mContext, "Failed to remove the event from your list.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(mContext, "Failed to leave the event. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("Your status is unknown so you can't leave this event.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    } else {
                        new AlertDialog.Builder(mContext)
                                .setMessage("Failed to retrieve your status. Please try again later.")
                                .setPositiveButton("OK", null)
                                .show();

                    }
                });
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
}




