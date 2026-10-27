package com.example.quizapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvStatTotal, tvStatPassed, tvStatAccuracy;
    private ProgressBar pbLoading;
    private RecyclerView rvProgress;
    private LinearLayout llEmptyProgress;

    private StudentProgressAdapter adapter;
    private final List<QuizResult> progressList = new ArrayList<>();

    private DatabaseReference mResultsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);

        // App-Bar setup
        toolbar = findViewById(R.id.student_history_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Progress History");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind Views
        tvStatTotal = findViewById(R.id.tv_stat_total_tests);
        tvStatPassed = findViewById(R.id.tv_stat_passed);
        tvStatAccuracy = findViewById(R.id.tv_stat_avg_accuracy);
        pbLoading = findViewById(R.id.pb_progress_loading);
        rvProgress = findViewById(R.id.rv_student_progress);
        llEmptyProgress = findViewById(R.id.ll_empty_progress);

        rvProgress.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentProgressAdapter(progressList);
        rvProgress.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mResultsRef = FirebaseDatabase.getInstance().getReference("Results");

        loadStudentProgress();
    }

    private void loadStudentProgress() {
        pbLoading.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentStudentId = currentUser != null ? currentUser.getUid() : null;

        mResultsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbLoading.setVisibility(View.GONE);
                progressList.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    QuizResult r = item.getValue(QuizResult.class);
                    if (r != null) {
                        if (currentStudentId != null && currentStudentId.equals(r.getStudentId())) {
                            progressList.add(r);
                        }
                    }
                }

                Collections.reverse(progressList); // Most recent first
                updateUIWithResults();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(StudentHistoryActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                updateUIWithResults();
            }
        });
    }

    private void updateUIWithResults() {
        adapter.notifyDataSetChanged();

        if (progressList.isEmpty()) {
            llEmptyProgress.setVisibility(View.VISIBLE);
            rvProgress.setVisibility(View.GONE);
            tvStatTotal.setText("0");
            tvStatPassed.setText("0");
            tvStatAccuracy.setText("0%");
        } else {
            llEmptyProgress.setVisibility(View.GONE);
            rvProgress.setVisibility(View.VISIBLE);

            int totalTests = progressList.size();
            int passedTests = 0;
            double totalPercentage = 0;

            for (QuizResult r : progressList) {
                int percent = r.getTotal() > 0 ? (int) Math.round(((double) r.getScore() / r.getTotal()) * 100) : 0;
                totalPercentage += percent;
                if (percent >= 60) {
                    passedTests++;
                }
            }

            int avgAccuracy = totalTests > 0 ? (int) Math.round(totalPercentage / totalTests) : 0;

            tvStatTotal.setText(String.valueOf(totalTests));
            tvStatPassed.setText(String.valueOf(passedTests));
            tvStatAccuracy.setText(avgAccuracy + "%");
        }
    }
}
