package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class OtherUserProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);
        Button backButton = findViewById(R.id.back_button3);
        Button codeButton = findViewById(R.id.view_code);
        TextView emailText = findViewById(R.id.email_text);
        TextView phoneText = findViewById(R.id.phone_text);
        TextView addressText = findViewById(R.id.address_text);
        TextView userText = findViewById(R.id.username_text);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userText.setText(username);
        db.collection("username")
                .document(username)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            String phone = documentSnapshot.getString("phone");
                            String address = documentSnapshot.getString("address");

                            if (email != null) {
                                emailText.setText("Email: "+email);
                            }
                            if (phone != null) {
                                phoneText.setText("Phone Number: "+phone);
                            }
                            if (address != null) {
                                addressText.setText("Address: " + address);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherUserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherUserProfile.this, OtherUserQR.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}
