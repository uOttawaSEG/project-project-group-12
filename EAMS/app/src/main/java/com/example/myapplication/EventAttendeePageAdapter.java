package com.example.myapplication;// EventAttendeePageAdapter.java

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Attendee;
import com.example.myapplication.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventAttendeePageAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;
    private Attendee attendee;
    private String uid;

    public EventAttendeePageAdapter(Context context, List<Event> events, Attendee attendee, String uid) {
        super(context, R.layout.activity_attendee_page, events);
        this.context = context;
        this.events = events;
        this.attendee = attendee;
        this.uid = uid;// Store the Attendee object
    }

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
            holder.addressEventTextView = listItem.findViewById(R.id.addressEventTextVIew);
            holder.startTimeTextView = listItem.findViewById(R.id.startTime);
            holder.JoinView = listItem.findViewById(R.id.JoinEvent);

            listItem.setTag(holder);

        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        Event event = events.get(position);
        holder.descriptionTextView.setText(event.getDescription());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.addressEventTextView.setText(event.getEventAddress());
        holder.startTimeTextView.setText(event.getStartTime().toString());

        // Set the Join button's click listener
        holder.JoinView.setOnClickListener(v -> {
            addToListBasedOnAutoAccept(event);
            events.remove(position);
            notifyDataSetChanged();
        });

        return listItem;
    }


    private void addToListBasedOnAutoAccept(Event event) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(event.getEventId());

        if ("On".equals(event.getAutoAccept())) {
            // Log the action and add attendee to the accepted list in Firebase
            Log.d("EventAttendeePageAdapter", "Auto accept is ON. Adding attendee to accepted list. Event ID: " + event.getEventId());
            databaseReference.child("acceptedAttendeesList").push().setValue(new Attendee("pl333ease", "help", "456789", "please", "Attendee", "approved"));
            // Log the action and add attendee to the pending list in Firebase
            Log.d("EventAttendeePageAdapter", "Auto accept is OFF. Adding attendee to pending list. Event ID: " + event.getEventId());
            databaseReference.child("pendingAttendeesList").push().setValue(new Attendee("pl333ease", "help", "456789", "please", "Attendee", "approved"));
        }

    }

    private static class ViewHolder {
        TextView eventTitleTextView, descriptionTextView, addressEventTextView, startTimeTextView;
        Button JoinView;
    }
}
