package com.example.myapplication;

import androidx.annotation.NonNull;

/**
 * The Attendee class is a subclass of User and represents an attendee.
 * It does not add any additional fields to the base User class.
 */
public class Attendee extends User {

    /**
     * Constructs a new Attendee with the given details.
     *
     * @param firstName the attendee's first name
     * @param lastName the attendee's last name
     * @param phoneNumber the attendee's phone number
     * @param address the attendee's address
     */

    public Attendee(String firstName, String lastName, String phoneNumber, String address, String userType, String status) {
        super(firstName, lastName, phoneNumber, address, userType, status); //Call parent class constructor
    }
    public Attendee() {
        super();
        // For firebase
    }

    @NonNull
    @Override
    public String toString() {
        return "Role: " + getUserType() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Name: " + getFirstName() + " " + getLastName() + "\n" +
                "Phone: " + getPhoneNumber() + "\n" +
                "Address: " + getAddress();
    }

}
