package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttemptsAdapter extends RecyclerView.Adapter<AttemptsAdapter.ViewHolder> {
    private List<AttemptDoc> attemptDocs;
    private Context context;
    private OnAttemptClickListener listener;

    public interface OnAttemptClickListener {
        void onAttemptSelected(int index);
    }

    public AttemptsAdapter(List<AttemptDoc> attemptDocs, OnAttemptClickListener listener) {
        this.attemptDocs = attemptDocs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attempthistory, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttemptDoc attemptDoc = attemptDocs.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date(attemptDoc.timestamp));
        String score = String.format("%.2f", (attemptDoc.percentage * 100))+"%";
        holder.attemptNumberText.setText("Attempt "+(position + 1));
        holder.attemptDateText.setText(timestamp);
        holder.attemptScoreText.setText("Score: "+score);


        holder.linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int colorFrom = ContextCompat.getColor(v.getContext(), R.color.ghost_white);
                int colorTo = ContextCompat.getColor(v.getContext(), R.color.metallic_violet);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    animateColorChange(holder.linearLayout, colorFrom, colorTo);
                    holder.attemptNumberText.setTextColor(Color.WHITE);
                    holder.attemptDateText.setTextColor(Color.WHITE);
                    holder.attemptScoreText.setTextColor(Color.WHITE);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    animateColorChange(holder.linearLayout, colorTo, colorFrom);
                    holder.attemptNumberText.setTextColor(Color.BLACK);
                    holder.attemptDateText.setTextColor(Color.BLACK);
                    holder.attemptScoreText.setTextColor(Color.BLACK);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    if(listener != null){
                        Log.d("Paolo", "is not null");
                        listener.onAttemptSelected(holder.getAdapterPosition());
                    }
                }

                return true;
            }
        });
    }

    private void animateColorChange(View view, int colorFrom, int colorTo) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
        colorFade.setDuration(100);
        colorFade.start();
    }

    @Override
    public int getItemCount() {
        return attemptDocs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView attemptNumberText, attemptDateText, attemptScoreText;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            attemptDateText = itemView.findViewById(R.id.attemptDateText);
            attemptNumberText = itemView.findViewById(R.id.attemptNumberText);
            attemptScoreText = itemView.findViewById(R.id.attemptScoreText);
        }
    }
}
