package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.InMyAccountActivity;
import com.example.myapplication.MyQRActivity;
import com.example.myapplication.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;

public class FireBaseRankActivity extends AppCompatActivity {

    // Declare the adapter as a global variable
    private ArrayAdapter<String> adapter;
    public String currUsername = "None";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_rank);
        ListView rankList = findViewById(R.id.rank_list);
        Button backButton = findViewById(R.id.back_button);
        FirebaseApp.initializeApp(this);
        ArrayList<String> qrCodesList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference userCollection = db.collection("username");
        DocumentReference userDocRef = userCollection.document(currUsername);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myData");
        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                // Do something with the value
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
                    qrCodesList.add(name+"\n"+face);

                }
                adapter = new ArrayAdapter<>(FireBaseRankActivity.this, android.R.layout.simple_list_item_1,qrCodesList);
                rankList.setAdapter(adapter);
                // Do something with the ArrayList of names
                System.out.println(qrCodesList);
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });


        rankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FireBaseRankActivity.this, MyQRActivity.class);
                String wholeQRCode = (String) parent.getItemAtPosition(position);
                String QRCode = wholeQRCode.substring(0,wholeQRCode.indexOf("\n"));
                intent.putExtra("QRCode", QRCode);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FireBaseRankActivity.this, InMyRankActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        // Notify the adapter that the data has changed only if it is not null
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}

//package com.example.myapplication;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class FireBaseRankActivity extends AppCompatActivity {
//    private ArrayAdapter<String> adapter;
//    public String currUsername = "1234";
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//
//
//        ListView qrList = findViewById(R.id.qr_list);
//        FirebaseApp.initializeApp(this);
//        ArrayList<String> qrCodesList = new ArrayList<>();
//
//        FirebaseFirestore DB = FirebaseFirestore.getInstance();
//
//        CollectionReference userCollection = DB.collection("username");
//        DocumentReference userDocRef = userCollection.document("Ranking");
//
//        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
//
//        userDocRef.get(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ArrayList<String> rankingsList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String ranking = snapshot.getValue(String.class);
//                    rankingsList.add(ranking);
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, rankingsList);
//                listView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("TAG", "Failed to read value.", databaseError.toException());
//            }
//        });
//
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.firebase_rank);
//        Button backButton = findViewById(R.id.back_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FireBaseRankActivity.this, InMyRankActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
////    FirebaseDatabase DB = FirebaseDatabase.getInstance();
////    DatabaseReference ref = database.getReference("rankings");
////
////    ListView listView = findViewById(R.id.listView);
////
////    ref.addValueEventListener(new ValueEventListener() {
////        @Override
////        public void onDataChange(DataSnapshot dataSnapshot) {
////            ArrayList<String> rankingsList = new ArrayList<>();
////            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                String ranking = snapshot.getValue(String.class);
////                rankingsList.add(ranking);
////            }
////            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, rankingsList);
////            listView.setAdapter(adapter);
////        }
////
////        @Override
////        public void onCancelled(DatabaseError databaseError) {
////            Log.d("TAG", "Failed to read value.", databaseError.toException());
////        }
////    });
//}
