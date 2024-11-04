package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EventRequestPage extends AppCompatActivity {
    private ListView acceptedAttendeesListView, pendingAttendeesListView;
    private ArrayAdapter<String> acceptedAdapter;
    private PendingAttendeesAdapter pendingAdapter;
    private List<String> acceptedAttendees = new ArrayList<>();
    private List<String> pendingAttendees = new ArrayList<>();

    // dialog with attendee information
    private void showAttendeeDialog(String attendeeName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attendee Information")
                .setMessage("Details for " + attendeeName)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        // Initialize the views
        acceptedAttendeesListView = findViewById(R.id.acceptedAttendeesListView);
        pendingAttendeesListView = findViewById(R.id.pendingAttendeesListView);
        Button backButton = findViewById(R.id.backButton);

        // Just testing some sample data
        for(int i = 0; i < 10; i++) {
            acceptedAttendees.add("Ren Amamiya");
            pendingAttendees.add("Goro Akechi");

        }
        // Set up accepted attendees adapter
        acceptedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        // Set up pending attendees adapter
        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        // Back button functionality to go to ...
        backButton.setOnClickListener(v -> {
            // Change to go to the required class later
            Intent intent = new Intent(EventRequestPage.this, EventRequestPage.class);
            startActivity(intent);
            finish();
        });

        // OnItemClickListener for accepted attendees
        acceptedAttendeesListView.setOnItemClickListener((parent, view, position, id) -> {
            String attendeeName = acceptedAttendees.get(position);
            showAttendeeDialog(attendeeName);
        });

        // OnItemClickListener for pending attendees
        pendingAttendeesListView.setOnItemClickListener((parent, view, position, id) -> {
            String attendeeName = pendingAttendees.get(position);
            showAttendeeDialog(attendeeName);
        });
    }

}

