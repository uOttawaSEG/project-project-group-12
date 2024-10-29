package com.example.myapplication;


/**
 * The User class represents a user with common fields like first name, last name,
 * email, password, phone number, and address. It serves as a base class for Organizer and Attendee.
 */
public abstract class User {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String userType;
    private String status;
    private String uid;

    /**
     * Constructs a new User with the given details.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param phoneNumber the user's phone number
     * @param address the user's address
     */
    public User(String firstName, String lastName, String phoneNumber, String address, String userType , String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userType = userType;
        this.status = status;
    }

    public User() {
        // For firebase (needs an empty constructor method)
    }


    //Getters and Setters
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}