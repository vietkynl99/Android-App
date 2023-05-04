package com.kynl.myassistant.fragment;

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
import com.kynl.myassistant.adapter.OnSubItemClickListener;
import com.kynl.myassistant.adapter.OnSubItemLongClickListener;
import com.kynl.myassistant.adapter.SuggestionDataAdapter;
import com.kynl.myassistant.model.MessageData;
import com.kynl.myassistant.model.MessageManager;
import com.kynl.myassistant.service.SocketService;

import java.util.List;

import static com.kynl.myassistant.common.CommonUtils.SOCKET_ACTION_DATA;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_ACTION_REQ;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_SEND_MESS;
import static com.kynl.myassistant.common.CommonUtils.SOCKET_REQ_STATUS;

public class FragmentAssistant extends Fragment {

    private final String TAG = this.getClass().getName();
    private RecyclerView messageRecyclerView, suggestionRecyclerView;
    private MessageDataAdapter messageDataAdapter;
    private SuggestionDataAdapter suggestionDataAdapter;
    private LinearLayout suggestionArea;
    private boolean socketStatus = false;
    private boolean isAdvanceMode = false;

    private BroadcastReceiver mBroadcastReceiver;

    private TextView selectedItemCount;
    private ViewGroup navBarNormal, navBarAdvance;
    private ImageButton navBarAdvanceCloseButton, copyButton, deleteButton;
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

        navBarNormal = view.findViewById(R.id.navBarNormal);
        navBarAdvance = view.findViewById(R.id.navBarAdvance);
        selectedItemCount = view.findViewById(R.id.selectedItemCount);
        copyButton = view.findViewById(R.id.copyButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        indicatorLightStatus = view.findViewById(R.id.indicatorLightStatus);
        activeStatus = view.findViewById(R.id.activeStatus);

        // message recyclerview
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        messageDataAdapter = new MessageDataAdapter(MessageManager.getInstance().getMessageDataList());
        messageDataAdapter.setOnSubItemClickListener(new OnSubItemClickListener() {
            @Override
            public void onSubItemClick(int position, String text) {
                if (isAdvanceMode) {
                    messageDataAdapter.toggleSelect(position);
                    updateSelectedItemCount();
                }
            }
        });
        messageDataAdapter.setOnSubItemLongClickListener(new OnSubItemLongClickListener() {
            @Override
            public void onSubItemLongClick(int position, String text) {
                if (isAdvanceMode) {
                    exitAdvanceMenu();
                } else {
                    hideMessageSuggestion();
                    setAdvanceMenuVisibility(true);
                    messageDataAdapter.select(position);
                    updateSelectedItemCount();
                }
            }
        });
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.setAdapter(messageDataAdapter);

        // copy message
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageDataAdapter.getFirstSelectedItemString();
                if (!message.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", message);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    exitAdvanceMenu();
                }
            }
        });

        // delete message
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // suggestion message recyclerview
        suggestionRecyclerView = view.findViewById(R.id.suggestionRecyclerView);
        suggestionDataAdapter = new SuggestionDataAdapter(MessageManager.getInstance().getSuggestionDataList());
        suggestionDataAdapter.setOnSubItemClickListener(new OnSubItemClickListener() {
            @Override
            public void onSubItemClick(int position, String text) {
                sendMessage(text);
                hideMessageSuggestion();
            }
        });
        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        suggestionRecyclerView.setAdapter(suggestionDataAdapter);
        suggestionArea = view.findViewById(R.id.suggestionArea);

        // edit text and send button
        ImageButton sendMessageButton = view.findViewById(R.id.sendMessageButton);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageEditText.getText().toString().trim();
                if (!text.isEmpty()) {
                    sendMessage(text);
                }
                messageEditText.getText().clear();
            }
        });

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    exitAdvanceMenu();
                    showMessageSuggestion();
                }
            }
        });
        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAdvanceMenu();
            }
        });

        // hide keyboard
        ViewGroup navBar = view.findViewById(R.id.navBar);
        navBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                    hideMessageSuggestion();
                }
                return false;
            }
        });
        messageRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                    hideMessageSuggestion();
                }
                return false;
            }
        });

        // advance menu
        navBarAdvanceCloseButton = view.findViewById(R.id.navBarAdvanceCloseButton);
        navBarAdvanceCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAdvanceMenu();
            }
        });


        // scroll
        scrollMessageToBottom();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        // register broadcast
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String event = intent.getStringExtra("event");
                if (event != null) {
                    switch (event) {
                        case "status":
                            int status = intent.getIntExtra("status", -1);
                            if (status >= 0) {
                                Log.e(TAG, "onReceive: get socket status=" + status);
                                socketStatus = status == 1;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateServerStatus();
                                        showMessageSuggestion();
                                    }
                                });
                            }
                            break;
                        case "message":
                            String message = intent.getStringExtra("message");
                            if (message != null) {
                                Log.e(TAG, "onReceive: get message from server: " + message);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        replyMessage(message);
                                        showMessageSuggestion();
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mBroadcastReceiver, new IntentFilter(SOCKET_ACTION_DATA));

        // send request to socket service
        requestSocketStatusFromService();
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

    private void requestSocketStatusFromService() {
        Intent intent = new Intent(SOCKET_ACTION_REQ);
        intent.putExtra("event", SOCKET_REQ_STATUS);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void sendMessageToServer(String message) {
        Intent intent = new Intent(SOCKET_ACTION_REQ);
        intent.putExtra("event", SOCKET_REQ_SEND_MESS);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
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