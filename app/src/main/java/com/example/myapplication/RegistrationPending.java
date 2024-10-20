package com.example.myapplication;




import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
/*

 */
public class RegistrationPending {

    private static List<String> pendingRegistration = new ArrayList<>();
    private OnItemActionListener listener;

    public RegistrationPending(OnItemActionListener listener) {
        this.listener = listener;
    }

    public static void addRegistration(String item) {
        pendingRegistration.add(item);
    }

    public static void approveRegistration(String item) {
        if (pendingRegistration.contains(item)) {
            // Notify the listener about the approval (if needed)
            // Instead of using a listener here, handle any necessary actions directly if required
            pendingRegistration.remove(item); // Remove the item from the list
        }
    }

    public static void rejectRegistration(String item) {
        if (pendingRegistration.contains(item)) {
            // Notify the listener about the rejection (if needed)
            // Instead of using a listener here, handle any necessary actions directly if required
            pendingRegistration.remove(item); // Remove the item from the list
        }
    }

    public static List<String> getPendingRegistrations() {
        return pendingRegistration;
    }

    public interface OnItemActionListener {
        void onApprove(String item);
        void onReject(String item);
    }
}
