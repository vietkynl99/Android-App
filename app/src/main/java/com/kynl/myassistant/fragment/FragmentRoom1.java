package com.kynl.myassistant.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kynl.myassistant.R;
import com.kynl.myassistant.adapter.DeviceRecyclerViewAdapter;
import com.kynl.myassistant.model.Device;
import com.larswerkman.holocolorpicker.OpacityBar;

import java.util.ArrayList;
import java.util.List;

import static com.kynl.myassistant.common.CommonUtils.BROADCAST_ACTION;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_UPDATE_DEVICE;


public class FragmentRoom1 extends Fragment {
    private final String TAG = getClass().getName();
    private List<Device> deviceList;

    public FragmentRoom1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // list
        deviceList = new ArrayList<>();
        deviceList.add(new Device(1, Device.Type.LIGHT, "Light 1", Device.State.ON));
        deviceList.add(new Device(2, Device.Type.LIGHT, "Light 2", Device.State.ON));
        deviceList.add(new Device(3, Device.Type.LIGHT, "Light 3", Device.State.ON));
        deviceList.add(new Device(4, Device.Type.LIGHT, "Light 4", Device.State.ON));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room1, container, false);

        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "onCreateView: getContext null");
            return view;
        }

        // lightOpacityBar
        OpacityBar lightOpacityBar = view.findViewById(R.id.lightOpacityBar);
        lightOpacityBar.setColor(ContextCompat.getColor(getContext(), R.color.white));

        // deviceRecyclerViewAdapter
        DeviceRecyclerViewAdapter deviceRecyclerViewAdapter = new DeviceRecyclerViewAdapter(deviceList);
        deviceRecyclerViewAdapter.setOnSubItemClickListener((position, text) -> {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("event", SOCKET_REQ_UPDATE_DEVICE);
            intent.putExtra("name", deviceList.get(position).getName());
            intent.putExtra("type", "switch");
            intent.putExtra("status", text.equals("1") ? 1 : 0);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        });
        RecyclerView deviceRecyclerView = view.findViewById(R.id.deviceRecyclerView);
        deviceRecyclerView.setAdapter(deviceRecyclerViewAdapter);

        return view;
    }
}