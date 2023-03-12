//package com.example.myapplication;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import static java.util.function.Predicate.not;
//
//import android.content.Intent;
//
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//
//  Instrumented test, which will execute on an Android device.
//
//  @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// 
//@RunWith(AndroidJUnit4.class)
//public class SignupActivityTest {
//
//    private ActivityScenario<SignupActivity> scenario;
//
//    @Before
//    public void setUp() {
//        // Launch the SignupActivity before each test
//        scenario = ActivityScenario.launch(SignupActivity.class);
//    }
//
//    @After
//    public void tearDown() {
//        // Close the SignupActivity after each test
//        scenario.close();
//    }
//
//    @Test
//    public void testSignupSuccess() {
//        // Enter valid user information
//        onView(withId(R.id.username)).perform(typeText("testuser"), closeSoftKeyboard());
//        onView(withId(R.id.password)).perform(typeText("testpassword"), closeSoftKeyboard());
//        onView(withId(R.id.password2)).perform(typeText("testpassword"), closeSoftKeyboard());
//
//        // Click on the Signup button
//        onView(withId(R.id.signupBtn)).perform(click());
//
//        // Verify that the SignupActivity has finished and the LoginActivity is launched
////        scenario.onActivity(activity -> {
////            assertTrue(activity.isFinishing());
////            Intent expectedIntent = new Intent(activity, loginActivity.class);
////            Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
////            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
////        });
//    }
//
//    @Test
//    public void testSignupUsernameAlreadyExists() {
//        // Add a document to the "username" collection to simulate an existing username
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> existingUser = new HashMap<>();
//        existingUser.put("userNameKey", "existinguser");
//        existingUser.put("passwordKey", "existingpassword");
//        db.collection("username").document("existinguser").set(existingUser);
//
//        // Enter an existing username and valid passwords
//        onView(withId(R.id.username)).perform(typeText("existinguser"), closeSoftKeyboard());
//        onView(withId(R.id.password)).perform(typeText("testpassword"), closeSoftKeyboard());
//        onView(withId(R.id.password2)).perform(typeText("testpassword"), closeSoftKeyboard());
//
//        // Click on the Signup button
//        onView(withId(R.id.signupBtn)).perform(click());
//    }
//
////    @Test
////    public void testSignupPasswordMismatch() {
////        // Enter a valid username and mismatching passwords
////        onView(withId(R.id.username)).perform(typeText("testuser"), closeSoftKeyboard());
////        onView(withId(R.id.password)).perform(typeText("testpassword"), closeSoftKeyboard());
////        onView(withId(R.id.password2)).perform(typeText("mismathcedpassword"), closeSoftKeyboard());
////
////        // Click on the Signup button
////        onView(withId(R.id.signupBtn)).perform(click());
////
////        // Verify that an error message is displayed
////        onView(withId(R.id.errorMessageTextView))
////                .check(matches(withText("Please make sure the two passwords are identical")));
////                }
//}
//
