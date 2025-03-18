package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.ViewHolder> {
    private List<AttemptDoc> attemptDocs;
    private List<Map.Entry<String, Boolean>> questionMap;
    private List<Question> wrongQuestions;
    private String issueType;
    private View view;

    public IssuesAdapter(List<AttemptDoc> attemptDocs, List<Question> wrongQuestions, String issueType){
        this.attemptDocs = attemptDocs;
        this.wrongQuestions = wrongQuestions;
        this.issueType = issueType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Log.d("Paolo", issueType);
        if(issueType.equals("nonPron")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nonpronissues, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pronissues, parent, false);
        }


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position){
        Question q = wrongQuestions.get(position);
        if(issueType.equals("nonPron")){
            String issueText =  q.questionText + "<span style='color: red'><br>" + q.correctAnswer + "</span>";
            holder.issueText.setText(Html.fromHtml(issueText,Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            holder.issueText.setText(q.correctAnswer);
        }
    }

    @Override
    public int getItemCount(){
        return wrongQuestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView issueText;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            issueText = itemView.findViewById(R.id.issueText);
        }
    }
}
