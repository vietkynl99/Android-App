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
        Log.e(TAG, "onCreate: ");

        messageDataList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            messageDataList.add(new MessageData("Viet Kynl", "Hello " + i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        messageDataAdapter = new MessageDataAdapter(messageDataList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageDataAdapter);

        return view;
    }
}