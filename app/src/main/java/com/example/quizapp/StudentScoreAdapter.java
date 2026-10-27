package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentScoreAdapter extends RecyclerView.Adapter<StudentScoreAdapter.ScoreViewHolder> {

    private final List<QuizResult> scoreList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public StudentScoreAdapter(List<QuizResult> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        QuizResult result = scoreList.get(position);
        holder.tvStudentName.setText(result.getStudentName() != null ? result.getStudentName() : result.getStudentEmail());
        holder.tvDate.setText(dateFormat.format(new Date(result.getTimestamp() > 0 ? result.getTimestamp() : System.currentTimeMillis())));
        holder.tvScoreBadge.setText(result.getScore() + " / " + result.getTotal());
    }

    @Override
    public int getItemCount() {
        return scoreList != null ? scoreList.size() : 0;
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvDate, tvScoreBadge;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_score_student_name);
            tvDate = itemView.findViewById(R.id.tv_score_date);
            tvScoreBadge = itemView.findViewById(R.id.tv_score_badge);
        }
    }
}
