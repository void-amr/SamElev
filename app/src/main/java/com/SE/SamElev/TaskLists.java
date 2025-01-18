package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TaskLists extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> taskList; // List of tasks
    private ArrayAdapter<String> adapter;

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksRef = db.collection("tasks"); // Replace "tasks" with your Firestore collection name

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

        // Fetch tasks from Firestore
        fetchTasksFromFirestore();

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
    }

    // Method to fetch tasks from Firestore
    private void fetchTasksFromFirestore() {
        tasksRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the current task list
                        taskList.clear();

                        // Loop through the query results and add task names to the list
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Assuming that your task has a field called "taskName"
                                String taskName = document.getString("taskName");

                                if (taskName != null) {
                                    taskList.add(taskName);
                                }
                            }

                            // Notify the adapter to refresh the ListView
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle error
                        Toast.makeText(TaskLists.this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Method to fetch task ID based on task name
    private void getTaskIdByName(String taskName, TaskIdCallback callback) {
        tasksRef.whereEqualTo("taskName", taskName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Return the task ID
                                callback.onTaskIdFetched(document.getId());
                                return;
                            }
                        }
                    }
                    // If task ID is not found, return null
                    callback.onTaskIdFetched(null);
                });
    }

    // Callback interface to handle the result of the task ID fetch
    interface TaskIdCallback {
        void onTaskIdFetched(String taskId);
    }
} 