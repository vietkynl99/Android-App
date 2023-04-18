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
        suggestionDataList.add("Suggestion 1");
        suggestionDataList.add("Suggestion 2");
        suggestionDataList.add("Suggestion 3");
        suggestionDataList.add("Suggestion 4");
        suggestionDataList.add("Suggestion 5");
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
