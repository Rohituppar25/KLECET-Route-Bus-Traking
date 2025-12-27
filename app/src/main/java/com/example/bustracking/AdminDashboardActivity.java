package com.example.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvStudentsCount, tvDriversCount, tvFeedbackCount;
    private LinearLayout cardStudents, cardDrivers, cardFeedbacks;
    private Button btnSendNotification, btnLogout;

    private DatabaseReference dbRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvStudentsCount = findViewById(R.id.tvStudentsCount);
        tvDriversCount = findViewById(R.id.tvDriversCount);
        tvFeedbackCount = findViewById(R.id.tvFeedbackCount);

        cardStudents = findViewById(R.id.cardStudents);
        cardDrivers = findViewById(R.id.cardDrivers);
        cardFeedbacks = findViewById(R.id.cardFeedbacks);

        btnSendNotification = findViewById(R.id.btnSendNotification);
        btnLogout = findViewById(R.id.btnLogout);

        dbRoot = FirebaseDatabase.getInstance().getReference();

        // Load counts
        loadStudentCount();
        loadDriverCount();
        loadFeedbackCount();

        // Click to open list pages
        cardStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, StudentsListActivity.class));
            }
        });

        cardDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, DriversListActivity.class));
            }
        });

        cardFeedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, FeedbackListActivity.class));
            }
        });

        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, SendNotificationActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // For now, simply go back to login
                startActivity(new Intent(AdminDashboardActivity.this, AdminLoginActivity.class));
                finish();
            }
        });
    }

    private void loadStudentCount() {
        dbRoot.child("Users").child("Students").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                tvStudentsCount.setText(String.valueOf(count));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadDriverCount() {
        dbRoot.child("Users").child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                tvDriversCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void loadFeedbackCount() {
        dbRoot.child("feedback").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = 0;
                for (DataSnapshot studentNode : snapshot.getChildren()) {
                    total += studentNode.getChildrenCount();
                }
                tvFeedbackCount.setText(String.valueOf(total));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
