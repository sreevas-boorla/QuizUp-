package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton btnRegister;
    private ProgressBar pbLoading;
    private TextView tvLogin;
    private RadioGroup rgRole;
    private RadioButton rbTeacher, rbStudent;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        pbLoading = findViewById(R.id.pb_loading);
        tvLogin = findViewById(R.id.tv_login);
        rgRole = findViewById(R.id.rg_role);
        rbTeacher = findViewById(R.id.rb_teacher);
        rbStudent = findViewById(R.id.rb_student);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            finish(); // Returns to Login activity
        });
    }

    private void registerUser() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String role = rbTeacher.isChecked() ? "teacher" : "student";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Firebase Create User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            User user = new User(uid, name, email, role);

                            // Store user details with role in Firebase Realtime Database
                            mDatabase.child(uid).setValue(user).addOnCompleteListener(dbTask -> {
                                pbLoading.setVisibility(View.GONE);
                                btnRegister.setEnabled(true);

                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    navigateToHome(role);
                                } else {
                                    Toast.makeText(Register.this, "Profile creation note: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    navigateToHome(role);
                                }
                            });
                        }
                    } else {
                        pbLoading.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        Toast.makeText(Register.this, "Registration Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToHome(String role) {
        Intent intent;
        if ("teacher".equalsIgnoreCase(role)) {
            intent = new Intent(Register.this, TeacherHomeActivity.class);
        } else {
            intent = new Intent(Register.this, StudentHomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
