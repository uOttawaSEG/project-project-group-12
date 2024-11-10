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

    private RecyclerView eventListRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
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

        //  initialize eventList and RecyclerView
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, uid);

        eventListRecyclerView = findViewById(R.id.eventList);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventListRecyclerView.setAdapter(eventAdapter);

        eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
        eventsDatabaseReference.addValueEventListener(new ValueEventListener() {
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            eventList.clear();
            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                Event event = eventSnapshot.getValue(Event.class);
                if (event != null) {
                    eventList.add(event);
                }
            }
            eventAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Log error if data retrieval fails
            System.err.println("Failed to read events: " + databaseError.toException());
        }
    });
        // log out button
        Button backButton = findViewById(R.id.OPbackBtn);
        backButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OrganizerPage.this, LoginPage.class));
            finish();
        });

        //button to event creation
        FloatingActionButton addEventButton = findViewById(R.id.addEventBtn);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerPage.this, EventCreationPage.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });
    }
        // add events to list

}
