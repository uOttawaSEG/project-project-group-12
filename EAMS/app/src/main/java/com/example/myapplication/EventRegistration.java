package com.example.myapplication;

//NOT USED YET
public class EventRegistration {
    private String status;

    private Event event;
    private String eventId; // may not need it hence why not in constructor

    private Attendee attendee;
    private String attendeeId; // may not need it hence why not in constructor


    public EventRegistration(String status , Event event , Attendee attendee){
        this.status = status;
        this.event = event;
        this.attendee = attendee;
    }



    public  EventRegistration(){
        //empty constructor for FireBase
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public String getAttendeeId() {
        return attendeeId;
    }
}
