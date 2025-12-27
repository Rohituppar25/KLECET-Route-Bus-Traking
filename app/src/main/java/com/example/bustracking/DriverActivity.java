package com.example.bustracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverActivity extends AppCompatActivity {

    public EditText demail, dpassword, dusername, dRouteNo;
    Button dsignup;
    TextView dtextviewloginhere;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        mFirebaseAuth = FirebaseAuth.getInstance();

        demail = findViewById(R.id.driveremaileditText);
        dpassword = findViewById(R.id.driverpasswordeditText);
        dusername = findViewById(R.id.driverusernameeditText);
        dRouteNo = findViewById(R.id.RouteNo);

        dsignup = findViewById(R.id.driversignupbutton);
        dtextviewloginhere = findViewById(R.id.alreadyhaveanaccountofdrivertextView);

        dsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String driveremail = demail.getText().toString().trim();
                String driverpassword = dpassword.getText().toString().trim();
                String driverusername = dusername.getText().toString().trim();
                String Route = dRouteNo.getText().toString().trim();

                // Create object
                final DriverCustomFields object = new DriverCustomFields(
                        driveremail,
                        driverpassword,
                        driverusername,
                        Route
                );

                // Validation
                if (driveremail.isEmpty()) {
                    demail.setError("Please enter Email Id");
                    demail.requestFocus();
                    return;
                }

                if (driverpassword.isEmpty()) {
                    dpassword.setError("Please enter Password");
                    dpassword.requestFocus();
                    return;
                }

                if (Route.isEmpty()) {
                    dRouteNo.setError("Please enter Route Number");
                    dRouteNo.requestFocus();
                    return;
                }

                // Firebase signup
                mFirebaseAuth.createUserWithEmailAndPassword(driveremail, driverpassword)
                        .addOnCompleteListener(DriverActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(DriverActivity.this,
                                            "Signup Failed! Try Again",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    String driver_id = mFirebaseAuth.getCurrentUser().getUid();

                                    // Save under Users -> Drivers
                                    DatabaseReference current_driver =
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("Users")
                                                    .child("Drivers")   // FIXED HERE
                                                    .child(driver_id);

                                    current_driver.setValue(object);

                                    Toast.makeText(DriverActivity.this,
                                            "Signup Successful!",
                                            Toast.LENGTH_SHORT).show();

                                    // Redirect to Driver Login Page
                                    startActivity(new Intent(DriverActivity.this, CabDriversActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

        // Already have an account? -> Go to login page
        dtextviewloginhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverActivity.this, CabDriversActivity.class));
                finish();
            }
        });
    }
}
