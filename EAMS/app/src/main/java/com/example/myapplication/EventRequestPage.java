// EventRequestPage.java uses AcceptedAttendeesAdapter and PendingAttendeesAdapter
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        acceptedAttendeesListView = findViewById(R.id.acceptedAttendeesListView);
        pendingAttendeesListView = findViewById(R.id.pendingAttendeesListView);
        Button backButton = findViewById(R.id.backButton);


        headingTextView = findViewById(R.id.eventsRequestTitle);
        descriptionTextView = findViewById(R.id.eventsRequestDescription);
        dateTextView = findViewById(R.id.eventsRequestDate);

        //fetching event data from EventAdapter
        Intent intent = getIntent();
        String date = intent.getStringExtra("event_date");
        String eventID = intent.getStringExtra("eventID");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventID);
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {

                    //fetch the lists from DB
                    GenericTypeIndicator<ArrayList<Attendee>> typeIndicator = new GenericTypeIndicator<ArrayList<Attendee>>() {};
                    pendingAttendees.addAll(dataSnapshot.child("pendingAttendees").getValue(typeIndicator));
                    acceptedAttendees.addAll(dataSnapshot.child("acceptedAttendees").getValue(typeIndicator));

                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                // Handle failure
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });

        headingTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);



        acceptedAdapter = new AcceptedAttendeesAdapter(this, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);


        backButton.setOnClickListener(v -> {
            //update changes to Db when finishing operations
            updateAttendeesInFirebase(databaseReference);

            Intent backIntent = new Intent(EventRequestPage.this, OrganizerPage.class);
            startActivity(backIntent);
            finish();
        });

        //Approve all button
        Button approveAll = findViewById(R.id.approveAll);
        approveAll.setOnClickListener(v -> {
            acceptedAttendees.addAll(pendingAttendees);
            pendingAdapter.clear();
            acceptedAdapter.notifyDataSetChanged();
            pendingAdapter.notifyDataSetChanged();

        });
    }
    public void updateAttendeesInFirebase(DatabaseReference databaseReference){
        //save updated pendingAttendees list
        databaseReference.child("pendingAttendees").setValue(pendingAdapter.getPendingAttendees())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Pending attendees updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update pending attendees.", task.getException());
                    }
                });

        //save updated acceptedAttendees list
        databaseReference.child("acceptedAttendees").setValue(acceptedAdapter.getAcceptedAttendees())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Accepted attendees updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update accepted attendees.", task.getException());
                    }
                });
    }
    }

