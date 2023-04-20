package com.kynl.myassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kynl.myassistant.adapter.MenuRecyclerViewAdapter;
import com.kynl.myassistant.adapter.OnSubItemClickListener;
import com.kynl.myassistant.fragment.FragmentRoom4;
import com.kynl.myassistant.fragment.FragmentRoom1;
import com.kynl.myassistant.fragment.FragmentRoom3;
import com.kynl.myassistant.fragment.FragmentRoom5;
import com.kynl.myassistant.fragment.FragmentRoom2;
import com.kynl.myassistant.model.MenuElement;
import com.kynl.myassistant.service.SocketService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private List<MenuElement> menuElementList;
    private List<Integer> menuElementIconIdList;
    MenuRecyclerViewAdapter menuRecyclerViewAdapter;

    private FragmentManager fragmentManager;
    private int pre_fragment_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Socket service
        Log.i(TAG, "onCreate: Start service");
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);

        // menu
        menuElementList = new ArrayList<>();
        menuElementList.add(new MenuElement("Room 1", R.drawable.living_room, FragmentRoom1.class.getName()));
        menuElementList.add(new MenuElement("Room 2", R.drawable.security, FragmentRoom2.class.getName()));
        menuElementList.add(new MenuElement("Room 3", R.drawable.surgery_room, FragmentRoom3.class.getName()));
        menuElementList.add(new MenuElement("Room 4", R.drawable.diningtable, FragmentRoom4.class.getName()));
        menuElementList.add(new MenuElement("Room 5", R.drawable.bedroom, FragmentRoom5.class.getName()));

        menuElementIconIdList = new ArrayList<>();
        for (MenuElement menuElement : menuElementList) {
            menuElementIconIdList.add(menuElement.getIconId());
        }

        menuRecyclerViewAdapter = new MenuRecyclerViewAdapter(menuElementIconIdList);
        menuRecyclerViewAdapter.setOnSubItemClickListener(new OnSubItemClickListener() {
            @Override
            public void onSubItemClick(int position, String text) {
                changeFragment(position);
            }
        });
        RecyclerView menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        menuRecyclerView.setAdapter(menuRecyclerViewAdapter);

        // fragmentManager
        fragmentManager = getSupportFragmentManager();
        changeFragment(0);

        // Bubble chat
        FrameLayout bubbleChatLayout = findViewById(R.id.bubbleChatLayout);
        CardView assistantIconView = findViewById(R.id.assistantIconView);
        assistantIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                bubbleChatLayout.setVisibility(bubbleChatLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        // go to setting
        FrameLayout settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bubbleChatLayout.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    startActivity(intent);
                } else {
                    hideKeyboard();
                }
            }
        });

        // hide keyboard
        ViewGroup mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");

        // Stop service
        Intent intent = new Intent(this, SocketService.class);
        stopService(intent);
    }

    private void changeFragment(int index) {
        if (pre_fragment_index != index && index >= 0 && index < menuElementList.size()) {
            try {
                // create fragment object
                String fragmentClassName = menuElementList.get(index).getFragmentClassName();
                Class<?> fragmentClass = Class.forName(fragmentClassName);
                Fragment fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commit();
                // update selected position
                menuRecyclerViewAdapter.select(index);
                // name
                TextView menuItemName = findViewById(R.id.menuItemName);
                menuItemName.setText(menuElementList.get(index).getItemName());
                pre_fragment_index = index;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (imm.isAcceptingText()) {
                View focusView = getCurrentFocus();
                if (focusView != null) {
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }
        }
    }
}