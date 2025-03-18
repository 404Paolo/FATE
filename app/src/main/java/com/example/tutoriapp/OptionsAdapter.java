package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
    private final List<String> options;
    private final String correctAnswer;
    private Context context;
    private int selectedPosition = -1; // Track selected option
    private final OnOptionSelectedListener listener; // Listener to communicate with Fragment
    private SpeechEvaluator speechEvaluator;

    public interface OnOptionSelectedListener {
        void onOptionSelected(boolean isCorrect); // Notify if the correct answer is chosen
    }

    public OptionsAdapter(List<String> options, String correctAnswer, OnOptionSelectedListener listener) {
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        speechEvaluator = new SpeechEvaluator(context);
        String option = options.get(position);
        holder.optionText.setText(option);

        // Check if this is the selected position
        if (position == selectedPosition) {
            holder.optionText.setBackgroundResource(R.drawable.button_choices_selected_background);
            holder.optionText.setTextColor(Color.WHITE);
        } else {
            holder.optionText.setBackgroundResource(R.drawable.button_choices_background);
            holder.optionText.setTextColor(Color.BLACK);
        }

        // Handle click event
        holder.optionText.setOnClickListener(v -> {
            selectedPosition = position; // Update selected position
            boolean isCorrect = option.equals(correctAnswer); // Check answer
            listener.onOptionSelected(isCorrect); // Notify fragment
            notifyDataSetChanged(); // Refresh all items
        });
    }

    @Override
    public int getItemCount() {
        Log.d("OptionsAdapter", "Options count: " + options.size());
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView optionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.option_text);
        }
    }
}
