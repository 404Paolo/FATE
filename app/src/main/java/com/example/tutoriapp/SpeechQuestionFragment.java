package com.example.tutoriapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SpeechQuestionFragment extends Fragment {
    private static final String ARG_QUESTIONTEXT = "questionText";
    private static final String ARG_CORRECTANSWER = "correctAnswer";
    private static final String ARG_QUESTIONTYPE = "questionType";
    private static final String ARG_QUESTIONID = "questionId";
    private static final String ARG_SCORETHRESHOLD = "scoreThreshold";

    private String questionId, questionText, questionType, correctAnswer;
    private float scoreThreshold;

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

        TextView questionTextView = view.findViewById(R.id.questionText);
        TextView characterToPronounceTextView = view.findViewById(R.id.characterToPronounceText);
        ImageButton speakButton = view.findViewById(R.id.microphone_button);

        questionTextView.setText(questionText);
        characterToPronounceTextView.setText(correctAnswer);

        return view;
    }


}