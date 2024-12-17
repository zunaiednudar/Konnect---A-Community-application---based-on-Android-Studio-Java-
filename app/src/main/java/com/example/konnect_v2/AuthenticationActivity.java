package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuthenticationActivity extends AppCompatActivity implements Support {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        EditText userEmail = findViewById(R.id.user_email_input);
        EditText userPassword = findViewById(R.id.user_password_input);
        Button signInButton = findViewById(R.id.user_sign_in_button);
        TextView forgotPasswordText = findViewById(R.id.user_forgot_password_text);
        TextView signUpText = findViewById(R.id.user_sign_up_text);


        signInButton.setOnClickListener(v -> {
            String email = userEmail.getText().toString().trim();
            String password = userPassword.getText().toString();

            if (email.equals(adminEmail) && password.equals(adminPassword)) {
                Intent intent = new Intent(AuthenticationActivity.this, FeedbackActivity.class);
                startActivity(intent);
            } else {
                signInUser(email, password);
            }
        });

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenticationActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenticationActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void signInUser(String email, String password) {
        if (email.isEmpty()) {
            showToast(AuthenticationActivity.this, "Email field cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            showToast(AuthenticationActivity.this, "Password field cannot be empty");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                showToast(AuthenticationActivity.this, "Authentication failed");
            }
        });
    }
}