package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttendeePage extends AppCompatActivity {
    private ListView appliedListView, notAppliedListView;
    private DatabaseReference eventsDatabaseReference;
    private DatabaseReference attendeesDatabaseReference;
    private EventAttendeePageAdapter appliedAdapter;
    private EventAttendeePageAdapter notAppliedAdapter;
    private Attendee attendee; // Variable to hold the Attendee object
    private Button logOutBtn;

    // Declare applied and not applied event lists
    private List<Event> appliedEvents = new ArrayList<>();
    private List<Event> notAppliedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_page);

        // Retrieve the uid from the Intent
        String uid = getIntent().getStringExtra("uid");

        appliedListView = findViewById(R.id.appliedEventList); // Correct id for applied events
        notAppliedListView = findViewById(R.id.NotAppliedEventList); // Correct id for not applied events

        // Initialize references to Firebase database nodes
        eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
        attendeesDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        // Add a listener for the attendee data
        attendeesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String userType = snapshot.child("userType").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    // Create a new Attendee object
                    attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status);

                    // Initialize the adapters after attendee data is fetched
                    appliedAdapter = new EventAttendeePageAdapter(AttendeePage.this, appliedEvents, attendee, uid);
                    notAppliedAdapter = new EventAttendeePageAdapter(AttendeePage.this, notAppliedEvents, attendee, uid);

                    // Set the adapters to the ListViews
                    appliedListView.setAdapter(appliedAdapter);
                    notAppliedListView.setAdapter(notAppliedAdapter);

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

        logOutBtn.setOnClickListener(v -> {
            // Navigate back to the LoginPage
            Intent intent = new Intent(AttendeePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchEvents() {
        eventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear both lists before adding new data
                appliedEvents.clear();
                notAppliedEvents.clear();

                // Iterate through the fetched events and classify them
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        // Classify events based on their status
                        if (event.getStatus() == null) {
                            event.setStatus("Not Applied");
                            eventsDatabaseReference.child(event.getEventId()).child("status").setValue("Not Applied");
                        }
                        if ("Not Applied".equals(event.getStatus())) {
                            notAppliedEvents.add(event);  // Add to not applied events
                        } else  {
                            appliedEvents.add(event);  // Add to applied events
                        }
                    }
                }

                //Sort by date from new to old
                notAppliedEvents.sort((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()));
                appliedEvents.sort((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()));


                // Notify the adapters to refresh the ListViews after fetching and classifying events
                appliedAdapter.notifyDataSetChanged();
                notAppliedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AttendeePage", "Failed to fetch events: " + databaseError.getMessage());
            }
        });
    }


}


