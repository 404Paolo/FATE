package com.example.tutoriapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChoiceQuestionFragment extends Fragment {
    private static final String ARG_QUESTIONTEXT = "questionText";
    private static final String ARG_CORRECTANSWER = "correctAnswer";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_QUESTIONTYPE = "questionType";
    private static final String ARG_QUESTIONID = "questionId";

    private String questionId, questionText, questionType, correctAnswer;
    private CardView playAudioButton;
    private Question question;
    private List<String> options;
    private MediaPlayer mediaPlayer;

    public static ChoiceQuestionFragment newInstance(String questionId, String questionText, String questionType, String correctAnswer, List<String> options) {
        ChoiceQuestionFragment fragment = new ChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTIONID, questionId);
        args.putString(ARG_QUESTIONTEXT, questionText);
        args.putString(ARG_QUESTIONTYPE, questionType);
        args.putString(ARG_CORRECTANSWER, correctAnswer);
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(options));
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
            options = getArguments().getStringArrayList(ARG_OPTIONS);
            Log.d("QuestionFragment", "Options received: " + options.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choicequestion, container, false);

        TextView questionTextView = view.findViewById(R.id.question_text);
        RecyclerView optionsRecyclerView = view.findViewById(R.id.options_recycler);
        playAudioButton = view.findViewById(R.id.play_audio_button);

        if (questionType.equals("listening")) playAudioButton.setVisibility(View.VISIBLE);
        playAudioButton.setOnClickListener(v ->{

            String resourceName = correctAnswer; // Extract the actual name (without "R.raw.")
            int resId = getContext().getResources().getIdentifier(resourceName, "raw", getContext().getPackageName());

            mediaPlayer = MediaPlayer.create(getContext(), resId);
            mediaPlayer.start();
        });

        questionTextView.setText(questionText);

        // Set up RecyclerView with listener
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsRecyclerView.setAdapter(new OptionsAdapter(options, correctAnswer, this::onOptionSelected));

        return view;
    }

    private void onOptionSelected(boolean isCorrect) {
        if (getActivity() instanceof AssessmentActivity) {
            ((AssessmentActivity) getActivity()).setNextButtonEnabled(true, isCorrect);
        }
    }
}
