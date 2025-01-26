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
    private ArrayList<String> announcementList;
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
        announcementList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, announcementList);
        taskListView.setAdapter(adapter);

        // Fetch pending tasks from Firestore
        fetchAnnoucements();

        return root;
    }
    private void fetchAnnoucements() {
        firestore.collection("Announcements")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Failed to announcements", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String an_message = document.getString("message");
                            if (an_message != null) {
                                announcementList.add(an_message);
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
