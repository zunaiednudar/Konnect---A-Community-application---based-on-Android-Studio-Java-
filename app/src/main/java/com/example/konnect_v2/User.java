package com.example.konnect_v2;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String avatarUrl;
    private String userId;
    private String username;
    private String email;
    private String password;
    private String bio;
    private ArrayList<String> postIds;
    private ArrayList<String> followerIds;
    private ArrayList<String> followingIds;
    private ArrayList<String> memberOfSubKonnectIds;

    //    creating hashmap for creating chatIds and mapping them to specific user (the same chatId is used for the other user as well, with this users reference)
    private HashMap<String, String> chatReferences;

    public User() {
    }

    public User(String avatarUrl, String userId, String username, String email, String password) {
        this.avatarUrl = avatarUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public ArrayList<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(ArrayList<String> postIds) {
        this.postIds = postIds;
    }

    public User(String avatarUrl, String userId, String username, String email, String password, String bio, ArrayList<String> postIds,
                ArrayList<String> followerIds, ArrayList<String> followingIds, ArrayList<String> memberOfSubKonnectIds, HashMap<String, String> chatReferences) {
        this.avatarUrl = avatarUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.postIds = postIds;
        this.followerIds = followerIds;
        this.followingIds = followingIds;
        this.memberOfSubKonnectIds = memberOfSubKonnectIds;
        this.chatReferences = chatReferences;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setFollowerIds(ArrayList<String> followerIds) {
        this.followerIds = followerIds;
    }

    public void setFollowingIds(ArrayList<String> followingIds) {
        this.followingIds = followingIds;
    }

    public void setMemberOfSubKonnectIds(ArrayList<String> memberOfSubKonnectIds) {
        this.memberOfSubKonnectIds = memberOfSubKonnectIds;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getFollowerIds() {
        return followerIds;
    }

    public ArrayList<String> getFollowingIds() {
        return followingIds;
    }

    public ArrayList<String> getMemberOfSubKonnectIds() {
        return memberOfSubKonnectIds;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void follow(User user) {
        String id = user.getUserId();
        this.followingIds.add(id);
    }

    public void unfollow(User user) {
        String id = user.getUserId();
        this.followingIds.remove(id);
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public HashMap<String, String> getChatReferences() {
        return chatReferences;
    }

    public void setChatReferences(HashMap<String, String> chatReferences) {
        this.chatReferences = chatReferences;
    }
}
