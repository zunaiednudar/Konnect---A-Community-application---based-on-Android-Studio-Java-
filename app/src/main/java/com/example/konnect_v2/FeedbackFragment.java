package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class FeedbackFragment extends Fragment implements Support {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        ratingBar.setStepSize(1.0f);

        TextView impression = view.findViewById(R.id.impression);
        impression.setText(impressions[0]);

        impression.setTextColor(getResources().getColor(R.color.red));
        ratingBar.setRating(1);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                impression.setText(impressions[(int) rating - 1]);

                switch ((int) rating) {
                    case 1:
                        impression.setTextColor(getResources().getColor(R.color.red));
                        break;
                    case 2:
                        impression.setTextColor(getResources().getColor(R.color.orange));
                        break;
                    case 3:
                        impression.setTextColor(getResources().getColor(R.color.yellow));
                        break;
                    case 4:
                        impression.setTextColor(getResources().getColor(R.color.light_green));
                        break;
                    case 5:
                        impression.setTextColor(getResources().getColor(R.color.green));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + (int) rating);
                }
            }
        });

        EditText feedbackDescription = view.findViewById(R.id.feedback_description);
        Button shareButton = view.findViewById(R.id.share_button);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = feedbackDescription.getText().toString().trim();
                int rating = (int) ratingBar.getRating();

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;

                String key = currentUser.getUid();
                usersReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user != null) {
                                Feedback feedback = new Feedback(rating, description, user.getUsername(), user.getEmail(), user.getAvatarUrl(), new Date());

                                feedbacksReference.child(key).removeValue();
                                feedbacksReference.child(key).setValue(feedback);
                                showToast("Feedback shared!");

                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setNavigationVisibility(false);

            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setNavigationVisibility(true);

            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}