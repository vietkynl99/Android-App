package com.kynl.myassistant.model;

public class MessageData {
    private boolean mine;
    private boolean error;
    private String message;

    public MessageData(boolean mine, boolean error, String message) {
        this.mine = mine;
        this.error = error;
        this.message = message;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
