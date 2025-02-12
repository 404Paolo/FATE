package com.example.tutoriapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CardView getStartedButton;
    FirebaseAuth authentication = FirebaseAuth.getInstance();
    FirebaseUser currentUser = authentication.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //App variable to determine if the user is opening the app for the first time.
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
        editor.apply();
        //---

        //If its the user first time opening the app, show an additional screen; otherwise, show the login screen.
        if(isFirstTime) {
            setContentView(R.layout.activity_main);

            getStartedButton = findViewById(R.id.main_card_view_get_started);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(toLoginActivity);
                }
            });

            editor.putBoolean("isFirstTime", false);
            editor.apply();
        } else {
            if(currentUser == null) {
                Intent toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(toLoginActivity);
            } else {
                Intent toMenuActivity = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(toMenuActivity);
            }
        }
    }
}