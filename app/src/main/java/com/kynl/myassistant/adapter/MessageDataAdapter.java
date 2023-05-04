package com.kynl.myassistant.adapter;

import android.animation.FloatEvaluator;
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
import com.kynl.myassistant.model.MessageManager;

import java.util.Date;
import java.util.List;

public class MessageDataAdapter extends RecyclerView.Adapter<MessageDataAdapter.MessageDataViewHolder> {
    private final String TAG = "MessageDataAdapter";
    private List<MessageData> messageDataList;
    private OnSubItemClickListener onSubItemClickListener;
    private OnSubItemLongClickListener onSubItemLongClickListener;
    private boolean isAdvanceMode = false;

    public MessageDataAdapter(List<MessageData> messageDataList) {
        this.messageDataList = messageDataList;
    }

    @NonNull
    @Override
    public MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recycler_view_item, parent, false);
        return new MessageDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDataViewHolder holder, int position) {
        MessageData messageData = messageDataList.get(position);

        holder.layoutAssistantAvatar.setVisibility(messageData.isMine() ? View.GONE : View.VISIBLE);
        holder.layoutPartnerMessagePosition.setVisibility(messageData.isMine() ? View.GONE : View.VISIBLE);
        holder.layoutMyMessagePosition.setVisibility(messageData.isMine() ? View.VISIBLE : View.GONE);
        holder.layoutMyMessageError.setVisibility(messageData.isError() ? View.VISIBLE : View.GONE);
        if (messageData.isMine()) {
            holder.textViewMyMessage.setText(messageData.getMessage());
            holder.layoutMyMessageShape.setSelected(messageData.isSelected());
        } else {
            holder.textViewPartnerMessage.setText(messageData.getMessage());
            holder.layoutPartnerMessagePosition.setSelected(messageData.isSelected());
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

        if (onSubItemClickListener != null) {
            holder.layoutPartnerMessagePosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdvanceMode) {
                        onSubItemClickListener.onSubItemClick(position, messageData.getMessage());
                    } else {
                        holder.dateTimeText.setVisibility(holder.dateTimeText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                }
            });
            holder.layoutMyMessageShape.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdvanceMode) {
                        onSubItemClickListener.onSubItemClick(position, messageData.getMessage());
                    } else {
                        holder.dateTimeText.setVisibility(holder.dateTimeText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                }
            });
        }

        // advance menu
        if (onSubItemLongClickListener != null) {
            holder.layoutPartnerMessagePosition.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onSubItemLongClickListener.onSubItemLongClick(position, messageData.getMessage());
                    return true;
                }
            });
            holder.layoutMyMessageShape.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onSubItemLongClickListener.onSubItemLongClick(position, messageData.getMessage());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (messageDataList != null) ? messageDataList.size() : 0;
    }

    public void setAdvanceMode(boolean isAdvanceMode) {
        this.isAdvanceMode = isAdvanceMode;
    }

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    public void setOnSubItemLongClickListener(OnSubItemLongClickListener onSubItemLongClickListener) {
        this.onSubItemLongClickListener = onSubItemLongClickListener;
    }

    public void select(int position) {
        if (position >= 0 && position < messageDataList.size()) {
            messageDataList.get(position).setSelected(true);
            notifyItemChanged(position);
        }
    }

    public void toggleSelect(int position) {
        if (position >= 0 && position < messageDataList.size()) {
            messageDataList.get(position).setSelected(!messageDataList.get(position).isSelected());
            notifyItemChanged(position);
        }
    }

    public void exitAdvanceMenu() {
        for (int position = 0; position < messageDataList.size(); position++) {
            if (messageDataList.get(position).isSelected()) {
                messageDataList.get(position).setSelected(false);
                notifyItemChanged(position);
            }
        }
    }

    public int getSelectedItemCount() {
        int count = 0;
        for (int position = 0; position < messageDataList.size(); position++) {
            if (messageDataList.get(position).isSelected()) {
                count++;
            }
        }
        return count;
    }

    public String getFirstSelectedItemString() {
        for (int position = 0; position < messageDataList.size(); position++) {
            if (messageDataList.get(position).isSelected()) {
                return messageDataList.get(position).getMessage();
            }
        }
        return "";
    }

    public void updateItemInserted() {
        if (messageDataList.size() > 0) {
            notifyItemInserted(messageDataList.size() - 1);
        }
    }

    class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout layoutAssistantAvatar;
        private LinearLayout layoutPartnerMessagePosition, layoutMyMessagePosition, layoutMyMessageError, layoutMyMessageShape;
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

        }
    }
}
