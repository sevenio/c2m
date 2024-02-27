package com.tvisha.click2magic.DataBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class DataBaseValues
{
    public static final String DATABASE_NAME = "com_tvisha_click2magic";
    public static final int DATABASE_VERSION = 1;
    public static final String ID = "id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    public static class Agent {
        public static final String TABLE_NAME = "Agent";
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String MOBILE = "mobile";
        public static final String PHOTO = "user_avatar";

        public static final String TABLE_CREATE_QUERY_AGENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_ID + " INTEGER DEFAULT 0, " +
                NAME + " TEXT, " +
                EMAIL + " TEXT, " +
                MOBILE + " TEXT, " +
                PHOTO + " TEXT," +
                CREATED_AT + " TIMESTAMP," +
                UPDATED_AT + " TIMESTAMP);";
    }
    public static class Customer {
        public static final String TABLE_NAME = "Customer";
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String MOBILE = "mobile";
        public static final String PHOTO = "user_avatar";


        public static final String TABLE_CREATE_QUERY_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_ID + " INTEGER DEFAULT 0, " +
                NAME + " TEXT, " +
                EMAIL + " TEXT, " +
                MOBILE + " TEXT, " +
                PHOTO + " TEXT," +
                CREATED_AT + " TIMESTAMP," +
                UPDATED_AT + " TIMESTAMP);";

    }

    public static class ChatConversation{

        public static final String TABLE_NAME = "ChatTable";

        public static final String MESSAGE_ID             = "message_id";
        public static final String SENDER_ID              = "sender_id";
        public static final String RECEIVER_ID            = "receiver_id";
        public static final String MESSAGE                = "message";
        public static final String ATTACHMENTS            = "attachment";
        public static final String IS_GROUP               = "is_group";
        public static final String IS_REPLY               = "is_reply";
        public static final String ORIZINAL_MESSAGE_ID    = "original_message_id";
        public static final String IS_SYNC                = "is_sync";
        public static final String IS_READ                = "is_read";
        public static final String IS_DELIVERED           = "is_delivered";
        public static final String MESSAGE_TYPE           = "message_type";
        public static final String IS_DOWNLOADED          = "is_downloaded";
        public static final String FILE_PATH              = "file_path";
        public static final String CAPTION                = "caption";
        public static final String REQUEST_TYPE           = "request_type";
        public static final String USER_ROLE              = "user_role";
        public static final String ORIZINAL_MESSAGE       = "orizinal_message";
        public static final String CHAT_ID                = "chat_id";
        public static final String SENDER_ROLE                = "sender_role";
        public static final String RECEIVER_ROLE                = "receiver_role";

        public static final String TABLE_CREATE_CONVERSATION = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MESSAGE_ID    + " INTEGER DEFAULT 0," +
                SENDER_ID     + " INTEGER DEFAULT 0," +
                CHAT_ID       + " INTEGER DEFAULT 0," +
                RECEIVER_ID   + " INTEGER DEFAULT 0," +
                MESSAGE       + " TEXT, " +
                ATTACHMENTS   + " TEXT, " +
                FILE_PATH     + " TEXT, "+
                CAPTION       + " TEXT, "+
                IS_GROUP      + " INTEGER DEFAULT 0,"+
                IS_REPLY      + " INTEGER DEFAULT 0, " +
                ORIZINAL_MESSAGE_ID +" INTEGER DEFAULT 0,"+
                IS_SYNC       +" INTEGER DEFAULT 0,"+
                IS_READ       +" INTEGER DEFAULT 0,"+
                IS_DELIVERED  + " INTEGER DEFAULT 0,"+
                MESSAGE_TYPE + " INTEGER DEFAULT 0, " +
                CREATED_AT + " TIMESTAMP," +
                ORIZINAL_MESSAGE+ " TEXT, "+
                USER_ROLE+" INTEGER DEFAULT 0, "+
                REQUEST_TYPE +" INTEGER DEFAULT 0, "+
                IS_DOWNLOADED + " INTEGER DEFAULT 0," +
                SENDER_ROLE+ " TEXT, "+
                RECEIVER_ROLE+ " TEXT, "+
                UPDATED_AT + " TIMESTAMP);";
    }
  /*  "create table if not exists conversationTable (id integer primary key,message_id text, sender_id text,
  receiver_id text,sender_role text, receiver_role text,chat_id text, message text, message_type text,
   attachment text, created_at, is_Read text)"

    */

    public static class ConversationTable{



        final public static String TABLE_NAME="conversationTable";

        final public static String id = "id";
        final public static String sender_id = "sender_id";
        final public static String receiver_id = "receiver_id";
        final public static String message_type = "message_type";
        final public static String message_id = "message_id";
        final public static String reference_id = "reference_id";
        final public static String is_group = "is_group";
        final public static String is_sync = "is_sync";
        final public static String is_reply = "is_reply";
        final public static String message="message";
        final public static String conversation_reference_id="conversation_reference_id";
        final public static String attachment="attachment";
        final public static String attachment_extension="attachment_extension";
        final public static String attachment_name="attachment_name";
        final public static String attachment_device_path="attachment_device_path";
        final public static String original_message_id="original_message_id";
        final public static String is_read="is_read";
        final public static String is_delivered="is_delivered";
        final public static String is_received="is_received";
        final public static String is_downloaded="is_downloaded";
        final public static String is_available="is_available";
        final public static String original_message="original_message";
        final public static String caption="caption";
        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                message_id+ " INTEGER DEFAULT 0," +
                sender_id+ " INTEGER DEFAULT 0," +
                receiver_id+ " INTEGER DEFAULT 0," +
                message+" Text ,"+
                attachment+" Text ,"+
                attachment_extension+" Text ,"+
                attachment_name+" Text ,"+
                attachment_device_path+" Text ,"+
                is_group+ " INTEGER DEFAULT 0," +
                is_reply+ " INTEGER DEFAULT 0," +
                original_message_id+ " INTEGER DEFAULT 0," +
                is_sync+ " INTEGER DEFAULT 0," +
                is_read+ " INTEGER DEFAULT 0," +
                is_delivered+ " INTEGER DEFAULT 0," +
                is_received+ " INTEGER DEFAULT 0," +
                message_type+ " INTEGER DEFAULT 0," +
                is_downloaded+ " INTEGER DEFAULT 0," +
                conversation_reference_id+ " Text ," +
                is_available+ " INTEGER DEFAULT 0," +
                reference_id+" Text ,"+
                original_message+" Text ,"+
                caption+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }


    public static class TypingMessageTable{



        final public static String TABLE_NAME="TypingMessageTable";
        final public static String id = "id";
        final public static String chat_id = "chat_id";
        final public static String message = "message";
        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_TYPING_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                chat_id+ " INTEGER DEFAULT 0," +
                message+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }
    public static class CategoriesTable{



        final public static String TABLE_NAME="CategoriesTable";
        final public static String id = "id";
        final public static String category_name = "category_name";
        final public static String status = "status";
        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY,"+
                category_name+ " Text," +
                status+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }


    public static class SiteAssetsTable{



        final public static String TABLE_NAME="SiteAssetsTable";
        final public static String id = "id";
        final public static String asset_id = "asset_id";
        final public static String title = "title";
        final public static String path = "path";
        final public static String added_by = "added_by";
        final public static String type = "type";
        final public static String status = "status";
        final public static String site_id = "site_id";
        final public static String user_name = "user_name";
        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_SITE_ASSESTS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                asset_id+" Text ,"+
                title+" Text ,"+
                path+" Text ,"+
                added_by+" Text ,"+
                type+" Text ,"+
                site_id+" Text ,"+
                status+" Text ,"+
                user_name+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }

    public static class SitesTable{

        final public static String TABLE_NAME="SitesTable";
        final public static String id = "id";
        final public static String site_id = "site_id";
        final public static String site_name = "site_name";
        final public static String site_token = "site_token";
        final public static String account_id = "account_id";
        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_SITES_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                site_id+ " Text," +
                site_name+" Text ,"+
                site_token+" Text ,"+
                account_id+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }

    public static class ActiveChatsTable{

        final public static String TABLE_NAME="ActiveChatsTable";

        final public static String id = "id";

        final public static String chat_id = "chat_id";
        final public static String visitor_id = "visitor_id";
        final public static String guestName = "guestName";
        final public static String start_time = "start_time";
        final public static String end_time = "end_time";

        final public static String visitor_os = "visitor_os";
        final public static String visitor_url = "visitor_url";
        final public static String chat_rating = "chat_rating";
        final public static String visitor_ip = "visitor_ip";
        final public static String visitor_browser = "visitor_browser";

        final public static String agent_id = "agent_id";
        final public static String chat_reference_id = "chat_reference_id";
        final public static String location = "location";
        final public static String status = "status";
        final public static String chat_status = "chat_status";

        final public static String visitor_query = "visitor_query";
        final public static String account_id = "account_id";
        final public static String site_id = "site_id";
        final public static String name = "name";
        final public static String email = "email";

        final public static String mobile = "mobile";
        final public static String visit_count = "visit_count";
        final public static String tm_visitor_id = "tm_visitor_id";
        final public static String user_name = "user_name";
        final public static String tag_name = "tag_name";

        final public static String rating = "rating";
        final public static String track_code = "track_code";
        final public static String unread_message_count = "unread_message_count";
        final public static String online = "online";
        final public static String typing_message = "typing_message";

        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_ACTIVE_CHATS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                chat_id+ " Text," +
                visitor_id+" Text ,"+
                guestName+" Text ,"+
                start_time+" Text ,"+
                end_time+" Text ,"+
                visitor_os+" Text ,"+
                visitor_url+" Text ,"+
                chat_rating+" Text ,"+
                visitor_ip+" Text ,"+
                visitor_browser+" Text ,"+
                agent_id+" Text ,"+
                chat_reference_id+" Text ,"+
                location+" Text ,"+
                status+" Text ,"+
                chat_status+" Text ,"+
                visitor_query+" Text ,"+
                account_id+" Text ,"+
                site_id+" Text ,"+
                name+" Text ,"+
                email+" Text ,"+
                mobile+" Text ,"+
                visit_count+" Text ,"+
                tm_visitor_id+" Text ,"+
                user_name+" Text ,"+
                tag_name+" Text ,"+
                rating+" Text ,"+
                track_code+" Text ,"+
                unread_message_count+" INTEGER DEFAULT 0,"+
                online+" INTEGER DEFAULT 0,"+
                typing_message+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }

    public static class ArchiveChatsTable{

        final public static String TABLE_NAME="ArchiveChatsTable";

        final public static String id = "id";

        final public static String chat_id = "chat_id";
        final public static String visitor_id = "visitor_id";
        final public static String guestName = "guestName";
        final public static String start_time = "start_time";
        final public static String end_time = "end_time";

        final public static String visitor_os = "visitor_os";
        final public static String visitor_url = "visitor_url";
        final public static String chat_rating = "chat_rating";
        final public static String visitor_ip = "visitor_ip";
        final public static String visitor_browser = "visitor_browser";

        final public static String agent_id = "agent_id";
        final public static String chat_reference_id = "chat_reference_id";
        final public static String location = "location";
        final public static String status = "status";
        final public static String chat_status = "chat_status";

        final public static String visitor_query = "visitor_query";
        final public static String account_id = "account_id";
        final public static String site_id = "site_id";
        final public static String name = "name";
        final public static String email = "email";

        final public static String mobile = "mobile";
        final public static String visit_count = "visit_count";
        final public static String tm_visitor_id = "tm_visitor_id";
        final public static String user_name = "user_name";
        final public static String tag_name = "tag_name";

        final public static String rating = "rating";
        final public static String track_code = "track_code";
        final public static String unread_message_count = "unread_message_count";
        final public static String online = "online";
        final public static String typing_message = "typing_message";

        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_ACTIVE_CHATS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                chat_id+ " Text," +
                visitor_id+" Text ,"+
                guestName+" Text ,"+
                start_time+" Text ,"+
                end_time+" Text ,"+
                visitor_os+" Text ,"+
                visitor_url+" Text ,"+
                chat_rating+" Text ,"+
                visitor_ip+" Text ,"+
                visitor_browser+" Text ,"+
                agent_id+" Text ,"+
                chat_reference_id+" Text ,"+
                location+" Text ,"+
                status+" Text ,"+
                chat_status+" Text ,"+
                visitor_query+" Text ,"+
                account_id+" Text ,"+
                site_id+" Text ,"+
                name+" Text ,"+
                email+" Text ,"+
                mobile+" Text ,"+
                visit_count+" Text ,"+
                tm_visitor_id+" Text ,"+
                user_name+" Text ,"+
                tag_name+" Text ,"+
                rating+" Text ,"+
                track_code+" Text ,"+
                unread_message_count+" INTEGER DEFAULT 0,"+
                online+" INTEGER DEFAULT 0,"+
                typing_message+" Text ,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }

    public static class AgentsTable{

        final public static String TABLE_NAME="AgentsTable";
        final public static String id = "id";

        final public static String site_id = "site_id";
        final public static String agent_id = "agent_id";
        final public static String is_online = "is_online";
        final public static String user_id = "user_id";
        final public static String user_name = "user_name";

        final public static String account_id = "account_id";
        final public static String role = "role";
        final public static String tm_user_id = "tm_user_id";
        final public static String active_chat_count = "active_chat_count";
        final public static String user_site_id = "user_site_id";

        final public static String created_at = "created_at";
        final public static String updated_at = "updated_at";

        public static String CREATE_AGENTS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                id+" Integer PRIMARY KEY AUTOINCREMENT,"+
                site_id+ " Text," +
                agent_id+" Text ,"+
                is_online+" Text ,"+
                account_id+" Text ,"+
                user_id+" Text ,"+
                user_name+" Text ,"+
                role+" Text ,"+
                tm_user_id+" Text ,"+
                user_site_id+" Text ,"+
                active_chat_count+" INTEGER DEFAULT 0,"+
                created_at+" datetime,"+
                updated_at+" datetime" +
                ");";

    }

}
