package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
    private final List<String> options;
    private final String correctAnswer;
    private int selectedPosition = -1; // Track selected option

    public OptionsAdapter(List<String> options, String correctAnswer) {
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String option = options.get(position);
        holder.optionText.setText(option);

        // Highlight selected answer
//        holder.optionText.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.WHITE);

        holder.optionText.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh UI to highlight selection
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
