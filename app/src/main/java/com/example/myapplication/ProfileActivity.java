package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button backButton = findViewById(R.id.back_button3);
        Button editButton = findViewById(R.id.edit_button);
        TextView emailText = findViewById(R.id.email_text);
        TextView phoneText = findViewById(R.id.phone_text);
        TextView addressText = findViewById(R.id.address_text);
        TextView userText = findViewById(R.id.username_text);
        String username = loginActivity.username1;

        userText.setText(username);

        // Listen for real-time updates in the Firestore document
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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Do you want to edit contact information?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setTitle("Contact Information");

                                LinearLayout layout = new LinearLayout(ProfileActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                final EditText emailInput = new EditText(ProfileActivity.this);
                                emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                emailInput.setHint("Email");
                                layout.addView(emailInput);

                                final EditText phoneInput = new EditText(ProfileActivity.this);
                                phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
                                phoneInput.setHint("Phone Number");
                                layout.addView(phoneInput);

                                final EditText addressInput = new EditText(ProfileActivity.this);
                                addressInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                addressInput.setHint("Address");
                                layout.addView(addressInput);

                                builder.setView(layout);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String userEmail = emailInput.getText().toString();
                                        String userPhone = phoneInput.getText().toString();
                                        String userAddress = addressInput.getText().toString();

                                        // Update the contact information in Firestore
                                        db.collection("username")
                                                .document(username)
                                                .update(
                                                        "email", userEmail,
                                                        "phone", userPhone,
                                                        "address", userAddress
                                                );
                                    }
                                });

                                builder.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, InMyAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
