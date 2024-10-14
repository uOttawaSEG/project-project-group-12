package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    private EditText editTextPassword;
    private Switch EyeSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        //Extract the login credentials
        EditText email = findViewById(R.id.editTextEmailAddress);
        EditText password = findViewById(R.id.editTextPassword);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to switch to registration
        Button button1 = findViewById(R.id.registerButton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegistrationPage.class));
            }
        });

        //Button to confirm login
        Button button2 = findViewById(R.id.LoginBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if credentials are right before proceeding
                boolean verified = checkCredentials(email, password);

                if(verified){
                    //passes on role and name of the user to welcome page
                    String userRole = determineRole();
                    Intent intent =new Intent(LoginPage.this, WelcomePage.class);
                    intent.putExtra("userRole", userRole);
                    startActivity(intent);
                }
                else{
                    //shows toast message if not credentials not verified
                    Toast.makeText(LoginPage.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Password visibility Switch
        EyeSw = findViewById(R.id.EyeSw);

        EyeSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {

                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    EyeSw.getTrackDrawable().setTint(getResources().getColor(android.R.color.holo_green_light));
                }

                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });
    }

    //Determine whether role is attendee/organize
    private String determineRole(){
        //logic to extract role
        return "Attendee";
    }

    //Checks if credentials are right
    private boolean checkCredentials(EditText email, EditText password){
        //logic to verify the credentials
        return true;
    }
}