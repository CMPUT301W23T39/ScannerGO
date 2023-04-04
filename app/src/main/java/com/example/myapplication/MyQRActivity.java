package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyQRActivity extends AppCompatActivity {
    String Hash;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_qr);
        TextView QRCodeName = findViewById(R.id.qrcode_name);

        TextView CommentText = findViewById(R.id.comment_text);
        TextView LocationText = findViewById(R.id.location_text);
        TextView ScoreText = findViewById(R.id.score);
        Button backButton = findViewById(R.id.back_button2);
        Intent intent = getIntent();
        String QRCode = intent.getStringExtra("QRCode");
        QRCodeName.setText("Name: "+QRCode);

        ImageView userImage = findViewById(R.id.loc_image);
        String username = intent.getStringExtra("username");
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
                        Hash = document.getString("HASH");
                        loadImage(username,userImage);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRActivity.this, QRListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

    }
    private void loadImage(String username, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference userImageRef = storageReference.child("Users/" + username + "/"+ Hash+"/");

        userImageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // Find the last .jpg file
                StorageReference lastJpgRef = getLastJpgFile(listResult.getItems());

                if (lastJpgRef != null) {
                    lastJpgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Use Picasso to load the image into the ImageView
                            Picasso.get().load(uri).into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("MyQRActivity", "Failed to load image from Firebase Storage", e);
                        }
                    });
                } else {
                    Log.e("MyQRActivity", "No .jpg file found in the folder");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MyQRActivity", "Failed to list files in the folder", e);
            }
        });
    }
    private StorageReference getLastJpgFile(List<StorageReference> files) {
        StorageReference lastJpgRef = null;
        for (StorageReference fileRef : files) {
            String name = fileRef.getName();
            if (name.toLowerCase().endsWith(".jpg")) {
                if (lastJpgRef == null || name.compareTo(lastJpgRef.getName()) > 0) {
                    lastJpgRef = fileRef;
                }
            }
        }
        return lastJpgRef;
    }
}
