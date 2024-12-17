package com.example.konnect_v2;

import java.util.ArrayList;
import java.util.Date;

public class PostListModel {
    String postId, ownerId, ownerAvatarUrl, ownerName, ownerEmail, postTitle, postDescription;
    Date date;
    ArrayList<String> upvoteIds, downvoteIds, commentIds;

    public PostListModel() {
    }

    public PostListModel(String ownerId, String postId, String postTitle, String postDescription, Date date,
                         ArrayList<String> upvoteIds, ArrayList<String> downvoteIds, ArrayList<String> commentIds) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.date = date;
        this.upvoteIds = upvoteIds;
        this.downvoteIds = downvoteIds;
        this.commentIds = commentIds;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<String> getUpvoteIds() {
        return upvoteIds;
    }

    public void setUpvoteIds(ArrayList<String> upvoteIds) {
        this.upvoteIds = upvoteIds;
    }

    public ArrayList<String> getDownvoteIds() {
        return downvoteIds;
    }

    public void setDownvoteIds(ArrayList<String> downvoteIds) {
        this.downvoteIds = downvoteIds;
    }

    public ArrayList<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(ArrayList<String> commentIds) {
        this.commentIds = commentIds;
    }
}
