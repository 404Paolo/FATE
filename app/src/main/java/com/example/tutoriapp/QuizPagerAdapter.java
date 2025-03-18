package com.example.tutoriapp;

import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuizPagerAdapter extends FragmentStateAdapter {
    private final List<ChoiceQuestionFragment> choiceQuestionFragments = new ArrayList<>();
    private final List<SpeechQuestionFragment> speechQuestionFragments = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    public QuizPagerAdapter(FragmentActivity activity, List<Question> questions) {
        super(activity);
        this.questions = questions;

        for(Question q : questions){
            if(q.questionType.equals("speaking")){
                speechQuestionFragments.add(SpeechQuestionFragment.newInstance(
                        q.questionId,
                        q.questionText,
                        q.questionType,
                        q.correctAnswer,
                        q.scoreThreshold
                ));
            }

            else{
                choiceQuestionFragments.add(ChoiceQuestionFragment.newInstance(
                        q.questionId,
                        q.questionText,
                        q.questionType,
                        q.correctAnswer,
                        q.options
                ));
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Question q = questions.get(position);
        if(q.questionType.equals("speaking")){
            return speechQuestionFragments.get(position - choiceQuestionFragments.size());
        }
        else{
            return choiceQuestionFragments.get(position);
        }
    }

    @Override
    public int getItemCount() {
        return this.questions.size();
    }
}
