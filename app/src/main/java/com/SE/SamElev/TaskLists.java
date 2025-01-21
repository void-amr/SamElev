package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TaskLists extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> taskList; // List of tasks
    private ArrayAdapter<String> adapter;

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksRef = db.collection("tasks"); // Replace "tasks" with your Firestore collection name
    private ListenerRegistration listenerRegistration; // To manage the listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_lists); // Your layout with the ListView

        listView = findViewById(R.id.listView);

        // Initialize task list
        taskList = new ArrayList<>();

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listView.setAdapter(adapter);

        // Fetch tasks from Firestore in real-time
        listenForTaskUpdates();

        listView.setOnItemClickListener((parent,view,position,id) -> {
            // Get the task that was clicked
            String selectedTask = taskList.get(position);

            // Fetch the task ID by task name
            getTaskIdByName(selectedTask, taskId -> {
                if (taskId != null) {
                    // Create an Intent to start TaskListData activity
                    Intent intent = new Intent(TaskLists.this, TaskListData.class);

                    // Pass the task ID to the new activity
                    intent.putExtra("TASK_ID", taskId);

                    // Start the new activity
                    startActivity(intent);
                } else {
                    Toast.makeText(TaskLists.this, "Task ID not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
        // Set an item click listener on the ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the task that was clicked
            String selectedTask = taskList.get(position);

            // Fetch the task ID by task name
            getTaskIdByName(selectedTask, taskId -> {
                if (taskId != null) {
                    // Create an Intent to start TaskListData activity
                    Intent intent = new Intent(TaskLists.this, TaskListData.class);

                    // Pass the task ID to the new activity
                    intent.putExtra("TASK_ID", taskId);

                    // Start the new activity
                    startActivity(intent);
                } else {
                    Toast.makeText(TaskLists.this, "Task ID not found", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set a long click listener to delete tasks
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the task name
            String taskNameToDelete = taskList.get(position);

            // Delete task from Firestore
            deleteTaskByName(taskNameToDelete);

            return true; // Return true to indicate the event was handled
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove(); // Clean up Firestore listener
        }
    }

    // Listen for real-time updates from Firestore
    private void listenForTaskUpdates() {
        listenerRegistration = tasksRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle error
                Toast.makeText(TaskLists.this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                // Clear the current task list
                taskList.clear();

                // Loop through the snapshot and add task names to the list
                for (QueryDocumentSnapshot document : value) {
                    String taskName = document.getString("taskName");

                    if (taskName != null) {
                        taskList.add(taskName);
                    }
                }

                // Notify the adapter to refresh the ListView
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Method to fetch task ID based on task name
    private void getTaskIdByName(String taskName, TaskIdCallback callback) {
        tasksRef.whereEqualTo("taskName", taskName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            callback.onTaskIdFetched(document.getId());
                            return;
                        }
                    }
                    callback.onTaskIdFetched(null);
                });
    }

    // Method to delete a task from Firestore
    private void deleteTaskByName(String taskName) {
        tasksRef.whereEqualTo("taskName", taskName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(TaskLists.this, "Task deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(TaskLists.this, "Error deleting task", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(TaskLists.this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Callback interface to handle the result of the task ID fetch
    interface TaskIdCallback {
        void onTaskIdFetched(String taskId);
    }
}
