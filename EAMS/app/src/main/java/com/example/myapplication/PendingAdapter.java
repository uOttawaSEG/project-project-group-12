package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

// The PendingAdapter class is responsible for displaying a list of pending registrations in a RecyclerView.
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {
    // List to hold the pending registration items
    private List<User> pendingItems;
    // Listener to handle approve and reject actions
    private RegistrationsPending.OnItemActionListener listener;
    private RegistrationsPending registrationsPending;
    private RejectedAdapter rejectedAdapter;

    // Constructor to initialize the adapter with pending items and a listener
    public PendingAdapter(RegistrationsPending registrationsPending, RejectedAdapter rejectedAdapter) {
        this.registrationsPending = registrationsPending;
        this.pendingItems = registrationsPending.getPendingRegistrations();
        this.listener = registrationsPending.getListener();
        this.rejectedAdapter = rejectedAdapter;
    }

    // Method to update the data in the adapter and refresh the view
    public void updateData(List<User> newItems) {
        this.pendingItems = newItems;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // Method to create new view holders for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each pending item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_item_layout, parent, false);
        return new ViewHolder(view); // Return a new ViewHolder instance
    }

    // Method to bind data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current item from the list
        User item = pendingItems.get(position);
        // Set the text for the item TextView
        holder.itemText.setText(item.toString());

        // Set click listeners for approve and reject buttons
        holder.approveButton.setOnClickListener(v -> listener.onApprove(item, registrationsPending, this));
        holder.rejectButton.setOnClickListener(v -> {
            String uid = item.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userRef.child("status").setValue("rejected").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Remove user from pending list
                    registrationsPending.removeRegistration(item);
                    // Update pending UI adapter
                    updateData(registrationsPending.getPendingRegistrations());
                    /* Add user to rejected list and update UI
                    RegistrationRejected.addRejectedRegistration(item);
                    rejectedAdapter.updateData(RegistrationRejected.getRejectedRegistrations());
                        */

                    // Show rejection message
                    Toast.makeText(holder.itemView.getContext(), "User rejected successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to reject user", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // Method to get the total number of items in the list
    @Override
    public int getItemCount() {
        return pendingItems.size(); // Return the size of the pending items list
    }

    // Inner class that holds the views for each pending item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText; // TextView for displaying the item text
        Button approveButton; // Button to approve the registration
        Button rejectButton; // Button to reject the registration

        // Constructor for the ViewHolder, initializing the views
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText); // Get reference to item text view
            approveButton = itemView.findViewById(R.id.approveBtn); // Get reference to approve button
            rejectButton = itemView.findViewById(R.id.rejectBtn); // Get reference to reject button
        }
    }
}




