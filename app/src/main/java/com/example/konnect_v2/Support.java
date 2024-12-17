package com.example.konnect_v2;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public interface Support {
    String TAG = "NEWS_FRAGMENT__NEWS";
    String API_URL = "https://seeking-alpha.p.rapidapi.com/news/v2/list?size=20&category=market-news%3A%3Atechnology&number=1";
    String API_KEY = "9adcdc66d2mshe625e02ecd919a3p1028c2jsn799d33b416c8";
    String API_HOST = "seeking-alpha.p.rapidapi.com";

    String[] impressions = {"Very bad", "Bad", "Neutral", "Nice", "Excellent!"};

    String adminEmail = "admin123@gmail.com";
    String adminPassword = "admin123";

    String phoneNumber = "+8801978243409";
    String emailAddress = "zunaied4821@gmail.com";
    String facebookUrl = "https://www.facebook.com/phantomuser.0011/";
    String instagramUrl = "https://www.instagram.com/zunaiednudar/";
    String twitterUrl = "https://x.com/ZunaiedNudar";
    String linkedinUrl = "https://www.linkedin.com/in/zunaied-nudar-a1226525b";
    String githubUrl = "https://github.com/zunaiednudar";

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("root/users");
    DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("root/posts");
    DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("root/comments");
    DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("root/chats");
    DatabaseReference repliesReference = FirebaseDatabase.getInstance().getReference("root/replies");
    DatabaseReference subKonnectsReference = FirebaseDatabase.getInstance().getReference("root/subKonnects");
    DatabaseReference feedbacksReference = FirebaseDatabase.getInstance().getReference("root/feedbacks");

    default void deleteReplyIdFromComment(String commentId, String replyId) {
        commentsReference.child(commentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    if (comment != null) {
                        ArrayList<String> commentIds = comment.getReplyIds();

                        if (commentIds != null) {
                            commentIds.remove(replyId);
                            comment.setReplyIds(commentIds);
                        }

                        commentsReference.child(commentId).setValue(comment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void deletePostIdFromUser(String userId, String postId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        ArrayList<String> postIds = user.getPostIds();

                        if (postIds != null) {
                            postIds.remove(postId);
                            user.setPostIds(postIds);
                        }

                        usersReference.child(userId).setValue(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void deleteCommentIdFromPost(String postId, String commentId) {
        postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post != null) {
                        ArrayList<String> commentIds = post.getCommentIds();

                        if (commentIds != null) {
                            commentIds.remove(commentId);
                            post.setCommentIds(commentIds);
                        }

                        postsReference.child(postId).setValue(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void deleteSubKonnectFromAllMemberUsers(String subKonnectId) {
//            Deleting subKonnect from their respective member users memberOfSubKonnect arraylist
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.exists()) {
                            User user = childSnapshot.getValue(User.class);

                            if (user != null) {
                                ArrayList<String> subKonnectIds = user.getMemberOfSubKonnectIds();

                                if (subKonnectIds != null && subKonnectIds.contains(subKonnectId)) {
                                    subKonnectIds.remove(subKonnectId);
                                    user.setMemberOfSubKonnectIds(subKonnectIds);
                                    usersReference.child(user.getUserId()).setValue(user);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    Deleting all posts respective to a subKonnect
    default void deleteAllPostsFromSubKonnect(String subKonnectId) {
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.exists()) {
                            Post post = childSnapshot.getValue(Post.class);

                            if (post != null && Objects.equals(post.getSubKonnectBelongsToId(), subKonnectId)) {
//                                Deleting the postId from respective postId arrayList of its owner
                                deletePostIdFromUser(post.getOwnerId(), post.getPostId());

//                                Deleting all the comments respective of the specific post in Firebase Realtime Database
                                deleteAllCommentsFromPost(post.getPostId());

//                                Deleting the postId from postIds tree Firebase Realtime Database
                                postsReference.child(post.getPostId()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void deleteAllCommentsFromPost(String postId) {
        commentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.exists()) {
                            Comment comment = childSnapshot.getValue(Comment.class);

                            if (comment != null && Objects.equals(comment.getMemberOfPostId(), postId)) {
//                                Deleting all the replies respective to this comment
                                deleteAllRepliesFromComment(comment.getCommentId());

                                commentsReference.child(comment.getCommentId()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void deleteAllRepliesFromComment(String commentId) {
        repliesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.exists()) {
                            Reply reply = childSnapshot.getValue(Reply.class);

                            if (reply != null && Objects.equals(reply.getMemberOfCommentId(), commentId)) {
                                repliesReference.child(reply.getReplyId()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    default void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
