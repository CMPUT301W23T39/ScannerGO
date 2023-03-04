package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        setContentView(R.layout.activity_signup);

        mNameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mConfirmPasswordEditText = findViewById(R.id.password2);
        mSignUpButton = findViewById(R.id.signupBtn);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        FirebaseApp.initializeApp(this);
//        if(FirebaseApp.getApps(this).isEmpty()){
//            FirebaseApp.initializeApp(this);
//        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String password2 = mConfirmPasswordEditText.getText().toString();
//                Map<String, Object> curCityMap = new HashMap<>();
//                curCityMap.put("userNameKey", name);
//                curCityMap.put("passwordKey", password);

                if (password2.equals(password)) {
                    // Create a new account with the given details
                    Account account = new Account(name, password);

                    // Store the account details in a local database or in the cloud

                    // Start the MainActivity
                    db.collection("username")
                            .add(account)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RRG","Document successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding account", e);
                                }
                            });
                }
                else{
                    errorMessageTextView.setText("Please make sure the two passwords are identical");
                }
            }
        });
    }
}



