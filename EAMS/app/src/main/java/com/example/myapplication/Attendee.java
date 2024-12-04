package com.example.myapplication;

import androidx.annotation.NonNull;
import java.util.ArrayList;

/**
 * The Attendee class is a subclass of User and represents an attendee.
 */
public class Attendee extends User {
    private ArrayList<String> eventIds;

    /**
     * Constructs a new Attendee with the given details, including a list of event IDs.
     *
     * @param firstName the attendee's first name
     * @param lastName the attendee's last name
     * @param phoneNumber the attendee's phone number
     * @param address the attendee's address
     * @param userType the attendee's user type
     * @param status the attendee's status
     * @param eventIds the list of event IDs associated with the attendee
     */
    public Attendee(String firstName, String lastName, String phoneNumber, String address, String userType, String status, ArrayList<String> eventIds) {
        super(firstName, lastName, phoneNumber, address, userType, status); // Call parent class constructor
        this.eventIds = eventIds != null ? eventIds : new ArrayList<>(); // Initialize with the provided list or an empty one
    }

    /**
     * Default constructor for Firebase.
     */
    public Attendee() {
        super();
        this.eventIds = new ArrayList<>(); // Initialize with an empty ArrayList
    }

    /**
     * Gets the list of event IDs.
     *
     * @return the list of event IDs
     */
    public ArrayList<String> getEventIds() {
        return eventIds;
    }

    /**
     * Adds an event ID to the list.
     *
     * @param eventId the event ID to add
     */
    public void addEventId(String eventId) {
        if (!eventIds.contains(eventId)) {
            eventIds.add(eventId);
        }
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return getUid() != null && getUid().equals(attendee.getUid());
    }

    @Override
    public int hashCode() {
        return getUid() != null ? getUid().hashCode() : 0;
    }
    /**
     * Removes an event ID from the list.
     *
     * @param eventId the event ID to remove
     */
    public void removeEventId(String eventId) {
        eventIds.remove(eventId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Role: " + getUserType() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Name: " + getFirstName() + " " + getLastName() + "\n" +
                "Phone: " + getPhoneNumber() + "\n" +
                "Address: " + getAddress() + "\n" +
                "Event IDs: " + eventIds;
    }
}
