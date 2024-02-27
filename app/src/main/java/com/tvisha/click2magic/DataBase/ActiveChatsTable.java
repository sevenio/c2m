package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvisha.click2magic.Helper.FileFormatHelper;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActiveChatsTable extends BaseTable  {

    public static final String TABLE_NAME = DataBaseValues.ActiveChatsTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    public ActiveChatsTable(Context context){
        super(context);
        this.context=context;
        if(db!=null){
            db.execSQL(DataBaseValues.ActiveChatsTable.CREATE_ACTIVE_CHATS_TABLE);
        }


    }

    public void insertActiveChat(ActiveChat activeChat){

        long status=0;
        Cursor cursor = null;
        ContentValues data = new ContentValues();

        data.put(DataBaseValues.ActiveChatsTable.chat_id, activeChat.getChatId());
        data.put(DataBaseValues.ActiveChatsTable.site_id, activeChat.getSiteId());
        data.put(DataBaseValues.ActiveChatsTable.agent_id, activeChat.getAgentId());
        data.put(DataBaseValues.ActiveChatsTable.account_id, activeChat.getAccountId());
        data.put(DataBaseValues.ActiveChatsTable.chat_reference_id, activeChat.getChatReferenceId());

        data.put(DataBaseValues.ActiveChatsTable.chat_rating, activeChat.getChatRating());
        data.put(DataBaseValues.ActiveChatsTable.chat_status, activeChat.getChatStatus());
        data.put(DataBaseValues.ActiveChatsTable.email, activeChat.getEmail());
        data.put(DataBaseValues.ActiveChatsTable.guestName, activeChat.getGuestName());
        data.put(DataBaseValues.ActiveChatsTable.user_name, activeChat.getUser_name());

        data.put(DataBaseValues.ActiveChatsTable.online, activeChat.getOnline());
        data.put(DataBaseValues.ActiveChatsTable.status, activeChat.getStatus());
        data.put(DataBaseValues.ActiveChatsTable.unread_message_count, activeChat.getUnread_message_count());
        data.put(DataBaseValues.ActiveChatsTable.name, activeChat.getName());
        data.put(DataBaseValues.ActiveChatsTable.rating, activeChat.getRating());

        data.put(DataBaseValues.ActiveChatsTable.mobile, activeChat.getMobile());
        data.put(DataBaseValues.ActiveChatsTable.start_time, activeChat.getCreatedAt());
        data.put(DataBaseValues.ActiveChatsTable.end_time, activeChat.getEndTime());
        data.put(DataBaseValues.ActiveChatsTable.tm_visitor_id, activeChat.getTmVisitorId());
        data.put(DataBaseValues.ActiveChatsTable.location, activeChat.getLocation());

        data.put(DataBaseValues.ActiveChatsTable.visit_count, activeChat.getVisitCount());
        data.put(DataBaseValues.ActiveChatsTable.visitor_browser, activeChat.getVisitorBrowser());
        data.put(DataBaseValues.ActiveChatsTable.visitor_id, activeChat.getVisitorId());
        data.put(DataBaseValues.ActiveChatsTable.visitor_ip, activeChat.getVisitorIp());
        data.put(DataBaseValues.ActiveChatsTable.visitor_os, activeChat.getVisitorOs());

        data.put(DataBaseValues.ActiveChatsTable.visitor_url, activeChat.getVisitorUrl());
        data.put(DataBaseValues.ActiveChatsTable.visitor_query, activeChat.getVisitorQuery());
        data.put(DataBaseValues.ActiveChatsTable.typing_message, activeChat.getTyping_message());
        data.put(DataBaseValues.ActiveChatsTable.track_code, activeChat.getTrack_code());
        data.put(DataBaseValues.ActiveChatsTable.tag_name, activeChat.getTag_name());






        data.put(DataBaseValues.ActiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());

        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.chat_id+" = "+activeChat.getChatId();


        try{
            cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToNext()){


                    int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.chat_id+" = "+activeChat.getChatId(),null);
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
                data.put(DataBaseValues.ActiveChatsTable.created_at, Utilities.getCurrentDateTimeNew());
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

    public void userChatEnded(JSONObject jsonObject){
        try
        {
            if(jsonObject!=null ){
                int chat_id=jsonObject.optInt("chat_id");
                String chat_status=jsonObject.optString("chat_status");

                long status=0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();


                data.put(DataBaseValues.ActiveChatsTable.chat_status, chat_status);
                data.put(DataBaseValues.ActiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());
                String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.chat_id+" = "+chat_id;

                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0)
                {

                    if(cursor.moveToNext()){


                        int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.chat_id+" = "+chat_id,null);
                        if(result>0)
                        {
                            Helper.getInstance().LogDetails("SyncData userChatEnded","updated true");

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("SyncData userChatEnded","updated false");
                        }

                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void contactUpdate(JSONObject jsonObject){
        try
        {
            if(jsonObject!=null ){

                String agentId = jsonObject.optString("agent_id");
                String chatReference = jsonObject.optString("chatReference");
                JSONObject data1=  jsonObject.optJSONObject("data");
                if(data1!=null) {

                    String type = data1.optString("type");
                    String value = data1.optString("value");


                    long status=0;
                    Cursor cursor = null;
                    ContentValues data = new ContentValues();

                    if(type!=null && type.equals("1")){
                        data.put(DataBaseValues.ActiveChatsTable.mobile, value);
                    }
                    else
                    {
                        data.put(DataBaseValues.ActiveChatsTable.email, value);
                    }

                    data.put(DataBaseValues.ActiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());
                    String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReference+"'";

                    cursor = db.rawQuery(query,null);
                    if(cursor!=null && cursor.getCount()>0)
                    {

                        if(cursor.moveToNext()){


                            int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReference+"'",null);
                            if(result>0)
                            {
                                Helper.getInstance().LogDetails("SyncData contactUpdate","updated true");

                            }
                            else
                            {
                                Helper.getInstance().LogDetails("SyncData contactUpdate","updated false");
                            }

                        }
                    }
                    else
                    {
                        Helper.getInstance().LogDetails("SyncData contactUpdate","results empty");
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void userOnline(JSONObject jsonObject){
        try
        {
            if(jsonObject!=null ){


                String tmUserId=jsonObject.optString("user_id");
                String chatReferenceId=jsonObject.optString("reference_token");



                long status=0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();
                data.put(DataBaseValues.ActiveChatsTable.online, 1);




                data.put(DataBaseValues.ActiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());
               // String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReferenceId+"'";
                String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.tm_visitor_id+" = '"+tmUserId+"'";

                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0)
                {

                    if(cursor.moveToNext()){


                       // int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReferenceId+"'",null);
                        int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.tm_visitor_id+" = '"+tmUserId+"'",null);
                        if(result>0)
                        {
                            Helper.getInstance().LogDetails("SyncData userOnline","updated true");

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("SyncData userOnline","updated false");
                        }

                    }
                }
                else
                {
                    Helper.getInstance().LogDetails("SyncData userOnline","results empty");
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void userOffline(JSONObject jsonObject){
        try
        {
            if(jsonObject!=null ){


                String tmUserId=jsonObject.optString("user_id");
                String chatReferenceId=jsonObject.optString("reference_token");



                long status=0;
                Cursor cursor = null;
                ContentValues data = new ContentValues();
                data.put(DataBaseValues.ActiveChatsTable.online, 0);




                data.put(DataBaseValues.ActiveChatsTable.updated_at, Utilities.getCurrentDateTimeNew());
              //  String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReferenceId+"'";
                String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+DataBaseValues.ActiveChatsTable.tm_visitor_id+" = '"+tmUserId+"'";

                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0)
                {

                    if(cursor.moveToNext()){


                      //  int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.chat_reference_id+" = '"+chatReferenceId+"'",null);
                        int result= db.update(TABLE_NAME,data,DataBaseValues.ActiveChatsTable.tm_visitor_id+" = '"+tmUserId+"'",null);
                        if(result>0)
                        {
                            Helper.getInstance().LogDetails("SyncData userOffline","updated true");

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("SyncData userOffline","updated false");
                        }

                    }
                }
                else
                {
                    Helper.getInstance().LogDetails("SyncData userOffline","results empty");
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<ActiveChat> getActiveChatList(int site_id){

        Helper.getInstance().LogDetails("getActiveChatList","method called"+site_id);
        List<ActiveChat> activeChatList=new ArrayList<>();

        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.ActiveChatsTable.site_id+ " = " +site_id
                     + " ORDER BY "+DataBaseValues.ActiveChatsTable.created_at+" DESC,"+DataBaseValues.ActiveChatsTable.id;;


            Helper.getInstance().LogDetails("getActiveChatList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    ActiveChat model=new ActiveChat();
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_id)));
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.site_id)));
                    model.setAgentId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.agent_id)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.account_id)));


                    model.setChatStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_status)));
                    model.setChatRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_rating)));
                    model.setChatReferenceId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_reference_id)));
                    model.setTyping_message(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.typing_message)));
                    model.setTrack_code(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.track_code)));

                    model.setTmVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.tm_visitor_id)));
                    model.setTag_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.tag_name)));
                    model.setStartTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.start_time)));
                    model.setEndTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.end_time)));
                    model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.status)));

                    model.setVisitorQuery(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_query)));
                    model.setVisitCount(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visit_count)));
                    model.setVisitorIp(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_ip)));
                    model.setVisitorOs(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_os)));
                    model.setVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_id)));

                    model.setVisitorBrowser(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_browser)));
                    model.setVisitorUrl(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_url)));
                    model.setOnline(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.online)));
                    model.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.email)));
                    model.setGuestName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.guestName)));

                    model.setUser_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.user_name)));
                    model.setUnread_message_count(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.unread_message_count)));
                    model.setMobile(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.mobile)));
                    model.setRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.rating)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.name)));

                    activeChatList.add(model);

                    Helper.getInstance().LogDetails("getActiveChatList","called"+model.getChatId()+" "+model.getGuestName());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getActiveChatList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getActiveChatList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  activeChatList;
    }

    public String getUserName(int agent_id){

        Helper.getInstance().LogDetails("getUserName","method called"+agent_id);

        String userName="";
        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.ActiveChatsTable.agent_id+ " = " +agent_id +" AND "
                    + " ORDER BY "+DataBaseValues.ActiveChatsTable.created_at+" DESC,"+DataBaseValues.ActiveChatsTable.id;;


            Helper.getInstance().LogDetails("getUserName","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

            //    do{

                    userName=cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.user_name));

                    Helper.getInstance().LogDetails("getUserName","called"+userName);
                //}while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getUserName","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getUserName","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  userName;
    }

    public ActiveChat getActiveChat(int agent_id,int site_id){

        Helper.getInstance().LogDetails("getActiveChat","method called"+agent_id);

        ActiveChat model=new ActiveChat();
        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +
                    DataBaseValues.ActiveChatsTable.agent_id+ " = " +agent_id +" AND "+DataBaseValues.ActiveChatsTable.site_id+ " = " +site_id
                    + " ORDER BY "+DataBaseValues.ActiveChatsTable.created_at+" DESC,"+DataBaseValues.ActiveChatsTable.id;;


            Helper.getInstance().LogDetails("getActiveChat","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_id)));
                    model.setSiteId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.site_id)));
                    model.setAgentId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.agent_id)));
                    model.setAccountId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.account_id)));
                    model.setChatId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_id)));

                    model.setChatStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_status)));
                    model.setChatRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_rating)));
                    model.setChatReferenceId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_reference_id)));
                    model.setTyping_message(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.typing_message)));
                    model.setTrack_code(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.track_code)));

                    model.setTmVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.tm_visitor_id)));
                    model.setTag_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.tag_name)));
                    model.setStartTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.start_time)));
                    model.setEndTime(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.end_time)));
                    model.setStatus(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.status)));

                    model.setVisitorQuery(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_query)));
                    model.setVisitCount(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visit_count)));
                    model.setVisitorIp(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_ip)));
                    model.setVisitorOs(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_os)));
                    model.setVisitorId(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_id)));

                    model.setVisitorBrowser(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_browser)));
                    model.setVisitorUrl(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.visitor_url)));
                    model.setOnline(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.online)));
                    model.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.email)));
                    model.setGuestName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.guestName)));

                    model.setUser_name(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.user_name)));
                    model.setUnread_message_count(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.unread_message_count)));
                    model.setMobile(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.mobile)));
                    model.setRating(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.rating)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.name)));



                    Helper.getInstance().LogDetails("getActiveChat","called"+model.getChatId()+" "+model.getGuestName()+" "+model.getChatStatus());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getActiveChat","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getActiveChat","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  model;
    }
    public String getConversationReferenceIdList(){

        String conversation_ref_id="";

        Cursor cursor = null;
        try{

            String query= "SELECT * FROM " +TABLE_NAME;


            Helper.getInstance().LogDetails("getConversationReferenceIdList","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                   String s= cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_reference_id));
                   if(conversation_ref_id.equals(""))
                   {
                       conversation_ref_id=s;
                   }
                   else
                   {
                       conversation_ref_id=conversation_ref_id+","+s;
                   }


                    Helper.getInstance().LogDetails("getConversationReferenceIdList","called"+s);
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getConversationReferenceIdList","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getConversationReferenceIdList","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  conversation_ref_id;
    }

    public void deleteActiveChat(int chatId){

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

    public JSONObject getActiveChatsOfSites() {
        Cursor cursor = null;
        JSONObject userObj = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            //2
            //3
            //SELECT a1, a2, b1, b2
            //FROM A
            //INNER JOIN B on B.f = A.f;

            String query="select count(ag.id) as count,s.site_name as site_name,s.site_id as site_id,s.site_token as site_token,s.account_id as account_id,* from ActiveChatsTable as `ac`  INNER JOIN AgentsTable as `ag` ON ag.user_site_id = ac.site_id  INNER JOIN SitesTable as `s` ON s.site_id = ac.site_id where ac.chat_status = 1 GROUP BY ac.id ORDER BY ac.created_at DESC";
         //   String count_query="select count(c.is_read) as count,a.user_name as user_name,a.name as name,a.guestName as guestName,a.site_id as site_id,s.site_name,* from conversationTable as `c`  INNER JOIN ActiveChatsTable as `a` ON a.tm_visitor_id = c.sender_id  INNER JOIN SitesTable as `s` ON s.site_id = a.site_id where c.receiver_id = "+tmUserId+ " and c.is_read <> 1 and c.message_id <> 0 GROUP BY c.id ORDER BY c.created_at DESC";

            Helper.getInstance().LogDetails("getActiveChatsOfSites query ",query);
            cursor = selectByManualQuery(query);
            if (cursor!=null && cursor.moveToFirst()){
                do {

                    int val = cursor.getInt(cursor.getColumnIndex("count"));

                    String site_name=(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_name)));
                    String site_id=(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)));
                    String site_token=(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_token)));

                    Helper.getInstance().LogDetails("getActiveChatsOfSites res ",val+" "+" site_name "+site_name+" site_id  "+site_id+" site_token "+site_token);


                    JSONObject data = new JSONObject();
                    data.put("guestName",cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.guestName)));
                    data.put("name",cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.guestName)));
                    data.put("site_id",cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.site_id)));
                    data.put("chat_id",cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.chat_id)));


                    if(userObj.has(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)))){
                        JSONArray tmpArr = userObj.getJSONArray(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)));
                        tmpArr.put(data);
                        userObj.put(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)),tmpArr);
                    }else{
                        JSONArray tmpArr = new JSONArray();
                        tmpArr.put(data);
                        userObj.put(cursor.getString(cursor.getColumnIndex(DataBaseValues.SitesTable.site_id)),tmpArr);
                    }

                }while (cursor.moveToNext());
                cursor.close();
            }
            object.put("sites",userObj);

            Helper.getInstance().LogDetails("getActiveChatsOfSites object  ",object.toString());

        }catch (Exception e){
            Helper.getInstance().LogDetails("getActiveChatsOfSites exc ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return object;
    }
}
