package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AvailableQuizAdapter extends RecyclerView.Adapter<AvailableQuizAdapter.QuizViewHolder> {

    public interface OnQuizClickListener {
        void onTakeQuiz(Quiz quiz);
    }

    private final List<Quiz> fullList;
    private final List<Quiz> filteredList;
    private final OnQuizClickListener listener;

    public AvailableQuizAdapter(List<Quiz> quizList, OnQuizClickListener listener) {
        this.fullList = new ArrayList<>(quizList != null ? quizList : new ArrayList<>());
        this.filteredList = new ArrayList<>(this.fullList);
        this.listener = listener;
    }

    public void updateList(List<Quiz> newList) {
        List<Quiz> copy = new ArrayList<>(newList != null ? newList : new ArrayList<>());
        fullList.clear();
        fullList.addAll(copy);
        filteredList.clear();
        filteredList.addAll(copy);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String lower = query.toLowerCase(Locale.getDefault()).trim();
            for (Quiz q : fullList) {
                String title = q.getTitle() != null ? q.getTitle().toLowerCase(Locale.getDefault()) : "";
                String topic = q.getTopic() != null ? q.getTopic().toLowerCase(Locale.getDefault()) : "";
                if (title.contains(lower) || topic.contains(lower)) {
                    filteredList.add(q);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return filteredList.size();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = filteredList.get(position);
        holder.tvTitle.setText(quiz.getTitle());
        holder.tvTopic.setText(quiz.getTopic() != null ? "Subject: " + quiz.getTopic() : "General");
        
        int count = quiz.getQuestionCount() > 0 ? quiz.getQuestionCount() : 5;
        holder.tvCount.setText(count + (count == 1 ? " Question" : " Questions"));
        
        // Estimated duration: ~30 seconds per question
        int totalSeconds = Math.max(30, count * 30);
        holder.tvDuration.setText("Timed: " + (totalSeconds >= 60 ? (totalSeconds / 60) + " min" : totalSeconds + " sec"));

        holder.btnTakeQuiz.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTakeQuiz(quiz);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTakeQuiz(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTopic, tvCount, tvDuration;
        MaterialButton btnTakeQuiz;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_available_quiz_title);
            tvTopic = itemView.findViewById(R.id.tv_available_quiz_topic);
            tvCount = itemView.findViewById(R.id.tv_available_question_count);
            tvDuration = itemView.findViewById(R.id.tv_available_quiz_duration);
            btnTakeQuiz = itemView.findViewById(R.id.btn_take_quiz);
        }
    }
}
