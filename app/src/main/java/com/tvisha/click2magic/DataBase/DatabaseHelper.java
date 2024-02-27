package com.tvisha.click2magic.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    protected String tableCreateQuery;
    protected SQLiteDatabase db;
    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }
    public DatabaseHelper(Context context) {
        super(
                context,
                DataBaseValues.DATABASE_NAME,null,
                DataBaseValues.DATABASE_VERSION
        );
        this.context = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseValues.Agent.TABLE_CREATE_QUERY_AGENTS);
        db.execSQL(DataBaseValues.Customer.TABLE_CREATE_QUERY_CUSTOMER);
        db.execSQL(DataBaseValues.ChatConversation.TABLE_CREATE_CONVERSATION);
        db.execSQL(DataBaseValues.ConversationTable.CREATE_CHAT_TABLE);
        db.execSQL(DataBaseValues.TypingMessageTable.CREATE_TYPING_MESSAGE_TABLE);
        db.execSQL(DataBaseValues.SitesTable.CREATE_SITES_TABLE);
        db.execSQL(DataBaseValues.ActiveChatsTable.CREATE_ACTIVE_CHATS_TABLE);
        db.execSQL(DataBaseValues.AgentsTable.CREATE_AGENTS_TABLE);
        db.execSQL(DataBaseValues.SiteAssetsTable.CREATE_SITE_ASSESTS_TABLE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
                try
                {
                    if(db!=null){
                        db.execSQL("ALTER TABLE conversationTable  ADD COLUMN attachment TEXT ");
                        db.execSQL("ALTER TABLE conversationTable  ADD COLUMN attachment_extension TEXT ");
                        db.execSQL("ALTER TABLE conversationTable  ADD COLUMN attachment_name TEXT ");
                        db.execSQL("ALTER TABLE conversationTable  ADD COLUMN attachment_device_path TEXT ");
                    }

                }catch (Exception e)
                {

                }

        }

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("PRAGMA automatic_index=off;");
        }
    }
    protected SQLiteDatabase getDB() {
        return getWritableDatabase();
    }
    protected String getDBPath() {

        SQLiteDatabase db = getDB();
        String path = db.getPath();

        //db.close();
        return path;
    }
    protected SQLiteDatabase openDB() {
        return SQLiteDatabase.openOrCreateDatabase(getDBPath(), null);
    }


}
