package com.kynl.myassistant.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
    public static final String SOCKET_PREFERENCES = "socket_preferences";
//    from another module to socket service
    public static final String SOCKET_ACTION_REQ = "socket_action_req";
    public static final String SOCKET_REQ_STATUS = "socket_req_status";
    public static final String SOCKET_REQ_SEND_MESS = "socket_req_send_mess";
    public static final String SOCKET_REQ_CHANGE_ADDRESS = "socket_req_change_address";
//    from socket service to another module
    public static final String SOCKET_ACTION_DATA = "socket_action_data";

    private final String TAG = "SocketService";
    private String serverAddressDefault = "http://192.168.100.198";
    private String serverAddress = "";
    private Socket socket;
    private boolean socketStatus = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Read old settings
        readOldSetting();

        // Register broadcast
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(socketStatusBroadcastReceiver,
                new IntentFilter(SOCKET_ACTION_REQ));


        // Connect to server
        connectToSocketSever();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: Stop service");
        super.onDestroy();

        socket.disconnect();
        try {
            unregisterReceiver(socketStatusBroadcastReceiver);
        } catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSocketStatus() {
        Intent intent = new Intent(SocketService.SOCKET_ACTION_DATA);
        intent.putExtra("event", "status");
        intent.putExtra("status", socketStatus ? 1 : 0);
        intent.putExtra("address", serverAddress);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendMessageToUI(String message) {
        Intent intent = new Intent(SocketService.SOCKET_ACTION_DATA);
        intent.putExtra("event", "message");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendToServer(String message) {
        if (socket != null) {
            if (socketStatus) {
                Log.e(TAG, "sendToServer: [MD_message] [" + message + "]");
                socket.emit("MD_message", message);
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
                    case SOCKET_REQ_STATUS:
                        sendSocketStatus();
                        break;
                    case SOCKET_REQ_SEND_MESS:
                        String message = intent.getStringExtra("message");
                        if (message != null) {
                            sendToServer(message);
                        }
                        break;
                    case SOCKET_REQ_CHANGE_ADDRESS:
                        String address = intent.getStringExtra("address");
                        if (address != null) {
                            address = address.trim();
                            if (!address.isEmpty() && address != serverAddress) {
                                Log.i(TAG, "onReceive: Change server address to" + address);
                                serverAddress = address;
                                saveOldSetting();
                                connectToSocketSever();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void connectToSocketSever() {
        if (serverAddress != null && !serverAddress.isEmpty()) {
            Log.i(TAG, "connectToSocketSever: request connect to " + serverAddress);
            if (socket != null) {
                if (socket.connected()) {
                    Log.i(TAG, "connectToSocketSever: Socket is still connected. Disconnect to old server! ");
                    socket.disconnect();
                }
            }
            Log.i(TAG, "connectToSocketSever: Start connect to socket server " + serverAddress);
            try {
                IO.Options options = new IO.Options();
                options.forceNew = true;
                socket = IO.socket(serverAddress, options);
                socket.connect();
            } catch (Exception e) {
                Log.e(TAG, "connectToSocketSever: Cannot connect to " + serverAddress + " : " + e.getCause());
            }

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Exception e = (Exception) args[0];
//                e.printStackTrace();
                    Log.e(TAG, "EVENT_CONNECT_ERROR Error cannot connect to  server " + serverAddress + " : " + e.getCause());
                    socketStatus = false;
                    sendSocketStatus();
                }
            });

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socketStatus = true;
                    Log.i(TAG, "EVENT_CONNECT Connected to server");
                    sendSocketStatus();
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "EVENT_DISCONNECT Disconnected from server");
                    socketStatus = false;
                    sendSocketStatus();
                }
            });

            socket.on("MD_message_res", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "call: [MD_message_res]" + args.length);
                    if (args.length > 0) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String message = data.getString("message");
                            sendMessageToUI(message);
                            Log.e(TAG, "call: message->" + message);
                        } catch (JSONException e) {
                            Log.e(TAG, "call: MD_message_res error : " + e.getCause());
                        }

                    }
                }
            });
        }
    }

    private void saveOldSetting() {
        // server address
        SharedPreferences prefs = getSharedPreferences(SOCKET_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("serverAddress", serverAddress);
        editor.apply();
    }

    private void readOldSetting() {
        SharedPreferences prefs = getSharedPreferences(SOCKET_PREFERENCES, Context.MODE_PRIVATE);

        String address = prefs.getString("serverAddress", null);
        if (address != null) {
            Log.e(TAG, "readOldSetting: server address " + address);
            serverAddress = address;
        } else {
            Log.e(TAG, "readOldSetting: Cannot read server address from old setting. Set default address " + serverAddressDefault);
            serverAddress = serverAddressDefault;
            saveOldSetting();
        }
    }

}