package com.SE.SamElev;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class markAttendance extends AppCompatActivity {

    private RecyclerView rvEmployeeList;
    private adminAttendance adapter;  // Use 'adminAttendance' adapter
    private List<Employee> employeeList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        rvEmployeeList = findViewById(R.id.rvEmployeeList);
        rvEmployeeList.setLayoutManager(new LinearLayoutManager(this));

        // Fetch employee data
        fetchEmployees();
    }

    private void fetchEmployees() {
        firestore.collection("Employees")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Toast.makeText(markAttendance.this, "Error fetching employees", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        employeeList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            employeeList.add(new Employee(id, name));
                        }

                        // Update adapter
                        if (adapter == null) {
                            adapter = new adminAttendance(markAttendance.this, employeeList);
                            rvEmployeeList.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}

