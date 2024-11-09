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
    private Event event;

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


        Intent intent = getIntent();
        String date = intent.getStringExtra("event_date");
        //recreating the event instance
        String eventID = intent.getStringExtra("event");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Fetching the data was successful
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    //fetching the main attributes of event
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String address = dataSnapshot.child("eventAddress").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    Date startTime = dataSnapshot.child("startTime").getValue(Date.class);
                    Date endTime = dataSnapshot.child("endTime").getValue(Date.class);

                    //fetch the list from DB aswell
                    GenericTypeIndicator<ArrayList<Attendee>> typeIndicator = new GenericTypeIndicator<ArrayList<Attendee>>() {};
                    ArrayList<Attendee> pendingAttendeesList = (ArrayList<Attendee>) dataSnapshot.child("pendingAttendees").getValue(typeIndicator);
                    ArrayList<Attendee> acceptedAttendeesList = (ArrayList<Attendee>) dataSnapshot.child("acceptedAttendees").getValue(typeIndicator);

                    //recreating the event instance
                    event = new Event(title, description, address, startTime, endTime, eventID, pendingAttendeesList, acceptedAttendeesList);
                } else {
                    Log.e("Firebase", "Event data not found for ID: " + eventID);
                }
            } else {
                // Handle failure
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });

        headingTextView.setText(event.getTitle());
        descriptionTextView.setText(event.getDescription());
        dateTextView.setText(date);

        // Add sample data
        for (int i = 0; i < 2; i++) {
            acceptedAttendees.add(new Attendee("Ren", "Amamiya", "123-456-7890", "Address 1", "UserType1", "yuh"));
            pendingAttendees.add(new Attendee("Goro", "Akechi", "098-765-4321", "Address 2", "UserType2", "yuh"));
        }

        acceptedAdapter = new AcceptedAttendeesAdapter(this, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        backButton.setOnClickListener(v -> {
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
}
