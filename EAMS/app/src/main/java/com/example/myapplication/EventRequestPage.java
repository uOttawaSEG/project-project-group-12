// EventRequestPage.java
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
    private ArrayAdapter<Attendee> acceptedAdapter;
    private PendingAttendeesAdapter pendingAdapter;
    private List<Attendee> acceptedAttendees = new ArrayList<>();
    private List<Attendee> pendingAttendees = new ArrayList<>();

    private void showAttendeeDialog(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attendee Information")
                .setMessage("Details for " + attendee.getFirstName() + " " + attendee.getLastName())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        acceptedAttendeesListView = findViewById(R.id.acceptedAttendeesListView);
        pendingAttendeesListView = findViewById(R.id.pendingAttendeesListView);
        Button backButton = findViewById(R.id.backButton);

        // Add sample data
        for (int i = 0; i < 2; i++) {
            acceptedAttendees.add(new Attendee("Ren", "Amamiya", "123-456-7890", "Address 1", "UserType1", "yuh"));
            pendingAttendees.add(new Attendee("Goro", "Akechi", "098-765-4321", "Address 2", "UserType2", "yuh"));
        }

        acceptedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, acceptedAttendees);
        acceptedAttendeesListView.setAdapter(acceptedAdapter);

        pendingAdapter = new PendingAttendeesAdapter(this, pendingAttendees, acceptedAttendees, acceptedAdapter);
        pendingAttendeesListView.setAdapter(pendingAdapter);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventRequestPage.this, EventRequestPage.class);
            startActivity(intent);
            finish();
        });

        acceptedAttendeesListView.setOnItemClickListener((parent, view, position, id) -> {
            Attendee attendee = acceptedAttendees.get(position);
            showAttendeeDialog(attendee);
        });

        pendingAttendeesListView.setOnItemClickListener((parent, view, position, id) -> {
            Attendee attendee = pendingAttendees.get(position);
            showAttendeeDialog(attendee);
        });
    }
}
