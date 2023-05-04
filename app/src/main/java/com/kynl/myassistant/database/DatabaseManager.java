package com.kynl.myassistant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kynl.myassistant.model.MessageData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.kynl.myassistant.common.CommonUtils.DATE_FORMAT_ISO8601;
import static com.kynl.myassistant.common.CommonUtils.LIMITED_DOWNLOAD_MESSAGES;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_COLUMN_DATETIME;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_COLUMN_ERROR;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_COLUMN_ID;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_COLUMN_MESSAGE;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_COLUMN_MINE;
import static com.kynl.myassistant.database.DatabaseHelper.SQL_MESSAGE_TABLE;

public class DatabaseManager {
    private final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private DatabaseManager() {
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void init(Context context) {
        Log.i(TAG, "init: ");
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
    }

    public void close() {
        if (databaseHelper == null || db == null) {
            Log.e(TAG, "close: object is null");
            return;
        }
        Log.i(TAG, "close: Close database");
        db.close();
    }

    public long insertMessage(MessageData messageData) {
        if (databaseHelper == null || db == null) {
            Log.e(TAG, "insertMessage: object is null");
            return -1;
        }
        Log.e(TAG, "insertMessage: " + messageData.getMessage());
        ContentValues values = new ContentValues();
        values.put(SQL_MESSAGE_COLUMN_MINE, messageData.isMine() ? 1 : 0);
        values.put(SQL_MESSAGE_COLUMN_ERROR, messageData.isError() ? 1 : 0);
        values.put(SQL_MESSAGE_COLUMN_MESSAGE, messageData.getMessage());
        values.put(SQL_MESSAGE_COLUMN_DATETIME, messageData.getDateTimeStringISO8601());
        return db.insert(SQL_MESSAGE_TABLE, null, values);
    }

    public List<MessageData> readSavedMessageList() {
        if (databaseHelper == null || db == null) {
            Log.e(TAG, "readSavedMessage: object is null");
            return null;
        }

        List<MessageData> messageDataList = new ArrayList<>();

        String[] columns = {SQL_MESSAGE_COLUMN_ID, SQL_MESSAGE_COLUMN_MINE, SQL_MESSAGE_COLUMN_ERROR, SQL_MESSAGE_COLUMN_MESSAGE, SQL_MESSAGE_COLUMN_DATETIME};
        String sortOrder = SQL_MESSAGE_COLUMN_ID + " DESC";
        Cursor cursor = db.query(SQL_MESSAGE_TABLE, columns, null, null, null, null, sortOrder, String.valueOf(LIMITED_DOWNLOAD_MESSAGES));

        while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_ID));
            boolean mine = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_MINE)) == 1;
            boolean error = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_ERROR)) == 1;
            String message = cursor.getString(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_MESSAGE));
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_ISO8601);
            String datetimeString = cursor.getString(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_DATETIME));
            Date dateTime = null;
            try {
                dateTime = dateFormat.parse(datetimeString);
            } catch (Exception e) {
            }
            if (dateTime != null) {
                messageDataList.add(new MessageData(mine, error, message, dateTime));
            }
        }

        cursor.close();

        if (messageDataList.size() > 0) {
            Collections.reverse(messageDataList);
        }
        return messageDataList;
    }

    public int removeMessage(MessageData messageData) {
        // Find the last element that satisfies the condition
        int count = 0;
        String[] columns = {SQL_MESSAGE_COLUMN_ID};
        String selection = SQL_MESSAGE_COLUMN_MINE + "=? AND " + SQL_MESSAGE_COLUMN_ERROR + "=? AND " + SQL_MESSAGE_COLUMN_MESSAGE + "=? AND " + SQL_MESSAGE_COLUMN_DATETIME + "=?";
        String[] selectionArgs = new String[]{messageData.isMine() ? "1" : "0", messageData.isError() ? "1" : "0", messageData.getMessage(), messageData.getDateTimeStringISO8601()};
        String sortOrder = SQL_MESSAGE_COLUMN_ID + " DESC";
        String limit = "1";

        Cursor cursor = db.query(SQL_MESSAGE_TABLE, columns, selection, selectionArgs, null, null, sortOrder, limit);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_MESSAGE_COLUMN_ID));
            // Delete it
            selection = SQL_MESSAGE_COLUMN_ID + "=? ";
            selectionArgs = new String[]{String.valueOf(id)};
            count = db.delete(SQL_MESSAGE_TABLE, selection, selectionArgs);
        }
        cursor.close();

        if (count != 1) {
            Log.e(TAG, "removeMessage: Error cannot delete message:" + messageData.getAllDataAsString());
        }
        return count;
    }
}
