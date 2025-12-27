package com.example.bustracking;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackBusActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker busMarker;
    private DatabaseReference dbRef;
    private String driverId, busNumber;
    private TextView busInfoText;
    private FloatingActionButton mapTypeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_bus);

        driverId = getIntent().getStringExtra("driverId");
        busNumber = getIntent().getStringExtra("busNumber");

        if (driverId == null || driverId.trim().isEmpty()) {
            Toast.makeText(this, "Driver ID missing, cannot track bus.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance().getReference("drivers").child(driverId);

        busInfoText = findViewById(R.id.busInfoText);
        mapTypeBtn = findViewById(R.id.mapTypeBtn);

        busInfoText.setText("Tracking Bus: " + busNumber);

        mapTypeBtn.setOnClickListener(v -> {
            if (mMap != null) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        dbRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double lat = snapshot.child("latitude").getValue(Double.class);
                    Double lng = snapshot.child("longitude").getValue(Double.class);

                    if (lat != null && lng != null) {
                        LatLng busLocation = new LatLng(lat, lng);
                        if (busMarker != null) busMarker.remove();
                        busMarker = mMap.addMarker(new MarkerOptions()
                                .position(busLocation)
                                .title("Bus " + busNumber));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15));
                    }
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
