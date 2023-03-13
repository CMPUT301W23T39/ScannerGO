package com.example.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.CredentialsProvider;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.model.DatabaseId;
import com.google.firebase.firestore.remote.GrpcMetadataProvider;
import com.google.firebase.firestore.util.AsyncQueue;

import org.junit.BeforeClass;
import org.junit.Test;

public class DBTest {
    static FirebaseFirestore fakeDatabase;

    @BeforeClass
    public static void setUp() {

        fakeDatabase = FirebaseFirestore.getInstance();
        DB.setDB(fakeDatabase);
    }

    @Test
    public void testUsernameIsNew() {
        // Test case where the username does not exist in the database
        assertTrue(DB.UsernameIsNew("newUser"));

        // Test case where the username already exists in the database
        assertFalse(DB.UsernameIsNew("existingUser"));
    }

    @Test
    public void testDeviceIDIsNew() {
        // Test case where the device ID does not exist in the database
        assertTrue(DB.deviceIDIsNew("newDeviceID"));

        // Test case where the device ID already exists in the database
        assertFalse(DB.deviceIDIsNew("existingDeviceID"));
    }
}
