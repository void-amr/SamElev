package com.SE.SamElev.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.SE.SamElev.R;
import com.SE.SamElev.TaskDetailActivity;
import com.SE.SamElev.databinding.FragmentGalleryBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private FirebaseFirestore firestore;
    private ArrayList<String> taskList;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore and ListView
        firestore = FirebaseFirestore.getInstance();
        ListView taskListView = binding.announcementsListView;

        // Initialize the list and adapter
        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, taskList);
        taskListView.setAdapter(adapter);

        // Fetch pending tasks from Firestore
        fetchPendingTasks();

        // Set up click listener to open task details
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            String taskName = taskList.get(position);

            firestore.collection("tasks")
                    .whereEqualTo("taskName", taskName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String taskId = document.getId();
                            Intent intent = new Intent(requireContext(), TaskDetailActivity.class);
                            intent.putExtra("TASK_ID", taskId);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to open task", Toast.LENGTH_SHORT).show());
        });

        return root;
    }

    private void fetchPendingTasks() {
        firestore.collection("tasks")
                .whereEqualTo("status", "completed") //
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        taskList.clear(); // Clear the list to avoid duplicates
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String taskName = document.getString("taskName");
                            if (taskName != null) {
                                taskList.add(taskName); // Add task name to the list
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
