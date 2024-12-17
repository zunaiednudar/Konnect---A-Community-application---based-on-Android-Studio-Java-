package com.example.konnect_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileCommentListAdapter extends RecyclerView.Adapter<ProfileCommentListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<CommentListModel> profileCommentsArrayList;

    private ProfileCommentListAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ProfileCommentListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfileCommentListAdapter(Context context, ArrayList<CommentListModel> profileCommentsArrayList) {
        this.context = context;
        this.profileCommentsArrayList = profileCommentsArrayList;
    }

    @NonNull
    @Override
    public ProfileCommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_profile, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileCommentListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        CommentListModel currentComment = profileCommentsArrayList.get(position);
        int currentPosition = holder.getAdapterPosition();
        String commentId = currentComment.getCommentId();

        if (currentPosition != RecyclerView.NO_POSITION) {
            commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Post post = dataSnapshot.getValue(Post.class);

                        if (post != null) {
                            ArrayList<String> upvoteIds = post.getUpvoteIds();
                            ArrayList<String> downvoteIds = post.getDownvoteIds();

                            if (upvoteIds != null && upvoteIds.contains(currentUserId)) {
                                holder.upvoteButtonBlack.setVisibility(View.GONE);
                                holder.upvoteButtonOrange.setVisibility(View.VISIBLE);
                            } else {
                                holder.upvoteButtonBlack.setVisibility(View.VISIBLE);
                                holder.upvoteButtonOrange.setVisibility(View.GONE);
                            }
                            if (downvoteIds != null && downvoteIds.contains(currentUserId)) {
                                holder.downvoteButtonBlack.setVisibility(View.GONE);
                                holder.downvoteButtonOrange.setVisibility(View.VISIBLE);
                            } else {
                                holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
                                holder.downvoteButtonOrange.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (currentComment.getOwnerAvatarUrl() != null) {
            Picasso.get().load(currentComment.getOwnerAvatarUrl()).into(holder.ownerImage);
        } else {
            holder.ownerImage.setImageResource(R.drawable.icon_account_circle_black_24);
        }

        holder.commentDate.setText(TimeAgoFormatter.timeAgo(profileCommentsArrayList.get(position).getDate()));
        holder.ownerUsername.setText(profileCommentsArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(profileCommentsArrayList.get(position).getOwnerEmail());
        holder.commentDescription.setText(profileCommentsArrayList.get(position).getCommentDescription());

        holder.newCommentDescription.setText(profileCommentsArrayList.get(position).getCommentDescription());

        if (profileCommentsArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(profileCommentsArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (profileCommentsArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(profileCommentsArrayList.get(position).getDownvoteIds().size()));
        else holder.downvoteCount.setText("0");

        //        Maintaining upvoteButtonBlack
        holder.upvoteButtonBlack.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);

                            if (comment != null) {
                                ArrayList<String> upvoteIds = comment.getUpvoteIds();
                                ArrayList<String> downvoteIds = comment.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);
                                if (upvoteIds == null) upvoteIds = new ArrayList<>();
                                upvoteIds.add(currentUserId);

                                comment.setUpvoteIds(upvoteIds);
                                comment.setDownvoteIds(downvoteIds);
                            }
                            commentsReference.child(commentId).setValue(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFILE_COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.GONE);
            holder.upvoteButtonOrange.setVisibility(View.VISIBLE);
            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);
        });
//
//        Maintaining downvoteButtonBlack
        holder.downvoteButtonBlack.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);

                            if (comment != null) {
                                ArrayList<String> upvoteIds = comment.getUpvoteIds();
                                ArrayList<String> downvoteIds = comment.getDownvoteIds();

                                if (upvoteIds != null) upvoteIds.remove(currentUserId);
                                if (downvoteIds == null) downvoteIds = new ArrayList<>();
                                downvoteIds.add(currentUserId);

                                comment.setUpvoteIds(upvoteIds);
                                comment.setDownvoteIds(downvoteIds);
                            }
                            commentsReference.child(commentId).setValue(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFILE_COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.upvoteButtonOrange.setVisibility(View.GONE);
            holder.downvoteButtonBlack.setVisibility(View.GONE);
            holder.downvoteButtonOrange.setVisibility(View.VISIBLE);
        });
//
//        Maintaining upvoteButtonOrange
        holder.upvoteButtonOrange.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);

                            if (comment != null) {
                                ArrayList<String> upvoteIds = comment.getUpvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (upvoteIds != null) upvoteIds.remove(currentUserId);

                                comment.setUpvoteIds(upvoteIds);
                            }
                            commentsReference.child(commentId).setValue(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFILE_COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.upvoteButtonOrange.setVisibility(View.GONE);
        });
//
//        Maintaining downvoteButtonOrange
        holder.downvoteButtonOrange.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);

                            if (comment != null) {
                                ArrayList<String> downvoteIds = comment.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);

                                comment.setDownvoteIds(downvoteIds);
                            }
                            commentsReference.child(commentId).setValue(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFILE_COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
                    }
                });
            }

            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);
        });

        holder.commentUpdateIcon.setOnClickListener(v -> {
            holder.commentUpdateIcon.setVisibility(View.GONE);
            holder.commentDeleteIcon.setVisibility(View.GONE);
            holder.commentDescription.setVisibility(View.GONE);
            holder.newCommentDescription.setVisibility(View.VISIBLE);
            holder.submitButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);

            holder.voteSectionLayout.setAlpha(0.5f);
            holder.upvoteButtonBlack.setEnabled(false);
            holder.upvoteButtonOrange.setEnabled(false);
            holder.downvoteButtonBlack.setEnabled(false);
            holder.downvoteButtonOrange.setEnabled(false);
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.commentUpdateIcon.setVisibility(View.VISIBLE);
            holder.commentDeleteIcon.setVisibility(View.VISIBLE);
            holder.commentDescription.setVisibility(View.VISIBLE);
            holder.newCommentDescription.setVisibility(View.GONE);
            holder.submitButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            holder.voteSectionLayout.setAlpha(1f);
            holder.upvoteButtonBlack.setEnabled(true);
            holder.upvoteButtonOrange.setEnabled(true);
            holder.downvoteButtonBlack.setEnabled(true);
            holder.downvoteButtonOrange.setEnabled(true);

            holder.newCommentDescription.setText(profileCommentsArrayList.get(position).getCommentDescription());
        });

        holder.submitButton.setOnClickListener(v -> {
            holder.commentUpdateIcon.setVisibility(View.VISIBLE);
            holder.commentDeleteIcon.setVisibility(View.VISIBLE);
            holder.commentDescription.setVisibility(View.VISIBLE);
            holder.newCommentDescription.setVisibility(View.GONE);
            holder.submitButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            holder.voteSectionLayout.setAlpha(1f);
            holder.upvoteButtonBlack.setEnabled(true);
            holder.upvoteButtonOrange.setEnabled(true);
            holder.downvoteButtonBlack.setEnabled(true);
            holder.downvoteButtonOrange.setEnabled(true);

            String newCommentDescription = holder.newCommentDescription.getText().toString().trim();

            if (newCommentDescription.isEmpty()) {
                Toast.makeText(context, "Comment field cannot be empty", Toast.LENGTH_SHORT).show();
                holder.newCommentDescription.setText(profileCommentsArrayList.get(position).getCommentDescription());
                return;
            }

            commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        if (comment != null) {
                            comment.setCommentDescription(newCommentDescription);
                            commentsReference.child(commentId).setValue(comment);

                            refresh();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
//
        holder.commentDeleteIcon.setOnClickListener(v -> {
//            Deleting the commentId from the commentIds arraylist of the respective post in Firebase Realtime Database
            deleteCommentIdFromPost(currentComment.getMemberOfPostId(), currentComment.getCommentId());

//            Deleting all the respective replies from the comment in Firebase Realtime Database
            deleteAllRepliesFromComment(currentComment.getCommentId());

//            Deleting the comment from the comment tree in Firebase Realtime Database
            commentsReference.child(currentComment.getCommentId()).removeValue().addOnSuccessListener(unused -> {
                Toast.makeText(context, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
                profileCommentsArrayList.remove(currentComment);
                notifyDataSetChanged();

                refresh();
            });
        });


    }

    @Override
    public int getItemCount() {
        return profileCommentsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ownerUsername, ownerEmail, commentDate, upvoteCount, downvoteCount, commentDescription;
        ImageView ownerImage, upvoteButtonBlack, downvoteButtonBlack, upvoteButtonOrange, downvoteButtonOrange, commentDeleteIcon, commentUpdateIcon;
        Button submitButton, cancelButton;
        EditText newCommentDescription;
        LinearLayout voteSectionLayout;

        public ViewHolder(@NonNull View itemView, ProfileCommentListAdapter.OnItemClickListener listener) {
            super(itemView);

            commentDate = itemView.findViewById(R.id.profile_comment_date);
            upvoteCount = itemView.findViewById(R.id.profile_comment_upvote_count);
            downvoteCount = itemView.findViewById(R.id.profile_comment_downvote_count);
            commentDescription = itemView.findViewById(R.id.profile_comment_description);

            ownerImage = itemView.findViewById(R.id.profile_comment_owner_image);
            ownerUsername = itemView.findViewById(R.id.profile_comment_owner_username);
            ownerEmail = itemView.findViewById(R.id.profile_comment_owner_email);

            upvoteButtonBlack = itemView.findViewById(R.id.profile_comment_upvote_button_black);
            downvoteButtonBlack = itemView.findViewById(R.id.profile_comment_downvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.profile_comment_upvote_button_orange);
            downvoteButtonOrange = itemView.findViewById(R.id.profile_comment_downvote_button_orange);

            commentDeleteIcon = itemView.findViewById(R.id.profile_comment_delete_icon);

            commentUpdateIcon = itemView.findViewById(R.id.profile_comment_update_icon);
            submitButton = itemView.findViewById(R.id.profile_comment_edit_submit_button);
            cancelButton = itemView.findViewById(R.id.profile_comment_edit_cancel_button);
            newCommentDescription = itemView.findViewById(R.id.profile_comment_description_edit);
            voteSectionLayout = itemView.findViewById(R.id.profile_comment_vote_section_layout);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private void refresh() {
        Intent intent = ((Activity) context).getIntent();
        ((Activity) context).finish();
        context.startActivity(intent);
    }
}

