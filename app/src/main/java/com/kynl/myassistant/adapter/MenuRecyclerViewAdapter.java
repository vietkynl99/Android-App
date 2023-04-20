package com.kynl.myassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kynl.myassistant.R;
import com.kynl.myassistant.model.MessageData;

import java.util.Date;
import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.CustomViewHolder> {
    private final String TAG = "MenuRecyclerViewAdapter";
    private List<Integer> menuIconList;
    private int selectedItemPosition;
    private OnSubItemClickListener onSubItemClickListener;

    public MenuRecyclerViewAdapter(List<Integer> menuIconList) {
        this.menuIconList = menuIconList;
        selectedItemPosition = -1;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_recycler_view_item, parent, false);
        return new CustomViewHolder(view, onSubItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bind(menuIconList.get(position), position == selectedItemPosition);
        holder.menuIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // not use text
                if (onSubItemClickListener != null) {
                    onSubItemClickListener.onSubItemClick(position, "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (menuIconList != null) ? menuIconList.size() : 0;
    }

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    public void select(int position) {
        if (position >= 0 && position < getItemCount()) {
            if (selectedItemPosition != position) {
                if (selectedItemPosition >= 0 && selectedItemPosition < getItemCount()) {
                    notifyItemChanged(selectedItemPosition);
                }
                selectedItemPosition = position;
                notifyItemChanged(position);
            }
        }
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private OnSubItemClickListener onSubItemClickListener;
        ImageView menuIcon;
        ViewGroup menuIconLayout;
        View dotHighlight;

        public CustomViewHolder(@NonNull View itemView, OnSubItemClickListener onSubItemClickListener) {
            super(itemView);
            this.onSubItemClickListener = onSubItemClickListener;
            menuIcon = itemView.findViewById(R.id.menuIcon);
            menuIconLayout = itemView.findViewById(R.id.menuIconLayout);
            dotHighlight = itemView.findViewById(R.id.dotHighlight);
        }

        public void bind(int iconId, boolean isSelected) {
            menuIcon.setImageResource(iconId);
            menuIconLayout.setSelected(isSelected);
            dotHighlight.setVisibility(isSelected? View.VISIBLE : View.GONE);
        }
    }
}
