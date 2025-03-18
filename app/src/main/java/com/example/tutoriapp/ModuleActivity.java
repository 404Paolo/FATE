package com.example.tutoriapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ModuleActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    ModulePagerAdapter adapter;
    QuizPagerAdapter adapterQuiz;

    Button previousButton, nextButton;
    String moduleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        viewPager = findViewById(R.id.module_viewPager);

        moduleNumber = getIntent().getStringExtra("moduleNumber");

        ModulesContainer modulesContainer = loadJsonWithGson(this);
        Module module = modulesContainer.modules.get(moduleNumber);


        List<Page> pages = new ArrayList<>();
        pages.addAll(module.pages);

        adapter = new ModulePagerAdapter(this, pages);

        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        previousButton.setOnClickListener(v -> {previousPage();});
        nextButton.setOnClickListener(v -> {nextPage();});

    }


    private ModulesContainer loadJsonWithGson(Context context) {
        try {
            InputStream is = context.getAssets().open("modules.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            return gson.fromJson(reader, ModulesContainer.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void previousPage() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true); // Smooth scroll to previous page
        }
    }

    public void nextPage(){
        int nextItem = viewPager.getCurrentItem() + 1;

        if (nextItem < adapter.getItemCount()) {
            viewPager.setCurrentItem(nextItem);
        }

        else {
            Intent intent = new Intent(ModuleActivity.this, AssessmentActivity.class);
            intent.putExtra("moduleNumber", moduleNumber);
            startActivity(intent);
            finish();
        }
    }
}