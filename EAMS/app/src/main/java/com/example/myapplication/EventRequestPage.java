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

        acceptedAdapter = new AcceptedAttendeesAdapter(this, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        //fetching event data from EventAdapter
        Intent intent = getIntent();
        String date = intent.getStringExtra("event_date");
        String eventID = intent.getStringExtra("eventID");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventID);

        // Fetch the list of attendees first
        databaseReference.child("pendingAttendeesList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Declaring variables
                    Attendee attendee;
                    String firstName, lastName, phoneNumber, address, userType, status;

                    // Clear the list to avoid adding duplicates
                    pendingAttendees.clear();

                    // Iterate over each attendee in the list
                    for (DataSnapshot attendeeSnapshot : dataSnapshot.getChildren()) {
                        // Get the attendee's information from Firebase
                        firstName = attendeeSnapshot.child("firstName").getValue(String.class);
                        lastName = attendeeSnapshot.child("lastName").getValue(String.class);
                        phoneNumber = attendeeSnapshot.child("phoneNumber").getValue(String.class);
                        address = attendeeSnapshot.child("address").getValue(String.class);
                        userType = attendeeSnapshot.child("userType").getValue(String.class);
                        status = attendeeSnapshot.child("status").getValue(String.class);

                        // Create an Attendee object
                        List<String> eventIds = new ArrayList<>();
                        attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status, (ArrayList<String>) eventIds);

                        // Add the attendee to the list
                        pendingAttendees.add(attendee);
                        pendingAdapter.notifyDataSetChanged();

                    }
                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                // Handle failure
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });

        // Now fetching data for accepted attendees list
        databaseReference.child("acceptedAttendeesList").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task1.getResult();
                if (dataSnapshot.exists()) {
                    // Declaring variables
                    Attendee attendee;
                    String firstName, lastName, phoneNumber, address, userType, status;

                    // Clear the list to avoid adding duplicates
                    acceptedAttendees.clear();

                    // Iterate over each attendee in the list
                    for (DataSnapshot attendeeSnapshot : dataSnapshot.getChildren()) {
                        // Get the attendee's information from Firebase
                        firstName = attendeeSnapshot.child("firstName").getValue(String.class);
                        lastName = attendeeSnapshot.child("lastName").getValue(String.class);
                        phoneNumber = attendeeSnapshot.child("phoneNumber").getValue(String.class);
                        address = attendeeSnapshot.child("address").getValue(String.class);
                        userType = attendeeSnapshot.child("userType").getValue(String.class);
                        status = attendeeSnapshot.child("status").getValue(String.class);

                        // Create an Attendee object
                        List<String> eventIds = new ArrayList<>();
                        attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status, (ArrayList<String>) eventIds);

                        // Add the attendee to the list
                        acceptedAttendees.add(attendee);
                        acceptedAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                // Handle failure
                Log.e("Firebase", "Error getting data", task1.getException());
            }
        });

        headingTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);

        backButton.setOnClickListener(v -> {
            //update changes to Db when finishing operations
            updateAttendeesInFirebase(databaseReference);
            Intent intent2 = new Intent(EventRequestPage.this, OrganizerPage.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
            finish();
        });

        //Approve all button
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

    public void updateAttendeesInFirebase(DatabaseReference databaseReference){
        //save updated pendingAttendees list
        databaseReference.child("pendingAttendeesList").setValue(pendingAdapter.getPendingAttendees())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Pending attendees updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update pending attendees.", task.getException());
                    }
                });

        //save updated acceptedAttendees list
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