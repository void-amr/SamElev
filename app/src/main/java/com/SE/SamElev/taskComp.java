package com.SE.SamElev;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class taskComp extends AppCompatActivity {

    private TextView taskNameTextView, taskDescriptionTextView, dueDateTextView, assignedEmailTextView;
    private Button deleteTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_comp);

        // Initialize the views
        taskNameTextView = findViewById(R.id.task_name_text_view);
        taskDescriptionTextView = findViewById(R.id.task_description_text_view);
        dueDateTextView = findViewById(R.id.due_date_text_view);
        assignedEmailTextView = findViewById(R.id.assigned_email_text_view);
        deleteTaskButton = findViewById(R.id.delete);

        // Get the task ID from the intent
        String taskId = getIntent().getStringExtra("TASK_ID");

        if (taskId != null && !taskId.isEmpty()) {
            // Fetch task details from Firestore
            fetchTaskDetailsFromFirestore(taskId);
        } else {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
        }

        // Set the delete button action
        deleteTaskButton.setOnClickListener(view -> {
            if (taskId != null && !taskId.isEmpty()) {
                deleteTaskFromFirestore(taskId);
            } else {
                Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTaskDetailsFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch task details safely
                        String taskName = documentSnapshot.getString("taskName");
                        String taskDescription = documentSnapshot.getString("taskDescription");
                        String dueDate = documentSnapshot.getString("dueDate");
                        String assignedEmail = documentSnapshot.getString("assignedEmail");

                        // Set data to the views
                        taskNameTextView.setText(taskName != null ? taskName : "N/A");
                        taskDescriptionTextView.setText(taskDescription != null ? taskDescription : "N/A");
                        dueDateTextView.setText(dueDate != null ? dueDate : "N/A");
                        assignedEmailTextView.setText(assignedEmail != null ? assignedEmail : "N/A");
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch task details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTaskFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
