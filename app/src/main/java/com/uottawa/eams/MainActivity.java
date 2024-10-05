package com.uottawa.eams;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnOrganizer;
    private Button btnAttendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOrganizer = findViewById(R.id.btnOrganizer);
        btnAttendee = findViewById(R.id.btnAttendee);

        // Navigate to ActivityOrganizer when the Organizer button is clicked
        btnOrganizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityOrganizer.class);
                startActivity(intent);
            }
        });

        // Navigate to ActivityAttendee when the Attendee button is clicked
        btnAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityAttendee.class);
                startActivity(intent);
            }
        });
    }
}
