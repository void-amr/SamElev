package com.SE.SamElev.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.SE.SamElev.R;
import com.SE.SamElev.TaskDetailActivity;
import com.SE.SamElev.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListView taskListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout and get a reference to the root view
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ListView
        taskListView = root.findViewById(R.id.taskListView); // Ensure the ID is correct

        // Fetch tasks from Firestore
        fetchTasksFromFirestore();

        return root;
    }

    private void fetchTasksFromFirestore() {
        // Get the current authenticated user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Initialize Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query Firestore for tasks assigned to the logged-in user
            db.collection("tasks")
                    .whereEqualTo("assignedEmail", userEmail)  // Filter tasks based on the assigned email
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ArrayList<String> taskNames = new ArrayList<>();
                        final ArrayList<String> taskIds = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String taskName = document.getString("taskName");
                            if (taskName != null) {
                                taskNames.add(taskName);
                                taskIds.add(document.getId());  // Store document ID for future reference
                            }
                        }
                        // Set the adapter to ListView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, taskNames);
                        taskListView.setAdapter(adapter);

                        // Set click listener for ListView items
                        taskListView.setOnItemClickListener((parent, view, position, id) -> {
                            // Pass the task document ID to TaskDetailActivity
                            String taskId = taskIds.get(position);
                            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                            intent.putExtra("TASK_ID", taskId); // Pass the task document ID
                            startActivity(intent);
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
