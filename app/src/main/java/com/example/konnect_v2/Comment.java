package com.example.konnect_v2;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
    String ownerAvatarUrl, ownerId, commentId, memberOfPostId, commentDescription;
    Date date;
    private ArrayList<String> upvoteIds, downvoteIds, replyIds;

    public Comment() {
    }

    public Comment(String ownerAvatarUrl, String ownerId, String commentId, String memberOfPostId, String commentDescription, Date date, ArrayList<String> upvoteIds, ArrayList<String> downvoteIds, ArrayList<String> replyIds) {
        this.ownerAvatarUrl = ownerAvatarUrl;
        this.ownerId = ownerId;
        this.commentId = commentId;
        this.memberOfPostId = memberOfPostId;
        this.commentDescription = commentDescription;
        this.date = date;
        this.upvoteIds = upvoteIds;
        this.downvoteIds = downvoteIds;
        this.replyIds = replyIds;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMemberOfPostId() {
        return memberOfPostId;
    }

    public void setMemberOfPostId(String memberOfPostId) {
        this.memberOfPostId = memberOfPostId;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public void setCommentDescription(String commentDescription) {
        this.commentDescription = commentDescription;
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

    public ArrayList<String> getReplyIds() {
        return replyIds;
    }

    public void setReplyIds(ArrayList<String> replyIds) {
        this.replyIds = replyIds;
    }
}
