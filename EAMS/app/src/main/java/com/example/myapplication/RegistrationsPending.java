package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * The RegistrationPending class manages a list of pending registrations,
 * allowing for adding, approving, and rejecting registrations.
 */
public class RegistrationsPending {

    //Static list to hold pending registrations
    private List<User> pendingRegistration ;



    //Listener for item action events
    private OnItemActionListener listener;
    private RegistrationRejected registrationRejected;

    //Constructor that initializes the listener
    public RegistrationsPending(OnItemActionListener listener) {
        this.listener = listener;
        this.pendingRegistration = new ArrayList<>();
    }
    public RegistrationsPending(){
        this.pendingRegistration = new ArrayList<>();
    }

    //Method to add a registration to the pending list
    public  void addRegistration(User item) {
        pendingRegistration.add(item); //Add the item to the list
    }

    //Method to approve a registration from the pending list
    public  void approveRegistration(User item) {
        if (pendingRegistration.contains(item)) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.child(item.getUid()).child("status").setValue("approved");
            pendingRegistration.remove(item); //Remove the item from the list
            //TODO Notify the listener about the approval

        }
    }

    //Method to reject a registration from the pending list
    public  void rejectRegistration(User item) {
        if (pendingRegistration.contains(item)) {
            //TODO rejection db manipulation and connect add elem to RegistrationRejected class  :-0
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.child(item.getUid()).child("status").setValue("rejected");
            registrationRejected.addRejectedRegistration(item);
            pendingRegistration.remove(item); //Remove the item from the list
            //TODO Notify the listener about the rejection (if needed)
        }
    }

    //Method to get the current list of pending registrations
    public  List<User> getPendingRegistrations() {
        return pendingRegistration; //Return the list of pending registrations
    }

    public OnItemActionListener getListener() {
        return listener;
    }

    public void initRegistrationRejected(RegistrationRejected registrationRejected){
        this.registrationRejected = registrationRejected;
    }

    public void initListener(){
        this.listener = new OnItemActionListener();
    }

    public void removeRegistration(User item) {
        pendingRegistration.remove(item);
    }

    //Event Listener to handle actions on registration items
    public class OnItemActionListener {
        public void onApprove(User user , RegistrationsPending registrationsPending, PendingAdapter pendingAdapter) //Callback for approving an item
        {
            //update db references
            registrationsPending.approveRegistration(user);
            //update UI - aka refresh the list of pending registration
            pendingAdapter.updateData(registrationsPending.getPendingRegistrations());
        }

        public void onReject(User user, RegistrationsPending registrationsPending, PendingAdapter pendingAdapter)   //Callback for rejecting an item
        {
            //update db references
            registrationsPending.rejectRegistration(user);
            //update UI - aka refresh the list of pending registration
            pendingAdapter.updateData(registrationsPending.getPendingRegistrations());
        }

        public  OnItemActionListener(){}
    }
}
