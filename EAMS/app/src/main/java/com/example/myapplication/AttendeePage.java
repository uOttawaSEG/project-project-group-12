package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendeePage extends AppCompatActivity {
    private ArrayList<Event> allEvents;
    private ListView eventListView;
    private DatabaseReference eventsDatabaseReference;
    private DatabaseReference attendeesDatabaseReference;
    private EventAttendeePageAdapter adapter;
    private Attendee attendee; // Variable to hold the Attendee object
    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_page);

        // Retrieve the uid from the Intent
        String uid = getIntent().getStringExtra("uid");

        allEvents = new ArrayList<>();
        eventListView = findViewById(R.id.eventListOnAttendeePage);

        // Initialize references to Firebase database nodes
        eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
        attendeesDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        // Add a listener for the attendee data
        attendeesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming the data structure in Firebase is such that each attendee is stored under a unique ID.
                    // For example: /users/<UID>/Attendee
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String userType = snapshot.child("userType").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    // Create a new Attendee object
                    attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status);

                    // Initialize the adapter after attendee data is fetched
                    adapter = new EventAttendeePageAdapter(AttendeePage.this, allEvents, attendee, uid);
                    eventListView.setAdapter(adapter);

                    // Fetch and display events after the adapter is set
                    fetchEvents();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

        // Initialize the Log Out button
        logOutBtn = findViewById(R.id.logOutBtn2);

        // Set up a listener for the button click
        logOutBtn.setOnClickListener(v -> {
            // Navigate back to the LoginPage
            Intent intent = new Intent(AttendeePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchEvents() {
        eventsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        allEvents.add(event);
                    }
                }
                // Refresh the ListView after fetching the events
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
