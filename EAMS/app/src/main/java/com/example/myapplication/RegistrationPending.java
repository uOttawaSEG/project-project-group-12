package com.example.myapplication;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import android.provider.ContactsContract;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationPending {
    private User user;
    private String status, email, password ,id;


    RegistrationPending(User user , String email , String password){
        this.user = user;
        this.email = email;
        this.password = password;
        this.status = "RegistrationPending";
    }


    RegistrationPending(){
        //Empty constructor for FireBase
    }


    //method to approve and reject request - should only be run by verified admin
    public void approveRegistration(){
        //this method should only be run by a logged in admin - who gave the the registration an id, email ,  password and a user

        OkHttpClient client = new OkHttpClient();


        //parse to json with GSON
        Gson gson = new GsonBuilder()
                //.serializeNulls() to Include nulls - but we don't want to include email and password, in the DB
                .create();

        String json = gson.toJson(this);
        System.out.println(json);

        //send to "backend" aka firebase cloud functions
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://your-backend-server.com/createUser")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    // TODO notify admin successfully approve registration

                } else {
                    // Handle error
                    //TODO notify admin of error
                }
            }
        });




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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
