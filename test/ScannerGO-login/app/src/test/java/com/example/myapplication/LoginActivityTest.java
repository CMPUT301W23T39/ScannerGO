package com.example.myapplication;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private LoginTestActivity activity;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("username");
        activity = new LoginTestActivity();
    }

    @After
    public void tearDown() {
        activity.finish();
    }

    @Test
    public void testLogin() {
        // Prepare test data
        Map<String, Object> user = new HashMap<>();
        user.put("userNameKey", "testuser");
        user.put("passwordKey", "testpassword");
        usersCollection.document("testuser").set(user);

        // Prepare test inputs
        activity.setUsernameEditText("testuser");
        activity.setPasswordEditText("testpassword");

        // Perform login
        activity.performLogin();

        // Verify that the user is logged in
        assertTrue(activity.isLoginSuccessful());

        // Verify that the Android ID is added to the user's document in the database
        AtomicBoolean androidIdAdded = new AtomicBoolean(false);
        Query query = usersCollection.whereEqualTo("userNameKey", "testuser");
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String device = documentSnapshot.getString("device");
                    if (device != null) {
                        androidIdAdded.set(true);
                    }
                }
            }
        });
        assertTrue(androidIdAdded.get());
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        // Prepare test data
        Map<String, Object> user = new HashMap<>();
        user.put("userNameKey", "testuser");
        user.put("passwordKey", "testpassword");
        usersCollection.document("testuser").set(user);

        // Prepare test inputs
        activity.setUsernameEditText("testuser");
        activity.setPasswordEditText("wrongpassword");

        // Perform login
        activity.performLogin();

        // Verify that the user is not logged in
        assertFalse(activity.isLoginSuccessful());

        // Verify that an error message is displayed
        assertEquals("Wrong username or password. Please input again!", activity.getErrorMessage());
    }

    private class LoginTestActivity extends LoginActivity {

        private String usernameEditTextValue;
        private String passwordEditTextValue;
        private boolean loginSuccessful;
        private String errorMessage;

        @Override
        public void setUsername1(String name) {
            super.setUsername1(name);
        }


        @Override
        public void onCreate(android.os.Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set the username and password EditText values
            setUsernameEditText(usernameEditTextValue);
            setPasswordEditText(passwordEditTextValue);

            // Perform the login
            View loginButton=findViewById(R.id.loginButton);
            loginButton.performClick();
        }

        private void setPasswordEditText(String passwordEditTextValue) {

        }

        public void setUsernameEditText(String testuser) {

        }

        public void performLogin() {

        }

        public String getErrorMessage() {
            return null;
        }

        public boolean isLoginSuccessful() {
            return false;
        }
    }
}
