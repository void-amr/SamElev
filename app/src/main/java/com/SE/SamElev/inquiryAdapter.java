package com.SE.SamElev;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

public class inquiryAdapter extends AppCompatActivity {
    private TextView inquiryData, inquiryPhone, inquiryEmail, inquiryName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inquiriesRef = db.collection("Inquiry");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_adapter);

        // Initialize UI components
        inquiryData = findViewById(R.id.inquiryData);
        inquiryPhone = findViewById(R.id.inquiryPhone);
        inquiryEmail = findViewById(R.id.inquiryEmail);
        inquiryName = findViewById(R.id.inquiryName);

        // Get the inquiryId from the intent
        String inquiryId = getIntent().getStringExtra("inquiryId");

        if (inquiryId != null) {
            // Fetch and display the inquiry details from Firestore
            fetchInquiryDetails(inquiryId);
        } else {
            Toast.makeText(this, "No inquiry ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchInquiryDetails(String inquiryId) {
        inquiriesRef.document(inquiryId)
                .get()
                .addOnCompleteListener(Inquiry -> {
                    if (Inquiry.isSuccessful() && Inquiry.getResult() != null) {
                        // Get the inquiry data from Firestore document
                        DocumentSnapshot document = Inquiry.getResult();
                        if (document.exists()) {
                            String data = document.getString("inquiry");  // Inquiry data (e.g., description)
                            String phone = document.getString("phone");  // Phone number
                            String email = document.getString("email");  // Email
                            String name = document.getString("name");  // Name of the request sender

                            // Display the inquiry details in the UI
                            inquiryData.setText("Inquiry Data: " + data);
                            inquiryPhone.setText("Phone: " + phone);
                            inquiryEmail.setText("Email: " + email);
                            inquiryName.setText("Name: " + name);
                        } else {
                            Toast.makeText(this, "Inquiry not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch inquiry details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
