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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<CommentListModel> commentsArrayList;

    public CommentListAdapter(Context context, ArrayList<CommentListModel> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        CommentListModel currentComment = commentsArrayList.get(position);
        int currentPosition = holder.getAdapterPosition();
        String commentId = currentComment.getCommentId();

        if (currentPosition != RecyclerView.NO_POSITION) {
            commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        if (comment != null) {
                            ArrayList<String> upvoteIds = comment.getUpvoteIds();
                            ArrayList<String> downvoteIds = comment.getDownvoteIds();

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

        holder.ownerUsername.setText(commentsArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(commentsArrayList.get(position).getOwnerEmail());
        holder.commentDate.setText(TimeAgoFormatter.timeAgo(commentsArrayList.get(position).getDate()));
        holder.commentDescription.setText(commentsArrayList.get(position).getCommentDescription());

        if (commentsArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(commentsArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (commentsArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(commentsArrayList.get(position).getDownvoteIds().size()));
        else holder.downvoteCount.setText("0");

        if (commentsArrayList.get(position).getReplyIds() != null) {
            List<String> replyIds = commentsArrayList.get(position).getReplyIds();
            String count = String.format("(%d)", replyIds.size());
            holder.replyCount.setText(count);
        } else holder.replyCount.setText("(0)");


        holder.replySubmitButton.setOnClickListener(v -> {
            String ownerId = currentUser.getUid();
            String replyId = repliesReference.push().getKey();
            String replyDescription = holder.replyWriteSection.getText().toString().trim();

//                Finding the user avatar URL
            usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            String ownerAvatarUrl = user.getAvatarUrl();

//                                Creating the new reply
                            Reply newReply = new Reply(replyId, ownerAvatarUrl, ownerId, user.getEmail(), commentId, replyDescription, new Date(), new ArrayList<>(), new ArrayList<>());

//                                Adding the new reply in Firebase Realtime Database
                            if (replyId != null) {
                                repliesReference.child(replyId).setValue(newReply);
                                holder.replyBox.setVisibility(View.GONE);

//                                Updating the corresponding comment information in Firebase Realtime Database
                                commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Comment comment = dataSnapshot.getValue(Comment.class);

                                            if (comment != null) {
                                                ArrayList<String> replyIds = comment.getReplyIds();
                                                if (replyIds == null) replyIds = new ArrayList<>();
                                                replyIds.add(replyId);

                                                comment.setReplyIds(replyIds);
                                                commentsReference.child(commentId).setValue(comment);
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
                            Log.e("COMMENT_LIST_ADAPTER__USER", "User not found");
                        }
                    } else {
                        Log.e("COMMENT_LIST_ADAPTER__USER", "No user found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("COMMENT_LIST_ADAPTER__USER", databaseError.getMessage());
                }
            });
        });

        holder.addReplyButton.setOnClickListener(v -> {
            if (holder.replyBox.getVisibility() == View.VISIBLE) {
                holder.replyBox.setVisibility(View.GONE);
            } else {
                holder.replyBox.setVisibility(View.VISIBLE);
            }
        });

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
                        Log.e("COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
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
                        Log.e("COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
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
                        Log.e("COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
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
                        Log.e("COMMENT_LIST_ADAPTER__COMMENT", databaseError.getMessage());
                    }
                });
            }

            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);
        });
//
//        Loads all the current replies
        holder.repliesArrayList = new ArrayList<>();

        repliesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.repliesArrayList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Reply reply = childSnapshot.getValue(Reply.class);

                        if (reply != null && Objects.equals(reply.getMemberOfCommentId(), commentId)) {
                            ReplyListModel replyListModel = new ReplyListModel(reply.getReplyId(), reply.getOwnerId(), reply.getMemberOfCommentId(), reply.getReplyDescription(), reply.getDate(), reply.getUpvoteIds(), reply.getDownvoteIds());
                            holder.repliesArrayList.add(replyListModel);

                            int index = holder.repliesArrayList.size() - 1;
                            fetchUserData(index, reply.getOwnerId(), holder.repliesArrayList, holder.replyListAdapter);
                        }
                    }
                    holder.replyListAdapter.notifyDataSetChanged();
                } else {
                    Log.e("COMMENT_LIST_ADAPTER__REPLY", "No reply found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("COMMENT_LIST_ADAPTER__REPLY", databaseError.getMessage());
            }
        });

//        Handling visibility of reply section
        holder.showRepliesIconBox.setOnClickListener(v -> {
            if (holder.repliesSection.getVisibility() == View.VISIBLE) {
                holder.repliesSection.setVisibility(View.GONE);
            } else {
                holder.repliesSection.setVisibility(View.VISIBLE);
            }
        });

//        Setting up linear layout manager for the replies
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recyclerViewReplies.setLayoutManager(linearLayoutManager);
        holder.replyListAdapter = new ReplyListAdapter(context, holder.repliesArrayList);
        holder.recyclerViewReplies.setAdapter(holder.replyListAdapter);
    }

    public void fetchUserData(int index, String ownerId, ArrayList<ReplyListModel> repliesArrayList, ReplyListAdapter replyListAdapter) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        repliesArrayList.get(index).setOwnerName(user.getUsername());
                        repliesArrayList.get(index).setOwnerEmail(user.getEmail());
                        repliesArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                        replyListAdapter.notifyItemChanged(index);
                    }
                } else {
                    Log.e("COMMENT_LIST_ADAPTER__USER", "No user found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("COMMENT_LIST_ADAPTER__USER", databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ownerImage, upvoteButtonBlack, downvoteButtonBlack, upvoteButtonOrange, downvoteButtonOrange;
        TextView ownerUsername, ownerEmail, commentDate, upvoteCount, downvoteCount, replyCount, commentDescription;
        Button addReplyButton, replySubmitButton;
        LinearLayout showRepliesIconBox, repliesSection, replyBox;
        EditText replyWriteSection;

        RecyclerView recyclerViewReplies;
        ArrayList<ReplyListModel> repliesArrayList;
        ReplyListAdapter replyListAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ownerImage = itemView.findViewById(R.id.comment_owner_image);
            ownerUsername = itemView.findViewById(R.id.comment_owner_username);
            ownerEmail = itemView.findViewById(R.id.comment_owner_email);
            commentDate = itemView.findViewById(R.id.comment_date);

            upvoteCount = itemView.findViewById(R.id.comment_upvote_count);
            downvoteCount = itemView.findViewById(R.id.comment_downvote_count);
            replyCount = itemView.findViewById(R.id.comment_reply_count);

            commentDescription = itemView.findViewById(R.id.comment_description);

            upvoteButtonBlack = itemView.findViewById(R.id.comment_upvote_button_black);
            downvoteButtonBlack = itemView.findViewById(R.id.comment_downvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.comment_upvote_button_orange);
            downvoteButtonOrange = itemView.findViewById(R.id.comment_downvote_button_orange);

            showRepliesIconBox = itemView.findViewById(R.id.show_replies_icon_box);
            addReplyButton = itemView.findViewById(R.id.add_reply_button);
            replyWriteSection = itemView.findViewById(R.id.reply_write_section);
            replyBox = itemView.findViewById(R.id.reply_box);
            replySubmitButton = itemView.findViewById(R.id.long_post_reply_submit_button);
            recyclerViewReplies = itemView.findViewById(R.id.recycler_view_replies_section);
            repliesSection = itemView.findViewById(R.id.replies_section);
        }
    }

    private void reloadActivity() {
        Activity activity = (Activity) context;
        Intent intent = activity.getIntent();
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }
}

