package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity implements Support {
    Button forgotPasswordResetButton;
    Button forgotPasswordBackButton;
    EditText emailInput;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordResetButton = findViewById(R.id.forgot_password_reset_button);
        forgotPasswordBackButton = findViewById(R.id.forgot_password_back_button);
        emailInput = findViewById(R.id.forgot_password_email_input);
        progressbar = findViewById(R.id.forgot_password_progress_bar);

        forgotPasswordResetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                ResetPassword(email);
            } else {
                showToast(ForgotPasswordActivity.this, "Email field cannot be empty");
            }
        });

        forgotPasswordBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void ResetPassword(String email) {
        progressbar.setVisibility(View.VISIBLE);
        forgotPasswordResetButton.setVisibility(View.GONE);

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
            showToast(ForgotPasswordActivity.this, "Reset password link has been sent");
            firebaseAuth.signOut();

            Intent intent = new Intent(ForgotPasswordActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            showToast(ForgotPasswordActivity.this, "Error");
            progressbar.setVisibility(View.GONE);
            forgotPasswordResetButton.setVisibility(View.VISIBLE);
        });
    }
}