package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class RankAlgorithmTest {

    @Test
    public void testDeleteItem() {
        // Create an intent with an item to delete
        Intent deleteIntent = new Intent();
        deleteIntent.putExtra("DeleteCode", "Item 2");

        // Create the activity scenario and pass the delete intent
        ActivityScenario<rank_algorithm> scenario = ActivityScenario.launch( deleteIntent);

        // Wait for the activity to be created
        scenario.onActivity(activity -> {
            // Get the list view and adapter
            ListView listView = activity.findViewById(R.id.qr_list);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();

            // Verify that the adapter contains the item to delete
            assertThat(adapter.getCount(), equalTo(3));
            assertThat(adapter.getPosition("Item 2"), equalTo(1));

            // Delete the item
            Intent intent = new Intent();
            int pos = adapter.getPosition("Item 2");
            intent.putExtra("DeleteCode", "Item 2");
            activity.onActivityReenter(RESULT_OK,  intent);

            // Verify that the item was removed from the adapter
            assertThat(adapter.getCount(), equalTo(2));
            assertThat(adapter.getPosition("Item 2"), equalTo(-1));
        });
    }
}
