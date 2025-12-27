package com.example.bustracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DriverHomeActivity extends AppCompatActivity {

    private Spinner busSpinner;
    private Button startLocationButton;

    private String[] busNumbers = {"Bus-101", "Bus-102", "Bus-103", "Bus-104"};

    // ✅ Launcher for permission request (modern way)
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startLocationService(getSelectedBusNumber());
                } else {
                    Toast.makeText(this, "Location permission is required to start tracking.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        busSpinner = findViewById(R.id.busSpinner);
        startLocationButton = findViewById(R.id.startLocationButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, busNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busSpinner.setAdapter(adapter);

        startLocationButton.setOnClickListener(v -> {
            if (hasLocationPermission()) {
                startLocationService(getSelectedBusNumber());
            } else {
                requestLocationPermission();
            }
        });
    }

    private String getSelectedBusNumber() {
        return busSpinner.getSelectedItem().toString();
    }

    // ✅ Permission check
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    // ✅ Permission request using launcher
    private void requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    // ✅ Start foreground service
    private void startLocationService(String busNumber) {
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("busNumber", busNumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Location tracking started for " + busNumber, Toast.LENGTH_SHORT).show();
    }
}
