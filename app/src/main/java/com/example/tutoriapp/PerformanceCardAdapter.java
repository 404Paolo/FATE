package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PerformanceCardAdapter extends RecyclerView.Adapter<PerformanceCardAdapter.ViewHolder> {
    private List<ModuleDoc> moduleDocs;
    private Context context;

    public PerformanceCardAdapter(List<ModuleDoc> moduleDocs, Context context){
        this.moduleDocs = moduleDocs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modulecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position){
        //Treat as onCreate()
        ModuleDoc moduleDoc = moduleDocs.get(position);
        String highestAttemptStr = String.format("%.0f", (moduleDoc.highestScore * 100));
        String latestAttemptStr = String.format("%.0f", (moduleDoc.latestScore * 100));
        String averageScoreStr = String.format("%.2f", (moduleDoc.averageScore * 100));

        holder.cardName.setText(moduleDoc.moduleName);
        holder.highestAttempt.setText("Highest Score: "+highestAttemptStr+"%");
        holder.latestAttempt.setText("Latest Score: "+latestAttemptStr+"%");
        holder.averageScore.setText("Average: "+averageScoreStr+"%");


        holder.startButton.setOnClickListener(v -> {
            Intent intent = new Intent((Activity) context, PerformanceActivity.class);
            intent.putExtra("moduleIndex", moduleDocs.indexOf(moduleDoc));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return moduleDocs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName;
        TextView highestAttempt;
        TextView latestAttempt;
        TextView averageScore;
        ImageButton startButton;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cardName = itemView.findViewById(R.id.cardName);
            highestAttempt = itemView.findViewById(R.id.highestAttempt);
            latestAttempt = itemView.findViewById(R.id.latestAttempt);
            averageScore = itemView.findViewById(R.id.averageScore);
            startButton = itemView.findViewById(R.id.startButton);
        }
    }
}
