package com.example.konnect_v2;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class ReplyListModel {
    String ownerId, replyId, memberOfCommentId, ownerAvatarUrl, ownerName, ownerEmail, replyDescription;
    Date date;
    private ArrayList<String> upvoteIds, downvoteIds;

    public ReplyListModel() {
    }

    public ReplyListModel(String replyId, String ownerId, String memberOfCommentId, String replyDescription, Date date, ArrayList<String> upvoteIds, ArrayList<String> downvoteIds) {
        this.replyId = replyId;
        this.ownerId = ownerId;
        this.memberOfCommentId = memberOfCommentId;
        this.upvoteIds = upvoteIds;
        this.downvoteIds = downvoteIds;
        this.date = date;
        this.replyDescription = replyDescription;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMemberOfCommentId() {
        return memberOfCommentId;
    }

    public void setMemberOfCommentId(String memberOfCommentId) {
        this.memberOfCommentId = memberOfCommentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
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

    public String getReplyDescription() {
        return replyDescription;
    }

    public void setReplyDescription(String replyDescription) {
        this.replyDescription = replyDescription;
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
}


