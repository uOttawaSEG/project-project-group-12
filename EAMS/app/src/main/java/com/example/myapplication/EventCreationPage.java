package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventCreationPage extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventDescription;
    private EditText eventLocation;

    private Button pickStartDateButton;
    private Button pickEndDateButton;
    private Button pickStartTimeButton;
    private Button pickEndTimeButton;
    private Button createEventButton;
    private Button backToPage;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();


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

        //checking if fetching from update
        String title = "", description="", location="";
        Intent intent1 = getIntent();
        if (intent1.getStringExtra("event_title") != null) {
            title = intent1.getStringExtra("event_title");
            description = intent1.getStringExtra("event_description");
            location = intent1.getStringExtra("event_location");
        }


            eventTitle = findViewById(R.id.eventTitle);
            eventDescription = findViewById(R.id.eventDescription);
            eventLocation = findViewById(R.id.eventLocation);

            pickStartDateButton = findViewById(R.id.pickStartDateButton);
            pickEndDateButton = findViewById(R.id.pickEndDateButton);

            pickStartTimeButton = findViewById(R.id.pickStartTimeButton);
            pickEndTimeButton = findViewById(R.id.pickEndTimeButton);

            createEventButton = findViewById(R.id.createEventButton);
            backToPage = findViewById((R.id.backButton2));

            //pre-fill if fetching data to update:
            if(!title.equals("")){
                eventTitle.setText(title);
                eventDescription.setText(description);
                eventLocation.setText(location);

            }

            backToPage.setOnClickListener(v -> {
                Intent intent = new Intent(EventCreationPage.this, OrganizerPage.class);
                startActivity(intent);
            });

            pickStartDateButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();

                //initializing the start date picker with the current date
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                //creation and display of start date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EventCreationPage.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            startCalendar.set(Calendar.YEAR, selectedYear);
                            startCalendar.set(Calendar.MONTH, selectedMonth);
                            startCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear; //+1 because months start with 0.
                            pickStartDateButton.setText("Start date: " + selectedDate); //set the text of the button with the selected date so the user can easily see what dat ehe picked
                        }, year, month, day);
                datePickerDialog.show(); //show the date picker
            });


            pickEndDateButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();

                //initializing the end date picker with the current date
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                //creation and display of end date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EventCreationPage.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            endCalendar.set(Calendar.YEAR, selectedYear);
                            endCalendar.set(Calendar.MONTH, selectedMonth);
                            endCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear; //+1 because months start with 0.
                            pickEndDateButton.setText("End Date: " + selectedDate); //set the text of the button with the selected date so the user can easily see what dat ehe picked
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
                            startCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            startCalendar.set(Calendar.MINUTE, selectedMinute);
                            String selectedTime = selectedHour + ":" + (selectedMinute < 30 ? "0" + selectedMinute : selectedMinute);
                            pickStartTimeButton.setText("Start Time: " + selectedTime); //set the text of the button with the selected time so the user can easily see what time he picked
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

                            endCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            endCalendar.set(Calendar.MINUTE, selectedMinute);
                            String selectedTime = selectedHour + ":" + (selectedMinute < 30 ? "0" + selectedMinute : selectedMinute);
                            pickEndTimeButton.setText("End time: " + selectedTime); //set the text of the button with the selected time so the user can easily see what time he picked
                        },
                        hour, minute, true);
                timePickerDialog.show(); //to show the time picker
            });


            //listener for the create button
            createEventButton.setOnClickListener(v -> {

                //get all the infos ready to send to firebase
                String finalTitle = eventTitle.getText().toString();
                String finalDescription = eventDescription.getText().toString();
                String finalEventAddress = eventLocation.getText().toString();
                String startDate = pickStartDateButton.getText().toString();
                String endDate = pickEndDateButton.getText().toString();
                String finalStartTime = pickStartTimeButton.getText().toString();
                String finalEndTime = pickEndTimeButton.getText().toString();


                // Check if user fill in all filed.
                if (finalTitle.isEmpty() || finalDescription.isEmpty() || finalEventAddress.isEmpty() ||
                        startDate.equals("Pick Start Date") || endDate.equals("Pick End Date") ||
                        finalStartTime.equals("Pick Start Time") || finalEndTime.equals("Pick End Time")) {
                    Toast.makeText(EventCreationPage.this, "You must fill all filed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (startCalendar.get(Calendar.MINUTE) != 0 && startCalendar.get(Calendar.MINUTE) != 30) {
                    Toast.makeText(EventCreationPage.this, "Start time minutes must be 0 or 30", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endCalendar.get(Calendar.MINUTE) != 0 && endCalendar.get(Calendar.MINUTE) != 30) {
                    Toast.makeText(EventCreationPage.this, "End time minutes must be 0 or 30", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar currentCalendar = Calendar.getInstance();

                // Check: Start time must be after current time
                if (startCalendar.compareTo(currentCalendar) <= 0) {
                    Toast.makeText(EventCreationPage.this, "Start time must be after current time", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check: End time cant be before the start time
                if (endCalendar.compareTo(startCalendar) <= 0) {
                    Toast.makeText(EventCreationPage.this, "End time cant be before the start time", Toast.LENGTH_SHORT).show();
                    return;
                }




            /*Log.i("Event Creation Page", "Title: " + title);
            Log.i("Event Creation Page", "Description: " + description);
            Log.i("Event Creation Page", "eventAddress: " + eventAddress);
            Log.i("Event Creation Page", "Start Date: " + startDate);
            Log.i("Event Creation Page", "End Date: " + endDate);
            Log.i("Event Creation Page", "Start Time: " + startTime);
            Log.i("Event Creation Page", "End Time: " + endTime);*/

                //to do: code for sending info gathered to firebase


                // send data to db
                DatabaseReference eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
                String eventId = eventsDatabaseReference.push().getKey();
                if (eventId != null) {
                    //sample data
                    Attendee attendee1 = new Attendee("please", "help", "456789", "please", "Attendee", "approved");
                    Attendee attendee2 = new Attendee("actually", "helpme", "456789", "please", "Attendee", "approved");
                    List<Attendee> pendingAttendeesList = new ArrayList<>();
                    List<Attendee> acceptedAttendeesList = new ArrayList<>();

                    for(int i=0; i<2; i++){
                        pendingAttendeesList.add(attendee1);
                        acceptedAttendeesList.add(attendee2);
                    }

                    Event event = new Event(finalTitle, finalDescription, finalEventAddress, startCalendar.getTime(), endCalendar.getTime(), eventId, (ArrayList<Attendee>) pendingAttendeesList, (ArrayList<Attendee>) acceptedAttendeesList);

                    eventsDatabaseReference.child(eventId).setValue(event);
                    Toast.makeText(EventCreationPage.this, "Event Created Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


        }
    }