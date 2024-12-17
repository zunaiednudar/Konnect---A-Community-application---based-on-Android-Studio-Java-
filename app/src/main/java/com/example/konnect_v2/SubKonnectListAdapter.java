package com.example.konnect_v2;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubKonnectListAdapter extends ArrayAdapter<SubKonnect> implements Support {
    private final Activity context;
    private final List<SubKonnect> subKonnectsList;

    public SubKonnectListAdapter(Activity context, List<SubKonnect> subKonnectsList) {
        super(context, R.layout.item_sub_konnect, subKonnectsList);
        this.context = context;
        this.subKonnectsList = subKonnectsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.item_sub_konnect, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.subKonnectTitle = convertView.findViewById(R.id.sub_konnect_title);
            viewHolder.subKonnectDescription = convertView.findViewById(R.id.sub_konnect_description);
            viewHolder.joinButton = convertView.findViewById(R.id.join_sub_konnect_button);

            viewHolder.leaveButton = convertView.findViewById(R.id.leave_sub_konnect_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        SubKonnect subKonnect = subKonnectsList.get(position);

        viewHolder.subKonnectTitle.setText(subKonnect.getSubKonnectTitle());
        viewHolder.subKonnectDescription.setText(subKonnect.getSubKonnectDescription());

        assert currentUser != null;

        if (subKonnect.getMemberIds() != null && subKonnect.getMemberIds().contains(currentUser.getUid())) {
            viewHolder.joinButton.setVisibility(View.GONE);
            viewHolder.leaveButton.setVisibility(View.VISIBLE);
        } else if (subKonnect.getOwnerId().equals(currentUser.getUid())) {
            String ownerText = "Owner";
            viewHolder.joinButton.setText(ownerText);
            viewHolder.joinButton.setBackgroundColor(context.getResources().getColor(R.color.card_background));
            viewHolder.joinButton.setEnabled(false);
            viewHolder.joinButton.setVisibility(View.VISIBLE);
            viewHolder.leaveButton.setVisibility(View.GONE);
        } else {
            viewHolder.joinButton.setVisibility(View.VISIBLE);
            viewHolder.leaveButton.setVisibility(View.GONE);
        }

        viewHolder.joinButton.setOnClickListener(v -> {
            String newMemberId = currentUser.getUid();
            String subKonnectId = subKonnect.getSubKonnectId();

            subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SubKonnect subKonnect1 = dataSnapshot.getValue(SubKonnect.class);
                        ArrayList<String> subKonnectMemberIds = Objects.requireNonNull(subKonnect1).getMemberIds();

                        if (subKonnectMemberIds == null) subKonnectMemberIds = new ArrayList<>();

                        subKonnectMemberIds.add(newMemberId);
                        subKonnect1.setMemberIds(subKonnectMemberIds);
                        subKonnectsReference.child(subKonnectId).setValue(subKonnect1);
                        showToast("You are now added as a member to the community");

                        viewHolder.joinButton.setVisibility(View.GONE);
                        viewHolder.leaveButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        viewHolder.leaveButton.setOnClickListener(v -> {
            String memberId = currentUser.getUid();
            String subKonnectId = subKonnect.getSubKonnectId();

            subKonnectsReference.child(subKonnectId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SubKonnect subKonnect1 = dataSnapshot.getValue(SubKonnect.class);
                        ArrayList<String> subKonnectMemberIds = Objects.requireNonNull(subKonnect1).getMemberIds();

                        if (subKonnectMemberIds != null) {
                            subKonnectMemberIds.remove(memberId);
                            subKonnect1.setMemberIds(subKonnectMemberIds);
                            subKonnectsReference.child(subKonnectId).setValue(subKonnect1);
                            showToast("You have been removed from the community");

                            viewHolder.joinButton.setVisibility(View.VISIBLE);
                            viewHolder.leaveButton.setVisibility(View.GONE);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubKonnectActivity.class);
            intent.putExtra("subKonnectId", subKonnect.getSubKonnectId());
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView subKonnectTitle, subKonnectDescription;
        Button joinButton, leaveButton;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
