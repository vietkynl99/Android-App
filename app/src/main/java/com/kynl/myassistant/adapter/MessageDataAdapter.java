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
        if (messageDataList.get(position).isMine()) {
            holder.layoutMessageMain.setPadding(0, 0, 0, 0);
            holder.textViewMessage.setText(messageData.getMessage());
            holder.layoutAssistantAvatar.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.layoutMessagePosition.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.layoutMessagePosition.setLayoutParams(params);
            holder.layoutMessagePosition.setBackgroundResource(R.drawable.message_background_rectangle_sender);
        }
        holder.textViewMessage.setText(messageData.getMessage());
    }

    @Override
    public int getItemCount() {
        return (messageDataList != null) ? messageDataList.size() : 0;
    }

    class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutMessageMain;
        private FrameLayout layoutAssistantAvatar;
        private RelativeLayout layoutMessagePosition;
        private TextView textViewMessage;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageMain = itemView.findViewById(R.id.layoutMessageMain);
            layoutAssistantAvatar = itemView.findViewById(R.id.layoutAssistantAvatar);
            layoutMessagePosition = itemView.findViewById(R.id.layoutMessagePosition);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
