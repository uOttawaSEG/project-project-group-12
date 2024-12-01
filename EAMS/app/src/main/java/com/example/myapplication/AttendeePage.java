package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class AttendeePage extends AppCompatActivity {
    private ArrayList<Event> allEvents;
    private ListView eventListView;
    private DatabaseReference eventsDatabaseReference;
    private DatabaseReference attendeesDatabaseReference;
    private EventAttendeePageAdapter adapter;
    private Attendee attendee; // Variable to hold the Attendee object
    private Button logOutBtn, myEvents;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_page);


        //initialize the searchview for searching events
        SearchView searchView = findViewById(R.id.searchView);

        // Retrieve the uid from the Intent
        uid = getIntent().getStringExtra("uid");

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
                    // Use GenericTypeIndicator to extract a list
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                    List<String> eventIds = snapshot.child("eventIds").getValue(t);
                    if (eventIds == null) {
                        eventIds = new ArrayList<>(); // Initialize as an empty list if it doesn't exist
                    }
                    // Create a new Attendee object
                    attendee = new Attendee(firstName, lastName, phoneNumber, address, userType, status, (ArrayList<String>) eventIds);

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

        myEvents = findViewById(R.id.toMyEvents);

        myEvents.setOnClickListener(v -> {
            // Navigate back to the LoginPage
            Intent intent = new Intent(AttendeePage.this, EventListActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
            finish();
        });

        // Set up a listener for the button click
        logOutBtn.setOnClickListener(v -> {
            // Navigate back to the LoginPage
            Intent intent = new Intent(AttendeePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });


        //setting up the listener for keywords typed into the search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //filter the events based on query
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //update the filter dynamically as user types
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    private void fetchEvents() {
        eventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    adapter = new EventAttendeePageAdapter(AttendeePage.this, allEvents, attendee, uid);
                    eventListView.setAdapter(adapter);
                } else {
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
