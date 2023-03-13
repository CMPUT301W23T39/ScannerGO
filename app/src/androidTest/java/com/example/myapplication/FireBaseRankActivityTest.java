//package com.example.myapplication;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//
//import static org.hamcrest.CoreMatchers.allOf;
//
//import android.content.Intent;
//
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class FireBaseRankActivityTest {
//
//    @Rule
//    public ActivityScenarioRule<FireBaseRankActivity> activityScenarioRule =
//            new ActivityScenarioRule<>(FireBaseRankActivity.class);
//
//    @Test
//    public void testIntent() {
//        // Launch the activity
//        ActivityScenario<FireBaseRankActivity> scenario = activityScenarioRule.getScenario();
//
//        // Create an intent to start the MyQRActivity
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MyQRActivity.class);
//        intent.putExtra("QRCode", "My QR Code");
//    }
//}
//
