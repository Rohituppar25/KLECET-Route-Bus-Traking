package com.example.bustracking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.UUID;

public class SendNotificationActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnSend;
    private TextView tvStatus;
    private DatabaseReference dbNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvStatus = findViewById(R.id.tvSendStatus);

        dbNotifications = FirebaseDatabase.getInstance().getReference().child("notifications");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();
                String message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                    tvStatus.setText("Enter title and message");
                    return;
                }
                sendNotification(title, message);
            }
        });
    }

    private void sendNotification(String title, String message) {
        String id = UUID.randomUUID().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("message", message);
        map.put("timestamp", System.currentTimeMillis());

        dbNotifications.child(id).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvStatus.setText("Notification sent");
                etTitle.setText("");
                etMessage.setText("");
            } else {
                tvStatus.setText("Failed to send");
            }
        });
    }
}
