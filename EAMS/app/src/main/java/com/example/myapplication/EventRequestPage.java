// EventRequestPage.java uses AcceptedAttendeesAdapter and PendingAttendeesAdapter
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EventRequestPage extends AppCompatActivity {
    private ListView acceptedAttendeesListView, pendingAttendeesListView;
    private AcceptedAttendeesAdapter acceptedAdapter;
    private PendingAttendeesAdapter pendingAdapter;
    private List<Attendee> acceptedAttendees = new ArrayList<>();
    private List<Attendee> pendingAttendees = new ArrayList<>();
    private TextView headingTextView, descriptionTextView, dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        acceptedAttendeesListView = findViewById(R.id.acceptedAttendeesListView);
        pendingAttendeesListView = findViewById(R.id.pendingAttendeesListView);
        Button backButton = findViewById(R.id.backButton);


        headingTextView = findViewById(R.id.eventsRequestTitle);
        descriptionTextView = findViewById(R.id.eventsRequestDescription);
        dateTextView = findViewById(R.id.eventsRequestDate);


        Intent intent = getIntent();
        String title = intent.getStringExtra("event_title");
        String description = intent.getStringExtra("event_description");
        String date = intent.getStringExtra("event_date");


        headingTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);

        // Add sample data
        for (int i = 0; i < 2; i++) {
            acceptedAttendees.add(new Attendee("Ren", "Amamiya", "123-456-7890", "Address 1", "UserType1", "yuh"));
            pendingAttendees.add(new Attendee("Goro", "Akechi", "098-765-4321", "Address 2", "UserType2", "yuh"));
        }

        acceptedAdapter = new AcceptedAttendeesAdapter(this, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(EventRequestPage.this, OrganizerPage.class);
            startActivity(backIntent);
            finish();
        });

        //Approve all button
        Button approveAll = findViewById(R.id.approveAll);
        approveAll.setOnClickListener(v -> {
            acceptedAttendees.addAll(pendingAttendees);
            pendingAdapter.clear();
            acceptedAdapter.notifyDataSetChanged();
            pendingAdapter.notifyDataSetChanged();


        });
    }
}
