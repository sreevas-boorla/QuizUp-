package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateQuizActivity extends AppCompatActivity {

    private TextInputEditText etQuizTitle, etQuizTopic;
    private MaterialButton btnProceed;
    private DatabaseReference mQuizzesRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Toolbar toolbar = findViewById(R.id.create_quiz_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        mQuizzesRef = FirebaseDatabase.getInstance().getReference("Quizzes");

        etQuizTitle = findViewById(R.id.et_quiz_title);
        etQuizTopic = findViewById(R.id.et_quiz_topic);
        btnProceed = findViewById(R.id.btn_proceed_add_questions);

        btnProceed.setOnClickListener(v -> proceedToManageQuestions());
    }

    private void proceedToManageQuestions() {
        String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
        String topic = etQuizTopic.getText() != null ? etQuizTopic.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            etQuizTitle.setError("Quiz Title is required");
            etQuizTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(topic)) {
            topic = "General";
        }

        FirebaseUser user = mAuth.getCurrentUser();
        String teacherId = user != null ? user.getUid() : "anonymous_teacher";
        String teacherName = user != null && user.getEmail() != null ? user.getEmail() : "Teacher";

        String quizId = mQuizzesRef.push().getKey();
        if (quizId == null) {
            quizId = "quiz_" + System.currentTimeMillis();
        }

        // Initialize Quiz Record in Firebase Realtime Database
        Quiz newQuiz = new Quiz(quizId, title, topic, teacherId, teacherName, System.currentTimeMillis(), 0);
        mQuizzesRef.child(quizId).setValue(newQuiz);

        // Open Manage Questions Activity
        Intent intent = new Intent(CreateQuizActivity.this, ManageQuizActivity.class);
        intent.putExtra("QUIZ_ID", quizId);
        intent.putExtra("QUIZ_TITLE", title);
        intent.putExtra("QUIZ_TOPIC", topic);
        startActivity(intent);
        finish();
    }
}
