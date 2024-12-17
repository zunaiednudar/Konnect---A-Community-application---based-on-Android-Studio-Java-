package com.example.konnect_v2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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


public class HomeFragment extends Fragment implements Support {
    RecyclerView recyclerViewPosts;
    PostListAdapter postListAdapter;
    ArrayList<PostListModel> postArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts = view.findViewById(R.id.recycler_view_home_fragment);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;

        postArrayList = new ArrayList<>();

        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postArrayList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Post post = childSnapshot.getValue(Post.class);

                        if (post != null) {
                            subKonnectsReference.child(post.getSubKonnectBelongsToId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);

                                        if (subKonnect != null) {
                                            ArrayList<String> memberIds = subKonnect.getMemberIds();

                                            if (memberIds != null) {
                                                if (memberIds.contains(currentUser.getUid())) {
                                                    PostListModel postListModel = new PostListModel(post.getOwnerId(), post.getPostId(), post.getTitle(), post.getDescription(), post.getDate(), post.getUpvoteIds(), post.getDownvoteIds(), post.getCommentIds());
                                                    postArrayList.add(postListModel);

                                                    int index = postArrayList.size() - 1;
                                                    fetchUserData(index, post.getOwnerId());
                                                }
                                            }

                                            if (subKonnect.getOwnerId().equals(currentUser.getUid())) {
                                                PostListModel postListModel = new PostListModel(post.getOwnerId(), post.getPostId(), post.getTitle(), post.getDescription(), post.getDate(), post.getUpvoteIds(), post.getDownvoteIds(), post.getCommentIds());
                                                postArrayList.add(postListModel);

                                                int index = postArrayList.size() - 1;
                                                fetchUserData(index, post.getOwnerId());
                                            }
                                            postListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } else {
                    showToast("Join a SubKonnect or create one to see posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        postListAdapter = new PostListAdapter(requireContext(), postArrayList);
        recyclerViewPosts.setAdapter(postListAdapter);

        postListAdapter.setOnItemClickListener(position -> {
            PostListModel clickedPost = postArrayList.get(position);
            Intent intent = new Intent(getActivity(), PostLongFormActivity.class);
            intent.putExtra("postId", clickedPost.getPostId());
            startActivity(intent);
        });

        return view;
    }

    public void fetchUserData(int index, String ownerId) {
        usersReference.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (index >= 0 && index < postArrayList.size()) {
                            postArrayList.get(index).setOwnerName(user.getUsername());
                            postArrayList.get(index).setOwnerEmail(user.getEmail());
                            postArrayList.get(index).setOwnerAvatarUrl(user.getAvatarUrl());

                            postListAdapter.notifyItemChanged(index);
                        } else {
                            postArrayList.clear();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast(databaseError.getMessage());
            }
        });
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}