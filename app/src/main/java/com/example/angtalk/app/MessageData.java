package com.example.angtalk.app;

public class MessageData {
    private String sender;
    private String receiver;
    private String messageData;

    public void setData(String sender, String receiver, String messageData) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageData = messageData;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageData() {
        return messageData;
    }
}
