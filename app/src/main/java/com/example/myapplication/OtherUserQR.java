package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class OtherUserQR extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_qr_list);

        ListView rankList = findViewById(R.id.rank_list);
        Button backButton = findViewById(R.id.back_button);
        FirebaseApp.initializeApp(this);
        ArrayList<String> qrCodesList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");
        Intent intent = getIntent();
        String currUsername = intent.getStringExtra("username");

        DocumentReference userDocRef = userCollection.document(currUsername);
        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
        qrCodesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve the "Name" field of each document in the subcollection
                    String name = document.getString("Name");
                    String visual = document.getString("Visual");
                    StringBuilder faceBuilder = new StringBuilder();
                    char hands = visual.charAt(4);
                    char ears = visual.charAt(1);
                    char eyes = visual.charAt(0);
                    char eyebrows = visual.charAt(2);
                    char mouth = visual.charAt(3);
                    char flower = visual.charAt(5);
                    faceBuilder.append(hands).append(ears).append('(').append(eyebrows).append(eyes).append(mouth).append(eyes).append(eyebrows).append(')').append(flower).append(ears).append(hands);
                    String face = faceBuilder.toString();
                    qrCodesList.add(name + "\n" + face);

                }
                adapter = new ArrayAdapter<>(OtherUserQR.this, android.R.layout.simple_list_item_1, qrCodesList);
                rankList.setAdapter(adapter);
                // Do something with the ArrayList of names
                System.out.println(qrCodesList);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherUserQR.this, OtherUserProfile.class);
                startActivity(intent);
            }
        });

    }
}
