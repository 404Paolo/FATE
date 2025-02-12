package com.example.tutoriapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreAssessmentActivity extends AppCompatActivity {
    public Button buttonNext;
    private ViewPager2 viewPager;
    private QuizPagerAdapter adapter;
    Map<String, Integer> modulePoints = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_assessment);

        buttonNext = findViewById(R.id.button_next);
        viewPager = findViewById(R.id.view_pager);


        ModulesContainer modulesBank = loadJsonWithGson(this);
        Module module1 = modulesBank.modules.get("module1");
        List<Question> questionsM1 = new ArrayList<>();

        for(Question q : module1.conceptual){questionsM1.add(q);}
        for(Question q : module1.listening){questionsM1.add(q);}
        for(Question q : module1.speaking){questionsM1.add(q);}

        adapter = new QuizPagerAdapter(this, questionsM1);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        // Handle Next button click
        buttonNext.setOnClickListener(v -> {
            nextQuestion();
        });
    }

    public void nextQuestion() {
        int nextItem = viewPager.getCurrentItem() + 1;
        if (nextItem < adapter.getItemCount()) {
            viewPager.setCurrentItem(nextItem);
        } else {
            finishAssessment();
        }
    }

    private void finishAssessment() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("modulePoints", (Serializable) modulePoints);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private ModulesContainer loadJsonWithGson(Context context) {
        try {
            // Read JSON file
            InputStream is = context.getAssets().open("assessmentQuestions.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            // Parse JSON
            Gson gson = new Gson();
            return gson.fromJson(reader, ModulesContainer.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


