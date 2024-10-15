package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class RegistrationPage extends AppCompatActivity {

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField, addressField, phoneNumberField;
    private RadioGroup rolegroup;

    private Button confirmSignUpButton, backButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrationpage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameField = findViewById(R.id.firstName);
        lastNameField = findViewById(R.id.lastName);
        emailField = findViewById(R.id.emailAddress);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.ConfirmPassword);
        addressField = findViewById(R.id.Address);
        phoneNumberField = findViewById(R.id.PhoneNumber);
        rolegroup = findViewById(R.id.radioGroup);
        confirmSignUpButton = findViewById(R.id.ConfirmSignUp);
        backButton = findViewById(R.id.backBtn);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            }
        });

        /*
        Button button2 = findViewById(R.id.ConfirmSignUp);
        button1.setOnClickListener(new View.OnClickListener() {

        });

         */
    }



}
