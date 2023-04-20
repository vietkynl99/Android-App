package com.kynl.myassistant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kynl.myassistant.service.SocketService;

import java.util.Set;

import static com.kynl.myassistant.common.CommonUtils.SOCKET_ACTION_REQ;
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
        FrameLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and return to the previous activity
                finish();
            }
        });

        // server address
        LinearLayout serverAddressLayout = findViewById(R.id.serverAddressLayout);
        serverAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readOldSetting();
                showDialog("Server address", "");
            }
        });

        // Weather forecast
        Switch weatherForecastSwitch = findViewById(R.id.weatherForecastSwitch);
        weatherForecastSwitch.setChecked(weatherForecastEnable);
        weatherForecastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weatherForecastEnable = isChecked;
                saveOldSetting();
            }
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

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        final EditText editText = new EditText(getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (serverAddress.isEmpty()) {
            editText.setHint("Enter your text here");
        } else {
            editText.setText(serverAddress);
        }

        builder.setView(editText);

        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString().trim();
                Log.e(TAG, "onClick: text" + text);
                if (!text.isEmpty()) {
                    serverAddress = text;
                    saveOldSetting();
                    Intent intent = new Intent(SOCKET_ACTION_REQ);
                    intent.putExtra("event", SOCKET_REQ_CHANGE_ADDRESS);
                    intent.putExtra("address", text);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}