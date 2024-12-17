package com.example.konnect_v2;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class CommentListModel {
    String commentId, memberOfPostId, ownerAvatarUrl, ownerName, ownerEmail, commentDescription;
    Date date;
    private ArrayList<String> upvoteIds, downvoteIds, replyIds;

    public CommentListModel() {
    }

    public CommentListModel(String commentId, String memberOfPostId, String commentDescription, Date date, ArrayList<String> upvoteIds, ArrayList<String> downvoteIds, ArrayList<String> replyIds) {
        this.commentId = commentId;
        this.memberOfPostId = memberOfPostId;
        this.downvoteIds = downvoteIds;
        this.upvoteIds = upvoteIds;
        this.replyIds = replyIds;
        this.date = date;
        this.commentDescription = commentDescription;
    }

    public String getMemberOfPostId() {
        return memberOfPostId;
    }

    public void setMemberOfPostId(String memberOfPostId) {
        this.memberOfPostId = memberOfPostId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

