package com.example.myapplication;

import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class InMyRankActivityTest {

    @Rule
    public ActivityScenarioRule<InMyRankActivity> activityScenarioRule =
            new ActivityScenarioRule<>(InMyRankActivity.class);

    private ActivityScenario<InMyRankActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = activityScenarioRule.getScenario();
    }

    @Test
    public void testActivityLaunch() {
        onView(ViewMatchers.withId(R.id.back_button)).check(matches(isDisplayed()));
        //onView(ViewMatchers.withId(R.id.firebase_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButton() {
        onView(ViewMatchers.withId(R.id.back_button)).perform(click());
        Intent expectedIntent = new Intent(activityScenario.getResult().getResultData());
        expectedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        expectedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        expectedIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intended((Matcher<Intent>) expectedIntent);
    }

}
