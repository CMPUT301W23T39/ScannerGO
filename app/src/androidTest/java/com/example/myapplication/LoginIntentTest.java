package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import com.example.myapplication.R;
import com.example.myapplication.loginActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class LoginIntentTest {

    private String mStringToBetyped;

    @Rule
    public IntentsTestRule<loginActivity> mActivityRule = new IntentsTestRule<>(
            loginActivity.class);

    @Before
    public void initValidString() {
        mStringToBetyped = "12";
    }

    @Test
    public void testLogin() {
        // Type text and then press the login button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText(mStringToBetyped));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("ddd"));
        onView(withId(R.id.loginButton)).perform(click());

        // Check that the MainActivity has been launched
        intending(allOf(hasAction(Intent.ACTION_MAIN), toPackage("com.example.myapplication")));

    }
}
