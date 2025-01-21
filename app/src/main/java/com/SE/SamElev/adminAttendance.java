package com.SE.SamElev;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class adminAttendance extends RecyclerView.Adapter<adminAttendance.EmployeeViewHolder> {

    private final Context context;
    private final List<Employee> employeeList; // Employee model with name and ID
    private final FirebaseFirestore firestore;

    public adminAttendance(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.tvEmployeeName.setText(employee.getName());

        // Present Button Click
        holder.btnPresent.setOnClickListener(v -> updateAttendanceStatus(employee.getId(), "present"));

        // Absent Button Click
        holder.btnAbsent.setOnClickListener(v -> updateAttendanceStatus(employee.getId(), "absent"));
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    // Update Firestore with attendance status
    private void updateAttendanceStatus(String employeeId, String status) {
        firestore.collection("Employees").document(employeeId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Status updated successfully");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error updating status", e);
                });
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName;
        Button btnPresent, btnAbsent;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            btnPresent = itemView.findViewById(R.id.btnPresent);
            btnAbsent = itemView.findViewById(R.id.btnAbsent);
        }
    }
}
