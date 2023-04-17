package com.kynl.myassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.kynl.myassistant.fragment.FanFragment;
import com.kynl.myassistant.fragment.FragmentHome;
import com.kynl.myassistant.fragment.LightFragment;
import com.kynl.myassistant.fragment.MediaFragment;
import com.kynl.myassistant.fragment.PumpFragment;
import com.kynl.myassistant.fragment.TemperatureFragment;
import com.kynl.myassistant.service.SocketService;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private int pre_fragment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Socket service
        Log.i(TAG, "onCreate: Start service");
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);

        // Bottom navigation menu
        fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (pre_fragment_id == item.getItemId()) {
                    return true;
                }
                pre_fragment_id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.nav_menu_light:
                        fragmentManager.beginTransaction().replace(R.id.fragment_content, new LightFragment()).commit();
                        break;
                    case R.id.nav_menu_temperature:
                        fragmentManager.beginTransaction().replace(R.id.fragment_content, new TemperatureFragment()).commit();
                        break;
                    case R.id.nav_menu_fan:
                        fragmentManager.beginTransaction().replace(R.id.fragment_content, new FanFragment()).commit();
                        break;
                    case R.id.nav_menu_pump:
                        fragmentManager.beginTransaction().replace(R.id.fragment_content, new PumpFragment()).commit();
                        break;
                    case R.id.nav_menu_media:
                        fragmentManager.beginTransaction().replace(R.id.fragment_content, new MediaFragment()).commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        pre_fragment_id = R.id.nav_menu_light;
        fragmentManager.beginTransaction().replace(R.id.fragment_content, new LightFragment()).commit();

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