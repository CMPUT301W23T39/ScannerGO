package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QRListActivity extends AppCompatActivity {

    // Declare the adapter as a global variable
    private ArrayAdapter<String> adapter;

    public String currUsername = loginActivity.username1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_rank);

        Intent intent = getIntent();
        String currUsername = loginActivity.username1;
        ListView rankList = findViewById(R.id.rank_list);
        Button backButton = findViewById(R.id.back_button);
        TextView highlowCode = findViewById(R.id.highlow_code);
        FirebaseApp.initializeApp(this);
        ArrayList<String> qrCodesList = new ArrayList<>();
        ArrayList<Integer> scoresList = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");

        DocumentReference userDocRef = userCollection.document(currUsername);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("QR Codes");
        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                // Do something with the value

                // Find the lowest and highest score
                long lowestScore = Long.MAX_VALUE;
                long highestScore = Long.MIN_VALUE;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long score = snapshot.child("Score").getValue(Long.class);
                    if (score != null) {
                        if (score < lowestScore) {
                            lowestScore = score;
                        }
                        if (score > highestScore) {
                            highestScore = score;
                        }
                    }
                }

                // Do something with the lowest and highest score
                System.out.println("Lowest score: " + lowestScore);
                System.out.println("Highest score: " + highestScore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });


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
                adapter = new ArrayAdapter<>(QRListActivity.this, android.R.layout.simple_list_item_1, qrCodesList);
                rankList.setAdapter(adapter);
                // Do something with the ArrayList of names
                System.out.println(qrCodesList);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Integer score = document.getLong("Point") != null ? document.getLong("Point").intValue() : 0;
                    scoresList.add(score);
                }

                // Use built-in Java methods to find the lowest and highest score
                int lowestScore = 0;
                int highestScore = 0;
                if (!scoresList.isEmpty()) {
                    lowestScore = Collections.min(scoresList);
                    highestScore = Collections.max(scoresList);
                }
                int size = scoresList.size();
                // Do something with the lowest and highest score
                System.out.println("Lowest score: " + lowestScore);
                System.out.println("Highest score: " + highestScore);
                highlowCode.setText("Highest QRcode: " + highestScore + "            " + "Lowest QRcode:" + lowestScore
                        + "\n" + "Total: " + size);

            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });


        rankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QRListActivity.this, MyQRActivity.class);
                String wholeQRCode = (String) parent.getItemAtPosition(position);
                String QRCode = wholeQRCode.substring(0, wholeQRCode.indexOf("\n"));
                intent.putExtra("username",currUsername);
                intent.putExtra("QRCode", QRCode);
                startActivity(intent);
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, InMyAccountActivity.class);
                intent.putExtra("username",currUsername);
                startActivity(intent);
                finish();
            }
        });


    }
}

