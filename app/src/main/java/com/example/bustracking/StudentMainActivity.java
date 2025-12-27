package com.example.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentMainActivity extends AppCompatActivity {

    ListView listView;
    EditText searchBar;
    BusListAdapter adapter;
    ArrayList<BusModel> busList = new ArrayList<>();
    ArrayList<BusModel> filteredList = new ArrayList<>();
    DatabaseReference dbRef;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        listView = findViewById(R.id.busListView);
        searchBar = findViewById(R.id.searchBar);
        bottomNav = findViewById(R.id.bottomNav);

        dbRef = FirebaseDatabase.getInstance().getReference("Buses");

        adapter = new BusListAdapter();
        listView.setAdapter(adapter);

        loadBusData();

        searchBar.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                filterList(cs.toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void afterTextChanged(Editable arg0) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home); // Highlight home icon

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true; // Already on home

            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(StudentMainActivity.this, StudentFeedbackActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(StudentMainActivity.this, ViewNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(StudentMainActivity.this, StudentProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

    }

    private void filterList(String query) {
        filteredList.clear();
        for (BusModel bus : busList) {
            if (bus.getBusNumber().toLowerCase().contains(query.toLowerCase()) ||
                    bus.getDriverName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(bus);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadBusData() {

        DatabaseReference driversLocationRef = FirebaseDatabase.getInstance().getReference("drivers");
        DatabaseReference driverInfoRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers");

        driversLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot locationSnapshot) {
                busList.clear();

                for (DataSnapshot busSnap : locationSnapshot.getChildren()) {

                    String driverId = busSnap.getKey();

                    String busNumber = busSnap.child("busNumber").getValue(String.class);
                    String routeName = busSnap.child("routeName").getValue(String.class);
                    Double latitude = busSnap.child("latitude").getValue(Double.class);
                    Double longitude = busSnap.child("longitude").getValue(Double.class);

                    // Now get the REAL driver name from Users/Drivers
                    driverInfoRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            String driverName = userSnap.child("driverusername").getValue(String.class);

                            if (driverName == null) driverName = "Unknown Driver";

                            // Add item to list
                            busList.add(new BusModel(
                                    driverName,
                                    busNumber,
                                    routeName,
                                    driverId,
                                    latitude,
                                    longitude
                            ));

                            filteredList.clear();
                            filteredList.addAll(busList);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    class BusListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(StudentMainActivity.this)
                        .inflate(R.layout.bus_list_item, parent, false);
            }

            TextView txtBusNumber = convertView.findViewById(R.id.txtBusNumber);
            TextView txtDriverName = convertView.findViewById(R.id.txtDriverName);
            Button btnTrack = convertView.findViewById(R.id.btnTrack);

            BusModel bus = filteredList.get(position);

            txtBusNumber.setText("Bus No: " + bus.getBusNumber());
            txtDriverName.setText("Driver: " + bus.getDriverName());

            btnTrack.setOnClickListener(v -> {
                Intent intent = new Intent(StudentMainActivity.this, TrackBusActivity.class);
                intent.putExtra("driverId", bus.getDriverId()); // Pass the correct driverId
                intent.putExtra("busNumber", bus.getBusNumber()); // Match TrackBusActivity variable name
                startActivity(intent);
            });


            return convertView;
        }
    }
}
