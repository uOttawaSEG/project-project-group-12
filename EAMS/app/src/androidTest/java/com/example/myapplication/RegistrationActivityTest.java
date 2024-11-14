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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    @Rule
    public ActivityScenarioRule<RegistrationPage> activityRule = new ActivityScenarioRule<>(RegistrationPage.class);

    @Test
    public void testLastNameValidationErrorMessage() {
        // Type an invalid last name (less than 2 characters)
        Espresso.onView(withId(R.id.firstName)).perform(ViewActions.typeText("a"), ViewActions.closeSoftKeyboard());

        // Trigger validation (e.g., by clicking a button or submitting the form)
        Espresso.onView(withId(R.id.ConfirmSignUp)).perform(ViewActions.click());

        // Check that the error message "Last name must be at least 2 characters" is displayed on the EditText
        Espresso.onView(withId(R.id.firstName))
                .check(matches(ViewMatchers.hasErrorText("First name must be at least 2 characters")));
    }
}

