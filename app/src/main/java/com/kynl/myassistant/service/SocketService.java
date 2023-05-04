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
import com.kynl.myassistant.common.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.kynl.myassistant.common.CommonUtils.BROADCAST_ACTION;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_GET_MESSAGE_FROM_SERVER;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_PREFERENCES;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_CHANGE_ADDRESS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_SEND_MESS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_STATUS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_UPDATE_DEVICE;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_STATUS;

public class SocketService extends Service {
    private final String TAG = "SocketService";
    private final String serverAddressDefault = "https://kynl-web.onrender.com/";
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
                new IntentFilter(BROADCAST_ACTION));

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
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("event", SOCKET_STATUS);
        intent.putExtra("status", socketStatus ? 1 : 0);
        intent.putExtra("address", serverAddress);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendMessageToUI(String message) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("event", SOCKET_GET_MESSAGE_FROM_SERVER);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendToServer(String message) {
        if (socket != null) {
            if (socketStatus) {
                Log.d(TAG, "sendToServer: [MD_message] [" + message + "]");
                socket.emit("MD_message", message);
            } else {
                Log.e(TAG, "sendToServer: server is disconnected. Cannot send message!");
            }
        }
    }

    private void updateDataToServer(String data) {
        if (socket != null) {
            if (socketStatus) {
                Log.d(TAG, "sendToServer: [MD_data] [" + data + "]");
                socket.emit("MD_data", data);
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
                        reconnectToSocketSever();
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
                    case SOCKET_REQ_UPDATE_DEVICE:
                        String type = intent.getStringExtra("type");
                        if (type == "switch") {
                            String name = intent.getStringExtra("name");
                            int status = intent.getIntExtra("status", -1);
                            if (name != null && status >= 0) {
                                updateDataToServer(type + ";" + name + ";" + status);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void reconnectToSocketSever() {
        if(socket != null && serverAddress != null && !serverAddress.isEmpty()) {
            if(!socket.connected()) {
                Log.i(TAG, "reconnectToSocketSever: " + serverAddress);
                socket.connect();
            }
        }
    }

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
                    if (args.length > 0) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String message = data.getString("message");
                            sendMessageToUI(message);
                            Log.d(TAG, "call: get message: " + message);
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
            Log.i(TAG, "readOldSetting: server address " + address);
            serverAddress = address;
        } else {
            Log.e(TAG, "readOldSetting: Cannot read server address from old setting. Set default address " + serverAddressDefault);
            serverAddress = serverAddressDefault;
            saveOldSetting();
        }
    }

}