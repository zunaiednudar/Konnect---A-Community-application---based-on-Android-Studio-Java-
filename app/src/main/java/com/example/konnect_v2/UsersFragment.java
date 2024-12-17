package com.example.konnect_v2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UsersFragment extends Fragment implements Support {
    RecyclerView recyclerViewUsers;

    UserListAdapter userListAdapter;
    ArrayList<UserListModel> usersArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerViewUsers = view.findViewById(R.id.recycler_view_search_fragment);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;

//        Creating the usersArrayList
        usersArrayList = new ArrayList<>();

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersArrayList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);

                        if (user != null) {
                            UserListModel userListModel = new UserListModel(user.getUserId(), user.getAvatarUrl(), user.getUsername(), user.getBio(), user.getEmail(), user.getPostIds(), user.getFollowerIds(), user.getFollowingIds());
                            usersArrayList.add(userListModel);
                        }
                        userListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewUsers.setLayoutManager(linearLayoutManager);
        userListAdapter = new UserListAdapter(requireContext(), usersArrayList);
        recyclerViewUsers.setAdapter(userListAdapter);
        return view;
    }


    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}