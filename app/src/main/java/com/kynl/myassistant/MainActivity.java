package com.kynl.myassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.kynl.myassistant.adapter.ViewPagerAdapter;
import com.kynl.myassistant.fragment.FragmentAssistant;
import com.kynl.myassistant.fragment.FragmentHome;
import com.kynl.myassistant.fragment.FragmentSettings;
import com.kynl.myassistant.service.SocketService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Fragment fragmentHome, fragmentAssistant, fragmentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Socket service
        Log.i(TAG, "onCreate: Start service");
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);

        // Tab layout & view pager
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Dashboard"));
        tabLayout.addTab(tabLayout.newTab().setText("Assistant"));
        tabLayout.addTab(tabLayout.newTab().setText("Setting"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentHome = new FragmentHome();
        fragmentAssistant = new FragmentAssistant();
        fragmentSettings = new FragmentSettings();
        fragmentList.add(fragmentHome);
        fragmentList.add(fragmentAssistant);
        fragmentList.add(fragmentSettings);

        // viewpager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabSelected: " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        FrameLayout bubbleChatLayout = findViewById(R.id.bubbleChatLayout);
        CardView assistantIconView = findViewById(R.id.assistantIconView);
        assistantIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleChatLayout.setVisibility(bubbleChatLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE );
            }
        });

//        FrameLayout bubbleChatBack = findViewById(R.id.bubbleChatBack);
//        bubbleChatBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "onClick: back" );
//                if(bubbleChatLayout.getVisibility() == View.VISIBLE) {
//                    bubbleChatLayout.setVisibility(View.GONE);
//                }
//            }
//        });

        // go to setting
        FrameLayout settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
//                intent.putExtra("key", "data");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
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
}