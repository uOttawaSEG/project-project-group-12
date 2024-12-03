// EventRequestPage.java uses AcceptedAttendeesAdapter and PendingAttendeesAdapter
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventRequestPage extends AppCompatActivity {
    private ListView acceptedAttendeesListView, pendingAttendeesListView;
    private AcceptedAttendeesAdapter acceptedAdapter;
    private PendingAttendeesAdapter pendingAdapter;
    private List<Attendee> acceptedAttendees = new ArrayList<>();
    private List<Attendee> pendingAttendees = new ArrayList<>();
    private TextView headingTextView, descriptionTextView, dateTextView;
    private String uid;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        // Retrieve UID from the Intent
        uid = getIntent().getStringExtra("uid");

        acceptedAttendeesListView = findViewById(R.id.acceptedAttendeesListView);
        pendingAttendeesListView = findViewById(R.id.pendingAttendeesListView);
        Button backButton = findViewById(R.id.backButton);

        headingTextView = findViewById(R.id.eventsRequestTitle);
        descriptionTextView = findViewById(R.id.eventsRequestDescription);
        dateTextView = findViewById(R.id.eventsRequestDate);

        // Initialize adapters
        acceptedAdapter = new AcceptedAttendeesAdapter(this, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        // Retrieve event details from Intent
        Intent intent = getIntent();
        String date = intent.getStringExtra("event_date");
        String eventID = intent.getStringExtra("eventID");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        // Fetch attendees data from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventID);

        // Fetch pending attendees
        databaseReference.child("pendingAttendeesList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Declaring variables for attendee data
                    String firstName, lastName, phoneNumber, address, userType, status;

                    // Clear the pending attendees list to avoid duplicates
                    pendingAttendees.clear();

                    // Iterate through each pending attendee
                    for (DataSnapshot attendeeSnapshot : dataSnapshot.getChildren()) {
                        firstName = attendeeSnapshot.child("firstName").getValue(String.class);
                        lastName = attendeeSnapshot.child("lastName").getValue(String.class);
                        phoneNumber = attendeeSnapshot.child("phoneNumber").getValue(String.class);
                        address = attendeeSnapshot.child("address").getValue(String.class);
                        userType = attendeeSnapshot.child("userType").getValue(String.class);
                        status = attendeeSnapshot.child("status").getValue(String.class);

                        // Create a new Attendee object
                        Attendee attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status, new ArrayList<>());

                        // Add attendee to the pending list
                        pendingAttendees.add(attendee);
                        pendingAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });

        // Fetch accepted attendees
        databaseReference.child("acceptedAttendeesList").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task1.getResult();
                if (dataSnapshot.exists()) {
                    String firstName, lastName, phoneNumber, address, userType, status;

                    // Clear the accepted attendees list to avoid duplicates
                    acceptedAttendees.clear();

                    // Iterate through each accepted attendee
                    for (DataSnapshot attendeeSnapshot : dataSnapshot.getChildren()) {
                        firstName = attendeeSnapshot.child("firstName").getValue(String.class);
                        lastName = attendeeSnapshot.child("lastName").getValue(String.class);
                        phoneNumber = attendeeSnapshot.child("phoneNumber").getValue(String.class);
                        address = attendeeSnapshot.child("address").getValue(String.class);
                        userType = attendeeSnapshot.child("userType").getValue(String.class);
                        status = attendeeSnapshot.child("status").getValue(String.class);

                        // Create a new Attendee object
                        Attendee attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status, new ArrayList<>());

                        // Add attendee to the accepted list
                        acceptedAttendees.add(attendee);
                        acceptedAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                Log.e("Firebase", "Error getting data", task1.getException());
            }
        });

        // Set event details on the page
        headingTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);

        // Initialize pending adapter with event ID
        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        backButton.setOnClickListener(v -> {
            // Update Firebase when navigating back
            updateAttendeesInFirebase(databaseReference);
            Intent intent2 = new Intent(EventRequestPage.this, OrganizerPage.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
            finish();
        });

        // Approve all pending attendees
        Button approveAll = findViewById(R.id.approveAll);
        approveAll.setOnClickListener(v -> {
            acceptedAttendees.addAll(pendingAttendees);
            acceptedAdapter.getAcceptedAttendees().addAll(pendingAttendees);

            pendingAdapter.clear();
            pendingAdapter.getPendingAttendees().clear();

            acceptedAdapter.notifyDataSetChanged();
            pendingAdapter.notifyDataSetChanged();
        });
    }

    // Save updated attendees to Firebase
    public void updateAttendeesInFirebase(DatabaseReference databaseReference){
        // Save updated pending attendees list
        databaseReference.child("pendingAttendeesList").setValue(pendingAdapter.getPendingAttendees())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Pending attendees updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update pending attendees.", task.getException());
                    }
                });

        // Save updated accepted attendees list
        databaseReference.child("acceptedAttendeesList").setValue(acceptedAdapter.getAcceptedAttendees())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Accepted attendees updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update accepted attendees.", task.getException());
                    }
                });
    }
}

