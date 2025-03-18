package com.example.tutoriapp;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InteractiveContentBlocksAdapter extends RecyclerView.Adapter<InteractiveContentBlocksAdapter.ViewHolder> {
    private final ArrayList<ContentBlock> contentBlocks;
    private Context context;
    private MediaPlayer mediaPlayer;
    public InteractiveContentBlocksAdapter(ArrayList<ContentBlock> contentBlocks, Context context){
        this.contentBlocks = contentBlocks;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interactivecontentblock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String charString = contentBlocks.get(position).header;
        String promptString = contentBlocks.get(position).content;

        holder.charButton.setText(Html.fromHtml(charString, Html.FROM_HTML_MODE_LEGACY));
        holder.prompt.setText(Html.fromHtml(promptString, Html.FROM_HTML_MODE_LEGACY));

        holder.charButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resourceName = contentBlocks.get(position).header; // Extract the actual name (without "R.raw.")
                int resId = context.getResources().getIdentifier(resourceName, "raw", context.getPackageName());

                mediaPlayer = MediaPlayer.create(context, resId);
                mediaPlayer.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentBlocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button charButton;
        TextView prompt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            charButton = itemView.findViewById(R.id.charButton);
            prompt = itemView.findViewById(R.id.prompt);
        }
    }
}
