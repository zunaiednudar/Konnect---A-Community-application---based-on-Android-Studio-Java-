package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class SubKonnectActivity extends AppCompatActivity implements Support {
    RecyclerView recyclerViewPosts, recyclerViewMembers;

    PostListAdapter postListAdapter;
    ArrayList<PostListModel> arrayListPosts;

    UserListAdapter userListAdapter;
    ArrayList<UserListModel> arrayListMembers;

    TextView subKonnectOwnerUsername, subKonnectOwnerEmail;
    LinearLayout subKonnectOwnerBox;
    ImageView subKonnectOwnerImage;

    Button subKonnectJoinButton, subKonnectLeaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_konnect);

        String subKonnectId = getIntent().getStringExtra("subKonnectId");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;

        recyclerViewPosts = findViewById(R.id.recycler_view_posts);
        recyclerViewMembers = findViewById(R.id.recycler_view_members);

        subKonnectOwnerUsername = findViewById(R.id.sub_konnect_owner_username);
        subKonnectOwnerEmail = findViewById(R.id.sub_konnect_owner_email);
        subKonnectOwnerBox = findViewById(R.id.sub_konnect_owner_box);
        subKonnectOwnerImage = findViewById(R.id.sub_konnect_owner_image);

        subKonnectJoinButton = findViewById(R.id.sub_konnect_join_button);
        subKonnectLeaveButton = findViewById(R.id.sub_konnect_leave_button);

        assert subKonnectId != null;


        subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);

                    if (subKonnect != null) {
                        if (subKonnect.getOwnerId().equals(currentUser.getUid())) {
                            subKonnectJoinButton.setVisibility(View.GONE);
                            subKonnectLeaveButton.setVisibility(View.GONE);
                        } else if (subKonnect.getMemberIds() != null && subKonnect.getMemberIds().contains(currentUser.getUid())) {
                            subKonnectJoinButton.setVisibility(View.GONE);
                            subKonnectLeaveButton.setVisibility(View.VISIBLE);
                        } else {
                            subKonnectJoinButton.setVisibility(View.VISIBLE);
                            subKonnectLeaveButton.setVisibility(View.GONE);
                        }

                        usersReference.child(subKonnect.getOwnerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null) {
                                        subKonnectOwnerEmail.setText(user.getEmail());
                                        subKonnectOwnerUsername.setText(user.getUsername());

                                        if (user.getAvatarUrl() != null) {
                                            Picasso.get().load(user.getAvatarUrl()).into(subKonnectOwnerImage);
                                        } else {
                                            subKonnectOwnerImage.setImageResource(R.drawable.icon_account_circle_black_24);
                                        }
                                    } else {
                                        subKonnectOwnerImage.setImageResource(R.drawable.icon_account_circle_black_24);
                                    }
                                } else {
                                    subKonnectOwnerImage.setImageResource(R.drawable.icon_account_circle_black_24);
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

        subKonnectOwnerBox.setOnClickListener(v -> subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);

                    if (subKonnect != null) {
                        usersReference.child(subKonnect.getOwnerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null) {
                                        Intent intent = new Intent(SubKonnectActivity.this, UserProfileViewActivity.class);
                                        intent.putExtra("user_id", user.getUserId());
                                        intent.putExtra("user_name", user.getUsername());
                                        intent.putExtra("user_bio", user.getBio());
                                        intent.putExtra("user_avatar_url", user.getAvatarUrl());
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SubKonnectActivity.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);
        recyclerViewMembers.addItemDecoration(dividerItemDecoration);

        arrayListPosts = new ArrayList<>();
        arrayListMembers = new ArrayList<>();

        assert subKonnectId != null;

        subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);
                    assert subKonnect != null;
                    ArrayList<String> memberIds = subKonnect.getMemberIds();

                    if (memberIds != null) {
                        usersReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                arrayListMembers.clear();
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    User user = childSnapshot.getValue(User.class);

                                    if (user != null && memberIds.contains(user.getUserId())) {
                                        UserListModel userListModel = new UserListModel(user.getUserId(), user.getAvatarUrl(), user.getUsername(), user.getBio(), user.getEmail(), user.getPostIds(), user.getFollowerIds(), user.getFollowingIds());
                                        arrayListMembers.add(userListModel);

                                        int index = arrayListMembers.size() - 1;
                                        fetchUserData2(index, user.getUserId());
                                    }
                                }
                                userListAdapter.notifyDataSetChanged();
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
                showToast(databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(SubKonnectActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMembers.setLayoutManager(linearLayoutManager1);
        userListAdapter = new UserListAdapter(SubKonnectActivity.this, arrayListMembers);
        recyclerViewMembers.setAdapter(userListAdapter);

        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayListPosts.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Post post = childSnapshot.getValue(Post.class);
                        if (post != null && Objects.equals(post.getSubKonnectBelongsToId(), subKonnectId)) {
                            PostListModel postListModel = new PostListModel(post.getOwnerId(), post.getPostId(), post.getTitle(), post.getDescription(), post.getDate(), post.getUpvoteIds(), post.getDownvoteIds(), post.getCommentIds());
                            arrayListPosts.add(postListModel);

                            int index = arrayListPosts.size() - 1;
                            fetchUserData(index, post.getOwnerId());
                        }
                    }
                    postListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(SubKonnectActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPosts.setLayoutManager(linearLayoutManager2);
        postListAdapter = new PostListAdapter(SubKonnectActivity.this, arrayListPosts);
        recyclerViewPosts.setAdapter(postListAdapter);

        postListAdapter.setOnItemClickListener(position -> {
            PostListModel clickedPost = arrayListPosts.get(position);
            Intent intent = new Intent(SubKonnectActivity.this, PostLongFormActivity.class);
            intent.putExtra("postId", clickedPost.getPostId());
            startActivity(intent);
        });

        subKonnectJoinButton.setOnClickListener(v -> {
            String newMemberId = currentUser.getUid();

            subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SubKonnect subKonnect1 = dataSnapshot.getValue(SubKonnect.class);
                        ArrayList<String> subKonnectMemberIds = Objects.requireNonNull(subKonnect1).getMemberIds();

                        if (subKonnectMemberIds == null) subKonnectMemberIds = new ArrayList<>();

                        subKonnectMemberIds.add(newMemberId);
                        subKonnect1.setMemberIds(subKonnectMemberIds);
                        subKonnectsReference.child(subKonnectId).setValue(subKonnect1);
                        showToast("You are now added as a member to the community");

                        subKonnectJoinButton.setVisibility(View.GONE);
                        subKonnectLeaveButton.setVisibility(View.VISIBLE);

                        refresh();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        subKonnectLeaveButton.setOnClickListener(v -> {
            String memberId = currentUser.getUid();

            subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SubKonnect subKonnect1 = dataSnapshot.getValue(SubKonnect.class);
                        ArrayList<String> subKonnectMemberIds = Objects.requireNonNull(subKonnect1).getMemberIds();

                        if (subKonnectMemberIds != null) subKonnectMemberIds.remove(memberId);
                        subKonnect1.setMemberIds(subKonnectMemberIds);
                        subKonnectsReference.child(subKonnectId).setValue(subKonnect1);
                        showToast("You have been removed from the community");

                        subKonnectJoinButton.setVisibility(View.VISIBLE);
                        subKonnectLeaveButton.setVisibility(View.GONE);

                        refresh();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    public void fetchUserData(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        arrayListPosts.get(index).setOwnerName(user.getUsername());
                        arrayListPosts.get(index).setOwnerEmail(user.getEmail());
                        arrayListPosts.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                        postListAdapter.notifyItemChanged(index);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    public void fetchUserData2(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        arrayListMembers.get(index).setUserName(user.getUsername());
                        arrayListMembers.get(index).setUserEmail(user.getEmail());
                        arrayListMembers.get(index).setAvatarUrl(user.getAvatarUrl());

                        userListAdapter.notifyItemChanged(index);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(SubKonnectActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}