package com.example.myapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.Manifest;

public class ScanAction extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseFirestore db;

    StringBuilder HASH = new StringBuilder();
    int QR_Point;
    String QR_Names,QR_Visual,QR_Comment;
    double QR_Latitude, QR_Longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        /**
         * back button
         */
        Button back = findViewById(R.id.scan_back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ScanAction.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        /**
         * Ask permission
         */
        if (ContextCompat.checkSelfPermission(ScanAction.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanAction.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPause();

                            try {
                                /**
                                 * 256 HASH
                                 */
                                MessageDigest md = MessageDigest.getInstance("SHA-256");
                                byte[] hash = md.digest(result.getText().getBytes(StandardCharsets.UTF_8));
                                for (byte b : hash) {
                                    HASH.append(String.format("%02x", b));
                                }

                                /**
                                 * Number of repeats
                                 */
                                Pattern pattern = Pattern.compile("([\\da-f])\\1+");
                                Matcher matcher = pattern.matcher(HASH);

                                /**
                                 * Get the score
                                 */
                                while (matcher.find()) {
                                    int base = 0;
                                    String repeat = matcher.group();
                                    if (Character.isDigit(repeat.charAt(0))) {
                                        base = repeat.charAt(0) - '0'; //get int.
                                    } else {
                                        base = (int) repeat.charAt(0) - 'a' + 10; //alphabet to int, for example a = 10.
                                    }
                                    int exponent = repeat.length() - 1; // exponent for "b" repeats
                                    int score = (int) Math.pow(base, exponent); // calculate output value
                                    QR_Point += score;
                                }


                            } catch (NoSuchAlgorithmException e) {
                                String message = "SHA-256 algorithm not found";
                                Toast.makeText(ScanAction.this, message, Toast.LENGTH_SHORT).show();
                            }

                            String hashPrefix = HASH.substring(0, 6);
                            Name_System(hashPrefix);
                            Visual_System(hashPrefix);

                            UpdateToUsers();
                            InitializeFireBase();

                            /**
                             * Pop up screens to ask user whether want to record location/image and leave comment
                             */
                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                            builder.setMessage("Do you want to record the location?")

                                    /**
                                     * Recording location
                                     */
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //record location
                                            String message1 = "Waiting for recording...";
                                            Toast.makeText(ScanAction.this, message1, Toast.LENGTH_SHORT).show();
                                            if (ContextCompat.checkSelfPermission(ScanAction.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                            }
                                        }
                                    })

                                    /**
                                     * Not recording location
                                     */
                                    .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                                            builder.setMessage("Do you want to record an image?")

                                                    /**
                                                     * Not recording location and Recording image
                                                     */
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent intent = new Intent(ScanAction.this, Record_image.class);
                                                            startActivity(intent);
                                                        }
                                                    })

                                                    /**
                                                     * Not recording location and Not recording image
                                                     */
                                                    .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Comment();
                                                        }
                                                    });
                                            builder.create().show();
                                        }
                                    });
                            builder.create().show();
                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    QR_Latitude = location.getLatitude();
                    QR_Longitude = location.getLongitude();
                    locationManager.removeUpdates(this);

                    User_UpdateGeolocation();
                    UpdateGeolocation();

                    String message2 = "Successfully recording location";
                    Toast.makeText(ScanAction.this, message2, Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                    builder.setMessage("Do you want to record an image?")
                            /**
                             * Recording location and Recording image
                             */
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ScanAction.this, Record_image.class);
                                    startActivity(intent);
                                }
                            })

                            /**
                             * Recording Location and Not recording image
                             */
                            .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Comment();
                                }
                            });
                    builder.create().show();
                }
            };
        }


    //Initialize QR Code FireBase
    public void  InitializeFireBase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("QR Codes");
        Query query = qrCollection.whereEqualTo("Name", String.valueOf(HASH));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot.isEmpty()) {
                        // QR code collection doesn't exist, create a new one
                        Map<String, Object> qrCode = new HashMap<>();
                        qrCode.put("Name", QR_Names);
                        qrCode.put("Score", QR_Point);
                        qrCollection.document(String.valueOf(HASH)).set(qrCode);

                        // Create a new users sub-collection
                        CollectionReference usersCollection = qrCollection.document(String.valueOf(HASH)).collection("users");
                        usersCollection.document("1234").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (!document.exists()) {
                                        // Create a new document for user 1234
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("Location", new GeoPoint(QR_Latitude, QR_Longitude));
                                        userData.put("Comment", "");
                                        usersCollection.document("1234").set(userData);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    public void  UpdateComment(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("QR Codes");
        DocumentReference qrHashDoc = qrCollection.document(String.valueOf(HASH));
        CollectionReference usersCollection = qrHashDoc.collection("users");
        DocumentReference user = usersCollection.document("1234");
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.update("Comment", QR_Comment);
            }
        });
    }

    public void  UpdateGeolocation(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("QR Codes");
        DocumentReference qrHashDoc = qrCollection.document(String.valueOf(HASH));
        CollectionReference usersCollection = qrHashDoc.collection("users");
        DocumentReference user = usersCollection.document("1234");
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.update("Location", new GeoPoint(QR_Latitude, QR_Longitude));
            }
        });
    }

    public void  UpdateToUsers(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");
        DocumentReference document = userCollection.document("1234");
        CollectionReference qrCollection = document.collection("QR Codes");
        DocumentReference HashDoc = qrCollection.document(String.valueOf(HASH));
        GeoPoint location = new GeoPoint(QR_Latitude, QR_Longitude);
        Map<String, Object> data = new HashMap<>();
        data.put("Name", QR_Names);
        data.put("Point", QR_Point);
        data.put("Comment", QR_Comment);
        data.put("Location", location);
        data.put("Visual", QR_Visual);
        HashDoc.set(data);
    }

    public void  User_UpdateGeolocation(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");
        DocumentReference document = userCollection.document("1234");
        CollectionReference qrCollection = document.collection("QR Codes");
        DocumentReference HashDoc = qrCollection.document(String.valueOf(HASH));
        HashDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashDoc.update("Location", new GeoPoint(QR_Latitude, QR_Longitude));
            }
        });
    }

    public void  User_UpdateComment(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("username");
        DocumentReference document = userCollection.document("1234");
        CollectionReference qrCollection = document.collection("QR Codes");
        DocumentReference HashDoc = qrCollection.document(String.valueOf(HASH));
        HashDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashDoc.update("Comment", QR_Comment);
            }
        });
    }

    public void Comment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
        builder.setTitle("Enter comments");
        builder.setMessage("Please enter your comments:");
        final EditText input = new EditText(ScanAction.this);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QR_Comment = input.getText().toString();
                UpdateComment();
                User_UpdateComment();
                String message = "QR Code has been saved";
                Toast.makeText(ScanAction.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScanAction.this, MainActivity.class);
                startActivity(intent);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = "QR Code has been saved";
                Toast.makeText(ScanAction.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScanAction.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

    public void Name_System(String str){
        String[] name1 = {"Cool ", "Hot "};
        String[] name2 = {"Fro", "Glo"};
        String[] name3 = {"Mo", "Lo"};
        String[] name4= {"Mega", "Ultra"};
        String[] name5 = {"Spectral", "Sonic"};
        String[] name6 = {"Shark", "Crab"};

        for (int i = 0; i < 6 && i < str.length(); i++) {
            String[] currentArray;
            char currentChar = str.charAt(i);

            if (Character.isLetter(currentChar)) {
                currentArray = i == 0 ? name1 : i == 1 ? name2 : i == 2 ? name3 : i == 3 ? name4 : i == 4 ? name5 : name6;
                QR_Names += currentArray[0];
            } else if (Character.isDigit(currentChar)) {
                currentArray = i == 0 ? name1 : i == 1 ? name2 : i == 2 ? name3 : i == 3 ? name4 : i == 4 ? name5 : name6;
                QR_Names += currentArray[1];
            }
        }
        QR_Names = QR_Names.substring(4);
    }

    public void Visual_System(String str){
        String[] name1 = {"^", "0"};
        String[] name2 = {"3", "@"};
        String[] name3 = {"-", "~"};
        String[] name4 = {"v", "d"};
        String[] name5 = {"/", "-"};
        String[] name6 = {"*", " "};

        for (int i = 0; i < 6 && i < str.length(); i++) {
            String[] currentArray;
            char currentChar = str.charAt(i);

            if (Character.isLetter(currentChar)) {
                currentArray = i == 0 ? name1 : i == 1 ? name2 : i == 2 ? name3 : i == 3 ? name4 : i == 4 ? name5 : name6;
                QR_Visual += currentArray[0];
            } else if (Character.isDigit(currentChar)) {
                currentArray = i == 0 ? name1 : i == 1 ? name2 : i == 2 ? name3 : i == 3 ? name4 : i == 4 ? name5 : name6;
                QR_Visual += currentArray[1];
            }
        }
        QR_Visual = QR_Visual.substring(4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
