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

    String QR_Name;
    int QR_Point;
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
                            QR_Name = result.getText();
                            try {
                                /**
                                 * 256 HASH
                                 */
                                MessageDigest md = MessageDigest.getInstance("SHA-256");
                                byte[] hash = md.digest(result.getText().getBytes(StandardCharsets.UTF_8));
                                StringBuilder string = new StringBuilder();
                                for (byte b : hash) {
                                    string.append(String.format("%02x", b));
                                }

                                /**
                                 * Number of repeats
                                 */
                                Pattern pattern = Pattern.compile("([\\da-f])\\1+");
                                Matcher matcher = pattern.matcher(string);

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
                                System.err.println("SHA-256 algorithm not found");
                            }

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

                                            if (ContextCompat.checkSelfPermission(ScanAction.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                            }
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }

                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                                            builder.setMessage("Do you want to record an image?")

                                                    /**
                                                     * Recording image
                                                     */
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            // Show a dialog allowing the user to choose between taking a photo or uploading from the gallery
                                                            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                                                            builder.setTitle("Choose an option");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    if (options[which].equals("Take a Picture")) {

                                                                    } else if (options[which].equals("Choose from Gallery")) {

                                                                    } else {
                                                                        onResume();
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    })

                                                    /**
                                                     * Not recording image
                                                     */
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // Show a dialog allowing the user to input a comment
                                                            // TODO: Add code to allow comment input
                                                        }
                                                    })
                                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            onResume();
                                                        }
                                                    });
                                            builder.create().show();
                                        }
                                    })

                                    /**
                                     * Not recording location
                                     */
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanAction.this);
                                            builder.setMessage("Do you want to record an image?")

                                                    /**
                                                     * Recording image
                                                     */
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // Show a dialog allowing the user to upload an image
                                                            // TODO: Add code to allow image upload
                                                        }
                                                    })

                                                    /**
                                                     * Not recording image
                                                     */
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // Show a dialog allowing the user to input a comment
                                                            // TODO: Add code to allow comment input
                                                        }
                                                    })
                                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            onResume();
                                                        }
                                                    });

                                            builder.create().show();
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            onResume();
                                        }
                                    });

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference qrCollection = db.collection("QR Codes");
                            Query query = qrCollection.whereEqualTo("Name", QR_Name);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot snapshot = task.getResult();
                                        if (snapshot.isEmpty()) {
                                            // QR code collection doesn't exist, create a new one
                                            Map<String, Object> qrCode = new HashMap<>();
                                            qrCode.put("Name", "GENERATE");
                                            qrCode.put("Score", QR_Point);
                                            qrCode.put("Visual", "GENERATE");
                                            qrCollection.document(QR_Name).set(qrCode);

                                            // Create a new users sub-collection
                                            CollectionReference usersCollection = qrCollection.document(QR_Name).collection("users");
                                            usersCollection.document("1234").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (!document.exists()) {
                                                            // Create a new document for user 1234
                                                            Map<String, Object> userData = new HashMap<>();
                                                            userData.put("Location", new GeoPoint(QR_Latitude, QR_Longitude));
                                                            userData.put("Comments", "");
                                                            usersCollection.document("1234").set(userData);
                                                        }
                                                    } else {
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                    }
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
                    String message = "Successfully recording location";
                    locationManager.removeUpdates(this);
                    Toast.makeText(ScanAction.this, message, Toast.LENGTH_SHORT).show();
                    // Do something with the latitude and longitude, such as saving to a database
                }
            };
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
