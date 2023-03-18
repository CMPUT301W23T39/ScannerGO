package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.LatLng;

import org.w3c.dom.Text;

public class MyQRActivity extends AppCompatActivity {
    String sameu = "Usernames with same QR Code: ";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myqr);
        TextView QRCodeName = findViewById(R.id.qrcode_name);

        TextView CommentText = findViewById(R.id.comment_text);
        TextView LocationText = findViewById(R.id.location_text);
        TextView ScoreText = findViewById(R.id.score);
        TextView SameUsersText = findViewById(R.id.sameUsers);
        Button backButton = findViewById(R.id.back_button2);
        Button deleteButton = findViewById(R.id.delete_button);
        Intent intent = getIntent();
        String QRCode = intent.getStringExtra("QRCode");
        String Hash = intent.getStringExtra("Hash");
        QRCodeName.setText("Name: "+QRCode);


        String username = loginActivity.username1;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");
        DocumentReference userDocRef = userCollection.document(username);
        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
// Get the document with ID "some username" from the "username" collection

        qrCodesCollection.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (QRCode.equals(document.getString("Name"))) {
                                String comment = document.getString("Comment");
                                CommentText.setText("Comment: " + comment);
                                Long score = document.getLong("Point");
                                ScoreText.setText("Score: " + score);
                                GeoPoint location = document.getGeoPoint("Location");
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                String loc = lat + ", " + lng;
                                LocationText.setText("Location: " + loc);
                            }
                        }
                    }
                });
        CollectionReference qrCollection = db.collection("QR Codes");
        DocumentReference qrDocRef = qrCollection.document(Hash);
        CollectionReference SameUserCollection = qrDocRef.collection("users");

        SameUserCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    if (!username.equals(document.getString("ID"))) {
                        sameu += "\n"+document.getString("ID");
                        SameUsersText.setText(sameu);
                    }
                }}});

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRActivity.this, FireBaseRankActivity.class);
                startActivity(intent);
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodesCollection.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("Name");
                            if (name.equals(QRCode)) {
                                document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        Intent intent = new Intent(MyQRActivity.this, FireBaseRankActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                            }
                        }
                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });
            }
        });

    }
}
