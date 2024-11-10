package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        // Fetch and store attendee information
        fetchAttendeeInfo();

        // Create and set the adapter, passing the Attendee object
        adapter = new EventAttendeePageAdapter(this, allEvents, attendee);
        eventListView.setAdapter(adapter);

        // Fetch and display events
        fetchEvents();

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

    private void fetchAttendeeInfo() {
        attendeesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    attendee = snapshot.getValue(Attendee.class);
                    if (attendee != null) {
                        // Display Toast with the attendee information
                        String attendeeInfo = "Name: " + attendee.getFirstName() + " " + attendee.getLastName() +
                                "\nEmail: " + attendee.getEmail() +
                                "\nPhone: " + attendee.getPhoneNumber() +
                                "\nAddress: " + attendee.getAddress();

                        Toast.makeText(AttendeePage.this, attendeeInfo, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
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
                adapter.notifyDataSetChanged(); // Refresh the ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}