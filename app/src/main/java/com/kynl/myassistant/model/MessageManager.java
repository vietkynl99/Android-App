package com.kynl.myassistant.model;

import com.kynl.myassistant.adapter.MessageDataAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static MessageManager instance;
    private List<MessageData> messageDataList;
    private MessageDataAdapter messageDataAdapter;

    private MessageManager() {
    }

    public static synchronized MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public void reply(String message) {
        messageDataList.add(new MessageData(false, false, message));
    }

    private void send(boolean error, String message) {
        messageDataList.add(new MessageData(true, error, message));
    }

    public void init() {
        messageDataList = new ArrayList<>();
        replyMessage("Hello!");
        replyMessage("I am your virtual assistant. May I help you?");
    }

    public List<MessageData> getMessageDataList() {
        return messageDataList;
    }

    public void replyMessage(String message) {
        reply(message);
    }

    public void sendMessage(boolean error, String message) {
        send(error, message);
    }
}
