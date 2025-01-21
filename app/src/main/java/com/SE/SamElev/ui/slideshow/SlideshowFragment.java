package com.SE.SamElev.ui.slideshow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SE.SamElev.AttendanceAdapter;
import com.SE.SamElev.AttendanceDay;
import com.SE.SamElev.databinding.FragmentSlideshowBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private RecyclerView rvAttendanceGrid;
    private AttendanceAdapter attendanceAdapter;
    private List<AttendanceDay> attendanceDays = new ArrayList<>();
    private FirebaseFirestore firestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        rvAttendanceGrid = binding.rvAttendanceGrid; // Reference RecyclerView in layout
        rvAttendanceGrid.setLayoutManager(new GridLayoutManager(requireContext(), 7)); // 7 columns

        // Fetch and update attendance data
        fetchAttendanceData();

        return root;
    }

    private void fetchAttendanceData() {
        firestore.collection("Employees")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Toast.makeText(requireContext(), "Error fetching attendance data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        attendanceDays.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String name = document.getString("name");
                            String status = document.getString("status");

                            // Map status to boolean (present = true, absent = false)
                            boolean isPresent = "present".equalsIgnoreCase(status);

                            // Add employee's attendance data
                            attendanceDays.add(new AttendanceDay(name, isPresent));
                        }

                        // Update RecyclerView
                        if (attendanceAdapter == null) {
                            attendanceAdapter = new AttendanceAdapter(attendanceDays);
                            rvAttendanceGrid.setAdapter(attendanceAdapter);
                        } else {
                            attendanceAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
