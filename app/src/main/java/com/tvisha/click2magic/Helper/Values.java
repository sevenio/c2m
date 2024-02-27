package com.tvisha.click2magic.Helper;


public class Values {


    public static final String PACKAGE = "com.tvisha.click2magic";




    public class ChatListTabClick{
        public static final int AGENT_TAB = 1;
        public static final int VISITOR_TAB = 2;
    }

    public static class AppActivities {
        public static final int ACTIVITY_NON = 0;
        public static final int ACTIVITY_MAIN = 1;
        public static final int ACTIVITY_CHAT = 2;
        public static final int ACTIVITY_HISTORY_CHAT  = 3;
        public static final int ACTIVITY_ARCHIVES  = 4;
        public static final int ACTIVITY_LOGIN  = 5;
        public static final int GALLARY_PAGE  = 6;
        public static final int ACTIVITY_SYNC_DATA  = 7;
    }
    public class UserRoles{


        public static final int ADMIN = 1;
        public static final int SUPERVISOR = 2;
        public static final int AGENT = 3;
        public static final int MODERATOR = 4;
    }

    public class AssetType{
        public static final int LINKS = 1;
        public static final int COLLATERAL = 2;
        public static final int IMAGES = 3;
        public static final int CANNEDRESPONSES = 4;
    }
    public class Action {
        public static final int INSERT = 1;
        public static final int DELETE = 2;

    }


    public class ChatStatus{


        public static final int CHAT_ACTIVE = 1;
        public static final int CHAT_USER_ENDED = 2;

    }
    public class NotificationType{


        public static final int USER_ADDED = 1;
        public static final int CHAT_ENDED_BY_USER = 2;

    }



    public class Notification {
        public static final String single_notification = "single_notification";
        public static final String notification = "notification";
        public static final String fcmnotification = "fcmnotification";
        public static final String tmnotification = "tmnotification";
        public static final String user_name = "user_name";
        public static final String tm_user_id = "tm_user_id";
        public static final String userData = "userData";
        public static final String receiver_id = "receiver_id";
        public static final String sender_id = "sender_id";
        public static final String image_path = "image_path";
        public static final String conversation_reference_id = "image_path";

    }


    public class SingleUserChat {
        public static final int CHAT_MESSAGE_READ = 1;
        public static final int CHAT_MESSAGE_SENT = 2;
        public static final int CHAT_NETWORK_CONNECTED = 3;
        public static final int CHAT_PLAY_VIDEO = 4;
        public static final int CHAT_PLAY_AUDIO = 5;
        public static final int CHAT_IMAGE_PREVIEW = 6;
        public static final int CHAT_FILE_PREVIEW = 7;
        public static final int CHAT_SELECTION_MODE_ACTIVE = 8;
        public static final int CHAT_SELECTION_MODE_INACTIVE = 9;
        public static final int CHAT_MULTIPLE_ITEMS_SELECTED = 10;
        public static final int CHAT_SINGLE_ITEMS_SELECTED = 11;
        public static final int CHAT_SINGLE_DIRECT_REPLY = 12;
        public static final int CHAT_CAL_TO_SHARED_CONTACT = 13;
        public static final int CHAT_ENABLE_COPY = 14;
        public static final int CHAT_DISABLE_COPY = 15;
        public static final int CHAT_SHOW_FULL_MSG = 16;
        public static final int CHAT_FULL_MSG_REPLY = 17;
        public static final int CHAT_FULL_MSG_FRWD = 18;
        public static final int CHAT_REPLY_CLICK = 19;
    }


    public static class Permissions {
        public static final int CAll_PERMISSION = 1;
        public static final int CAMERA_PERMISSION = 2;
        public static final int STORAGE_PERMISSION = 3;
        public static final int LOCATION_PERMISSION = 4;
        public static final int CAMERA_AND_STORAGE_PERMISSION = 5;
        public static final int READ_PHONE_STATE = 6;

        public static final String STORAGE_PERMISSIONS_LIST[] = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        public static final String CAMERA_PERMISSIONS_LIST[] = {"android.permission.CAMERA"};
        public static final String CONTATCT_PERMISSIONS_LIST[] = {"android.permission.CALL_PHONE", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"};
        public static final String LOCATION_PERMISSIONS_LIST[] = {"android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION"};
    }




    public class MessageType{
        public static final int MESSAGE_TYPE_TEXT = 0;
        public static final int MESSAGE_TYPE_ATTACHMENT = 1;
        public static final int MESSAGE_TYPE_REQUEST = 12;
        public static final int MESSAGE_TYPE_SHARED_LOCATION = 3;
        public static final int MESSAGE_TYPE_SHARED_CONTACT = 2;
        public static final int MESSAGE_TYPE_TYPING = 20;


    }

    public class Media {
        public static final String MEDIA_DATA = "media_data";

    }
    public static class ErrorMessages {
        public static final String NETWORK_ERROR = "Check network connection!";
        public static final String SOMETHING_WENT_WRONG = "something went wrong, Please try again.";
    }


    public class RecentList {


        public static final int RECENT_NETWORK_CONNECTED = 1;
        public static final int RECENT_NETWORK_DISCONNECTED = 2;
        public static final int FCM_TRIGGER = 3;
        public static final int MESSAGES_SYNC_COMPLETED = 4;
        public static final int ACCESS_TOKEN_UPDATED = 5;
        public static final int UPDATE_UNREAD_COUNT = 6;
        public static final int SYNC_FAILED = 7;
        public static final int MESSAGES_READ_COMPLETED = 8;
        public static final int AGENT_CLICKED = 9;
        public static final int CROP_RESULT = 10;
        public static final int OPEN_CAMERA = 11;
        public static final int OPEN_GALLERY = 12;
        public static final int TAB_CLICKED = 13;
        public static final int OPEN_AGENT_TAB = 14;
        public static final int LOGOUT = 15;
        public static final int ACTIVE_CHATS_SYNC_COMPLETED = 16;
        public static final int ACTIVE_AGENTS_SYNC_COMPLETED = 17;
        public static final int PAGE_CHANGED = 18;
        public static final int SITES_SYNC_COMPLETED = 19;
        public static final int CATEGORIES_SYNC_COMPLETED = 20;
        public static final int OFFLINE = 21;

                public static final int SOCKETCONNECT = 22;
              public static final int SOCKETDISCONNECT = 23;
              public static final int ENABLE_SWIPE = 23;
              public static final int DISABLE_SWIPE = 24;
              public static final int SITE_DELETED = 25;
              public static final int SITE_ADDED = 26;





        public static final int ADD_NEW_NOTIFICATION_REPLY_MESSAGE=1000000006;



    }

    public  class IntentData{
        public static final String USER_DATA = "userData";
        public static final String USER_LIST = "userList";
        public static final String ACTIVE_AGENT_LIST = "activeAgentList";
        public static final String SITE_LIST = "siteList";
        public static final String POSITION = "position";
        public static final String IS_SELF = "isSelf";
        public static final String ATTACHMENT_PATH = "attachmentPath";
        public static final String ATTACHMENT_NAME = "attachmentName";
        public static final String ATTACHMENT_EXTENSION = "attachmentExtension";
        public static final String RECEIVER_ID = "receiver_id";
        public static final String RECEIVER_NAME = "receiver_name";
        public static final String SENDER_NAME = "sender_name";
        public static final String SENDER_ID = "sender_id";
        public static final String MESSAGE_ID = "message_id";
        public static final String IMAGE_PATH = "image_path";
        public static final String CONVERSATION_REFERENCE_ID = "conversation_reference_id";
        public static final String FILTER_AGENT_IDS = "filter_agent_ids";
        public static final String FILTER_SITE_IDS = "filter_site_ids";
        public static final String FILTER_FROM_DATE = "filter_from_date";
        public static final String FILTER_TO_DATE = "filter_to_date";
        public static final String SELECTED_AGENT_SITE_ID = "selected_agent_site_id";
        public static final String DOC_PATH = "doc_path";
    }


    public class MyActions {
        public static final String ACTION_DATA = PACKAGE + "_action_data";
        public static final String NOTIFICATION = PACKAGE + "_notification";
        public static final String MAIN_NOTIFICATION = PACKAGE + "main_notification";
    }
    public class MyActionsRequestCode{
        public static final int DISPLAY_ATTACHMENT= 1;
        public static final int PICK_IMAGE= 2;
        public static final int CAMERA_IMAGE= 3;
        public static final int GALLERY_IMAGE= 4;
        public static final int ATTACHMENT_DOC= 5;
        public static final int ATTACHMENT_VIDEO= 6;
        public static final int FILTER_ARCHIVES= 7;
        public static final int PICK_DOC= 8;
        public static final int APP_UPDATE= 9;
    }



    public class Gallery {
        /*types*/
        public static final int GALLERY_NON = 0; //not file
        public static final int GALLERY_IMAGE = 1;
        public static final int GALLERY_VIDEO = 2;
        public static final int GALLERY_AUDIO = 3;
        public static final int GALLERY_TXT = 4;
        public static final int GALLERY_PDF = 5;
        public static final int GALLERY_DOC = 6;
        public static final int GALLERY_XLS = 7;
        public static final int GALLERY_ZIP = 8;
        public static final int GALLERY_OTHER = 9;
        public static final int GALLERY_JSON = 10;
        public static final int GALLERY_SQL = 11;
        public static final int GALLERY_ODT = 12;
        public static final int GALLERY_CSV = 13;
        public static final int GALLERY_XML = 14;
        public static final int GALLERY_HTML = 15;
        public static final int GALLERY_JS = 16;
        public static final int GALLERY_LINK = 17;
        public static final int GALLERY_PPT = 18;
        public static final int CONTACT_TYPE = 19;

    }


    public class RequestPermission{
        public static final int READ_PHONE_STATE = 1;

    }
}
