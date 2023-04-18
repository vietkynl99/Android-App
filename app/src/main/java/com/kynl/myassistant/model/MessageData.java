package com.kynl.myassistant.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MessageData {
    private boolean mine;
    private boolean error;
    private String message;
    private Date dateTime;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat today12hDateFormat = new SimpleDateFormat("hh:mm a");

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
        Date today = new Date();
        // check if its today
        if (todayDateFormat.format(dateTime).equals(todayDateFormat.format(today))) {
            return today12hDateFormat.format(dateTime);
        } else {
            return dateFormat.format(dateTime);
        }
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
