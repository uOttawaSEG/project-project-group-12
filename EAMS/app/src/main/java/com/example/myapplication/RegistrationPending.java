package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * The RegistrationPending class manages a list of pending registrations,
 * allowing for adding, approving, and rejecting registrations.
 */
public class RegistrationPending {

/*    MailSender mailSender = new MailSender();

    public void sendEmail(String toEmail, String subject, String messageBody){
        mailSender.sendEmail(toEmail, subject, messageBody);
    }*/



    //Static list to hold pending registrations
    private static List<User> pendingRegistration = new ArrayList<>();
    //Listener for item action events
    private OnItemActionListener listener;

    //Constructor that initializes the listener
    public RegistrationPending(OnItemActionListener listener) {
        this.listener = listener;
    }

    //Method to add a registration to the pending list
    public static void addRegistration(User item) {
        pendingRegistration.add(item); //Add the item to the list
    }

    //Method to approve a registration from the pending list
    public static void approveRegistration(User item) {
        if (pendingRegistration.contains(item)) {
            //Notify the listener about the approval (if needed)
            //Instead of using a listener here, handle any necessary actions directly if required

            pendingRegistration.remove(item); //Remove the item from the list
            MailSender.getInstance().sendEmail(
                    item.getEmail(),
                    "Registration Approved",
                    "Congratulations! Your registration has been approved."
            );
        }
    }

    //Method to reject a registration from the pending list
    public static void rejectRegistration(User item) {
        if (pendingRegistration.contains(item)) {
            //Notify the listener about the rejection (if needed)
            //Instead of using a listener here, handle any necessary actions directly if required
            pendingRegistration.remove(item); //Remove the item from the list
        }
    }

    //Method to get the current list of pending registrations
    public static List<User> getPendingRegistrations() {
        return pendingRegistration; //Return the list of pending registrations
    }



    //Interface to handle actions on registration items
    public interface OnItemActionListener {
        void onApprove(User item); //Callback for approving an item
        void onReject(User item);   //Callback for rejecting an item
    }
}
