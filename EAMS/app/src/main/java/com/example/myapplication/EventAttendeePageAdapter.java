package com.example.myapplication; // EventAttendeePageAdapter.java

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Filter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventAttendeePageAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> originalEvents; //represents the original set of events
    private List<Event> filteredEvents; //represents the filtered set of events
    private List<Event> events;
    private Attendee attendee;
    private String uid;

    public EventAttendeePageAdapter(Context context, List<Event> events, Attendee attendee, String uid) {
        super(context, R.layout.activity_attendee_page, events);
        this.context = context;
        this.originalEvents = new ArrayList<>(events); //make a copy of the original list for filtering
        this.filteredEvents = events; //this is the filtered list
        this.attendee = attendee;
        this.uid = uid; // Store the Attendee object
    }

/*    public int getCount() {
        return filteredEvents.size();
    }

    public Event getItem(int position){
        return filteredEvents.get(position);
    }*/

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        ViewHolder holder;

        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            listItem = inflater.inflate(R.layout.event_attendee_page_item, parent, false);

            holder = new ViewHolder();
            holder.eventTitleTextView = listItem.findViewById(R.id.eventTitleTextView);
            holder.descriptionTextView = listItem.findViewById(R.id.eventDescriptionTextView);
            holder.addressEventTextView = listItem.findViewById(R.id.addressEventTextView);
            holder.startTimeTextView = listItem.findViewById(R.id.startTime);
            holder.JoinView = listItem.findViewById(R.id.JoinEvent);

            listItem.setTag(holder);

        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        Event event = filteredEvents.get(position);
        holder.descriptionTextView.setText(event.getDescription());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.addressEventTextView.setText(event.getEventAddress());
        holder.startTimeTextView.setText(event.getStartTime().toString());

        // Set the Join button's click listener
        holder.JoinView.setOnClickListener(v -> {
            //flag to check for conflicts
            final boolean[] conflicted = {false};

            List<Task<DataSnapshot>> tasks = new ArrayList<>();

            DatabaseReference events = FirebaseDatabase.getInstance().getReference("events");

            //loop through each event and get the event data from DB
            for (String eventID : attendee.getEventIds()) {
                Task<DataSnapshot> task = events.child(eventID).get();
                tasks.add(task);
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(taskList -> {
                //check if there is any conflict
                for (Task<DataSnapshot> t : tasks) {
                    if (t.isSuccessful()) {
                        DataSnapshot dataSnapshot = t.getResult();
                        if (dataSnapshot.exists()) {
                            long startTime1 = dataSnapshot.child("startTime").getValue(Date.class).getTime();
                            long endTime1 = dataSnapshot.child("endTime").getValue(Date.class).getTime();
                            long startTime2 = event.getStartTime().getTime();
                            long endTime2 = event.getEndTime().getTime();

                            //update flag if conflict
                            if (startTime1 < endTime2 && startTime2 < endTime1) {
                                conflicted[0] = true;
                                break;  //conflict found so break
                            }
                        }
                    }
                }

                //checks for conflict
                if (conflicted[0]) {
                    Toast.makeText(context, "This event conflicts with another of your joined events.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //no conflicts so proceed to join
                addToListBasedOnAutoAccept(event);
                filteredEvents.remove(position);
                originalEvents.remove(event);

                //add eventId to attendee list
                attendee.addEventId(event.getEventId());

                notifyDataSetChanged();
            });
        });

        return listItem;
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Event> filteredResults = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(originalEvents); //if there are no constraints show all the events
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Event event : originalEvents) {
                        if (event.getTitle().toLowerCase().contains(filterPattern) || event.getDescription().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(event); //add events that match the filter pattern
                        }
                    }
                }

                Log.d("EventFilter", "Filtered " + filteredResults.size() + " events matching the pattern: " + constraint); //debug

                //put the results in a type of FilterResults and return it, becuz performFiltering method expects a FilterResults object to be returned as specified in API
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEvents.clear();
                filteredEvents.addAll((List) results.values); //update filtered list with the results
                notifyDataSetChanged(); //notify the adapter to update the ListView
            }
        };
    }

    private static class ViewHolder {
        TextView eventTitleTextView, descriptionTextView, addressEventTextView, startTimeTextView;
        Button JoinView;
    }

    private void addToListBasedOnAutoAccept(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());
        DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        // Fetch the current event data to get the existing lists of attendees
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the event from the database
                Event eventFromDb = dataSnapshot.getValue(Event.class);
                if (eventFromDb != null) {
                    // Create temporary lists if the event was not retrieved
                    List<Attendee> pendingAttendeesList = eventFromDb.getPendingAttendeesList() != null ? eventFromDb.getPendingAttendeesList() : new ArrayList<>();
                    List<Attendee> acceptedAttendeesList = eventFromDb.getAcceptedAttendeesList() != null ? eventFromDb.getAcceptedAttendeesList() : new ArrayList<>();

                    // Check the auto-accept status and add the attendee accordingly
                    if ("On".equals(event.getAutoAccept())) {
                        // Log the action and add attendee to the accepted list in Firebase
                        Log.d("EventAttendeePageAdapter", "Auto accept is ON. Adding attendee to accepted list. Event ID: " + event.getEventId());
                        acceptedAttendeesList.add(attendee);  // Add the second attendee to the accepted list
                        Toast.makeText(context, "You have been added to the accepted list!", Toast.LENGTH_SHORT).show();


                        attendee.addEventId(event.getEventId());

                    } else {
                        // Add attendee to the pending list in Firebase
                        Log.d("EventAttendeePageAdapter", "Auto accept is OFF. Adding attendee to pending list. Event ID: " + event.getEventId());
                        pendingAttendeesList.add(attendee);  // Add the first attendee to the pending list
                        Toast.makeText(context, "Your registration is pending!", Toast.LENGTH_SHORT).show();

                        attendee.addEventId(event.getEventId());
                    }

                    // Reconstruct the event object with updated lists
                    Event updatedEvent = new Event(
                            eventFromDb.getTitle(),
                            eventFromDb.getDescription(),
                            eventFromDb.getEventAddress(),
                            eventFromDb.getStartTime(),
                            eventFromDb.getEndTime(),
                            eventFromDb.getEventId(),
                            (ArrayList<Attendee>) pendingAttendeesList,
                            (ArrayList<Attendee>) acceptedAttendeesList,
                            eventFromDb.getAutoAccept(),
                            eventFromDb.getOrganizerUId()
                    );

                    // Update the event in the database
                    eventRef.setValue(updatedEvent).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Event updated successfully
                            Log.d("EventAttendeePageAdapter", "Event updated successfully. Event ID: " + event.getEventId());
                        } else {
                            // Handle the failure of updating the event
                            Log.d("EventAttendeePageAdapter", "Failed to update event. Event ID: " + event.getEventId());
                        }
                    });

                    // Now update the Attendee object in the "users" node
                    attendeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Retrieve the Attendee from the database
                            Attendee currentAttendee = dataSnapshot.getValue(Attendee.class);
                            if (currentAttendee != null) {
                                // Update the eventIds list of the Attendee
                                currentAttendee.addEventId(event.getEventId());

                                // Update the Attendee object in the database
                                attendeeRef.setValue(currentAttendee).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("EventAttendeePageAdapter", "Attendee updated successfully with event ID.");
                                    } else {
                                        Log.d("EventAttendeePageAdapter", "Failed to update Attendee with event ID.");
                                    }
                                });
                            } else {
                                Log.d("EventAttendeePageAdapter", "Attendee not found. UID: " + uid);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("EventAttendeePageAdapter", "Failed to fetch Attendee data: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.d("EventAttendeePageAdapter", "Event not found. Event ID: " + event.getEventId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("EventAttendeePageAdapter", "Failed to fetch event data: " + databaseError.getMessage());
            }
        });
    }

}
