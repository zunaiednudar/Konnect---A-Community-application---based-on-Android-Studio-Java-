package com.example.konnect_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostLongFormActivity extends AppCompatActivity implements Support, TimeAgoFormatter {
    ImageView longPostOwnerImage, longPostImage1, longPostImage2, longPostImage3, longPostImage4,
            longPostUpvoteButtonBlack, longPostUpvoteButtonOrange, longPostDownvoteButtonBlack, longPostDownvoteButtonOrange;
    TextView longPostOwnerUsername, longPostOwnerEmail, longPostDate, longPostTitle, longPostDescription, longPostUpvoteCount, longPostDownvoteCount, longPostCommentCount;
    LinearLayout commentBox, commentSectionBox, longPostCommentIconBox, longPostShareIconBox;
    RecyclerView recyclerViewComments;
    EditText commentWriteSection;
    Button submitButton;

    CommentListAdapter commentListAdapter;
    ArrayList<CommentListModel> commentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_long_form);

        String postId = getIntent().getStringExtra("postId");

        longPostOwnerImage = findViewById(R.id.long_post_owner_image);
        longPostOwnerUsername = findViewById(R.id.long_post_owner_username);
        longPostOwnerEmail = findViewById(R.id.long_post_owner_email);
        longPostDate = findViewById(R.id.long_post_date);
        longPostTitle = findViewById(R.id.long_post_title);
        longPostDescription = findViewById(R.id.long_post_description);

        longPostUpvoteButtonBlack = findViewById(R.id.long_post_upvote_button_black);
        longPostUpvoteButtonOrange = findViewById(R.id.long_post_upvote_button_orange);
        longPostDownvoteButtonBlack = findViewById(R.id.long_post_downvote_button_black);
        longPostDownvoteButtonOrange = findViewById(R.id.long_post_downvote_button_orange);

        longPostUpvoteCount = findViewById(R.id.long_post_upvote_count);
        longPostDownvoteCount = findViewById(R.id.long_post_downvote_count);
        longPostCommentCount = findViewById(R.id.long_post_comment_count);
        longPostCommentIconBox = findViewById(R.id.long_post_comment_icon_box);
        longPostShareIconBox = findViewById(R.id.long_post_share_icon_box);

        longPostImage1 = findViewById(R.id.post_image1);
        longPostImage2 = findViewById(R.id.post_image2);
        longPostImage3 = findViewById(R.id.post_image3);
        longPostImage4 = findViewById(R.id.post_image4);

        commentSectionBox = findViewById(R.id.comment_section_box);
        longPostCommentIconBox = findViewById(R.id.long_post_comment_icon_box);
        recyclerViewComments = findViewById(R.id.recycler_view_long_post_comment_section);
        commentBox = findViewById(R.id.comment_box);
        commentWriteSection = findViewById(R.id.comment_write_section);
        submitButton = findViewById(R.id.long_post_comment_submit_button);

        commentSectionBox.setVisibility(View.GONE);
        commentBox.setVisibility(View.GONE);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(PostLongFormActivity.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerViewComments.addItemDecoration(dividerItemDecoration);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        assert postId != null;
        String currentUserId = currentUser.getUid();

        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Post post = childSnapshot.getValue(Post.class);

                        if (post != null && Objects.equals(post.getPostId(), postId)) {
                            String ownerId = post.getOwnerId();
                            longPostTitle.setText(post.getTitle());
                            longPostDescription.setText(post.getDescription());
                            longPostDate.setText(TimeAgoFormatter.timeAgo(post.getDate()));

                            if (post.getUpvoteIds() != null)
                                longPostUpvoteCount.setText(String.valueOf(post.getUpvoteIds().size()));
                            else longPostUpvoteCount.setText("0");
                            if (post.getDownvoteIds() != null)
                                longPostDownvoteCount.setText(String.valueOf(post.getDownvoteIds().size()));
                            else longPostDownvoteCount.setText("0");

                            if (post.getCommentIds() != null)
                                longPostCommentCount.setText(String.valueOf(post.getCommentIds().size()));
                            else longPostCommentCount.setText("0");

                            fetchPostImages(postId);
                            fetchUserData(ownerId);
                        }
                    }
                } else {
                    Log.e("POST_LONG_FORM_ACTIVITY__POST", "No post found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });

        postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post != null) {
                        ArrayList<String> upvoteIds = post.getUpvoteIds();
                        ArrayList<String> downvoteIds = post.getDownvoteIds();

                        if (upvoteIds != null && upvoteIds.contains(currentUserId)) {
                            longPostUpvoteButtonBlack.setVisibility(View.GONE);
                            longPostUpvoteButtonOrange.setVisibility(View.VISIBLE);
                        } else {
                            longPostUpvoteButtonBlack.setVisibility(View.VISIBLE);
                            longPostUpvoteButtonOrange.setVisibility(View.GONE);
                        }
                        if (downvoteIds != null && downvoteIds.contains(currentUserId)) {
                            longPostDownvoteButtonBlack.setVisibility(View.GONE);
                            longPostDownvoteButtonOrange.setVisibility(View.VISIBLE);
                        } else {
                            longPostDownvoteButtonBlack.setVisibility(View.VISIBLE);
                            longPostDownvoteButtonOrange.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Maintaining longPostUpvoteButtonBlack
            longPostUpvoteButtonBlack.setOnClickListener(v -> {
                postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Post post = dataSnapshot.getValue(Post.class);

                            if (post != null) {
                                ArrayList<String> upvoteIds = post.getUpvoteIds();
                                ArrayList<String> downvoteIds = post.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);
                                if (upvoteIds == null) upvoteIds = new ArrayList<>();
                                upvoteIds.add(currentUserId);

                                post.setUpvoteIds(upvoteIds);
                                post.setDownvoteIds(downvoteIds);
                            }
                            postsReference.child(postId).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            longPostUpvoteButtonBlack.setVisibility(View.GONE);
            longPostUpvoteButtonOrange.setVisibility(View.VISIBLE);
            longPostDownvoteButtonBlack.setVisibility(View.VISIBLE);
            longPostDownvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
        });
//
//        Maintaining longPostDownvoteButtonBlack
        longPostDownvoteButtonBlack.setOnClickListener(v -> {
            postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Post post = dataSnapshot.getValue(Post.class);

                        if (post != null) {
                            ArrayList<String> upvoteIds = post.getUpvoteIds();
                            ArrayList<String> downvoteIds = post.getDownvoteIds();

                            if (upvoteIds != null) upvoteIds.remove(currentUserId);
                            if (downvoteIds == null) downvoteIds = new ArrayList<>();
                            downvoteIds.add(currentUserId);

                            post.setUpvoteIds(upvoteIds);
                            post.setDownvoteIds(downvoteIds);
                        }
                        postsReference.child(postId).setValue(post);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            longPostUpvoteButtonBlack.setVisibility(View.VISIBLE);
            longPostUpvoteButtonOrange.setVisibility(View.GONE);
            longPostDownvoteButtonBlack.setVisibility(View.GONE);
            longPostDownvoteButtonOrange.setVisibility(View.VISIBLE);

            reloadActivity();
        });
//
//        Maintaining longPostUpvoteButtonOrange
            longPostUpvoteButtonOrange.setOnClickListener(v -> {
                postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Post post = dataSnapshot.getValue(Post.class);

                            if (post != null) {
                                ArrayList<String> upvoteIds = post.getUpvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (upvoteIds != null) upvoteIds.remove(currentUserId);

                                post.setUpvoteIds(upvoteIds);
                            }
                            postsReference.child(postId).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            longPostUpvoteButtonBlack.setVisibility(View.VISIBLE);
            longPostUpvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
        });
//
//        Maintaining longPostDownvoteButtonOrange
        longPostDownvoteButtonOrange.setOnClickListener(v -> {
            postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Post post = dataSnapshot.getValue(Post.class);

                        if (post != null) {
                            ArrayList<String> downvoteIds = post.getDownvoteIds();

                            String currentUserId = currentUser.getUid();
                            if (downvoteIds != null) downvoteIds.remove(currentUserId);

                            post.setUpvoteIds(downvoteIds);
                        }
                        postsReference.child(postId).setValue(post);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            longPostDownvoteButtonBlack.setVisibility(View.VISIBLE);
            longPostDownvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
        });
//

        commentArrayList = new ArrayList<>();

        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    commentArrayList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Comment comment = childSnapshot.getValue(Comment.class);

                        if (comment != null && Objects.equals(comment.getMemberOfPostId(), postId)) {
                            CommentListModel commentListModel = new CommentListModel(comment.getCommentId(), comment.getMemberOfPostId(), comment.getCommentDescription(), comment.getDate(), comment.getUpvoteIds(), comment.getDownvoteIds(), comment.getReplyIds());
                            commentArrayList.add(commentListModel);

                            int index = commentArrayList.size() - 1;
                            fetchUserData(index, comment.getOwnerId());
                            commentListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.e("POST_LONG_FORM_ACTIVITY__COMMENT", "No comment found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManagerComment = new LinearLayoutManager(PostLongFormActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewComments.setLayoutManager(linearLayoutManagerComment);
        commentListAdapter = new CommentListAdapter(PostLongFormActivity.this, commentArrayList);
        recyclerViewComments.setAdapter(commentListAdapter);

        longPostShareIconBox.setOnClickListener(v -> {
            String postUrl = "https://konnect-a6216-default-rtdb.firebaseio.com/posts/" + postId;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, postUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        submitButton.setOnClickListener(v -> {
            String ownerId = currentUser.getUid();
            String commentDescription = commentWriteSection.getText().toString().trim();

            if (commentDescription.isEmpty()) {
                showToast("Comment cannot be empty!");
                return;
            }

            String commentId = commentsReference.push().getKey();

//                Finding the user avatar URL
            usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            String ownerAvatarUrl = user.getAvatarUrl();

//                                Creating the new comment
                            Comment newComment = new Comment(ownerAvatarUrl, ownerId, commentId, postId, commentDescription, new Date(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                            if (commentId != null) {
//                                Adding the new reply in Firebase Realtime Database
                                commentsReference.child(commentId).setValue(newComment);
                                commentBox.setVisibility(View.GONE);

//                                Updating the corresponding post information in Firebase Realtime Database
                                postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Post post = dataSnapshot.getValue(Post.class);

                                            if (post != null) {
                                                ArrayList<String> commentIds = post.getCommentIds();
                                                if (commentIds == null) commentIds = new ArrayList<>();
                                                commentIds.add(commentId);

                                                post.setCommentIds(commentIds);
                                                postsReference.child(postId).setValue(post);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                reloadActivity();
                            }

                        } else {
                            Log.e("POST_LONG_FORM_ACTIVITY__USER", "No user found in database");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("POST_LONG_FORM_ACTIVITY__USER", databaseError.getMessage());
                }
            });
        });

        longPostCommentIconBox.setOnClickListener(v -> {
            if (commentSectionBox.getVisibility() == View.VISIBLE) {
                commentSectionBox.setVisibility(View.GONE);
                commentBox.setVisibility(View.GONE);
            } else {
                commentSectionBox.setVisibility(View.VISIBLE);
                commentBox.setVisibility(View.VISIBLE);
            }
        });
    }

    public void fetchUserData(String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        longPostOwnerUsername.setText(user.getUsername());
                        longPostOwnerEmail.setText(user.getEmail());

                        if (user.getAvatarUrl() != null) {
                            Picasso.get().load(user.getAvatarUrl()).into(longPostOwnerImage);
                        } else {
                            longPostOwnerImage.setImageResource(R.drawable.icon_account_circle_black_24);
                        }
                    }
                } else {
                    Log.e("POST_LONG_FORM_ACTIVITY__USER", "No user found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    private void fetchPostImages(String postId) {
        retrievePostImagesFromFirestore(postId + "_1", 1);
        retrievePostImagesFromFirestore(postId + "_2", 2);
        retrievePostImagesFromFirestore(postId + "_3", 3);
        retrievePostImagesFromFirestore(postId + "_4", 4);
    }

    private void retrievePostImagesFromFirestore(String path, int number) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("postImages").document(path)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String base64Image = document.getString("image");
                            if (base64Image != null) {
                                Bitmap bitmap = decodeBase64ToBitmap(base64Image);
                                if (bitmap != null) {
                                    switch (number) {
                                        case 1:
                                            longPostImage1.setImageBitmap(bitmap);
                                            longPostImage1.setVisibility(View.VISIBLE);
                                            break;
                                        case 2:
                                            longPostImage2.setImageBitmap(bitmap);
                                            longPostImage2.setVisibility(View.VISIBLE);
                                            break;
                                        case 3:
                                            longPostImage3.setImageBitmap(bitmap);
                                            longPostImage3.setVisibility(View.VISIBLE);
                                            break;
                                        case 4:
                                            longPostImage4.setImageBitmap(bitmap);
                                            longPostImage4.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                } else {
                                    Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "Failed to decode image");
                                }
                            } else {
                                Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "No image found");
                            }
                        } else {
                            Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "No image document found");
                        }
                    } else {
                        Log.e("POST_LONG_FORM_ACTIVITY__POST_IMAGE", "Error retrieving image: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void fetchUserData(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (index >= 0 && index < commentArrayList.size()) {
                            commentArrayList.get(index).setOwnerName(user.getUsername());
                            commentArrayList.get(index).setOwnerEmail(user.getEmail());
                            commentArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                            commentListAdapter.notifyItemChanged(index);
                        } else {
                            commentArrayList.clear();
                        }
                    }
                } else {
                    Log.e("POST_LONG_FORM_ACTIVITY__USER", "No user found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(PostLongFormActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}