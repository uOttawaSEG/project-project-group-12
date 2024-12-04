package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView mEventListView;
    private MyEventAdapter mEventAdapter;
    private List<Event> mEventList;
    private String uid;
    private DatabaseReference attendeesDatabaseReference;
    private Attendee attendee;

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

                    //does the call to fetch event snow or else the attendee object is null
                    fetchEventsForAttendee(eventIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

    }
    private void fetchEventsForAttendee(List<String> eventIds) {
        mEventList = new ArrayList<>();
        final int[] eventsFetched = {0};

        DatabaseReference eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");

        for (String eventId : eventIds) {
            eventsDatabaseReference.child(eventId).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);

                    if (event != null) {
                        mEventList.add(event);

                        //
                        Date currentTIme = new Date();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(currentTIme);
                        calendar.add(Calendar.HOUR_OF_DAY, 24);

                        Date updatedDate = calendar.getTime();

                        if (event.getStartTime().compareTo(updatedDate) <= 0){
                            String msg = event.getTitle() + " starts in les sthan 24 hours";
                            Toast.makeText(EventListActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }

                eventsFetched[0]++;

                //initializes the adapter inside the loop (but only once in the end) because of asynchronous firebase calls
                if (eventsFetched[0] == eventIds.size()) {
                    Log.d("Firebase", "users uid " + uid);
                    Log.d("Firebase", "attendee name " + attendee.getFirstName());
                    mEventAdapter = new MyEventAdapter(EventListActivity.this, mEventList, uid, attendee);
                    mEventListView = findViewById(R.id.event_list_view_my);
                    mEventListView.setAdapter(mEventAdapter);
                    mEventAdapter.notifyDataSetChanged(); // Ensure the adapter updates with the fetched data
                }
            }).addOnFailureListener(e -> {
                // Handle error during fetch
                Log.e("Firebase", "Error fetching event data", e);
            });
        }
    }
}