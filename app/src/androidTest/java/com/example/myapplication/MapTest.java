package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapTest {

    @Rule
    public ActivityScenarioRule<map> activityScenarioRule =
            new ActivityScenarioRule<>(map.class);

    @Before
    public void setUp() {
        // Grant location permission
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("pm grant " + InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName()
                        + " android.permission.ACCESS_FINE_LOCATION");
    }

    @Test
    public void testMapFragmentIsDisplayed() {
        // Launch activity
        ActivityScenario.launch(map.class);

        // Check that the map fragment is displayed
        onView(ViewMatchers.withId(R.id.google_map)).check(matches(isDisplayed()));
    }
}
