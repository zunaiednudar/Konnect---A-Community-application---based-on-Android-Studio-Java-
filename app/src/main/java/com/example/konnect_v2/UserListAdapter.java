package com.example.konnect_v2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserListModel> arrayList;

    public UserListAdapter(Context context, ArrayList<UserListModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        UserListModel currentUser = arrayList.get(position);

        if (currentUser.getAvatarUrl() != null) {
            Picasso.get().load(currentUser.getAvatarUrl()).into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.icon_account_circle_black_24);
        }

        holder.userUsername.setText(arrayList.get(position).getUserName());
        holder.userEmail.setText(arrayList.get(position).getUserEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileViewActivity.class);
            intent.putExtra("user_id", currentUser.getUserId());
            intent.putExtra("user_name", currentUser.getUserName());
            intent.putExtra("user_bio", currentUser.getUserBio());
            intent.putExtra("user_avatar_url", currentUser.getAvatarUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userUsername;
        TextView userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userUsername = itemView.findViewById(R.id.user_username);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}

