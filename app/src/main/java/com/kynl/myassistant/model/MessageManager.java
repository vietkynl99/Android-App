package com.kynl.myassistant.model;

import com.kynl.myassistant.database.DatabaseManager;

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
        messageDataList = DatabaseManager.getInstance().readSavedMessageList();

        // Add welcome message every day
        if (messageDataList.size() == 0) {
            addWelcomeMessage();
        } else if (!messageDataList.get(messageDataList.size() - 1).isToday()) {
            addWelcomeMessage();
        }

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

    private void addWelcomeMessage() {
        replyMessage("Hello!");
        replyMessage("I am your virtual assistant. May I help you?");
    }

    public void replyMessage(String message) {
        MessageData messageData = new MessageData(false, false, message, new Date());
        messageDataList.add(messageData);
        DatabaseManager.getInstance().insertMessage(messageData);
    }

    public void sendMessage(boolean error, String message) {
        MessageData messageData = new MessageData(true, error, message, new Date());
        messageDataList.add(messageData);
        DatabaseManager.getInstance().insertMessage(messageData);
    }

    public void deleteMessage(int position) {
        if (position >= 0 && position <= messageDataList.size()) {
            DatabaseManager.getInstance().removeMessage(messageDataList.get(position));
            messageDataList.remove(position);
        }
    }

    public int deleteSelectedItem() {
        int count = 0;
        for (int position = messageDataList.size() - 1; position >= 0; position--) {
            if (messageDataList.get(position).isSelected()) {
                count++;
                deleteMessage(position);
            }
        }
        return count;
    }
}
