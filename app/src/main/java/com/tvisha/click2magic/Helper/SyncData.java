package com.tvisha.click2magic.Helper;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tvisha.click2magic.DataBase.ActiveAgentsTableAsynckTask;
import com.tvisha.click2magic.DataBase.ActiveChatsTable;
import com.tvisha.click2magic.DataBase.ActiveChatsTableAsynckTask;
import com.tvisha.click2magic.DataBase.AgentsTable;
import com.tvisha.click2magic.DataBase.ArchiveChatsTableAsynckTask;
import com.tvisha.click2magic.DataBase.CategoriesTableAsynckTask;
import com.tvisha.click2magic.DataBase.ChatModel;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.ConversationTableAsynckTask;
import com.tvisha.click2magic.DataBase.SiteAssetsTable;
import com.tvisha.click2magic.DataBase.SiteAssetsTableAsynckTask;
import com.tvisha.click2magic.DataBase.SitesTableAsynckTask;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.ActiveAgentsApi;
import com.tvisha.click2magic.api.post.ArchievsApi;
import com.tvisha.click2magic.api.post.ArchievsResponse;
import com.tvisha.click2magic.api.post.SiteAgentsData;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.AllAgentsApi;
import com.tvisha.click2magic.api.post.model.CannedResponse;
import com.tvisha.click2magic.api.post.model.CategoriesApi;
import com.tvisha.click2magic.api.post.model.Category;
import com.tvisha.click2magic.api.post.model.Collateral;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.api.post.model.Link;
import com.tvisha.click2magic.api.post.model.SiteAssetData;
import com.tvisha.click2magic.api.post.model.SiteAssetsResponse;
import com.tvisha.click2magic.api.post.model.SitesResponse;
import com.tvisha.click2magic.chatApi.ApiClient;
import com.tvisha.click2magic.chatApi.ApiInterface;
import com.tvisha.click2magic.constants.ApiEndPoint;

import com.tvisha.click2magic.model.GetMessagesResponse;
import com.tvisha.click2magic.model.Message;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;
import com.tvisha.click2magic.socket.SocketIo;
import com.tvisha.click2magic.ui.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import io.socket.client.Ack;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HEAD;


public class SyncData {
    static String AWS_KEY = "", AWS_SECRET_KEY = "", AWS_BUCKET = "", AWS_REGION = "", AWS_BASE_URL = "",s3Url="";
    public static void syncChat(final Context context){
        try{
            new Runnable(){
                @Override
                public void run() {
                    if (Helper.getInstance().isAppForground(context)) {
                        SocketIo socketIo = SocketIo.getInstance();
                        if(socketIo!=null && socketIo.isSocketConnected() && socketIo.isAccessToken()){
                            ConversationTable table = new ConversationTable(context);
                            JSONArray data = table.getNonSyncChat();
                        if(data!=null && data.length()>0)
                        {
                                Socket socket = socketIo.getSocket();
                                if(data.length() > 0){
                                    socket.emit(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES,data);
                                }

                            }
                        }
                    }
                    else
                    {

                        //Helper.getInstance().LogDetails("syncReceivedMessage","not connected");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            try {
                                if(BackgroundServiceForOreo.tmSocket !=null && BackgroundServiceForOreo.tmSocket.connected() && BackgroundServiceForOreo.isAccessTokenUpdated)
                                {
                                    ConversationTable table = new ConversationTable(context);
                                    JSONArray data = table.getNonSyncChat();
                                    if(data!=null && data.length()>0)
                                    {

                                        if(data.length() > 0){
                                            BackgroundServiceForOreo.tmSocket.emit(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES,data);
                                        }

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {


                            if(SocketService.tmSocket !=null && SocketService.tmSocket.connected() && SocketService.isAccessTokenUpdated)
                            {
                                ConversationTable table = new ConversationTable(context);
                                JSONArray data = table.getNonSyncChat();
                                if(data!=null && data.length()>0)
                                {

                                    if(data.length() > 0){
                                        SocketService.tmSocket.emit(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES,data);
                                    }

                                }
                            }

                        }
                    }

                   // getMissingMessages(context);

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void uploadAttachment(final Context context) {
        Helper.getInstance().LogDetails("getUploadedAttachment uploadAttachment","called");
        ConversationTable table = new ConversationTable(context);
        JSONArray data = table.getUploadedAttachment();

        if (data != null && data.length() > 0) {

            for (int i = 0; i < data.length(); i++) {

                try {
                    JSONObject jsonObject = data.getJSONObject(i);
                    int messageType = jsonObject.getInt("message_type");
                    final String referenceId=jsonObject.getString("reference_id");
                    final String devicePath=jsonObject.getString("attachment_device_path");
                    final String conversation_reference_id=jsonObject.getString("conversation_reference_id");
                    if (referenceId != null && !referenceId.trim().isEmpty()) {


                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    (new FileUpLoader(HandlerHolder.attachmentUploadHandler, context, referenceId , "", conversation_reference_id,AWS_KEY,AWS_SECRET_KEY,AWS_BASE_URL,AWS_REGION,AWS_BUCKET,s3Url)).execute(devicePath.replace("\"", ""));
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void syncMessage(final Context context, final String reference_id_val){
        try{
            new Runnable(){
                @Override
                public void run() {
                    SocketIo socketIo = SocketIo.getInstance();
                    if(socketIo!=null && socketIo.isSocketConnected() && socketIo.isAccessToken()){
                        Helper.getInstance().LogDetails("syncMessage","socket connected");
                        ConversationTable table = new ConversationTable(context);
                        JSONArray data = table.getNonSyncMessage(reference_id_val);
                        if(data!=null && data.length()>0)
                        {
                                Socket socket = socketIo.getSocket();
                                if(socket!=null ){

                                    for(int i=0;i<data.length();i++)
                                    {

                                        try {
                                            JSONObject jsonObject=data.getJSONObject(i);
                                            int messageType=jsonObject.getInt("message_type");
                                            if(messageType==0){
                                                Helper.getInstance().LogDetails("syncMessage","emit called"+data.get(i).toString());
                                                socket.emit(SocketConstants.EVENT_SEND_MESSAGE,data.get(i));
                                            }
                                            else
                                            {
                                                socket.emit(SocketConstants.EVENT_SEND_ATTACHMENT,data.get(i));
                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                    }

                                }

                        }

                    }else
                    {
                        Helper.getInstance().LogDetails("syncMessage","socket not connected");
                    }


                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }






    public static void syncNonDeliveredMessage(final Context context){
        try{
            new Runnable(){
                @Override
                public void run() {
                    if (Helper.getInstance().isAppForground(context)) {
                        SocketIo socketIo = SocketIo.getInstance();
                        if(socketIo.isSocketConnected()){

                        ConversationTable table = new ConversationTable(context);
                        JSONArray data = table.getNonDeliveredChat();
                        if(data!=null && data.length()>0)
                        {



                                Socket socket = socketIo.getSocket();
                                if(data.length() > 0){
                                    for (int i=0;i<data.length();i++)
                                    {
                                        try {

                                            socket.emit(SocketConstants.EVENT_SEND_MESSAGE,data.get(i));
                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                    }

                                }

                            }
                        }
                    }


                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void syncReceivedMessage(final Context context){

       // Helper.getInstance().LogDetails("syncReceivedMessage","called");
        try{
            new Runnable(){
                @Override
                public void run() {

                    boolean isSelf=Session.getIsSelf(context);
                    if (Helper.getInstance().isAppForground(context)) {
                        SocketIo socketIo = SocketIo.getInstance();

                        if(socketIo!=null && socketIo.isSocketConnected() && socketIo.isAccessToken()){
                          //  Helper.getInstance().LogDetails("syncReceivedMessage","connected");
                            ConversationTable table = new ConversationTable(context);
                            JSONArray data = table.getRecivedChat();
                            if(isSelf)
                            {
                                if(data!=null && data.length()>0)
                                {

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
                            }
                        }
                    }
                    else
                    {

                     //   Helper.getInstance().LogDetails("syncReceivedMessage","not connected");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            try {
                                if(BackgroundServiceForOreo.tmSocket !=null && BackgroundServiceForOreo.tmSocket.connected() && BackgroundServiceForOreo.isAccessTokenUpdated)
                                {
                                    Helper.getInstance().LogDetails("syncReceivedMessage","socket service connected");
                                    ConversationTable table = new ConversationTable(context);
                                    JSONArray data = table.getRecivedChat();
                                    if(isSelf)
                                    {
                                        if(data!=null && data.length()>0)
                                        {

                                            if(data.length() > 0){
                                                for (int i=0;i<data.length();i++)
                                                {
                                                    try {
                                                        Helper.getInstance().LogDetails("syncReceivedMessage","socket service connected"+data.get(i));
                                                        BackgroundServiceForOreo.tmSocket.emit(SocketConstants.EVENT_MESSAGE_DELIVERED,data.get(i));
                                                    } catch (JSONException e) {

                                                        e.printStackTrace();
                                                    }
                                                }

                                            }

                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {


                            if(SocketService.tmSocket !=null && SocketService.tmSocket.connected() && SocketService.isAccessTokenUpdated)
                            {
                                Helper.getInstance().LogDetails("syncReceivedMessage","socket service connected");
                                ConversationTable table = new ConversationTable(context);
                                JSONArray data = table.getRecivedChat();
                                if(isSelf)
                                {
                                    if(data!=null && data.length()>0)
                                    {

                                        if(data.length() > 0){
                                            for (int i=0;i<data.length();i++)
                                            {
                                                try {
                                                    Helper.getInstance().LogDetails("syncReceivedMessage","socket service connected"+data.get(i));
                                                    SocketService.tmSocket.emit(SocketConstants.EVENT_MESSAGE_DELIVERED,data.get(i));
                                                } catch (JSONException e) {

                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                    }
                                }
                            }
                            else
                            {
                                Helper.getInstance().LogDetails("syncReceivedMessage","socket service not connected");
                            }
                        }
                    }



                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void syncNonReadMessage(final Context context, final int senderId, final int receiverId, final String conversationReferenceId){
        try{
            new Runnable(){
                @Override
                public void run() {

                    boolean isSelf=Session.getIsSelf(context);


                        SocketIo socketIo = SocketIo.getInstance();
                        ConversationTable table = new ConversationTable(context);
                        if(!isSelf)
                        {

                            JSONArray data = table.getNonReadChat(senderId,receiverId,conversationReferenceId);
                            if(data!=null && data.length()>0)
                            {
                                if(HandlerHolder.mainActivityUiHandler!=null){
                                    HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.UPDATE_UNREAD_COUNT);
                                }
                            }

                        }
                        if(socketIo!=null){
                            Helper.getInstance().LogDetails("syncNonReadMessage",socketIo.isAccessToken()+"");
                        }

                         if(socketIo!=null && socketIo.isSocketConnected() && isSelf && socketIo.isAccessToken()){

                            JSONArray data = table.getNonReadChat(senderId,receiverId,conversationReferenceId);
                            if(data!=null && data.length()>0)
                            {
                                if(HandlerHolder.mainActivityUiHandler!=null){
                                    HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.UPDATE_UNREAD_COUNT);
                                }
                            }

                            if(isSelf)
                            {
                                if(data!=null && data.length()>0)
                                {
                                    if(socketIo.isSocketConnected()){
                                        Socket socket = socketIo.getSocket();

                                        if(data.length() > 0){
                                            for (int i=0;i<data.length();i++)
                                            {
                                                try {

                                                    Helper.getInstance().LogDetails("syncNonReadMessage",data.get(i).toString()+"");
                                                    JSONObject jsonObject=data.getJSONObject(i);
                                                    String accessToken=jsonObject.getString("access_token");
                                                    String accessTokenNew=Session.getAccessToken(context);
                                                    if(accessToken!=null && accessTokenNew!=null && accessToken.equals(accessTokenNew))
                                                    {
                                                        Helper.getInstance().LogDetails("syncNonReadMessage if",data.get(i).toString()+"");
                                                        socket.emit(SocketConstants.EVENT_MESSAGE_READ,data.get(i));
                                                    }
                                                    else
                                                    {

                                                        jsonObject.put("access_token",accessTokenNew);
                                                        Helper.getInstance().LogDetails("syncNonReadMessage else",accessToken+" "+jsonObject.toString());
                                                        socket.emit(SocketConstants.EVENT_MESSAGE_READ,jsonObject);
                                                    }
                                                   // socket.emit(SocketConstants.EVENT_MESSAGE_READ,data.get(i));
                                                } catch (JSONException e) {

                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                    }
                                }
                                Helper.getInstance().LogDetails("syncNonReadMessage","completed===");
                            }

                        }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateChat(final Context context, final JSONObject jsonObject) {


        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {
                        ConversationTable table = new ConversationTable(context);
                        table.updateChat(jsonObject);

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public static void updateDeliveredMessage(final Context context, final JSONObject jsonObject) {
        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {
                        ConversationTable table = new ConversationTable(context);
                        table.updateDeliveredMessage(jsonObject);

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void updateReadMessage(final Context context, final JSONObject jsonObject) {
        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {
                        ConversationTable table = new ConversationTable(context);
                        table.updateReadMessage(jsonObject);

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void updateAvailabilityStatus(final Context context, final JSONObject jsonObject) {
        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {
                        ConversationTable table = new ConversationTable(context);
                        table.updateAvailableStatus(jsonObject);

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void addMessage(final Context context, final JSONObject jsonObject) {

        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessage ","called"+jsonObject.toString());



/*        {"sender_id":"566",
                "receiver_id":2,
                "message_type":0,
                "message_id":7700,
                "created_at":"2019-03-21 11:06:05",
                "reference_id":"",
                "is_group":0,
                "is_reply":0,"message":"jk;kl;",
                "conversation_reference_id":"16a78f5ce939356969"}*/

        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {

                        ChatModel model=new ChatModel();

                        model.setSender_id(jsonObject.optInt("sender_id"));
                        model.setReceiver_id(jsonObject.optInt("receiver_id"));
                        model.setMessage_type(jsonObject.optInt("message_type"));
                        //model.setMessage_type(jsonObject.optInt("msg_type"));
                        model.setReference_id(jsonObject.optString("reference_id"));
                        model.setIs_reply(jsonObject.optInt("is_reply"));
                        model.setMessage(jsonObject.optString("message"));
                        model.setCreated_at(jsonObject.optString("created_at"));
                        model.setMessage_id(jsonObject.optInt("message_id"));
                        model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                        model.setAttachment(jsonObject.optString("attachment"));
                        model.setAttachment_name(jsonObject.optString("attachment_name"));
                        model.setAttachment_extension(jsonObject.optString("attachment_extension"));

                        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessage","called"+jsonObject.optString("conversation_reference_id")+" "+model.getConversation_reference_id());

                        model.setIs_read(0);
                        model.setIs_delivered(0);
                        model.setIs_received(1);
                        model.setIs_sync(1);


                        ConversationTable table = new ConversationTable(context);
                        boolean status=table.checkMessageIdAndInsert(jsonObject);
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

    public static void addSelfMessage(final Context context, final JSONObject jsonObject) {

        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessage ","called"+jsonObject.toString());



/*        {"sender_id":"566",
                "receiver_id":2,
                "message_type":0,
                "message_id":7700,
                "created_at":"2019-03-21 11:06:05",
                "reference_id":"",
                "is_group":0,
                "is_reply":0,"message":"jk;kl;",
                "conversation_reference_id":"16a78f5ce939356969"}*/

        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null)
                    {

                        ChatModel model=new ChatModel();

                        model.setSender_id(jsonObject.optInt("sender_id"));
                        model.setReceiver_id(jsonObject.optInt("receiver_id"));
                        model.setMessage_type(jsonObject.optInt("message_type"));
                        //model.setMessage_type(jsonObject.optInt("msg_type"));
                        model.setReference_id(jsonObject.optString("reference_id"));
                        model.setIs_reply(jsonObject.optInt("is_reply"));
                        model.setMessage(jsonObject.optString("message"));
                        model.setCreated_at(jsonObject.optString("created_at"));
                        model.setMessage_id(jsonObject.optInt("message_id"));
                        model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                        model.setAttachment(jsonObject.optString("attachment"));
                        model.setAttachment_name(jsonObject.optString("attachment_name"));
                        model.setAttachment_extension(jsonObject.optString("attachment_extension"));

                        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessage","called"+jsonObject.optString("conversation_reference_id")+" "+model.getConversation_reference_id());

                        model.setIs_read(0);
                        model.setIs_delivered(0);
                        model.setIs_received(0);
                        model.setIs_sync(1);


                        ConversationTable table = new ConversationTable(context);
                        boolean status=table.checkMessageIdAndInsert(jsonObject);
                        /*if(status){
                            syncReceivedMessage(context);
                        }*/

                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void addMessageFromNotification( Context context, final JSONObject jsonObject) {

        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessageFromNotification","called"+jsonObject.toString());

/*        {"sender_id":"566",
                "receiver_id":2,
                "message_type":0,
                "message_id":7700,
                "created_at":"2019-03-21 11:06:05",
                "reference_id":"",
                "is_group":0,
                "is_reply":0,"message":"jk;kl;",
                "conversation_reference_id":"16a78f5ce939356969"}*/



        try{
            new Runnable(){
                @Override
                public void run() {
                    if(jsonObject!=null && context!=null)
                    {


                        String tmUserId= Session.getTmUserId(context);
                        Helper.getInstance().LogDetails("ConversationTableAsynckTask addMessageFromNotification","called "+tmUserId  +" "+jsonObject.optInt("sender_id"));




                        ChatModel model=new ChatModel();

                        model.setSender_id(jsonObject.optInt("sender_id"));
                        model.setReceiver_id(jsonObject.optInt("receiver_id"));
                      //  model.setMessage_type(jsonObject.optInt("message_type"));
                        model.setMessage_type(jsonObject.optInt("msg_type"));
                        model.setReference_id(jsonObject.optString("reference_id"));
                        model.setIs_reply(jsonObject.optInt("is_reply"));
                        model.setMessage(jsonObject.optString("message"));
                        model.setCreated_at(jsonObject.optString("created_at"));
                        model.setMessage_id(jsonObject.optInt("message_id"));
                        model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                        model.setAttachment(jsonObject.optString("attachment"));
                        model.setAttachment_name(jsonObject.optString("attachment_name"));
                        model.setAttachment_extension(jsonObject.optString("attachment_extension"));

                        Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE addMessage","called"+jsonObject.optString("conversation_reference_id")+" "+model.getConversation_reference_id());


                        if(jsonObject.optInt("msg_type")==12)
                        {
                            model.setIs_read(1);
                        }
                        else
                        {
                            model.setIs_read(0);
                        }

                      /*  if( tmUserId!=null && jsonObject.optInt("sender_id")==Integer.parseInt(tmUserId))
                        {
                            model.setIs_received(0);
                        }
                        else
                        {
                            model.setIs_received(1);
                        }*/

                        model.setIs_delivered(0);

                        model.setIs_sync(1);

                        JSONArray jsonArray=new JSONArray();
                        jsonArray.put(jsonObject);

                        ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                        conversationTableAsynckTask.execute(context,jsonArray);


                       /* ConversationTable table = new ConversationTable(context);
                        boolean status=table.checkMessageIdAndInsert(jsonObject);
                        if(status){

                            syncReceivedMessage(context);


                        }*/



                    }

                }
            }.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getMissingMessages(final Context context){
        Helper.getInstance().LogDetails("getMissingMessages ","called");
     /*   new Thread(new Runnable() {
            @Override
            public void run() {*/
                if(Utilities.getConnectivityStatus(context)>0){
                    Helper.getInstance().LogDetails("getMissingMessages ","if ");
                   // callMessagesApi(context);
                    ConversationTable chatTable=new ConversationTable(context);
                    JSONArray jsonArray=chatTable.getMissingMessages();
                    ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                    String conversation_reference_id=activeChatsTable.getConversationReferenceIdList();



                    int tmUserId;
                    tmUserId= Integer.parseInt( Session.getTmUserId(context));

                    if(jsonArray!=null)
                    {
                        Helper.getInstance().LogDetails("getMissingMessages messageid array:",jsonArray.toString()+" "+conversation_reference_id);
                        JSONObject data =new JSONObject();
                        try {
                            data.put("user_id",tmUserId);
                            data.put("conversation_reference_id",conversation_reference_id);
                            data.put("message_ids",jsonArray);

                            if(data!=null && data.length()>0)
                            {
                                Helper.getInstance().LogDetails("getMissingMessages chat get missing messages data",data.toString());
                                SocketIo socketIo = SocketIo.getInstance();
                                if(socketIo.isSocketConnected()){
                                    Socket socket = socketIo.getSocket();
                                    if(data.length() > 0){
                                        socket.emit(SocketConstants.EVENT_GET_MISSING_MESSAGES,data);
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else
                {
                  /*  if(HandlerHolder.mainActivityUiHandler!=null)
                    {
                        HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.SYNC_FAILED);
                    }*/

                }
      /*      }
        });*/

    }



    public static void callMessagesApiArray(final Context context) {
        Helper.getInstance().LogDetails("getMissingMessages callMessagesApiArray ","called");

        String tmUserId="",socketId="",accessToken="";
        tmUserId= Session.getTmUserId(context);
        socketId= Session.getSocketId(context);
        accessToken=Session.getAccessToken(context);
        String tmToken =ApiEndPoint.TM_SERVER_SOCKET_TOKEN;

        ApiInterface apiService;
        apiService = ApiClient.getClient().create(ApiInterface.class);

       Helper.getInstance().LogDetails("getMissingMessages callMessagesApiArray ","tmToken: "+tmToken+" tmUserId: "+tmUserId+" socketId: "+socketId+" accessToken :"+accessToken);
        Call<JsonObject> call = apiService.getMessages1(tmToken,Integer.parseInt(tmUserId),socketId,accessToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();

             /*   {"success":true,"messages":[{"reference_id":"",
                        "conversation_reference_id":"7553bb9625892ca9c0",
                        "message_id":10552,"sender_id":2,"receiver_id":2,"message_type":0,
                        "is_reply":0,"original_message_id":0,"message":"wqwqwaxzax","attachment":"",
                        "caption":"","is_read":0,"is_delivered":0,"is_group":0,"created_at":"2019-03-27 19:47:25",
                        "row_number":1,"dummy":22,"group_member_status":[]}*/

                if (object != null) {

                    Helper.getInstance().LogDetails("getMissingMessages callMessagesApiArray ",object.toString());

                    JsonArray jsonArray= object.getAsJsonArray("messages");
                   if(jsonArray!=null && jsonArray.size()>0){
                       Helper.getInstance().LogDetails("getMissingMessages callMessagesApiArray ",jsonArray.size()+"");
                       ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                       conversationTableAsynckTask.execute(context,jsonArray);
                   }



                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

               Helper.getInstance().LogDetails("getMissingMessages"," Calling sync get messages API Something went wrong try again later."+t.getLocalizedMessage()+" "+t.getCause());

            }

        });

    }
    public static void getSitesApi(Context context) {

        try{


            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");

                return;
            }

            String user_id="",company_token="";
            user_id= Session.getUserID(context);
            company_token= Session.getCompanyToken(context);
            Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", ApiEndPoint.token + " " + company_token + " " + user_id);
            //  openProgess();
            C2mApiInterface c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);
            Call<SitesResponse> call = c2mApiService.getSites(ApiEndPoint.token, company_token, user_id);
            call.enqueue(new Callback<SitesResponse>() {
                @Override
                public void onResponse(Call<SitesResponse> call, Response<SitesResponse> response) {
                    SitesResponse apiResponse = response.body();
                    //closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {



                            Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", "success"+apiResponse.toString());

                            if (apiResponse.getSitesInfo() != null) {

                                insertSites(apiResponse.getSitesInfo(),context);
                                removeSites(apiResponse.getSitesInfo(),context);

                            } else {
                                Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", "data empty");
                            }
                        } else {

                            Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", "false");
                        }

                    } else {

                        Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", "null");
                    }

                }

                @Override
                public void onFailure(Call<SitesResponse> call, Throwable t) {

                    Helper.getInstance().LogDetails("SitesTableAsynckTask getSiteAssetsApi", "exeption " + t.getLocalizedMessage() + " " + t.getMessage());
                    //closeProgress();
                }

            });
        }catch (Exception e){

            e.printStackTrace();
        }

    }
    private static void insertSites(List<SitesInfo> sitesInfo, Context context){
        try {
            if (sitesInfo != null && sitesInfo.size() > 0) {
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(sitesInfo).getAsJsonArray();

                if(jsonArray!=null && jsonArray.size()>0) {
                    Helper.getInstance().LogDetails("SitesTableAsynckTask insertSites", jsonArray.size()+"");
                    JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                    SitesTableAsynckTask siteAssetsTable = new SitesTableAsynckTask();
                    siteAssetsTable.execute(context, Values.Action.INSERT, jsonArray1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void removeSites(List<SitesInfo> sitesInfo, Context context){
        try {
            if (sitesInfo != null && sitesInfo.size() > 0) {
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(sitesInfo).getAsJsonArray();

                if(jsonArray!=null && jsonArray.size()>0) {
                    JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                    SitesTableAsynckTask siteAssetsTable = new SitesTableAsynckTask();
                    siteAssetsTable.execute(context, Values.Action.DELETE, jsonArray1);
                }
            }
            else
            {
                SitesTableAsynckTask siteAssetsTable = new SitesTableAsynckTask();
                siteAssetsTable.execute(context, Values.Action.DELETE, new JSONArray());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void getSiteAssetsApi(Context context) {

        try{


            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");

                return;
            }

            String user_id="",company_token="";
            user_id= Session.getUserID(context);
            company_token= Session.getCompanyToken(context);
            Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", ApiEndPoint.token + " " + company_token + " " + user_id);
            //  openProgess();
           C2mApiInterface c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);
            Call<SiteAssetsResponse> call = c2mApiService.getSiteAssets(ApiEndPoint.token, company_token, user_id);
            call.enqueue(new Callback<SiteAssetsResponse>() {
                @Override
                public void onResponse(Call<SiteAssetsResponse> call, Response<SiteAssetsResponse> response) {
                    SiteAssetsResponse apiResponse = response.body();
                    //closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {

                            s3Url = apiResponse.getS3url();

                            Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", "success"+apiResponse.toString());

                            if (apiResponse.getData() != null) {

                                insertSiteAssests(apiResponse.getData(),context);

                            } else {

                                Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", "data empty");
                            }
                        } else {

                            Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", "false");
                        }

                    } else {

                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", "null");
                    }

                }

                @Override
                public void onFailure(Call<SiteAssetsResponse> call, Throwable t) {

                    Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask getSiteAssetsApi", "exeption " + t.getLocalizedMessage() + " " + t.getMessage());
                    //closeProgress();
                }

            });
        }catch (Exception e){

            e.printStackTrace();
        }

    }

    public static void insertSiteAssests(SiteAssetData data,Context context)
    {
        if(data!=null ){
            List<Image> images=new ArrayList<>();
            images=data.getImages();
            //3
            if(images!=null && images.size()>0){
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(images).getAsJsonArray();

                if (jsonArray != null && jsonArray.size() > 0) {
                    try {
                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertActiveAgents images size", jsonArray1.length() + " "+jsonArray1.toString());
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask.execute(context,Values.Action.INSERT,Values.AssetType.IMAGES, jsonArray1);
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask1 = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask1.execute(context,Values.Action.DELETE,Values.AssetType.IMAGES, jsonArray1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
            else{

                SiteAssetsTable siteAssetsTable=new SiteAssetsTable(context);
                siteAssetsTable.clearDb(Values.AssetType.IMAGES);
            }

            List<Collateral> collateralList=new ArrayList<>();
            collateralList=data.getCollateral();

            //2
            if(collateralList!=null && collateralList.size()>0){
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(collateralList).getAsJsonArray();

                if (jsonArray != null && jsonArray.size() > 0) {
                    try {
                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertActiveAgents collateralList  size", jsonArray1.length() + " "+jsonArray1.toString());
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask.execute(context,Values.Action.INSERT,Values.AssetType.COLLATERAL, jsonArray1);
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask1 = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask1.execute(context,Values.Action.DELETE,Values.AssetType.COLLATERAL, jsonArray1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
            else{
                SiteAssetsTable siteAssetsTable=new SiteAssetsTable(context);
                siteAssetsTable.clearDb(Values.AssetType.COLLATERAL);
            }
            List<CannedResponse> cannedMessageList=new ArrayList<>();
            cannedMessageList=data.getCannedResponses();
            //4
            if(cannedMessageList!=null && cannedMessageList.size()>0){
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(cannedMessageList).getAsJsonArray();

                if (jsonArray != null && jsonArray.size() > 0) {
                    try {
                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertActiveAgents cannedMessageList size", jsonArray1.length() + " "+jsonArray1.toString());
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask.execute(context,Values.Action.INSERT,Values.AssetType.CANNEDRESPONSES, jsonArray1);
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask1 = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask1.execute(context,Values.Action.DELETE,Values.AssetType.CANNEDRESPONSES, jsonArray1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
            else{
                SiteAssetsTable siteAssetsTable=new SiteAssetsTable(context);
                siteAssetsTable.clearDb(Values.AssetType.CANNEDRESPONSES);
            }

            List<Link> linkList=new ArrayList<>();
            linkList=data.getLinks();
            if(linkList!=null && linkList.size()>0){
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(linkList).getAsJsonArray();

                if (jsonArray != null && jsonArray.size() > 0) {
                    try {
                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                        Helper.getInstance().LogDetails("SiteAssetsTableAsynckTask insertActiveAgents linkList size", jsonArray1.length() + ""+jsonArray1.toString());
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask.execute(context,Values.Action.INSERT,Values.AssetType.LINKS, jsonArray1);
                        SiteAssetsTableAsynckTask siteAssetsTableAsynckTask1 = new SiteAssetsTableAsynckTask();
                        siteAssetsTableAsynckTask1.execute(context,Values.Action.DELETE,Values.AssetType.LINKS, jsonArray1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            else{
                SiteAssetsTable siteAssetsTable=new SiteAssetsTable(context);
                siteAssetsTable.clearDb(Values.AssetType.LINKS);
            }


        }

    }

    public static void callMessagesApi(final Context context) {


        String tmUserId="",socketId="",accessToken="";
        tmUserId= Session.getTmUserId(context);
        socketId= Session.getSocketId(context);
        accessToken=Session.getAccessToken(context);
        String tmToken =ApiEndPoint.TM_SERVER_SOCKET_TOKEN;

        ApiInterface apiService;
        apiService = ApiClient.getClient().create(ApiInterface.class);

        ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
        String conversation_reference_id=activeChatsTable.getConversationReferenceIdList();

        Helper.getInstance().LogDetails("getMissingMessages get messages API call  : ","tmToken: "+tmToken+" tmUserId: "+tmUserId+" socketId: "+socketId+" accessToken :"+accessToken+" conversation_reference_id  "+conversation_reference_id);
        Call<GetMessagesResponse> call = apiService.getMessages(tmToken,Integer.parseInt(tmUserId),socketId,accessToken,conversation_reference_id);
        call.enqueue(new Callback<GetMessagesResponse>() {
            @Override
            public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                GetMessagesResponse apiResponse = response.body();

                if (apiResponse != null) {


                    if (apiResponse.isSuccess()) {

                        Helper.getInstance().LogDetails("getMissingMessages get messages API call  : ",apiResponse.isSuccess()+" ");

                        if(apiResponse.getMessages()!=null && apiResponse.getMessages().size()>0)
                        {

                            Helper.getInstance().LogDetails("getMissingMessages get messages API call  : ","if"+apiResponse.getMessages().size()+" ");
                            List<Message> messageList = new ArrayList<>();
                            messageList=apiResponse.getMessages();

                            for(int i=0;i<messageList.size();i++){
                                Helper.getInstance().LogDetails("getMissingMessages callMessagesApi ",messageList.get(i).getMessage()+" "+messageList.get(i).getSender_id());
                            }
                           // updateMessages(messageList,context);
                            if(messageList!=null && messageList.size()>0){

                                Gson gson = new GsonBuilder().create();
                                JsonArray jsonArray = gson.toJsonTree(messageList).getAsJsonArray();

                                if(jsonArray!=null && jsonArray.size()>0){
                                    try {
                                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                                        Helper.getInstance().LogDetails("getMissingMessages callMessagesApiArray size",jsonArray1.length()+"");
                                        ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                                        conversationTableAsynckTask.execute(context,jsonArray1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                            }

                           }



                        }
                        else
                        {
                            Helper.getInstance().LogDetails("getMissingMessages get messages API call  : ","else "+apiResponse.getMessages().size()+" ");
                            if(HandlerHolder.mainActivityUiHandler!=null)
                            {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
                            }
                        }


                    }
                    else
                    {
                        if(HandlerHolder.mainActivityUiHandler!=null)
                        {
                            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
                        }
                    }



                }

            }

            @Override
            public void onFailure(Call<GetMessagesResponse> call, Throwable t) {


                    if(HandlerHolder.mainActivityUiHandler!=null)
                    {
                        HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.SYNC_FAILED);
                    }


            }

        });
    }




    public static void getOfflineMessages(final Context context) {


        String tmUserId="",socketId="",accessToken="";
        tmUserId= Session.getTmUserId(context);
        socketId= Session.getSocketId(context);
        accessToken=Session.getAccessToken(context);

        ConversationTable chatTable=new ConversationTable(context);
        int lastMessageId=chatTable.getLastMessageId();
        ApiInterface apiService;
        apiService = ApiClient.getClient().create(ApiInterface.class);

        ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
        String conversation_reference_id=activeChatsTable.getConversationReferenceIdList();



        Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ","tmToken: "+ApiEndPoint.TM_SERVER_SOCKET_TOKEN+" tmUserId: "+tmUserId+" lastMessageId: "+lastMessageId+" accessToken :"+accessToken+" conversation_reference_id  "+conversation_reference_id);
        Call<GetMessagesResponse> call = apiService.getOfflineMessages(ApiEndPoint.TM_SERVER_SOCKET_TOKEN,Integer.parseInt(tmUserId),socketId,lastMessageId,accessToken,conversation_reference_id);
        call.enqueue(new Callback<GetMessagesResponse>() {
            @Override
            public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                GetMessagesResponse apiResponse = response.body();

                if (apiResponse != null) {

                    Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ",apiResponse.toString()+" ");
                    if (apiResponse.isSuccess()) {

                        if(apiResponse.getMessages()!=null && apiResponse.getMessages().size()>0)
                        {

                            Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ","if"+apiResponse.getMessages().size()+" ");
                            List<Message> messageList = new ArrayList<>();
                            messageList=apiResponse.getMessages();
                            // updateMessages(messageList,context);
                            if(messageList!=null && messageList.size()>0){

                                Gson gson = new GsonBuilder().create();
                                JsonArray jsonArray = gson.toJsonTree(messageList).getAsJsonArray();

                                if(jsonArray!=null && jsonArray.size()>0){
                                    try {
                                        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                                        Helper.getInstance().LogDetails("getOfflineMessages callMessagesApiArray size",jsonArray1.length()+"");
                                        ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                                        conversationTableAsynckTask.execute(context,jsonArray1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ","else "+apiResponse.getMessages().size()+" ");
                            if(HandlerHolder.mainActivityUiHandler!=null)
                            {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
                            }
                        }


                    }
                    else
                    {
                        if(HandlerHolder.mainActivityUiHandler!=null)
                        {
                            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
                        }
                    }



                }
                else
                {
                    Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ","null");
                }

            }

            @Override
            public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
                Helper.getInstance().LogDetails("getOfflineMessages get messages API call  : ","onFailure"+t.getLocalizedMessage()+" "+t.getCause());
            }

        });
    }

    public static void updateMessages(List<Message> messages, Context context) {
        for(int i=0;i<messages.size();i++){

            List<ChatModel> list=new ArrayList<>();

            Message message=messages.get(i);
            ChatModel model=new ChatModel();

            model.setMessage_id(message.getMessage_id());
            model.setSender_id(message.getSender_id());
            model.setReceiver_id(message.getReceiver_id());
            model.setMessage(message.getMessage().toString());
            model.setIs_group(0);
            model.setIs_delivered(message.getIs_delivered());
            model.setIs_read(message.getIs_read());
            model.setConversation_reference_id(message.getConversation_reference_id());
            model.setAttachment(model.getAttachment());
            model.setAttachment_name(model.getAttachment_name());
            model.setAttachment_extension(model.getAttachment_extension());
            model.setCaption("");
            model.setOriginal_message("");
            model.setReference_id(message.getReference_id());
            model.setIs_sync(1);
            model.setIs_downloaded(0);
            model.setCreated_at(message.getCreated_at());

            list.add(model);

            ConversationTable table = new ConversationTable(context);
            boolean status=table.checkMessageIdAndInsertMessage(model);



        }
    }
    public static void EmitGetAgentChats(Context context) {

        //2020-02-17T13:43:28.000Z

        /*{site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token}; c2mSocket.emit("check_agent_status",agentStatusData);*/
        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask EmitGetAgentChats ","called ");

        String agent_id= Session.getUserID(context);
        try {
            if (agent_id != null && !agent_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("agent_id", Integer.parseInt(agent_id));

                Socket mSocket=AppSocket.mSocket;
                //   object.put("site_id", siteId);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask EmitGetAgentChats ",object.toString());
                    mSocket.emit(SocketConstants.GET_AGENT_CHATS, object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask EmitGetAgentChats ","res "+args[0].toString());
                            JSONArray jsonArray=(JSONArray) args[0];
                            insertAgentChats(jsonArray,context);
                            removeDeletedChats(jsonArray,context);

                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void removeDeletedChats(JSONArray jsonArray,Context context){
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask removeDeletedChats","if called");

                ActiveChatsTableAsynckTask activeChatsTableAsynckTask=new ActiveChatsTableAsynckTask();
                activeChatsTableAsynckTask.execute(context,Values.Action.DELETE,jsonArray);

            }
            else
            {
                Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask removeDeletedChats","else called");
                ActiveChatsTableAsynckTask activeChatsTableAsynckTask=new ActiveChatsTableAsynckTask();
                activeChatsTableAsynckTask.execute(context,Values.Action.DELETE,jsonArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public static void insertAgentChats(JSONArray jsonArray,Context context){
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask insertChats","called"+jsonArray.toString());

                ActiveChatsTableAsynckTask activeChatsTableAsynckTask=new ActiveChatsTableAsynckTask();
                activeChatsTableAsynckTask.execute(context,Values.Action.INSERT,jsonArray);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void newOnlineActiveChat(JSONObject jsonObject,Context context){
        try {

            boolean isSelf=Session.getIsSelf(context);
            if(isSelf)
            {
                if (jsonObject != null) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask newOnlineActiveChat", "called"+jsonObject.toString());

                    if(Helper.isAppInBackground(context))
                    {
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask newOnlineActiveChat", "if called");
                        ActiveChatsTableAsynckTask activeChatsTableAsynckTask = new ActiveChatsTableAsynckTask();
                        activeChatsTableAsynckTask.execute(context,Values.Action.INSERT, jsonArray);
                    }
                    else
                    {
                        Helper.getInstance().LogDetails("ActiveChatsTableAsynckTask newOnlineActiveChat", "else called");

                        ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                        ActiveChat activeChat=setActiveUserData(jsonObject);
                        activeChatsTable.insertActiveChat(activeChat);
                    }



                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static ActiveChat setActiveUserData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("setActiveUserData", jsonObject.toString());


        ActiveChat activeChat = null;
        try {
            if (jsonObject != null) {
                activeChat = new ActiveChat();
                activeChat.setChatId(jsonObject.optString("chat_id"));
                activeChat.setVisitorOs(jsonObject.optString("visitor_os"));
                activeChat.setVisitorUrl(jsonObject.optString("visitor_url"));
                activeChat.setVisitorIp(jsonObject.optString("visitor_ip"));
                activeChat.setVisitorBrowser(jsonObject.optString("visitor_browser"));
                activeChat.setAgentId(jsonObject.optString("agent_id"));
                activeChat.setChatReferenceId(jsonObject.optString("chat_reference_id"));
                activeChat.setLocation(jsonObject.optString("location"));
                activeChat.setGuestName(jsonObject.optString("guest_name"));
                activeChat.setVisitorId(jsonObject.optString("visitor_id"));
                activeChat.setAccountId(jsonObject.optString("account_id"));
                activeChat.setSiteId(jsonObject.optString("site_id"));
                activeChat.setEmail(jsonObject.optString("email"));
                activeChat.setMobile(jsonObject.optString("mobile"));
                activeChat.setTmVisitorId(jsonObject.optString("tm_visitor_id"));
                activeChat.setVisitCount(jsonObject.optString("visit_count"));
                activeChat.setTrack_code(jsonObject.optString("track_code"));
                activeChat.setChatStatus(jsonObject.optString("chat_status"));


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return activeChat;
    }
    public static void agentChatEnded(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null)
            {
                String chat_id=jsonObject.optString("chat_id");
                ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                if(chat_id!=null && !chat_id.trim().isEmpty())
                {
                    Helper.getInstance().LogDetails("SyncData agentChatEnded","called "+jsonObject.toString());
                    activeChatsTable.deleteActiveChat(Integer.parseInt(chat_id));
                }


            }
        }catch (Exception e){

        }
    }

    public static void getAllAgentsApi(Context context) {

        try{


            String loginUserId= Session.getUserID(context);
            String company_token= Session.getCompanyToken(context);


            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "updateAgentsSiteList ==== "+ApiEndPoint.token + " " + loginUserId + " " +company_token);

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }

            retrofit2.Call<AllAgentsApi.ActiveAgentsResponse> call = AllAgentsApi.getApiService().getActiveAgents(ApiEndPoint.token, company_token, loginUserId);

            call.enqueue(new Callback<AllAgentsApi.ActiveAgentsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<AllAgentsApi.ActiveAgentsResponse> call, @NonNull Response<AllAgentsApi.ActiveAgentsResponse> response) {


                    AllAgentsApi.ActiveAgentsResponse data = response.body();

                    if (data != null) {
                        if (data.isSuccess()) {
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "true");
                            if (data.getData() != null && data.getData().size() > 0) {
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi =====", "size " + data.getData().size());
                                insertAllAgents(data.getData(),context);
                            } else {
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "size 0");

                            }

                        } else {
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "false");


                        }
                    } else {

                        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "res null");


                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<AllAgentsApi.ActiveAgentsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi =====", "onFailure " + t.getLocalizedMessage() + " " + t.getCause());


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void insertAllAgents(List<ActiveAgent> activeAgents,Context context) {
        try {
            if (activeAgents != null && activeAgents.size() > 0) {


                    if (activeAgents != null && activeAgents.size() > 0) {
                        for(int k=0;k<activeAgents.size();k++){
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertActiveAgents","data "+activeAgents.get(k).toString());
                        }


                        Gson gson = new GsonBuilder().create();
                        JsonArray jsonArray = gson.toJsonTree(activeAgents).getAsJsonArray();

                        if (jsonArray != null && jsonArray.size() > 0) {
                            try {
                                JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertActiveAgents  size", jsonArray1.length() + "");
                                ActiveAgentsTableAsynckTask activeAgentsTableAsynckTask = new ActiveAgentsTableAsynckTask();
                                activeAgentsTableAsynckTask.execute(context, jsonArray1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void userChatEnded(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null){
                Helper.getInstance().LogDetails("SyncData userChatEnded",jsonObject.toString());
                ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                activeChatsTable.userChatEnded(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void contactUpdate(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null){
                Helper.getInstance().LogDetails("SyncData contactUpdate",jsonObject.toString());
                ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                activeChatsTable.contactUpdate(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void agentActivity(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null){
                Helper.getInstance().LogDetails("SyncData agentOnlineOfflineStatus",jsonObject.toString());
                AgentsTable agentsTable=new AgentsTable(context);
                agentsTable.agentOnlineOfflineStatus(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void userOnline(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null){
                Helper.getInstance().LogDetails("SyncData userOnline",jsonObject.toString());
                ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                activeChatsTable.userOnline(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void userOffline(JSONObject jsonObject,Context context){
        try
        {
            if(jsonObject!=null){
                Helper.getInstance().LogDetails("SyncData userOffline",jsonObject.toString());
                ActiveChatsTable activeChatsTable=new ActiveChatsTable(context);
                activeChatsTable.userOffline(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getArchievesApi(Context context) {

        try {
            String site_id,fromDate,toDate,agent_ids,role,loginUserId,company_token,site_ids;


            loginUserId= Session.getUserID(context);
             company_token= Session.getCompanyToken(context);
             site_ids=Session.getAllSiteIds(context);
             role= Session.getUserRole(context);


            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }
            if(site_ids!=null && !site_ids.replace(" ","").trim().isEmpty() && site_ids.equals(""))
            {
                String all_site_ids="";
               List<SitesInfo> sitesInfoList = Session.getSiteInfoList(context, Session.SP_SITE_INFO);
                for(int i=0;i<sitesInfoList.size();i++){
                    //SitesTable sitesTable=new SitesTable(context);
                    //sitesTable.insertSites(sitesInfoList.get(i));
                    all_site_ids=all_site_ids+sitesInfoList.get(i).getSiteId()+",";
                }

                Session.saveAllSiteIds(context,all_site_ids);

                site_ids=all_site_ids;
            }


            int offset=0,limit=1000;

            site_id="";
            fromDate="";
            toDate="";
            agent_ids="";


            //getArchievesApi before data === RdFgcWB7HpZn3Meo user_id 7 site_id ,1, role 1 limit 50 offset 0 0 agent_ids  site_ids 7 fromDate  toDate  company_token 5d826b7d1ae7df8a6f2852b4ee00fec2e422b428e47ffe05126208316d20935e3/+wedkLisuuzfKVnJYfylDshW3sov73xdXlakTTBVE=
            Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi before", ApiEndPoint.token + " user_id " + loginUserId + " site_id " + site_id + " role " + role + " limit " + limit + " offset " + offset + " "  + " agent_ids " + agent_ids + " site_ids " + site_ids + " fromDate " + fromDate + " toDate " + toDate + " company_token " + company_token);

            retrofit2.Call<ArchievsResponse> call = ArchievsApi.getApiService().getArchievs(ApiEndPoint.token, company_token, loginUserId, site_id, role, limit, offset, agent_ids, site_ids, fromDate, toDate);

            call.enqueue(new Callback<ArchievsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Response<ArchievsResponse> response) {


                    ArchievsResponse data = response.body();


                    // offset=offset+limit;

                    if (data != null) {
                        if (data.isSuccess()) {

                            if (data.getData() != null && data.getData().size() > 0) {



                                Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi success", data.getData().size() + " ");

                              insertArchiveChats(data.getData(),context);

                            } else {

                                Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi", "size 0");



                            }

                        } else {
                            Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi", "false");
                            Helper.getInstance().pushToast(context, data.getMessage());
                        }
                    } else {
                        Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi", "null");
                        Helper.getInstance().pushToast(context, "Server connection failed");
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask getArchievesApi", "onFailure" + t.getLocalizedMessage() + " " + t.getMessage());


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void insertArchiveChats(List<ActiveChat> activeChats,Context context){
        try {
            if(activeChats!=null && activeChats.size()>0){

                Helper.getInstance().LogDetails("insertArchiveChats=== before",activeChats.get(0).toString());

                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(activeChats).getAsJsonArray();
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONArray jsonArray1 = new JSONArray(jsonArray.toString());

                    Helper.getInstance().LogDetails("ArchiveChatsTableAsynckTask insertArchiveChats","called"+jsonArray1.length());
                    Helper.getInstance().LogDetails("insertArchiveChats=== after",jsonArray1.get(0).toString());
                    ArchiveChatsTableAsynckTask archiveChatsTableAsynckTask=new ArchiveChatsTableAsynckTask();
                    archiveChatsTableAsynckTask.execute(context,jsonArray1);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getActiveAgentsApi(Context context) {

        try{


            String loginUserId= Session.getUserID(context);
            String company_token= Session.getCompanyToken(context);


            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "updateAgentsSiteList ==== "+ApiEndPoint.token + " " + loginUserId + " " +company_token);

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }

            retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call = ActiveAgentsApi.getApiService().getActiveAgents(ApiEndPoint.token, company_token, loginUserId);

            call.enqueue(new Callback<ActiveAgentsApi.ActiveAgentsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call, @NonNull Response<ActiveAgentsApi.ActiveAgentsResponse> response) {


                    ActiveAgentsApi.ActiveAgentsResponse data = response.body();

                    if (data != null) {
                        if (data.isSuccess()) {
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "true");
                            if (data.getData() != null && data.getData().size() > 0) {
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi =====", "size " + data.getData().size());
                                insertActiveAgents(data.getData(),context);
                            } else {
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "size 0");

                            }

                        } else {
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "false");


                        }
                    } else {

                        Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi", "res null");


                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask getActiveAgentsApi =====", "onFailure " + t.getLocalizedMessage() + " " + t.getCause());


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getFeedBackCategoryApi(Context context) {
        try {

            Helper.getInstance().LogDetails("CategoriesTableAsynckTask FeedBackDialogBox getFeedBackCategory","called ");

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }

            String loginUserId= Session.getUserID(context);
            String company_token= Session.getCompanyToken(context);


            retrofit2.Call<CategoriesApi.CategoriesResponse> call =
                    CategoriesApi.getApiService().getCategories(ApiEndPoint.token, loginUserId, company_token);

            call.enqueue(new Callback<CategoriesApi.CategoriesResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<CategoriesApi.CategoriesResponse> call,
                                       @NonNull Response<CategoriesApi.CategoriesResponse> response) {


                    CategoriesApi.CategoriesResponse data = response.body();



                    if (data != null) {
                        if (data.getSuccess()) {
                            if(data.getData() != null && !data.getData().isEmpty()){
                              insertCategories(data.getData(),context);
                            }

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<CategoriesApi.CategoriesResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    public static void insertCategories(List<Category> categories, Context context) {
        try {
            if (categories != null && categories.size() > 0) {
                        for(int k=0;k<categories.size();k++){
                            Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertActiveAgents","data "+categories.get(k).toString());
                        }

                        Gson gson = new GsonBuilder().create();
                        JsonArray jsonArray = gson.toJsonTree(categories).getAsJsonArray();
                        if (jsonArray != null && jsonArray.size() > 0) {
                            try {
                                JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                                Helper.getInstance().LogDetails("CategoriesTableAsynckTask insertActiveAgents  size", jsonArray1.length() + "");
                                CategoriesTableAsynckTask categoriesTableAsynckTask = new CategoriesTableAsynckTask();
                                categoriesTableAsynckTask.execute(context,Values.Action.INSERT, jsonArray1);
                                removeDeletedCategories(jsonArray1,context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            removeDeletedCategories(new JSONArray(),context);
                        }


            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void removeDeletedCategories(JSONArray jsonArray,Context context){
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                Helper.getInstance().LogDetails("CategoriesTableAsynckTask removeDeletedCategories","if called");

                CategoriesTableAsynckTask categoriesTableAsynckTask = new CategoriesTableAsynckTask();
                categoriesTableAsynckTask.execute(context, Values.Action.DELETE,jsonArray);


            }
            else
            {
                Helper.getInstance().LogDetails("CategoriesTableAsynckTask removeDeletedCategories","else called");
                CategoriesTableAsynckTask categoriesTableAsynckTask = new CategoriesTableAsynckTask();
                categoriesTableAsynckTask.execute(context, Values.Action.DELETE,jsonArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void insertActiveAgents(List<SiteAgentsData> data,Context context) {
        try {
            if (data != null && data.size() > 0) {
                for(int i=0;i<data.size();i++){
                    List<ActiveAgent> activeAgents=data.get(i).getActiveAgents();
                    if (activeAgents != null && activeAgents.size() > 0) {
                        for(int k=0;k<activeAgents.size();k++){
                            Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertActiveAgents","data "+activeAgents.get(k).toString());
                        }


                        Gson gson = new GsonBuilder().create();
                        JsonArray jsonArray = gson.toJsonTree(activeAgents).getAsJsonArray();

                        if (jsonArray != null && jsonArray.size() > 0) {
                            try {
                                JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
                                Helper.getInstance().LogDetails("ActiveAgentsTableAsynckTask insertActiveAgents  size", jsonArray1.length() + "");
                                ActiveAgentsTableAsynckTask activeAgentsTableAsynckTask = new ActiveAgentsTableAsynckTask();
                                activeAgentsTableAsynckTask.execute(context, jsonArray1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
