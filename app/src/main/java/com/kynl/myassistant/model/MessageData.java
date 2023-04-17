package com.kynl.myassistant.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageData {
    private boolean mine;
    private boolean error;
    private String message;
    private Date dateTime;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

    public MessageData(boolean mine, boolean error, String message, Date dateTime) {
        this.mine = mine;
        this.error = error;
        this.message = message;
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getDateTimeString() {
        return dateFormat.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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
