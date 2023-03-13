package com.example.myapplication;

import android.content.Intent;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FireBaseRankActivityTest {

    @Mock
    View mockView;

    @Mock
    FireBaseRankActivity mockActivity;

    @Test
    public void backButtonClickListener_startsInMyRankActivity() {
        // Given
        Mockito.when(mockActivity.getApplicationContext()).thenReturn(mockActivity);
        Mockito.when(mockActivity.getPackageName()).thenReturn("com.example.myapplication");

        Intent expectedIntent = new Intent(mockActivity, InMyRankActivity.class);

        // When
        mockActivity.findViewById(R.id.back_button).performClick();

        // Then
        Mockito.verify(mockActivity).startActivity(expectedIntent);
        Mockito.verify(mockActivity).finish();
    }
}
