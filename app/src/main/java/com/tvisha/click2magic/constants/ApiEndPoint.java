package com.tvisha.click2magic.constants;


public class ApiEndPoint {




   public static final String token="RdFgcWB7HpZn3Meo";

    public static final String FILE_IMAGE_BASE ="https://s3.amazonaws.com/click.2.magic/";


    //local
 /*   public static final String SOCKET_PATH   = "http://192.168.2.135:8080";
    public static final String BASE_PATH = "http://192.168.2.135/c2m/public/api/user/";*/

/*
    public static final String SOCKET_PATH   = "http://192.168.2.77:8080";
    public static final String BASE_PATH = "http://192.168.2.77/c2m/public/api/user/";*/


/*    public static final String SOCKET_PATH   = "http://192.168.4.144:8080";
    public static final String BASE_PATH = "http://192.168.4.114/c2m/public/api/user/";*/


    //testing
   /* public static final String SOCKET_PATH = "http://testing.click2magic.com:8080";
    public static final String BASE_PATH = "http://testing.click2magic.com/api/user/";
*/
 // live
  public static final String SOCKET_PATH = "https://click2magic.com:8080";
    public final static String BASE_PATH = "https://www.click2magic.com/api/user/";


//    public static final String FILE_IMAGE_BASE = "https://s3.amazonaws.com/click.2.magic/";

    public static final String s3Url = "https://s3.amazonaws.com/files.c2m/";
    public final static String API_LOGIN = BASE_PATH + "login";
    public final static String API_UPDATE_FCM = BASE_PATH + "update-fcm";
    public final static String API_GET_WEBSITE_AGENTS = BASE_PATH + "api/getWebsiteAgents";
    public final static String API_ACTIVE_CHATS = BASE_PATH + "active-chats";

    public final static String API_ARCHIVES = BASE_PATH + "archives";
    public final static String API_ACTIVE_AGENTS = BASE_PATH + "active-agents";
    public final static String API_AGENT_STATUS = BASE_PATH + "agent-status";
    public final static String API_GET_AWS_KEYS = BASE_PATH + "get-aws-keys";
    public final static String API_GET_AGENTS = BASE_PATH + "get-agents";
    public final static String API_SITE_ASSETS = BASE_PATH + "get-site-assets";
    public final static String API_AGENTS = BASE_PATH + "agents";
    public final static String API_ALL_AGENTS = BASE_PATH + "all-agents";
 public final static String API_FEEDBACK_CATEGORIES = BASE_PATH + "categories";
 public final static String API_CHAT_RATING_UPDATE = BASE_PATH + "chat-rating-update";
 public final static String API_GET_TRACK = BASE_PATH + "get-track";
 public final static String API_SITES = BASE_PATH + "user-sites";




    public final static String API_LOGOUT = BASE_PATH + "logout";


    public final static String TM_SERVER_SOCKET_PATH = "https://apis.troopmessenger.com";
    public final static String TM_SERVER_API_PATH = "https://apis.troopmessenger.com/api/";
    public final static String TM_SERVER_SOCKET_TOKEN = "AsrqZ1ozGLXkaMSPQLCADgfLAk";


 //tmAPIs

 public static final String getMessages ="get-messages";
 public static final String getConversation ="get-conversation";
 public static final String getOfflineMessages ="get-offline-messages";
 public final static String API_CONVERSATION_HISTOTY =  "conversation-history";


}
