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

import java.util.ArrayList;
import java.util.List;


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
                                List<String> eventIds = new ArrayList<>();
                                Attendee userInfo = new Attendee(firstName,lastName,phoneNumber,address,"Attendee","pending", (ArrayList<String>) eventIds);
                                userInfo.setEmail(email);//should be able to query firebase auth to get email of user
                                mDatabase.child("users").child(userId).setValue(userInfo);
                            } else {
                                Organizer userInfo = new Organizer(firstName,lastName,phoneNumber,address,organizationName , "Organizer", "pending") ;
                                userInfo.setEmail(email);//should be able to query firebase auth to get email of user
                                mDatabase.child("users").child(userId).setValue(userInfo);
                            }

                            // Creating AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
                            builder.setTitle("The registration request was sent successfully");
                            builder.setMessage("Please wait for the admin's approval. \nYour account is: " + email);

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
                            Toast.makeText(RegistrationPage.this, "This email address has already been registered", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

    }



    private boolean checkAllFields() {
        boolean allFieldsValid = true;

        if (!RegistrationValidator.isValidFirstName(firstNameField.getText().toString())) {
            firstNameField.setError("First name must be at least 2 characters");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isValidLastName(lastNameField.getText().toString())) {
            lastNameField.setError("Last name must be at least 2 characters");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isValidEmail(emailField.getText().toString())) {
            emailField.setError("Valid email is required");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isValidPassword(passwordField.getText().toString())) {
            passwordField.setError("Password must be at least 8 characters long and contain a letter or number");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isPasswordMatching(passwordField.getText().toString(), confirmPasswordField.getText().toString())) {
            confirmPasswordField.setError("Two passwords are not the same");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isValidPhoneNumber(phoneNumberField.getText().toString())) {
            phoneNumberField.setError("Please enter numbers");
            allFieldsValid = false;
        }

        if (!RegistrationValidator.isValidAddress(addressField.getText().toString())) {
            addressField.setError("Address is required");
            allFieldsValid = false;
        }


        if (((RadioButton) findViewById(radioGroupField.getCheckedRadioButtonId())).getText().toString().equals("Organizer")
                && !RegistrationValidator.isValidOrganizationName(organizationNameField.getText().toString())) {
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