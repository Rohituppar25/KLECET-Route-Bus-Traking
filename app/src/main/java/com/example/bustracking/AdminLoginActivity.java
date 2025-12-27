package com.example.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvStatus;

    // NOTE: For simplicity the admin credentials are hardcoded here.
    // In production, store admin users in Firebase Auth or a secure DB node.
    private final String ADMIN_EMAIL = "admin@klecet.edu";
    private final String ADMIN_PASSWORD = "admin@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etEmail = findViewById(R.id.etAdminEmail);
        etPassword = findViewById(R.id.etAdminPassword);
        btnLogin = findViewById(R.id.btnAdminLogin);
        tvStatus = findViewById(R.id.tvLoginStatus);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    tvStatus.setText("Enter email and password");
                    return;
                }

                // Simple check (replace with Firebase Auth or DB lookup for production)
                if (email.equalsIgnoreCase(ADMIN_EMAIL) && pass.equals(ADMIN_PASSWORD)) {
                    tvStatus.setText("");
                    Intent i = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    tvStatus.setText("Invalid credentials");
                }
            }
        });
    }
}
