package com.kynl.myassistant.fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kynl.myassistant.R;
import com.kynl.myassistant.adapter.GridViewAdapter;
import com.kynl.myassistant.model.Device;
import com.larswerkman.holocolorpicker.OpacityBar;

import java.util.ArrayList;
import java.util.List;


public class FragmentRoom1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Device> deviceList;


    public FragmentRoom1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LightFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRoom1 newInstance(String param1, String param2) {
        FragmentRoom1 fragment = new FragmentRoom1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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

        // lightOpacityBar
        OpacityBar lightOpacityBar = view.findViewById(R.id.lightOpacityBar);
        lightOpacityBar.setColor(ContextCompat.getColor(getContext(), R.color.white));

        // gridView
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getContext(), deviceList);
        GridView gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(gridViewAdapter);

        return view;
    }
}