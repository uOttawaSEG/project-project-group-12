package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * The RegistrationsPendingList class manages a list of pending registrations,
 * allowing for adding, approving, and rejecting registrations.
 */
public class RegistrationsPendingList {

    //Static list to hold pending registrations
    private static List<String> pendingRegistration = new ArrayList<>();
    //Listener for item action events
    private OnItemActionListener listener;

    //Constructor that initializes the listener
    public RegistrationsPendingList(OnItemActionListener listener) {
        this.listener = listener;
    }

    //Method to add a registration to the pending list
    public static void addRegistration(String item) {
        pendingRegistration.add(item); //Add the item to the list
    }

    //Method to approve a registration from the pending list
    public static void approveRegistration(String item) {
        if (pendingRegistration.contains(item)) {
            //Notify the listener about the approval (if needed)
            //Instead of using a listener here, handle any necessary actions directly if required
            pendingRegistration.remove(item); //Remove the item from the list
        }
    }

    //Method to reject a registration from the pending list
    public static void rejectRegistration(String item) {
        if (pendingRegistration.contains(item)) {
            //Notify the listener about the rejection (if needed)
            //Instead of using a listener here, handle any necessary actions directly if required
            pendingRegistration.remove(item); //Remove the item from the list
        }
    }

    //Method to get the current list of pending registrations
    public static List<String> getPendingRegistrations() {
        return pendingRegistration; //Return the list of pending registrations
    }

    //Interface to handle actions on registration items
    public interface OnItemActionListener {
        void onApprove(String item); //Callback for approving an item
        void onReject(String item);   //Callback for rejecting an item
    }
}
