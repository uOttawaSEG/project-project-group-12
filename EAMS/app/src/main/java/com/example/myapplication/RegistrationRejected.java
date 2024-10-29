package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * The RegistrationRejected class manages a list of rejected registrations,
 * allowing for displaying and approving rejected registrations.
 */
public class RegistrationRejected {

    // Static list to hold rejected registrations
    private  List<User> rejectedRegistration = new ArrayList<>();
    // Listener for item action events
    private OnItemActionListener listener;

    // Constructor that initializes the listener
    public RegistrationRejected(OnItemActionListener listener) {
        this.listener = listener;
    }
    public RegistrationRejected(){}

    // Method to add a registration to the rejected list
    public  void addRejectedRegistration(User item) {
        rejectedRegistration.add(item); // Add the item to the list
    }

    // Method to approve a registration from the rejected list
    public  void approveRegistration(User item) {
        if (rejectedRegistration.contains(item)) {
            rejectedRegistration.remove(item); // Remove the item from the list
            //TODO copy paste from Registration Pending
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.child(item.getUid()).child("status").setValue("approved");
            //smth
            //TODO Notify the listener about the approval action
        }
    }

    // Method to get the current list of rejected registrations
    public  List<User> getRejectedRegistrations() {
        return rejectedRegistration; // Return the list of rejected registrations
    }

    public void initListener() {
        this.listener = new RegistrationRejected.OnItemActionListener();
    }

    public OnItemActionListener getListener() {
        return listener;
    }

    // Interface to handle actions on registration items
    public class OnItemActionListener {
        public void onApprove(User user , RegistrationRejected registrationRejected, RejectedAdapter rejectedAdapter) //Callback for approving an item
        {
            //update db references
            registrationRejected.approveRegistration(user);
            //update UI - aka refresh the list of pending registration
            rejectedAdapter.updateData(registrationRejected.getRejectedRegistrations());
        }
    }
}
