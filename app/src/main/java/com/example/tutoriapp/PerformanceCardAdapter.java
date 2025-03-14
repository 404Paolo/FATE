package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ModuleDocsAdapter extends RecyclerView.Adapter<ModuleDocsAdapter.ViewHolder> {
    private List<ModuleDoc> moduleDocs;

    public ModuleDocsAdapter(List<ModuleDoc> moduleDocs){
        this.moduleDocs = moduleDocs;
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

        holder.cardName.setText(moduleDoc.moduleName);
        holder.highestAttempt.setText("Kept Score(highest): "+Float.toString(moduleDoc.highestScore * 100)+"%");
        holder.latestAttempt.setText("Latest Attempt: "+Float.toString(moduleDoc.latestScore * 100)+"%");
        holder.averageScore.setText("Average: "+Float.toString(moduleDoc.averageScore * 100)+"%");

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
        }
    }
}
