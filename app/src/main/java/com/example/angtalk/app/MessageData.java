package com.example.angtalk.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ganghan-yong on 2014. 3. 23..
 */
public class MessageData {
    private String sender;
    private String receiver;
    private String messageData;

    public void setData(String sender, String receiver, String messageData) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageData = messageData;
    }
}
