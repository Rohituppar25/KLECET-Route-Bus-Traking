package com.example.bustracking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class UserLoginActivity extends AppCompatActivity {

    EditText lemailid, lpassword;
    Button loginbtn;
    TextView signuphere, textViewForgotPassword;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        // UI
        lemailid = findViewById(R.id.emaileditTextlogin);
        lpassword = findViewById(R.id.passwordeditTextlogin);
        loginbtn = findViewById(R.id.Loginbutton);
        signuphere = findViewById(R.id.donthaveaccounttextView);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // LOGIN BUTTON
        loginbtn.setOnClickListener(v -> {
            String email = lemailid.getText().toString().trim();
            String password = lpassword.getText().toString().trim();

            if (email.isEmpty()) {
                lemailid.setError("Please enter Email ID");
                lemailid.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                lpassword.setError("Please enter Password");
                lpassword.requestFocus();
                return;
            }

            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (!task.isSuccessful()) {
                            Toast.makeText(UserLoginActivity.this, "Login Error! Try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            redirectToMain();
                        }
                    });
        });

        // SIGNUP Redirection
        signuphere.setOnClickListener(v -> {
            startActivity(new Intent(UserLoginActivity.this, NewUserActivity.class));
            finish();
        });

        // FORGOT PASSWORD
        textViewForgotPassword.setOnClickListener(v -> {
            String email = lemailid.getText().toString().trim();

            if (email.isEmpty()) {
                lemailid.setError("Enter your registered email first");
                lemailid.requestFocus();
            } else {
                mFirebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserLoginActivity.this,
                                        "Password reset email sent!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UserLoginActivity.this,
                                        "Error sending reset email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void redirectToMain() {
        Intent intent = new Intent(UserLoginActivity.this, StudentMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
