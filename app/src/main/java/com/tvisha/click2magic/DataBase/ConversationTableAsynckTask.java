package com.tvisha.click2magic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.socket.SocketConstants;
import com.tvisha.click2magic.socket.SocketIo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class ConversationTableAsynckTask extends AsyncTask<Object, String, Boolean> {
    DataBaseContext dbContext;
    Context context;
    protected SQLiteDatabase db;

    public static final String TABLE_NAME = DataBaseValues.ConversationTable.TABLE_NAME;

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
    final public static String attachment="attachment";
    final public static String attachment_extension="attachment_extension";
    final public static String attachment_name="attachment_name";
    final public static String attachment_device_path="attachment_device_path";
    final public static String conversation_reference_id="conversation_reference_id";

    final public static String original_message_id="original_message_id";
    final public static String is_read="is_read";
    final public static String is_delivered="is_delivered";
    final public static String is_downloaded="is_downloaded";
    final public static String is_available="is_available";
    final public static String original_message="original_message";
    final public static String caption="caption";
    final public static String created_at = "created_at";
    final public static String updated_at = "updated_at";




    @Override
    protected void onPostExecute(Boolean result) {
        if(HandlerHolder.mainActivityUiHandler!=null)
        {
            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
        }
        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
        }


        Helper.getInstance().LogDetails("ConversationTableAsynckTask onPostExecute","called");



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
                    db.execSQL(DataBaseValues.ConversationTable.CREATE_CHAT_TABLE);
                }
            }

        }


        JSONArray jsonArray =(JSONArray) params[1];
        for(int k = 0;k < jsonArray.length();k++){
            JSONObject jsonObject=jsonArray.optJSONObject(k);
            if(jsonObject!=null ){
                    Helper.getInstance().LogDetails("ConversationTableAsynckTask addMessage",jsonObject.toString());
                    if(context!=null)
                    {
                        addMessage(context,jsonObject);
                    }


            }

        }

        return status;
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
    public  void addMessage(final Context context, final JSONObject jsonObject) {

        Helper.getInstance().LogDetails("ConversationTableAsynckTask addMessage","called"+jsonObject.toString());

        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {
                        boolean status=checkMessageIdAndInsert(jsonObject);
                        Helper.getInstance().LogDetails("ConversationTableAsynckTask","status "+status);
                        if(status){
                            syncReceivedMessage(context);
                        }

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void syncReceivedMessage(final Context context){
        Helper.getInstance().LogDetails("ConversationTableAsynckTask","called");
        try{
            new Runnable(){
                @Override
                public void run() {

                    SocketIo socketIo = SocketIo.getInstance();

                    boolean isSelf= Session.getIsSelf(context);


                       ConversationTable table = new ConversationTable(context);
                        JSONArray data = table.getRecivedChat();
                      if(isSelf) {
                        if(data!=null && data.length()>0)
                        {


                            if(socketIo.isSocketConnected()){
                                Helper.getInstance().LogDetails("syncReceivedMessage","socket  connected");
                                Socket socket = socketIo.getSocket();
                                if(data.length() > 0){
                                    for (int i=0;i<data.length();i++)
                                    {
                                        try {

                                            socket.emit(SocketConstants.EVENT_MESSAGE_DELIVERED,data.get(i));
                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                    }

                                }

                            }
                            else
                            {
                                Helper.getInstance().LogDetails("syncReceivedMessage","socket not connected");
                            }
                        }
                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkMessageIdAndInsert(JSONObject jsonObject){

        Helper.getInstance().LogDetails("ConversationTableAsynckTask checkMessageIdAndInsert","called"+jsonObject.toString());
        Cursor cursor = null;
        boolean status=false;
        try{

            //String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+message_id+" = "+model.getMessage_id();
            //  String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+reference_id+" = "+model.getReference_id()+" AND "+message_id+" = "+model.getMessage_id();
/*        {"sender_id":"566",
                "receiver_id":2,
                "message_type":0,
                "message_id":7700,
                "created_at":"2019-03-21 11:06:05",
                "reference_id":"",
                "is_group":0,
                "is_reply":0,"message":"jk;kl;",
                "conversation_reference_id":"16a78f5ce939356969"
                "attachment":"","caption":"",}*/

            ChatModel model=new ChatModel();

            model.setSender_id(jsonObject.optInt(sender_id));
            model.setReceiver_id(jsonObject.optInt(receiver_id));
            model.setMessage_type(jsonObject.optInt(message_type));
            model.setReference_id(jsonObject.optString(reference_id));
            model.setIs_reply(jsonObject.optInt(is_reply));
            model.setMessage(jsonObject.optString(message));
            model.setCreated_at(jsonObject.optString(created_at));
            model.setMessage_id(jsonObject.optInt(message_id));
            model.setConversation_reference_id(jsonObject.optString(conversation_reference_id));
            model.setAttachment(jsonObject.optString(attachment));
            model.setAttachment_name(jsonObject.optString(attachment_name));
            model.setAttachment_extension(jsonObject.optString(attachment_extension));
            model.setIs_read(jsonObject.optInt(is_read));
            model.setIs_delivered(jsonObject.optInt(is_delivered));
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
                        if(cursor.getInt(cursor.getColumnIndex(is_read))==1)
                        {
                            data.put(is_read, 1);
                        }
                        else
                        {
                            data.put(is_read, model.getIs_read());
                        }
                      //  data.put(is_read, model.getIs_read());
                        data.put(is_delivered,model.getIs_delivered());
                        data.put(message_type, model.getMessage_type());
                        data.put(reference_id, model.getReference_id());
                        data.put(conversation_reference_id, model.getConversation_reference_id());
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
                    Helper.getInstance().LogDetails("ConversationTableAsynckTask addReceiveMessage","status "+status);

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


    public boolean addReceiveMessage(ChatModel model) {

        Helper.getInstance().LogDetails("ConversationTableAsynckTask SOCKET_RECEIVE_MESSAGE addReceiveMessage","called"+model.getConversation_reference_id());

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

        data.put(message_type, model.getMessage_type());
        data.put(reference_id, model.getReference_id());
        data.put(conversation_reference_id, model.getConversation_reference_id());
        data.put(created_at, model.getCreated_at());
        data.put(is_read, model.getIs_read());
        data.put(attachment, model.getAttachment());
        data.put(attachment_name, model.getAttachment_name());
        data.put(attachment_extension, model.getAttachment_extension());
        data.put(updated_at, Utilities.getCurrentDateTimeNew());

        status = db.insert(TABLE_NAME, null, data);
        if (status > 0) {

            return true;
        } else {

            return false;
        }
    }


}
