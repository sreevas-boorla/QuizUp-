package com.example.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class StudentExamActivity extends AppCompatActivity {

    private String quizId, quizTitle, quizTopic;
    private TextView tvCountdownTimer, tvExamQuestionIndex, tvExamQuestionText, tvExamBadge;
    private ProgressBar pbExamCompletion;
    private MaterialButton btnOptionA, btnOptionB, btnOptionC, btnOptionD;

    private final List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;
    private boolean isFinished = false;

    private CountDownTimer countDownTimer;
    private long totalDurationMillis = 60000; // default 60s

    private DatabaseReference mQuizQuestionsRef;
    private DatabaseReference mResultsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exam);

        quizId = getIntent().getStringExtra("QUIZ_ID");
        quizTitle = getIntent().getStringExtra("QUIZ_TITLE");
        quizTopic = getIntent().getStringExtra("QUIZ_TOPIC");

        if (quizId == null) quizId = "quiz_java_01";
        if (quizTitle == null) quizTitle = "Java Basics Exam";
        if (quizTopic == null) quizTopic = "Computer Science";

        // Setup custom App-Bar
        Toolbar toolbar = findViewById(R.id.exam_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(quizTitle);
        }
        toolbar.setNavigationOnClickListener(v -> confirmExitExam());

        // Bind Views
        tvCountdownTimer = findViewById(R.id.tv_countdown_timer);
        tvExamQuestionIndex = findViewById(R.id.tv_exam_question_index);
        tvExamQuestionText = findViewById(R.id.tv_exam_question_text);
        tvExamBadge = findViewById(R.id.tv_exam_badge);
        pbExamCompletion = findViewById(R.id.pb_exam_completion);
        btnOptionA = findViewById(R.id.btn_option_a);
        btnOptionB = findViewById(R.id.btn_option_b);
        btnOptionC = findViewById(R.id.btn_option_c);
        btnOptionD = findViewById(R.id.btn_option_d);

        mAuth = FirebaseAuth.getInstance();
        mQuizQuestionsRef = FirebaseDatabase.getInstance().getReference("Quizzes").child(quizId).child("questions");
        mResultsRef = FirebaseDatabase.getInstance().getReference("Results");

        setupOptionListeners();
        loadQuizQuestions();
    }

    private void setupOptionListeners() {
        btnOptionA.setOnClickListener(v -> handleOptionSelection(1));
        btnOptionB.setOnClickListener(v -> handleOptionSelection(2));
        btnOptionC.setOnClickListener(v -> handleOptionSelection(3));
        btnOptionD.setOnClickListener(v -> handleOptionSelection(4));
    }

    private boolean isExamSessionStarted = false;

    private void loadQuizQuestions() {
        mQuizQuestionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isExamSessionStarted) {
                    return; // Prevent duplicate starts from late events
                }

                questionList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Question q = item.getValue(Question.class);
                        if (q != null) {
                            questionList.add(q);
                        }
                    }
                }

                if (questionList.isEmpty()) {
                    new AlertDialog.Builder(StudentExamActivity.this)
                            .setTitle("No Questions Available")
                            .setMessage("This quiz does not have any questions uploaded by the instructor yet.")
                            .setCancelable(false)
                            .setPositiveButton("Return", (d, w) -> finish())
                            .show();
                    return;
                }

                isExamSessionStarted = true;
                startExamSession();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isExamSessionStarted) return;
                new AlertDialog.Builder(StudentExamActivity.this)
                        .setTitle("Database Error")
                        .setMessage("Failed to load questions from Firebase: " + error.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (d, w) -> finish())
                        .show();
            }
        });
    }

    private void startExamSession() {
        totalQuestions = questionList.size();
        currentQuestionIndex = 0;
        score = 0;
        isFinished = false;

        // Allocate 30 seconds per question (min 30 seconds)
        totalDurationMillis = Math.max(30000, (long) totalQuestions * 30000);

        // Start CountDownTimer for back counting
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(totalDurationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long minutes = secondsRemaining / 60;
                long seconds = secondsRemaining % 60;
                tvCountdownTimer.setText(String.format("seconds remaining: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdownTimer.setText("Time Up!");
                finishExamSession(true);
            }
        }.start();

        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questionList.size()) {
            finishExamSession(false);
            return;
        }

        Question q = questionList.get(index);
        tvExamQuestionIndex.setText("Question " + (index + 1) + " of " + totalQuestions);
        tvExamBadge.setText("SUBJECT: " + quizTopic.toUpperCase());
        tvExamQuestionText.setText(q.getQuestionText());

        btnOptionA.setText("A) " + q.getOptionA());
        btnOptionB.setText("B) " + q.getOptionB());
        btnOptionC.setText("C) " + q.getOptionC());
        btnOptionD.setText("D) " + q.getOptionD());

        resetOptionStyles();

        // Update progress bar
        int progressPercent = (int) (((double) (index + 1) / totalQuestions) * 100);
        pbExamCompletion.setProgress(progressPercent);
    }

    private void resetOptionStyles() {
        int defaultColor = Color.parseColor("#3B345D");
        int defaultStroke = Color.parseColor("#CCCCCC");

        btnOptionA.setTextColor(defaultColor);
        btnOptionA.setStrokeColorResource(R.color.textColorSecondary);
        btnOptionB.setTextColor(defaultColor);
        btnOptionB.setStrokeColorResource(R.color.textColorSecondary);
        btnOptionC.setTextColor(defaultColor);
        btnOptionC.setStrokeColorResource(R.color.textColorSecondary);
        btnOptionD.setTextColor(defaultColor);
        btnOptionD.setStrokeColorResource(R.color.textColorSecondary);
    }

    private void handleOptionSelection(int selectedOption) {
        if (isFinished || currentQuestionIndex >= questionList.size()) {
            return;
        }

        Question currentQ = questionList.get(currentQuestionIndex);
        if (selectedOption == currentQ.getCorrectOption()) {
            score++;
        }

        // Advance to next question immediately
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion(currentQuestionIndex);
        } else {
            finishExamSession(false);
        }
    }

    private void finishExamSession(boolean timedOut) {
        if (isFinished) return;
        isFinished = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        FirebaseUser user = mAuth.getCurrentUser();
        String studentId = user != null ? user.getUid() : "student_demo";
        String studentName = user != null && user.getEmail() != null ? user.getEmail().split("@")[0] : "Student Candidate";
        String studentEmail = user != null && user.getEmail() != null ? user.getEmail() : "student@exam.com";

        String resultId = mResultsRef.push().getKey();
        if (resultId == null) {
            resultId = "res_" + System.currentTimeMillis();
        }

        QuizResult result = new QuizResult(resultId, quizId, quizTitle, studentId, studentName, studentEmail, score, totalQuestions, System.currentTimeMillis());

        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(StudentExamActivity.this);
        progressDialog.setMessage("Recording marks to Firebase...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String finalResultId = resultId;
        mResultsRef.child(resultId).setValue(result)
                .addOnSuccessListener(aVoid -> {
                    if (!isFinishing()) {
                        progressDialog.dismiss();
                        showResultDialog(timedOut, score, totalQuestions, true, null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isFinishing()) {
                        progressDialog.dismiss();
                        showResultDialog(timedOut, score, totalQuestions, false, e.getMessage());
                    }
                });
    }

    private void showResultDialog(boolean timedOut, int score, int total, boolean isSaved, String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exam_result, null);
        builder.setView(dialogView);

        TextView tvHeadline = dialogView.findViewById(R.id.tv_result_headline);
        TextView tvQuizName = dialogView.findViewById(R.id.tv_result_quiz_name);
        TextView tvScoreBig = dialogView.findViewById(R.id.tv_result_score_big);
        TextView tvPercentage = dialogView.findViewById(R.id.tv_result_percentage);
        TextView tvSummaryNote = dialogView.findViewById(R.id.tv_result_summary_note);
        ImageView ivIcon = dialogView.findViewById(R.id.iv_result_status_icon);
        MaterialButton btnHistory = dialogView.findViewById(R.id.btn_result_view_history);
        MaterialButton btnHome = dialogView.findViewById(R.id.btn_result_return_home);

        tvQuizName.setText(quizTitle + " (" + quizTopic + ")");
        tvScoreBig.setText(score + " / " + total);

        int percent = total > 0 ? (int) Math.round(((double) score / total) * 100) : 0;
        boolean passed = percent >= 60;

        if (!isSaved) {
            tvHeadline.setText("Save Warning!");
            tvHeadline.setTextColor(Color.parseColor("#D32F2F"));
            tvSummaryNote.setText("Result failed to record into Firebase (" + (errorMsg != null ? errorMsg : "Network error") + "). Please verify your connection.");
        } else if (timedOut) {
            tvHeadline.setText("Time Up! Test Submitted");
            tvHeadline.setTextColor(Color.parseColor("#D32F2F"));
            tvSummaryNote.setText("Test automatically closed because the countdown timer expired. Marks have been verified and saved to Firebase.");
        } else {
            tvHeadline.setText("Exam Completed!");
            tvHeadline.setTextColor(Color.parseColor("#3B345D"));
            tvSummaryNote.setText("All questions completed. Marks verified and recorded in Firebase progress history.");
        }

        if (passed) {
            tvPercentage.setText(percent + "% • PASSED");
            tvPercentage.setTextColor(Color.parseColor("#1EB113"));
            ivIcon.setColorFilter(Color.parseColor("#1EB113"));
        } else {
            tvPercentage.setText(percent + "% • NEEDS RETEST");
            tvPercentage.setTextColor(Color.parseColor("#E65100"));
            ivIcon.setColorFilter(Color.parseColor("#E65100"));
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        btnHistory.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(StudentExamActivity.this, StudentHistoryActivity.class);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
    }

    private void confirmExitExam() {
        new AlertDialog.Builder(this)
                .setTitle("Quit Quiz?")
                .setMessage("Are you sure you want to exit? Your exam progress will not be saved.")
                .setPositiveButton("QUIT", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish();
                })
                .setNegativeButton("CONTINUE", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
