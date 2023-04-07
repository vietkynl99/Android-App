package com.kynl.myassistant.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        if (messageData == null) {
            return;
        }

        holder.layoutAssistantAvatar.setVisibility(messageData.isMine() ? View.INVISIBLE : View.VISIBLE);
        holder.layoutPartnerMessagePosition.setVisibility(messageData.isMine() ? View.INVISIBLE : View.VISIBLE);
        holder.layoutMyMessagePosition.setVisibility(messageData.isMine() ? View.VISIBLE : View.INVISIBLE);
        if (messageData.isMine()) {
            holder.textViewMyMessage.setText(messageData.getMessage());
        } else {
            holder.textViewPartnerMessage.setText(messageData.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return (messageDataList != null) ? messageDataList.size() : 0;
    }

    public void updateItemInserted() {
        if (messageDataList.size() > 0) {
            notifyItemInserted(messageDataList.size() - 1);
        }
    }

    class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout layoutAssistantAvatar;
        private RelativeLayout layoutPartnerMessagePosition, layoutMyMessagePosition;
        private TextView textViewPartnerMessage, textViewMyMessage;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAssistantAvatar = itemView.findViewById(R.id.layoutAssistantAvatar);
            layoutPartnerMessagePosition = itemView.findViewById(R.id.layoutPartnerMessagePosition);
            layoutMyMessagePosition = itemView.findViewById(R.id.layoutMyMessagePosition);
            textViewPartnerMessage = itemView.findViewById(R.id.textViewPartnerMessage);
            textViewMyMessage = itemView.findViewById(R.id.textViewMyMessage);
        }
    }
}
