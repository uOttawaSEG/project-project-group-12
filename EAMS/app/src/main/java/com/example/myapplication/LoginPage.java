package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private EditText passwordField, emailField;
    private Switch EyeSw;
    // Firebase Authentication and database reference
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Extract the login credentials
        emailField = findViewById(R.id.editTextEmailAddress);
        passwordField = findViewById(R.id.editTextPassword); // Initialize editTextPassword here


        // Initialize Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Button to switch to registration
        Button button1 = findViewById(R.id.registerButton);
        button1.setOnClickListener(v -> startActivity(new Intent(LoginPage.this, RegistrationPage.class)));

        // Button to confirm login
        Button button2 = findViewById(R.id.LoginBtn);
        button2.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                return; //stop execution if email is empty
            }

            if (password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return; //stop execution if password is empty
            }

            loginUser(email, password);

        });


        // Password visibility Switch
        EyeSw = findViewById(R.id.EyeSw);

        EyeSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (passwordField != null) { //Ensure editTextPassword is not null
                if (passwordField.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    //If the password is visible, hide it
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    //If the password is hidden, show it
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_green_light));
                }
                passwordField.setSelection(passwordField.getText().length());
            }
        });
    }

    // I Separated into a separate class for cleanliness.
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                determineRole(user); // Pass user to determine role
                            }
                        } else {
                            // Sign in failed - display message to user
                            Toast.makeText(LoginPage.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Determine whether the role is attendee/organizer
    private void determineRole(FirebaseUser user) {
        mDatabase.child("users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {

                    String userRole = task.getResult().child("userType").getValue(String.class);

                    if(Objects.equals(userRole, "Administrator")) {
                        navigateToWelcomePage(userRole);
                    }
                    else{
                        String status = task.getResult().child("status").getValue(String.class);
                        checkRegistrationStatus(userRole, status);
                    }
                } else {
                    Log.e("firebase", "user role not found", task.getException());
                }
            }
            });
            }




    private void navigateToWelcomePage(String userRole) {
        Intent intent;

        if(Objects.equals(userRole, "Administrator")) {

            intent = new Intent(LoginPage.this, AdminPage.class);
        } else if (Objects.equals(userRole, "Organizer")) {
            intent = new Intent(LoginPage.this, OrganizerPage.class);
        } else {
            intent = new Intent(LoginPage.this, AttendeePage.class);
        }

        intent.putExtra("userType", userRole);
        Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }


    private void checkRegistrationStatus(String userRole,String status){
        if(Objects.equals(status, "pending")){
            Toast.makeText(LoginPage.this, "Your registration is still pending.", Toast.LENGTH_LONG).show();
        }
        else if(Objects.equals(status, "rejected")) {
            //used an AlertDialog to give the user enough time to see the phone number of the admin
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
            builder.setTitle("Your registration has been rejected by the Administrator");
            builder.setMessage("Please contact them through this phone number: +1 123-456-7890");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        }
        else{
            //the status is confirmed so send the user to WelcomePage
            navigateToWelcomePage(userRole);
        }
    }
}
