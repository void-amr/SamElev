package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up Edge-to-Edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLogin = findViewById(R.id.btnLogin);
        // Set click listener for registration
        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Register.class)));
        // Set click listener for login
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Login.class)));
    }

    @Override
    public void onBackPressed() {
        // Exits the app
        super.onBackPressed();
        finishAffinity();
    }

}