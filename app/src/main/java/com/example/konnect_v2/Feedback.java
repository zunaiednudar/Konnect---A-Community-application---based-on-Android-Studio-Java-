package com.example.konnect_v2;

import java.util.Date;

public class Feedback {
    int feedbackRating;
    String feedbackDescription, feedbackOwnerUsername, feedbackOwnerEmail, feedbackOwnerAvatarUrl;
    Date feedbackDate;

    Feedback() {
    }

    public Feedback(int feedbackRating, String feedbackDescription, String feedbackOwnerUsername, String feedbackOwnerEmail, String feedbackOwnerAvatarUrl, Date feedbackDate) {
        this.feedbackRating = feedbackRating;
        this.feedbackDescription = feedbackDescription;
        this.feedbackOwnerUsername = feedbackOwnerUsername;
        this.feedbackOwnerEmail = feedbackOwnerEmail;
        this.feedbackOwnerAvatarUrl = feedbackOwnerAvatarUrl;
        this.feedbackDate = feedbackDate;
    }

    public int getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(int feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackDescription() {
        return feedbackDescription;
    }

    public void setFeedbackDescription(String feedbackDescription) {
        this.feedbackDescription = feedbackDescription;
    }

    public String getFeedbackOwnerUsername() {
        return feedbackOwnerUsername;
    }

    public void setFeedbackOwnerUsername(String feedbackOwnerUsername) {
        this.feedbackOwnerUsername = feedbackOwnerUsername;
    }

    public String getFeedbackOwnerEmail() {
        return feedbackOwnerEmail;
    }

    public void setFeedbackOwnerEmail(String feedbackOwnerEmail) {
        this.feedbackOwnerEmail = feedbackOwnerEmail;
    }

    public String getFeedbackOwnerAvatarUrl() {
        return feedbackOwnerAvatarUrl;
    }

    public void setFeedbackOwnerAvatarUrl(String feedbackOwnerAvatarUrl) {
        this.feedbackOwnerAvatarUrl = feedbackOwnerAvatarUrl;
    }

    public Date getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(Date feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
}
