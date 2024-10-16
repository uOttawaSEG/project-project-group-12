package com.example.myapplication;

import  android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.LoginButton);
        Button button2 = findViewById(R.id.RegisterButton);

        // Sends user to Login page
        button1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginPage.class)));

        // Sends user to Register Page
        button2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegistrationPage.class)));



    }
}