package com.kynl.myassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kynl.myassistant.R;
import com.kynl.myassistant.model.Device;

import java.util.List;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.CustomViewHolder> {
    private final String TAG = "DeviceRecyclerViewAdapter";
    private List<Device> deviceList;
    private OnSubItemClickListener onSubItemClickListener;

    public DeviceRecyclerViewAdapter(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_recycler_view_item, parent, false);
        return new CustomViewHolder(view, onSubItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bind(position, deviceList.get(position).getName(), deviceList.get(position).getState());
        holder.deviceState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSubItemClickListener != null) {
                    onSubItemClickListener.onSubItemClick(position, isChecked ? "1" : "0");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (deviceList != null) ? deviceList.size() : 0;
    }

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private OnSubItemClickListener onSubItemClickListener;
        View itemView;
        TextView deviceName;
        Switch deviceState;

        public CustomViewHolder(@NonNull View itemView, OnSubItemClickListener onSubItemClickListener) {
            super(itemView);
            this.onSubItemClickListener = onSubItemClickListener;
            this.itemView = itemView;
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceState = itemView.findViewById(R.id.deviceState);
        }

        public void bind(int position, String name, Device.State state) {
            int index = position % 4;
            itemView.setBackgroundResource((index == 0 || index == 3) ? R.drawable.device_background_1 : R.drawable.device_background_2);
            deviceName.setText(name);
            deviceState.setChecked(state == Device.State.ON);
        }
    }
}