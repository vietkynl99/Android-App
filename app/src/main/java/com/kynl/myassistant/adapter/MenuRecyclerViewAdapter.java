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
        holder.menuIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemPosition != position) {
                    if (selectedItemPosition >= 0 && selectedItemPosition < getItemCount()) {
                        notifyItemChanged(selectedItemPosition);
                    }
                    selectedItemPosition = position;
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (menuIconList != null) ? menuIconList.size() : 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView menuIcon;
        LinearLayout menuIconLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            menuIcon = itemView.findViewById(R.id.menuIcon);
            menuIconLayout = itemView.findViewById(R.id.menuIconLayout);
        }

        public void bind(int iconId, boolean isSelected) {
            menuIcon.setImageResource(iconId);
            menuIconLayout.setSelected(isSelected);
        }
    }
}
