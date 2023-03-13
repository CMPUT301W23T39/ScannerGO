package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule =
            new ActivityTestRule<>(ProfileActivity.class);

    @Test
    public void testEditButton() {
        onView(withId(R.id.edit_button)).perform(click());

        onView(withText("Do you want to edit contact information?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("Yes"))
                .inRoot(isDialog())
                .perform(click());

        onView(withText("Contact Information"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button1))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .check(matches(withText("OK")));

        onView(withId(android.R.id.button2))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .check(matches(withText("Cancel")));
    }

    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button3)).perform(click());

        //intended(hasComponent(InMyAccountActivity.class.getName()));
    }

}

