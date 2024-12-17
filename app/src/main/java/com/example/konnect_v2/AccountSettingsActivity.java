package com.example.konnect_v2;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccountSettingsActivity extends AppCompatActivity implements Support {
    EditText newUserNameInput, oldPasswordInput, newPasswordInput, confirmNewPasswordInput;
    Button editProfileButton, changeEmailButton, verifyEmailButton, updatePasswordButton;
    TextView verifyEmailChecked, emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        String id = getIntent().getStringExtra("id");
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");

        newUserNameInput = findViewById(R.id.new_username_input);
        oldPasswordInput = findViewById(R.id.old_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmNewPasswordInput = findViewById(R.id.confirm_new_password_input);

        editProfileButton = findViewById(R.id.edit_profile_button);
        changeEmailButton = findViewById(R.id.change_email_button);
        verifyEmailButton = findViewById(R.id.verify_email_button);
        updatePasswordButton = this.findViewById(R.id.update_password_button);
        verifyEmailChecked = findViewById(R.id.verify_email_checked);
        emailText = findViewById(R.id.email_text);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        newUserNameInput.setText(username);
        emailText.setText(email);

        if (currentUser != null && currentUser.isEmailVerified()) {
            verifyEmailButton.setAlpha(0.5f);
            verifyEmailButton.setEnabled(false);
            verifyEmailChecked.setVisibility(View.VISIBLE);
        } else {
            verifyEmailButton.setAlpha(1f);
            verifyEmailButton.setEnabled(true);
            verifyEmailChecked.setVisibility(View.GONE);
        }


        editProfileButton.setOnClickListener(v -> {
            if (currentUser != null) {
                if (id != null && id.equals(currentUser.getUid())) {
                    String newUsername = newUserNameInput.getText().toString().trim();

                    if (newUsername.equals(username)) {
                        return;
                    }

                    usersReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);

                                if (user != null) {
                                    if (!newUsername.isEmpty()) user.setUsername(newUsername);
                                    usersReference.child(id).setValue(user);

                                    Intent intent = new Intent(AccountSettingsActivity.this, ProfileFragment.class);
                                    showToast(AccountSettingsActivity.this, "User updated");
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    showToast(AccountSettingsActivity.this, "User conflict");
                }
            }
        });

        changeEmailButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Typeface customFont = ResourcesCompat.getFont(AccountSettingsActivity.this, R.font.acme);

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingsActivity.this);

                TextView customTitle = new TextView(AccountSettingsActivity.this);
                String reAuthenticationText = "Re-authentication";
                customTitle.setText(reAuthenticationText);
                customTitle.setPadding(20, 20, 20, 20);
                customTitle.setTextSize(20);
                customTitle.setTypeface(customFont);
                builder.setCustomTitle(customTitle);

//                Set up the input fields for email and password
                LinearLayout layout = new LinearLayout(AccountSettingsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(20, 20, 20, 20);

                final EditText emailInput = new EditText(AccountSettingsActivity.this);
                emailInput.setHint("Current Email");
                emailInput.setTypeface(customFont);
                layout.addView(emailInput);

                final EditText passwordInput = new EditText(AccountSettingsActivity.this);
                passwordInput.setHint("Password");
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordInput.setTypeface(customFont);
                layout.addView(passwordInput);

                builder.setView(layout);

//                Set up the buttons
                builder.setPositiveButton("Proceed", (dialog, which) -> {
                    String currentEmail = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();

                    if (currentEmail.isEmpty() || password.isEmpty()) {
                        showToast(AccountSettingsActivity.this, "Please provide both email and password");
                        return;
                    }

//                    Re-authenticate the user
                    AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

                    currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                        if (reauthTask.isSuccessful()) {
//                            Proceed to update the email
                            showUpdateEmailDialog(currentUser);
                        } else {
                            showToast(AccountSettingsActivity.this, "Re-authentication failed");
                        }
                    });
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                AlertDialog dialog = builder.create();
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTypeface(customFont);
                negativeButton.setTypeface(customFont);
            } else {
                showToast(AccountSettingsActivity.this, "No user is currently logged in");
            }
        });

        verifyEmailButton.setOnClickListener(v -> {
            if (currentUser == null) {
                showToast(AccountSettingsActivity.this, "No user is currently logged in");
                return;
            }
//            Send verification email for the new email
            currentUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast(AccountSettingsActivity.this, "Verification email sent");

                    firebaseAuth.signOut();
                    Intent intent = new Intent(AccountSettingsActivity.this, AuthenticationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showToast(AccountSettingsActivity.this, "Failed to send verification email");
                }
            });
        });

        updatePasswordButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();

            if (!newPassword.equals(confirmNewPassword)) {
                showToast(AccountSettingsActivity.this, "Password mismatch");
            }

            assert currentUser != null;

            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), oldPassword);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            usersReference.child(currentUser.getUid()).child("password").setValue(newPassword);

                                            showToast(AccountSettingsActivity.this, "Password updated successfully!");
                                            Intent intent = new Intent(AccountSettingsActivity.this, ProfileFragment.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
//                                            Handle the error (e.g., weak password, network issue)
                                            showToast(AccountSettingsActivity.this, "Error updating password");
                                        }
                                    });
                        } else {
//                            Handle the error (e.g., incorrect old password)
                            showToast(AccountSettingsActivity.this, "Re-authentication failed");
                        }
                    });
        });
    }

    private void showUpdateEmailDialog(FirebaseUser currentUser) {
        Typeface customFont = ResourcesCompat.getFont(AccountSettingsActivity.this, R.font.acme);

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingsActivity.this);

        TextView customTitle = new TextView(AccountSettingsActivity.this);
        String updateEmailText = "Update Email";
        customTitle.setText(updateEmailText);
        customTitle.setPadding(20, 20, 20, 20);
        customTitle.setTextSize(20);
        customTitle.setTypeface(customFont);
        builder.setCustomTitle(customTitle);

        final EditText newEmailInput = new EditText(AccountSettingsActivity.this);
        newEmailInput.setHint("New Email");
        newEmailInput.setTypeface(customFont);
        newEmailInput.setPadding(20, 20, 20, 20);

        builder.setView(newEmailInput);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newEmail = newEmailInput.getText().toString().trim();

            if (newEmail.isEmpty()) {
                showToast(AccountSettingsActivity.this, "Please provide the new email address");
                return;
            }

            currentUser.updateEmail(newEmail).addOnCompleteListener(updateTask -> {
                if (updateTask.isSuccessful()) {
                    showToast(AccountSettingsActivity.this, "Email updated successfully");
                    usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);

                                if (user != null) {
                                    user.setEmail(currentUser.getEmail());
                                    usersReference.child(currentUser.getUid()).setValue(user);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    firebaseAuth.signOut();
                    Intent intent = new Intent(AccountSettingsActivity.this, AuthenticationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showToast(AccountSettingsActivity.this, "Failed to update email");
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTypeface(customFont);
        negativeButton.setTypeface(customFont);
    }
}
