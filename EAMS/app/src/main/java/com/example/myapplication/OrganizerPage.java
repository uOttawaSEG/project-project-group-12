package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrganizerPage extends ComponentActivity {

    private RecyclerView eventListRecyclerView, pastEventListRecyclerView;
    private EventAdapter eventAdapter, pastEventAdapter;
    private List<Event> eventList, pastEventList;
    private DatabaseReference eventsDatabaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_page);

        // Retrieve UID from the Intent
        uid = getIntent().getStringExtra("uid");
        Toast.makeText(OrganizerPage.this, "UID: " + uid, Toast.LENGTH_LONG).show();

        // Initialize event lists and RecyclerViews
        eventList = new ArrayList<>();
        pastEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, uid);
        pastEventAdapter = new EventAdapter(this, pastEventList, uid);

        eventListRecyclerView = findViewById(R.id.eventList);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventListRecyclerView.setAdapter(eventAdapter);

        pastEventListRecyclerView = findViewById(R.id.pastEventList);
        pastEventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pastEventListRecyclerView.setAdapter(pastEventAdapter);

        eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
        eventsDatabaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                pastEventList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        if (event.getEndTime().before(new Date())) {
                            // Add to past events if end time is before current time
                            pastEventList.add(event);
                        } else {
                            // Add to upcoming events
                            eventList.add(event);
                        }
                    }
                }
                eventAdapter.notifyDataSetChanged();
                pastEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log error if data retrieval fails
                System.err.println("Failed to read events: " + databaseError.toException());
            }
        });

        // Log out button
        Button backButton = findViewById(R.id.OPbackBtn);
        backButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OrganizerPage.this, LoginPage.class));
            finish();
        });

        // Button to event creation
        FloatingActionButton addEventButton = findViewById(R.id.addEventBtn);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerPage.this, EventCreationPage.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });
    }
}
