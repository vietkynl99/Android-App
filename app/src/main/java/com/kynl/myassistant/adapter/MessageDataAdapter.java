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

import java.util.Date;
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

        holder.layoutAssistantAvatar.setVisibility(messageData.isMine() ? View.GONE : View.VISIBLE);
        holder.layoutPartnerMessagePosition.setVisibility(messageData.isMine() ? View.GONE : View.VISIBLE);
        holder.layoutMyMessagePosition.setVisibility(messageData.isMine() ? View.VISIBLE : View.GONE);
        holder.layoutMyMessageError.setVisibility(messageData.isError() ? View.VISIBLE : View.GONE);
        holder.layoutMyMessageShape.setBackgroundResource(messageData.isError() ? R.drawable.message_background_rectangle_error : R.drawable.message_background_rectangle_sender);
        if (messageData.isMine()) {
            holder.textViewMyMessage.setText(messageData.getMessage());
        } else {
            holder.textViewPartnerMessage.setText(messageData.getMessage());
        }

        // time
        boolean visibility = true;
        if (position >= 1) {
            Date preMessageDateTime = messageDataList.get(position - 1).getDateTime();
            Date currentDateTime = messageDataList.get(position).getDateTime();
            long diffInMinutes = Math.abs(currentDateTime.getTime() - preMessageDateTime.getTime()) / (60000);
            visibility = diffInMinutes > 30;
        }
        holder.dateTimeText.setText(messageData.getDateTimeString());
        holder.dateTimeText.setVisibility(visibility ? View.VISIBLE : View.GONE);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    holder.dateTimeText.setVisibility(holder.dateTimeText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });
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

    class MessageDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        private FrameLayout layoutAssistantAvatar;
        private RelativeLayout layoutPartnerMessagePosition;
        private LinearLayout layoutMyMessagePosition, layoutMyMessageError, layoutMyMessageShape;
        private TextView textViewPartnerMessage, textViewMyMessage, dateTimeText;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAssistantAvatar = itemView.findViewById(R.id.layoutAssistantAvatar);
            layoutPartnerMessagePosition = itemView.findViewById(R.id.layoutPartnerMessagePosition);
            layoutMyMessagePosition = itemView.findViewById(R.id.layoutMyMessagePosition);
            layoutMyMessageError = itemView.findViewById(R.id.layoutMyMessageError);
            layoutMyMessageShape = itemView.findViewById(R.id.layoutMyMessageShape);
            textViewPartnerMessage = itemView.findViewById(R.id.textViewPartnerMessage);
            textViewMyMessage = itemView.findViewById(R.id.textViewMyMessage);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }
}
