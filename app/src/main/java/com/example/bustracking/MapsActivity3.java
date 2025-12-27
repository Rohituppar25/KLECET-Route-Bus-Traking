package com.example.bustracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity3 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private TextView tvDriverName, tvBusDetails;
    private Button btnStopSharing, btnRecenter;

    private DatabaseReference driverLocationRef;
    private String driverId, driverName, busNumber, routeName;

    private Marker driverMarker;

    private boolean isSharing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvDriverName = findViewById(R.id.tvDriverName);
        tvBusDetails = findViewById(R.id.tvBusDetails);
        btnStopSharing = findViewById(R.id.button10);
        btnRecenter = findViewById(R.id.btnRecenter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get current driver ID
        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1️⃣ Load REAL driver name from Users -> Drivers
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child("Drivers")
                .child(driverId)
                .child("driverusername")
                .get()
                .addOnSuccessListener(snapshot -> {
                    driverName = snapshot.getValue(String.class);
                    if (driverName == null) driverName = "Unknown Driver";

                    tvDriverName.setText("Driver: " + driverName);
                })
                .addOnFailureListener(e -> {
                    driverName = "Unknown Driver";
                    tvDriverName.setText("Driver: Unknown Driver");
                });

        // 2️⃣ Get bus + route from intent
        busNumber = getIntent().getStringExtra("busNumber");
        routeName = getIntent().getStringExtra("routeName");

        if (busNumber == null) busNumber = "N/A";
        if (routeName == null) routeName = "N/A";

        tvBusDetails.setText("Bus: " + busNumber + "  |  Route: " + routeName);

        // Firebase reference for live location
        driverLocationRef = FirebaseDatabase.getInstance().getReference("drivers").child(driverId);

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Stop sharing
        btnStopSharing.setOnClickListener(v -> {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            driverLocationRef.removeValue();
            Toast.makeText(this, "Stopped sharing location", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MapsActivity3.this, DriverPortalActivity.class));
            finish();
        });

        // Recenter
        btnRecenter.setOnClickListener(v -> {
            if (driverMarker != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverMarker.getPosition(), 16f));
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // update every 5s
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (!isSharing) return;

                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (driverMarker == null) {
                        driverMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                    } else {
                        driverMarker.setPosition(latLng);
                    }

                    // Store in Firebase
                    driverLocationRef.setValue(new DriverLocation(
                            driverId,
                            driverName,
                            busNumber,
                            routeName,
                            location.getLatitude(),
                            location.getLongitude()
                    ));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Model class for Firebase
    public static class DriverLocation {
        public String driverId;
        public String driverName;
        public String busNumber;
        public String routeName;
        public double latitude;
        public double longitude;

        public DriverLocation() {} // required for Firebase

        public DriverLocation(String driverId, String driverName, String busNumber, String routeName, double latitude, double longitude) {
            this.driverId = driverId;
            this.driverName = driverName;
            this.busNumber = busNumber;
            this.routeName = routeName;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
