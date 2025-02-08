package com.example.tutoriapp;

import android.content.Context;
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
    private ConstraintLayout moduleZeroCard, moduleOneCard, moduleTwoCard, moduleThreeCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize SharedPreferences
        dashboardPreferences = requireActivity().getSharedPreferences(DASHBOARD_PREFERENCES_NAME, Context.MODE_PRIVATE);
        dashboardPreferencesEditor = dashboardPreferences.edit();

        // Initialize buttons
        moduleOneImageButton = view.findViewById(R.id.dashboard_image_module_1_button_start);
        moduleTwoImageButton = view.findViewById(R.id.dashboard_image_module_2_button_start);
        moduleThreeImageButton = view.findViewById(R.id.dashboard_image_module_3_button_start);

        // Initialize each module card
        moduleZeroCard = view.findViewById(R.id.dashboard_constraint_layout_module_0);
        moduleOneCard = view.findViewById(R.id.dashboard_constraint_layout_module_1);
        moduleTwoCard = view.findViewById(R.id.dashboard_constraint_layout_module_2);
        moduleThreeCard = view.findViewById(R.id.dashboard_constraint_layout_module_3);

        // Restore saved button images
        moduleOneImageButton.setImageResource(dashboardPreferences.getInt(MODULE_ONE_IMAGE_KEY, R.drawable.module_padlock_icon));
        moduleTwoImageButton.setImageResource(dashboardPreferences.getInt(MODULE_TWO_IMAGE_KEY, R.drawable.module_padlock_icon));
        moduleThreeImageButton.setImageResource(dashboardPreferences.getInt(MODULE_THREE_IMAGE_KEY, R.drawable.module_padlock_icon));

        // Restore saved enabled states
        moduleOneCard.setClickable(dashboardPreferences.getBoolean(MODULE_ONE_ENABLED_KEY, false));
        moduleOneCard.setFocusable(dashboardPreferences.getBoolean(MODULE_ONE_ENABLED_KEY, false));

        moduleTwoCard.setClickable(dashboardPreferences.getBoolean(MODULE_TWO_ENABLED_KEY, false));
        moduleTwoCard.setFocusable(dashboardPreferences.getBoolean(MODULE_TWO_ENABLED_KEY, false));

        moduleThreeCard.setClickable(dashboardPreferences.getBoolean(MODULE_THREE_ENABLED_KEY, false));
        moduleThreeCard.setFocusable(dashboardPreferences.getBoolean(MODULE_THREE_ENABLED_KEY, false));

        moduleZeroCard.setOnClickListener(v -> {
            Log.d("DashboardFragment", "Hello from moduleZeroCard");
        });

        return view;
    }

    public void updateModuleButton(String moduleName, int imageResource) {
        switch (moduleName) {
            case "ModuleOne":
                moduleOneImageButton.setImageResource(imageResource);
                dashboardPreferencesEditor.putInt(MODULE_ONE_IMAGE_KEY, imageResource);
                enableModuleCard(moduleOneCard, MODULE_ONE_ENABLED_KEY);

                moduleOneCard.setOnClickListener(v -> {
                    Log.d("DashboardFragment", "Hello from moduleOneCard");
                });

                break;
            case "ModuleTwo":
                moduleTwoImageButton.setImageResource(imageResource);
                dashboardPreferencesEditor.putInt(MODULE_TWO_IMAGE_KEY, imageResource);
                enableModuleCard(moduleTwoCard, MODULE_TWO_ENABLED_KEY);

                moduleTwoCard.setOnClickListener(v -> {
                    Log.d("DashboardFragment", "Hello from moduleTwoCard");
                });

                break;
            case "ModuleThree":
                moduleThreeImageButton.setImageResource(imageResource);
                dashboardPreferencesEditor.putInt(MODULE_THREE_IMAGE_KEY, imageResource);
                enableModuleCard(moduleThreeCard, MODULE_THREE_ENABLED_KEY);

                moduleThreeCard.setOnClickListener(v -> {
                    Log.d("DashboardFragment", "Hello from moduleThreeCard");
                });

                break;
        }
        dashboardPreferencesEditor.apply(); // Save changes

        dashboardPreferencesEditor.clear();
        dashboardPreferencesEditor.apply();
    }

    private void enableModuleCard(ConstraintLayout moduleCard, String preferenceKey) {
        moduleCard.setClickable(true);
        moduleCard.setFocusable(true);
        dashboardPreferencesEditor.putBoolean(preferenceKey, true);
    }
}


