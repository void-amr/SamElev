
package com.SE.SamElev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class assignTasks extends AppCompatActivity {

    private EditText taskName, taskDescription, dueDateField, assignEmailField;
    private Button assignButton, taskLogButton, attachDocumentsButton;
    private static final int PICK_DOCUMENT_REQUEST = 1;
    private Uri documentUri = null; // Uri to hold the document

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_tasks); // Update layout resource name

        // Initialize views
        taskName = findViewById(R.id.task_name);
        taskDescription = findViewById(R.id.task_description);
        dueDateField = findViewById(R.id.due_date_field);
        assignEmailField = findViewById(R.id.assign_email_field);
        assignButton = findViewById(R.id.assign_button);
        taskLogButton = findViewById(R.id.task_log_button);
        attachDocumentsButton = findViewById(R.id.attach_documents_button);

        // Set click listener for the Assign button
        assignButton.setOnClickListener(v -> {
            String taskNameText = taskName.getText().toString();
            String taskDescriptionText = taskDescription.getText().toString();
            String dueDate = dueDateField.getText().toString();
            String assignedEmail = assignEmailField.getText().toString();

            // Store data in Firestore
            storeDataInFirebase(taskNameText, taskDescriptionText, dueDate, assignedEmail);
        });
        taskLogButton.setOnClickListener(v -> {
            Intent intent = new Intent(assignTasks.this, TaskLists.class);
            startActivity(intent);
        });
        // Set click listener for Attach Documents button
        attachDocumentsButton.setOnClickListener(v -> openDocumentPicker());
    }

    // Method to open document picker
    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // To select any type of file
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            documentUri = data.getData(); // Get URI of the selected document
        }
    }

    // Method to store task data in Firestore
    private void storeDataInFirebase(String taskName, String taskDescription, String dueDate, String assignedEmail) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to hold task data
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("taskName", taskName);
        taskData.put("taskDescription", taskDescription);
        taskData.put("dueDate", dueDate);
        taskData.put("assignedEmail", assignedEmail);
        taskData.put("documentUri", documentUri != null ? documentUri.toString() : "");

        // Create a new document in the "tasks" collection
        db.collection("tasks")
                .add(taskData) // This will add a new document with an auto-generated ID
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Task Assigned", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error assigning task", Toast.LENGTH_SHORT).show();
                });
    }

    // Create a Task class to store task details
    public static class Task {
        public String taskName, taskDescription, dueDate, assignedEmail, documentUri;

        public Task(String taskName, String taskDescription, String dueDate, String assignedEmail, String documentUri) {
            this.taskName = taskName;
            this.taskDescription = taskDescription;
            this.dueDate = dueDate;
            this.assignedEmail = assignedEmail;
            this.documentUri = documentUri;
        }
    }
}
