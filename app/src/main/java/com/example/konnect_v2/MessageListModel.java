package com.example.konnect_v2;

import java.util.Date;

public class MessageListModel {
    String senderAvatarUrl, senderId, chatId, messageDescription;
    Date messageTimestamp;

    public MessageListModel() {
    }

    public MessageListModel(String senderId, String chatId, String messageDescription, Date messageTimestamp) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.messageDescription = messageDescription;
        this.messageTimestamp = messageTimestamp;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }
}

