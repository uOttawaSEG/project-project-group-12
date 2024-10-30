package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPage extends AppCompatActivity {

    private RecyclerView pendingList, rejectedList;
    private PendingAdapter pendingAdapter;
    private RejectedAdapter rejectedAdapter;
    private RegistrationsPending registrationsPending;
    private DatabaseReference databaseReference;
    private  RegistrationRejected registrationRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        initlayout();

        //try to populate page
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        registrationRejected = new RegistrationRejected();
        registrationRejected.initListener();

        registrationsPending = new RegistrationsPending();
        registrationsPending.initListener();
        registrationsPending.initRegistrationRejected(registrationRejected);

        loadUsers();
        registrationToUI();

        Button refreshButton = findViewById(R.id.refreshBtn);
        refreshButton.setOnClickListener(v -> refreshData()); // Call refreshData when clicked
    }

    private void refreshData() {
        // Create an intent to restart the AdminPage activity
        Intent intent = new Intent(this, AdminPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish the current activity to remove it from the back stack
        finish();
    }

    private void registrationToUI() {

        // Initialize RecyclerView for rejected items
        rejectedList = findViewById(R.id.rejectedList);
        rejectedList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of rejected items and the listener
        rejectedAdapter = new RejectedAdapter(registrationRejected);

        // Set the adapter to the RecyclerView for rejected items
        rejectedList.setAdapter(rejectedAdapter);

        //RecyclerView for pending registrations
        pendingList = findViewById(R.id.pendingList);
        pendingList.setLayoutManager(new LinearLayoutManager(this));

        //Initialize adapter
        pendingAdapter = new PendingAdapter(registrationsPending, registrationRejected, rejectedAdapter);

        pendingList.setAdapter(pendingAdapter);
        pendingList.setAdapter(pendingAdapter);

    }

    private void initlayout() {
        //Set insets for proper layout handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Logout button
        Button logOutButton = findViewById(R.id.logOutBtn);
        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminPage.this, LoginPage.class));
        });
    }

    private void loadUsers( ){
        this.databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    //Retrieve role and ensure non-null, ignoring case in the comparison
                    String status = childSnapshot.child("status").getValue(String.class);

                    //send to pending or rejected list
                    if ("pending".equalsIgnoreCase(status)) {
                        addUserToRegistrationsPendingList(childSnapshot);
                    } else if ("rejected".equalsIgnoreCase(status)) {
                        addUserToRegistrationsRejectedPendingList(childSnapshot);
                    }


                }
                rejectedAdapter.updateData(AdminPage.this.registrationRejected.getRejectedRegistrations()); // Refresh rejected list
                pendingAdapter.updateData(AdminPage.this.registrationsPending.getPendingRegistrations()); // Refresh pending list
                rejectedAdapter.updateData(AdminPage.this.registrationRejected.getRejectedRegistrations());
                Toast.makeText(AdminPage.this, "Users loaded successfully", Toast.LENGTH_LONG).show();
            }

            private void addUserToRegistrationsRejectedPendingList(DataSnapshot childSnapshot) {
                String role = childSnapshot.child("userType").getValue(String.class);
                String uid = childSnapshot.getKey();

                // Extract user data and add to the rejected list
                User rejectedUser;
                if ("Attendee".equalsIgnoreCase(role)) {
                    Attendee attendeeData = childSnapshot.getValue(Attendee.class);
                    assert attendeeData != null; // Ensure attendee data is not null
                    attendeeData.setUid(uid);
                    rejectedUser = attendeeData; // Use the attendee object as a User
                } else if ("Organizer".equalsIgnoreCase(role)) {
                    Organizer organizerData = childSnapshot.getValue(Organizer.class);
                    assert organizerData != null; // Ensure organizer data is not null
                    organizerData.setUid(uid);
                    rejectedUser = organizerData; // Use the organizer object as a User
                } else {
                    return; // If the role doesn't match, exit the method
                }

                // Add the rejected user to the RegistrationRejected list
                AdminPage.this.registrationRejected.addRejectedRegistration(rejectedUser);
                Log.d("Firebase", "Rejected user added: " + rejectedUser.getFirstName());
            }

            private void addUserToRegistrationsPendingList(DataSnapshot childSnapshot) {
                String role = childSnapshot.child("userType").getValue(String.class);
                String uid = childSnapshot.getKey();

                //equalsIgnoreCase allows comparisons with any case, ensuring “attendee” or “Attendee” match the same way.
                if ("Attendee".equalsIgnoreCase(role)) {
                    Attendee attendeeData = childSnapshot.getValue(Attendee.class);
                    assert attendeeData != null; //throws error when null, should never
                    attendeeData.setUid(uid);

                    AdminPage.this.registrationsPending.addRegistration(attendeeData);
                    Log.d("Firebase", "User added: " + attendeeData.getFirstName());


                } else if ("Organizer".equalsIgnoreCase(role)) {
                    Organizer organizerData = childSnapshot.getValue(Organizer.class);
                    assert organizerData != null; //throws error when null, should never
                    organizerData.setUid(uid);

                    AdminPage.this.registrationsPending.addRegistration(organizerData);
                    Log.d("Firebase", "User added: " + organizerData.getFirstName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }



}
