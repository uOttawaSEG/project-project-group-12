package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class EventCreationPage extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventDescription;
    private EditText eventLocation;

    private Button pickStartDateButton;
    private Button pickEndDateButton;
    private Button pickStartTimeButton;
    private Button pickEndTimeButton;
    private Button createEventButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_creation_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventLocation = findViewById(R.id.eventLocation);

        pickStartDateButton = findViewById(R.id.pickStartDateButton);
        pickEndDateButton = findViewById(R.id.pickEndDateButton);

        pickStartTimeButton = findViewById(R.id.pickStartTimeButton);
        pickEndTimeButton = findViewById(R.id.pickEndTimeButton);

        createEventButton = findViewById(R.id.createEventButton);

        //listener for date and time picker

        pickStartDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            //initializing the start date picker with the current date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            //creation and display of start date pickr
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventCreationPage.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear; //+1 because months start with 0.
                        pickStartDateButton.setText(selectedDate); //set the text of the button with the selected date so the user can easily see what dat ehe picked
                    }, year, month, day);
            datePickerDialog.show(); //show the date picker
        });


        pickEndDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            //initializing the end date picker with the current date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            //creation and display of end date pickr
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventCreationPage.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear; //+1 because months start with 0.
                        pickEndDateButton.setText("Start Date: " + selectedDate); //set the text of the button with the selected date so the user can easily see what dat ehe picked
                    }, year, month, day);
            datePickerDialog.show(); //show the date picker
        });



        pickStartTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            //initializing the start time picker with the current time
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            //creation and display of time picker
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    EventCreationPage.this,
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute);
                        pickStartTimeButton.setText(selectedTime); //set the text of the button with the selected time so the user can easily see what time he picked
                    },
                    hour, minute, true);
            timePickerDialog.show(); //to show the time picker
        });


        pickEndTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            //initializing the end time picker with the current time
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            //creation and display of time picker
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    EventCreationPage.this,
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute);
                        pickEndTimeButton.setText(selectedTime); //set the text of the button with the selected time so the user can easily see what time he picked
                    },
                    hour, minute, true);
            timePickerDialog.show(); //to show the time picker
        });



        //listener for the create button
        createEventButton.setOnClickListener(v -> {

            //get all the infos ready to send to firebase
            String title = eventTitle.getText().toString();
            String description = eventDescription.getText().toString();
            String location = eventLocation.getText().toString();
            String startDate = pickStartDateButton.getText().toString();
            String endDate = pickEndDateButton.getText().toString();
            String startTime = pickStartTimeButton.getText().toString();
            String endTime = pickEndTimeButton.getText().toString();

            Log.i("Event Creation Page", "Title: " + title);
            Log.i("Event Creation Page", "Description: " + description);
            Log.i("Event Creation Page", "Location: " + location);
            Log.i("Event Creation Page", "Start Date: " + startDate);
            Log.i("Event Creation Page", "End Date: " + endDate);
            Log.i("Event Creation Page", "Start Time: " + startTime);
            Log.i("Event Creation Page", "End Time: " + endTime);

            //to do: code for sending info gathered to firebase


        });


    }
}
