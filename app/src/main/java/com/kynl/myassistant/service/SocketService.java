package com.kynl.myassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {

    private Socket socket;
    private String serverAddress = "http://192.168.10.110";
    private final String TAG = "SocketService";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate: Start connect to socket server" );

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
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Error Timeout cannot connect to socket server");
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "Connected to server");
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "Disconnected from server");
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}