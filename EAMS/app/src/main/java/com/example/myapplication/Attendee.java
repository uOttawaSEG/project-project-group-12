package com.example.myapplication;

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

    public Attendee(String firstName, String lastName, String phoneNumber, String address, String role) {
        super(firstName, lastName, phoneNumber, address, role); //Call parent class constructor
    }
    public Attendee() {
        super();
        // For firebase
    }

}
