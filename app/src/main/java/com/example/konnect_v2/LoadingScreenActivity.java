package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingScreenActivity extends AppCompatActivity implements Support {

    private void checkUserStatus() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                    navigateTo(MainActivity.class);
                } else {
                    handleInvalidUser();
                }
            });
        } else {
            navigateTo(AuthenticationActivity.class);
        }
    }

    private void handleInvalidUser() {
        FirebaseAuth.getInstance().signOut();
        navigateTo(AuthenticationActivity.class);
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(LoadingScreenActivity.this, targetActivity));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Handler handler = new Handler();
        handler.postDelayed(this::checkUserStatus, 3000);
    }
}
