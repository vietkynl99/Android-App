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

import java.net.URISyntaxException;

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
                new IntentFilter(getResources().getString(R.string.REQ_SOCKET_STATUS)));


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

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Stop service");
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
        Intent intent = new Intent(getResources().getString(R.string.SOCKET_STATUS));
        intent.putExtra(getResources().getString(R.string.SOCKET_STATUS), socketStatus ? 1 : 0);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private BroadcastReceiver socketStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendSocketStatus();
        }
    };
}