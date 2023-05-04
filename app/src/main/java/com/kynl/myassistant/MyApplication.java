package com.kynl.myassistant;

import android.app.Application;
import android.util.Log;

import com.kynl.myassistant.database.DatabaseManager;
import com.kynl.myassistant.model.MessageManager;

public class MyApplication extends Application {
    private final String TAG = getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: " );

        DatabaseManager.getInstance().init(getApplicationContext());
        MessageManager.getInstance().init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e(TAG, "onTerminate: " );
    }
}
