package com.example.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentFeedbackActivity extends AppCompatActivity {

    private EditText editFeedback;
    private Button btnSend;
    private RecyclerView recyclerView;

    private FeedbackAdapter adapter;
    private List<FeedbackModel> feedbackList = new ArrayList<>();

    private DatabaseReference feedbackRef;
    private String studentId;  // logged-in student

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_feedback);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_feedback);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, StudentMainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, ViewNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, StudentProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });

        editFeedback = findViewById(R.id.editFeedback);
        btnSend = findViewById(R.id.btnSendFeedback);
        recyclerView = findViewById(R.id.recyclerFeedback);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Logged-in student UID
        studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        feedbackRef = FirebaseDatabase.getInstance()
                .getReference("feedback")
                .child(studentId);

        // Adapter with 2 parameters
        adapter = new FeedbackAdapter(this, feedbackList);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendFeedback());
        loadFeedback();
    }

    private void sendFeedback() {
        String message = editFeedback.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        FeedbackModel feedback = new FeedbackModel(
                feedbackId,
                message,
                timestamp,
                studentId
        );

        feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Feedback sent", Toast.LENGTH_SHORT).show();
                    editFeedback.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadFeedback() {
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    FeedbackModel fb = snap.getValue(FeedbackModel.class);
                    if (fb != null) feedbackList.add(fb);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentFeedbackActivity.this,
                        "Failed to load feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
