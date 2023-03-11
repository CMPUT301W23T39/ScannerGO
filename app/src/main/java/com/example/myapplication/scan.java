package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class scan extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        Button back = findViewById(R.id.scan_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(scan.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPause();
                        //256 hash
                        try {
                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                            byte[] hash = md.digest(result.getText().getBytes());
                            StringBuilder sb = new StringBuilder();
                            for (byte b : hash) {
                                sb.append(String.format("%02x", b));
                            }
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("SHA-256 algorithm not found");
                        }

                        //pop up
                        AlertDialog.Builder builder = new AlertDialog.Builder(scan.this);
                        builder.setMessage("Do you want to record the location?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Show another dialog asking if the user wants to record an image
                                        AlertDialog.Builder builder = new AlertDialog.Builder(scan.this);
                                        builder.setMessage("Do you want to record an image?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        // Show a dialog allowing the user to choose between taking a photo or uploading from the gallery
                                                        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(scan.this);
                                                        builder.setTitle("Choose an option");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (options[which].equals("Take Photo")) {

                                                                } else if (options[which].equals("Choose from Gallery")) {

                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Show a dialog allowing the user to input a comment
                                                        // TODO: Add code to allow comment input
                                                    }
                                                });
                                        builder.setCancelable(true);
                                        builder.create().show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(scan.this);
                                        builder.setMessage("Do you want to record an image?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Show a dialog allowing the user to upload an image
                                                        // TODO: Add code to allow image upload
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Show a dialog allowing the user to input a comment
                                                        // TODO: Add code to allow comment input
                                                    }
                                                });
                                        builder.setCancelable(true);
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