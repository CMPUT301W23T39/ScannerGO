package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button recordButton = findViewById(R.id.Scan);
        Button accountButton = findViewById(R.id.Account);

        Button rankButton = findViewById(R.id.Rank);

        Button mapButton = findViewById(R.id.Map);
        EditText searchInput = findViewById(R.id.search_input);
        Button searchButton = findViewById(R.id.search_button);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchInput.getText().toString();

                if (!searchQuery.isEmpty()) {
                    db.collection("username")
                            .document(searchQuery)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Intent intent = new Intent(MainActivity.this,OtherUserProfile.class);
                                        intent.putExtra("username", searchQuery);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "No such user!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Tag", "Error searching for user", e);
                                    Toast.makeText(MainActivity.this, "Error searching for user", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a username to search", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanAction.class);
                startActivity(intent);
                finish();
            }

        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InMyAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InMyRankActivity.class);
                startActivity(intent);
                finish();
            }
            });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, map.class);
                startActivity(intent);
                finish();
            }
        });
    }
}