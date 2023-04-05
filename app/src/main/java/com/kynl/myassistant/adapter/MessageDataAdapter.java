package com.kynl.myassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kynl.myassistant.R;
import com.kynl.myassistant.model.MessageData;

import java.util.List;

public class MessageDataAdapter extends RecyclerView.Adapter<MessageDataAdapter.MessageDataViewHolder> {
    private final String TAG = "MessageDataAdapter";
    private List<MessageData> messageDataList;

    public MessageDataAdapter(List<MessageData> messageDataList) {
        this.messageDataList = messageDataList;
    }

    @NonNull
    @Override
    public MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MessageDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDataViewHolder holder, int position) {
        MessageData messageData = messageDataList.get(position);
        if(messageData == null) {
            return;
        }
        holder.textView1.setText(messageData.getSender());
        holder.textView2.setText(messageData.getSender());
        holder.textView3.setText(messageData.getMessage());
    }

    @Override
    public int getItemCount() {
        return (messageDataList != null) ? messageDataList.size() : 0;
    }

    class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1, textView2, textView3;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
        }
    }
}
