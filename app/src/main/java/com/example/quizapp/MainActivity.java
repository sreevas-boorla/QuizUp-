package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton btnGoToLogin = findViewById(R.id.btn_go_to_login);
        if (btnGoToLogin != null) {
            btnGoToLogin.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            });
        }
    }
}
