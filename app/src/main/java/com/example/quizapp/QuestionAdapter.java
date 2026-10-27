package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public interface OnQuestionDeleteListener {
        void onDeleteQuestion(Question question, int position);
    }

    private final List<Question> questionList;
    private final OnQuestionDeleteListener deleteListener;

    public QuestionAdapter(List<Question> questionList, OnQuestionDeleteListener deleteListener) {
        this.questionList = questionList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questionList.get(position);
        holder.tvQuestionNumber.setText("Q" + (position + 1));
        holder.tvQuestionText.setText(q.getQuestionText());
        holder.tvOptionA.setText("A) " + q.getOptionA());
        holder.tvOptionB.setText("B) " + q.getOptionB());
        holder.tvOptionC.setText("C) " + q.getOptionC());
        holder.tvOptionD.setText("D) " + q.getOptionD());
        holder.tvCorrectAnswer.setText(q.getCorrectAnswerText());

        holder.btnDeleteQuestion.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteQuestion(q, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList != null ? questionList.size() : 0;
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionText;
        TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
        TextView tvCorrectAnswer;
        ImageButton btnDeleteQuestion;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvOptionA = itemView.findViewById(R.id.tv_option_a);
            tvOptionB = itemView.findViewById(R.id.tv_option_b);
            tvOptionC = itemView.findViewById(R.id.tv_option_c);
            tvOptionD = itemView.findViewById(R.id.tv_option_d);
            tvCorrectAnswer = itemView.findViewById(R.id.tv_correct_answer);
            btnDeleteQuestion = itemView.findViewById(R.id.btn_delete_question);
        }
    }
}
