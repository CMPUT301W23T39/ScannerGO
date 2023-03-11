package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class loginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorMessageTextView;
    private Button signupButton;
    private Button loginbyDeviceButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        signupButton = findViewById(R.id.signupButton);
        loginbyDeviceButton = findViewById(R.id.loginByDeviceButton);

// Get a reference to the "devices" node in the database
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    errorMessageTextView.setText("Please enter valid credentials");
                }
//                } else if (username.equals("admin") && (password.equals("admin"))) {
                else {
                    // Get the Firestore collection for users
                    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("username");

// Create a query to retrieve the user document with the given username
                    Query query = usersCollection.whereEqualTo("userNameKey", username);

// Execute the query
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Check if the query returned any documents
                            if (!task.getResult().isEmpty()) {
                                // Get the first document (assuming there is only one document with the given username)
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                // Get the password field from the document
                                String passwordFromDatabase = document.getString("passwordKey");

                                // Check if the password matches
                                if (passwordFromDatabase.equals(password)) {
                                    // Password matches, so login

                                    String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                    Account account = new Account(username, password, androidId);
//                                    Map<String, Object> curUserMap = new HashMap<>();
                                    //curUserMap.put("device", androidId);
                                    Task<Void> userRef = db.collection("username").document(username)
                                            .update("device", androidId)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Code to run when the operation succeeds
                                                    Toast.makeText(getApplicationContext(), "AndroidID added successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
//                                    Map<String, Object> curidMap = new HashMap<>();
//                                    curidMap.put("device", androidId);
//                                    db.collection("device key").document(androidId)
//                                            .set(curidMap)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // Code to run when the operation succeeds
//                                                    Toast.makeText(getApplicationContext(), "AndroidID added successfully", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
                                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    // Password doesn't match, show an error message
                                    Toast.makeText(getApplicationContext(), "Wrong username or password. Please input again!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // No documents were returned, so the username doesn't exist
                                // Show an error message
                                Toast.makeText(getApplicationContext(), "Username does not exist. Please sign up first!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // An error occurred while executing the query
                            // Show an error message
                            Toast.makeText(loginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
//                } else {
//                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignup();
            }
        });
        loginbyDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("username");
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
// Create a query to retrieve the user document with the given username
                Query query = usersCollection.whereEqualTo("device", androidId);

                query.get().addOnCompleteListener(task -> { if (task.isSuccessful()) {
                    // Iterate through the documents and get the userNamesKey value for the document that matches the filter
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userNamesKey = document.getString("userNameKey");
                        // Do something with the userNamesKey value, such as print it to the console
                        System.out.println("userNamesKey: " + userNamesKey); } }
                    else { // Handle errors
                         Log.d(TAG, "Error getting documents: ", task.getException()); } });
//                DocumentReference userDoc = usersCollection.document(androidId);
//                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override public void onSuccess(DocumentSnapshot documentSnapshot) { if (documentSnapshot.exists()) { String userNamesKey = documentSnapshot.getString("userNamesKey"); // Do something with the userNamesKey value } } });

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
                            Toast.makeText(getApplicationContext(), "This device id does not exist. Please use username and password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // An error occurred while executing the query
                        // Show an error message
                        Toast.makeText(loginActivity.this, "error occurred while executing the query", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void openSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}