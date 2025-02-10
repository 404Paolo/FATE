package com.example.tutoriapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PreAssessmentActivity extends AppCompatActivity {
    private QuestionManager questionManager;
    public Button buttonNext, buttonPrevious;
    PreAssessmentFragment preAssessmentFragment;
    Map<String, Integer> modulePoints = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_assessment);

        // Initialize variables
        preAssessmentFragment = new PreAssessmentFragment();
        questionManager = new QuestionManager();

        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);

        if (buttonNext == null || buttonPrevious == null) {
            Log.e("PreAssessmentActivity", "Buttons not found in layout");
            return;
        }

        // Begin fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, preAssessmentFragment);
        transaction.commit();

        // Refresh the current fragment
        new Handler(Looper.getMainLooper()).postDelayed(this::refreshCurrentFragment, 100);

        // Handle Next button click
        buttonNext.setOnClickListener(v -> {
            int currentIndex = questionManager.getCurrentIndex();

            // Ensure an answer is selected before proceeding
            if (modulePoints.get(Integer.toString(currentIndex)) == null) {
                if("conceptual".equals(questionManager.getCurrentQuestion().getQuestionType()) || "listening".equals(questionManager.getCurrentQuestion().getQuestionType())) {
                Toast.makeText(PreAssessmentActivity.this, "Please select an answer before proceeding", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PreAssessmentActivity.this, "Click the mic button and speak", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // Move to next question or finish if last one
            if (currentIndex < questionManager.getQuestions().size() - 1) {
                questionManager.getNextQuestion();
                refreshCurrentFragment();
            } else {
                finishAssessment();
            }
        });

        // Handle Previous button click
        buttonPrevious.setOnClickListener(v -> {
            questionManager.getPreviousQuestion();
            refreshCurrentFragment();
        });
    }

    private void switchQuestion(){

    }

    private void refreshCurrentFragment() {
        if (preAssessmentFragment != null) {
            preAssessmentFragment.updateQuestion(questionManager.getCurrentQuestion());
        } else {
            Log.e("PreAssessmentActivity", "Fragment is null, cannot update question");
        }

        if (questionManager.getCurrentIndex() == questionManager.getQuestions().size() - 1) {
            buttonNext.setText("Finish");
        } else {
            buttonNext.setText("Next");
        }
    }

    public void onAnswerSelected(int point) {
        int questionNumber = questionManager.getCurrentIndex();
        modulePoints.put(Integer.toString(questionNumber), point);
        Log.d("PreAssessmentActivity", "Module Points Map -> " + modulePoints);
    }

    private void finishAssessment() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("modulePoints", (Serializable) modulePoints);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}


