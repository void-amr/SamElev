package com.SE.SamElev;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addEmployee extends AppCompatActivity {
    private EditText editEmployeeName, editEmployeeEmail, editEmployeePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize UI components
        editEmployeeName = findViewById(R.id.u_name);
        editEmployeeEmail = findViewById(R.id.u_editEmail);
        editEmployeePassword = findViewById(R.id.u_editPassword);
        Button btnAddEmployee = findViewById(R.id.u_btnRegister);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Add employee to Firebase on button click
        btnAddEmployee.setOnClickListener(v -> addEmployeeToFirebase());
    }

    private void addEmployeeToFirebase() {
        String name = editEmployeeName.getText().toString().trim();
        String email = editEmployeeEmail.getText().toString().trim();
        String password = editEmployeePassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            editEmployeeName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editEmployeeEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editEmployeePassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            editEmployeePassword.setError("Password must be at least 6 characters");
            return;
        }

        // Register employee in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Store additional data in Firestore
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                        Map<String, Object> employeeData = new HashMap<>();
                        employeeData.put("name", name);
                        employeeData.put("email", email);
                        employeeData.put("password", password);
                        employeeData.put("userType", "employee");

                        firestore.collection("Employees").document(userId)
                                .set(employeeData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(addEmployee.this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error adding employee data", e);
                                    Toast.makeText(addEmployee.this, "Error adding employee data", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("FirebaseAuth", "Error adding employee", task.getException());
                        Toast.makeText(addEmployee.this, "Error adding employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        // Exits the app
        super.onBackPressed();
        finishAffinity();

    }
}
