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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private EditText editEmail, editPassword, editUserType;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editUserType = findViewById(R.id.editUserType);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Handle Registration
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String userType = editUserType.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (TextUtils.isEmpty(userType)) {
            editUserType.setError("User Type is required");
            return;
        }

        // Register user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Store additional data in Firestore
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("userType", userType);

                        firestore.collection("Users").document(userId).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    navigateToNextActivity(userType); // Redirect based on user type
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error storing user data", e));
                    } else {
                        Log.e("FirebaseAuth", "Registration failed", task.getException());;
                    }
                });
    }

    private void navigateToNextActivity(String userType) {
        if (userType.equalsIgnoreCase("employee")) {
            startActivity(new Intent(Register.this, EmployeeActivity.class));
        } else if (userType.equalsIgnoreCase("user")) {
            startActivity(new Intent(Register.this, UserActivity.class));
        }
         else {
            Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
