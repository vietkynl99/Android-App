package com.kynl.myassistant.model;

public class MessageData {
    private boolean mine;
    private String message;

    public MessageData(boolean mine, String message) {
        this.mine = mine;
        this.message = message;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
