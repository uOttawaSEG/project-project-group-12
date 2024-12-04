package com.example.myapplication;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    @Rule
    public ActivityScenarioRule<RegistrationPage> activityRule = new ActivityScenarioRule<>(RegistrationPage.class);

    @Test
    public void testFirstNameValidationErrorMessage() {
        Espresso.onView(withId(R.id.firstName)).perform(ViewActions.typeText("a"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.firstName))
                .check(matches(ViewMatchers.hasErrorText("First name must be at least 2 characters")));
    }

    @Test
    public void testLastNameValidationErrorMessage() {
        Espresso.onView(withId(R.id.lastName)).perform(ViewActions.typeText("b"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.lastName))
                .check(matches(ViewMatchers.hasErrorText("Last name must be at least 2 characters")));
    }

    @Test
    public void testEmailValidationErrorMessage() {
        Espresso.onView(withId(R.id.emailAddress)).perform(ViewActions.typeText("invalidemail"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.emailAddress))
                .check(matches(ViewMatchers.hasErrorText("Valid email is required")));
    }

    @Test
    public void testPasswordValidationErrorMessage() {
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("short"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.password))
                .check(matches(ViewMatchers.hasErrorText("Password must be at least 8 characters long and contain a letter or number")));
    }

    @Test
    public void testPasswordMismatchErrorMessage() {
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.confirmPassword)).perform(ViewActions.typeText("password321"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.confirmPassword))
                .check(matches(ViewMatchers.hasErrorText("Two passwords are not the same")));
    }

    @Test
    public void testPhoneNumberValidationErrorMessage() {
        Espresso.onView(withId(R.id.PhoneNumber)).perform(ViewActions.typeText("invalidPhone"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.PhoneNumber))
                .check(matches(ViewMatchers.hasErrorText("Please enter numbers")));
    }

    @Test
    public void testAddressValidationErrorMessage() {
        Espresso.onView(withId(R.id.Address)).perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.Address))
                .check(matches(ViewMatchers.hasErrorText("Address is required")));
    }


    @Test
    public void testOrganizationNameValidationErrorMessage() {
        // Select Organizer and leave organization name empty
        Espresso.onView(withId(R.id.organizerRadioButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.organizationNameField))
                .check(matches(ViewMatchers.hasErrorText("Organization name is required")));
    }
}

