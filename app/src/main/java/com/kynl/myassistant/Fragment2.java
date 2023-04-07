package com.kynl.myassistant;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

    public Fragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessageManager.getInstance().init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        messageDataList = MessageManager.getInstance().getMessageDataList();
        messageDataAdapter = new MessageDataAdapter(messageDataList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            replyMessage(text);
                        }
                    }, 1000);
                }
                messageEditText.getText().clear();
            }
        });

        return view;
    }

    public void sendMessage(String message) {
        Log.d(TAG, "send message: " + message);
        MessageManager.getInstance().sendMessage(message);
        // update to view
        messageDataAdapter.updateItemInserted();
        if (messageDataAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(messageDataAdapter.getItemCount() - 1);
        }
    }

    public void replyMessage(String inputMessage) {
        String response = "You sent: " + inputMessage;
        MessageManager.getInstance().replyMessage(response);
        // update to view
        messageDataAdapter.updateItemInserted();
        if (messageDataAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(messageDataAdapter.getItemCount() - 1);
        }
    }
}