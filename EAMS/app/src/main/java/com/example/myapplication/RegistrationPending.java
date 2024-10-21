package com.example.myapplication;

public class RegistrationPending {
    private User user;
    private String status;




    RegistrationPending(User user){
        this.user = user;
        this.status = "RegistrationPending";
    }


    RegistrationPending(){
        //Empty constructor for FireBase
    }


    //getters and setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
