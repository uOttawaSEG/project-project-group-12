package com.example.myapplication;


/**
 * The Administrator class represents a Administrator with common fields like
 * email, password.
 */
public abstract class Administrator {

    private String email;
    private String password;

    // Default Constructor
    public Administrator() {
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

}