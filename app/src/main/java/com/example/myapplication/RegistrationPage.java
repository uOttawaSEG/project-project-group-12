package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class RegistrationPage extends AppCompatActivity {

    private EditText firstName, lastName, emailAddress, password, confirmPassword, phoneNumber, address;
    private RadioGroup radioGroup;
    private Button SignUpButton;
    private Boolean checkAllFields;
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

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        phoneNumber = findViewById(R.id.PhoneNumber);
        address = findViewById(R.id.Address);
        radioGroup = findViewById(R.id.radioGroup);
        SignUpButton = findViewById(R.id.ConfirmSignUp);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            }
        });


        SignUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkAllFields()) {
                    Toast.makeText(RegistrationPage.this, "Registration Successful", Toast.LENGTH_LONG).show();
                }
            }

            private boolean checkAllFields() {
                boolean allFieldsValid = true;


                //first name must contain 2 letters
                if (firstName.getText().toString().trim().length() < 2) {
                    firstName.setError("First name must be at least 2 characters");
                    allFieldsValid = false;
                }

                // lastname must contain 2 letters
                if (lastName.getText().toString().trim().length() < 2) {
                    lastName.setError("Last name must be at least 2 characters");
                    allFieldsValid = false;
                }


                // email must contain @
                String emailInput = emailAddress.getText().toString();
                if (!emailInput.contains("@")) {
                    emailAddress.setError("Valid email is required");
                    allFieldsValid = false;
                }

                // password must contain 1 letter or 1 number
                String passwordInput = password.getText().toString();
                if (passwordInput.length() < 8 || !passwordInput.matches(".*[a-zA-Z0-9].*")) {
                    password.setError("Password must be at least 8 characters long and contain a letter or number");
                    allFieldsValid = false;
                }


                // double check password
                String confirmPasswordInput = confirmPassword.getText().toString();

                if (!confirmPasswordInput.equals(passwordInput)) {
                    confirmPassword.setError("Two passwords are not the same");
                    allFieldsValid = false;
                }

                // phone number must be pure numbers
                String phoneInput = phoneNumber.getText().toString();
                if (phoneInput.isEmpty() || !phoneInput.matches(".*[0-9].*")) {
                    phoneNumber.setError("Please enter numbers");
                    allFieldsValid = false;
                }

                // check address input is empty or not
                if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Address is required");
                    allFieldsValid = false;
                }

                //check if user selected a identity
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegistrationPage.this, "Please select registering as Attendee or Organizer", Toast.LENGTH_SHORT).show();
                    allFieldsValid = false;
                }


                return allFieldsValid;
            }        });
    }
}


        /*
        Button button2 = findViewById(R.id.ConfirmSignUp);
        button1.setOnClickListener(new View.OnClickListener() {

        });

         */
    }



}
