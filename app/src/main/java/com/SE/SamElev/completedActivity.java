package com.SE.SamElev;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class completedActivity extends AppCompatActivity {

    private ListView completedTasksListView;
    private ArrayList<String> completedTasksList;
    private ArrayList<String> taskIdsList; // List to hold task IDs
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        completedTasksListView = findViewById(R.id.complete);

        // Initialize lists and adapter
        completedTasksList = new ArrayList<>();
        taskIdsList = new ArrayList<>(); // Initialize task IDs list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, completedTasksList);
        completedTasksListView.setAdapter(adapter);

        // Fetch completed tasks from Firestore
        fetchCompletedTasks();

        // Set an item click listener to view the task details
        completedTasksListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked task's ID
            String taskId = taskIdsList.get(position);

            // Open taskComp activity and pass the task ID
            openTaskCompActivity(taskId);
        });

        // Set a long press listener to delete the task
        completedTasksListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the clicked task's ID
            String taskId = taskIdsList.get(position);

            // Show a confirmation dialog to delete the task
            showDeleteConfirmationDialog(taskId);
            return true; // Return true to indicate that the event is handled
        });
    }

    private void fetchCompletedTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch tasks where status is 'completed'
        db.collection("tasks")
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        completedTasksList.clear(); // Clear the previous list
                        taskIdsList.clear(); // Clear the previous task IDs list
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String taskName = document.getString("taskName");
                            String taskId = document.getId(); // Get the task ID

                            if (taskName != null) {
                                completedTasksList.add(taskName); // Add task name to the list
                                taskIdsList.add(taskId); // Add task ID to the list
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter of changes
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch completed tasks", Toast.LENGTH_SHORT).show();
                });
    }

    private void openTaskCompActivity(String taskId) {
        // Create an Intent to open taskComp activity
        Intent intent = new Intent(completedActivity.this, taskComp.class);

        // Pass the task ID to the next activity (taskComp)
        intent.putExtra("TASK_ID", taskId);

        // Start taskComp activity
        startActivity(intent);
    }

    // Show a confirmation dialog before deleting the task
    private void showDeleteConfirmationDialog(String taskId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteTaskFromFirestore(taskId);
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Delete the task from Firestore
    private void deleteTaskFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete task document from Firestore
        db.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(unused -> {
                    // Task deleted successfully
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    // Remove the task from the list and notify the adapter
                    int position = taskIdsList.indexOf(taskId);
                    if (position != -1) {
                        completedTasksList.remove(position);
                        taskIdsList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to delete the task
                    Toast.makeText(this, "Failed to delete task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
