package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView errorMessageTextView;

    private Button mSignUpButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mNameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mConfirmPasswordEditText = findViewById(R.id.password2);
        mSignUpButton = findViewById(R.id.signupBtn);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        FirebaseApp.initializeApp(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String password2 = mConfirmPasswordEditText.getText().toString();
                Map<String, Object> curUserMap = new HashMap<>();
                db.collection("username");
                CollectionReference usersCollection = db.collection("username");

                Query query = usersCollection.whereEqualTo("userNameKey", name);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if the query returned any documents
                        Toast.makeText(getApplicationContext(), "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password2.equals(password)) {
                            curUserMap.put("userNameKey", name);
                            curUserMap.put("passwordKey", password);
                            // Create a new account with the given details

                            // Store the account details in a local database or in the cloud ekxww9yWbT19GDfhUQLX
                            db.collection("username").document(name)
                                    .set(curUserMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Code to run when the operation succeeds
                                            Log.d("RRG", "Document successfully written!");
                                            Toast.makeText(getApplicationContext(), "User information added successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, loginActivity.class);
                                            startActivity(intent);
                                            finish();
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
                        } else {
                            errorMessageTextView.setText("Please make sure the two passwords are identical");
                        }
                    }
                });
            }
        });
    }
}



