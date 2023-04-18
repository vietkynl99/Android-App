package com.kynl.myassistant.common;

public class CommonUtils {
    //    Shared Preferences
    public static final String SOCKET_PREFERENCES = "socket_preferences";

    //    Broadcast
    //    from another module to socket service
    public static final String SOCKET_ACTION_REQ = "socket_action_req";
    public static final String SOCKET_REQ_STATUS = "socket_req_status";
    public static final String SOCKET_REQ_SEND_MESS = "socket_req_send_mess";
    public static final String SOCKET_REQ_CHANGE_ADDRESS = "socket_req_change_address";
    public static final String SOCKET_REQ_UPDATE_DEVICE = "socket_req_update_device";
    //    from socket service to another module
    public static final String SOCKET_ACTION_DATA = "socket_action_data";
}
