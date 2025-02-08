package com.example.tutoriapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput, passwordInput;
    CardView enterButton;
    ProgressBar progressBar;
    FirebaseAuth authentication;

    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.login_edit_text_email);
        passwordInput = findViewById(R.id.login_edit_text_password);

        enterButton = findViewById(R.id.login_card_view_enter);

        progressBar = findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        authentication = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {


            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentEmail = emailInput.getText().toString();
                String studentPassword = passwordInput.getText().toString();

                emailInput.clearFocus();
                passwordInput.clearFocus();

                if(TextUtils.isEmpty(studentEmail)) {
                    emailInput.setError("This field must not be empty");
                } else if(TextUtils.isEmpty(studentPassword)) {
                    passwordInput.setError("This field must not be empty");
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    authentication.signInWithEmailAndPassword(studentEmail, studentPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "Login Success");
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent toMenuActivity = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(toMenuActivity);
                            } else {
                                Log.d(TAG, "Error logging in");
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
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
}