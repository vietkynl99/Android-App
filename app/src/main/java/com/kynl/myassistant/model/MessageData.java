package com.kynl.myassistant.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.kynl.myassistant.common.CommonUtils.DATE_FORMAT_ISO8601;

public class MessageData {
    private boolean mine;
    private boolean error;
    private String message;
    private Date dateTime;

    private boolean selected;

    SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT_ISO8601);
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat today12hDateFormat = new SimpleDateFormat("hh:mm a");

    public MessageData(boolean mine, boolean error, String message, Date dateTime) {
        this.mine = mine;
        this.error = error;
        this.message = message;
        this.dateTime = dateTime;
        selected = false;
    }

    // use for debug
    public String getAllDataAsString() {
        return "mine[" + mine + "] error[" + error + "] time[" + getDateTimeStringISO8601() + "] message[" + message + "]";
    }

    public Date getDateTime() {
        return dateTime;
    }

    public boolean isToday() {
        Date today = new Date();
        return todayDateFormat.format(dateTime).equals(todayDateFormat.format(today));
    }

    public String getDateTimeStringISO8601() {
        return iso8601Format.format(dateTime);
    }

    public String getDateTimeString() {
        // check if its today
        if (isToday()) {
            return today12hDateFormat.format(dateTime);
        } else {
            return dateFormat.format(dateTime);
        }
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
