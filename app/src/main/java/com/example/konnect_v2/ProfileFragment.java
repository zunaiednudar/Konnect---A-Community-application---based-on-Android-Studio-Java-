package com.example.konnect_v2;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konnect_v2.databinding.FragmentProfileBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment implements Support {
    private FragmentProfileBinding binding;
    TextView profileUsername, profileEmail, labelSubkonnects, labelPosts, labelComments, labelReplies;
    RecyclerView recyclerViewSubKonnects, recyclerViewPosts, recyclerViewComments, recyclerViewReplies;

    ProfilePostListAdapter profilePostListAdapter;
    ArrayList<PostListModel> profilePostsArrayList;

    ProfileSubKonnectListAdapter profileSubKonnectListAdapter;
    ArrayList<ProfileSubKonnectListModel> profileSubKonnectsArrayList;

    ProfileCommentListAdapter profileCommentListAdapter;
    ArrayList<CommentListModel> profileCommentsArrayList;

    ProfileReplyListAdapter profileReplyListAdapter;
    ArrayList<ReplyListModel> profileRepliesArrayList;

    ImageView profileImage;
    AppCompatButton settingsButton;
    AppCompatButton deleteAccountButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        settingsButton = binding.accountSettingsButton;
        labelSubkonnects = binding.labelSubkonnects;
        labelPosts = binding.labelPosts;
        labelComments = binding.labelComments;
        labelReplies = binding.labelReplies;
        recyclerViewSubKonnects = binding.profileSubkonnectView;
        recyclerViewPosts = binding.profilePostView;
        recyclerViewComments = binding.profileCommentView;
        recyclerViewReplies = binding.profileReplyView;
        profileUsername = binding.profileUsername;
        profileEmail = binding.profileEmail;
        deleteAccountButton = binding.deleteAccountButton;
        profileImage = binding.profileImage;

        selectLabel(labelSubkonnects, recyclerViewSubKonnects);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerViewSubKonnects.addItemDecoration(dividerItemDecoration);
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);
        recyclerViewComments.addItemDecoration(dividerItemDecoration);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String key = currentUser.getUid();

            usersReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            String username = user.getUsername();
                            String email = user.getEmail();

                            profileUsername.setText(username);
                            profileEmail.setText(email);

                            if (!Objects.equals(user.getAvatarUrl(), "")) {
                                Picasso.get().load(user.getAvatarUrl()).into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.icon_account_circle_black_24);
                            }
                        } else {
                            Log.e("PROFILE_FRAGMENT__USER", "User not found");
                        }
                    } else {
                        Log.e("PROFILE_FRAGMENT__USER", "No user found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PROFILE_FRAGMENT__USER", databaseError.getMessage());
                }
            });
        } else {
            Log.e("PROFILE_FRAGMENT__USER", "No user is signed in");
        }

        profilePostsArrayList = new ArrayList<>();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            postsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profilePostsArrayList.clear();

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Post post = childSnapshot.getValue(Post.class);

                            if (post != null && Objects.equals(post.getOwnerId(), currentUserId)) {
                                PostListModel profilePostListModel = new PostListModel(post.getOwnerId(), post.getPostId(), post.getTitle(), post.getDescription(), post.getDate(), post.getUpvoteIds(), post.getDownvoteIds(), post.getCommentIds());
                                profilePostsArrayList.add(profilePostListModel);

                                int index = profilePostsArrayList.size() - 1;
                                fetchUserDataPost(index, post.getOwnerId());
                                profilePostListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("PROFILE_FRAGMENT__POST", "No post found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PROFILE_FRAGMENT__POST", databaseError.getMessage());
                }
            });
        }

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewPosts.setLayoutManager(linearLayoutManager1);
        profilePostListAdapter = new ProfilePostListAdapter(requireContext(), profilePostsArrayList);
        recyclerViewPosts.setAdapter(profilePostListAdapter);

        profileSubKonnectsArrayList = new ArrayList<>();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            subKonnectsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profileSubKonnectsArrayList.clear();

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            SubKonnect subKonnect = childSnapshot.getValue(SubKonnect.class);
                            if (subKonnect != null && Objects.equals(subKonnect.getOwnerId(), currentUserId)) {
                                ProfileSubKonnectListModel profileSubKonnectListModel = new ProfileSubKonnectListModel(subKonnect.getSubKonnectId(), subKonnect.getSubKonnectTitle(), subKonnect.getSubKonnectDescription(), subKonnect.getMemberIds());
                                profileSubKonnectsArrayList.add(profileSubKonnectListModel);

                                profileSubKonnectListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("PROFILE_FRAGMENT__SUBKONNECT", "No subkonnect found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PROFILE_FRAGMENT__SUBKONNECT", databaseError.getMessage());
                }
            });
        }

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSubKonnects.setLayoutManager(linearLayoutManager2);
        profileSubKonnectListAdapter = new ProfileSubKonnectListAdapter(requireContext(), profileSubKonnectsArrayList);
        recyclerViewSubKonnects.setAdapter(profileSubKonnectListAdapter);

        profileCommentsArrayList = new ArrayList<>();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            commentsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profileCommentsArrayList.clear();

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Comment comment = childSnapshot.getValue(Comment.class);

                            if (comment != null && Objects.equals(comment.getOwnerId(), currentUserId)) {
                                CommentListModel profileCommentListModel = new CommentListModel(comment.getCommentId(), comment.getMemberOfPostId(), comment.getCommentDescription(), comment.getDate(), comment.getUpvoteIds(), comment.getDownvoteIds(), comment.getReplyIds());
                                profileCommentsArrayList.add(profileCommentListModel);

                                int index = profileCommentsArrayList.size() - 1;
                                fetchUserDataComment(index, comment.getOwnerId());
                                profileCommentListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("PROFILE_FRAGMENT__COMMENT", "No comment found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PROFILE_FRAGMENT__COMMENT", databaseError.getMessage());
                }
            });
        }

        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComments.setLayoutManager(linearLayoutManager3);
        profileCommentListAdapter = new ProfileCommentListAdapter(requireContext(), profileCommentsArrayList);
        recyclerViewComments.setAdapter(profileCommentListAdapter);

        profileRepliesArrayList = new ArrayList<>();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            repliesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profileRepliesArrayList.clear();

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Reply reply = childSnapshot.getValue(Reply.class);

                            if (reply != null && Objects.equals(reply.getOwnerId(), currentUserId)) {
                                ReplyListModel profileReplyListModel = new ReplyListModel(reply.getReplyId(), reply.getOwnerId(), reply.getMemberOfCommentId(), reply.getReplyDescription(), reply.getDate(), reply.getUpvoteIds(), reply.getDownvoteIds());
                                profileRepliesArrayList.add(profileReplyListModel);

                                int index = profileRepliesArrayList.size() - 1;
                                fetchUserDataReply(index, reply.getOwnerId());
                                profileReplyListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("PROFILE_FRAGMENT__REPLY", "No reply found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PROFILE_FRAGMENT__REPLY", databaseError.getMessage());
                }
            });
        }

        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReplies.setLayoutManager(linearLayoutManager4);
        profileReplyListAdapter = new ProfileReplyListAdapter(requireContext(), profileRepliesArrayList);
        recyclerViewReplies.setAdapter(profileReplyListAdapter);

        labelSubkonnects.setOnClickListener(v -> selectLabel(labelSubkonnects, recyclerViewSubKonnects));

        labelPosts.setOnClickListener(v -> selectLabel(labelPosts, recyclerViewPosts));

        labelComments.setOnClickListener(v -> selectLabel(labelComments, recyclerViewComments));

        labelReplies.setOnClickListener(v -> selectLabel(labelReplies, recyclerViewReplies));

//        labelFeedbacks.setOnClickListener(v -> selectLabel(labelFeedbacks, recyclerViewFeedbacks));

        profilePostListAdapter.setOnItemClickListener(position -> {
            PostListModel clickedPost = profilePostsArrayList.get(position);
            Intent intent = new Intent(getActivity(), PostLongFormActivity.class);
            intent.putExtra("postId", clickedPost.getPostId());
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String key = currentUser.getUid();
                usersReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user != null) {
                                String username = user.getUsername();
                                String email = user.getEmail();

                                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                                intent.putExtra("id", key);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                Log.e("PROFILE_FRAGMENT__USER", "User not found");
                            }
                        } else {
                            Log.e("PROFILE_FRAGMENT__USER", "User not found in database");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFILE_FRAGMENT__USER", databaseError.getMessage());
                    }
                });
            } else {
                Log.e("PROFILE_FRAGMENT__USER", "No user is signed in");
            }
        });

        deleteAccountButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Typeface customFont = ResourcesCompat.getFont(requireContext(), R.font.acme);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                TextView customTitle = new TextView(requireContext());
                String reAuthenticationText = "Re-authentication";
                customTitle.setText(reAuthenticationText);
                customTitle.setPadding(20, 20, 20, 20);
                customTitle.setTextSize(20);
                customTitle.setTypeface(customFont);
                builder.setCustomTitle(customTitle);

                // Set up the input fields for email and password
                LinearLayout layout = new LinearLayout(requireContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(20, 20, 20, 20);

                final EditText emailInput = new EditText(requireContext());
                emailInput.setHint("Email");
                emailInput.setTypeface(customFont);
                layout.addView(emailInput);

                final EditText passwordInput = new EditText(requireContext());
                passwordInput.setHint("Password");
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordInput.setTypeface(customFont);
                layout.addView(passwordInput);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("Proceed", (dialog, which) -> {
                    String email = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty()) {
                        showToast("Please provide both email and password.");
                        return;
                    }

                    // Create AuthCredential for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                        if (reauthTask.isSuccessful()) {
                            String currentUserId = currentUser.getUid();
                            subKonnectsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            SubKonnect subKonnect = childSnapshot.getValue(SubKonnect.class);
                                            if (subKonnect != null && Objects.equals(subKonnect.getOwnerId(), currentUserId)) {
                                                String currentSubKonnectId = childSnapshot.getKey();
                                                if (currentSubKonnectId != null) {
                                                    subKonnectsReference.child(currentSubKonnectId).removeValue();
                                                }
                                            }
                                        }
                                    } else {
//                                showToast("No subkonnect found in database");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showToast(databaseError.getMessage());
                                }
                            });

                            postsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            Post post = childSnapshot.getValue(Post.class);
                                            if (post != null && Objects.equals(post.getOwnerId(), currentUserId)) {
                                                String currentPostId = childSnapshot.getKey();
                                                if (currentPostId != null) {
                                                    postsReference.child(currentPostId).removeValue();
                                                }
                                            }
                                        }
                                    } else {
//                                showToast("No post found in database");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showToast(databaseError.getMessage());
                                }
                            });

                            commentsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            Comment comment = childSnapshot.getValue(Comment.class);
                                            if (comment != null && Objects.equals(comment.getOwnerId(), currentUserId)) {
                                                String currentCommentId = childSnapshot.getKey();
                                                if (currentCommentId != null) {
                                                    commentsReference.child(currentCommentId).removeValue();
                                                }
                                            }
                                        }
                                    } else {
//                                showToast("No comment found in database");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showToast(databaseError.getMessage());
                                }
                            });

                            repliesReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            Reply reply = childSnapshot.getValue(Reply.class);
                                            if (reply != null && Objects.equals(reply.getOwnerId(), currentUserId)) {
                                                String currentReplyId = childSnapshot.getKey();
                                                if (currentReplyId != null) {
                                                    commentsReference.child(currentReplyId).removeValue();
                                                }
                                            }
                                        }
                                    } else {
//                                showToast("No reply found in database");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showToast(databaseError.getMessage());
                                }
                            });

                            usersReference.child(currentUserId).removeValue();
                            currentUser.delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    // Account deletion successful
                                    firebaseAuth.signOut();
                                    showToast("User account deleted successfully");
                                    Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    requireActivity().finish();
                                } else {
                                    // Account deletion failed
                                    showToast("Failed to delete user account");
                                }
                            });
                        } else {
//                            Re-authentication failed
                            showToast("Re-authentication failed. Please try again.");
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
        });

        profileSubKonnectListAdapter.setOnItemClickListener(position -> {
            ProfileSubKonnectListModel clickedSubKonnect = profileSubKonnectsArrayList.get(position);
            Intent intent = new Intent(getActivity(), SubKonnectActivity.class);
            intent.putExtra("subKonnectId", clickedSubKonnect.getSubKonnectId());
            startActivity(intent);
        });

        profileCommentListAdapter.setOnItemClickListener(position -> {
            CommentListModel clickedComment = profileCommentsArrayList.get(position);
            Intent intent = new Intent(getActivity(), PostLongFormActivity.class);
            intent.putExtra("postId", clickedComment.getMemberOfPostId());
            startActivity(intent);
        });

        profileReplyListAdapter.setOnItemClickListener(position -> {
            ReplyListModel clickedReply = profileRepliesArrayList.get(position);
            commentsReference.child(clickedReply.getMemberOfCommentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        if (comment != null) {
                            Intent intent = new Intent(getActivity(), PostLongFormActivity.class);
                            intent.putExtra("postId", comment.getMemberOfPostId());
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        return binding.getRoot();
    }

    public void fetchUserDataComment(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (index >= 0 && index < profileCommentsArrayList.size()) {
                            profileCommentsArrayList.get(index).setOwnerName(user.getUsername());
                            profileCommentsArrayList.get(index).setOwnerEmail(user.getEmail());
                            profileCommentsArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                            profileCommentListAdapter.notifyItemChanged(index);
                        } else {
                            profileCommentsArrayList.clear();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    public void fetchUserDataReply(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (index >= 0 && index < profileRepliesArrayList.size()) {
                            profileRepliesArrayList.get(index).setOwnerName(user.getUsername());
                            profileRepliesArrayList.get(index).setOwnerEmail(user.getEmail());
                            profileRepliesArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                            profileReplyListAdapter.notifyItemChanged(index);
                        } else {
                            profileRepliesArrayList.clear();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    public void fetchUserDataPost(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (index >= 0 && index < profilePostsArrayList.size()) {
                            profilePostsArrayList.get(index).setOwnerName(user.getUsername());
                            profilePostsArrayList.get(index).setOwnerEmail(user.getEmail());
                            profilePostsArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                            profilePostListAdapter.notifyItemChanged(index);
                        } else {
                            profilePostsArrayList.clear();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    private void selectLabel(TextView selectedLabel, RecyclerView associatedRecyclerView) {
        resetLabels();
        selectedLabel.setAlpha(1f);
        selectedLabel.setClickable(false);
        selectedLabel.setBackgroundColor(getResources().getColor(R.color.dent_color));
        associatedRecyclerView.setVisibility(View.VISIBLE);
    }

    private void resetLabels() {
        labelSubkonnects.setAlpha(0.5f);
        labelSubkonnects.setClickable(true);

        labelPosts.setAlpha(0.5f);
        labelPosts.setClickable(true);

        labelComments.setAlpha(0.5f);
        labelComments.setClickable(true);

        labelReplies.setAlpha(0.5f);
        labelReplies.setClickable(true);

        recyclerViewSubKonnects.setVisibility(View.GONE);
        recyclerViewPosts.setVisibility(View.GONE);
        recyclerViewComments.setVisibility(View.GONE);
        recyclerViewReplies.setVisibility(View.GONE);

        labelPosts.setBackgroundColor(getResources().getColor(R.color.primary));
        labelSubkonnects.setBackgroundColor(getResources().getColor(R.color.primary));
        labelComments.setBackgroundColor(getResources().getColor(R.color.primary));
        labelReplies.setBackgroundColor(getResources().getColor(R.color.primary));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}