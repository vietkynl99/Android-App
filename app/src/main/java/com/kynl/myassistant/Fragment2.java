package com.kynl.myassistant;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kynl.myassistant.adapter.MessageDataAdapter;
import com.kynl.myassistant.model.MessageData;

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
        messageDataList = new ArrayList<>();
        messageDataList.add(new MessageData(false, "Hello!"));
        messageDataList.add(new MessageData(false, "Good morning!"));
        messageDataList.add(new MessageData(false, "I am your virtual assistant. May I help you?"));
        messageDataList.add(new MessageData(true, "Hi. Nice to meet you!"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
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
                    Log.e(TAG, "send message: " + text);
                    messageDataList.add(new MessageData(true, text));
                    messageDataAdapter.notifyItemInserted(messageDataList.size() - 1);
                    recyclerView.smoothScrollToPosition(messageDataList.size() - 1);
                }
                messageEditText.getText().clear();
            }
        });

        return view;
    }
}