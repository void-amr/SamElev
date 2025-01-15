package com.SE.SamElev.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.SE.SamElev.databinding.FragmentGalleryBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private FirebaseFirestore firestore;
    private ArrayList<String> announcementsList;
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore and ListView
        firestore = FirebaseFirestore.getInstance();
        ListView announcementsListView = binding.announcementsListView;

        // Initialize the list and adapter
        announcementsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, announcementsList);
        announcementsListView.setAdapter(adapter);

        // Fetch announcements
        fetchAnnouncements();

        return root;
    }

    private void fetchAnnouncements() {
        firestore.collection("Announcements").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        announcementsList.clear(); // Clear the list to avoid duplicates
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            if (message != null) {
                                announcementsList.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    } else {
                        Toast.makeText(requireContext(), "Failed to load announcements", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
