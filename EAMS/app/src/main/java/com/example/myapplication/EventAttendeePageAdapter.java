package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EventAttendeePageAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public EventAttendeePageAdapter(Context context, List<Event> events){
        super(context,R.layout.activity_attendee_page,events );
        this.context = context;
        this.events = events;
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
            //initialise a new video if none was there
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
        //populate view with event attributes
        holder.descriptionTextView.setText(event.getDescription());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.addressEventTextView.setText(event.getEventAddress());
        holder.startTimeTextView.setText(event.getStartTime().toString());
        //TODO join view add event listener for button


        return listItem;
    }




    private static class ViewHolder {
        TextView eventTitleTextView, descriptionTextView, addressEventTextView, startTimeTextView;
        Button JoinView;
    }
}
