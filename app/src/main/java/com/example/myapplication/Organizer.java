package com.example.myapplication;


/**
 * The Organizer class is a subclass of User and represents an organizer with an additional
 * field for the organization name.
 */
public class Organizer extends User {
    private String organizationName;

    /**
     * Constructs a new Organizer with the given details and organization name.
     *
     * @param firstName the organizer's first name
     * @param lastName the organizer's last name
     * @param phoneNumber the organizer's phone number
     * @param address the organizer's address
     * @param organizationName the name of the organization the organizer represents
     */
    public Organizer(String firstName, String lastName, String phoneNumber, String address, String organizationName) {
        super(firstName, lastName, phoneNumber, address); // Call parent class constructor
        this.organizationName = organizationName;
    }

    public Organizer() {
        super();
        // For firebase
    }


    //Getter and Setter for organizationName
    public String getOrganizationName() {

        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


}
