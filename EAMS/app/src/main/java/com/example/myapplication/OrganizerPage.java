package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class OrganizerPage extends ComponentActivity {

    private RecyclerView eventListRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_page);

        //  initialize eventList and RecyclerView
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);

        eventListRecyclerView = findViewById(R.id.eventList);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventListRecyclerView.setAdapter(eventAdapter);

        // log out button
        Button backButton = findViewById(R.id.OPbackBtn);
        backButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OrganizerPage.this, LoginPage.class));
            finish();
        });

        // Michael you can change the button to addingEvent button from here
        FloatingActionButton addEventButton = findViewById(R.id.addEventBtn);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerPage.this, EventCreationPage.class);
            startActivity(intent);
        });
    }
        // add events to list
    private void addEventToEventList() {

        //Intent intent = new Intent(OrganizerPage.this, EventCreationPage.class);
        //startActivity(intent);


        eventList.add(new Event("New event", "an event", "160 place", new Date(2024, 4, 15,12, 30),  new Date(2024, 4, 15,13, 30)));
        eventAdapter.notifyItemInserted(eventList.size() - 1);
        eventListRecyclerView.scrollToPosition(eventList.size() - 1);
    }

}
