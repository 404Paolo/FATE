package com.example.tutoriapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.HashMap;
import java.util.Map;

public class PreAssessmentFragment extends Fragment {
    private TextView questionText, characterToPronounceText;
    private Button choice1, choice2, choice3, nextButton;
    private CardView speakingButton, characterToPronounceCard;
    private MediaPlayer mediaPlayer;
    private String correctAnswer;
    private static final int REQUEST_MICROPHONE = 1;

    private SpeechEvaluator speechEvaluator;
    private FirebaseUploader firebaseUploader;

    Map<String, String> buttonColorHistory = new HashMap<>();
    ImageButton microphoneButton;
    ImageView speakingIcon, checkIcon;
    ProgressBar loadingAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pre_assessment, container, false);

        questionText = view.findViewById(R.id.questionText);
        choice1 = view.findViewById(R.id.choiceOne);
        choice2 = view.findViewById(R.id.choiceTwo);
        choice3 = view.findViewById(R.id.choiceThree);
        speakingButton = view.findViewById(R.id.speakingButton);
        speakingIcon = view.findViewById(R.id.speakingIcon);
        characterToPronounceCard = view.findViewById(R.id.characterToPronounceCard);
        characterToPronounceText = view.findViewById(R.id.characterToPronounceText);
        microphoneButton = view.findViewById(R.id.microphone_button);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        checkIcon = view.findViewById(R.id.check_icon);

        firebaseUploader = new FirebaseUploader(getContext());


        nextButton = ((PreAssessmentActivity) getActivity()).buttonNext;
        speechEvaluator = new SpeechEvaluator(getContext());

        // Set up click listeners for the Button choices
        choice1.setOnClickListener(this::onChoiceButtonClicked);
        choice2.setOnClickListener(this::onChoiceButtonClicked);
        choice3.setOnClickListener(this::onChoiceButtonClicked);

        return view;
    }

    public void updateQuestion(Question question) {
        questionText.setText(question.getQuestionText());
        speakingButton.setVisibility(View.GONE); // Ensure it resets for each question

        if (question.getQuestionType().equals("conceptual") || question.getQuestionType().equals("listening")) {
            choice1.setText(question.getChoices()[0]);
            choice2.setText(question.getChoices()[1]);
            choice3.setText(question.getChoices()[2]);

            if (question.getAudioFile() != null) {
                speakingButton.setVisibility(View.VISIBLE);
                speakingButton.setOnClickListener(v -> {
                    speakingIcon.setVisibility(View.VISIBLE);
                    playAudio(question.getAudioFile());
                });
            }

            resetButtonColors();

            if (buttonColorHistory.containsKey(questionText.getText().toString())) {
                String selectedAnswer = buttonColorHistory.get(questionText.getText().toString());

                if (selectedAnswer.equals(choice1.getText().toString())) {
                    choice1.setBackgroundResource(R.drawable.custom_button_choices_background);
                } else if (selectedAnswer.equals(choice2.getText().toString())) {
                    choice2.setBackgroundResource(R.drawable.custom_button_choices_background);
                } else if (selectedAnswer.equals(choice3.getText().toString())) {
                    choice3.setBackgroundResource(R.drawable.custom_button_choices_background);
                }
            }

            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.VISIBLE);
            choice3.setVisibility(View.VISIBLE);

            correctAnswer = question.getCorrectAnswer();
        } else {
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
            choice3.setVisibility(View.GONE);
        }

        if (question.getQuestionType().equals("pronunciation")) {
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(Color.GRAY);
            String correctCharacter = question.getCharacterToPronounce().toString();

            characterToPronounceText.setText(correctCharacter);
            characterToPronounceCard.setVisibility(View.VISIBLE);
            microphoneButton.setVisibility(View.VISIBLE);

            microphoneButton.setOnClickListener(v -> {
                microphoneButton.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);

                //Use model to make prediction
                startRecording_Google();

                onSpeakButtonClicked();

                // Delay for 2 seconds, then hide loadingAnimation and show checkIcon
                new Handler().postDelayed(() -> {
                    loadingAnimation.setVisibility(View.GONE);
                    checkIcon.setVisibility(View.VISIBLE);

                    // Delay for another 2 seconds, then hide checkIcon and show microphoneButton
                    new Handler().postDelayed(() -> {
                        checkIcon.setVisibility(View.GONE);
                        microphoneButton.setVisibility(View.VISIBLE);
                    }, 2000);
                }, 2000);
            });
        } else {
            characterToPronounceCard.setVisibility(View.GONE);
            microphoneButton.setVisibility(View.GONE);
        }
    }

    private void playAudio(String audioFile) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        int resId = getResources().getIdentifier(audioFile, "raw", getActivity().getPackageName());

        if (resId == 0) {
            Log.e("AudioError", "Audio file not found: " + audioFile);
            Toast.makeText(getActivity(), "Audio file not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer = MediaPlayer.create(getActivity(), resId);

        if (mediaPlayer == null) {
            Log.e("AudioError", "MediaPlayer failed to create for: " + audioFile);
            Toast.makeText(getActivity(), "Audio playback failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        speakingButton.setEnabled(false);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            speakingIcon.setVisibility(View.INVISIBLE);
            speakingButton.setEnabled(true);
            mp.release();
            mediaPlayer = null;
        });
    }

    private void onSpeakButtonClicked() {
        if (getActivity() instanceof PreAssessmentActivity) {
            ((PreAssessmentActivity) getActivity()).onAnswerSelected(1);
        }
    }

    private void onChoiceButtonClicked(View v) {
        Button clickedButton = (Button) v;

        buttonColorHistory.put(questionText.getText().toString(), clickedButton.getText().toString());

        resetButtonColors();
        clickedButton.setBackgroundResource(R.drawable.custom_button_choices_background);

        int point = clickedButton.getText().toString().equals(correctAnswer) ? 1 : 0;

        if (getActivity() instanceof PreAssessmentActivity) {
            ((PreAssessmentActivity) getActivity()).onAnswerSelected(point);
        }
    }

    private void resetButtonColors() {
        choice1.setBackgroundResource(R.drawable.button_choices_background);
        choice2.setBackgroundResource(R.drawable.button_choices_background);
        choice3.setBackgroundResource(R.drawable.button_choices_background);
    }

    private void startRecording_Prinz() {
        firebaseUploader.audioRecordingToURL(new FirebaseUploader.UploadCallback() {
            @Override
            public void onSuccess(String fileUrl) {
                HuggingFaceClient client = new HuggingFaceClient();
                client.predict(fileUrl, new HuggingFaceClient.PredictionCallback() {
                    @Override
                    public void onSuccess(String prediction) {
                        questionText.setText("Prediction: "+prediction);

                        String correctCharacter = characterToPronounceText.getText().toString().trim();
                        getActivity().runOnUiThread(() -> {
                            if (prediction.contains(correctCharacter)) {
                                nextButton.setEnabled(true);
                                nextButton.setBackgroundColor(Color.parseColor("#4821a8"));
                                Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("PreAssessmentFragment", prediction);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("PreAssessmentFragment",error);
                        getActivity().runOnUiThread(()->{
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("PreAssessmentFragment",errorMessage);
            }
        });
    }

    private void startRecording_Google() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        }
        else {
            AudioRecorderHelper.startRecording(base64Audio ->
                    speechEvaluator.transcribeAudio(base64Audio, new SpeechEvaluator.OnTranscriptionCompleteListener() {
                        @Override
                        public void onTranscriptionComplete(String pinyinText, float confidence) {
                            questionText.setText("PinyinText: " + pinyinText + "  Confidence: " + confidence);

                            String correctCharacter = characterToPronounceText.getText().toString().trim();
                            getActivity().runOnUiThread(() -> {
                                if (pinyinText.contains(correctCharacter)) {
                                    nextButton.setEnabled(true);
                                    nextButton.setBackgroundColor(Color.parseColor("#4821a8"));
                                    Toast.makeText(getContext(), "Correct! Confidence: " + confidence, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("PreAssessmentFragment", error);
                            getActivity().runOnUiThread(()->{
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
            );
        }
    }
}




