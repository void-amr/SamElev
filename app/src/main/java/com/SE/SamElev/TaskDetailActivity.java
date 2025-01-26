package com.SE.SamElev;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView taskNameTextView, taskDescriptionTextView, dueDateTextView, assignedEmailTextView;
    private Button taskComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Initialize views
        taskNameTextView = findViewById(R.id.taskNameTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        assignedEmailTextView = findViewById(R.id.assignedEmailTextView);
        taskComplete = findViewById(R.id.taskComplete);

        // Get taskId from intent
        String taskId = getIntent().getStringExtra("TASK_ID");

        // Fetch task details
        fetchTaskDetailsFromFirestore(taskId);

        // Mark task as completed and remove it from the list
        taskComplete.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tasks").document(taskId)
                    .update("status", "completed") // Mark the task as completed
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();

                        finish(); // Close the activity to return to the previous screen

                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to complete task", Toast.LENGTH_SHORT).show());
        });


    }
    private void fetchTaskDetailsFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String taskName = documentSnapshot.getString("taskName");
                        String taskDescription = documentSnapshot.getString("taskDescription");
                        String dueDate = documentSnapshot.getString("dueDate");
                        String assignedEmail = documentSnapshot.getString("assignedEmail");

                        taskNameTextView.setText(taskName);
                        taskDescriptionTextView.setText(taskDescription);
                        dueDateTextView.setText(dueDate);
                        assignedEmailTextView.setText(assignedEmail);
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch task details", Toast.LENGTH_SHORT).show());
    }

}
