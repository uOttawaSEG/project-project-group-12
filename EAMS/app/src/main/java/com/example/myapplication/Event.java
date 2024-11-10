package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Event {
    private String eventId, title, description, eventAddress;
    private Date startTime, endTime;
    private List<Attendee> pendingAttendeesList, acceptedAttendeesList;



    public Event(String title, String description , String eventAddress, Date startTime , Date endTime, String eventId){
        this.title = title;
        this.description = description;
        this.eventAddress = eventAddress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventId = eventId;
        //create 2 empty arraylists
        pendingAttendeesList = new ArrayList<>();
        acceptedAttendeesList = new ArrayList<>();
    }

    //if we ever need to create an event with an already populated list
    public Event(String title, String description , String eventAddress, Date startTime , Date endTime, String eventId, ArrayList<Attendee> pendingAttendeesList, ArrayList<Attendee> acceptedAttendeesList){
        this.title = title;
        this.description = description;
        this.eventAddress = eventAddress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventId = eventId;
        this.pendingAttendeesList = pendingAttendeesList;
        this.acceptedAttendeesList = acceptedAttendeesList;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }



    public Event(){
        //empty constructor for FireBase
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<Attendee> getPendingAttendeesList(){ return pendingAttendeesList;}

    public List<Attendee> getAcceptedAttendeesList() {return acceptedAttendeesList;}
}
