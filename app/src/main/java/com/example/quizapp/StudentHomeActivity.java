package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity implements AvailableQuizAdapter.OnQuizClickListener {

    private Toolbar toolbar;
    private TextView tvStudentGreeting, tvAvailableCountBadge;
    private MaterialButton btnProgressHistory;
    private TextInputEditText etSearchQuizzes;
    private ProgressBar pbLoading;
    private RecyclerView rvAvailableQuizzes;
    private LinearLayout llEmptyQuizzes;
    private ImageButton btnLogout;

    private AvailableQuizAdapter quizAdapter;
    private final List<Quiz> quizList = new ArrayList<>();

    private DatabaseReference mQuizzesRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // App-Bar Toolbar
        toolbar = findViewById(R.id.student_home_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student's Portal");
        }

        // Bind Views
        tvStudentGreeting = findViewById(R.id.tv_student_name_greeting);
        tvAvailableCountBadge = findViewById(R.id.tv_available_count_badge);
        btnProgressHistory = findViewById(R.id.btn_action_progress_history);
        etSearchQuizzes = findViewById(R.id.et_search_quizzes);
        pbLoading = findViewById(R.id.pb_quizzes_loading);
        rvAvailableQuizzes = findViewById(R.id.rv_available_quizzes);
        llEmptyQuizzes = findViewById(R.id.ll_empty_quizzes);
        btnLogout = findViewById(R.id.btn_student_logout);

        mAuth = FirebaseAuth.getInstance();
        mQuizzesRef = FirebaseDatabase.getInstance().getReference("Quizzes");

        // Set user greeting
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String name = currentUser.getEmail().split("@")[0];
            tvStudentGreeting.setText("Welcome, " + name.substring(0, 1).toUpperCase() + name.substring(1) + "!");
        }

        // Logout action
        btnLogout.setOnClickListener(v -> performLogout());

        // Option 2: Progress History button
        btnProgressHistory.setOnClickListener(v -> {
            Intent intent = new Intent(StudentHomeActivity.this, StudentHistoryActivity.class);
            startActivity(intent);
        });

        // Option 1: Available Quizzes RecyclerView setup
        rvAvailableQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new AvailableQuizAdapter(quizList, this);
        rvAvailableQuizzes.setAdapter(quizAdapter);

        // Search feature with real-time TextWatcher
        setupSearchFeature();

        // Load quizzes
        loadAvailableQuizzes();
    }

    private void setupSearchFeature() {
        etSearchQuizzes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                quizAdapter.filter(s != null ? s.toString() : "");
                updateSearchState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSearchState() {
        int count = quizAdapter.getFilteredCount();
        tvAvailableCountBadge.setText(count + (count == 1 ? " Quiz Available" : " Quizzes Available"));
        if (count == 0) {
            llEmptyQuizzes.setVisibility(View.VISIBLE);
            rvAvailableQuizzes.setVisibility(View.GONE);
        } else {
            llEmptyQuizzes.setVisibility(View.GONE);
            rvAvailableQuizzes.setVisibility(View.VISIBLE);
        }
    }

    private void loadAvailableQuizzes() {
        pbLoading.setVisibility(View.VISIBLE);
        final boolean[] loaded = {false};

        // Fallback timer if Firebase is offline
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!loaded[0]) {
                loaded[0] = true;
                pbLoading.setVisibility(View.GONE);
                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                }
            }
        }, 1200);

        mQuizzesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loaded[0] = true;
                pbLoading.setVisibility(View.GONE);
                quizList.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Quiz q = item.getValue(Quiz.class);
                    if (q != null) {
                        quizList.add(q);
                    }
                }

                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                } else {
                    quizAdapter.updateList(quizList);
                    updateSearchState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loaded[0] = true;
                pbLoading.setVisibility(View.GONE);
                if (quizList.isEmpty()) {
                    populateDefaultQuizzes();
                }
            }
        });
    }

    private void populateDefaultQuizzes() {
        quizList.clear();
        quizList.add(new Quiz("quiz_java_01", "Java Basics Exam", "Computer Science", "teacher_01", "Teacher", System.currentTimeMillis() - 3600000, 5));
        quizList.add(new Quiz("quiz_android_02", "Android Architecture & Lifecycle", "Android Development", "teacher_01", "Teacher", System.currentTimeMillis() - 86400000, 5));
        quizList.add(new Quiz("quiz_dsa_03", "Data Structures & Algorithms", "Algorithms", "teacher_01", "Teacher", System.currentTimeMillis() - 172800000, 10));
        quizAdapter.updateList(quizList);
        updateSearchState();
    }

    @Override
    public void onTakeQuiz(Quiz quiz) {
        showQuizInstructionsDialog(quiz);
    }

    private void showQuizInstructionsDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_instructions, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_info_title);
        TextView tvTopic = dialogView.findViewById(R.id.tv_dialog_info_topic);
        TextView tvQuestions = dialogView.findViewById(R.id.tv_dialog_info_questions);
        TextView tvDuration = dialogView.findViewById(R.id.tv_dialog_info_duration);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel_quiz);
        MaterialButton btnStart = dialogView.findViewById(R.id.btn_dialog_start_quiz);

        tvTitle.setText(quiz.getTitle());
        tvTopic.setText("Subject: " + (quiz.getTopic() != null ? quiz.getTopic() : "General"));
        
        int count = quiz.getQuestionCount() > 0 ? quiz.getQuestionCount() : 5;
        tvQuestions.setText(count + (count == 1 ? " Question" : " Questions"));
        
        int totalSeconds = Math.max(30, count * 30);
        tvDuration.setText(totalSeconds >= 60 ? (totalSeconds / 60) + " Minutes (" + totalSeconds + "s)" : totalSeconds + " Seconds");

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnStart.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(StudentHomeActivity.this, StudentExamActivity.class);
            intent.putExtra("QUIZ_ID", quiz.getQuizId());
            intent.putExtra("QUIZ_TITLE", quiz.getTitle());
            intent.putExtra("QUIZ_TOPIC", quiz.getTopic());
            startActivity(intent);
        });
    }

    private void performLogout() {
        mAuth.signOut();
        Intent intent = new Intent(StudentHomeActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
