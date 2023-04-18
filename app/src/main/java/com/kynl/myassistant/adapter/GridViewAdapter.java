package com.kynl.myassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kynl.myassistant.R;
import com.kynl.myassistant.model.Device;
import com.kynl.myassistant.service.SocketService;

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
        deviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("onCheckedChanged", "onCheckedChanged: " + deviceList.get(position).getName()  + " " + isChecked );
                Intent intent = new Intent(SocketService.SOCKET_ACTION_REQ);
                intent.putExtra("event", SocketService.SOCKET_REQ_UPDATE_DEVICE);
                intent.putExtra("name", deviceList.get(position).getName());
                intent.putExtra("type", "switch");
                intent.putExtra("status", isChecked ? 1 : 0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        return view;
    }
}
