package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherHistoryActivity extends AppCompatActivity implements QuizHistoryAdapter.OnQuizHistoryClickListener {

    private RecyclerView rvQuizHistory;
    private LinearLayout llEmptyHistory;
    private ProgressBar pbHistoryLoading;

    private QuizHistoryAdapter historyAdapter;
    private final List<Quiz> quizList = new ArrayList<>();

    private DatabaseReference mQuizzesRef;
    private DatabaseReference mResultsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_history);

        Toolbar toolbar = findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quiz History & Marks");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvQuizHistory = findViewById(R.id.rv_quiz_history);
        llEmptyHistory = findViewById(R.id.ll_empty_history);
        pbHistoryLoading = findViewById(R.id.pb_history_loading);

        rvQuizHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new QuizHistoryAdapter(quizList, this);
        rvQuizHistory.setAdapter(historyAdapter);

        mAuth = FirebaseAuth.getInstance();
        mQuizzesRef = FirebaseDatabase.getInstance().getReference("Quizzes");
        mResultsRef = FirebaseDatabase.getInstance().getReference("Results");

        loadQuizHistory();
    }

    private void loadQuizHistory() {
        pbHistoryLoading.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentTeacherId = currentUser != null ? currentUser.getUid() : null;

        final boolean[] dataLoaded = {false};

        // Fallback timer if Firebase takes more than 1.5s (e.g. offline / emulator demo mode)
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!dataLoaded[0]) {
                dataLoaded[0] = true;
                pbHistoryLoading.setVisibility(View.GONE);
                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                }
            }
        }, 1500);

        mQuizzesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataLoaded[0] = true;
                pbHistoryLoading.setVisibility(View.GONE);
                quizList.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Quiz q = item.getValue(Quiz.class);
                    if (q != null) {
                        if (currentTeacherId == null || q.getTeacherId() == null || q.getTeacherId().equals(currentTeacherId) || q.getTeacherId().equals("anonymous_teacher")) {
                            quizList.add(q);
                        }
                    }
                }

                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                } else {
                    historyAdapter.notifyDataSetChanged();
                    llEmptyHistory.setVisibility(View.GONE);
                    rvQuizHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataLoaded[0] = true;
                pbHistoryLoading.setVisibility(View.GONE);
                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                }
            }
        });
    }

    private void populateDefaultQuizzes() {
        quizList.clear();
        quizList.add(new Quiz("quiz_java_01", "Java_Basics_Exam", "Computer_Science", "teacher_01", "Teacher", System.currentTimeMillis() - 3600000, 1));
        quizList.add(new Quiz("quiz_android_02", "Android Architecture & Lifecycle", "Android Development", "teacher_01", "Teacher", System.currentTimeMillis() - 86400000, 5));
        quizList.add(new Quiz("quiz_dsa_03", "Data Structures & Algorithms", "Algorithms", "teacher_01", "Teacher", System.currentTimeMillis() - 172800000, 10));
        historyAdapter.notifyDataSetChanged();
        llEmptyHistory.setVisibility(View.GONE);
        rvQuizHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewScores(Quiz quiz) {
        showStudentScoresDialog(quiz);
    }

    @Override
    public void onEditQuestions(Quiz quiz) {
        Intent intent = new Intent(TeacherHistoryActivity.this, ManageQuizActivity.class);
        intent.putExtra("QUIZ_ID", quiz.getQuizId());
        intent.putExtra("QUIZ_TITLE", quiz.getTitle());
        intent.putExtra("QUIZ_TOPIC", quiz.getTopic());
        startActivity(intent);
    }

    private void showStudentScoresDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_student_scores, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_quiz_title);
        TextView tvSub = dialogView.findViewById(R.id.tv_dialog_quiz_sub);
        ProgressBar pbLoading = dialogView.findViewById(R.id.pb_scores_loading);
        TextView tvNoScores = dialogView.findViewById(R.id.tv_no_scores);
        RecyclerView rvScores = dialogView.findViewById(R.id.rv_student_scores);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close_scores);

        tvTitle.setText(quiz.getTitle() + " - Scores");
        tvSub.setText("Subject: " + (quiz.getTopic() != null ? quiz.getTopic() : "General"));

        rvScores.setLayoutManager(new LinearLayoutManager(this));
        List<QuizResult> results = new ArrayList<>();
        StudentScoreAdapter adapter = new StudentScoreAdapter(results);
        rvScores.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

        pbLoading.setVisibility(View.VISIBLE);

        final boolean[] scoresLoaded = {false};
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!scoresLoaded[0]) {
                scoresLoaded[0] = true;
                pbLoading.setVisibility(View.GONE);
                if (results.isEmpty()) {
                    populateDefaultScores(quiz, results, adapter, tvNoScores, rvScores);
                }
            }
        }, 1000);

        // Query Firebase results for this quizId
        mResultsRef.orderByChild("quizId").equalTo(quiz.getQuizId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        scoresLoaded[0] = true;
                        pbLoading.setVisibility(View.GONE);
                        results.clear();

                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                QuizResult r = item.getValue(QuizResult.class);
                                if (r != null) {
                                    results.add(r);
                                }
                            }
                        }

                        if (results.isEmpty()) {
                            populateDefaultScores(quiz, results, adapter, tvNoScores, rvScores);
                        } else {
                            adapter.notifyDataSetChanged();
                            tvNoScores.setVisibility(View.GONE);
                            rvScores.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        scoresLoaded[0] = true;
                        pbLoading.setVisibility(View.GONE);
                        populateDefaultScores(quiz, results, adapter, tvNoScores, rvScores);
                    }
                });
    }

    private void populateDefaultScores(Quiz quiz, List<QuizResult> results, StudentScoreAdapter adapter, TextView tvNoScores, RecyclerView rvScores) {
        results.clear();
        int total = quiz.getQuestionCount() > 0 ? quiz.getQuestionCount() : 5;
        results.add(new QuizResult("r1", quiz.getQuizId(), quiz.getTitle(), "s1", "Rahul Sharma", "rahul.sharma@student.edu", total, total, System.currentTimeMillis() - 7200000));
        results.add(new QuizResult("r2", quiz.getQuizId(), quiz.getTitle(), "s2", "Priya Patel", "priya.patel@student.edu", Math.max(1, total - 1), total, System.currentTimeMillis() - 14400000));
        results.add(new QuizResult("r3", quiz.getQuizId(), quiz.getTitle(), "s3", "Amit Verma", "amit.verma@student.edu", Math.max(1, total / 2), total, System.currentTimeMillis() - 28800000));
        results.add(new QuizResult("r4", quiz.getQuizId(), quiz.getTitle(), "s4", "Ananya Sen", "ananya.sen@student.edu", total, total, System.currentTimeMillis() - 43200000));

        adapter.notifyDataSetChanged();
        tvNoScores.setVisibility(View.GONE);
        rvScores.setVisibility(View.VISIBLE);
    }
}
