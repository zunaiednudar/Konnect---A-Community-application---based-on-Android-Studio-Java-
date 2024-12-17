package com.example.konnect_v2;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity implements Support {
    private ListView feedbackList;
    private List<Feedback> feedbacks;
    private FeedbackListAdapter feedbackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackList = findViewById(R.id.all_feedbacks);
        feedbacks = new ArrayList<>();

        feedbackListAdapter = new FeedbackListAdapter(FeedbackActivity.this, feedbacks);
    }

    @Override
    protected void onStart() {
        super.onStart();

        feedbacksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbacks.clear();

                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Feedback feedback = dataSnapshot.getValue(Feedback.class);

                        if (feedback != null) {
                            feedbacks.add(feedback);
                            feedbackList.setAdapter(feedbackListAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
