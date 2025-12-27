package com.example.bustracking;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DriversListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DriversAdapter adapter;
    private List<DriverModel> drivers = new ArrayList<>();
    private DatabaseReference dbRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_list);
        rv = findViewById(R.id.rvDrivers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DriversAdapter(drivers);
        rv.setAdapter(adapter);
        dbRoot = FirebaseDatabase.getInstance().getReference();
        loadDrivers();
    }

    private void loadDrivers() {
        dbRoot.child("Users").child("Drivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                drivers.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    DriverModel m = s.getValue(DriverModel.class);
                    if (m != null) drivers.add(m);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static class DriverModel {
        public String driveremail;
        public String driverpassword;
        public String driverusername;
        public String route;
        public DriverModel() {}
    }

    static class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.VH> {
        List<DriverModel> list;
        DatabaseReference dbRoot;

        DriversAdapter(List<DriverModel> list) {
            this.list = list;
            this.dbRoot = FirebaseDatabase.getInstance().getReference();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_driver, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            DriverModel d = list.get(position);

            holder.tvName.setText(d.driverusername != null ? d.driverusername : "—");
            holder.tvEmail.setText("Email: " + (d.driveremail != null ? d.driveremail : "—"));
            holder.tvRoute.setText("Route: " + (d.route != null ? d.route : "—"));

            holder.btnDelete.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return; // invalid position safety check
                DriverModel currentDriver = list.get(currentPos);
                if (currentDriver.driveremail == null) return;

                new android.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Driver")
                        .setMessage("Are you sure you want to delete " + currentDriver.driverusername + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dbRoot.child("Users").child("Riders")
                                    .orderByChild("driveremail").equalTo(currentDriver.driveremail)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                ds.getRef().removeValue();
                                            }
                                            int adapterPos = holder.getAdapterPosition();
                                            if (adapterPos != RecyclerView.NO_POSITION) {
                                                list.remove(adapterPos);
                                                notifyItemRemoved(adapterPos);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvRoute, btnDelete;

            VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvDriverName);
                tvEmail = itemView.findViewById(R.id.tvDriverEmail);
                tvRoute = itemView.findViewById(R.id.tvDriverRoute);
                btnDelete = itemView.findViewById(R.id.btnDeleteDriver);
            }
        }
    }
}
