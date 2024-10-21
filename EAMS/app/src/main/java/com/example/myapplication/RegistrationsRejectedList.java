package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * The RegistrationsRejectedList class manages a list of rejected registrations,
 * allowing for displaying and approving rejected registrations.
 */
public class RegistrationsRejectedList {

    // Static list to hold rejected registrations
    private static List<String> rejectedRegistration = new ArrayList<>();
    // Listener for item action events
    private OnItemActionListener listener;

    // Constructor that initializes the listener
    public RegistrationsRejectedList(OnItemActionListener listener) {
        this.listener = listener;
    }

    // Method to add a registration to the rejected list
    public static void addRejectedRegistration(String item) {
        rejectedRegistration.add(item); // Add the item to the list
    }

    // Method to approve a registration from the rejected list
    public static void approveRegistration(String item) {
        if (rejectedRegistration.contains(item)) {
            // Notify the listener about the approval action
            rejectedRegistration.remove(item); // Remove the item from the list
            // Here, you can notify the listener if you want
            // listener.onApprove(item); // Uncomment if you want to use the listener
        }
    }

    // Method to get the current list of rejected registrations
    public static List<String> getRejectedRegistrations() {
        return rejectedRegistration; // Return the list of rejected registrations
    }

    // Interface to handle actions on registration items
    public interface OnItemActionListener {
        void onApprove(String item); // Callback for approving an item
    }
}
