package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherHomeActivity extends AppCompatActivity {

    private TextView tvTeacherName;
    private MaterialCardView cardCreateQuiz, cardQuizHistory;
    private MaterialButton btnActionCreateQuiz, btnActionQuizHistory;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        // App-Bar Configuration
        Toolbar toolbar = findViewById(R.id.teacher_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teacher's Portal");
        }

        mAuth = FirebaseAuth.getInstance();
        tvTeacherName = findViewById(R.id.tv_teacher_name);
        cardCreateQuiz = findViewById(R.id.card_create_quiz);
        cardQuizHistory = findViewById(R.id.card_quiz_history);
        btnActionCreateQuiz = findViewById(R.id.btn_action_create_quiz);
        btnActionQuizHistory = findViewById(R.id.btn_action_quiz_history);

        // Fetch Teacher Details from Firebase Database
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getName() != null && !user.getName().isEmpty()) {
                            tvTeacherName.setText("Welcome, " + user.getName());
                            return;
                        }
                    }
                    if (currentUser.getEmail() != null) {
                        tvTeacherName.setText("Welcome, " + currentUser.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Ignored, fallback stays default
                }
            });
        }

        // Navigate to Create Quiz Activity
        Runnable openCreateQuiz = () -> {
            Intent intent = new Intent(TeacherHomeActivity.this, CreateQuizActivity.class);
            startActivity(intent);
        };
        cardCreateQuiz.setOnClickListener(v -> openCreateQuiz.run());
        btnActionCreateQuiz.setOnClickListener(v -> openCreateQuiz.run());

        // Navigate to Quiz History Activity
        Runnable openQuizHistory = () -> {
            Intent intent = new Intent(TeacherHomeActivity.this, TeacherHistoryActivity.class);
            startActivity(intent);
        };
        cardQuizHistory.setOnClickListener(v -> openQuizHistory.run());
        btnActionQuizHistory.setOnClickListener(v -> openQuizHistory.run());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TeacherHomeActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
