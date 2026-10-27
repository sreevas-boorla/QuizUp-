package com.example.quizapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageQuizActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionDeleteListener {

    private String quizId, quizTitle, quizTopic;
    private TextView tvManageQuizTitle, tvManageQuizTopic, tvTotalQuestionsBadge;
    private MaterialButton btnAddQuestion;
    private RecyclerView rvQuestions;
    private LinearLayout llEmptyQuestions;
    private ProgressBar pbManageLoading;

    private QuestionAdapter questionAdapter;
    private final List<Question> questionList = new ArrayList<>();

    private DatabaseReference mQuizRef;
    private DatabaseReference mQuestionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quiz);

        quizId = getIntent().getStringExtra("QUIZ_ID");
        quizTitle = getIntent().getStringExtra("QUIZ_TITLE");
        quizTopic = getIntent().getStringExtra("QUIZ_TOPIC");

        if (quizId == null) {
            quizId = "default_quiz";
        }
        if (quizTitle == null) {
            quizTitle = "Sample Quiz";
        }
        if (quizTopic == null) {
            quizTopic = "General";
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.manage_quiz_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(quizTitle);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind Views
        tvManageQuizTitle = findViewById(R.id.tv_manage_quiz_title);
        tvManageQuizTopic = findViewById(R.id.tv_manage_quiz_topic);
        tvTotalQuestionsBadge = findViewById(R.id.tv_total_questions_badge);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        rvQuestions = findViewById(R.id.rv_questions);
        llEmptyQuestions = findViewById(R.id.ll_empty_questions);
        pbManageLoading = findViewById(R.id.pb_manage_loading);

        tvManageQuizTitle.setText(quizTitle);
        tvManageQuizTopic.setText("Subject: " + quizTopic);

        // RecyclerView Setup
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionList, this);
        rvQuestions.setAdapter(questionAdapter);

        // Firebase References
        mQuizRef = FirebaseDatabase.getInstance().getReference("Quizzes").child(quizId);
        mQuestionsRef = mQuizRef.child("questions");

        // Load Questions from Firebase
        loadQuestionsFromFirebase();

        // Add Question Click
        btnAddQuestion.setOnClickListener(v -> showAddQuestionDialog());
    }

    private void loadQuestionsFromFirebase() {
        pbManageLoading.setVisibility(View.VISIBLE);

        mQuestionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbManageLoading.setVisibility(View.GONE);
                questionList.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Question q = item.getValue(Question.class);
                    if (q != null) {
                        questionList.add(q);
                    }
                }

                questionAdapter.notifyDataSetChanged();
                updateUIState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbManageLoading.setVisibility(View.GONE);
                Toast.makeText(ManageQuizActivity.this, "Error loading questions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIState() {
        int count = questionList.size();
        tvTotalQuestionsBadge.setText(count + (count == 1 ? " Question" : " Questions"));

        // Update questionCount in parent Quiz node
        mQuizRef.child("questionCount").setValue(count);

        if (count == 0) {
            llEmptyQuestions.setVisibility(View.VISIBLE);
            rvQuestions.setVisibility(View.GONE);
        } else {
            llEmptyQuestions.setVisibility(View.GONE);
            rvQuestions.setVisibility(View.VISIBLE);
        }
    }

    private void showAddQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_question, null);
        builder.setView(dialogView);

        TextInputEditText etQuestion = dialogView.findViewById(R.id.et_dialog_question);
        TextInputEditText etOptA = dialogView.findViewById(R.id.et_dialog_opt_a);
        TextInputEditText etOptB = dialogView.findViewById(R.id.et_dialog_opt_b);
        TextInputEditText etOptC = dialogView.findViewById(R.id.et_dialog_opt_c);
        TextInputEditText etOptD = dialogView.findViewById(R.id.et_dialog_opt_d);
        RadioGroup rgCorrect = dialogView.findViewById(R.id.rg_dialog_correct_option);

        builder.setPositiveButton("ADD", null); // Set null first to prevent auto-closing on validation error
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom validation on Positive Button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String questionText = etQuestion.getText() != null ? etQuestion.getText().toString().trim() : "";
            String optA = etOptA.getText() != null ? etOptA.getText().toString().trim() : "";
            String optB = etOptB.getText() != null ? etOptB.getText().toString().trim() : "";
            String optC = etOptC.getText() != null ? etOptC.getText().toString().trim() : "";
            String optD = etOptD.getText() != null ? etOptD.getText().toString().trim() : "";

            if (TextUtils.isEmpty(questionText)) {
                etQuestion.setError("Question text is required");
                return;
            }
            if (TextUtils.isEmpty(optA)) {
                etOptA.setError("Option A is required");
                return;
            }
            if (TextUtils.isEmpty(optB)) {
                etOptB.setError("Option B is required");
                return;
            }
            if (TextUtils.isEmpty(optC)) {
                etOptC.setError("Option C is required");
                return;
            }
            if (TextUtils.isEmpty(optD)) {
                etOptD.setError("Option D is required");
                return;
            }

            int correctOption = 1;
            int checkedId = rgCorrect.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_opt_b) {
                correctOption = 2;
            } else if (checkedId == R.id.rb_opt_c) {
                correctOption = 3;
            } else if (checkedId == R.id.rb_opt_d) {
                correctOption = 4;
            }

            String questionId = mQuestionsRef.push().getKey();
            if (questionId == null) {
                questionId = "q_" + System.currentTimeMillis();
            }

            Question newQuestion = new Question(questionId, questionText, optA, optB, optC, optD, correctOption);
            mQuestionsRef.child(questionId).setValue(newQuestion).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageQuizActivity.this, "Question added successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ManageQuizActivity.this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDeleteQuestion(Question question, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    mQuestionsRef.child(question.getQuestionId()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ManageQuizActivity.this, "Question deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ManageQuizActivity.this, "Delete failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}
