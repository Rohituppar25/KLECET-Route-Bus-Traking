package com.example.bustracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverPortalActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Spinner spinnerBusNumber, spinnerRoute;
    private Button btnLogout, btnShareLocation;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private String driverName = "Driver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_portal);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            loadDriverName();
        }

        welcomeText = findViewById(R.id.textView3);
        spinnerBusNumber = findViewById(R.id.spinnerBusNumber);
        spinnerRoute = findViewById(R.id.spinnerRoute);
        btnLogout = findViewById(R.id.button2);
        btnShareLocation = findViewById(R.id.buttonShareLocation);

        // Populate bus numbers
        String[] busNumbers = {"Bus 1", "Bus 2", "Bus 3", "Bus 4", "Bus 5", "Bus 6", "Bus 7", "Bus 8", "Bus 9", "Bus 10"};
        ArrayAdapter<String> busAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, busNumbers);
        spinnerBusNumber.setAdapter(busAdapter);

        // Populate routes
        String[] routes = {"Route Raibag", "Route Nippani", "Route Boragaov", "Route Galataga", "Route Shiraguppi", "Route Sankeshwar", "Route Sadalaga"};
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, routes);
        spinnerRoute.setAdapter(routeAdapter);

        // Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(DriverPortalActivity.this, UserDriverActivity.class);
            startActivity(i);
            finish();
        });

        // Share Location
        btnShareLocation.setOnClickListener(v -> {
            String busNumber = spinnerBusNumber.getSelectedItem().toString();
            String routeName = spinnerRoute.getSelectedItem().toString();

            Intent i = new Intent(DriverPortalActivity.this, MapsActivity3.class);
            i.putExtra("driverName", driverName);
            i.putExtra("busNumber", busNumber);
            i.putExtra("routeName", routeName);
            startActivity(i);
        });
    }

    private void loadDriverName() {
        usersRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    driverName = snapshot.getValue(String.class);
                    welcomeText.setText("Welcome, " + driverName);
                } else {
                    welcomeText.setText("Welcome, Driver");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DriverPortalActivity.this, "Error loading name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
