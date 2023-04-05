package com.kynl.myassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.textViewMessage.setText(messageData.getMessage());
    }

    @Override
    public int getItemCount() {
        return (messageDataList != null) ? messageDataList.size() : 0;
    }

    class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
