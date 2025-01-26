package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity {
    Button btnUser, btnTasks, btnAttendance, btnAnnounce, btnInquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnUser = findViewById(R.id.btnUser);
        btnTasks = findViewById(R.id.btnTasks);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnAnnounce = findViewById(R.id.btnAnnounce);
        btnInquiry = findViewById(R.id.inquiryButton);

        btnUser.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, updateUser.class));
        });

        btnTasks.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, assignTasks.class));
        });

        btnAttendance.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, markAttendance.class));
        });

        btnAnnounce.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, announce.class));
        });

        btnInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, inquiryDetails.class));
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