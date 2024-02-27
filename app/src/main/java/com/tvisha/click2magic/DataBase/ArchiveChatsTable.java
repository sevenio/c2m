package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import java.util.ArrayList;
import java.util.List;


public class ArchiveChatsTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.ArchiveChatsTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;

    public ArchiveChatsTable(Context context){
        super(context);
        this.context=context;

        if(db!=null){
            db.execSQL(DataBaseValues.ArchiveChatsTable.CREATE_ACTIVE_CHATS_TABLE);
        }


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
                        Helper.getInstance().LogDetails("insertActiveChat","updated true");

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("insertActiveChat","updated false");
                    }

                }
            }else{
                data.put(DataBaseValues.ArchiveChatsTable.created_at, Utilities.getCurrentDateTimeNew());
                status = db.insert(TABLE_NAME, null, data);
                if(status>0){
                    Helper.getInstance().LogDetails("insertActiveChat","inserted true");
                }else
                {
                    Helper.getInstance().LogDetails("insertActiveChat","inserted true");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public List<ActiveChat> getArchiveChatList(int site_id){

        Helper.getInstance().LogDetails("getArchiveChatList","method called"+site_id);
        List<ActiveChat> activeChatList=new ArrayList<>();

        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.ArchiveChatsTable.site_id+ " = " +site_id+" ORDER BY "+DataBaseValues.ArchiveChatsTable.start_time+" DESC ";


            Helper.getInstance().LogDetails("getArchiveChatList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    ActiveChat model=new ActiveChat();
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_id)));
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.site_id)));
                    model.setAgentId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.agent_id)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.account_id)));
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_id)));

                    model.setChatStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_status)));
                    model.setChatRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_rating)));
                    model.setChatReferenceId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_reference_id)));
                    model.setTyping_message(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.typing_message)));
                    model.setTrack_code(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.track_code)));

                    model.setTmVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.tm_visitor_id)));
                    model.setTag_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.tag_name)));
                    model.setStartTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.start_time)));
                    model.setEndTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.end_time)));
                    model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.status)));

                    model.setVisitorQuery(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_query)));
                    model.setVisitCount(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visit_count)));
                    model.setVisitorIp(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_ip)));
                    model.setVisitorOs(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_os)));
                    model.setVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_id)));

                    model.setVisitorBrowser(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_browser)));
                    model.setVisitorUrl(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_url)));
                    model.setOnline(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.online)));
                    model.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.email)));
                    model.setGuestName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.guestName)));

                    model.setUser_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.user_name)));
                    model.setUnread_message_count(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.unread_message_count)));
                    model.setMobile(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.mobile)));
                    model.setRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.rating)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.name)));

                    activeChatList.add(model);

                    Helper.getInstance().LogDetails("getArchiveChatList","called"+model.getChatId()+" "+model.getGuestName()+" "+model.toString());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getArchiveChatList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getArchiveChatList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  activeChatList;
    }



    public List<ActiveChat> getArchiveChatListWithAgentId(int site_id,int agentId){

        Helper.getInstance().LogDetails("getArchiveChatList","method called"+site_id);
        List<ActiveChat> activeChatList=new ArrayList<>();

        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.ArchiveChatsTable.site_id+ " = " +site_id+" AND "+  DataBaseValues.ArchiveChatsTable.agent_id+ " = " +agentId
                    +" ORDER BY "+DataBaseValues.ArchiveChatsTable.start_time+" DESC ";


            Helper.getInstance().LogDetails("getArchiveChatList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    ActiveChat model=new ActiveChat();
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_id)));
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.site_id)));
                    model.setAgentId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.agent_id)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.account_id)));
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_id)));

                    model.setChatStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_status)));
                    model.setChatRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_rating)));
                    model.setChatReferenceId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.chat_reference_id)));
                    model.setTyping_message(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.typing_message)));
                    model.setTrack_code(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.track_code)));

                    model.setTmVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.tm_visitor_id)));
                    model.setTag_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.tag_name)));
                    model.setStartTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.start_time)));
                    model.setEndTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.end_time)));
                    model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.status)));

                    model.setVisitorQuery(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_query)));
                    model.setVisitCount(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visit_count)));
                    model.setVisitorIp(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_ip)));
                    model.setVisitorOs(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_os)));
                    model.setVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_id)));

                    model.setVisitorBrowser(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_browser)));
                    model.setVisitorUrl(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.visitor_url)));
                    model.setOnline(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.online)));
                    model.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.email)));
                    model.setGuestName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.guestName)));

                    model.setUser_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.user_name)));
                    model.setUnread_message_count(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.unread_message_count)));
                    model.setMobile(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.mobile)));
                    model.setRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.rating)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ArchiveChatsTable.name)));

                    activeChatList.add(model);

                    Helper.getInstance().LogDetails("getArchiveChatList","called"+model.getChatId()+" "+model.getGuestName());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getArchiveChatList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getArchiveChatList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  activeChatList;
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
