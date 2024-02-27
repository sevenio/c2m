package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonObject;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.FileFormatHelper;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.Chat;

import com.tvisha.click2magic.model.GalleryModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.http.Header;



public class ConversationTable extends BaseTable {

   public static final String TABLE_NAME = DataBaseValues.ConversationTable.TABLE_NAME;
    public SQLiteDatabase db = database;
    private Context context;


    String ACCESS_TOKEN="";
         int   TM_USER_ID=0;

   final   public static String access_token ="access_token";

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


    String[] table = {

            id,
            sender_id,
            receiver_id,
            message_type,
            message_id,
            reference_id,
            is_group,
            is_sync,
            is_reply,
            message,
            conversation_reference_id,
            attachment,
            attachment_extension,
            attachment_name,
            attachment_device_path,
            original_message_id,
            is_read,
            is_delivered,
            is_received,
            is_downloaded,
            is_available,
            original_message,
            caption,
            created_at,
            updated_at
    };


    public ConversationTable(Context context){
        super(context);
        this.context=context;

        this.ACCESS_TOKEN=Session.getAccessToken(context);
        if(! Session.getTmUserId(context).isEmpty())
        {
            this.TM_USER_ID= Integer.parseInt( Session.getTmUserId(context));
        }
        if(db!=null){
            db.execSQL(DataBaseValues.ConversationTable.CREATE_CHAT_TABLE);
        }
      //  checkTable();



    }


    public static String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            id+" Integer PRIMARY KEY AUTOINCREMENT,"+
            message_id+" Integer,"+
            sender_id+" Integer,"+
            receiver_id+" Integer ,"+
            message+" Text ,"+
            attachment+" Text ,"+
            attachment_extension+" Text ,"+
            attachment_name+" Text ,"+
            is_group+" Integer,"+
            is_reply+" Integer,"+
            original_message_id+" Integer,"+
            is_sync+" Integer,"+
            is_read+" Integer ,"+
            is_delivered+" Integer ,"+
            is_received+" Integer ,"+
            message_type+" Integer ,"+
            is_downloaded+" Integer ,"+
            conversation_reference_id+" Text ,"+
            is_available+" Integer ,"+
            reference_id+" Text ,"+
            original_message+" Text ,"+
            caption+" Text ,"+
            created_at+" datetime,"+
            updated_at+" datetime" +
            ");";

    private void checkTable(){
        if(table!=null && table.length>0){
            for(int i=0;i<table.length;i++){
              boolean isPresent=  existsColumnInTable(db,TABLE_NAME,table[i]);
              Helper.getInstance().LogDetails("checkTable",isPresent+"");
              String new_column=table[i];
              if(!isPresent){
                  try
                  {
                         if(new_column.equals(message_id)
                              || new_column.equals(sender_id)
                              || new_column.equals(is_group)
                              || new_column.equals(is_reply)
                              || new_column.equals(is_sync)
                              || new_column.equals(is_read)
                              || new_column.equals(is_delivered)
                              || new_column.equals(is_received)
                              || new_column.equals(message_type)
                              || new_column.equals(is_downloaded)
                              || new_column.equals(is_available)

                              )
                         {
                             db.execSQL("ALTER TABLE conversationTable  ADD COLUMN "+new_column+"INTEGER DEFAULT 0");
                         }
                         else if(new_column.equals(message)
                                 || new_column.equals(attachment)
                                 || new_column.equals(attachment_extension)
                                 || new_column.equals(attachment_name)
                                 || new_column.equals(conversation_reference_id)
                                 || new_column.equals(reference_id)
                                 || new_column.equals(original_message)
                                 || new_column.equals(caption)

                                 )
                         {
                             db.execSQL("ALTER TABLE conversationTable  ADD COLUMN "+new_column+" TEXT ");
                         }



                  }catch (Exception e){

                  }


              }
            }
        }
    }


    private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = inDatabase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Helper.getInstance().LogDetails("existsColumnInTable","not exists");
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    public String insertChat(ChatModel model) {



        boolean updateStatus=false;
        String reference_id_val=" ";

        long status=0,result=0;
        ContentValues data = new ContentValues();
        data.put(message_id, model.getMessage_id());
        data.put(sender_id, model.getSender_id());
        data.put(receiver_id, model.getReceiver_id());
        data.put(message, model.getMessage());
        data.put(attachment, model.getAttachment());
        data.put(attachment_name, model.getAttachment_name());
        data.put(attachment_extension, model.getAttachment_extension());
        data.put(attachment_device_path, model.getAttachmentDevicePath());
        data.put(is_group, model.getIs_group());
        data.put(is_reply,model.getIs_reply());
        data.put(is_sync,model.getIs_sync());
        data.put(is_read,model.getIs_read());
        data.put(is_delivered, model.getIs_delivered());
        data.put(is_received, model.getIs_received());
        data.put(message_type, model.getMessage_type());
        data.put(original_message, model.getOriginal_message());
        data.put(is_downloaded, model.getIs_downloaded());
        data.put(caption, model.getCaption());
        data.put(conversation_reference_id, model.getConversation_reference_id());
        data.put(reference_id, model.getReference_id());
        data.put(original_message_id, model.getOriginal_message_id());
        data.put(created_at, Utilities.getCurrentDateTimeNew());
        data.put(updated_at, Utilities.getCurrentDateTimeNew());
        status = db.insert(TABLE_NAME, null, data);
        if (status > 0) {


            int id_val=(int)status;

             reference_id_val=id_val+model.getReference_id();

            ContentValues data1 = new ContentValues();
            data1.put(reference_id, reference_id_val);
            data1.put(updated_at,Utilities.getCurrentDateTimeNew());

            result=  db.update(TABLE_NAME, data1, "id="+id_val, null);

            if(result>0)
            {

                updateStatus=true;
                SyncData.syncMessage(context,reference_id_val);
               // SyncData.syncChat(context,model.getSender_id(),model.getReceiver_id());
            }
            else
            {

                updateStatus=false;
            }


        } else {

            updateStatus=false;

        }
      return reference_id_val;
    }

    public boolean addReceiveMessage(ChatModel model) {

        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addReceiveMessage","called"+model.getConversation_reference_id());

        long status=0;


        ContentValues data = new ContentValues();
        data.put(message_id, model.getMessage_id());
        data.put(sender_id, model.getSender_id());
        data.put(receiver_id, model.getReceiver_id());
        data.put(message, model.getMessage());
        data.put(is_group, model.getIs_group());
        data.put(is_reply,model.getIs_reply());
        data.put(is_sync,model.getIs_sync());
        data.put(is_delivered,model.getIs_delivered());
        data.put(is_received,model.getIs_received());
        data.put(message_type, model.getMessage_type());
        data.put(reference_id, model.getReference_id());
        data.put(conversation_reference_id, model.getConversation_reference_id());
        data.put(created_at, model.getCreated_at());
        data.put(is_read, model.getIs_read());
        data.put(attachment, model.getAttachment());
        data.put(attachment_name, model.getAttachment_name());
        data.put(attachment_extension, model.getAttachment_extension());
        data.put(is_downloaded, 0);
        data.put(updated_at, Utilities.getCurrentDateTimeNew());

        status = db.insert(TABLE_NAME, null, data);
        if (status > 0) {

            return true;
        } else {

            return false;
        }
    }

    public JSONArray getUploadedAttachment(){

        Helper.getInstance().LogDetails("getUploadedAttachment","called");
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;

        ACCESS_TOKEN=Session.getAccessToken(context);

        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
                // String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = "+reference_id_val;
                String type=Values.MessageType.MESSAGE_TYPE_ATTACHMENT+"";
                String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+attachment+" = '"+""+"'" +" AND "+message_type+" = '"+type+"'" ;
                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
                    Helper.getInstance().LogDetails("getUploadedAttachment","if called");
                    do {
                        try{
                            JSONObject jsonObject = new JSONObject();
                            if(cursor.getInt(cursor.getColumnIndex(message_type))==Values.MessageType.MESSAGE_TYPE_ATTACHMENT){
                                jsonObject.put(receiver_id,cursor.getInt(cursor.getColumnIndex(receiver_id)));
                                jsonObject.put(sender_id,cursor.getInt(cursor.getColumnIndex(sender_id)));
                                jsonObject.put(message,cursor.getString(cursor.getColumnIndex(message)));
                                jsonObject.put(reference_id,cursor.getString(cursor.getColumnIndex(reference_id)));
                                jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                                jsonObject.put(message_type,cursor.getInt(cursor.getColumnIndex(message_type)));
                                jsonObject.put(attachment_device_path,cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                                Helper.getInstance().LogDetails("getUploadedAttachment nonsync query  message data",jsonObject.toString());
                                jsonArray.put(jsonObject);
                            }





                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }
                else
                {
                    Helper.getInstance().LogDetails("getUploadedAttachment","else called");
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }
        return jsonArray;
    }

    public JSONArray getNonSyncMessage(String reference_id_val){
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;

        ACCESS_TOKEN=Session.getAccessToken(context);

        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
                // String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = "+reference_id_val;
                String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = '"+reference_id_val+"'";
                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
                    do {
                        try{
                            JSONObject jsonObject = new JSONObject();
                            if(cursor.getInt(cursor.getColumnIndex(message_type))==0){

                                jsonObject.put(access_token,ACCESS_TOKEN);
                                jsonObject.put(receiver_id,cursor.getInt(cursor.getColumnIndex(receiver_id)));
                                jsonObject.put(message,cursor.getString(cursor.getColumnIndex(message)));
                                jsonObject.put(reference_id,cursor.getString(cursor.getColumnIndex(reference_id)));
                                jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                                jsonObject.put(message_type,cursor.getInt(cursor.getColumnIndex(message_type)));
                                Helper.getInstance().LogDetails("chat nonsync query  message data",jsonObject.toString());
                                jsonArray.put(jsonObject);
                            }
                            else{
                                jsonObject.put(access_token,ACCESS_TOKEN);
                                jsonObject.put(receiver_id,cursor.getInt(cursor.getColumnIndex(receiver_id)));
                                jsonObject.put(reference_id,cursor.getString(cursor.getColumnIndex(reference_id)));
                                jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                                jsonObject.put(attachment,cursor.getString(cursor.getColumnIndex(attachment)));
                                jsonObject.put(message_type,cursor.getInt(cursor.getColumnIndex(message_type)));

                                Helper.getInstance().LogDetails("chat nonsync query  message_attachment data",jsonObject.toString());
                                if(cursor.getString(cursor.getColumnIndex(attachment))!=null && !cursor.getString(cursor.getColumnIndex(attachment)).trim().isEmpty())
                                {
                                    jsonArray.put(jsonObject);
                                }

                            }




                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }


        return jsonArray;
    }


    public  void deleteRecords(){
        try {
            String query="DELETE FROM " +
                    TABLE_NAME+
                    " WHERE " +
                    id +"<" +
                    "("+"SELECT * FROM "+
                    "("+"SELECT "+
                    "("+"MAX"+"("+id+")"+-51+")"+
                    " FROM"+ TABLE_NAME+ ")"
                    +")";
            db.execSQL(query);
        }catch (Exception e){

        }
    }

    public void dropTable(){
        try
        {
           // String query=" DROP TABLE IF EXISTS "+TABLE_NAME;
            String query=" DROP TABLE IF EXISTS conversationTable";
            Helper.getInstance().LogDetails("dropTable","query "+query);
            db.execSQL(query);
        }catch (Exception e){
            Helper.getInstance().LogDetails("dropTable"," exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }

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


    public JSONArray getNonSyncChat(){
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;

        ACCESS_TOKEN=Session.getAccessToken(context);
        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
                // String query = "select * from "+TABLE_NAME+" WHERE "+is_sync+" = 0"+"  AND "+message_id+" = 0";
                String query = "select * from "+TABLE_NAME+" WHERE "+is_sync+" = 0"+" AND "+ "("+ message_id+ " = 0" + " AND "+ sender_id+ " = " +TM_USER_ID  +  ")";
                cursor = db.rawQuery(query,null);
                if(cursor.moveToFirst()){
                    do {
                        try{
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(access_token,ACCESS_TOKEN);
                            jsonObject.put(ConversationTable.receiver_id,cursor.getInt(cursor.getColumnIndex(ConversationTable.receiver_id)));
                            jsonObject.put(message,cursor.getString(cursor.getColumnIndex(message)));
                            jsonObject.put(reference_id,cursor.getString(cursor.getColumnIndex(reference_id)));
                            jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                            jsonObject.put(message_type,cursor.getInt(cursor.getColumnIndex(message_type)));

                            jsonArray.put(jsonObject);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }
        return jsonArray;
    }


    public JSONArray getNonDeliveredChat(){
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        ACCESS_TOKEN=Session.getAccessToken(context);
        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
                // String query = "select * from "+TABLE_NAME+" WHERE "+is_delivered+" = 0";
                String query = "select * from "+TABLE_NAME+" WHERE "+is_delivered+" = 0"+" AND "+is_received+" =0";
                cursor = db.rawQuery(query,null);
                if(cursor.moveToFirst()){
                    do {
                        try{


                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(access_token,ACCESS_TOKEN);
                            jsonObject.put(is_group,cursor.getInt(cursor.getColumnIndex(is_group)));
                            jsonObject.put(message_id,cursor.getInt(cursor.getColumnIndex(message_id)));

                            jsonArray.put(jsonObject);




                            int id_val=cursor.getInt(cursor.getColumnIndex(id));


                            ContentValues data = new ContentValues();
                            data.put(is_delivered, 1);
                            data.put(updated_at,Utilities.getCurrentDateTimeNew());


                            int  status=  db.update(TABLE_NAME, data, "id="+id_val, null);



                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }

        return jsonArray;
    }

    public JSONArray getRecivedChat(){
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        ACCESS_TOKEN=Session.getAccessToken(context);
        String tmUserId= Session.getTmUserId(context);
        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
                //String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  ;
                String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +receiver_id+ " = " +tmUserId+ " AND "+ is_delivered+ " = 0";
                //String query = "select * from "+TABLE_NAME+" WHERE "+is_delivered+" = 0"+" AND "+is_received+" =1";

                Helper.getInstance().LogDetails("getRecivedChat",tmUserId+"  "+query);
                cursor = db.rawQuery(query,null);
                if(cursor.moveToFirst()){
                    do {
                        try{


                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(access_token,ACCESS_TOKEN);
                            jsonObject.put(is_group,cursor.getInt(cursor.getColumnIndex(is_group)));
                            jsonObject.put(message_id,cursor.getInt(cursor.getColumnIndex(message_id)));
                            jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));

                            jsonArray.put(jsonObject);




                            int id_val=cursor.getInt(cursor.getColumnIndex(id));


                            ContentValues data = new ContentValues();
                            data.put(is_delivered, 1);
                            data.put(updated_at,Utilities.getCurrentDateTimeNew());


                            int  status=  db.update(TABLE_NAME, data, "id="+id_val, null);


                            if(status>0)
                            {
                                Helper.getInstance().LogDetails("chat received update true","");
                            }
                            else
                            {
                                Helper.getInstance().LogDetails("chat received update false","");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }

        return jsonArray;
    }





    public JSONArray getNonReadChat(int senderId, int receiverId,String conversationReferenceId){
        Helper.getInstance().LogDetails("getNonReadChat ",senderId+" "+receiverId +" "+conversationReferenceId);
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        ACCESS_TOKEN=Session.getAccessToken(context);
        if(ACCESS_TOKEN!=null && !ACCESS_TOKEN.trim().isEmpty())
        {
            try{
            /*String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +is_read+ " = 0"  +" AND "+ "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")";*/
                // String query = "select * from "+TABLE_NAME+" WHERE "+is_read+" = 0";
            /*String query= "SELECT * FROM " +TABLE_NAME+ " WHERE "+
                    "(" +is_read+ " = 0"  +" AND " +is_received+ " = 1"+  ")"+ " AND "+
                    conversation_reference_id+"= '"+conversationReferenceId+
                    "' AND "+
                    "("+ receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")";*/
      /*      String query= "SELECT * FROM " +TABLE_NAME+ " WHERE "+
                    "(" +is_read+ " = 0"  +" AND " +is_received+ " = 1"+  ")"+ " AND "+
                    conversation_reference_id+"= '"+conversationReferenceId+ "'";
            cursor = db.rawQuery(query,null);*/
                String query= "SELECT * FROM " +TABLE_NAME+ " WHERE "+
                        "(" +is_read+ " = 0"  +" AND " +receiver_id+ " = "+receiverId+  ")"+ " AND "+
                        conversation_reference_id+"= '"+conversationReferenceId+ "'";
                cursor = db.rawQuery(query,null);
                if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
                    do {
                        try{

                            Helper.getInstance().LogDetails("getNonReadChat ","count "+cursor.getCount());


                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(access_token,ACCESS_TOKEN);
                            jsonObject.put(is_group,cursor.getInt(cursor.getColumnIndex(is_group)));
                            jsonObject.put(message_id,cursor.getInt(cursor.getColumnIndex(message_id)));
                            jsonObject.put(conversation_reference_id,cursor.getString(cursor.getColumnIndex(conversation_reference_id)));

                            jsonArray.put(jsonObject);


                            int id_val=cursor.getInt(cursor.getColumnIndex(id));


                            ContentValues data = new ContentValues();
                            data.put(is_read, 1);
                            data.put(updated_at,Utilities.getCurrentDateTimeNew());


                            int  status=  db.update(TABLE_NAME, data, "id="+id_val, null);






                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }

        return jsonArray;
    }


    public JSONObject changeAvailbleStatus(int status){
        JSONObject jsonObject = new JSONObject();
        try {
            //1-online 2-offline
            jsonObject.put(access_token,ACCESS_TOKEN);
            jsonObject.put("status",status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    public boolean checkMessageIdAndInsert(JSONObject jsonObject){
        Helper.getInstance().LogDetails("getNonReadChat SOCKET_RECEIVE_MESSAGE checkMessageIdAndInsert","called"+jsonObject.toString());
        Cursor cursor = null;
        boolean status=false;
        try{

            //String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+message_id+" = "+model.getMessage_id();
              //  String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = "+model.getReference_id()+" AND "+message_id+" = "+model.getMessage_id();


            ChatModel model=new ChatModel();

            model.setSender_id(jsonObject.optInt(sender_id));
            model.setReceiver_id(jsonObject.optInt(receiver_id));
            model.setMessage_type(jsonObject.optInt(message_type));
            model.setReference_id(jsonObject.optString(reference_id));
            model.setIs_reply(jsonObject.optInt(is_reply));
            model.setMessage(jsonObject.optString(message));
            model.setCreated_at(jsonObject.optString(created_at));
            model.setMessage_id(jsonObject.optInt(message_id));
            model.setAttachment(jsonObject.optString(attachment));
            model.setAttachment_name(jsonObject.optString(attachment_name));
            model.setAttachment_extension(jsonObject.optString(attachment_extension));
            model.setConversation_reference_id(jsonObject.optString(conversation_reference_id));
            model.setIs_read(jsonObject.optInt(is_read));
            model.setIs_delivered(jsonObject.optInt(is_delivered));
            model.setIs_downloaded(0);
            model.setIs_sync(1);

              String query="";

              if(model.getReference_id()!=null && !model.getReference_id().isEmpty())
              {
                  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = '"+model.getReference_id()+"' OR "+message_id+" = "+model.getMessage_id();
              }
              else
              {
                //  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+model.getMessage_id();
                  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+model.getConversation_reference_id()+"' AND "+message_id+" = "+model.getMessage_id();
              }


                cursor = db.rawQuery(query,null);
                try{

                    if(cursor!=null && cursor.getCount()>0)
                    {

                    if(cursor.moveToNext()){

                            ContentValues data = new ContentValues();
                            data.put(message_id, model.getMessage_id());
                            data.put(sender_id, model.getSender_id());
                            data.put(receiver_id, model.getReceiver_id());
                            data.put(message, model.getMessage());
                            data.put(is_group, model.getIs_group());
                            data.put(is_reply,model.getIs_reply());
                            data.put(is_sync,model.getIs_sync());
                            data.put(is_delivered,model.getIs_delivered());
                            data.put(is_received,model.getIs_received());
                          //  data.put(message_type, model.getMessage_type());
                            data.put(reference_id, model.getReference_id());
                            data.put(conversation_reference_id, model.getConversation_reference_id());
                            if(cursor.getInt(cursor.getColumnIndex(is_read))==1)
                            {
                                data.put(is_read, 1);
                            }
                            else
                            {
                                data.put(is_read, model.getIs_read());
                            }

                            data.put(attachment, model.getAttachment());
                            data.put(attachment_name, model.getAttachment_name());
                            data.put(attachment_extension, model.getAttachment_extension());
                           int result= db.update(TABLE_NAME,data,message_id+" = "+model.getMessage_id(),null);
                           if(result>0)
                           {
                               status=true;

                           }

                        }
                    }else{
                       status= addReceiveMessage(model);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return status;
    }




    public boolean checkMessageIdAndInsertMessage(ChatModel model){
        Cursor cursor = null;
        boolean status=false;
        boolean isDownloaded=false;
        try{

            if(model!=null )
            {

            String query="";
           // String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+message_id+" = "+model.getMessage_id();
            if(model.getReference_id()!=null && !model.getReference_id().isEmpty())
            {
                 query = "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = '"+model.getReference_id()+"' OR "+message_id+" = "+model.getMessage_id();
            }
            else
            {
                query = "SELECT * FROM "+TABLE_NAME+" WHERE "+message_id+" = "+model.getMessage_id();
            }

            cursor = db.rawQuery(query,null);
            try{

                if(cursor!=null && cursor.getCount()>0)
                {

                    if(cursor.moveToNext()){

                        ContentValues data = new ContentValues();
                        data.put(message_id, model.getMessage_id());
                        data.put(sender_id, model.getSender_id());
                        data.put(receiver_id, model.getReceiver_id());
                        data.put(message, model.getMessage());
                        data.put(is_group, model.getIs_group());
                        data.put(is_reply,model.getIs_reply());
                        if(cursor.getInt(cursor.getColumnIndex(is_sync))==1)
                        {
                            data.put(is_sync, 1);
                        }
                        else
                        {
                            data.put(is_sync, model.getIs_sync());
                        }

                     //   data.put(is_sync, cursor.getInt(cursor.getColumnIndex(is_sync)));
                        data.put(is_delivered,model.getIs_delivered());
                        data.put(is_received,model.getIs_received());
                        data.put(message_type, model.getMessage_type());
                        data.put(reference_id, cursor.getString(cursor.getColumnIndex(reference_id)));
                        if(cursor.getInt(cursor.getColumnIndex(is_read))==1)
                        {
                            data.put(is_read, 1);
                        }
                        else
                        {
                            data.put(is_read, model.getIs_read());
                        }

                        if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                        {
                            data.put(is_downloaded, 1);
                            isDownloaded=true;
                        }
                        else
                        {
                            data.put(is_downloaded, model.getIs_downloaded());
                            isDownloaded=false;
                        }

                        data.put(attachment, model.getAttachment());
                        data.put(attachment_name, model.getAttachment_name());
                        data.put(attachment_extension, model.getAttachment_extension());
                        //data.put(is_downloaded,model.getIs_downloaded());
                        int result= db.update(TABLE_NAME,data,message_id+" = "+model.getMessage_id(),null);
                        if(result>0)
                        {
                            status=true;

                        }

                    }
                }else{
                    status= addReceiveMessage(model);
                    isDownloaded=false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return isDownloaded;
    }




    public Boolean updateChat(JSONObject jsonObject){
        Boolean status = false;
        Cursor cursor = null;
        try{



                if(jsonObject.has(reference_id)){
                    String rid = jsonObject.getString(reference_id);
                        try{
                            ContentValues data = new ContentValues();
                            if(rid!=null && rid.trim().isEmpty())
                            {
                                if(jsonObject.optString(sender_id)!=null && jsonObject.optString(sender_id).equals(tmUserId) )
                                {
                                     insertSelfAccountMessage(jsonObject);
                                }
                            }
                            else
                            {


                                data.put(message_id,jsonObject.optInt(message_id));
                                data.put(sender_id,Integer.parseInt(jsonObject.optString(sender_id)));
                                data.put(receiver_id,Integer.parseInt(jsonObject.optString(receiver_id)));
                                data.put(message_type,jsonObject.optInt(message_type));
                                data.put(is_group,jsonObject.optInt(is_group));
                                data.put(is_reply,jsonObject.optInt(is_reply));
                                data.put(is_sync,1);
                                data.put(is_delivered,0);
                                data.put(is_received,0);
                                data.put(updated_at,Utilities.getCurrentDateTimeNew());
                                if(jsonObject.optString(created_at)!=null && !jsonObject.optString(created_at).trim().isEmpty())
                                {
                                    data.put(created_at,jsonObject.optString(created_at));
                                }

                                // int result = db.update(TABLE_NAME,data,reference_id+" = "+rid,null);
                                int result=db.update(TABLE_NAME, data, "reference_id = ?", new String[]{rid});

                            }

                        }catch (Exception e){

                            e.printStackTrace();

                    }
                }

            status = true;
        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return status;
    }

    private void insertSelfAccountMessage(JSONObject jsonObject){

          /* {"sender_id":"2","receiver_id":"739","message_type":0,"message_id":9565,"created_at":"2019-03-26 16:39:14","reference_id":"",
                    "is_group":0,"is_reply":0,"message":"mhjkhjlkj",
                    "conversation_reference_id":"375f6799c0bc5875a3"}*/
          /*    {"sender_id":"1190","receiver_id":2,"message_type":1,"message_id":15593,"created_at":"2019-05-28 11:42:26","reference_id":"","is_group":0,"is_reply":0,"attachment":"https:\/\/s3.amazonaws.com\/files.c2m\/user\/28052019114225\/IMG-20190522-WA0003.jpg.jpeg",
                "attachment_extension":"jpeg","attachment_name":"IMG-20190522-WA0003.jpg.jpeg",
                "conversation_reference_id":"696b87eacbc8b67d55"}*/


        ChatModel model=new ChatModel();
        model.setMessage_id(jsonObject.optInt(message_id));
        model.setSender_id(Integer.parseInt(jsonObject.optString(sender_id)));
        model.setReceiver_id(Integer.parseInt(jsonObject.optString(receiver_id)));
        model.setMessage(jsonObject.optString(message));
        model.setIs_group(jsonObject.optInt(is_group));
        model.setIs_received(0);
        model.setIs_sync(1);
        model.setIs_reply(jsonObject.optInt(is_reply));
        model.setAttachment(jsonObject.optString(attachment));
        model.setAttachment_name(jsonObject.optString(attachment_name));
        model.setAttachment_extension(jsonObject.optString(attachment_extension));
        model.setCaption("");
        model.setOriginal_message("");
        model.setIs_delivered(0);
        model.setMessage_type(jsonObject.optInt(message_type));
        model.setReference_id(jsonObject.optString(reference_id));
        model.setConversation_reference_id(jsonObject.optString(conversation_reference_id));
        model.setCreated_at(jsonObject.optString(created_at));





        addReceiveMessage(model);

    }




    public Boolean updateDeliveredMessage(JSONObject jsonObject){
        Boolean status = false;
        Cursor cursor = null;


        try{

                        try{
                            ContentValues data = new ContentValues();

                            int mid=jsonObject.optInt(message_id);

                            if(mid!=0)
                            {
                               // data.put(receiver_id,jsonObject.optInt(receiver_id));
                                data.put(is_delivered,jsonObject.optInt("is_delivered_to_all"));
                                data.put(updated_at,Utilities.getCurrentDateTimeNew());
                                int result = db.update(TABLE_NAME,data,message_id+" = "+mid,null);

                            }



                        }catch (Exception e){
                            e.printStackTrace();
                        }


            status = true;
        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return status;
    }


    public Boolean updateReadMessage(JSONObject jsonObject){
        Boolean status = false;
        Cursor cursor = null;
        Helper.getInstance().LogDetails("updateReadMessage","called"+jsonObject.toString());
        try{

            try{
                ContentValues data = new ContentValues();
                String tuid= Session.getTmUserId(context);
                int receiverId=0;
                if(tuid!=null && !tuid.trim().isEmpty())
                {
                    receiverId=Integer.parseInt(tuid);
                }

                int mid=jsonObject.optInt(message_id);

                if(mid!=0)
                {
                    //data.put(receiver_id,jsonObject.optInt(receiver_id));
                  //  data.put(is_read,jsonObject.optInt("is_read_by_all"));
                    data.put(is_read,1);
                    data.put(updated_at,Utilities.getCurrentDateTimeNew());
                    int result = db.update(TABLE_NAME,data,message_id+" = "+mid,null);
                   // int result = db.update(TABLE_NAME,data,message_id+" = "+mid+" AND "+receiver_id+" = "+receiverId,null);

                    if(result>0){
                        Helper.getInstance().LogDetails("updateReadMessage","true"+mid);
                    }
                    else
                    {
                        Helper.getInstance().LogDetails("updateReadMessage","false"+mid);
                    }



                }



            }catch (Exception e){
                e.printStackTrace();
                Helper.getInstance().LogDetails("updateReadMessage","exception"+e.getLocalizedMessage()+" "+e.getCause());
            }


            status = true;
        }catch (Exception e){
            status = false;
            e.printStackTrace();
            Helper.getInstance().LogDetails("updateReadMessage","exception end"+e.getLocalizedMessage()+" "+e.getCause());
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return status;
    }



    public Boolean updateAvailableStatus(JSONObject jsonObject){
        Boolean status = false;
        Cursor cursor = null;
        try{

            try{
                ContentValues data = new ContentValues();

                int receiverId=jsonObject.optInt("user_id");


                    data.put(is_available,1);
                    data.put(updated_at,Utilities.getCurrentDateTimeNew());
                    int result = db.update(TABLE_NAME,data,receiver_id+" = "+receiverId,null);





            }catch (Exception e){
                e.printStackTrace();
            }


            status = true;
        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return status;
    }



    public List<ChatModel> getChat(int receiverId, int senderId){
        //open();
        List<ChatModel> chats = new ArrayList<>();
        Cursor cursor = null;
        try{

            //String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+receiver_id+" = "+receiverId;
            //String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " +receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  ;

            String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")";
            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    ChatModel model = new ChatModel();

                    model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                    model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                    model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                    model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                    model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                    model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                    model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                    model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                    model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                    model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                    model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                    model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                    model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                    model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                    model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                    model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));



                    chats.add(model);

                }while(cursor.moveToNext());

            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  chats;
    }

    public List<ChatModel> getLocalMessages(int receiverId, int senderId,String conversationReferenceId){

        Helper.getInstance().LogDetails("getLatestChat","method called"+receiverId+" "+senderId+" "+conversationReferenceId);

        List<ChatModel> chats = new ArrayList<>();
        Cursor cursor = null;
        try{

           /* String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+" ORDER BY "+created_at+" DESC,"+id+" DESC limit 50";
*/
          /*  String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+
                    " AND "+conversation_reference_id+"= '"+conversationReferenceId+
                    "' ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";*/

            //email+" = '"+username+"'"
            String  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'"+
                    " AND "+message_id+" = 0"
                    + " ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";

            Helper.getInstance().LogDetails("getLocalMessages","method called query"+query);


            cursor = db.rawQuery(query,null);

            if(cursor.moveToFirst()){

                do{
                    ChatModel model = new ChatModel();

                    model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                    model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                    model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                    model.setAttachment_name(cursor.getString(cursor.getColumnIndex(attachment_name)));
                    model.setAttachment_extension(cursor.getString(cursor.getColumnIndex(attachment_extension)));
                    model.setAttachmentDevicePath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                    model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                    model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                    model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                    model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                    model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                    model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                    model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                    model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                    model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                    if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                    {
                        model.setAttachmentDownloaded(true);
                    }
                    else
                    {
                        model.setAttachmentDownloaded(false);
                    }
                    model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                    model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                    model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                    model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));


                    chats.add(model);
                    Helper.getInstance().LogDetails("getLatestChat","called"+model.getConversation_reference_id());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getLatestChat","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getLatestChat","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  chats;
    }

    public List<ChatModel> getLocalMessagesAll(int receiverId, int senderId,String conversationReferenceId){

        Helper.getInstance().LogDetails("getLocalMessages","method called"+receiverId+" "+senderId+" "+conversationReferenceId);

        List<ChatModel> chats = new ArrayList<>();
        Cursor cursor = null;
        try{

           /* String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+" ORDER BY "+created_at+" DESC,"+id+" DESC limit 50";
*/
          /*  String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+
                    " AND "+conversation_reference_id+"= '"+conversationReferenceId+
                    "' ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";*/

            //email+" = '"+username+"'"
            String  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'"
                    + " ORDER BY "+created_at+" DESC,"+id;

            Helper.getInstance().LogDetails("getLocalMessages","method called query"+query);


            cursor = db.rawQuery(query,null);

            if(cursor.moveToFirst()){

                do{
                    ChatModel model = new ChatModel();

                    model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                    model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                    model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                    model.setAttachment_name(cursor.getString(cursor.getColumnIndex(attachment_name)));
                    model.setAttachment_extension(cursor.getString(cursor.getColumnIndex(attachment_extension)));
                    model.setAttachmentDevicePath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                    model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                    model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                    model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                    model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                    model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                    model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                    model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                    model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                    model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                    if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                    {
                        model.setAttachmentDownloaded(true);
                    }
                    else
                    {
                        model.setAttachmentDownloaded(false);
                    }
                    model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                    model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                    model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                    model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));


                    chats.add(model);
                    Helper.getInstance().LogDetails("getLocalMessages","called"+model.getConversation_reference_id());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getLocalMessages","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getLocalMessages","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  chats;
    }


    public int getTheUnreadCountOfAll1() {
        Cursor cursor = null;
        List<ChatModel> chats = new ArrayList<>();
        try {
            String count_query = "select count(is_read) as count,* from "+TABLE_NAME+" where "+ " receiver_id = "+tmUserId+" and is_read <> 1 and message_id <> 0"+" GROUP BY id"
            +" ORDER BY "+created_at+" DESC,"+id;
            Helper.getInstance().LogDetails("getTheUnreadCountOfAll query ",count_query);
            cursor = selectByManualQuery(count_query);
            /*if (cursor != null && cursor.moveToFirst()) {

                int val = cursor.getInt(cursor.getColumnIndex("count"));
                int msgId=(cursor.getInt(cursor.getColumnIndex(message_id)));
                int userId=(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)));
                Helper.getInstance().LogDetails("getTheUnreadCountOfAll res ",val+" "+msgId+" "+userId);
                cursor.close();
                return val;
            }*/
            if(cursor!=null && cursor.moveToFirst()){

                do{
                    ChatModel model = new ChatModel();

                    model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                    model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                    model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                    model.setAttachment_name(cursor.getString(cursor.getColumnIndex(attachment_name)));
                    model.setAttachment_extension(cursor.getString(cursor.getColumnIndex(attachment_extension)));
                    model.setAttachmentDevicePath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                    model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                    model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                    model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                    model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                    model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                    model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                    model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                    model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                    model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                    if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                    {
                        model.setAttachmentDownloaded(true);
                    }
                    else
                    {
                        model.setAttachmentDownloaded(false);
                    }
                    model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                    model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                    model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                    model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));


                    chats.add(model);

                    int val = cursor.getInt(cursor.getColumnIndex("count"));
                    int msgId=(cursor.getInt(cursor.getColumnIndex(message_id)));
                    int userId=(cursor.getInt(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)));
                    Helper.getInstance().LogDetails("getTheUnreadCountOfAll res ",val+" "+msgId+" "+userId);

                    Helper.getInstance().LogDetails("getTheUnreadCountOfAll","called"+model.getConversation_reference_id());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getTheUnreadCountOfAll","no results");
            }
        }catch (Exception e){
            Helper.getInstance().LogDetails("getTheUnreadCountOfAll exc ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return 0;
    }


    public List<ChatModel> getLatestChat(int receiverId, int senderId,String conversationReferenceId){

        Helper.getInstance().LogDetails("getLatestChat","method called"+receiverId+" "+senderId+" "+conversationReferenceId);

        List<ChatModel> chats = new ArrayList<>();
        Cursor cursor = null;
        try{

           /* String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+" ORDER BY "+created_at+" DESC,"+id+" DESC limit 50";
*/
          /*  String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+
                    " AND "+conversation_reference_id+"= '"+conversationReferenceId+
                    "' ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";*/

          //email+" = '"+username+"'"
            String  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'"+
                    " ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";

            Helper.getInstance().LogDetails("getLatestChat","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{
                    ChatModel model = new ChatModel();

                    model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                    model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                    model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                    model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                    model.setAttachment_name(cursor.getString(cursor.getColumnIndex(attachment_name)));
                    model.setAttachment_extension(cursor.getString(cursor.getColumnIndex(attachment_extension)));
                    model.setAttachmentDevicePath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                    model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                    model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                    model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                    model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                    model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                    model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                    model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                    model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                    model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                    if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                    {
                        model.setAttachmentDownloaded(true);
                    }
                    else
                    {
                        model.setAttachmentDownloaded(false);
                    }
                    model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                    model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                    model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                    model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                    model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));


                    chats.add(model);
                    Helper.getInstance().LogDetails("getLatestChat","called"+model.getConversation_reference_id());
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getLatestChat","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getLatestChat","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }

        return  chats;
    }


    public ChatModel getLatestMessage(String conversationReferenceId){

        Helper.getInstance().LogDetails("getLatestMessage","method called"+" "+conversationReferenceId);

        ChatModel model = new ChatModel();
        if(conversationReferenceId!=null && !conversationReferenceId.trim().isEmpty())
        {
            Cursor cursor = null;
            try{

           /* String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+" ORDER BY "+created_at+" DESC,"+id+" DESC limit 50";
*/
          /*  String query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+
                    " AND "+conversation_reference_id+"= '"+conversationReferenceId+
                    "' ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";*/

                //email+" = '"+username+"'"
                String  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'"+
                        " ORDER BY "+created_at+" DESC,"+id+" DESC limit 1";

                Helper.getInstance().LogDetails("getLatestMessaget","method called query"+query);


                cursor = db.rawQuery(query,null);
                if(cursor.moveToFirst()){

                    do{


                        model.setMessage_id(cursor.getInt(cursor.getColumnIndex(message_id)));
                        model.setSender_id(cursor.getInt(cursor.getColumnIndex(sender_id)));
                        model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(receiver_id)));
                        model.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                        model.setAttachment(cursor.getString(cursor.getColumnIndex(attachment)));
                        model.setAttachment_name(cursor.getString(cursor.getColumnIndex(attachment_name)));
                        model.setAttachment_extension(cursor.getString(cursor.getColumnIndex(attachment_extension)));
                        model.setAttachmentDevicePath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                        model.setIs_group(cursor.getInt(cursor.getColumnIndex(is_group)));
                        model.setIs_reply(cursor.getInt(cursor.getColumnIndex(is_reply)));
                        model.setIs_sync(cursor.getInt(cursor.getColumnIndex(is_sync)));
                        model.setIs_read(cursor.getInt(cursor.getColumnIndex(is_read)));
                        model.setIs_delivered(cursor.getInt(cursor.getColumnIndex(is_delivered)));
                        model.setIs_received(cursor.getInt(cursor.getColumnIndex(is_received)));
                        model.setMessage_type(cursor.getInt(cursor.getColumnIndex(message_type)));
                        model.setOriginal_message(cursor.getString(cursor.getColumnIndex(original_message)));
                        model.setIs_downloaded(cursor.getInt(cursor.getColumnIndex(is_downloaded)));
                        if(cursor.getInt(cursor.getColumnIndex(is_downloaded))==1)
                        {
                            model.setAttachmentDownloaded(true);
                        }
                        else
                        {
                            model.setAttachmentDownloaded(false);
                        }
                        model.setCaption(cursor.getString(cursor.getColumnIndex(caption)));
                        model.setReference_id(cursor.getString(cursor.getColumnIndex(reference_id)));
                        model.setConversation_reference_id(cursor.getString(cursor.getColumnIndex(conversation_reference_id)));
                        model.setReceiver_id(cursor.getInt(cursor.getColumnIndex(original_message_id)));
                        model.setCreated_at(cursor.getString(cursor.getColumnIndex(created_at)));

                        Helper.getInstance().LogDetails("getLatestMessage","called"+model.getConversation_reference_id());

                        return  model;

                    }while(cursor.moveToNext());

                }else{
                    Helper.getInstance().LogDetails("getLatestMessage","no results");
                }
                cursor.close();
            }catch (Exception e){
                Helper.getInstance().LogDetails("getLatestMessage","exception "+e.getLocalizedMessage()+" "+e.getCause());
                e.printStackTrace();
            }finally {
                if(cursor!=null){ cursor.close();}
            }
        }
        return  model;
    }


    public JSONArray getMissingMessages(){

        Helper.getInstance().LogDetails("getMissingMessages ","conversationTable");
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;

        try{

            String query= "SELECT * FROM " +TABLE_NAME+ " ORDER BY "+message_id+" DESC,"+id+" DESC limit 100";


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){
                do {
                    try{
                        /*JSONObject jsonObject = new JSONObject();
                        jsonObject.put(message_id,cursor.getInt(cursor.getColumnIndex(message_id)));*/

                        Helper.getInstance().LogDetails("getMissingMessages ","chat get miising query message data"+cursor.getInt(cursor.getColumnIndex(message_id)));
                        jsonArray.put(cursor.getInt(cursor.getColumnIndex(message_id)));
                    }catch (Exception e){
                        e.printStackTrace();
                        Helper.getInstance().LogDetails("getMissingMessages ","conversationTable exception "+e.getLocalizedMessage() +" "+e.getCause());
                    }
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
            Helper.getInstance().LogDetails("getMissingMessages ","conversationTable exception "+e.getLocalizedMessage() +" "+e.getCause());
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return jsonArray;
    }

    public int getTheUnreadMessageAvailable(int senderId,int tmUserId,String conversationReferenceId) {
        Cursor cursor = null;
        Helper.getInstance().LogDetails("getTheUnreadMessageAvailable","called "+senderId+" "+tmUserId+" "+conversationReferenceId);
        try {
          //  String unread_count =  "Select count(message_id) as count from "+TABLE_NAME+" where (sender_id = " + senderId + " and receiver_id = " + tmUserId + " and is_read <> 1 ) and message_id <> 0  order by message_id desc";

          /*  String unread_count= "SELECT count(message_id) as count from " +TABLE_NAME+ " WHERE "+
                    "(" +is_read+ " = 0"  +" AND " +is_received+ " = 1"+  ")"+
                    " AND "+
                    "("+ receiver_id+ " = " +tmUserId+ " AND "+ sender_id+ " = " +senderId  +  ")";  */
          if(conversationReferenceId!=null && !conversationReferenceId.trim().isEmpty())
          {
             /* String unread_count= "SELECT count(message_id) as count from " +TABLE_NAME+ " WHERE "+
                      "(" +is_read+ " = 0"  +" AND " +is_received+ " = 1"+  ")"+
                      " AND "+
                      conversation_reference_id+"= '"+conversationReferenceId+
                      "' AND "+
                      "("+ receiver_id+ " = " +tmUserId+ " AND "+ sender_id+ " = " +senderId  +  ")";*/
              String unread_count= "SELECT count(message_id) as count from " +TABLE_NAME+ " WHERE "+
                      "(" +is_read+ " = 0"  +" AND " +sender_id+ " = "+senderId+  ")"+
                      " AND "+
                      conversation_reference_id+"= '"+conversationReferenceId+
                      "'";

              cursor = selectWithQuery(unread_count);
              if (cursor!=null && cursor.moveToFirst()){
                  return cursor.getInt(cursor.getColumnIndex("count"));
              }
          }

        }catch (Exception e){
            e.printStackTrace();

        }
        return 0;
    }

    public boolean updateUnreadMessageById(String senderId, String receiverId,String conversation_reference_id) {
        Cursor cursor = null;
        try {
            String update_query = "UPDATE "+TABLE_NAME+" SET is_read = 1 where sender_id = " + senderId + " and receiver_id = " + receiverId + " and is_sync = 1 and is_read <> 1";
            cursor = selectWithQuery(update_query);
            if (cursor!=null && cursor.moveToFirst()){
                cursor.close();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return false;
    }




    public int getTheUnreadCount(int receiver_id) {
        Cursor cursor = null;
        try {
            String count_query = "select count(is_read) as is_read from "+TABLE_NAME+" where sender_id ="+receiver_id+" and receiver_id = "+tmUserId+" and is_read <> 1 and message_id <> 0";
            cursor = selectByManualQuery(count_query);
            if (cursor != null && cursor.moveToFirst()) {

                int val = cursor.getInt(cursor.getColumnIndex("is_read"));
                cursor.close();
                return val;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return 0;
    }




    public boolean updateAwsAttachmentPath(String awsPath,String reference_id) {
        try {
            boolean status=false;
            Helper.getInstance().LogDetails("FileUpLoader","updateAwsAttachmentPath "+awsPath+" "+reference_id);
            // String where = DataBaseValues.ConversationTable.message_id + "=? ";
            String where =  DataBaseValues.ConversationTable.reference_id +"=?";
            String whereArgs[] = {reference_id};
            ContentValues values = new ContentValues();
            values.put(DataBaseValues.ConversationTable.attachment, awsPath);
            status= update(TABLE_NAME, values, where, whereArgs);
            Helper.getInstance().LogDetails("FileUpLoader","updateAwsAttachmentPath after update "+status);
            if(status){
                SyncData.syncMessage(context,reference_id);
            }

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().LogDetails("FileUpLoader","updateAwsAttachmentPath exception"+e.getCause()+" "+e.getLocalizedMessage());
        } finally {

        }

        return false;
    }

    public boolean updateAttachment(String devicePath, String msgId, String download_path,String conversationReferenceId) {
        try {
            Helper.getInstance().LogDetails("FileDownLoader","updateAttachment "+devicePath+" "+download_path+" "+msgId);
           // String where = DataBaseValues.ConversationTable.message_id + "=? ";
            String where = DataBaseValues.ConversationTable.message_id + "=? and " + DataBaseValues.ConversationTable.conversation_reference_id +"=?";
            String whereArgs[] = {msgId,conversationReferenceId};
            ContentValues values = new ContentValues();
            values.put(DataBaseValues.ConversationTable.attachment_device_path, devicePath);
            values.put(DataBaseValues.ConversationTable.is_downloaded, DataBaseValues.ACTIVE);

            return update(TABLE_NAME, values, where, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().LogDetails("FileDownLoader","updateAttachment exception"+e.getCause()+" "+e.getLocalizedMessage());
        } finally {

        }

        return false;
    }

    public void updateDownloadFileStatus(int msgId,int senderId, int messageReceiverId,String conversationReferenceId ,String file_path) {
        Cursor cursor = null;
        Helper.getInstance().LogDetails("FileDownLoader","updateDownloadFileStatus "+senderId+" "+messageReceiverId+" "+conversationReferenceId);
        try {
          /*  String update_query = "update " + TABLE_NAME +
                    " set is_downloaded = 1  where (sender_id = " + senderId +" AND "+
                    "conversation_reference_id = '"+conversationReferenceId+"'" ;

            cursor = selectWithQuery(update_query);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return;
            }*/

            try{
                ContentValues data = new ContentValues();



                data.put(is_downloaded,1);
                data.put(updated_at,Utilities.getCurrentDateTimeNew());
                String where="sender_id = " + senderId +" AND "+
                "conversation_reference_id = '"+conversationReferenceId+"'";
                int result = db.update(TABLE_NAME,data,where,null);


                Helper.getInstance().LogDetails("FileDownLoader ","update status "+result);


            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().LogDetails("FileDownLoader","updateDownloadFileStatus exception"+e.getCause()+" "+e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getTheDocumentPath(String conversationReferenceId,int messageId, boolean is_mine) {
        Helper.getInstance().LogDetails("getTheDocumentPath","called"+conversationReferenceId);
        String file_path = "";
        String query = "";


        try {
            if (!is_mine) {
                 query= "SELECT attachment_device_path from " +TABLE_NAME+ " WHERE "+
                         message_id+" = "+messageId+" AND "+
                        conversation_reference_id+"= '"+conversationReferenceId+ "'";
            } else {
                query= "SELECT attachment_device_path from " +TABLE_NAME+ " WHERE "+
                        message_id+" = "+messageId+" AND "+
                        conversation_reference_id+"= '"+conversationReferenceId+ "'";
            }
            Cursor cursor = selectWithQuery(query);
            if (cursor != null && cursor.getCount()>0 && cursor.moveToFirst()) {
                file_path = cursor.getString(cursor.getColumnIndex(attachment_device_path));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Helper.getInstance().LogDetails("getTheDocumentPath","file_path "+file_path);
        return file_path;
    }

    public String getTheDocPath(String id, boolean is_mine) {
        String file_path = "";
        String query = "";

        try {
            if (!is_mine) {
                query = "SELECT attachment from " + DataBaseValues.ConversationTable.TABLE_NAME + " WHERE id = " + id;
            } else {
                query = "SELECT attachment  from " + DataBaseValues.ConversationTable.TABLE_NAME + " WHERE id = " + id;
            }
            Cursor cursor = selectWithQuery(query);
            if (cursor != null && cursor.moveToFirst()) {
                file_path = cursor.getString(cursor.getColumnIndex(attachment_device_path));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file_path;
    }

    public List<GalleryModel> getMedia(String receiverId, String conversationReferenceId ,String senderId ) {

        Helper.getInstance().LogDetails("getMedia","called"+receiverId+"   "+conversationReferenceId+"   "+senderId);

        Cursor cursor = null;
        List<GalleryModel> models = new ArrayList<GalleryModel>();
        try {
            String query;



            //  query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'";
              query = "SELECT * FROM "+TABLE_NAME+" WHERE "+conversation_reference_id+" = '"+conversationReferenceId+"'"+
                      " AND "+message_type+" = '"+Values.MessageType.MESSAGE_TYPE_ATTACHMENT+"'"+

                      " ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";

        //    " AND "+message_type+" = '"+Values.MessageType.MESSAGE_TYPE_ATTACHMENT+"'"+

             /*query= "SELECT * FROM " +TABLE_NAME+ " WHERE " + "("+
                    receiver_id+ " = " +receiverId+ " AND "+ sender_id+ " = " +senderId  +  ")"+
                    " OR " +"("+
                    receiver_id+ " = " +senderId+ " AND "+ sender_id+ " = " +receiverId +  ")"+
                    " AND "+conversation_reference_id+"= '"+conversationReferenceId+
                    "' ORDER BY "+created_at+" DESC,"+id+" DESC limit 500";*/

             Helper.getInstance().LogDetails("getMedia==",query+" ");

            cursor = selectWithQuery(query);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    GalleryModel model = new GalleryModel();
                    int attachment_type = 0;
                    if (cursor.getString(cursor.getColumnIndex(attachment_device_path)) != null && !cursor.getString(cursor.getColumnIndex(attachment_device_path)).trim().isEmpty() && !cursor.getString(cursor.getColumnIndex(attachment_device_path)).equals("null")) {
                        File file = new File(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                        if (file.exists() && file.length() > 0) {

                            String path=cursor.getString(cursor.getColumnIndex("attachment"));

                            if(path!=null && !path.trim().isEmpty())
                            {
                                attachment_type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(path);
                            }
                            else
                            {
                                 path=cursor.getString(cursor.getColumnIndex("attachment_device_path"));
                                if(path!=null && !path.trim().isEmpty())
                                {
                                    attachment_type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(path);
                                }
                            }


                                if (attachment_type == Values.Gallery.GALLERY_IMAGE ) {
                                    model.setReceiverid(cursor.getString(cursor.getColumnIndex(receiver_id)));
                                    model.setSenderid(cursor.getString(cursor.getColumnIndex(sender_id)));
                                    model.setCreatedAt(Helper.getInstance().indiaTimeTolocalTime(cursor.getString(cursor.getColumnIndex(created_at))));
                                    model.setId(cursor.getString(cursor.getColumnIndex(id)));
                                    model.setEntity(cursor.getInt(cursor.getColumnIndex(is_group)));
                                    model.setType(attachment_type);
                                    model.setMessageID(cursor.getString(cursor.getColumnIndex(message_id)));
                                    model.setPath(cursor.getString(cursor.getColumnIndex(attachment_device_path)));
                                    model.setImageUrl(cursor.getString(cursor.getColumnIndex(attachment)));
                                    models.add(FileFormatHelper.getInstance().getFileData(model));
                                }
                            }


                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return models;
    }

    public int getLastMessageId(){
        int lastMessageId=0;

        Cursor cursor = null;
        try{


            String  query = "SELECT * FROM "+TABLE_NAME+ " ORDER BY "+created_at+" DESC,"+id+" DESC limit 1";

            Helper.getInstance().LogDetails("getLastMessageId","method called query"+query);


            cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){

                do{

                    lastMessageId=cursor.getInt(cursor.getColumnIndex(message_id));
                    Helper.getInstance().LogDetails("getLastMessageId","called"+lastMessageId);
                }while(cursor.moveToNext());

            }else{
                Helper.getInstance().LogDetails("getLastMessageId","no results");
            }
            cursor.close();
        }catch (Exception e){
            Helper.getInstance().LogDetails("getLastMessageId","exception "+e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }finally {
            if(cursor!=null){ cursor.close();}
        }
        return lastMessageId;

    }
    public JSONObject getTheUnreadCountOfAll(Context context) {
        Cursor cursor = null;
        JSONObject userObj = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            //2
            //3
            //SELECT a1, a2, b1, b2
            //FROM A
            //INNER JOIN B on B.f = A.f;
            String count_query="select count(c.is_read) as count,a.user_name as user_name,a.name as name,a.guestName as guestName,a.site_id as site_id,s.site_name, c.created_at as message_time ,* from conversationTable as `c`  INNER JOIN ActiveChatsTable as `a` ON a.tm_visitor_id = c.sender_id  INNER JOIN SitesTable as `s` ON s.site_id = a.site_id where c.receiver_id = "+tmUserId+ " and c.is_read <> 1 and c.message_id <> 0 GROUP BY c.id ORDER BY c.created_at DESC";



        //    String count_query="select count(c.is_read) as count,a.user_name as user_name,a.name as name,a.guestName as guestName,a.site_id as site_id,* from conversationTable as `c`  INNER JOIN ActiveChatsTable as `a` ON a.tm_visitor_id = c.sender_id where c.receiver_id = "+tmUserId+ " and c.is_read <> 1 and c.message_id <> 0 GROUP BY c.id ORDER BY c.created_at DESC";



           /* String count_query = "select count(c.is_read) as count,* from "+TABLE_NAME+" as `c`"+" where "+ " c.receiver_id = "+tmUserId+" and c.is_read <> 1 and c.message_id <> 0"+" GROUP BY c.id" +" ORDER BY c.created_at DESC, c.id"+
                    " INNER JOIN ActiveChatsTable as  `a` ON a.tm_visitor_id = c.sender_id";*/


            //  String count_query = "select count(is_read) as count,* from "+TABLE_NAME+" where "+ " receiver_id = "+tmUserId+" and is_read <> 1 and message_id <> 0"+" GROUP BY id" +" ORDER BY "+created_at+" DESC,"+id;
            Helper.getInstance().LogDetails("getTheUnreadCountOfAll query ",count_query);
            cursor = selectByManualQuery(count_query);
            if (cursor!=null && cursor.moveToFirst()){
                do {

                    int val = cursor.getInt(cursor.getColumnIndex("count"));
                    int msgId=(cursor.getInt(cursor.getColumnIndex(message_id)));
                    int userId=(cursor.getInt(cursor.getColumnIndex(sender_id)));
                    String name=(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.name)));
                    String user_name=(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.user_name)));
                    String guestName=(cursor.getString(cursor.getColumnIndex(DataBaseValues.ActiveChatsTable.guestName)));

                    Helper.getInstance().LogDetails("getTheUnreadCountOfAll res ",val+" "+msgId+" "+userId+" name "+name+" user_name  "+user_name+" guestName "+guestName);

                    // String userName=activeChatsTable.getUserName(userId);

                    JSONObject data = new JSONObject();
                    data.put("user_id",cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)));
                    // data.put("name",userName);
                     data.put("name",Helper.getInstance().capitalize(cursor.getString(cursor.getColumnIndex("guestName"))));
                     data.put("site_id",cursor.getString(cursor.getColumnIndex("site_id")));
                     data.put("conversation_reference_id",cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.conversation_reference_id)));

                    data.put("created_at",cursor.getString(cursor.getColumnIndex("message_time")));
                    // data.put("workspace_id",cursor.getString(cursor.getColumnIndex("workspace_id")));
                      data.put("site_name",Helper.getInstance().capitalize(cursor.getString(cursor.getColumnIndex("site_name"))));
                    data.put("is_group",0);

                    String message =cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.message));
                    if (cursor.getInt(cursor.getColumnIndex(DataBaseValues.ConversationTable.message_type)) == Values.MessageType.MESSAGE_TYPE_TEXT) {
                        message = cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.message));

                    } else if (cursor.getInt(cursor.getColumnIndex(DataBaseValues.ConversationTable.message_type)) == Values.MessageType.MESSAGE_TYPE_ATTACHMENT) {
                            int type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(cursor.getString(cursor.getColumnIndex("attachment")));
                            if (type == Values.Gallery.GALLERY_IMAGE) {
                                message = "\uD83D\uDCF8 image";
                            } else if (type == Values.Gallery.GALLERY_VIDEO) {
                                message = "\uD83D\uDCFD️ video";
                            } else if (type == Values.Gallery.GALLERY_AUDIO) {
                                message = "\uD83C\uDFA7 audio";
                            } else {
                                message = "\uD83D\uDCCE attachment";
                            }
                        }


                      else  if (cursor.getInt(cursor.getColumnIndex("message_type")) == Values.MessageType.MESSAGE_TYPE_REQUEST) {
                        message = cursor.getString(cursor.getColumnIndex("message"));

                        JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject!=null){
                            message=Helper.getInstance().capitalize(jsonObject.getString("message"));
                        }



                    }
                    data.put("message",message);
                    if(userObj.has(cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)))){
                        JSONArray tmpArr = userObj.getJSONArray(cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)));
                        tmpArr.put(data);
                        userObj.put(cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)),tmpArr);
                    }else{
                        JSONArray tmpArr = new JSONArray();
                        tmpArr.put(data);
                        userObj.put(cursor.getString(cursor.getColumnIndex(DataBaseValues.ConversationTable.sender_id)),tmpArr);
                    }




                }while (cursor.moveToNext());
                cursor.close();
            }
            object.put("user",userObj);

            Helper.getInstance().LogDetails("getTheUnreadCountOfAll object  ",object.toString());

        }catch (Exception e){
            Helper.getInstance().LogDetails("getTheUnreadCountOfAll exc ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return object;
    }
    public JSONObject getTheNotificationData(String workspace_userids,String workspace_id) {
        JSONObject object = new JSONObject();
        JSONObject userObj = new JSONObject();

        Cursor cursor = null;
        try {

            String notification_query = "";


            notification_query = "SELECT (select count(message_id) as count from messenger WHERE (sender_id = m.sender_id and receiver_id = m.receiver_id and sender_id NOT IN (" + workspace_userids + ") and receiver_id IN (" + workspace_userids + ") and is_read <> 1 and is_group <> 1 and message_id <> 0 and message_type < 12 and status >0) or (sender_id NOT IN (" + workspace_userids + ") and receiver_id = m.receiver_id and is_read <> 1 and is_group = 1 and message_id <> 0 and message_type <12 and status >0)) as unread_messages, CASE WHEN (m.is_group <> 1) THEN 1 ELSE 2 end as entity_type,`m`.`workspace_id` as workspace_id, `m`.`message_hash` as `message_hash`,`m`.`status` as `status`,`m`.`attachment` as `attachment`,`m`.`message_id` as `id`,`m`.`message_type` as `message_type`, `m`.`message` as `message`, `m`.`created_at` as `created_at`,`m`.`is_read` as `is_read`, `m`.`sender_id` as `sender_id`, `m`.`is_group` as `is_group`, `m`.`receiver_id` as `receiver_id`, `u`.`name` as `sender_name`, `u`.`user_avatar` as `sender_profile_pic`,w.workspace_name as company_name, CASE WHEN (m.is_group = 0) THEN `u1`.`name` ELSE mg.group_name end as `receiver_name`, CASE WHEN (m.is_group = 1) THEN `u1`.`user_avatar` ELSE mg.group_avatar end as `receiver_profile_pic` FROM `messenger` as `m` INNER JOIN workspace as w ON m.workspace_id = w.workspace_id  LEFT JOIN `user` as `u` ON m.sender_id = u.user_id AND m.workspace_id = u.workspace_id LEFT JOIN `user` as `u1` ON m.receiver_id = u1.user_id AND m.workspace_id = u1.workspace_id AND m.is_group <> 0 LEFT JOIN messenger_group as mg ON m.receiver_id = mg.group_id AND m.is_group = 1 WHERE `m`.`id` IN (SELECT `cm`.`id` as `id` FROM `messenger` as `cm` INNER JOIN (SELECT m.id as `id`,CASE WHEN (sender_id < receiver_id AND is_group <> 1) THEN sender_id || '-' || receiver_id || '-' || '-user' WHEN (receiver_id < sender_id AND is_group <> 1) THEN receiver_id || '-' || sender_id || '-' || '-user' ELSE sender_id || '-' || receiver_id || '-' || '-group' END as `concat_id`,MAX(created_at) as `created_at` FROM `messenger` as `m` WHERE (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`receiver_id` IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` = 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) GROUP BY `id` ORDER BY `created_at` DESC) as `cmi` ON `cmi`.`id` = `cm`.`id`) GROUP BY m.id HAVING unread_messages > 0 ORDER BY `created_at` DESC";
            //notification_query = "SELECT (select count(message_id) as count from messenger WHERE (sender_id = m.sender_id and receiver_id = m.receiver_id and workspace_id = '"+workspace_id+"'"+"and sender_id NOT IN (" + workspace_userids + ") and receiver_id IN (" + workspace_userids + ") and is_read <> 1 and is_group <> 1 and message_id <> 0 and message_type < 12 and status >0) or (sender_id NOT IN (" + workspace_userids + ") and receiver_id = m.receiver_id and is_read <> 1 and is_group = 1 and message_id <> 0 and message_type <12 and status >0)) as unread_messages, CASE WHEN (m.is_group <> 1) THEN 1 ELSE 2 end as entity_type,`m`.`workspace_id` as workspace_id, `m`.`message_hash` as `message_hash`,`m`.`status` as `status`,`m`.`attachment` as `attachment`,`m`.`message_id` as `id`,`m`.`message_type` as `message_type`, `m`.`message` as `message`, `m`.`created_at` as `created_at`,`m`.`is_read` as `is_read`, `m`.`sender_id` as `sender_id`, `m`.`is_group` as `is_group`, `m`.`receiver_id` as `receiver_id`, `u`.`name` as `sender_name`, `u`.`user_avatar` as `sender_profile_pic`,w.workspace_name as company_name, CASE WHEN (m.is_group = 0) THEN `u1`.`name` ELSE mg.group_name end as `receiver_name`, CASE WHEN (m.is_group = 1) THEN `u1`.`user_avatar` ELSE mg.group_avatar end as `receiver_profile_pic` FROM `messenger` as `m` INNER JOIN workspace as w ON m.workspace_id = w.workspace_id  LEFT JOIN `user` as `u` ON m.sender_id = u.user_id AND m.workspace_id = u.workspace_id LEFT JOIN `user` as `u1` ON m.receiver_id = u1.user_id AND m.workspace_id = u1.workspace_id AND m.is_group <> 0 LEFT JOIN messenger_group as mg ON m.receiver_id = mg.group_id AND m.is_group = 1 WHERE `m`.`id` IN (SELECT `cm`.`id` as `id` FROM `messenger` as `cm` INNER JOIN (SELECT m.id as `id`,CASE WHEN (sender_id < receiver_id AND is_group <> 1) THEN sender_id || '-' || receiver_id || '-' || '-user' WHEN (receiver_id < sender_id AND is_group <> 1) THEN receiver_id || '-' || sender_id || '-' || '-user' ELSE sender_id || '-' || receiver_id || '-' || '-group' END as `concat_id`,MAX(created_at) as `created_at` FROM `messenger` as `m` WHERE (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`receiver_id` IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` = 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) GROUP BY `id` ORDER BY `created_at` DESC) as `cmi` ON `cmi`.`id` = `cm`.`id`) GROUP BY m.id HAVING unread_messages > 0 ORDER BY `created_at` DESC";
            //notification_query = "SELECT (select count(message_id) as count from messenger WHERE
            // (sender_id = m.sender_id and receiver_id = m.receiver_id and sender_id NOT IN (" + workspace_userids + ") and receiver_id IN (" + workspace_userids + ") and is_read <> 1 and is_group <> 1 and message_id <> 0 and message_type < 12 and status >0) or (sender_id NOT IN (" + workspace_userids + ") and receiver_id = m.receiver_id and is_read <> 1 and is_group = 1 and message_id <> 0 and message_type <12 and status >0)) as unread_messages, CASE WHEN (m.is_group <> 1) THEN 1 ELSE 2 end as entity_type,`m`.`workspace_id` as workspace_id, `m`.`message_hash` as `message_hash`,`m`.`status` as `status`,`m`.`attachment` as `attachment`,`m`.`message_id` as `id`,`m`.`message_type` as `message_type`, `m`.`message` as `message`, `m`.`created_at` as `created_at`,`m`.`is_read` as `is_read`, `m`.`sender_id` as `sender_id`, `m`.`is_group` as `is_group`, `m`.`receiver_id` as `receiver_id`, `u`.`name` as `sender_name`, `u`.`user_avatar` as `sender_profile_pic`,w.workspace_name as company_name, CASE WHEN (m.is_group = 0) THEN `u1`.`name` ELSE mg.group_name end as `receiver_name`, CASE WHEN (m.is_group = 1) THEN `u1`.`user_avatar` ELSE mg.group_avatar end as `receiver_profile_pic` FROM `messenger` as `m` INNER JOIN workspace as w ON m.workspace_id = w.workspace_id  LEFT JOIN `user` as `u` ON m.sender_id = u.user_id AND m.workspace_id = u.workspace_id LEFT JOIN `user` as `u1` ON m.receiver_id = u1.user_id AND m.workspace_id = u1.workspace_id AND m.is_group <> 0 LEFT JOIN messenger_group as mg ON m.receiver_id = mg.group_id AND m.is_group = 1 WHERE `m`.`id` IN (SELECT `cm`.`id` as `id` FROM `messenger` as `cm` INNER JOIN (SELECT m.id as `id`,CASE WHEN (sender_id < receiver_id AND is_group <> 1) THEN sender_id || '-' || receiver_id || '-' || '-user' WHEN (receiver_id < sender_id AND is_group <> 1) THEN receiver_id || '-' || sender_id || '-' || '-user' ELSE sender_id || '-' || receiver_id || '-' || '-group' END as `concat_id`,MAX(created_at) as `created_at` FROM `messenger` as `m` WHERE (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`receiver_id` IN (" + workspace_userids + ") and `m`.`is_group` <> 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) OR (`m`.`sender_id` NOT IN (" + workspace_userids + ") and `m`.`is_group` = 1 and `m`.`is_read` <> 1 and `m`.`message_id` <> 0 and `m`.`message_type` < 12 and `m`.`status` >0) GROUP BY `id` ORDER BY `created_at` DESC) as `cmi` ON `cmi`.`id` = `cm`.`id`) GROUP BY m.id HAVING unread_messages > 0 ORDER BY `created_at` DESC";

            cursor = selectWithQuery(notification_query);
            if (cursor!=null && cursor.moveToFirst()){
                do {


                        JSONObject data = new JSONObject();
                        data.put("user_id",cursor.getString(cursor.getColumnIndex("sender_id")));
                        data.put("name",cursor.getString(cursor.getColumnIndex("sender_name")));
                        data.put("entity_id",cursor.getString(cursor.getColumnIndex("sender_id")));
                        data.put("created_at",cursor.getString(cursor.getColumnIndex("created_at")));
                        data.put("workspace_id",cursor.getString(cursor.getColumnIndex("workspace_id")));
                        data.put("company_name",cursor.getString(cursor.getColumnIndex("company_name")));
                        data.put("is_group",0);

                        String message =cursor.getString(cursor.getColumnIndex("message"));
                        if (cursor.getInt(cursor.getColumnIndex("message_type")) == Values.MessageType.MESSAGE_TYPE_TEXT) {
                            message = cursor.getString(cursor.getColumnIndex("message"));

                        }
                        else if (cursor.getInt(cursor.getColumnIndex("message_type")) == Values.MessageType.MESSAGE_TYPE_ATTACHMENT) {
                                int type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(cursor.getString(cursor.getColumnIndex("attachment")));
                                if (type == Values.Gallery.GALLERY_IMAGE) {
                                    message = "\uD83D\uDCF8 image";
                                } else if (type == Values.Gallery.GALLERY_VIDEO) {
                                    message = "\uD83D\uDCFD️ video";
                                } else if (type == Values.Gallery.GALLERY_AUDIO) {
                                    message = "\uD83C\uDFA7 audio";
                                } else {
                                    message = "\uD83D\uDCCE attachment";
                                }
                            }
                       else  if (cursor.getInt(cursor.getColumnIndex("message_type")) == Values.MessageType.MESSAGE_TYPE_REQUEST) {
                        message = cursor.getString(cursor.getColumnIndex("message"));

                            JSONObject jsonObject=new JSONObject(message);
                            if(jsonObject!=null){
                                message=Helper.getInstance().capitalize(jsonObject.getString("message"));
                            }



                      }


                        data.put("message",message);
                        if(userObj.has(cursor.getString(cursor.getColumnIndex("sender_id")))){
                            JSONArray tmpArr = userObj.getJSONArray(cursor.getString(cursor.getColumnIndex("sender_id")));
                            tmpArr.put(data);
                            userObj.put(cursor.getString(cursor.getColumnIndex("sender_id")),tmpArr);
                        }else{
                            JSONArray tmpArr = new JSONArray();
                            tmpArr.put(data);
                            userObj.put(cursor.getString(cursor.getColumnIndex("sender_id")),tmpArr);
                        }




                }while (cursor.moveToNext());
                cursor.close();
            }
            object.put("user",userObj);

        }catch (Exception e){
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
