package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AttendeePage  extends AppCompatActivity {
    private ArrayList<Event> allEvents;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_page);


        allEvents = new ArrayList<>();
        eventListView = findViewById(R.id.eventListOnAttendeePage);

        // Create and set the adapter
        EventAdapter adapter = new EventAdapter(this, allEvents);
        eventListView.setAdapter(adapter);
    }


}
