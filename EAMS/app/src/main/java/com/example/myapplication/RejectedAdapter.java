package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// The RejectedAdapter class is responsible for displaying a list of rejected registrations in a RecyclerView.
public class RejectedAdapter extends RecyclerView.Adapter<RejectedAdapter.ViewHolder> {
    // List to hold the rejected registration items
    private List<User> rejectedItems;
    // Listener to handle approve actions
    private RegistrationRejected.OnItemActionListener listener;

    // Constructor to initialize the adapter with rejected items and a listener
    public RejectedAdapter(List<User> rejectedItems, RegistrationRejected.OnItemActionListener listener) {
        this.rejectedItems = rejectedItems;
        this.listener = listener;
    }
    

    // Method to update the data in the adapter and refresh the view
    public void updateData(List<User> newItems) {
        this.rejectedItems = newItems;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // Method to create new view holders for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each rejected item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rejected_item_layout, parent, false);
        return new ViewHolder(view); // Return a new ViewHolder instance
    }

    // Method to bind data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current item from the list
        User item = rejectedItems.get(position);
        // Set the text for the item TextView
        holder.itemText.setText(item.toString());

        // Set click listener for approve button
        holder.approveButton.setOnClickListener(v -> listener.onApprove(item));
    }

    // Method to get the total number of items in the list
    @Override
    public int getItemCount() {
        return rejectedItems.size(); // Return the size of the rejected items list
    }

    // Inner class that holds the views for each rejected item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText; // TextView for displaying the item text
        Button approveButton; // Button to approve the registration

        // Constructor for the ViewHolder, initializing the views
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText); // Get reference to item text view
            approveButton = itemView.findViewById(R.id.approveBtn); // Get reference to approve button
        }
    }
}
