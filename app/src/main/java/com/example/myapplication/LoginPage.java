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
        EditText emailField = findViewById(R.id.editTextEmailAddress);
        passwordField = findViewById(R.id.editTextPassword); // Initialize editTextPassword here

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Button to switch to registration
        Button button1 = findViewById(R.id.registerButton);
        button1.setOnClickListener(v -> startActivity(new Intent(LoginPage.this, RegistrationPage.class)));

        // Button to confirm login
        Button button2 = findViewById(R.id.LoginBtn);
        button2.setOnClickListener(v -> {

            /*
            // Check if credentials are right before proceeding
            boolean verified = checkCredentials(email, passwordField);

            if (verified) {
                // Passes on role and name of the user to the welcome page
                String userRole = determineRole();
                Intent intent = new Intent(LoginPage.this, WelcomePage.class);
                intent.putExtra("userRole", userRole);
                startActivity(intent);
            } else {
                // Shows toast message if credentials are not verified
                Toast.makeText(LoginPage.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
            }

            */

            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, redirect to welcome page with the signed-in user's information ( role )
                                FirebaseUser user = mAuth.getCurrentUser();

                                String userRole = determineRole(user);
                                Intent intent = new Intent(LoginPage.this, WelcomePage.class);
                                intent.putExtra("userRole", userRole);
                                startActivity(intent);

                            } else {
                                //sign in failed - display message to user
                                Toast.makeText(LoginPage.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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

    // Determine whether the role is attendee/organizer
    private String determineRole(FirebaseUser user) {

        final String[] ans = {""};

        mDatabase.child("users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    try {
                        Attendee user = task.getResult().getValue(Attendee.class);
                        ans[0] =  "Attendee";

                    } catch ( Exception error){
                        Log.e("firebase", "attempt to get attendee", error);
                    }

                    try {
                        Organizer user = task.getResult().getValue(Organizer.class);
                        ans[0] =  "Organizer";

                    } catch ( Exception error){
                        Log.e("firebase", "attempt to get attendee", error);
                    }
                    

                    //TODO actually save the role in db instead of doing whatever is above

                }
            }
        });

        return ans[0];
    }

}
