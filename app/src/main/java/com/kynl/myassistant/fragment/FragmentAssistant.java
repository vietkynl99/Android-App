package com.kynl.myassistant.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kynl.myassistant.R;
import com.kynl.myassistant.adapter.MessageDataAdapter;
import com.kynl.myassistant.adapter.SuggestionDataAdapter;
import com.kynl.myassistant.model.MessageManager;

import static com.kynl.myassistant.common.CommonUtils.BROADCAST_ACTION;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_GET_MESSAGE_FROM_SERVER;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_SEND_MESS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_STATUS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_STATUS;
import static com.kynl.myassistant.common.CommonUtils.UI_EXIT_BUBBLE_CHAT;

public class FragmentAssistant extends Fragment {

    private final String TAG = this.getClass().getName();
    private RecyclerView messageRecyclerView;
    private MessageDataAdapter messageDataAdapter;
    private LinearLayout suggestionArea;
    private boolean socketStatus = false;
    private boolean isAdvanceMode = false;

    private BroadcastReceiver mBroadcastReceiver;

    private TextView selectedItemCount;
    private ViewGroup navBarNormal, navBarAdvance;
    private ImageButton copyButton;
    private View indicatorLightStatus;
    private TextView activeStatus;
    private EditText messageEditText;

    public FragmentAssistant() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assistant, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "onCreateView: getActivity null");
            return view;
        }

        navBarNormal = view.findViewById(R.id.navBarNormal);
        navBarAdvance = view.findViewById(R.id.navBarAdvance);
        selectedItemCount = view.findViewById(R.id.selectedItemCount);
        copyButton = view.findViewById(R.id.copyButton);
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        indicatorLightStatus = view.findViewById(R.id.indicatorLightStatus);
        activeStatus = view.findViewById(R.id.activeStatus);

        // message recyclerview
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        messageDataAdapter = new MessageDataAdapter(MessageManager.getInstance().getMessageDataList());
        messageDataAdapter.setOnSubItemClickListener((position, text) -> {
            if (isAdvanceMode) {
                messageDataAdapter.toggleSelect(position);
                updateSelectedItemCount();
            }
        });
        messageDataAdapter.setOnSubItemLongClickListener((position, text) -> {
            if (isAdvanceMode) {
                exitAdvanceMenu();
            } else {
                hideMessageSuggestion();
                setAdvanceMenuVisibility(true);
                messageDataAdapter.select(position);
                updateSelectedItemCount();
            }
        });
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.setAdapter(messageDataAdapter);

        // copy message
        copyButton.setOnClickListener(v -> {
            String message = messageDataAdapter.getFirstSelectedItemString();
            if (!message.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                exitAdvanceMenu();
            }
        });

        // delete message
        deleteButton.setOnClickListener(v -> {
            int count = MessageManager.getInstance().deleteSelectedItem();
            if (count > 0) {
                messageDataAdapter.notifyDataSetChanged();
                String message = "Delete " + count + " message";
                if (count > 1) {
                    message += "s";
                }
                message += "!";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                exitAdvanceMenu();
            }
        });

        // suggestion message recyclerview
        RecyclerView suggestionRecyclerView = view.findViewById(R.id.suggestionRecyclerView);
        SuggestionDataAdapter suggestionDataAdapter = new SuggestionDataAdapter(MessageManager.getInstance().getSuggestionDataList());
        suggestionDataAdapter.setOnSubItemClickListener((position, text) -> {
            sendMessage(text);
            hideMessageSuggestion();
        });
        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        suggestionRecyclerView.setAdapter(suggestionDataAdapter);
        suggestionArea = view.findViewById(R.id.suggestionArea);

        // edit text and send button
        ImageButton sendMessageButton = view.findViewById(R.id.sendMessageButton);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendMessageButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
            messageEditText.getText().clear();
        });

        messageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                exitAdvanceMenu();
                showMessageSuggestion();
            }
        });
        messageEditText.setOnClickListener(v -> exitAdvanceMenu());

        // exit
        ImageButton backToMainUIButton = view.findViewById(R.id.backToMainUIButton);
        backToMainUIButton.setOnClickListener(v -> {
            hideKeyboard();
            sendBackToMainUIRequest();
        });

        // hide keyboard
        ViewGroup navBar = view.findViewById(R.id.navBar);
        navBar.setOnClickListener(v -> {
            hideKeyboard();
            hideMessageSuggestion();
        });

        messageRecyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                hideMessageSuggestion();
            }
            return false;
        });

        // advance menu
        ImageButton navBarAdvanceCloseButton = view.findViewById(R.id.navBarAdvanceCloseButton);
        navBarAdvanceCloseButton.setOnClickListener(v -> exitAdvanceMenu());


        // scroll
        scrollMessageToBottom();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        Activity activity = getActivity();
        if (activity != null) {
            // register broadcast
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String event = intent.getStringExtra("event");
                    if (event != null) {
                        switch (event) {
                            case SOCKET_STATUS:
                                int status = intent.getIntExtra("status", -1);
                                if (status >= 0) {
                                    Log.e(TAG, "onReceive: get socket status=" + status);
                                    socketStatus = status == 1;
                                    activity.runOnUiThread(() -> {
                                        updateServerStatus();
                                        showMessageSuggestion();
                                    });
                                }
                                break;
                            case SOCKET_GET_MESSAGE_FROM_SERVER:
                                String message = intent.getStringExtra("message");
                                if (message != null) {
                                    Log.e(TAG, "onReceive: get message from server: " + message);
                                    activity.runOnUiThread(() -> {
                                        replyMessage(message);
                                        showMessageSuggestion();
                                    });
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            };
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_ACTION));
            // send request to socket service
            requestSocketStatusFromService();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(mBroadcastReceiver);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void updateSelectedItemCount() {
        if (selectedItemCount != null && messageDataAdapter != null && copyButton != null) {
            if (isAdvanceMode) {
                int count = messageDataAdapter.getSelectedItemCount();
                if (count > 0) {
                    selectedItemCount.setText(String.valueOf(count));
                    copyButton.setVisibility(count == 1 ? View.VISIBLE : View.GONE);
                } else {
                    setAdvanceMenuVisibility(false);
                    messageDataAdapter.exitAdvanceMenu();
                }
            }
        }
    }

    private void setAdvanceMenuVisibility(boolean visibility) {
        if (navBarNormal == null || navBarAdvance == null) {
            Log.e(TAG, "setAdvanceMenuVisibility: error null");
            return;
        }
        navBarNormal.setVisibility(visibility ? View.GONE : View.VISIBLE);
        navBarAdvance.setVisibility(visibility ? View.VISIBLE : View.GONE);
        isAdvanceMode = visibility;
        messageDataAdapter.setAdvanceMode(isAdvanceMode);
    }

    private void exitAdvanceMenu() {
        if (isAdvanceMode) {
            setAdvanceMenuVisibility(false);
            if (messageDataAdapter != null) {
                messageDataAdapter.exitAdvanceMenu();
            }
        }
    }

    private void hideMessageSuggestion() {
        if (suggestionArea != null) {
            if (suggestionArea.getVisibility() == View.VISIBLE) {
                suggestionArea.setVisibility(View.GONE);
            }
        }
    }

    private void showMessageSuggestion() {
        if (suggestionArea != null) {
            if (suggestionArea.getVisibility() != View.VISIBLE && socketStatus) {
                suggestionArea.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideKeyboard() {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                if (imm.isAcceptingText()) {
                    View focusView = getActivity().getCurrentFocus();
                    if (focusView != null) {
                        imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                    }
                }
            }
            if (messageEditText != null) {
                if (messageEditText.isFocused()) {
                    messageEditText.clearFocus();
                }
            }
        }
    }

    private void sendBackToMainUIRequest() {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("event", UI_EXIT_BUBBLE_CHAT);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private void requestSocketStatusFromService() {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("event", SOCKET_REQ_STATUS);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private void sendMessageToServer(String message) {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("event", SOCKET_REQ_SEND_MESS);
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private void updateServerStatus() {
        if (indicatorLightStatus != null) {
            indicatorLightStatus.setBackgroundResource(socketStatus ? R.drawable.ellipse_12_shape_green : R.drawable.ellipse_12_shape_red);
        }
        if (activeStatus != null) {
            activeStatus.setText(getResources().getString(socketStatus ? R.string.active_status_connected : R.string.active_status_disconnected));
        }
    }

    public void scrollMessageToBottom() {
        if (messageDataAdapter != null && messageRecyclerView != null) {
            if (messageDataAdapter.getItemCount() > 0) {
                messageRecyclerView.smoothScrollToPosition(messageDataAdapter.getItemCount() - 1);
            }
        }
    }

    public void sendMessage(String message) {
        Log.d(TAG, "send message: " + message);
        MessageManager.getInstance().sendMessage(!socketStatus, message);
        // update to view
        messageDataAdapter.updateItemInserted();
        scrollMessageToBottom();
        // send message to server
        if (socketStatus) {
            sendMessageToServer(message);
        }
    }

    public void replyMessage(String message) {
        MessageManager.getInstance().replyMessage(message);
        // update to view
        messageDataAdapter.updateItemInserted();
        scrollMessageToBottom();
    }
}