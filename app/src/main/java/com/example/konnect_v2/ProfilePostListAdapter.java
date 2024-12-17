package com.example.konnect_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ProfilePostListAdapter extends RecyclerView.Adapter<ProfilePostListAdapter.ViewHolder> implements Support, TimeAgoFormatter {
    Context context;
    ArrayList<PostListModel> profilePostArrayList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfilePostListAdapter(Context context, ArrayList<PostListModel> profilePostArrayList) {
        this.context = context;
        this.profilePostArrayList = profilePostArrayList;
    }

    @NonNull
    @Override
    public ProfilePostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_profile, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostListAdapter.ViewHolder holder, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        PostListModel currentPost = profilePostArrayList.get(position);
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

        holder.ownerUsername.setText(profilePostArrayList.get(position).getOwnerName());
        holder.ownerEmail.setText(profilePostArrayList.get(position).getOwnerEmail());
        holder.postDate.setText(TimeAgoFormatter.timeAgo(profilePostArrayList.get(position).getDate()));

        if (profilePostArrayList.get(position).getUpvoteIds() != null)
            holder.upvoteCount.setText(String.valueOf(profilePostArrayList.get(position).getUpvoteIds().size()));
        else holder.upvoteCount.setText("0");
        if (profilePostArrayList.get(position).getDownvoteIds() != null)
            holder.downvoteCount.setText(String.valueOf(profilePostArrayList.get(position).getDownvoteIds().size()));
        else holder.downvoteCount.setText("0");

        if (profilePostArrayList.get(position).getCommentIds() != null)
            holder.commentCount.setText(String.valueOf(profilePostArrayList.get(position).getCommentIds().size()));
        else holder.commentCount.setText("0");

        holder.postTitle.setText(profilePostArrayList.get(position).getPostTitle());
        holder.postDescription.setText(profilePostArrayList.get(position).getPostDescription());

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

        holder.postDeleteIcon.setOnClickListener(v -> {
//            Deleting the postId from postId arraylist in user class in Firebase Realtime Database
            deletePostIdFromUser(currentPost.getOwnerId(), currentPost.getPostId());

//            Deleting all the respective comments and replies respective those comments from Firebase Realtime Database
            deleteAllCommentsFromPost(currentPost.getPostId());

//            Deleting the postId from posts tree in Firebase Realtime Database
            postsReference.child(currentPost.getPostId()).removeValue().addOnSuccessListener(unused -> {
                Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                profilePostArrayList.remove(currentPost);
                notifyDataSetChanged();

                Intent intent = ((Activity) context).getIntent();
                ((Activity) context).finish();
                context.startActivity(intent);
            });
        });

        holder.postUpdateIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostEditActivity.class);
            intent.putExtra("postId", currentPost.getPostId());
            intent.putExtra("postTitle", currentPost.getPostTitle());
            intent.putExtra("postDescription", currentPost.getPostDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return profilePostArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ownerImage, upvoteButtonBlack, upvoteButtonOrange, downvoteButtonBlack, downvoteButtonOrange, postUpdateIcon, postDeleteIcon;
        TextView ownerUsername, ownerEmail, postDate, postTitle, postDescription, upvoteCount, downvoteCount, commentCount;
        LinearLayout commentIconBox;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            ownerImage = itemView.findViewById(R.id.post_profile_owner_image);
            ownerUsername = itemView.findViewById(R.id.post_profile_owner_username);
            ownerEmail = itemView.findViewById(R.id.post_profile_owner_email);
            postDate = itemView.findViewById(R.id.profile_post_date);

            upvoteCount = itemView.findViewById(R.id.post_profile_upvote_count);
            downvoteCount = itemView.findViewById(R.id.post_profile_downvote_count);
            commentCount = itemView.findViewById(R.id.post_profile_comment_count);

            postTitle = itemView.findViewById(R.id.profile_post_title);
            postDescription = itemView.findViewById(R.id.profile_post_description);

            upvoteButtonBlack = itemView.findViewById(R.id.post_profile_upvote_button_black);
            upvoteButtonOrange = itemView.findViewById(R.id.post_profile_upvote_button_orange);
            downvoteButtonBlack = itemView.findViewById(R.id.post_profile_downvote_button_black);
            downvoteButtonOrange = itemView.findViewById(R.id.post_profile_downvote_button_orange);

            commentIconBox = itemView.findViewById(R.id.item_post_profile_comment_icon_box);
            postUpdateIcon = itemView.findViewById(R.id.profile_post_update_icon);
            postDeleteIcon = itemView.findViewById(R.id.profile_post_delete_icon);

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

