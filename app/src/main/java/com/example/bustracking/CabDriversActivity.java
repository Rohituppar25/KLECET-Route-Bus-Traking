package com.example.bustracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CabDriversActivity extends AppCompatActivity {

    EditText cabdriveremail, cabdriverpassword;
    TextView cabdriversignup, textViewForgotPassword;
    Button cabdriverloginbtn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_drivers);

        // Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // UI Elements
        cabdriveremail = findViewById(R.id.editTextcabdriveremail);
        cabdriverpassword = findViewById(R.id.editTextcabdriverpassword);
        cabdriversignup = findViewById(R.id.textViewcabtosignup);
        cabdriverloginbtn = findViewById(R.id.buttoncabdriver);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // LOGIN BUTTON
        cabdriverloginbtn.setOnClickListener(v -> {
            String cabemail = cabdriveremail.getText().toString().trim();
            String cabpassword = cabdriverpassword.getText().toString().trim();

            if (cabemail.isEmpty()) {
                cabdriveremail.setError("Please enter Email ID");
                cabdriveremail.requestFocus();
                return;
            }

            if (cabpassword.isEmpty()) {
                cabdriverpassword.setError("Please enter Password");
                cabdriverpassword.requestFocus();
                return;
            }

            // Firebase login
            mFirebaseAuth.signInWithEmailAndPassword(cabemail, cabpassword)
                    .addOnCompleteListener(CabDriversActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(CabDriversActivity.this, "Login failed! Try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CabDriversActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CabDriversActivity.this, DriverPortalActivity.class));
                                finish();
                            }
                        }
                    });
        });

        // SIGNUP REDIRECTION
        cabdriversignup.setOnClickListener(v -> {
            startActivity(new Intent(CabDriversActivity.this, DriverActivity.class));
        });

        // FORGOT PASSWORD
        textViewForgotPassword.setOnClickListener(v -> {
            String email = cabdriveremail.getText().toString().trim();

            if (email.isEmpty()) {
                cabdriveremail.setError("Enter your registered email first");
                cabdriveremail.requestFocus();
            } else {
                mFirebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CabDriversActivity.this,
                                        "Password reset email sent. Check your inbox!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CabDriversActivity.this,
                                        "Error: Unable to send reset email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
