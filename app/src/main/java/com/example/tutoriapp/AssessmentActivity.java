package com.example.tutoriapp;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class AssessmentActivity extends AppCompatActivity {
    public Button buttonNext;
    private ViewPager2 viewPager;
    private QuizPagerAdapter adapter;
    private boolean isCorrectSelected = false;
    private float percentScore;
    private Map<String, Integer> modulePoints = new HashMap<>();
    private String moduleNumber;
    private List<Question> questions = new ArrayList<>();
    private List<Question> correctQuestions = new ArrayList<>();
    private FirebaseUtil firebaseUploader;
    private Map<String, Boolean> questionScores = new HashMap<>();
    private int numCorrect = 0;
    private SpeechEvaluator speechEvaluator;
    private Context context;

    private static final int REQUEST_MICROPHONE = 1;
    private SharedPreferences dashboardPreferences;
    private SharedPreferences.Editor dashboardPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dashboardPreferences = this.getSharedPreferences("DashboardPreference", Context.MODE_PRIVATE);
        dashboardPreferencesEditor = dashboardPreferences.edit();

        getWindow().setStatusBarColor(Color.parseColor("#fcfaff"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        firebaseUploader = new FirebaseUtil(this);
        speechEvaluator = new SpeechEvaluator(this);
        context = getBaseContext();

        buttonNext = findViewById(R.id.button_next);
        viewPager = findViewById(R.id.view_pager);
        moduleNumber = getIntent().getStringExtra("moduleNumber");

        ModulesContainer modulesBank = loadJsonWithGson(this);
        Module module = modulesBank.modules.get(moduleNumber);
        questions.addAll(module.conceptual);
        questions.addAll(module.listening);
        questions.addAll(module.speaking);

        adapter = new QuizPagerAdapter(this, questions);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);
        buttonNext.setEnabled(false);
        buttonNext.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

        // Handle Next button click
        buttonNext.setOnClickListener(v -> {
            nextQuestion();
        });
    }

    public void nextQuestion() {
        if (isCorrectSelected) {
            questionScores.put(questions.get(viewPager.getCurrentItem()).questionId, true);
            correctQuestions.add(questions.get(viewPager.getCurrentItem()));
            numCorrect++;
        } else {
            questionScores.put(questions.get(viewPager.getCurrentItem()).questionId, false);
        }

        int nextItem = viewPager.getCurrentItem() + 1;
        if (nextItem < adapter.getItemCount()) {
            viewPager.setCurrentItem(nextItem);
            buttonNext.setEnabled(false);
            buttonNext.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
        else {
            percentScore = (float) numCorrect / adapter.getItemCount();
            finishAssessment();
            firebaseUploader.recordQuizAttempt(questionScores, percentScore, moduleNumber);
        }
    }

    private void finishAssessment() {
        Log.d("Paolo", Float.toString(percentScore));

        switch(moduleNumber){
            case "Module 0":
                dashboardPreferencesEditor.putFloat("ModuleZeroScore", percentScore);
                if(percentScore >= 0.7f) {
                    dashboardPreferencesEditor.putFloat("ModuleOneAlpha", 1.0f);
                    dashboardPreferencesEditor.putInt("ModuleOneImage", R.drawable.dashboard_start_button);
                    dashboardPreferencesEditor.putBoolean("ModuleOneUnlocked", true);
                    showDialog("Required score met! Module 1 is Unlocked");
                }

                else{
                    showDialog("Required score not met. Module 1 is still locked");
                }

                dashboardPreferencesEditor.apply();
                break;

            case "Module 1":
                dashboardPreferencesEditor.putFloat("ModuleOneScore", percentScore);
                if(percentScore >= 0.7f) {
                    dashboardPreferencesEditor.putFloat("ModuleTwoAlpha", 1.0f);
                    dashboardPreferencesEditor.putInt("ModuleTwoImage", R.drawable.dashboard_start_button);
                    dashboardPreferencesEditor.putBoolean("ModuleTwoUnlocked", true);
                    showDialog("Required score met! Module 2 is Unlocked");
                }
                else {
                    showDialog("Required score not met. Module 2 is still locked");
                }
                dashboardPreferencesEditor.apply();
                break;
            case "Module 2":
                dashboardPreferencesEditor.putFloat("ModuleTwoScore", percentScore);
                if(percentScore >= 0.7f) {
                    dashboardPreferencesEditor.putFloat("ModuleThreeAlpha", 1.0f);
                    dashboardPreferencesEditor.putInt("ModuleThreeImage", R.drawable.dashboard_start_button);
                    dashboardPreferencesEditor.putBoolean("ModuleThreeLocked", true);
                    showDialog("Required score met. Module 3 Unlocked");
                }
                else {
                    showDialog("Required score not met. Module 3 is still locked");
                }
                dashboardPreferencesEditor.apply();
                break;
            case "Module 3":
                dashboardPreferencesEditor.putFloat("ModuleThreeScore", percentScore);
                dashboardPreferencesEditor.apply();
                break;
            case "Pre Assessment":
                dashboardPreferencesEditor.putFloat("ModuleZeroScore", percentScore);

                if(percentScore >= 0.7f) {
                    dashboardPreferencesEditor.putFloat("ModuleOneAlpha", 1.0f);
                    dashboardPreferencesEditor.putInt("ModuleOneImage", R.drawable.dashboard_start_button);
                }

                dashboardPreferencesEditor.apply();
                break;
        }
    }

    public void setNextButtonEnabled(boolean enabled, boolean isCorrect) {

        buttonNext.setEnabled(enabled);
        buttonNext.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#651384")));
        isCorrectSelected = isCorrect;
    }

    private ModulesContainer loadJsonWithGson(Context context) {
        try {
            // Read JSON file
            InputStream is = context.getAssets().open("modules.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            // Parse JSON
            Gson gson = new Gson();
            return gson.fromJson(reader, ModulesContainer.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button is disabled while taking assessment", Toast.LENGTH_SHORT).show();
    }

    public void checkPreassessment(){
        List<Question> questionsM1 = new ArrayList<>();
        List<Question> questionsM2 = new ArrayList<>();
        List<Question> questionsM3 = new ArrayList<>();
        List<String> unlockedModules = new ArrayList<>();

        for(Question q : correctQuestions){
            switch (q.fromModule){
                case "m1" : questionsM1.add(q); break;
                case "m2" : questionsM2.add(q); break;
                case "m3" : questionsM3.add(q); break;
            }
        }

        if(questionsM1.size() == 16){
            dashboardPreferencesEditor.putFloat("ModuleOneAlpha", 1.0f);
            dashboardPreferencesEditor.putInt("ModuleOneImage", R.drawable.dashboard_start_button);
            dashboardPreferencesEditor.putBoolean("ModuleOneUnlocked", true);
            unlockedModules.add("Module 1");
        }

        if(questionsM2.size() == 11){
            dashboardPreferencesEditor.putFloat("ModuleTwoAlpha", 1.0f);
            dashboardPreferencesEditor.putInt("ModuleTwoImage", R.drawable.dashboard_start_button);
            dashboardPreferencesEditor.putBoolean("ModuleTwoUnlocked", true);
            unlockedModules.add("Module 2");
        }

        if(questionsM2.size() == 13){
            dashboardPreferencesEditor.putFloat("ModuleTwoAlpha", 1.0f);
            dashboardPreferencesEditor.putInt("ModuleThreeUnlocked", R.drawable.dashboard_start_button);
            dashboardPreferencesEditor.putBoolean("ModuleThreeUnlocked", true);
            unlockedModules.add("Module 3");
        }
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_endofassessment, null);
        TextView messageText = dialogView.findViewById(R.id.messageText);
        Button buttonOk = dialogView.findViewById(R.id.btnOk);
        messageText.setText(message);


        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        messageText.setText(message);
        buttonOk.setOnClickListener(v -> {
            dialog.dismiss();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("modulePoints", (Serializable) modulePoints);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }
}


