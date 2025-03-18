package com.example.tutoriapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceActivity extends AppCompatActivity implements AttemptsBottomSheetFragment.OnDataChangedListener {
    CircularProgressIndicator progressIndicator;
    TextView progressText;
    TextView progressPercentage;
    TextView pronIssuesText;
    TextView nonPronIssuesText;
    RecyclerView pronIssuesRecycler;
    RecyclerView nonPronIssuesRecycler;
    ImageButton buttonAttemptHistory;
    List<Question> questions = new ArrayList<>();
    List<AttemptDoc> attemptDocs = new ArrayList<>();
    List<ModuleDoc> moduleDocs = new ArrayList<>();
    ModuleDoc moduleDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        int moduleIndex = getIntent().getIntExtra("moduleIndex", 1);
        setContentView(R.layout.activity_performance);
        progressText = findViewById(R.id.progressText);
        progressIndicator = findViewById(R.id.progressIndicator);
        progressPercentage = findViewById(R.id.progressPercentage);
        pronIssuesText = findViewById(R.id.pronunciationIssuesText);
        nonPronIssuesText = findViewById(R.id.nonPronunciationIssuesText);
        pronIssuesRecycler = findViewById(R.id.pronunciationIssues_recycler);
        nonPronIssuesRecycler = findViewById(R.id.nonPronunciationIssues_recycler);
        buttonAttemptHistory = findViewById(R.id.button_attemptHistory);

        moduleDocs = FirebaseUtil.loadPerformanceData(this);
        moduleDoc = moduleDocs.get(moduleIndex);
        ModulesContainer modulesContainer = loadJsonWithGson(this);
        Module module = modulesContainer.modules.get(moduleDoc.moduleName);
        questions.addAll(module.conceptual);
        questions.addAll(module.speaking);
        questions.addAll(module.listening);

        attemptDocs = moduleDoc.attempts;
        AttemptDoc attempt = attemptDocs.get(attemptDocs.size() - 1);

        showAdapters(attempt);

        //Attempt History Stuff
        buttonAttemptHistory.setOnClickListener(v -> {
            AttemptsBottomSheetFragment bottomSheet = AttemptsBottomSheetFragment.newInstance(attemptDocs);
            bottomSheet.setOnDataChangedListener(this);
            bottomSheet.show(getSupportFragmentManager(), "AttemptHistoryBottomSheet");
        });
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

    private Question findQuestionById(String id) {
        return questions.stream()
                .filter(q -> q.questionId.equals(id))
                .findFirst()
                .orElse(null); // Returns null if not found
    }

    public void showAdapters(AttemptDoc attempt){
        List<Question> pronWrongQuestions = new ArrayList<>();
        List<Question> nonPronWrongQuestions = new ArrayList<>();
        int attemptNo = attemptDocs.indexOf(attempt);

        for(Map.Entry<String, Boolean> question : attempt.questionMap.entrySet()){
            Question q = findQuestionById(question.getKey());

            if(!question.getValue() && q.questionType.equals("speaking")){
                pronWrongQuestions.add(q);
            }
            else if(!question.getValue()){
                nonPronWrongQuestions.add(q);
            }
        }

        nonPronIssuesText.setText("Non-Pronunciation Issues ("+nonPronWrongQuestions.size()+")");
        IssuesAdapter nonPronIssuesAdapter = new IssuesAdapter(attemptDocs, nonPronWrongQuestions, "nonPron");
        nonPronIssuesRecycler.post(() -> {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxHeight = (int) (screenHeight * 0.25);

            ViewGroup.LayoutParams params = nonPronIssuesRecycler.getLayoutParams();
            params.height = maxHeight;
            nonPronIssuesRecycler.setLayoutParams(params);
        });
        nonPronIssuesRecycler.setAdapter(nonPronIssuesAdapter);

        pronIssuesText.setText("Pronunciation Issues ("+pronWrongQuestions.size()+")");
        IssuesAdapter pronIssuesAdapter = new IssuesAdapter(attemptDocs, pronWrongQuestions, "pron");
        pronIssuesRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        pronIssuesRecycler.post(() -> {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxHeight = (int) (screenHeight * 0.25);

            ViewGroup.LayoutParams params = pronIssuesRecycler.getLayoutParams();
            params.height = maxHeight;
            pronIssuesRecycler.setLayoutParams(params);
        });
        pronIssuesRecycler.setAdapter(pronIssuesAdapter);

        int attemptScore = ((int) (attempt.percentage * 100));
        progressText.setText("Your score for Attempt "+Integer.toString(attemptNo+1)+" of "+moduleDoc.moduleName);
        progressIndicator.setProgress(attemptScore, true);
        progressPercentage.setText(Integer.toString(attemptScore)+"%");
    }

    @Override
    public void onDataChanged(int index) {
        // Reload activity with new data
        AttemptDoc selectedAttempt = attemptDocs.get(index);
        Log.d("Paolo", selectedAttempt.attemptId);
        showAdapters(selectedAttempt);
    }
}