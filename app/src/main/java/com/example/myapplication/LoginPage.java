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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize database
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
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
        mDatabase.child("users").child("admins").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                if (task.getResult().exists()) {
                    navigateToWelcomePage("Admin");
                } else {
                    // Check if user is Organizer
                    mDatabase.child("users").child("organizers").child(user.getUid()).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful() && task2.getResult().exists()) {
                            navigateToWelcomePage("Organizer");
                        } else {
                            // Check if user is Attendee
                            mDatabase.child("users").child("attendees").child(user.getUid()).get().addOnCompleteListener(task3 -> {
                                if (task3.isSuccessful() && task3.getResult().exists()) {
                                    navigateToWelcomePage("Attendee");
                                } else {
                                    Log.e("firebase", "User role not found");
                                }
                            });
                        }
                    });
                }
            }
        });
    }


    private void navigateToWelcomePage(String userRole) {
        Intent intent = new Intent(LoginPage.this, WelcomePage.class);
        intent.putExtra("userRole", userRole);
        Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

}
