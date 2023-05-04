package com.kynl.myassistant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import static com.kynl.myassistant.common.CommonUtils.BROADCAST_ACTION;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_PREFERENCES;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_CHANGE_ADDRESS;

public class Settings extends AppCompatActivity {
    private final String TAG = "Settings";
    private String serverAddress = "";
    private boolean weatherForecastEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        readOldSetting();

        // back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Close the current activity and return to the previous activity
            finish();
        });

        // server address
        LinearLayout serverAddressLayout = findViewById(R.id.serverAddressLayout);
        serverAddressLayout.setOnClickListener(v -> {
            readOldSetting();
            showDialog();
        });

        // Weather forecast
        Switch weatherForecastSwitch = findViewById(R.id.weatherForecastSwitch);
        weatherForecastSwitch.setChecked(weatherForecastEnable);
        weatherForecastSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            weatherForecastEnable = isChecked;
            saveOldSetting();
        });
    }

    private void readOldSetting() {
        SharedPreferences prefs = getSharedPreferences(SOCKET_PREFERENCES, Context.MODE_PRIVATE);
        // serverAddress
        String address = prefs.getString("serverAddress", null);
        if (address != null) {
            serverAddress = address;
        } else {
            serverAddress = "";
        }
        // weatherForecast
        weatherForecastEnable = prefs.getBoolean("weatherForecastEnable", false);
    }

    private void saveOldSetting() {
        SharedPreferences prefs = getSharedPreferences(SOCKET_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // server address
        editor.putString("serverAddress", serverAddress);
        // weatherForecast
        editor.putBoolean("weatherForecastEnable", weatherForecastEnable);
        editor.apply();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        final EditText editText = new EditText(getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (serverAddress.isEmpty()) {
            editText.setHint("Enter your text here");
        } else {
            editText.setText(serverAddress);
        }

        builder.setView(editText);

            builder.setTitle("Server address");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("OK", (dialog, which) -> {
            String text = editText.getText().toString().trim();
            Log.e(TAG, "onClick: text" + text);
            if (!text.isEmpty()) {
                serverAddress = text;
                saveOldSetting();
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra("event", SOCKET_REQ_CHANGE_ADDRESS);
                intent.putExtra("address", text);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            dialog.cancel();
        });

        builder.create().show();
    }
}