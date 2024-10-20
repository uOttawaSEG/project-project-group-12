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
public class Registration {

    private String email;
    private String password;
    private String role;

    public Registration(String email, String pwd, String role) {
        this.email = email;
        this.password = pwd;
        this.role = role;
    }
    // Default Constructor
    public Registration() {
        // For firebase (needs an empty constructor method)
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

    public String getRole(){
        return role;
    }

}