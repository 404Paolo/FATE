package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TextContentBlocksAdapter extends RecyclerView.Adapter<TextContentBlocksAdapter.ViewHolder> {
    private final ArrayList<ContentBlock> contentBlocks;
    public TextContentBlocksAdapter(ArrayList contentBlocks){
        this.contentBlocks = contentBlocks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textcontentblock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String headerString = contentBlocks.get(position).header;
        String contentString = contentBlocks.get(position).content;

        holder.header.setText(Html.fromHtml(headerString, Html.FROM_HTML_MODE_LEGACY));
        holder.content.setText(Html.fromHtml(contentString, Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() {
        return contentBlocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        TextView content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            content = itemView.findViewById(R.id.content);
        }
    }
}
