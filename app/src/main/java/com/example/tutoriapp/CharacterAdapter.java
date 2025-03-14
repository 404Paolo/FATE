package com.example.tutoriapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.speech.RecognizerIntent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    private Context context;
    private List<CharacterModel> characterList;

    public CharacterAdapter(Context context, List<CharacterModel> characterList) {
        this.context = context;
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences dashboardPreferences = context.getSharedPreferences("DashboardPreference", Context.MODE_PRIVATE);
        boolean isModuleOneComplete = dashboardPreferences.getBoolean("isModuleOneFinalAssessmentComplete", false);
        boolean isModuleTwoComplete = dashboardPreferences.getBoolean("isModuleTwoFinalAssessmentComplete", false);
        boolean isModuleThreeComplete = dashboardPreferences.getBoolean("isModuleThreeFinalAssessmentComplete", false);

        CharacterModel character = characterList.get(position);
        holder.characterText.setText(character.getCharacter());

        boolean isClickable;

        if (character.getType().equals("initial") && isModuleOneComplete) {
            isClickable = true;
        } else if (character.getType().equals("final") && isModuleTwoComplete) {
            isClickable = true;
        } else if (character.getType().equals("tone") && isModuleThreeComplete) {
            isClickable = true;
        } else {
            isClickable = false;
        }

        // Reset the appearance of the card and text for all items
        holder.cardView.setEnabled(isClickable);
        holder.characterText.setAlpha(isClickable ? 1.0f : 0.3f);

        if (isClickable) {
            // Set appearance for clickable items
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lavender_indigo)); // Default card color
            holder.cardView.setOutlineAmbientShadowColor(ContextCompat.getColor(context, R.color.lavender_indigo)); // Default shadow color
            holder.cardView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.lavender_indigo)); // Default shadow color
            holder.characterText.setTextColor(ContextCompat.getColor(context, R.color.charcoal_gray)); // Default text color
        } else {
            // Set appearance for non-clickable items
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chinese_silver));
            holder.cardView.setOutlineAmbientShadowColor(ContextCompat.getColor(context, R.color.chinese_silver));
            holder.cardView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.chinese_silver));
            holder.characterText.setTextColor(ContextCompat.getColor(context, R.color.chinese_silver)); // Text color for non-clickable items
        }


        // ✅ Always set an OnClickListener but check inside the listener
        holder.characterText.setOnClickListener(v -> {
            if (isClickable) {
                showCharacterDialog(character.getCharacter(), character.getType());
            } else {
                showLockedDialog(character.getType());
            }
        });
    }


    private void showLockedDialog(String type) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_locked_character_custom_layout, null);

        TextView tvLockedTitle = dialogView.findViewById(R.id.tvLockedTitle);
        TextView tvLockedMessage = dialogView.findViewById(R.id.tvLockedMessage);
        Button btnBack = dialogView.findViewById(R.id.btnBack);

        tvLockedTitle.setText("Character Locked");

        String message = "You must complete ";
        if (type.equals("initial")) {
            message += "Module 1 to unlock it.";
        } else if (type.equals("final")) {
            message += "Module 2 to unlock it.";
        } else if (type.equals("tone")) {
            message += "Module 3 to unlock it.";
        }
        tvLockedMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView characterText;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            characterText = itemView.findViewById(R.id.characterText);
            cardView = itemView.findViewById((R.id.characterCard));
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d("RecyclerViewDebug", "Recycled ViewHolder: " + holder.getAdapterPosition());
    }

    private void showCharacterDialog(String character, String type) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_layout, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button btnDescription = dialogView.findViewById(R.id.btnDescription);
        Button btnVideo = dialogView.findViewById(R.id.btnVideo);
        Button btnSpeech = dialogView.findViewById(R.id.btnSpeech);

        // Style the character variable (bold, underline, orange color)
        SpannableString styledCharacter = new SpannableString(character);
        styledCharacter.setSpan(new UnderlineSpan(), 0, styledCharacter.length(), 0);
        styledCharacter.setSpan(new StyleSpan(Typeface.BOLD), 0, styledCharacter.length(), 0);
        styledCharacter.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), 0, styledCharacter.length(), 0);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Character: ").append(styledCharacter);

        dialogTitle.setText(builder);
        dialogMessage.setText("Choose an option:");

        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        builderDialog.setView(dialogView);
        AlertDialog characterDialog = builderDialog.create();

        if (characterDialog.getWindow() != null) {
            characterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnDescription.setOnClickListener(v -> {
            characterDialog.dismiss();
            fetchCharacterDescription(character, type, characterDialog);
        });

        btnVideo.setOnClickListener(v -> {
            characterDialog.dismiss();
            showVideoDialog(character, type);
        });

        btnSpeech.setOnClickListener(v -> {
            characterDialog.dismiss();
            showSpeechDialog(character, characterDialog);
        });

        characterDialog.show();
    }

    private void showVideoDialog(String character, String type) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_video_player, null);

        // Reference UI elements
        VideoView videoView = dialogView.findViewById(R.id.videoView);
        Button btnBack = dialogView.findViewById(R.id.btnBack);

        // Firebase Storage reference to the video
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference videoRef = storage.getReference().child("videos/" + type + "/" + character + ".mp4");

        // Fetch the video URL
        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(false); // Prevent infinite looping
                videoView.start();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to load video", Toast.LENGTH_SHORT).show();
        });

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Remove gray background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Back button to return to previous dialog
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            showCharacterDialog(character, type); // Reopen the previous dialog
        });

        dialog.show();
    }




    private void showSpeechDialog(String character, AlertDialog previousDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Speech Comparison");
        builder.setMessage("Start speech comparison for " + character + "?");
        builder.setPositiveButton("Start", (dialog, which) -> {
            dialog.dismiss();
            startSpeechComparison(character); // Call the function to start speech comparison
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            previousDialog.show(); // Reopen previous dialog
        });
        builder.show();
    }


    private void fetchCharacterDescription(String character, String type, AlertDialog previousDialog) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference descriptionRef = storage.getReference().child("descriptions/" + type + "/" + character + ".txt");

        descriptionRef.getBytes(1024 * 1024) // Max size: 1MB
                .addOnSuccessListener(bytes -> {
                    String description = new String(bytes, StandardCharsets.UTF_8);
                    showDescriptionDialog(character, description, previousDialog);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load description", Toast.LENGTH_SHORT).show();
                });
    }

    private void startSpeechComparison(String character) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Pronounce: " + character);

//        ((Activity) context).startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    private void showDescriptionDialog(String character, String description, AlertDialog previousDialog) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_description_custom_layout, null);

        // Reference UI elements
        TextView tvCharacterTitle = dialogView.findViewById(R.id.tvCharacterTitle);
        TextView tvCharacterDescription = dialogView.findViewById(R.id.tvCharacterDescription);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // Make character bold, underlined, and orange
        SpannableString boldUnderlineCharacter = new SpannableString(character);
        boldUnderlineCharacter.setSpan(new UnderlineSpan(), 0, boldUnderlineCharacter.length(), 0);
        boldUnderlineCharacter.setSpan(new StyleSpan(Typeface.BOLD), 0, boldUnderlineCharacter.length(), 0);
        boldUnderlineCharacter.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), 0, boldUnderlineCharacter.length(), 0);

        // Append " Description" normally
        spannableStringBuilder.append(boldUnderlineCharacter).append(" Description");

        // Set to TextView
        tvCharacterTitle.setText(spannableStringBuilder);
        tvCharacterDescription.setText(description);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Remove the gray background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            previousDialog.show(); // Reopen the previous dialog when exiting
        });

        dialog.show();
    }
}

