package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView mEventListView;
    private MyEventAdapter mEventAdapter;
    private List<Event> mEventList;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_my);

        uid = getIntent().getStringExtra("uid");

        // Back Button functionality
        Button backButton = findViewById(R.id.back_button_my);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, AttendeePage.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        // Initialize the event list (you should replace this with actual data)
        mEventList = new ArrayList<>();

        // Example data
        mEventList.add(new Event("Event 1", "Description of Event 1", "Address 1", new Date(), new Date(), "1", "autoAccept", "organizerUId"));
        mEventList.add(new Event("Event 2", "Description of Event 2", "Address 2", new Date(), new Date(), "2", "autoAccept", "organizerUId"));

        // Set up the adapter and ListView
        mEventAdapter = new MyEventAdapter(this, mEventList);
        mEventListView = findViewById(R.id.event_list_view_my);
        mEventListView.setAdapter(mEventAdapter);
    }
}
