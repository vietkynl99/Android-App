package com.kynl.myassistant.model;

import com.kynl.myassistant.adapter.MessageDataAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageManager {
    private static MessageManager instance;
    private List<MessageData> messageDataList;
    private List<String> suggestionDataList;

    private MessageManager() {
    }

    public static synchronized MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public void init() {
        messageDataList = new ArrayList<>();
        replyMessage("Hello!");
        replyMessage("I am your virtual assistant. May I help you?");

        suggestionDataList = new ArrayList<>();
        suggestionDataList.add("Turn on the lamp 1");
        suggestionDataList.add("Turn on the lamp 2");
        suggestionDataList.add("Turn off the lamp 1");
        suggestionDataList.add("Turn off the lamp 2");
    }

    public List<MessageData> getMessageDataList() {
        return messageDataList;
    }

    public List<String> getSuggestionDataList() {
        return suggestionDataList;
    }

    public void replyMessage(String message) {
        messageDataList.add(new MessageData(false, false, message, new Date()));
    }

    public void sendMessage(boolean error, String message) {
        messageDataList.add(new MessageData(true, error, message, new Date()));
    }
}
