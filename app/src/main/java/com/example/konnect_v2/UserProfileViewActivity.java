package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileViewActivity extends AppCompatActivity implements Support {
    ImageView userProfileImage;
    TextView usernameText, userBio, postCount, followerCount, followingCount;
    Button followButton, followBackButton, unfollowButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        String userId = getIntent().getStringExtra("user_id");
        String userAvatarUrl = getIntent().getStringExtra("user_avatar_url");
        String usernameString = getIntent().getStringExtra("user_name");
        String userBioString = getIntent().getStringExtra("user_bio");

        userProfileImage = findViewById(R.id.user_profile_image);
        usernameText = findViewById(R.id.user_profile_username);
        userBio = findViewById(R.id.user_profile_bio);

        postCount = findViewById(R.id.post_count);
        followerCount = findViewById(R.id.follower_count);
        followingCount = findViewById(R.id.following_count);
        followButton = findViewById(R.id.follow_button);
        followBackButton = findViewById(R.id.follow_back_button);
        unfollowButton = findViewById(R.id.unfollow_button);
        chatButton = findViewById(R.id.chat_button);

        if (userAvatarUrl != null) Picasso.get().load(userAvatarUrl).into(userProfileImage);
        else userProfileImage.setImageResource(R.drawable.icon_account_circle_black_24);

        usernameText.setText(usernameString);
        userBio.setText(userBioString);
        loadData(userId);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        assert currentUser != null;

        if (currentUser.getUid().equals(userId)) {
            followButton.setVisibility(View.GONE);
            followBackButton.setVisibility(View.GONE);
            unfollowButton.setVisibility(View.GONE);

            chatButton.setAlpha(0.5f);
            chatButton.setEnabled(false);
        } else {
//            Maintaining the 'FOLLOW', 'FOLLOW BACK' and 'UNFOLLOW' button visibility
            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (user.getFollowingIds() != null && user.getFollowingIds().contains(userId)) {
                                followButton.setVisibility(View.GONE);
                                followBackButton.setVisibility(View.GONE);
                                unfollowButton.setVisibility(View.VISIBLE);
                            } else {
                                if (user.getFollowerIds() != null && user.getFollowerIds().contains(userId)) {
                                    followButton.setVisibility(View.GONE);
                                    followBackButton.setVisibility(View.VISIBLE);
                                } else {
                                    followButton.setVisibility(View.VISIBLE);
                                    followBackButton.setVisibility(View.GONE);
                                }
                                unfollowButton.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
//
//        Customizing 'FOLLOW' button
        followButton.setOnClickListener(v -> {
            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followingIds = user.getFollowingIds();

                            if (followingIds == null) followingIds = new ArrayList<>();
                            followingIds.add(userId);

                            user.setFollowingIds(followingIds);
                        }
                        usersReference.child(currentUser.getUid()).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            assert userId != null;
            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followerIds = user.getFollowerIds();

                            if (followerIds == null) followerIds = new ArrayList<>();
                            followerIds.add(currentUser.getUid());

                            user.setFollowerIds(followerIds);
                        }
                        usersReference.child(userId).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            followButton.setVisibility(View.GONE);
            unfollowButton.setVisibility(View.VISIBLE);
            reloadActivity();
        });
//
//        Customizing 'FOLLOW BACK' button
        followBackButton.setOnClickListener(v -> {
            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followingIds = user.getFollowingIds();

                            if (followingIds == null) followingIds = new ArrayList<>();
                            followingIds.add(userId);

                            user.setFollowingIds(followingIds);
                        }
                        usersReference.child(currentUser.getUid()).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            assert userId != null;
            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followerIds = user.getFollowerIds();

                            if (followerIds == null) followerIds = new ArrayList<>();
                            followerIds.add(currentUser.getUid());

                            user.setFollowerIds(followerIds);
                        }
                        usersReference.child(userId).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            followBackButton.setVisibility(View.GONE);
            unfollowButton.setVisibility(View.VISIBLE);
            reloadActivity();
        });
//
//        Customizing 'UNFOLLOW' button
        unfollowButton.setOnClickListener(v -> {
            assert userId != null;
            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followingIds = user.getFollowingIds();

                            if (followingIds == null) followingIds = new ArrayList<>();
                            followingIds.remove(userId);

                            user.setFollowingIds(followingIds);

                            if (user.getFollowerIds() != null && user.getFollowerIds().contains(userId)) {
                                followButton.setVisibility(View.GONE);
                                followBackButton.setVisibility(View.VISIBLE);
                            } else {
                                followButton.setVisibility(View.VISIBLE);
                                followBackButton.setVisibility(View.GONE);
                            }
                        }
                        usersReference.child(currentUser.getUid()).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            ArrayList<String> followerIds = user.getFollowerIds();

                            if (followerIds == null) followerIds = new ArrayList<>();
                            followerIds.remove(currentUser.getUid());

                            user.setFollowerIds(followerIds);
                        }
                        usersReference.child(userId).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            unfollowButton.setVisibility(View.GONE);
            reloadActivity();
        });
//

//        Customizing the chatButton
        chatButton.setOnClickListener(v -> {
            assert userId != null;
//            Checks whether the sender follows and being followed by the receiver or not
            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            ArrayList<String> followingIds = user.getFollowingIds();
                            ArrayList<String> followerIds = user.getFollowerIds();

                            if (followingIds == null || !followingIds.contains(userId)) {
                                Toast.makeText(UserProfileViewActivity.this, "You don't follow this user yet!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (followerIds == null || !followerIds.contains(userId)) {
                                Toast.makeText(UserProfileViewActivity.this, "You are not followed but this user yet!", Toast.LENGTH_SHORT).show();
                                return;
                            }

//                            Checks if a chatId already exists or not
                            usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        User user = dataSnapshot.getValue(User.class);

                                        if (user != null) {
                                            HashMap<String, String> senderChatReferences = user.getChatReferences();

                                            if (senderChatReferences != null && senderChatReferences.containsKey(userId)) {
                                                Intent intent = new Intent(UserProfileViewActivity.this, ChatWindowActivity.class);
                                                intent.putExtra("receiver_name", usernameString);
                                                intent.putExtra("receiver_avatar_url", userAvatarUrl);
                                                intent.putExtra("receiver_id", userId);
                                                intent.putExtra("chat_id", senderChatReferences.get(userId));
                                                startActivity(intent);
                                            } else {
//                            If the common chatId doesn't exist in the moment, a new key is generated and stored in both users as chatReference
                                                String newChatId = chatsReference.push().getKey();

//                            Updating the chatReferences in Firebase Realtime Database
                                                assert newChatId != null;
                                                chatsReference.child(newChatId).setValue(new Chat(newChatId, new ArrayList<>()));

//                            Updating the chatReferences of both the sender and receiver

//                            First phase
                                                usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            User user = dataSnapshot.getValue(User.class);

                                                            if (user != null) {
                                                                HashMap<String, String> senderChatReferences = user.getChatReferences();
                                                                if (senderChatReferences == null)
                                                                    senderChatReferences = new HashMap<>();
                                                                senderChatReferences.put(userId, newChatId);
                                                                user.setChatReferences(senderChatReferences);
                                                                usersReference.child(currentUser.getUid()).setValue(user);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

//                            Second phase
                                                usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            User user = dataSnapshot.getValue(User.class);

                                                            if (user != null) {
                                                                HashMap<String, String> receiverChatReferences = user.getChatReferences();
                                                                if (receiverChatReferences == null)
                                                                    receiverChatReferences = new HashMap<>();
                                                                receiverChatReferences.put(currentUser.getUid(), newChatId);
                                                                user.setChatReferences(receiverChatReferences);
                                                                usersReference.child(userId).setValue(user);

//                                    Passing intent to the chatWindowActivity (in the second phase)
                                                                Intent intent = new Intent(UserProfileViewActivity.this, ChatWindowActivity.class);
                                                                intent.putExtra("receiver_name", usernameString);
                                                                intent.putExtra("receiver_avatar_url", userAvatarUrl);
                                                                intent.putExtra("receiver_id", userId);
                                                                intent.putExtra("chat_id", receiverChatReferences.get(currentUser.getUid()));
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    private void loadData(String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getPostIds() != null)
                            postCount.setText(String.valueOf(user.getPostIds().size()));
                        else postCount.setText("0");
                        if (user.getFollowerIds() != null)
                            followerCount.setText(String.valueOf(user.getFollowerIds().size()));
                        else followerCount.setText("0");
                        if (user.getFollowingIds() != null)
                            followingCount.setText(String.valueOf(user.getFollowingIds().size()));
                        else followingCount.setText("0");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}