package com.example.konnect_v2;

import java.util.ArrayList;

public class UserListModel {
    String userId, avatarUrl, userName, userBio, userEmail;
    ArrayList<String> postIds, followerIds, followingIds;

    UserListModel() {
    }

    public UserListModel(String userId, String avatarUrl, String userName, String userBio, String userEmail, ArrayList<String> postIds, ArrayList<String> followerIds, ArrayList<String> followingIds) {
        this.userId = userId;
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.userBio = userBio;
        this.userEmail = userEmail;
        this.postIds = postIds;
        this.followerIds = followerIds;
        this.followingIds = followingIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ArrayList<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(ArrayList<String> postIds) {
        this.postIds = postIds;
    }

    public ArrayList<String> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(ArrayList<String> followerIds) {
        this.followerIds = followerIds;
    }

    public ArrayList<String> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(ArrayList<String> followingIds) {
        this.followingIds = followingIds;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }
}

