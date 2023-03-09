package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.Manifest;

public class ScanAction extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private LocationManager locationManager;
    private LocationListener locationListener;
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

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            String QR_Value, Point, QR_Name;
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
                            int totalScore = 0;
                            while (matcher.find()) {
                                int base = 0;
                                String repeat = matcher.group();
                                if(Character.isDigit(repeat.charAt(0))){
                                    base = repeat.charAt(0) - '0'; //get int.
                                }
                                else{
                                    base = (int)repeat.charAt(0) - 'a' + 10; //alphabet to int, for example a = 10.
                                }
                                int exponent = repeat.length() - 1; // exponent for "b" repeats
                                int score = (int) Math.pow(base, exponent); // calculate output value
                                totalScore += score;
                            }

                            Toast.makeText(ScanAction.this, Integer.toString(totalScore), Toast.LENGTH_SHORT).show();

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
                                        locationManager.removeUpdates(locationListener);

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

                                                                } else{
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
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "Latitude: " + latitude + ", Longitude: " + longitude;
                Toast.makeText(ScanAction.this, message, Toast.LENGTH_SHORT).show();
                // Do something with the latitude and longitude, such as saving to a database
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
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