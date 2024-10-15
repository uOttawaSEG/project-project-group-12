package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    private EditText editTextPassword;
    private Switch EyeSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Extract the login credentials
        EditText email = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword); // Initialize editTextPassword here

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to switch to registration
        Button button1 = findViewById(R.id.registerButton);
        button1.setOnClickListener(v -> startActivity(new Intent(LoginPage.this, RegistrationPage.class)));

        // Button to confirm login
        Button button2 = findViewById(R.id.LoginBtn);
        button2.setOnClickListener(v -> {

            // Check if credentials are right before proceeding
            boolean verified = checkCredentials(email, editTextPassword);

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
        });

        // Password visibility Switch
        EyeSw = findViewById(R.id.EyeSw);

        EyeSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (editTextPassword != null) { //Ensure editTextPassword is not null
                if (editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    //If the password is visible, hide it
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    //If the password is hidden, show it
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_green_light));
                }
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });
    }

    // Determine whether the role is attendee/organizer
    private String determineRole() {
        // Logic to extract role
        return "Attendee";
    }

    // Checks if credentials are right
    private boolean checkCredentials(EditText email, EditText password) {
        // Logic to verify the credentials
        return true;
    }
}
