package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements Support {
    EditText userUsername, userEmail, userPassword, userConfirmPassword;
    TextView passwordTypeRequirement, passwordLengthRequirement;
    Button nextButton;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userUsername = findViewById(R.id.user_username_input);
        userEmail = findViewById(R.id.user_email_input);
        userPassword = findViewById(R.id.user_password_input);
        userConfirmPassword = findViewById(R.id.user_confirm_password_input);

        passwordTypeRequirement = findViewById(R.id.user_password_type_requirements);
        passwordLengthRequirement = findViewById(R.id.user_password_length_requirements);

        nextButton = findViewById(R.id.next_button);

//        In case of incorrect information, the fields must be reset with default color after getting clicked again
        userUsername.setOnClickListener(v -> userUsername.setBackgroundColor(getResources().getColor(R.color.primary_mid)));
        userEmail.setOnClickListener(v -> userEmail.setBackgroundColor(getResources().getColor(R.color.primary_mid)));
        userPassword.setOnClickListener(v -> {
            userPassword.setBackgroundColor(getResources().getColor(R.color.primary_mid));
            userConfirmPassword.setBackgroundColor(getResources().getColor(R.color.primary_mid));
            passwordTypeRequirement.setBackgroundColor(getResources().getColor(R.color.white));
            passwordLengthRequirement.setBackgroundColor(getResources().getColor(R.color.white));
        });
        userConfirmPassword.setOnClickListener(v -> userConfirmPassword.setBackgroundColor(getResources().getColor(R.color.primary_mid)));

        nextButton.setOnClickListener(v -> {
            String username = userUsername.getText().toString().trim();
            String email = userEmail.getText().toString().trim();
            String password = userPassword.getText().toString().trim();
            String confirmPassword = userConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                userUsername.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Enter username");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                userEmail.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Enter email");
                return;
            }

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                userPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                userConfirmPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Enter both passwords");
                return;
            }

            if (password.length() < 6) {
                userPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                passwordLengthRequirement.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Password does not fulfill the requirements");
                return;
            }

            boolean capitalLetterFound = false;
            boolean smallLetterFound = false;
            boolean numberFound = false;
            boolean specialCharacterFound = false;

            char[] passwordCharArray = password.toCharArray();

            for (char ch : passwordCharArray) {
                if (ch >= 'A' && ch <= 'Z') capitalLetterFound = true;
                if (ch >= 'a' && ch <= 'z') smallLetterFound = true;
                if (ch >= '0' && ch <= '9') numberFound = true;
                if (ch == '@' || ch == '#' || ch == '$') specialCharacterFound = true;
            }

            if (!capitalLetterFound || !smallLetterFound || !numberFound || !specialCharacterFound) {
                userPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                passwordTypeRequirement.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Password does not fulfill the requirements");
                return;
            }

            if (!password.equals(confirmPassword)) {
                userPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                userConfirmPassword.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                showToast("Passwords do not match");
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
                if (task.isSuccessful()) {
                    saveUserInfo(username, email, password);
                    Intent intent = new Intent(SignUpActivity.this, ProfileCustomizationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("Network error");
                }
            });
        });
    }

    public void saveUserInfo(String username, String email, String password) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String key = currentUser.getUid();
        User newUser = new User(null, key, username, email, password, "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        usersReference.child(key).setValue(newUser);
        Toast.makeText(SignUpActivity.this, "User info added", Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}