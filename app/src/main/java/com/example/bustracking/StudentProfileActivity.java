package com.example.bustracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentProfileActivity extends AppCompatActivity {

    TextView nameTextView, usnTextView, emailTextView, branchTextView, semesterTextView;
    Button changePasswordButton, logoutButton;
    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child("Students").child(uid);

        // UI init
        nameTextView = findViewById(R.id.nameTextView);
        usnTextView = findViewById(R.id.usnTextView);
        emailTextView = findViewById(R.id.emailTextView);
        branchTextView = findViewById(R.id.branchTextView);
        semesterTextView = findViewById(R.id.semesterTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);


        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile); // Highlight current page

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(StudentProfileActivity.this, StudentMainActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(StudentProfileActivity.this, StudentFeedbackActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(StudentProfileActivity.this, ViewNotificationsActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // already here
            }

            return false;
        });


        // Load student info
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("username").getValue(String.class);
                String usn = snapshot.child("usn").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String branch = snapshot.child("branch").getValue(String.class);
                String semester = snapshot.child("semester").getValue(String.class);

                nameTextView.setText("Name: " + name);
                usnTextView.setText("USN: " + usn);
                emailTextView.setText("Email: " + email);
                branchTextView.setText("Branch: " + branch);
                semesterTextView.setText("Semester: " + semester);
            }
        });

        // Change password dialog
        changePasswordButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentProfileActivity.this);
            builder.setTitle("Change Password");

            final EditText input = new EditText(StudentProfileActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setHint("Enter new password");
            builder.setView(input);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newPass = input.getText().toString().trim();
                if (newPass.length() < 6) {
                    Toast.makeText(StudentProfileActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.getCurrentUser().updatePassword(newPass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(StudentProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StudentProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(StudentProfileActivity.this, UserLoginActivity.class);
            // Clear back stack after logout
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
