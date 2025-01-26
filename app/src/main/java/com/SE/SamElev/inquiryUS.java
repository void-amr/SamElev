package com.SE.SamElev;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class inquiryUS extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText userName, userPhno, userMail, inData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inquiry_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = findViewById(R.id.userNAME);
        userPhno = findViewById(R.id.userPHNO);
        userMail = findViewById(R.id.userEMAIL);
        inData = findViewById(R.id.inquiry);

        // Initialize Firebase Auth and Firestore here, at the class level
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Button contactButton = findViewById(R.id.contactButton);

        contactButton.setOnClickListener(v -> {
            sendInquiryRequest();
            Toast.makeText(inquiryUS.this, "Works", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendInquiryRequest() {
        // Data retrieving
        String name = userName.getText().toString().trim();
        String phoneno = userPhno.getText().toString().trim();
        String useremail = userMail.getText().toString().trim();
        String indata = inData.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(useremail)) {
            userMail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(phoneno)) {
            userPhno.setError("Phone number is required");
            return;
        }
        if(phoneno.length() < 10) {
            userPhno.setError("Enter 10 digits");
            return;
        }

        inquiryData inqdata = new inquiryData(name, phoneno, useremail, indata);
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        firestore.collection("Inquiry").add(inqdata)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "We will contact you shortly", Toast.LENGTH_LONG).show();
                    userName.setText("");
                    userPhno.setText("");
                    userMail.setText("");
                    inData.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send request for sales", Toast.LENGTH_LONG).show();
                });
    }
}
