package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class AttendeePage extends AppCompatActivity {
    private ArrayList<Event> allEvents;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_page);

        allEvents = new ArrayList<>();

        // Create a test event and add it to the list
        Event testEvent = new Event(
                "Sample Event",
                "This is a sample event description.",
                "123 Event St.",
                new Date(),
                new Date(System.currentTimeMillis() + 3600000),
                "event1"
        );
        allEvents.add(testEvent);

        eventListView = findViewById(R.id.eventListOnAttendeePage);

        // Create and set the adapter
        EventAttendeePageAdapter adapter = new EventAttendeePageAdapter(this, allEvents);
        eventListView.setAdapter(adapter);
    }
}
