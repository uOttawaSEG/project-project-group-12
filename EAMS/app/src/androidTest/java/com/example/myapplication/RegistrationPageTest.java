package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class RegistrationPageTest {

    @Rule
    public ActivityScenarioRule<RegistrationPage> activityRule =  new ActivityScenarioRule<>(RegistrationPage.class);




    @Test
    public void testAttendeeFieldVerification(){
        //add all the field correctly except one, than click field registration button and verified

        onView(withId(R.id.emailAddress)).perform(
                typeText(""), closeSoftKeyboard()
        );

        //other fields...

        onView(withId(R.id.ConfirmSignUp)).perform(
                click()
        );

        //check alertbox
        onView(withText("The registration request was sent successfully")).check(doesNotExist());

        //could also check TOAST ( temporary dialog box at bottom of screen with error message but don't think we're doing that, right now when click out of invalid field it has error message attached to it in white box, red icon and black font, if i recall correctly)

    }
}
