package com.example.konnect_v2;

import java.util.ArrayList;

public class Chat {
    private String chatId;
    private ArrayList<Message> messages;

    public Chat() {
    }

    public Chat(String chatId, ArrayList<Message> messages) {
        this.chatId = chatId;
        this.messages = messages;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
