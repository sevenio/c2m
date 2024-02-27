package com.tvisha.click2magic.socket;


public class SocketConstants {


    public static final String GET_AGENT_JOINED = "agent_joined";
    public static final String USER_LIVE_STATUS = "agentLiveStatus";
    public static final String AGENT_STATUS_UPDATED = "agent_status_updated";
    public static final String AGENT_STATUS_UPDATE = "agent_status_update";
    public static final String USER_CHAT_ENDED = "user_chat_ended";
    public static final String CHAT_ENDED_AGENT = "chat_ended_agent";
    public static final String NEW_ONLINE = "new_online";
    public static final String CHECK_AGENT_STATUS = "check_agent_status";
    public static final String AGENT_STATUS_INFO = "agent_status_info";
    public static final String USER_TYPING_TO_AGENT = "user_typing_to_agent";
    public static final String AGENT_REQUEST = "agent_request";
    public static final String CONTACT_UPDATE = "contact_update";
    public static final String AGENT_ACTIVITY = "agent_activity";
    public static final String AGENT_CHAT_ENDED = "agent_chat_ended";
    public static final String GET_AGENT_CHATS = "get_agent_chats";
    public static final String AGENT_LOGFF_EVERYWHERE = "agent_logoff_everywhere";
    public static final String PUSH_AGENT_OFFLINE = "push_agent_offline";
    public static final String AGENT_TYPING = "agent_typing";


    public class SocketEvents{
        public static final int EVENT_CONNECT = 1;
        public static final int EVENT_DISCONNECT=2;
        public static final int EVENT_AGENT_STATUS_UPDATED = 3;
        public static final int EVENT_USER_CHAT_ENDED = 4;
        public static final int EVENT_NEW_ONLINE = 5;
        public static final int EVENT_CHAT_ENDED_AGENT = 6 ;
        public static final int EVENT_AGENT_STATUS_UPDATE = 7;
        public static final int EVENT_CHECK_AGENT_STATUS = 8;
        public static final int EVENT_CHECK_AGENT_STATUS_INFO = 9;
        public static final int EVENT_USER_TYPING_TO_AGENT = 10;
        public static final int EVENT_AGENT_REQUEST = 11;
        public static final int EVENT_CONTACT_UPDATE = 12;
        public static final int EVENT_AGENT_ACTIVITY = 13;
        public static final int EVENT_AGENT_CHAT_ENDED = 14;
        public static final int EVENT_GET_AGENT_CHATS = 15;
        public static final int EVENT_AGENT_LOGFF_EVERYWHERE = 16;
        public static final int EVENT_PUSH_AGENT_OFFLINE = 17;
        public static final int EVENT_AGENT_TYPING = 17;

    }


    public final static String EVENT_SEND_MESSAGE = "send_message";
    public final static String EVENT_SEND_ATTACHMENT = "send_attachment";
    public final static String EVENT_MESSAGE_DELIVERED = "message_delivered";
    public final static String EVENT_MESSAGE_READ = "message_read";
    public final static String EVENT_TM_ERROR = "tm_error";
    public final static String EVENT_SENT_MESSAGE = "message_sent";
    public final static String EVENT_ACCESS_TOKEN = "access_token";
    public final static String EVENT_RECEIVE_MESSAGE = "receive_message";
    public final static String EVENT_CHANGE_USER_AVAILABILITY_STATUS = "change_user_availability_status";
    public final static String EVENT_SYNC_OFFLINE_MESSAGES = "sync_offline_messages";
    public final static String EVENT_GET_MISSING_MESSAGES = "get_missing_messages";
    public final static String EVENT_MESSAGE_READ_BY_ME = "message_read_by_me";
    public final static String EVENT_GET_USER_AVAILABILITY_STATUS = "get_user_availability_status";
    public final static String EVENT_USER_AVAILABILITY_STATUS = "user_availability_status";
    public final static String EVENT_USER_OFFLINE = "user_offline";
    public final static String EVENT_USER_ONLINE = "user_online";
    public final static String EVENT_GET_OFFLINE_MESSAGES = "get-offline-messages";


    public class TmSocketEvents{

        public   final  static int SOCKET_CONNECT = 1;
        public final static int SOCKET_DISCONNECT = 2;
        public final static int SOCKET_SEND_MESSAGE = 3;
        public final static int SOCKET_MESSAGE_DELIVERED = 4;
        public final static int SOCKET_MESSAGE_READ = 5;
        public final static int SOCKET_TM_ERROR = 6;
        public final static int SOCKET_MESSAGE_SENT = 7;
        public final static int SOCKET_ACESS_TOKEN = 8;
        public final static int SOCKET_RECEIVE_MESSAGE = 9;
        public final static int SOCKET_CHANGE_USER_AVAILABILITY_STATUS = 10;
        public final static int SOCKET_SYNC_OFFLINE_MESSAGES = 11;
        public final static int SOCKET_GET_MISSING_MESSAGES = 12;
        public final static int SOCKET_MESSAGE_READ_BY_ME = 13;
        public final static int SOCKET_SEND_ATTACHMENT = 14;
        public static final int SOCKET_GET_USER_AVAILABILITY_STATUS  = 15;
        public static final int SOCKET_USER_AVAILABILITY_STATUS  = 16;
        public static final int SOCKET_USER_OFFLINE  = 17;
        public static final int SOCKET_USER_ONLINE  = 18;
        public static final int SOCKET_GET_OFFLINE_MESSAGES  = 19;
        public final static int DISCONNECT_SOCKET = 20;

    }








}
