package com.example.bustracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {

    private EditText emailid, password, username, usn;
    private Spinner branchSpinner, semesterSpinner;
    private Button signupbtn;
    private TextView textViewloginhere;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mydatabase = FirebaseDatabase.getInstance();

        // UI
        emailid = findViewById(R.id.emaileditText);
        password = findViewById(R.id.passwordeditText);
        username = findViewById(R.id.usernameText);
        usn = findViewById(R.id.usnEditText);
        branchSpinner = findViewById(R.id.branchSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        signupbtn = findViewById(R.id.SignUpbutton);
        textViewloginhere = findViewById(R.id.alreadyhaveaccounttextView);

        // Branch Spinner Hint
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item);
        branchAdapter.add("Select Branch");
        branchAdapter.addAll(getResources().getStringArray(R.array.branch_list));
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        // Semester Spinner Hint
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item);
        semesterAdapter.add("Select Semester");
        semesterAdapter.addAll(getResources().getStringArray(R.array.semester_list));
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        // SIGNUP BUTTON
        signupbtn.setOnClickListener(v -> {

            String email = emailid.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String user = username.getText().toString().trim();
            String usnValue = usn.getText().toString().trim();
            String branch = branchSpinner.getSelectedItem().toString();
            String semester = semesterSpinner.getSelectedItem().toString();

            // VALIDATION
            if (user.isEmpty()) {
                username.setError("Enter Username");
                username.requestFocus();
                return;
            }
            if (usnValue.isEmpty()) {
                usn.setError("Enter USN");
                usn.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                emailid.setError("Enter Email");
                emailid.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                password.setError("Enter Password");
                password.requestFocus();
                return;
            }

            // Create Student Object
            StudentData student = new StudentData(user, usnValue, email, pass, branch, semester);

            // Firebase Create User
            mFirebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(NewUserActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(NewUserActivity.this,
                                        "Signup Failed! Try again.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String user_ID = mFirebaseAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db =
                                        mydatabase.getReference("Users")
                                                .child("Students")
                                                .child(user_ID);

                                current_user_db.setValue(student);

                                // Logout after signup
                                mFirebaseAuth.signOut();

                                Toast.makeText(NewUserActivity.this,
                                        "Account Created! Please login.",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(NewUserActivity.this, UserLoginActivity.class));
                                finish();
                            }
                        }
                    });
        });

        // Already have account → Login
        textViewloginhere.setOnClickListener(v -> {
            startActivity(new Intent(NewUserActivity.this, UserLoginActivity.class));
            finish();
        });
    }

    // Student model
    public static class StudentData {
        public String username, usn, email, password, branch, semester;

        public StudentData() {}

        public StudentData(String username, String usn, String email, String password, String branch, String semester) {
            this.username = username;
            this.usn = usn;
            this.email = email;
            this.password = password;
            this.branch = branch;
            this.semester = semester;
        }
    }
}
