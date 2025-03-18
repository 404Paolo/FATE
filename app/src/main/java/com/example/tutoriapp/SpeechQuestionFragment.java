package com.example.tutoriapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SpeechQuestionFragment extends Fragment {
    private static final String ARG_QUESTIONTEXT = "questionText";
    private static final String ARG_CORRECTANSWER = "correctAnswer";
    private static final String ARG_QUESTIONTYPE = "questionType";
    private static final String ARG_QUESTIONID = "questionId";
    private static final String ARG_SCORETHRESHOLD = "scoreThreshold";

    private String questionId, questionText, questionType, correctAnswer;
    private float scoreThreshold;
    private static final int REQUEST_MICROPHONE = 1;
    private SpeechEvaluator speechEvaluator;
    private FirebaseUtil firebaseUploader;

    public static SpeechQuestionFragment newInstance(String questionId, String questionText, String questionType, String correctAnswer, float scoreThreshold) {
        SpeechQuestionFragment fragment = new SpeechQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTIONID, questionId);
        args.putString(ARG_QUESTIONTEXT, questionText);
        args.putString(ARG_QUESTIONTYPE, questionType);
        args.putString(ARG_CORRECTANSWER, correctAnswer);
        args.putFloat(ARG_SCORETHRESHOLD, scoreThreshold);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionId = getArguments().getString(ARG_QUESTIONID);
            questionText = getArguments().getString(ARG_QUESTIONTEXT);
            questionType = getArguments().getString(ARG_QUESTIONTYPE);
            correctAnswer = getArguments().getString(ARG_CORRECTANSWER);
            scoreThreshold = getArguments().getFloat(ARG_SCORETHRESHOLD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speechquestion, container, false);
        firebaseUploader = new FirebaseUtil(getContext());
        speechEvaluator = new SpeechEvaluator(getContext());

        TextView questionTextView = view.findViewById(R.id.questionText);
        TextView characterToPronounceTextView = view.findViewById(R.id.characterToPronounceText);
        ImageButton speakButton = view.findViewById(R.id.microphone_button);

        questionTextView.setText(questionText);
        characterToPronounceTextView.setText(correctAnswer);

        speakButton.setOnClickListener(v -> {
            startRecording_Google(correctAnswer);
        });

        return view;
    }

    // This method is called when an option is selected
    private void onOptionSelected(boolean isCorrect) {
        if (getActivity() instanceof AssessmentActivity) {
            ((AssessmentActivity) getActivity()).setNextButtonEnabled(true, isCorrect);
        }
    }

    private void startRecording_Google(String correctCharacter) {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        }
        else {
            AudioRecorderHelper.startRecording(base64Audio ->
                    speechEvaluator.transcribeAudio(base64Audio, new SpeechEvaluator.OnTranscriptionCompleteListener() {
                        @Override
                        public void onTranscriptionComplete(String pinyinText, float confidence) {
                            getActivity().runOnUiThread(() -> {
                                if (pinyinText.contains(correctCharacter)) {
                                    onOptionSelected(true);
                                    Log.d("SpeechQuestionFragment:" ,"User Correct: "+pinyinText);
                                } else {
                                    onOptionSelected(false);
                                    Log.d("SpeechQuestionFragment:" ,"User Wrong: "+pinyinText);
                                }

                                Toast.makeText(getContext(), "Pronounciation successfully recorded and evaluated", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            getActivity().runOnUiThread(()->{
                                Toast.makeText(getContext(), "Something went wrong while recording, please try again.", Toast.LENGTH_LONG).show();
                            });
                        }
                    })
            );
        }
    }
}