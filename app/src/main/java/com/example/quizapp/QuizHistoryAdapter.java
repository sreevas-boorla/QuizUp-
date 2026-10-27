package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.QuizHistoryViewHolder> {

    public interface OnQuizHistoryClickListener {
        void onViewScores(Quiz quiz);
        void onEditQuestions(Quiz quiz);
    }

    private final List<Quiz> quizList;
    private final OnQuizHistoryClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public QuizHistoryAdapter(List<Quiz> quizList, OnQuizHistoryClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_history, parent, false);
        return new QuizHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizHistoryViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.tvQuizTitle.setText(quiz.getTitle());
        holder.tvQuizTopic.setText(quiz.getTopic() != null ? quiz.getTopic() : "General");
        int count = quiz.getQuestionCount();
        holder.tvQuestionCount.setText(count + (count == 1 ? " Question" : " Questions"));
        holder.tvCreatedDate.setText("Created on: " + dateFormat.format(new Date(quiz.getTimestamp() > 0 ? quiz.getTimestamp() : System.currentTimeMillis())));

        holder.btnViewScores.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewScores(quiz);
            }
        });

        holder.btnEditQuestions.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditQuestions(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList != null ? quizList.size() : 0;
    }

    static class QuizHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle, tvQuizTopic, tvQuestionCount, tvCreatedDate;
        MaterialButton btnViewScores, btnEditQuestions;

        public QuizHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_history_quiz_title);
            tvQuizTopic = itemView.findViewById(R.id.tv_history_quiz_topic);
            tvQuestionCount = itemView.findViewById(R.id.tv_history_question_count);
            tvCreatedDate = itemView.findViewById(R.id.tv_history_created_date);
            btnViewScores = itemView.findViewById(R.id.btn_view_scores);
            btnEditQuestions = itemView.findViewById(R.id.btn_edit_questions);
        }
    }
}
