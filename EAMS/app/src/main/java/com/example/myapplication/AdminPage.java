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


    }

    private void registrationToUI() {
        //RecyclerView for pending registrations
        pendingList = findViewById(R.id.pendingList);
        pendingList.setLayoutManager(new LinearLayoutManager(this));

        //Initialize adapter
        pendingAdapter = new PendingAdapter(registrationsPending,rejectedAdapter);

        pendingList.setAdapter(pendingAdapter);


        // Initialize RecyclerView for rejected items
        rejectedList = findViewById(R.id.rejectedList);
        rejectedList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of rejected items and the listener
        rejectedAdapter = new RejectedAdapter(registrationRejected);

        // Set the adapter to the RecyclerView for rejected items
        rejectedList.setAdapter(rejectedAdapter);

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
                pendingAdapter.updateData(AdminPage.this.registrationsPending.getPendingRegistrations()); // Refresh pending list
                Toast.makeText(AdminPage.this, "Users loaded successfully", Toast.LENGTH_LONG).show();
            }

            private void addUserToRegistrationsRejectedPendingList(DataSnapshot childSnapshot) {

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
