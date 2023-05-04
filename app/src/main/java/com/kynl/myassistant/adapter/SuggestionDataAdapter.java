package com.kynl.myassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kynl.myassistant.R;

import java.util.List;

public class SuggestionDataAdapter extends RecyclerView.Adapter<SuggestionDataAdapter.MessageDataViewHolder> {
    private final List<String> suggestionDataList;
    private OnSubItemClickListener onSubItemClickListener;

    public SuggestionDataAdapter(List<String> suggestionDataList) {
        this.suggestionDataList = suggestionDataList;
    }

    @NonNull
    @Override
    public MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_recycler_view_item, parent, false);
        return new MessageDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDataViewHolder holder, int position) {
        holder.suggestionText.setText(suggestionDataList.get(position));

        if (onSubItemClickListener != null) {
            holder.suggestionText.setOnClickListener(v -> onSubItemClickListener.onSubItemClick(position, suggestionDataList.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return (suggestionDataList != null) ? suggestionDataList.size() : 0;
    }

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    static class MessageDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView suggestionText;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.suggestionText);
        }
    }
}
