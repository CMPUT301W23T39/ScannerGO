package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Location;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class map extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap googleMap;
    ArrayList<LatLng> latLngList = new ArrayList<>();  // Declare latLngList as a member variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        FirebaseApp.initializeApp(this);
        getUserLocation();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(map.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            getCurrentLocation();
        else {
            ActivityCompat.requestPermissions(map.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    // Add this method to add markers to the map
    public void getUserLocation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("QR Codes");

        qrCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot qrDoc : task.getResult()) {
                    CollectionReference usersCollection = qrDoc.getReference().collection("users");
                    usersCollection.get().addOnCompleteListener(usersTask -> {
                        if (usersTask.isSuccessful()) {
                            for (QueryDocumentSnapshot userDoc : usersTask.getResult()) {
                                GeoPoint location = userDoc.getGeoPoint("Location");
                                if (location != null) {
                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    latLngList.add(latLng);

                                    // do something with the location
                                    Log.d(TAG, "Location: " + lat + ", " + lng);
                                }
                            }
                            for (LatLng latLng : latLngList) {
                                MarkerOptions options = new MarkerOptions().position(latLng).title("QRcode is here");
                                googleMap.addMarker(options);
                            }

                        } else {
                            Log.d(TAG, "Error getting users: ", usersTask.getException());
                        }
                    });
                }
            } else {
                Log.d(TAG, "Error getting QR codes: ", task.getException());
            }
        });

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, do nothing
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map.this.googleMap = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            Log.e("latLng",latLng.toString());    //37.4220936      -122.083922
                            MarkerOptions options = new MarkerOptions().position(latLng).title("QRcode is here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                        }
                    });
                }

            }
        });
//
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44){
            if(grantResults.length>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }
}
