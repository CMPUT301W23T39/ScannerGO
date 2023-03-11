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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QRListActivity extends AppCompatActivity {

    // Declare the adapter as a global variable
    private ArrayAdapter<String> adapter;
    private String currUsername = "1234";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_list);
        ListView qrList = findViewById(R.id.qr_list);
        Button backButton = findViewById(R.id.back_button4);
        FirebaseApp.initializeApp(this);
        ArrayList<String> qrCodesList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference userCollection = db.collection("username");
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
                    qrCodesList.add(name+"\n"+face);

                }
                adapter = new ArrayAdapter<>(QRListActivity.this, android.R.layout.simple_list_item_1,qrCodesList);
                qrList.setAdapter(adapter);
                // Do something with the ArrayList of names
                System.out.println(qrCodesList);
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });


        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QRListActivity.this, MyQRActivity.class);
                String QRCode = (String) parent.getItemAtPosition(position);
                intent.putExtra("QRCode", QRCode);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, InMyAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        String deleteItem = intent.getStringExtra("DeleteCode");
        if (deleteItem != null) {
            // Remove the item from the adapter and update the ListView
            adapter.remove(deleteItem);
            adapter.notifyDataSetChanged();
        }
    }
}
