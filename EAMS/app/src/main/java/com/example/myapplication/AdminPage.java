package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

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
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Persona 5 royal - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("Ren Amamiya - Registration Request");
        RegistrationsPendingList.addRegistration("scrolling works? - Registration Request");
        RegistrationsPendingList.addRegistration("TEST - owo");

        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("Ren Amamiya - Registration Request");
        RegistrationsRejectedList.addRejectedRegistration("end of line uwu - Registration Request");

        //RecyclerView for pending registrations
        pendingList = findViewById(R.id.pendingList);
        pendingList.setLayoutManager(new LinearLayoutManager(this));

        //Initialize adapter
        pendingAdapter = new PendingAdapter(RegistrationsPendingList.getPendingRegistrations(), new RegistrationsPendingList.OnItemActionListener() {
            @Override
            public void onApprove(String item) {
                //Handle the approval action
                RegistrationsPendingList.approveRegistration(item);  // Just call approveRegistration without passing 'this'
                pendingAdapter.updateData(RegistrationsPendingList.getPendingRegistrations()); // Refresh the list
            }

            @Override
            public void onReject(String item) {
                //Handle the rejection action
                RegistrationsPendingList.rejectRegistration(item);  // Just call rejectRegistration without passing 'this'
                pendingAdapter.updateData(RegistrationsPendingList.getPendingRegistrations()); // Refresh the list
            }
        });

        pendingList.setAdapter(pendingAdapter);


        // Initialize RecyclerView for rejected items
        rejectedList = findViewById(R.id.rejectedList);
        rejectedList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of rejected items and the listener
        rejectedAdapter = new RejectedAdapter(RegistrationsRejectedList.getRejectedRegistrations(), new RegistrationsRejectedList.OnItemActionListener() {
            @Override
            public void onApprove(String item) {
                // Logic to re-approve the registration
                RegistrationsRejectedList.approveRegistration(item); // Approve the registration
                rejectedAdapter.updateData(RegistrationsRejectedList.getRejectedRegistrations()); // Refresh the list
            }
        });

// Set the adapter to the RecyclerView for rejected items
        rejectedList.setAdapter(rejectedAdapter);



    }
}
