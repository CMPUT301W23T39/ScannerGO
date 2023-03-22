package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
//import com.google.api.core.ApiFuture;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class loginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorMessageTextView;
    private FirebaseFirestore db;

    public static String username1;
    public String getUsername1(){
        return username1;
    }
    public void setUsername1(String name){
        username1 = name;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

// Get a reference to the "devices" node in the database
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("username");
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String device = document.getString("device");
                    if (device != null) {
                        if (device.equals(androidId)) {
                            setUsername1(document.getString("userNameKey"));
                        }
                    } else { // Handle errors
                        Log.d(TAG, "android id does not exist ", task.getException());
                    }
                }
            }
        });
//                  @Override public void onSuccess(Document
        Query query = usersCollection.whereEqualTo("device", androidId);
// Execute the query
        query.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                // Check if the query returned any documents
                if (!task1.getResult().isEmpty()) {
                    // Get the first document (assuming there is only one document with the given username)
                    //DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // No documents were returned
                    // Show an error message
                    Toast.makeText(getApplicationContext(), "This device id does not exist. Please use username", Toast.LENGTH_SHORT).show();
                }
            } else {
                // An error occurred while executing the query
                // Show an error message
                Toast.makeText(loginActivity.this, "error occurred while executing the query", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                if (username.isEmpty()) {
                    errorMessageTextView.setText("Please enter valid credentials");
                } else {
                    Map<String, Object> curUserMap = new HashMap<>();
                    db.collection("username");
                    CollectionReference usersCollection = db.collection("username");

                    Query query = usersCollection.whereEqualTo("userNameKey", username);
                    query.get().addOnCompleteListener(task -> {
                        if (!task.getResult().isEmpty()) {
                            // Check if the query returned any documents
                            Toast.makeText(getApplicationContext(), "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                        } else {
                            curUserMap.put("userNameKey", username);
                            // Create a new account with the given details
                            // Store the account details in a local database or in the cloud ekxww9yWbT19GDfhUQLX
                            db.collection("username").document(username)
                                    .set(curUserMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Code to run when the operation succeeds
                                            Log.d("RRG", "Document successfully written!");
                                            Toast.makeText(getApplicationContext(), "User information added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Code to run when the operation fails
                                            Log.d("RRG", "Error adding account");
                                            Toast.makeText(getApplicationContext(), "Failed to add user information", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }
        });
    }
}