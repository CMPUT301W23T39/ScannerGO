package com.example.myapplication;

import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class InMyAccountActivityTest {

    @Test
    public void testButtons() {
        ActivityScenario<InMyAccountActivity> scenario = ActivityScenario.launch(InMyAccountActivity.class);
        scenario.onActivity(activity -> {
            Button profileButton = activity.findViewById(R.id.profile_button);
            assertNotNull(profileButton);
            assertEquals("Profile", profileButton.getText());

            Button QRListButton = activity.findViewById(R.id.QRList_button);
            assertNotNull(QRListButton);
            assertEquals("QR List", QRListButton.getText());

            Button backButton = activity.findViewById(R.id.back_button);
            assertNotNull(backButton);
            assertEquals("Back", backButton.getText());

            // Test clicking on the buttons
            Espresso.onView(withId(R.id.profile_button)).perform(ViewActions.click());
            Intent expectedIntent = new Intent(activity, ProfileActivity.class);
            assertEquals(expectedIntent.getComponent(), activity.getIntent().getComponent());

            Espresso.onView(withId(R.id.QRList_button)).perform(ViewActions.click());
            expectedIntent = new Intent(activity, QRListActivity.class);
            assertEquals(expectedIntent.getComponent(), activity.getIntent().getComponent());

            Espresso.onView(withId(R.id.back_button)).check(ViewAssertions.matches(isClickable()));
        });
    }
}
