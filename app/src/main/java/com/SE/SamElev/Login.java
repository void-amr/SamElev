package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Handle Login
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password is required");
            return;
        }

        // Authenticate user with Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                        // Retrieve user type from Users collection
                        firestore.collection("Users").document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String userType = documentSnapshot.getString("userType");
                                        navigateToNextActivity(userType); // Redirect based on user type
                                    } else {
                                        // If not found in Users, check Employees collection
                                        firestore.collection("Employees").document(userId)
                                                .get()
                                                .addOnSuccessListener(employeeSnapshot -> {
                                                    if (employeeSnapshot.exists()) {
                                                        navigateToNextActivity("employee"); // Treat as employee
                                                    } else {
                                                        Toast.makeText(Login.this, "User data not found in Firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Error fetching employee data", e);
                                                    Toast.makeText(Login.this, "Error fetching employee data", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error fetching user data", e);
                                    Toast.makeText(Login.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("FirebaseAuth", "Login failed", task.getException());
                        Toast.makeText(Login.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToNextActivity(String userType) {
        if (userType == null) {
            Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        switch (userType.toLowerCase()) {
            case "employee":
                intent = new Intent(Login.this, EmployeeActivity.class);
                break;
            case "user":
                intent = new Intent(Login.this, UserActivity.class);
                break;
            case "admin":
                intent = new Intent(Login.this, AdminActivity.class);
                break;
            default:
                Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        // Exits the app
        super.onBackPressed();
        finishAffinity();
    }
}
