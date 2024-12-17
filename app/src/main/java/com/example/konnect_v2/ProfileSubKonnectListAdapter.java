package com.example.konnect_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileSubKonnectListAdapter extends RecyclerView.Adapter<ProfileSubKonnectListAdapter.ViewHolder> implements Support {
    Context context;
    ArrayList<ProfileSubKonnectListModel> profileSubKonnectsArrayList;
    private ProfileSubKonnectListAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ProfileSubKonnectListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfileSubKonnectListAdapter(Context context, ArrayList<ProfileSubKonnectListModel> profileSubKonnectsArrayList) {
        this.context = context;
        this.profileSubKonnectsArrayList = profileSubKonnectsArrayList;
    }

    @NonNull
    @Override
    public ProfileSubKonnectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subkonnect_profile, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileSubKonnectListAdapter.ViewHolder holder, int position) {
        ProfileSubKonnectListModel currentSubKonnect = profileSubKonnectsArrayList.get(position);

        holder.subKonnectTitle.setText(profileSubKonnectsArrayList.get(position).getSubKonnectTitle());
        holder.subKonnectDescription.setText(profileSubKonnectsArrayList.get(position).getSubKonnectDescription());

        holder.newSubKonnectTitle.setText(profileSubKonnectsArrayList.get(position).getSubKonnectTitle());
        holder.newSubKonnectDescription.setText(profileSubKonnectsArrayList.get(position).getSubKonnectDescription());

        if (profileSubKonnectsArrayList.get(position).getSubKonnectMemberIds() != null)
            holder.subKonnectMembers.setText(String.valueOf(profileSubKonnectsArrayList.get(position).getSubKonnectMemberIds().size()));
        else holder.subKonnectMembers.setText("0");

        holder.subKonnectEditIcon.setOnClickListener(v -> {
            holder.subKonnectEditIcon.setVisibility(View.GONE);
            holder.subKonnectDeleteIcon.setVisibility(View.GONE);

            holder.subKonnectTitle.setVisibility(View.GONE);
            holder.subKonnectDescription.setVisibility(View.GONE);

            holder.newSubKonnectTitle.setVisibility(View.VISIBLE);
            holder.newSubKonnectDescription.setVisibility(View.VISIBLE);

            holder.updateButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.subKonnectEditIcon.setVisibility(View.VISIBLE);
            holder.subKonnectDeleteIcon.setVisibility(View.VISIBLE);

            holder.subKonnectTitle.setVisibility(View.VISIBLE);
            holder.subKonnectDescription.setVisibility(View.VISIBLE);

            holder.newSubKonnectTitle.setVisibility(View.GONE);
            holder.newSubKonnectDescription.setVisibility(View.GONE);

            holder.updateButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            holder.newSubKonnectTitle.setText(profileSubKonnectsArrayList.get(position).getSubKonnectTitle());
            holder.newSubKonnectDescription.setText(profileSubKonnectsArrayList.get(position).getSubKonnectDescription());
        });

        holder.updateButton.setOnClickListener(v -> {
            holder.subKonnectEditIcon.setVisibility(View.VISIBLE);
            holder.subKonnectDeleteIcon.setVisibility(View.VISIBLE);

            holder.subKonnectTitle.setVisibility(View.VISIBLE);
            holder.subKonnectDescription.setVisibility(View.VISIBLE);

            holder.newSubKonnectTitle.setVisibility(View.GONE);
            holder.newSubKonnectDescription.setVisibility(View.GONE);

            holder.updateButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);

            String newSubKonnectTitle = holder.newSubKonnectTitle.getText().toString().trim();
            String newSubKonnectDescription = holder.newSubKonnectDescription.getText().toString().trim();

            if (newSubKonnectTitle.isEmpty()) {
                Toast.makeText(context, "Title field cannot be empty", Toast.LENGTH_SHORT).show();
                holder.newSubKonnectTitle.setText(profileSubKonnectsArrayList.get(position).getSubKonnectTitle());
                holder.newSubKonnectDescription.setText(profileSubKonnectsArrayList.get(position).getSubKonnectDescription());
                return;
            }

            if (newSubKonnectDescription.isEmpty()) {
                Toast.makeText(context, "Description field cannot be empty", Toast.LENGTH_SHORT).show();
                holder.newSubKonnectTitle.setText(profileSubKonnectsArrayList.get(position).getSubKonnectTitle());
                holder.newSubKonnectDescription.setText(profileSubKonnectsArrayList.get(position).getSubKonnectDescription());
                return;
            }

            subKonnectsReference.child(currentSubKonnect.getSubKonnectId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);

                        if (subKonnect != null) {
                            subKonnect.setSubKonnectTitle(newSubKonnectTitle);
                            subKonnect.setSubKonnectDescription(newSubKonnectDescription);
                            subKonnectsReference.child(currentSubKonnect.getSubKonnectId()).setValue(subKonnect);

                            refresh();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        holder.subKonnectDeleteIcon.setOnClickListener(v -> {
//            Deleting all the respective posts, their comments and replies of those respective comments from Firebase Realtime Database
            deleteAllPostsFromSubKonnect(currentSubKonnect.getSubKonnectId());

//            Deleting subKonnect from their respective member users memberOfSubKonnect arraylist
            deleteSubKonnectFromAllMemberUsers(currentSubKonnect.getSubKonnectId());

//            Deleting subKonnect from subKonnects tree in Firebase Realtime Database
            subKonnectsReference.child(currentSubKonnect.getSubKonnectId()).removeValue().addOnSuccessListener(unused -> {
                Toast.makeText(context, "SubKonnect deleted successfully!", Toast.LENGTH_SHORT).show();
                profileSubKonnectsArrayList.remove(currentSubKonnect);
                notifyDataSetChanged();

                Intent intent = ((Activity) context).getIntent();
                ((Activity) context).finish();
                context.startActivity(intent);
            });
        });
    }

    @Override
    public int getItemCount() {
        return profileSubKonnectsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subKonnectTitle, subKonnectDescription, subKonnectMembers;
        ImageView subKonnectDeleteIcon, subKonnectEditIcon;
        EditText newSubKonnectTitle, newSubKonnectDescription;
        Button updateButton, cancelButton;

        public ViewHolder(@NonNull View itemView, ProfileSubKonnectListAdapter.OnItemClickListener listener) {
            super(itemView);

            subKonnectTitle = itemView.findViewById(R.id.profile_sub_konnect_title);
            subKonnectDescription = itemView.findViewById(R.id.profile_sub_konnect_description);
            subKonnectMembers = itemView.findViewById(R.id.profile_subkonnect_members);

            subKonnectDeleteIcon = itemView.findViewById(R.id.profile_sub_konnect_delete_icon);

            newSubKonnectTitle = itemView.findViewById(R.id.profile_sub_konnect_title_edit);
            newSubKonnectDescription = itemView.findViewById(R.id.profile_sub_konnect_description_edit);
            subKonnectEditIcon = itemView.findViewById(R.id.profile_sub_konnect_update_icon);
            updateButton = itemView.findViewById(R.id.profile_sub_konnect_edit_update_button);
            cancelButton = itemView.findViewById(R.id.profile_sub_konnect_edit_cancel_button);

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


