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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

public class Settings extends AppCompatActivity {
    private final String TAG = "Settings";
    private String serverAddress = "";

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
                showDialog("Server address", "");
            }
        });
    }

    private void readOldSetting() {
        SharedPreferences prefs = getSharedPreferences("SOCKET", Context.MODE_PRIVATE);
        String address = prefs.getString("serverAddress", null);
        if (address != null) {
            serverAddress = address;
        } else {
            serverAddress = "";
        }
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
                    Intent intent = new Intent(getResources().getString(R.string.SOCKET_REQ));
                    intent.putExtra("event", "change_address");
                    intent.putExtra("address", text);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}