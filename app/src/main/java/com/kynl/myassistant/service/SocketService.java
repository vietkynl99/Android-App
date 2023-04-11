package com.kynl.myassistant.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kynl.myassistant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {

    private final String TAG = "SocketService";
    private String serverAddress = "http://192.168.1.2";
    private Socket socket;
    private boolean socketStatus = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register broadcast
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(socketStatusBroadcastReceiver,
                new IntentFilter(getResources().getString(R.string.SOCKET_REQ)));


        // Connect to server
        Log.i(TAG, "onCreate: Start connect to socket server");
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            socket = IO.socket(serverAddress, options);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Error cannot connect to socket server");
                Exception ex = (Exception) args[0];
                ex.printStackTrace();
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketStatus = true;
                Log.i(TAG, "Connected to server");
                sendSocketStatus();
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketStatus = false;
                Log.i(TAG, "Disconnected from server");
                sendSocketStatus();
            }
        });

        socket.on("MD_message_res", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: [MD_message_res]" + args.length);
                if (args.length > 0) {
                    JSONObject data = (JSONObject) args[0];
//                    Log.e(TAG, "call: data->" + data.toString());
                    try {
                        String message = data.getString("message");
                        sendMessageToUI(message);
                        Log.e(TAG, "call: message->" + message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: Stop service");
        super.onDestroy();

        socket.disconnect();
        unregisterReceiver(socketStatusBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSocketStatus() {
        Intent intent = new Intent(getResources().getString(R.string.SOCKET_DATA));
        intent.putExtra("event", "status");
        intent.putExtra("status", socketStatus ? 1 : 0);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendMessageToUI(String message) {
        Intent intent = new Intent(getResources().getString(R.string.SOCKET_DATA));
        intent.putExtra("event", "message");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendToServer(String message) {
        if (socket != null) {
            if (socketStatus) {
                Log.e(TAG, "sendToServer: [MD_message] [" + message + "]");
                socket.emit("MD_message", message, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "call: ..............");
                    }
                });
            } else {
                Log.e(TAG, "sendToServer: server is disconnected. Cannot send message!");
            }
        }
    }

    private BroadcastReceiver socketStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra("event");
            if (event != null) {
                switch (event) {
                    case "req_status":
                        sendSocketStatus();
                        break;
                    case "send_mess":
                        String message = intent.getStringExtra("message");
                        if (message != null) {
                            sendToServer(message);
                        }
                    default:
                        break;
                }
            }
        }
    };
}