package com.uottawa.eams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityOrganizer extends AppCompatActivity {

    private Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        btnMain = findViewById(R.id.btnMain);

        // Navigate to ActivityOrganizer when the Organizer button is clicked
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityOrganizer.this, MainActivity.class); // Fixed the Intent constructor
                startActivity(intent);
            }
        });
    }
}
