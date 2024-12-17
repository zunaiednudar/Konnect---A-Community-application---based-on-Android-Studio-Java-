package com.example.konnect_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<ReplyListModel> repliesArrayList;

    public ReplyListAdapter(Context context, ArrayList<ReplyListModel> repliesArrayList) {
        this.context = context;
        this.repliesArrayList = repliesArrayList;
    }

    @NonNull
    @Override
    public ReplyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        ReplyListModel currentReply = repliesArrayList.get(position);
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

        holder.ownerUsername.setText(repliesArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(repliesArrayList.get(position).getOwnerEmail());
        holder.replyDate.setText(TimeAgoFormatter.timeAgo(repliesArrayList.get(position).getDate()));
        holder.replyDescription.setText(repliesArrayList.get(position).getReplyDescription());

        if (repliesArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(repliesArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (repliesArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(repliesArrayList.get(position).getDownvoteIds().size()));
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
                        Log.e("REPLY_LIST_ADAPTER__REPLY", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.GONE);
            holder.upvoteButtonOrange.setVisibility(View.VISIBLE);
            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
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
                        Log.e("REPLY_LIST_ADAPTER__REPLY", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.upvoteButtonOrange.setVisibility(View.GONE);
            holder.downvoteButtonBlack.setVisibility(View.GONE);
            holder.downvoteButtonOrange.setVisibility(View.VISIBLE);

            reloadActivity();
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
                        Log.e("REPLY_LIST_ADAPTER__REPLY", databaseError.getMessage());
                    }
                });
            }

            holder.upvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.upvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
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
                        Log.e("REPLY_LIST_ADAPTER__REPLY", databaseError.getMessage());
                    }
                });
            }

            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);

            reloadActivity();
        });
//
    }

    @Override
    public int getItemCount() {
        return repliesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ownerImage, upvoteButtonBlack, downvoteButtonBlack, upvoteButtonOrange, downvoteButtonOrange;
        TextView ownerUsername, ownerEmail, replyDate, upvoteCount, downvoteCount, replyDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ownerImage = itemView.findViewById(R.id.reply_owner_image);
            ownerUsername = itemView.findViewById(R.id.reply_owner_username);
            ownerEmail = itemView.findViewById(R.id.reply_owner_email);
            replyDate = itemView.findViewById(R.id.reply_date);
            upvoteCount = itemView.findViewById(R.id.reply_upvote_count);
            downvoteCount = itemView.findViewById(R.id.reply_downvote_count);
            replyDescription = itemView.findViewById(R.id.reply_description);

            upvoteButtonBlack = itemView.findViewById(R.id.reply_upvote_button_black);
            downvoteButtonBlack = itemView.findViewById(R.id.reply_downvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.reply_upvote_button_orange);
            downvoteButtonOrange = itemView.findViewById(R.id.reply_downvote_button_orange);
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

