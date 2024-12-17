package com.example.konnect_v2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<PostListModel> postsArrayList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PostListAdapter(Context context, ArrayList<PostListModel> postsArrayList) {
        this.context = context;
        this.postsArrayList = postsArrayList;
    }

    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        PostListModel currentPost = postsArrayList.get(position);
        int currentPosition = holder.getAdapterPosition();

        if (currentPosition != RecyclerView.NO_POSITION) {
            postsReference.child(currentPost.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (currentPost.getOwnerAvatarUrl() != null) {
            Picasso.get().load(currentPost.getOwnerAvatarUrl()).into(holder.ownerImage);
        } else {
            holder.ownerImage.setImageResource(R.drawable.icon_account_circle_black_24);
        }

        holder.ownerUsername.setText(postsArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(postsArrayList.get(position).getOwnerEmail());
        holder.postDate.setText(TimeAgoFormatter.timeAgo(postsArrayList.get(position).getDate()));

        if (postsArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(postsArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (postsArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(postsArrayList.get(position).getDownvoteIds().size()));
        else holder.downvoteCount.setText("0");

        if (postsArrayList.get(position).getCommentIds() != null)
            holder.commentCount.setText(String.valueOf(postsArrayList.get(position).getCommentIds().size()));
        else holder.commentCount.setText("0");

        holder.postTitle.setText(postsArrayList.get(position).getPostTitle());
        holder.postDescription.setText(postsArrayList.get(position).getPostDescription());

//        Maintaining upvoteButtonBlack
        holder.upvoteButtonBlack.setOnClickListener(v -> {
            if (currentPosition != RecyclerView.NO_POSITION) {
                postsReference.child(currentPost.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            postsReference.child(currentPost.getPostId()).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                postsReference.child(currentPost.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            postsReference.child(currentPost.getPostId()).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                postsReference.child(currentPost.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            postsReference.child(currentPost.getPostId()).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                postsReference.child(currentPost.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Post post = dataSnapshot.getValue(Post.class);

                            if (post != null) {
                                ArrayList<String> downvoteIds = post.getDownvoteIds();

                                String currentUserId = currentUser.getUid();
                                if (downvoteIds != null) downvoteIds.remove(currentUserId);

                                post.setDownvoteIds(downvoteIds);
                            }
                            postsReference.child(currentPost.getPostId()).setValue(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            holder.downvoteButtonBlack.setVisibility(View.VISIBLE);
            holder.downvoteButtonOrange.setVisibility(View.GONE);
        });
//
        holder.commentIconBox.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostLongFormActivity.class);
            intent.putExtra("postId", currentPost.getPostId());
            context.startActivity(intent);
        });

        holder.shareIconBox.setOnClickListener(v -> {
            String postUrl = "https://konnect-a6216-default-rtdb.firebaseio.com/posts/" + currentPost.getPostId();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, postUrl);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ownerImage, upvoteButtonBlack, upvoteButtonOrange, downvoteButtonBlack, downvoteButtonOrange;
        TextView ownerUsername, ownerEmail, postDate, postTitle, postDescription, upvoteCount, downvoteCount, commentCount;
        LinearLayout commentIconBox, shareIconBox;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            ownerImage = itemView.findViewById(R.id.owner_image);
            ownerUsername = itemView.findViewById(R.id.owner_username);
            ownerEmail = itemView.findViewById(R.id.owner_email);
            postDate = itemView.findViewById(R.id.post_date);

            upvoteCount = itemView.findViewById(R.id.upvote_count);
            downvoteCount = itemView.findViewById(R.id.downvote_count);
            commentCount = itemView.findViewById(R.id.comment_count);

            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);

            upvoteButtonBlack = itemView.findViewById(R.id.upvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.upvote_button_orange);
            downvoteButtonBlack = itemView.findViewById(R.id.downvote_button_black);
            downvoteButtonOrange = itemView.findViewById(R.id.downvote_button_orange);

            commentIconBox = itemView.findViewById(R.id.item_post_comment_icon_box);
            shareIconBox = itemView.findViewById(R.id.item_post_share_icon_box);

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
}
