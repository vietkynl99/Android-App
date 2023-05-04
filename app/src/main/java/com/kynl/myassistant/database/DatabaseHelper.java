package com.kynl.myassistant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "DatabaseHelper";

    private static final int SQL_DATABASE_VERSION = 1;
    private static final String SQL_DATABASE_NAME = "my_assistant.db";

    public static final String SQL_MESSAGE_TABLE = "message_table";
    public static final String SQL_MESSAGE_COLUMN_ID = "id";
    public static final String SQL_MESSAGE_COLUMN_MINE = "mine";
    public static final String SQL_MESSAGE_COLUMN_ERROR = "error";
    public static final String SQL_MESSAGE_COLUMN_MESSAGE = "message";
    public static final String SQL_MESSAGE_COLUMN_DATETIME = "datetime";

    public DatabaseHelper(@Nullable Context context) {
        super(context, SQL_DATABASE_NAME, null, SQL_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: ");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SQL_MESSAGE_TABLE + " (" +
                SQL_MESSAGE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                SQL_MESSAGE_COLUMN_MINE + " INTEGER NOT NULL,\n" +
                SQL_MESSAGE_COLUMN_ERROR + " INTEGER NOT NULL,\n" +
                SQL_MESSAGE_COLUMN_MESSAGE + " TEXT NOT NULL,\n" +
                SQL_MESSAGE_COLUMN_DATETIME + " DATETIME NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: ");
        db.execSQL("DROP TABLE IF EXISTS " + SQL_MESSAGE_TABLE);
        onCreate(db);
    }
}
