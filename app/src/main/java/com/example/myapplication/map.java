package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.Manifest;

import com.example.myapplication.Utils.CountDistanceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.MapMaker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kotlin.collections.MapsKt;


public class map extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap googleMap;
    private EditText search_input_player;
    private Button back, search;
    ArrayList<LatLng> latLngList = new ArrayList<>();  // Declare latLngList as a member variable
    ArrayList<LatLng> latLngList2;  // Declare latLngList as a member variable
    List<String> ids;  // Declare latLngList as a member variable


    private Button button5,button10,button20;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        back = findViewById(R.id.backFromMap);
        button5 = findViewById(R.id.button5);
        button10 = findViewById(R.id.button10);
        button20 = findViewById(R.id.button20);
        search_input_player = findViewById(R.id.search_input_player);
        search = findViewById(R.id.search);
        FirebaseApp.initializeApp(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(map.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
           // getUserLocation();
        } else {
            ActivityCompat.requestPermissions(map.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        getUserLocation();
        getDistanceUserLocation2();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(map.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //去搜索
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(search_input_player.getText().toString().trim())) {
                    //    Log.e("Location1111111", search_input_player.getText().toString());
                    Toast.makeText(map.this, "Enter the username you want to search for", Toast.LENGTH_SHORT).show();
                } else {
                    getSearchUserLocation(search_input_player.getText().toString().trim());
                }
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDistanceUserLocation(5000);
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDistanceUserLocation(10000);
            }
        });
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDistanceUserLocation(20000);
            }
        });



    }

    private void getDistanceUserLocation(double distance){
        ids = new ArrayList<>();

        final boolean[] ifFrist = {false};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("username");

        qrCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot qrDoc : task.getResult()) {

                    Map<String, Object> user = qrDoc.getData();

                    String userKey = (String) user.get("userNameKey");

                    if (userKey != null) {
                        ids.add(userKey);
                    }

                }


                Log.e("distance",ids.toString());
                if (ids != null && ids.size() != 0) {
                    for (String oldUserName : ids) {
                        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                        CollectionReference userCollection = db2.collection("username");
                        DocumentReference userDocRef = userCollection.document(oldUserName);
                        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
                        qrCodesCollection.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    GeoPoint location = document.getGeoPoint("Location");
                                    double distance2= CountDistanceUtil.distance(currentLongitude,currentLatitude, location.getLongitude(),location.getLatitude());
                                    if(distance2<distance){

                                        Log.e("distance",distance2+"---"+distance);
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                                MarkerOptions options = new MarkerOptions().position(latLng).title("QR Codes within"+ distance +"meters are here");
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                                Marker marker=googleMap.addMarker(options);
                                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red_qrcode)));
                                                marker.setTitle("QR Codes within"+ distance +"meters are here");
                                                marker.showInfoWindow();
                                                if (!ifFrist[0]) {
                                                    ifFrist[0] = true;
                                                    Toast.makeText(map.this, "The QR code logo is the QR Codes within the specified"+distance +"meter", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
//
                                }
                            } else {
                                System.out.println("Error getting documents: " + task2.getException());
                            }
                        });

                    }
                }

            } else {
                Log.d(TAG, "Error getting QR codes: ", task.getException());
            }
        });
    }
    //重置
    private void getDistanceUserLocation2(){
        ids = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("username");

        qrCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot qrDoc : task.getResult()) {

                    Map<String, Object> user = qrDoc.getData();

                    String userKey = (String) user.get("userNameKey");

                    if (userKey != null) {
                        ids.add(userKey);
                    }

                }

                Log.e("distance",ids.toString());
                if (ids != null && ids.size() != 0) {
                    for (String oldUserName : ids) {
                        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                        CollectionReference userCollection = db2.collection("username");
                        DocumentReference userDocRef = userCollection.document(oldUserName);
                        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
                        qrCodesCollection.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    GeoPoint location = document.getGeoPoint("Location");
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                                MarkerOptions options = new MarkerOptions().position(latLng).title("QRcode is here");
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                                Marker marker=googleMap.addMarker(options);
                                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.black_qrcode)));
                                                marker.setTitle("QRcode is here");
                                                marker.showInfoWindow();
                                            }
                                        });
//                                    }

                                }
                            } else {
                                System.out.println("Error getting documents: " + task2.getException());
                            }
                        });

                    }
                }

            } else {
                Log.d(TAG, "Error getting QR codes: ", task.getException());
            }
        });
    }
    public void getSearchUserLocation(String username1) {
        ids = new ArrayList<>();
        latLngList2 = new ArrayList<>();
        final boolean[] ifFrist = {false};
        String oldUserName = username1;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCollection = db.collection("username");

        qrCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot qrDoc : task.getResult()) {
                    Map<String, Object> user = qrDoc.getData();

                    String userKey = (String) user.get("userNameKey");
                     Log.e("Location222", "user--" + userKey);
                    if (userKey != null) {
                        ids.add(userKey);
                    }

                }


                if (ids != null && ids.size() != 0) {
                    Log.e("Location2223333", "location--" + ids);
                    if (!ids.contains(oldUserName)) {
                        search_input_player.setText(oldUserName);
                        Toast.makeText(this, "The user name does not exist or you are not logged in.", Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                        CollectionReference userCollection = db2.collection("username");
                        DocumentReference userDocRef = userCollection.document(oldUserName);
                        CollectionReference qrCodesCollection = userDocRef.collection("QR Codes");
                        qrCodesCollection.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                if(task2.getResult().size()==0){
                                    search_input_player.setText("");
                                    Toast.makeText(map.this, "Users don't have this QR Codes, Unable to get location", Toast.LENGTH_LONG).show();
                                }
                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    GeoPoint location = document.getGeoPoint("Location");
                                    Log.e("Location2223333", "err--" + location);

                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(@NonNull GoogleMap googleMap) {
                                            MarkerOptions options = new MarkerOptions().position(latLng).title("The search user" +oldUserName+"all QR Codes is here");
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                            Marker marker=googleMap.addMarker(options);
                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red_qrcode)));
                                            marker.setTitle("The search user" +oldUserName+"all QR Codes is here");
                                            marker.showInfoWindow();
                                            search_input_player.setText("");
                                            if(!ifFrist[0]){
                                                ifFrist[0] =true;
                                                Toast.makeText(map.this, "The search found the red identifier is the user's QR Codes address", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                System.out.println("Error getting documents: " + task2.getException());
                            }
                        });
                    }
                }
                //搜索的是否在里面

            } else {
                Log.d(TAG, "Error getting QR codes: ", task.getException());
            }
        });


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
                                //   Log.e("Location1111111", userDoc.getData().toString());
                                GeoPoint location = userDoc.getGeoPoint("Location");
                                if (location != null) {
                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    LatLng latLng = new LatLng(lat, lng);
                                    latLngList.add(latLng);

                                    // do something with the location
                                    Log.d("wwrwerewre", "Location: " + lat + ", " + lng);
                                }
                            }

                            // Move this loop to inside the onMapReady callback
                            // to add markers to the map
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    for (LatLng latLng : latLngList) {
                                        MarkerOptions options = new MarkerOptions().position(latLng).title("QRcode is here");
                                 //       googleMap.addMarker(options).setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.designatedspot)));
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                        Marker marker=googleMap.addMarker(options);
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.black_qrcode)));
                                        marker.setTitle("QRcode is here");
                                        marker.showInfoWindow();

//                                        marker.showInfoWindow();
                                    }
                                }
                            });

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
private double currentLatitude,currentLongitude;

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
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map.this.googleMap = googleMap;
                            currentLatitude=location.getLatitude();
                            currentLongitude=location.getLongitude();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.e("latLng", latLng.toString());    //37.4220936      -122.083922
                            MarkerOptions options = new MarkerOptions().position(latLng).title("you are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                           Marker marker= googleMap.addMarker(options);
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.currentlocation_icon)));
                            marker.showInfoWindow();
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

        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
                getUserLocation();
                getDistanceUserLocation2();
            }
        }
    }
}
