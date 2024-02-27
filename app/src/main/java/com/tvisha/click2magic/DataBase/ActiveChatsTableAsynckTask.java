package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.Data;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActiveChatsTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;
    int type=0;

    public static final String TABLE_NAME = DataBaseValues.ActiveChatsTable.TABLE_NAME;

    




    @Override
    protected void onPostExecute(Boolean result) {
        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.ACTIVE_CHATS_SYNC_COMPLETED);
        }

        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.ACTIVE_CHATS_SYNC_COMPLETED);
        }

        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask ActiveChatsTableAsynckTask onPostExecute","called");

    }

    @Override
    protected  Boolean doInBackground(Object... params) {
        boolean status=false;
        context =(Context) params[0];
        type=(int) params[1];
        if(context!=null){
            dbContext = new DataBaseContext(context);
            if(dbContext!=null){
                db=dbContext.getDatabase();
                if(db!=null){
                    db.execSQL(DataBaseValues.ActiveChatsTable.CREATE_ACTIVE_CHATS_TABLE);
                }
            }

        }


        JSONArray jsonArray =(JSONArray) params[2];

        if(type==Values.Action.INSERT)
        {
            if(jsonArray!=null){
                for(int k = 0;k < jsonArray.length();k++){
                    JSONObject jsonObject=jsonArray.optJSONObject(k);
                    if(jsonObject!=null ){
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask insertChat",jsonObject.toString());
                        if(context!=null)
                        {
                            ActiveChat activeChat=setActiveUserData(jsonObject);
                            insertActiveChat(activeChat);
                        }
                    }

                }
            }


        }
        else if(type==Values.Action.DELETE){
            if(jsonArray!=null && jsonArray.length()>0){
                checkChats(jsonArray);
            }
            else
            {
                clearDb();
            }
        }

        return status;
    }
    public ActiveChat setActiveUserData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask setActiveUserData", jsonObject.toString());
        ActiveChat activeChat = null;
/*
        {"chat_id":1476,"visitor_id":1316,"visitor_tm_id":0,
                "guest_name":"sai","start_time":"2020-02-17T13:43:28.000Z",
                "end_time":"2020-02-17T13:45:25.000Z","visitor_os":"Windows (Windows 10)",
                "visitor_url":"http:\/\/localhost\/local.php",
                "chat_rating":"","quality":"",
                "category":0,"visitor_ip":"192.168.2.66",
                "visitor_browser":"Chrome (80.0.3987.106)",
                "agent_id":"10","chat_reference_id":"5a9585dc765e993c94",
                "location":",,","status":0,"visitor_query":"rerere",
                "chat_status":2,"account_id":53,"site_id":1,
                "track_code":"1WF561GP58101","created_at":"2020-02-17T13:43:28.000Z",
                "updated_at":"2020-02-17T13:45:25.000Z","name":"sai",
                "email":"sai@tvisha.in","mobile":"+919700008470","visit_count":1,
                "tm_visitor_id":25}*/

        try {
            if (jsonObject != null) {
                activeChat = new ActiveChat();
                activeChat.setChatId(jsonObject.optString(DataBaseValues.ActiveChatsTable.chat_id));
                activeChat.setVisitorId(jsonObject.optString(DataBaseValues.ActiveChatsTable.visitor_id));
                activeChat.setGuestName(jsonObject.optString("guest_name"));
                activeChat.setStartTime(jsonObject.optString(DataBaseValues.ActiveChatsTable.start_time));


                activeChat.setEndTime(jsonObject.optString(DataBaseValues.ActiveChatsTable.end_time));
                activeChat.setVisitorOs(jsonObject.optString(DataBaseValues.ActiveChatsTable.visitor_os));
                activeChat.setVisitorUrl(jsonObject.optString(DataBaseValues.ActiveChatsTable.visitor_url));
                activeChat.setChatRating(jsonObject.optString(DataBaseValues.ActiveChatsTable.chat_rating));


                activeChat.setVisitorIp(jsonObject.optString(DataBaseValues.ActiveChatsTable.visitor_ip));
                activeChat.setVisitorBrowser(jsonObject.optString(DataBaseValues.ActiveChatsTable.visitor_browser));
                activeChat.setAgentId(jsonObject.optString(DataBaseValues.ActiveChatsTable.agent_id));
                activeChat.setChatReferenceId(jsonObject.optString(DataBaseValues.ActiveChatsTable.chat_reference_id));
                activeChat.setLocation(jsonObject.optString(DataBaseValues.ActiveChatsTable.location));

                activeChat.setSiteId(jsonObject.optString(DataBaseValues.ActiveChatsTable.site_id));
                activeChat.setTrack_code(jsonObject.optString(DataBaseValues.ActiveChatsTable.track_code));
                activeChat.setCreatedAt(jsonObject.optString(DataBaseValues.ActiveChatsTable.created_at));
                activeChat.setUpdatedAt(jsonObject.optString(DataBaseValues.ActiveChatsTable.updated_at));
                activeChat.setName(jsonObject.optString(DataBaseValues.ActiveChatsTable.name));

                activeChat.setEmail(jsonObject.optString(DataBaseValues.ActiveChatsTable.email));
                activeChat.setMobile(jsonObject.optString(DataBaseValues.ActiveChatsTable.mobile));
                activeChat.setTmVisitorId(jsonObject.optString(DataBaseValues.ActiveChatsTable.tm_visitor_id));
                activeChat.setVisitCount(jsonObject.optString(DataBaseValues.ActiveChatsTable.visit_count));
                activeChat.setUser_name(jsonObject.optString(DataBaseValues.ActiveChatsTable.user_name));


                activeChat.setTag_name(jsonObject.optString(DataBaseValues.ActiveChatsTable.tag_name));
                activeChat.setRating(jsonObject.optString(DataBaseValues.ActiveChatsTable.rating));
                activeChat.setAccountId(jsonObject.optString(DataBaseValues.ActiveChatsTable.account_id));
                activeChat.setChatStatus(jsonObject.optString(DataBaseValues.ActiveChatsTable.chat_status));


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
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask insertActiveChat","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask ActiveChatsTableAsynckTask insertActiveChat","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.ArchiveChatsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask insertActiveChat","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask insertActiveChat","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void checkChats(JSONArray jsonArray){

        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask checkChats","method called");


        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME;

            Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask checkChats","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    String chatId= cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_id));
                    checkChatExists(Integer.parseInt(chatId),jsonArray);

                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask checkChats","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask checkChats","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }


    }

    private void checkChatExists(int chatId,JSONArray jsonArray)
    {
        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask ","checkChatExists called");
        boolean isExists=false;
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){

                if(context!=null)
                {
                    int chat_id=jsonObject.optInt("chat_id");
                    if(chat_id==chatId){
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask ","checkChatExists true");
                        isExists=true;
                        break;
                    }


                }
            }

        }
        if(!isExists)
        {
            deleteActiveChat(chatId);
        }

    }

    public void deleteActiveChat(int chatId){

        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask ","deleteActiveChat called");

        int delete = db.delete(TABLE_NAME, DataBaseValues.ActiveChatsTable.chat_id+" = "+chatId, null);

    }

    public void clearDb(){
        //delete from
        try{
            String query="delete from "+ TABLE_NAME;
            Helper.getInstance().LogDetails("clearDb","query "+query);
            db.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
