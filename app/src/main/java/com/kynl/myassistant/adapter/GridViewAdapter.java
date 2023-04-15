package com.kynl.myassistant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.kynl.myassistant.R;
import com.kynl.myassistant.model.Device;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Device> deviceList;

    public GridViewAdapter(Context context, List<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return (deviceList != null) ? deviceList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.gridview_item, null);
        int index = position % 4;
        view.setBackgroundResource((index == 0 || index == 3) ? R.drawable.device_background_1 : R.drawable.device_background_2);
        TextView deviceName = view.findViewById(R.id.deviceName);
        deviceName.setText(deviceList.get(position).getName());
        Switch deviceStatus = view.findViewById(R.id.deviceStatus);
        deviceStatus.setChecked(deviceList.get(position).getStatus() == Device.State.ON);
        return view;
    }
}
