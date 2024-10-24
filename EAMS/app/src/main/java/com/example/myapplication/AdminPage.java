package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdminPage extends AppCompatActivity {

    private RecyclerView pendingList, rejectedList;
    private PendingAdapter pendingAdapter;
    private RejectedAdapter rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

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

        //Add two sample registrations to the pending list
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Persona 5 royal - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("Ren Amamiya - Registration Request");
        RegistrationPending.addRegistration("scrolling works? - Registration Request");
        RegistrationPending.addRegistration("TEST - owo");

        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationRejected.addRejectedRegistration("end of line uwu - Registration Request");

        //RecyclerView for pending registrations
        pendingList = findViewById(R.id.pendingList);
        pendingList.setLayoutManager(new LinearLayoutManager(this));

        //Initialize adapter
        pendingAdapter = new PendingAdapter(RegistrationPending.getPendingRegistrations(), new RegistrationPending.OnItemActionListener() {
            @Override
            public void onApprove(String item) {
                //Handle the approval action
                RegistrationPending.approveRegistration(item);  // Just call approveRegistration without passing 'this'
                pendingAdapter.updateData(RegistrationPending.getPendingRegistrations()); // Refresh the list
            }

            @Override
            public void onReject(String item) {
                //Handle the rejection action
                RegistrationPending.rejectRegistration(item);  // Just call rejectRegistration without passing 'this'
                pendingAdapter.updateData(RegistrationPending.getPendingRegistrations()); // Refresh the list
            }
        });

        pendingList.setAdapter(pendingAdapter);


        // Initialize RecyclerView for rejected items
        rejectedList = findViewById(R.id.rejectedList);
        rejectedList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of rejected items and the listener
        rejectedAdapter = new RejectedAdapter(RegistrationRejected.getRejectedRegistrations(), new RegistrationRejected.OnItemActionListener() {
            @Override
            public void onApprove(String item) {
                // Logic to re-approve the registration
                RegistrationRejected.approveRegistration(item); // Approve the registration
                rejectedAdapter.updateData(RegistrationRejected.getRejectedRegistrations()); // Refresh the list
            }
        });

// Set the adapter to the RecyclerView for rejected items
        rejectedList.setAdapter(rejectedAdapter);



    }
}
