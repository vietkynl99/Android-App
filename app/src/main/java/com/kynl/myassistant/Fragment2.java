package com.kynl.myassistant;

import android.animation.FloatEvaluator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kynl.myassistant.adapter.MessageDataAdapter;
import com.kynl.myassistant.model.MessageData;
import com.kynl.myassistant.model.MessageManager;

import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment {

    private static final String TAG = "Fragment2";
    private RecyclerView recyclerView;
    private MessageDataAdapter messageDataAdapter;
    private List<MessageData> messageDataList;
    private boolean socketStatus = false;

    private BroadcastReceiver mBroadcastReceiver;
    private View indicatorLightStatus;
    private TextView activeStatus;

    public Fragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        MessageManager.getInstance().init();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        messageDataList = MessageManager.getInstance().getMessageDataList();
        messageDataAdapter = new MessageDataAdapter(messageDataList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        indicatorLightStatus = view.findViewById(R.id.indicatorLightStatus);
        activeStatus = view.findViewById(R.id.activeStatus);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageDataAdapter);

        ImageButton sendMessageButton = view.findViewById(R.id.sendMessageButton);
        EditText messageEditText = view.findViewById(R.id.messageEditText);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageEditText.getText().toString().trim();
                if (!text.isEmpty()) {
                    sendMessage(text);
                }
                messageEditText.getText().clear();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        // register broadcast
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String event = intent.getStringExtra("event");
                if (event != null) {
                    switch (event) {
                        case "status":
                            int status = intent.getIntExtra("status", -1);
                            if (status >= 0) {
                                Log.e(TAG, "onReceive: get socket status=" + status);
                                socketStatus = status == 1;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateServerStatus();
                                    }
                                });
                            }
                            break;
                        case "message":
                            String message = intent.getStringExtra("message");
                            if(message != null) {
                                Log.e(TAG, "onReceive: get message from server: " + message );
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        replyMessage(message);
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mBroadcastReceiver, new IntentFilter(getResources().getString(R.string.SOCKET_DATA)));

        // send request to socket service
        requestSocketStatusFromService();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        // unregister broadcast
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void requestSocketStatusFromService() {
        Intent intent = new Intent(getResources().getString(R.string.SOCKET_REQ));
        intent.putExtra("event", "req_status");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void sendMessageToServer(String message) {
        Intent intent = new Intent(getResources().getString(R.string.SOCKET_REQ));
        intent.putExtra("event", "send_mess");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void updateServerStatus() {
        if (indicatorLightStatus != null) {
            indicatorLightStatus.setBackgroundResource(socketStatus ? R.drawable.ellipse_12_shape_green : R.drawable.ellipse_12_shape_red);
        }
        if (activeStatus != null) {
            activeStatus.setText(getResources().getString(socketStatus ? R.string.active_status_connected : R.string.active_status_disconnected));
        }
    }

    public void sendMessage(String message) {
        Log.d(TAG, "send message: " + message);
        MessageManager.getInstance().sendMessage(message);
        // update to view
        messageDataAdapter.updateItemInserted();
        if (messageDataAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(messageDataAdapter.getItemCount() - 1);
        }
        // send message to server
        if (socketStatus) {
            sendMessageToServer(message);
        }
    }

    public void replyMessage(String message) {
        MessageManager.getInstance().replyMessage(message);
        // update to view
        messageDataAdapter.updateItemInserted();
        if (messageDataAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(messageDataAdapter.getItemCount() - 1);
        }
    }
}