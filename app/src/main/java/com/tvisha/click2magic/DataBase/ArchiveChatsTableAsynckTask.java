package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArchiveChatsTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;

    public static final String TABLE_NAME = DataBaseValues.ArchiveChatsTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {
        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
        }
        Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask ArchiveChatsTableAsynckTask onPostExecute","called");



    }

    @Override
    protected  Boolean doInBackground(Object... params) {
        boolean status=false;
        context =(Context) params[0];
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
                if(db!=null){
                    db.execSQL(DataBaseValues.ArchiveChatsTable.CREATE_ACTIVE_CHATS_TABLE);
                }
            }

        }


        JSONArray jsonArray =(JSONArray) params[1];
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){
                    Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask addMessage",jsonObject.toString());
                    if(context!=null)
                    {
                        ActiveChat activeChat=setActiveUserData(jsonObject);
                        insertActiveChat(activeChat);
                    }


            }
        }
        return status;
    }
    public ActiveChat setActiveUserData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("setActiveUserData", jsonObject.toString());


        ActiveChat activeChat = null;
        try {
            if (jsonObject != null) {
                activeChat = new ActiveChat();
                activeChat.setChatId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.chat_id));
                activeChat.setVisitorOs(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_os));
                activeChat.setVisitorUrl(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_url));
                activeChat.setVisitorIp(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_ip));
                activeChat.setVisitorBrowser(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_browser));
                activeChat.setAgentId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.agent_id));
                activeChat.setChatReferenceId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.chat_reference_id));
                activeChat.setLocation(jsonObject.optString(DataBaseValues.ArchiveChatsTable.location));
                activeChat.setGuestName(jsonObject.optString("guest_name"));
                activeChat.setVisitorId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_id));
                activeChat.setAccountId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.account_id));
                activeChat.setSiteId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.site_id));
                activeChat.setEmail(jsonObject.optString(DataBaseValues.ArchiveChatsTable.email));
                activeChat.setMobile(jsonObject.optString(DataBaseValues.ArchiveChatsTable.mobile));
                activeChat.setTmVisitorId(jsonObject.optString(DataBaseValues.ArchiveChatsTable.tm_visitor_id));
                activeChat.setVisitCount(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visit_count));
                activeChat.setTrack_code(jsonObject.optString(DataBaseValues.ArchiveChatsTable.track_code));
                activeChat.setChatStatus(jsonObject.optString(DataBaseValues.ArchiveChatsTable.chat_status));
                activeChat.setStartTime(jsonObject.optString(DataBaseValues.ArchiveChatsTable.start_time));
                activeChat.setEndTime(jsonObject.optString(DataBaseValues.ArchiveChatsTable.end_time));
                activeChat.setChatRating(jsonObject.optString(DataBaseValues.ArchiveChatsTable.chat_rating));
                activeChat.setStatus(jsonObject.optString(DataBaseValues.ArchiveChatsTable.status));
                activeChat.setVisitorQuery(jsonObject.optString(DataBaseValues.ArchiveChatsTable.visitor_query));
                activeChat.setCreatedAt(jsonObject.optString(DataBaseValues.ArchiveChatsTable.created_at));
                activeChat.setUpdatedAt(jsonObject.optString(DataBaseValues.ArchiveChatsTable.updated_at));
                activeChat.setName(jsonObject.optString(DataBaseValues.ArchiveChatsTable.name));
                activeChat.setUser_name(jsonObject.optString(DataBaseValues.ArchiveChatsTable.user_name));
                activeChat.setTag_name(jsonObject.optString(DataBaseValues.ArchiveChatsTable.tag_name));
                activeChat.setRating(jsonObject.optString(DataBaseValues.ArchiveChatsTable.rating));





            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return activeChat;
    }
    @Override
    protected void onPreExecute() {
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
            }
        }

    }

    @Override
    protected void onProgressUpdate(String... text) {
    }


    public void insertActiveChat(ActiveChat activeChat){

        long status=0;
        Cursor cursor = null;
        ContentValues data = new ContentValues();

        data.put(DataBaseValues.ArchiveChatsTable.chat_id, activeChat.getChatId());
        data.put(DataBaseValues.ArchiveChatsTable.site_id, activeChat.getSiteId());
        data.put(DataBaseValues.ArchiveChatsTable.agent_id, activeChat.getAgentId());
        data.put(DataBaseValues.ArchiveChatsTable.account_id, activeChat.getAccountId());
        data.put(DataBaseValues.ArchiveChatsTable.chat_reference_id, activeChat.getChatReferenceId());

        data.put(DataBaseValues.ArchiveChatsTable.chat_rating, activeChat.getChatRating());
        data.put(DataBaseValues.ArchiveChatsTable.chat_status, activeChat.getChatStatus());
        data.put(DataBaseValues.ArchiveChatsTable.email, activeChat.getEmail());
        data.put(DataBaseValues.ArchiveChatsTable.guestName, activeChat.getGuestName());
        data.put(DataBaseValues.ArchiveChatsTable.user_name, activeChat.getUser_name());

        data.put(DataBaseValues.ArchiveChatsTable.online, activeChat.getOnline());
        data.put(DataBaseValues.ArchiveChatsTable.status, activeChat.getStatus());
        data.put(DataBaseValues.ArchiveChatsTable.unread_message_count, activeChat.getUnread_message_count());
        data.put(DataBaseValues.ArchiveChatsTable.name, activeChat.getName());
        data.put(DataBaseValues.ArchiveChatsTable.rating, activeChat.getRating());

        data.put(DataBaseValues.ArchiveChatsTable.mobile, activeChat.getMobile());
        data.put(DataBaseValues.ArchiveChatsTable.start_time, activeChat.getStartTime());
        data.put(DataBaseValues.ArchiveChatsTable.end_time, activeChat.getEndTime());
        data.put(DataBaseValues.ArchiveChatsTable.tm_visitor_id, activeChat.getTmVisitorId());
        data.put(DataBaseValues.ArchiveChatsTable.location, activeChat.getLocation());

        data.put(DataBaseValues.ArchiveChatsTable.visit_count, activeChat.getVisitCount());
        data.put(DataBaseValues.ArchiveChatsTable.visitor_browser, activeChat.getVisitorBrowser());
        data.put(DataBaseValues.ArchiveChatsTable.visitor_id, activeChat.getVisitorId());
        data.put(DataBaseValues.ArchiveChatsTable.visitor_ip, activeChat.getVisitorIp());
        data.put(DataBaseValues.ArchiveChatsTable.visitor_os, activeChat.getVisitorOs());

        data.put(DataBaseValues.ArchiveChatsTable.visitor_url, activeChat.getVisitorUrl());
        data.put(DataBaseValues.ArchiveChatsTable.visitor_query, activeChat.getVisitorQuery());
        data.put(DataBaseValues.ArchiveChatsTable.typing_message, activeChat.getTyping_message());
        data.put(DataBaseValues.ArchiveChatsTable.track_code, activeChat.getTrack_code());
        data.put(DataBaseValues.ArchiveChatsTable.tag_name, activeChat.getTag_name());






        data.put(DataBaseValues.ArchiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ArchiveChatsTable.chat_id+" = "+activeChat.getChatId();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    int result= db.update(TABLE_NAME,data,DataBaseValues.ArchiveChatsTable.chat_id+" = "+activeChat.getChatId(),null);
                    if(result>0)
                    {
                        Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask insertActiveChat","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask ArchiveChatsTableAsynckTask insertActiveChat","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.ArchiveChatsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask insertActiveChat","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask insertActiveChat","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
