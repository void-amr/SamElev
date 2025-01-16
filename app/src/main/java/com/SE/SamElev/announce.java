package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class announce extends AppCompatActivity {

    private TextInputEditText editText;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announce);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.subtext);
        Button submitButton = findViewById(R.id.submitButton);
        Button viewAnnouncementsButton = findViewById(R.id.viewAnnouncementsButton);

        submitButton.setOnClickListener(v -> sendAnnouncement());
        viewAnnouncementsButton.setOnClickListener(v -> viewAnnouncements());
    }

    private void sendAnnouncement() {
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            editText.setError("Message is required");
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create announcement data
            Announcement announcement = new Announcement(message, userId);

            // Add announcement to Firestore
            firestore.collection("Announcements").add(announcement)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(announce.this, "Announcement sent", Toast.LENGTH_SHORT).show();
                        editText.setText(""); // Clear the input field
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(announce.this, "Failed to send announcement", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewAnnouncements() {
        Intent intent = new Intent(this, ViewAnnouncementsActivity.class);
        startActivity(intent);
    }

    private static class Announcement {
        private String message;
        private String userId;

        public Announcement(String message, String userId) {
            this.message = message;
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public String getUserId() {
            return userId;
        }
    }
}