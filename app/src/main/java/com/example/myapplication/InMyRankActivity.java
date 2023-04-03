package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.FireBaseRankActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.loginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InMyRankActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    public String curUsername = loginActivity.username1;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);
        ListView mHighScoresListView = findViewById(R.id.rank_list);

        Button backButton = findViewById(R.id.back_button);
        Button firebaseButton = findViewById(R.id.firebase_button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the high scores collection and retrieve the scores that match the current user's username
        CollectionReference playersRef = db.collection("username");
        Query playersQuery = playersRef.orderBy("totalScore", Query.Direction.DESCENDING).limit(10);
        playersQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> highScoresList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String username = documentSnapshot.getString("userNameKey");
                    int score = documentSnapshot.getLong("totalScore").intValue();
                    highScoresList.add(username + " - " + score);
                }
                adapter = new ArrayAdapter<>(InMyRankActivity.this, android.R.layout.simple_list_item_1, highScoresList);
                mHighScoresListView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InMyRankActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InMyRankActivity.this, FireBaseRankActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}