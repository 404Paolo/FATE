package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    Dialog preAssessmentDialog;
    Button nextButton;
    TextView yiMessage;
    private static final String TAG = "MenuActivity";
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static int dialogPage = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int preAssessmentModuleOneScore = 0;
    int preAssessmentModuleTwoScore;
    int preAssessmentModuleThreeScore;
    int preAssessmentTotalScore;
    String message = "";
    boolean hasPerfectScore = false;
    DashboardFragment dashboardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        bottomNavigationView = findViewById(R.id.main_menu_bottom_navigation);
        customizeBottomNavigationBar();

        frameLayout = findViewById(R.id.main_menu_frame_layout);

//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
//        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        addFragment(new DashboardFragment(), "DASHBOARD_FRAGMENT");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuItemItemId = menuItem.getItemId();

                if (menuItemItemId == R.id.bottom_navigation_dashboard) {
                        addFragment(new DashboardFragment(), "DASHBOARD_FRAGMENT");
                } else if (menuItemItemId == R.id.bottom_navigation_performance) {
                        addFragment(new PerformanceFragment(), "PERFORMANCE_FRAGMENT");
                } else if (menuItemItemId == R.id.bottom_navigation_practice) {
                        addFragment(new PracticeFragment(), "PRACTICE_FRAGMENT");
                } else if (menuItemItemId == R.id.bottom_navigation_settings) {
                        addFragment(new SettingsFragment(), "SETTINGS_FRAGMENT");
                } else {
                        addFragment(new DashboardFragment(), "DASHBOARD_FRAGMENT");
                }
//                else if (menuItemItemId == R.id.bottom_navigation_leader_board) {
//                    addFragment(new LeaderboardFragment(), "LEADERBOARD_FRAGMENT");
//                }
                return true;
            }
        });
        preAssessmentChecker();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).
                setMessage("Are you sure you want to exit?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                }).
                setNegativeButton("No", null).
                show();
    }

    private void preAssessmentChecker() {
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Check if the key exists
        if (!sharedPreferences.contains("isPreAssessmentComplete")) {
            // Set the default value to false if the key doesn't exist

            editor.putBoolean("isPreAssessmentComplete", false);
            editor.apply();
        }
        // Retrieve the current value of isPreAssessmentComplete
        boolean isPreAssessmentComplete = sharedPreferences.getBoolean("isPreAssessmentComplete", false);

        if (!isPreAssessmentComplete) {
            Log.d(TAG, "No dialog displayed");
            sharedPreferences.edit().clear().apply();
        } else {
            Log.d(TAG, "Dialog displayed");

            initializeDialog();

            nextButton = preAssessmentDialog.findViewById(R.id.pre_assessment_button_next);
            yiMessage = preAssessmentDialog.findViewById(R.id.pre_assessment_dialog_welcome_text);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogPage == 1) {
                        String message1 = getString(R.string.pre_assessment_dialog_text_one) + " " + currentUser.getDisplayName();
                        yiMessage.setText(message1);
                    } else if (dialogPage == 2) {
                        yiMessage.setText(R.string.pre_assessment_dialog_text_two);
                    } else if(dialogPage == 3) {
                        yiMessage.setText(R.string.pre_assessment_dialog_text_three);
                    } else if(dialogPage == 4) {
                        preAssessmentDialog.dismiss();
//                        Intent toPreAssessmentActivity = new Intent(MenuActivity.this, AssessmentActivity.class);
//                        toPreAssessmentActivity.putExtra("moduleNumber", "Pre Assessment");
//                        startActivityForResult(toPreAssessmentActivity, 100);
                    } else if(dialogPage == 5) {
                        message += getString(R.string.pre_assessment_dialog_text_five) + "\n";
                        if (preAssessmentModuleOneScore == 8) {
                            message += "One  ";
                            hasPerfectScore = true;
//                            dashboardFragment.updateModuleButton("ModuleOne", R.drawable.dashboard_start_button);
                        }
                        if (preAssessmentModuleTwoScore == 5) {
                            message += "Two  ";
                            hasPerfectScore = true;

//                            dashboardFragment.updateModuleButton("ModuleTwo", R.drawable.dashboard_start_button);
                        }
                        if (preAssessmentModuleThreeScore == 5) {
                            message += "Three  ";
                            hasPerfectScore = true;

//                            dashboardFragment.updateModuleButton("ModuleThree", R.drawable.dashboard_start_button);
                        }
                        if (hasPerfectScore) {
                            yiMessage.setText(message);
                        } else {
                            message += "None";

                            yiMessage.setText(message);
                        }
                    } else if(dialogPage == 6) {
                        preAssessmentDialog.dismiss();
                    }
                    dialogPage++;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        editor.putBoolean("isPreAssessmentComplete", true);
        editor.apply();

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            HashMap<String, Integer> modulePoints = (HashMap<String, Integer>) data.getSerializableExtra("modulePoints");

            if (modulePoints != null) {
                for (int i = 0; i <= 7; i++) {
                    Integer value = modulePoints.get(Integer.toString(i));

                    if (value != null && value == 1) {
                        preAssessmentModuleOneScore++;
                    }
                }
                Log.d("MenuActivity", "Total Score for Module One: " + preAssessmentModuleOneScore);
            } else {
                Log.e("MenuActivity", "modulePoints is null!");
            }

            String congratulationMessage = getString(R.string.pre_assessment_dialog_text_four) + " " + currentUser.getDisplayName();
            yiMessage.setText(congratulationMessage);

            preAssessmentDialog.show();

            dashboardFragment = (DashboardFragment) getSupportFragmentManager().findFragmentById(R.id.main_menu_frame_layout);
        }
    }


    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_menu_frame_layout, fragment, tag);
        fragmentTransaction.commit();
    }

    private void customizeBottomNavigationBar() {
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.vivid_cerise)));
    }
    private void initializeDialog() {
        preAssessmentDialog = new Dialog(MenuActivity.this);
        preAssessmentDialog.setContentView(R.layout.custom_dialog_box);
        preAssessmentDialog.getWindow().setLayout(875, 450); // Custom width in pixels
        preAssessmentDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        preAssessmentDialog.setCancelable(false);
        preAssessmentDialog.show();
    }
}