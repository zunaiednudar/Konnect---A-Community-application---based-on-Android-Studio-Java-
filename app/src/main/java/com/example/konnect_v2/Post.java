package com.example.konnect_v2;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String ownerId, postId, subKonnectBelongsToId, title, description;
    Date date;
    private ArrayList<String> upvoteIds, downvoteIds, commentIds;

    public Post() {
    }

    public Post(String ownerId, String subKonnectBelongsToId, String postId, String title, String description, Date date,
                ArrayList<String> upvotesIds, ArrayList<String> downvoteIds, ArrayList<String> commentIds) {
        this.ownerId = ownerId;
        this.subKonnectBelongsToId = subKonnectBelongsToId;
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.upvoteIds = upvotesIds;
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

    public String getSubKonnectBelongsToId() {
        return subKonnectBelongsToId;
    }

    public void setSubKonnectBelongsToId(String subKonnectBelongsToId) {
        this.subKonnectBelongsToId = subKonnectBelongsToId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
