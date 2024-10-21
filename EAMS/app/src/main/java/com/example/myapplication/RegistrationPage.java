package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;


import androidx.appcompat.app.AlertDialog;



public class RegistrationPage extends AppCompatActivity {

    //form Field
    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField, addressField, phoneNumberField, organizationNameField;
    private RadioGroup radioGroupField;
    private RadioButton organizerRadioButton, attendeeRadioButton;
    private Button logInButton;
    // Firebase Authentication and database reference
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //for field validation ?
    private Boolean checkAllFields;


    //check if not already log -> redirect to another activity/page
    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
     */



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



        //init form fields
        firstNameField  = findViewById(R.id.firstName);
        lastNameField = findViewById(R.id.lastName);
        emailField = findViewById(R.id.emailAddress);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirmPassword);
        addressField = findViewById(R.id.Address);
        phoneNumberField = findViewById(R.id.PhoneNumber);
        organizationNameField = findViewById(R.id.organizationNameField);
        radioGroupField = findViewById(R.id.radioGroup);
        organizerRadioButton = findViewById(R.id.organizerRadioButton);
        attendeeRadioButton = findViewById(R.id.attendeeRadioButton);


        //setting up the onCheckedChangeListener for the radioGroup
        radioGroupField.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.organizerRadioButton) {
                //field fades in when selecting organizer
                fadeInView(organizationNameField);
            }
            else{
                //field fades out if selected attendee (or stays hidden in the first place)
                fadeOutView(organizationNameField);
            }
        });


        // Initialize Firebase Authentication and  database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Button confirmSignUpButton = findViewById(R.id.ConfirmSignUp);
        //anonymous function for event listener bruh
        confirmSignUpButton.setOnClickListener(v -> {
            addUserToDB();
        });


        //Back button
        Button backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            }
        });

    }

    private void addAdminToDB() {
        String email = "admin90@gmail.com";
        String password = "adminadmin";



        //Using Firebase auth -  handles user session, password hashing,etc

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //when a user is created successfully automatically signed in
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            Administrator userInfo = new Administrator();
                            mDatabase.child("users").child(userId).setValue(userInfo);
                            mDatabase.child("users").child(userId).child("userType").setValue("Administrator");

                            // Creating AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
                            builder.setTitle("Registration Successful");
                            builder.setMessage("Your account is: " + email);

                            // Back to Login Button
                            builder.setPositiveButton("Back to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // back to login then
                                    Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                            builder.setCancelable(false);
                            AlertDialog dialog = builder.create();
                            dialog.show();



                        } else {
                            //TODO there was an error in the registration process
                            Toast.makeText(RegistrationPage.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

    }


    private void addUserToDB(){
        if (!checkAllFields() ) {
            //Toast.makeText(RegistrationPage.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
            return;
        }

        ///rest of method - for now at least
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();
        String organizationName = organizationNameField.getText().toString().trim();


        //create our registration object
        User userInfo;
        String userType = ((RadioButton)findViewById(radioGroupField.getCheckedRadioButtonId())).getText().toString().toLowerCase();
        if ( userType.equals("attendee")){
            userInfo = new Attendee(firstName,lastName,phoneNumber,address, "Attendee");
        } else {
            userInfo = new Organizer(firstName,lastName,phoneNumber,address,organizationName , "Organizer") ;
        }
        RegistrationPending registrationPending = new RegistrationPending(userInfo);

        //generate unique ID
        String registrationID =  mDatabase.child("registration").push().getKey();

        //write to database
        assert registrationID != null; //throws error if null
        mDatabase.child("registration").child(registrationID).setValue(userInfo)
                .addOnSuccessListener(aVoid -> {
                    //display log of successful registration

                    // Creating AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
                    builder.setTitle("Registration Successful");
                    builder.setMessage("Now wait for the Admin to approve the request of the account with this email address " + email );

                    // Back to Login Button
                    builder.setPositiveButton("Back to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // back to login then
                            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });


                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                })
                .addOnFailureListener(e -> Toast.makeText(RegistrationPage.this, "Failed registration attempt", Toast.LENGTH_SHORT).show());




        //Using Firebase auth -  handles user session, password hashing,etc

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //when a user is created successfully automatically signed in
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            // Attendee or  Organizer  - idk the id -_- could guess but nah -> tho prolly better
                            String userType = ((RadioButton)findViewById(radioGroupField.getCheckedRadioButtonId())).getText().toString().toLowerCase();

                            if ( userType.equals("attendee")){
                                Attendee userInfo = new Attendee(firstName,lastName,phoneNumber,address, "Attendee");
                                mDatabase.child("users").child(userId).setValue(userInfo);
                                mDatabase.child("users").child(userId).child("userType").setValue("attendee");
                            } else {
                                Organizer userInfo = new Organizer(firstName,lastName,phoneNumber,address,organizationName , "Organizer") ;
                                mDatabase.child("users").child(userId).setValue(userInfo);
                                mDatabase.child("users").child(userId).child("userType").setValue("organizer");
                            }

                            // Creating AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
                            builder.setTitle("Registration Successful");
                            builder.setMessage("Your account is: " + email);

                            // Back to Login Button
                            builder.setPositiveButton("Back to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // back to login then
                                    Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                            builder.setCancelable(false);
                            AlertDialog dialog = builder.create();
                            dialog.show();



                        } else {
                            //TODO there was an error in the registration process
                            Toast.makeText(RegistrationPage.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

    }


    private boolean checkAllFields() {
        boolean allFieldsValid = true;


        //first name must contain 2 letters
        if (firstNameField.getText().toString().trim().length() < 2) {
            firstNameField.setError("First name must be at least 2 characters");
            allFieldsValid = false;
        }

        // lastname must contain 2 letters
        if (lastNameField.getText().toString().trim().length() < 2) {
            lastNameField.setError("Last name must be at least 2 characters");
            allFieldsValid = false;
        }


        // email must contain @
        String emailInput = emailField.getText().toString();
        if (!emailInput.contains("@")) {
            emailField.setError("Valid email is required");
            allFieldsValid = false;
        }

        // password must contain 1 letter or 1 number
        String passwordInput = passwordField.getText().toString();
        if (passwordInput.length() < 8 || !passwordInput.matches(".*[a-zA-Z0-9].*")) {
            passwordField.setError("Password must be at least 8 characters long and contain a letter or number");
            allFieldsValid = false;
        }


        // double check password
        String confirmPasswordInput = confirmPasswordField.getText().toString();

        if (!confirmPasswordInput.equals(passwordInput)) {
            confirmPasswordField.setError("Two passwords are not the same");
            allFieldsValid = false;
        }

        // phone number must be pure numbers
        String phoneInput = phoneNumberField.getText().toString();
        if (phoneInput.isEmpty() || !phoneInput.matches(".*[0-9].*")) {
            phoneNumberField.setError("Please enter numbers");
            allFieldsValid = false;
        }

        // check address input is empty or not
        if (addressField.getText().toString().trim().isEmpty()) {
            addressField.setError("Address is required");
            allFieldsValid = false;
        }

        //check if user selected a identity
        if (radioGroupField.getCheckedRadioButtonId() == -1) {
            Toast.makeText(RegistrationPage.this, "Please select registering as Attendee or Organizer", Toast.LENGTH_SHORT).show();
            allFieldsValid = false;
        }

        if(((RadioButton)findViewById(radioGroupField.getCheckedRadioButtonId())).getText().toString().equals("Organizer") && organizationNameField.getText().toString().trim().isEmpty()){
            organizationNameField.setError("Organization name is required");
            allFieldsValid = false;
        }


        return allFieldsValid;
    }

    //creating the two methods to fadeIn/fadeOut the organizationNameField
    private void fadeInView(EditText randomField){
        randomField.setVisibility(View.VISIBLE);
        randomField.setAlpha(0f);
        randomField.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);
    }
    //creating the two methods to fadeIn/fadeOut the organizationNameField
    private void fadeOutView(EditText randomField){
        randomField.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        randomField.setVisibility(View.GONE);
                    }
                });
    }


}