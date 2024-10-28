package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.List;

public class AdminPage extends AppCompatActivity {

    private RecyclerView pendingList, rejectedList;
    private PendingAdapter pendingAdapter;
    private RejectedAdapter rejectedAdapter;
    private  RegistrationPending registrationPending;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        initlayout();

        //try to populate page
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        registrationPending = new RegistrationPending();
        loadUsers();

        registrationToUI();


    }

    private void registrationToUI() {
        //RecyclerView for pending registrations
        pendingList = findViewById(R.id.pendingList);
        pendingList.setLayoutManager(new LinearLayoutManager(this));

        //Initialize adapter
        pendingAdapter = new PendingAdapter(registrationPending.getPendingRegistrations(), new RegistrationPending.OnItemActionListener() {
            @Override
            public void onApprove(User item) {
                //Handle the approval action
                registrationPending.approveRegistration(item);  // Just call approveRegistration without passing 'this'
                pendingAdapter.updateData(registrationPending.getPendingRegistrations()); // Refresh the list
            }

            @Override
            public void onReject(User item) {
                //Handle the rejection action
                registrationPending.rejectRegistration(item);  // Just call rejectRegistration without passing 'this'
                pendingAdapter.updateData(registrationPending.getPendingRegistrations()); // Refresh the list
            }
        });

        pendingList.setAdapter(pendingAdapter);


        // Initialize RecyclerView for rejected items
        rejectedList = findViewById(R.id.rejectedList);
        rejectedList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of rejected items and the listener
        rejectedAdapter = new RejectedAdapter(RegistrationRejected.getRejectedRegistrations(), new RegistrationRejected.OnItemActionListener() {
            @Override
            public void onApprove(User item) {
                // Logic to re-approve the registration
                RegistrationRejected.approveRegistration(item); // Approve the registration
                rejectedAdapter.updateData(RegistrationRejected.getRejectedRegistrations()); // Refresh the list
            }
        });

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
                    String role = childSnapshot.child("role").getValue(String.class);
                    //equalsIgnoreCase allows comparisons with any case, ensuring “attendee” or “Attendee” match the same way.
                    if ("Attendee".equalsIgnoreCase(role)) {
                        Attendee attendeeData = childSnapshot.getValue(Attendee.class);
                        if (attendeeData != null) {
                            AdminPage.this.registrationPending.addRegistration(attendeeData);
                            Log.d("Firebase", "User added: " + attendeeData.getFirstName());
                        }


                    } else if ("Organizer".equalsIgnoreCase(role)) {
                        Organizer organizerData = childSnapshot.getValue(Organizer.class);
                        if (organizerData != null) {
                            AdminPage.this.registrationPending.addRegistration(organizerData);
                            Log.d("Firebase", "User added: " + organizerData.getFirstName());
                        }
                    }
                }
                pendingAdapter.updateData(AdminPage.this.registrationPending.getPendingRegistrations()); // Refresh pending list
                Toast.makeText(AdminPage.this, "Users loaded sucesfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }



}
