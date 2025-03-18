package com.example.tutoriapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class DashboardFragment extends Fragment {
    private SharedPreferences dashboardPreferences;
    private SharedPreferences.Editor dashboardPreferencesEditor;
    private static final String DASHBOARD_PREFERENCES_NAME = "DashboardPreference";

    private static final String MODULE_ONE_IMAGE_KEY = "ModuleOneImage";
    private static final String MODULE_TWO_IMAGE_KEY = "ModuleTwoImage";
    private static final String MODULE_THREE_IMAGE_KEY = "ModuleThreeImage";

    private static final String MODULE_ONE_ENABLED_KEY = "ModuleOneEnabled";
    private static final String MODULE_TWO_ENABLED_KEY = "ModuleTwoEnabled";
    private static final String MODULE_THREE_ENABLED_KEY = "ModuleThreeEnabled";

    private ImageButton moduleOneImageButton, moduleTwoImageButton, moduleThreeImageButton;
    private CardView moduleZeroCard, moduleOneCard, moduleTwoCard, moduleThreeCard;
    private ProgressBar moduleZeroProg, moduleOneProg, moduleTwoProg, moduleThreeProg;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        intent = new Intent(getActivity(), ModuleActivity.class);

        // Initialize SharedPreferences
        dashboardPreferences = requireActivity().getSharedPreferences(DASHBOARD_PREFERENCES_NAME, Context.MODE_PRIVATE);
        dashboardPreferencesEditor = dashboardPreferences.edit();

        dashboardPreferencesEditor.putBoolean("isModuleOneFinalAssessmentComplete", true);
        dashboardPreferencesEditor.putBoolean("isModuleTwoFinalAssessmentComplete", false);
        dashboardPreferencesEditor.putBoolean("isModuleThreeFinalAssessmentComplete", false);

        dashboardPreferencesEditor.apply();

        // Initialize buttons
        moduleOneImageButton = view.findViewById(R.id.dashboard_image_module_1_button_start);
        moduleTwoImageButton = view.findViewById(R.id.dashboard_image_module_2_button_start);
        moduleThreeImageButton = view.findViewById(R.id.dashboard_image_module_3_button_start);

        // Initialize each module card
        moduleZeroCard = view.findViewById(R.id.dashboard_card_view_module_0);
        moduleOneCard = view.findViewById(R.id.dashboard_card_view_module_1);
        moduleTwoCard = view.findViewById(R.id.dashboard_card_view_module_2);
        moduleThreeCard = view.findViewById(R.id.dashboard_card_view_module_3);

        moduleZeroProg = view.findViewById(R.id.dashboard_progress_bar_module_0);
        moduleOneProg = view.findViewById(R.id.dashboard_progress_bar_module_1);
        moduleTwoProg = view.findViewById(R.id.dashboard_progress_bar_module_2);
        moduleThreeProg = view.findViewById(R.id.dashboard_progress_bar_module_3);

        // Restore saved button images
        moduleOneImageButton.setImageResource(dashboardPreferences.getInt(MODULE_ONE_IMAGE_KEY, R.drawable.module_padlock_icon));
        moduleTwoImageButton.setImageResource(dashboardPreferences.getInt(MODULE_TWO_IMAGE_KEY, R.drawable.module_padlock_icon));
        moduleThreeImageButton.setImageResource(dashboardPreferences.getInt(MODULE_THREE_IMAGE_KEY, R.drawable.module_padlock_icon));

        moduleZeroCard.setOnClickListener(v -> {
            intent.putExtra("moduleNumber", "Module 0");
            startActivity(intent);
        });

        moduleOneCard.setOnClickListener(v -> {
            intent.putExtra("moduleNumber", "Module 1");
            startActivity(intent);
        });

        moduleTwoCard.setOnClickListener(v -> {
            intent.putExtra("moduleNumber", "Module 2");
            startActivity(intent);
        });

        moduleThreeCard.setOnClickListener(v -> {
            intent.putExtra("moduleNumber", "Module 3");
            startActivity(intent);
        });


        // Restore saved enabled states
        moduleOneCard.setClickable(dashboardPreferences.getBoolean("ModuleOneUnlocked", false));
        moduleOneCard.setFocusable(dashboardPreferences.getBoolean("ModuleOneUnlocked", false));

        moduleTwoCard.setClickable(dashboardPreferences.getBoolean("ModuleTwoUnlocked", false));
        moduleTwoCard.setFocusable(dashboardPreferences.getBoolean("ModuleTwoUnlocked", false));

        moduleThreeCard.setClickable(dashboardPreferences.getBoolean("ModuleThreeUnlocked", false));
        moduleThreeCard.setFocusable(dashboardPreferences.getBoolean("ModuleThreeUnlocked", false));

        moduleOneCard.setAlpha(dashboardPreferences.getFloat("ModuleOneAlpha", 0.5f));
        moduleTwoCard.setAlpha(dashboardPreferences.getFloat("ModuleTwoAlpha", 0.5f));
        moduleThreeCard.setAlpha(dashboardPreferences.getFloat("ModuleThreeAlpha", 0.5f));

        return view;
    }

    private void enableModuleCard(ConstraintLayout moduleCard, String preferenceKey) {
        moduleCard.setClickable(true);
        moduleCard.setFocusable(true);
        dashboardPreferencesEditor.putBoolean(preferenceKey, true);
    }
}


