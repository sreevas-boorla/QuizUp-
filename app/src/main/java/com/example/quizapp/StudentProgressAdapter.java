package com.example.quizapp;

import android.graphics.Color;
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

public class StudentProgressAdapter extends RecyclerView.Adapter<StudentProgressAdapter.ProgressViewHolder> {

    private final List<QuizResult> progressList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public StudentProgressAdapter(List<QuizResult> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        QuizResult result = progressList.get(position);
        holder.tvTitle.setText(result.getQuizTitle() != null ? result.getQuizTitle() : "Quiz Examination");
        holder.tvTopic.setText("Candidate: " + (result.getStudentName() != null ? result.getStudentName() : "Student"));
        holder.tvScoreBadge.setText(result.getScore() + " / " + result.getTotal());

        int percentage = result.getTotal() > 0 ? (int) Math.round(((double) result.getScore() / result.getTotal()) * 100) : 0;
        boolean passed = percentage >= 60;

        if (passed) {
            holder.tvStatusBadge.setText("PASSED (" + percentage + "%)");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#17890F"));
        } else {
            holder.tvStatusBadge.setText("NEEDS RETEST (" + percentage + "%)");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#E65100"));
        }

        long time = result.getTimestamp() > 0 ? result.getTimestamp() : System.currentTimeMillis();
        holder.tvDate.setText("Attended: " + dateFormat.format(new Date(time)));
    }

    @Override
    public int getItemCount() {
        return progressList != null ? progressList.size() : 0;
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTopic, tvScoreBadge, tvStatusBadge, tvDate;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_progress_quiz_title);
            tvTopic = itemView.findViewById(R.id.tv_progress_quiz_topic);
            tvScoreBadge = itemView.findViewById(R.id.tv_progress_score_badge);
            tvStatusBadge = itemView.findViewById(R.id.tv_progress_status_badge);
            tvDate = itemView.findViewById(R.id.tv_progress_date);
        }
    }
}
