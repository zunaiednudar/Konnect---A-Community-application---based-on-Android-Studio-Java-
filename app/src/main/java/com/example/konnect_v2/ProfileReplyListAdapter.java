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


public class ProfileReplyListAdapter extends RecyclerView.Adapter<ProfileReplyListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<ReplyListModel> profileRepliesArrayList;

    private ProfileReplyListAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ProfileReplyListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfileReplyListAdapter(Context context, ArrayList<ReplyListModel> profileRepliesArrayList) {
        this.context = context;
        this.profileRepliesArrayList = profileRepliesArrayList;
    }

    @NonNull
    @Override
    public ProfileReplyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_profile, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileReplyListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        ReplyListModel currentReply = profileRepliesArrayList.get(position);
        int currentPosition = holder.getAdapterPosition();
        String replyId = currentReply.getReplyId();

        if (currentPosition != RecyclerView.NO_POSITION) {
            repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Reply reply = dataSnapshot.getValue(Reply.class);

                        if (reply != null) {
                            ArrayList<String> upvoteIds = reply.getUpvoteIds();
                            ArrayList<String> downvoteIds = reply.getDownvoteIds();

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

        if (currentReply.getOwnerAvatarUrl() != null) {
            Picasso.get().load(currentReply.getOwnerAvatarUrl()).into(holder.ownerImage);
        } else {
            holder.ownerImage.setImageResource(R.drawable.icon_account_circle_black_24);
        }

        holder.replyDate.setText(TimeAgoFormatter.timeAgo(profileRepliesArrayList.get(position).getDate()));
        holder.ownerUsername.setText(profileRepliesArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(profileRepliesArrayList.get(position).getOwnerEmail());
        holder.replyDescription.setText(profileRepliesArrayList.get(position).getReplyDescription());

        holder.newReplyDescription.setText(profileRepliesArrayList.get(position).getReplyDescription());

        if (profileRepliesArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(profileRepliesArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (profileRepliesArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(profileRepliesArrayList.get(position).getDownvoteIds().size()));
        else holder.downvoteCount.setText("0");

        //        Maintaining upvoteButtonBlack
        holder.upvoteButtonBlack.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Reply reply = dataSnapshot.getValue(Reply.class);

                            if (reply != null) {
                                ArrayList<String> upvoteIds = reply.getUpvoteIds();
                                ArrayList<String> downvoteIds = reply.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);
                                if (upvoteIds == null) upvoteIds = new ArrayList<>();
                                upvoteIds.add(currentUserId);

                                reply.setUpvoteIds(upvoteIds);
                                reply.setDownvoteIds(downvoteIds);
                            }
                            repliesReference.child(replyId).setValue(reply);
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
                repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Reply reply = dataSnapshot.getValue(Reply.class);

                            if (reply != null) {
                                ArrayList<String> upvoteIds = reply.getUpvoteIds();
                                ArrayList<String> downvoteIds = reply.getDownvoteIds();

                                if (upvoteIds != null) upvoteIds.remove(currentUserId);
                                if (downvoteIds == null) downvoteIds = new ArrayList<>();
                                downvoteIds.add(currentUserId);

                                reply.setUpvoteIds(upvoteIds);
                                reply.setDownvoteIds(downvoteIds);
                            }
                            repliesReference.child(replyId).setValue(reply);
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
                repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Reply reply = dataSnapshot.getValue(Reply.class);

                            if (reply != null) {
                                ArrayList<String> upvoteIds = reply.getUpvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (upvoteIds != null) upvoteIds.remove(currentUserId);

                                reply.setUpvoteIds(upvoteIds);
                            }
                            repliesReference.child(replyId).setValue(reply);
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
                repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Reply reply = dataSnapshot.getValue(Reply.class);

                            if (reply != null) {
                                ArrayList<String> downvoteIds = reply.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);

                                reply.setDownvoteIds(downvoteIds);
                            }
                            repliesReference.child(replyId).setValue(reply);
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

        holder.replyUpdateIcon.setOnClickListener(v -> {
            holder.replyUpdateIcon.setVisibility(View.GONE);
            holder.replyDeleteIcon.setVisibility(View.GONE);
            holder.replyDescription.setVisibility(View.GONE);
            holder.newReplyDescription.setVisibility(View.VISIBLE);
            holder.submitButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);

            holder.voteSectionLayout.setAlpha(0.5f);
            holder.upvoteButtonBlack.setEnabled(false);
            holder.upvoteButtonOrange.setEnabled(false);
            holder.downvoteButtonBlack.setEnabled(false);
            holder.downvoteButtonOrange.setEnabled(false);
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.replyUpdateIcon.setVisibility(View.VISIBLE);
            holder.replyDeleteIcon.setVisibility(View.VISIBLE);
            holder.replyDescription.setVisibility(View.VISIBLE);
            holder.newReplyDescription.setVisibility(View.GONE);
            holder.submitButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            holder.voteSectionLayout.setAlpha(1f);
            holder.upvoteButtonBlack.setEnabled(true);
            holder.upvoteButtonOrange.setEnabled(true);
            holder.downvoteButtonBlack.setEnabled(true);
            holder.downvoteButtonOrange.setEnabled(true);

            holder.newReplyDescription.setText(profileRepliesArrayList.get(position).getReplyDescription());
        });

        holder.submitButton.setOnClickListener(v -> {
            holder.replyUpdateIcon.setVisibility(View.VISIBLE);
            holder.replyDeleteIcon.setVisibility(View.VISIBLE);
            holder.replyDescription.setVisibility(View.VISIBLE);
            holder.newReplyDescription.setVisibility(View.GONE);
            holder.submitButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            holder.voteSectionLayout.setAlpha(1f);
            holder.upvoteButtonBlack.setEnabled(true);
            holder.upvoteButtonOrange.setEnabled(true);
            holder.downvoteButtonBlack.setEnabled(true);
            holder.downvoteButtonOrange.setEnabled(true);

            String newReplyDescription = holder.newReplyDescription.getText().toString().trim();

            if (newReplyDescription.isEmpty()) {
                Toast.makeText(context, "Reply field cannot be empty", Toast.LENGTH_SHORT).show();
                holder.newReplyDescription.setText(profileRepliesArrayList.get(position).getReplyDescription());
                return;
            }

            repliesReference.child(replyId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Reply reply = dataSnapshot.getValue(Reply.class);

                        if (reply != null) {
                            reply.setReplyDescription(newReplyDescription);
                            repliesReference.child(replyId).setValue(reply);

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
        holder.replyDeleteIcon.setOnClickListener(v -> {
//            Deleting the replyId from the replyIds arraylist of the respective comment in Firebase Realtime Database
            deleteReplyIdFromComment(currentReply.getMemberOfCommentId(), currentReply.getReplyId());

//            Deleting the reply from the replies tree in Firebase Realtime Database
            repliesReference.child(currentReply.getReplyId()).removeValue().addOnSuccessListener(unused -> {
                Toast.makeText(context, "Reply deleted successfully!", Toast.LENGTH_SHORT).show();
                profileRepliesArrayList.remove(currentReply);
                notifyDataSetChanged();

                refresh();
            });
        });


    }

    @Override
    public int getItemCount() {
        return profileRepliesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ownerUsername, ownerEmail, replyDate, upvoteCount, downvoteCount, replyDescription;
        ImageView ownerImage, upvoteButtonBlack, downvoteButtonBlack, upvoteButtonOrange, downvoteButtonOrange, replyDeleteIcon, replyUpdateIcon;
        Button submitButton, cancelButton;
        EditText newReplyDescription;
        LinearLayout voteSectionLayout;

        public ViewHolder(@NonNull View itemView, ProfileReplyListAdapter.OnItemClickListener listener) {
            super(itemView);

            replyDate = itemView.findViewById(R.id.profile_reply_date);
            upvoteCount = itemView.findViewById(R.id.profile_reply_upvote_count);
            downvoteCount = itemView.findViewById(R.id.profile_reply_downvote_count);
            replyDescription = itemView.findViewById(R.id.profile_reply_description);

            ownerImage = itemView.findViewById(R.id.profile_reply_owner_image);
            ownerUsername = itemView.findViewById(R.id.profile_reply_owner_username);
            ownerEmail = itemView.findViewById(R.id.profile_reply_owner_email);

            upvoteButtonBlack = itemView.findViewById(R.id.profile_reply_upvote_button_black);
            downvoteButtonBlack = itemView.findViewById(R.id.profile_reply_downvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.profile_reply_upvote_button_orange);
            downvoteButtonOrange = itemView.findViewById(R.id.profile_reply_downvote_button_orange);

            replyDeleteIcon = itemView.findViewById(R.id.profile_reply_delete_icon);

            replyUpdateIcon = itemView.findViewById(R.id.profile_reply_update_icon);
            submitButton = itemView.findViewById(R.id.profile_reply_edit_submit_button);
            cancelButton = itemView.findViewById(R.id.profile_reply_edit_cancel_button);
            newReplyDescription = itemView.findViewById(R.id.profile_reply_description_edit);
            voteSectionLayout = itemView.findViewById(R.id.profile_reply_vote_section_layout);

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


