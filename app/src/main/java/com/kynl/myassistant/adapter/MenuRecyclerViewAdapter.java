package com.kynl.myassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kynl.myassistant.R;

import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.CustomViewHolder> {
    private final List<Integer> menuIconList;
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
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bind(menuIconList.get(position), position == selectedItemPosition);
        holder.menuIconLayout.setOnClickListener(v -> {
            // not use text
            if (onSubItemClickListener != null) {
                onSubItemClickListener.onSubItemClick(position, "");
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

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView menuIcon;
        ViewGroup menuIconLayout;
        View dotHighlight;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
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
