package com.example.myapplication;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;
/**
 * The Administrator class represents a Administrator with common fields like
 * email, password.
 */
public class Administrator {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Administrator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }
    // Default Constructor
    public Administrator() {
        // For firebase (needs an empty constructor method)
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}