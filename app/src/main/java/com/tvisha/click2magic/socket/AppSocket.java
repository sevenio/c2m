package com.tvisha.click2magic.socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.multidex.MultiDex;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.constants.ApiEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;




public class AppSocket extends Application implements  Application.ActivityLifecycleCallbacks{

    public static int SOCKET_OPENED_ACTIVITY= Values.AppActivities.ACTIVITY_NON;
    public static List<SitesInfo> sitesInfoList =new ArrayList<>();



    public static  Handler eventHandler,tmEventHandler;
    public static Context context;



    public static  Socket mSocket,tmSocket;
    public  boolean isSelf=true;
    private String userId;

    long mLastTimeInMills=0;



    static SocketIo socketIo;
    Activity activity;



    public Socket getTmSocketInstance(){
        if(tmSocket==null){
            tmSocket=initTmSocket();
            return tmSocket;
        }
        else
        {
            return tmSocket;
        }

    }
    public Socket getSocketInstance(){
        if(mSocket==null)
        {
            mSocket= getSocket();
            Helper.getInstance().LogDetails("AppSocket getSocketInstance","if called");
            return mSocket;
        }
        else
        {
            Helper.getInstance().LogDetails("AppSocket getSocketInstance","else called");
            return mSocket;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }



    public Socket getSocket() {

        isSelf=Session.getIsSelf(context);
        if (userId==null) {
            if(isSelf)
            {
                userId =  Session.getUserID(AppSocket.this);
                Helper.getInstance().LogDetails("AppSocket getSocket","isSelf "+userId+" "+isSelf);
            }
            else
            {
                userId = Session.getUserID(AppSocket.this);
                Helper.getInstance().LogDetails("AppSocket getSocket","other "+userId+" "+isSelf);
            }
        }
        if (mSocket == null) {
            mSocket = getSocketObj();
            return mSocket;

        } else if (!mSocket.connected()) {
            connectSocket();
            return mSocket;

        } else {
            return mSocket;
        }
    }



    public void disconnectSocket(){
        if(mSocket != null){
            socketDestroy();
        }
    }


    public boolean isSocketConnected() {
        return mSocket != null && mSocket.connected();
    }

    public boolean isTmSocketConnected() {
        return tmSocket != null && tmSocket.connected();
    }


    public Socket getSocketObj() {
        try {
            if (mSocket == null && (userId != null && !userId.isEmpty() && !userId.equals("null"))) {

                String siteToken="",agent="agent",serverToken="";
                siteToken = Session.getSiteToken(context);
                serverToken =   Session.getCompanySocketToken(AppSocket.this);

                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.query = "DJdZj6NIMFU1Q=" + siteToken + "&guest_id="+userId+"&type="+agent+"&server_token="+serverToken;
                opts.reconnection = true;
                mSocket = IO.socket(ApiEndPoint.SOCKET_PATH, opts);
                Helper.getInstance().LogDetails("AppSocket getSocketObj","called"+" siteToken "+siteToken+" guest_id  "+userId+" type "+agent);
                Helper.getInstance().LogDetails("","called"+" siteToken "+siteToken+" guest_id  "+userId+" type "+agent);

                socketConnection();
            }

            //}
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return mSocket;
    }

    public void connectSocket(){

        if(SystemClock.elapsedRealtime()-mLastTimeInMills<800){
            Helper.getInstance().LogDetails("AppSocket connectSocket ","return called");
            return;
        }
        mLastTimeInMills=SystemClock.elapsedRealtime();

        try {
            if (!mSocket.connected()) {
                Helper.getInstance().LogDetails("AppSocket connectSocket ","called");
                mSocket.connect();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(AppSocket.this);
    }

    public void tmSocketDestroy(){
        try {
            if (socketIo != null) {
                socketIo.removeSocketListener();

            }
            if (tmSocket != null) {
                if (tmSocket.connected()) {
                    tmSocket.disconnect();

                }
                tmSocket.close();
                tmSocket = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("AppSocket Socket On connect c2m","called");
                if(eventHandler!=null){
                    Helper.getInstance().LogDetails("AppSocket Socket On connect c2m","eventHandler");
                    eventHandler.sendEmptyMessage(SocketConstants.SocketEvents.EVENT_CONNECT);
                    if(mSocket!=null){
                        SyncData.EmitGetAgentChats(AppSocket.this);
                        SyncData.getActiveAgentsApi(AppSocket.this);
                        SyncData.getArchievesApi(AppSocket.this);
                        SyncData.getSiteAssetsApi(AppSocket.this);
                        SyncData.getSitesApi(AppSocket.this);
                        SyncData.getFeedBackCategoryApi(AppSocket.this);
                        String socketId=mSocket.id();
                        if(socketId!=null)
                        {

                            Helper.getInstance().LogDetails("AppSocket Socket On connect c2m socket params:","socket id "+socketId);

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("AppSocket Socket On connect c2m socket params:", "socket id null ");
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("Socket On connect error c2m"," "+args[0].toString());
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("Socket On Disconnect c2m","");
            if (HandlerHolder.transfer_dialog!=null){
                HandlerHolder.transfer_dialog.obtainMessage(Values.RecentList.SOCKETDISCONNECT).sendToTarget();
            }
        }
    };




    private  Emitter.Listener agentStatusUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentStatusUpdated","called "+args[0]);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED,args[0]).sendToTarget();

            }
        }
    };

    private  Emitter.Listener agentStatusUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentStatusUpdate","called "+args[0]);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATE,args[0]).sendToTarget();
            }
        }
    };

    private  Emitter.Listener userChatEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("userChatEnded AppSocket called",args[0].toString());
            JSONObject jsonObject=(JSONObject) args[0];
            SyncData.userChatEnded(jsonObject,AppSocket.this);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED,args[0]).sendToTarget();

            }
        }
    };

    private  Emitter.Listener chatEndedAgent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("chatEndedAgent AppSocket called",args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT,args[0]).sendToTarget();
            }

        }
    };


    private  Emitter.Listener newOnline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("newOnline AppSocket called",args[0].toString());
            if(eventHandler!=null){
                JSONObject jsonObject=(JSONObject) args[0];
                SyncData.newOnlineActiveChat(jsonObject,AppSocket.this);
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_NEW_ONLINE,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener checkAgentStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("checkAgentStatus AppSocket called",args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener agentStatusInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentStatusInfo AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO,args[0]).sendToTarget();
            }

        }
    };
    private  Emitter.Listener userTypingToAgent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("userTypingToAgent AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT,args[0]).sendToTarget();
            }

        }
    };
    private  Emitter.Listener agentRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentRequest AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_REQUEST,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener contactUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("contactUpdate AppSocket called"," "+args[0].toString());
           JSONObject jsonObject=(JSONObject) args[0];
            SyncData.contactUpdate(jsonObject,AppSocket.this);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE,args[0]).sendToTarget();
            }

        }
    };


    private  Emitter.Listener agentActivity = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentActivity AppSocket called"," "+args[0].toString());
                JSONObject jsonObject=(JSONObject) args[0];
                SyncData.agentActivity(jsonObject,AppSocket.this);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener agentChatEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentChatEnded AppSocket called"," "+args[0].toString());
            JSONObject jsonObject=(JSONObject) args[0];
            SyncData.agentChatEnded(jsonObject,AppSocket.this);
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED,args[0]).sendToTarget();
            }

        }
    };
    private  Emitter.Listener getAgentChats = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){
                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_GET_AGENT_CHATS,args[0]).sendToTarget();
            }

        }
    };
    private  Emitter.Listener agentLogoffEverywhere = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("emitOffline agentLogoffEverywhere AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){

                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_LOGFF_EVERYWHERE,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener pushAgentOffline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("pushAgentOffline AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){

                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_PUSH_AGENT_OFFLINE,args[0]).sendToTarget();
            }

        }
    };

    private  Emitter.Listener agentTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentTyping AppSocket called"," "+args[0].toString());
            if(eventHandler!=null){

                eventHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_TYPING,args[0]).sendToTarget();
            }

        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        this.activity = activity;


    }

    public Socket initTmSocket(){
        Helper.getInstance().LogDetails("initTmSocket called","");

        if(socketIo==null)
        {
            socketIo = SocketIo.getInstance();
        }

        socketIo.setContext(getApplicationContext());
        socketIo.setHandlers(socketHandler);
        tmSocket= socketIo.connectSocket();
        return tmSocket;
    }

    long mTmLastTimeInMills=0;

    public void connectTmSocket(){
        if(SystemClock.elapsedRealtime()-mTmLastTimeInMills<800){
            Helper.getInstance().LogDetails("AppSocket connectTmSocket ","return called");
            return;
        }

        mTmLastTimeInMills=SystemClock.elapsedRealtime();
        if(socketIo!=null){
            socketIo.connectTmSocket();
        }
    }


    public void disconnectTmSocket(){

        if(socketIo!=null)
        {
            tmSocketDestroy();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @SuppressLint("HandlerLeak")
    public Handler socketHandler = new Handler()
    {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {

                case SocketConstants.TmSocketEvents.SOCKET_CONNECT:
                    try {
                        JSONObject obj = (JSONObject) msg.obj;
                        Helper.getInstance().LogDetails("SOCKET_CONNECT APPSOCKET", "tm event called ");
                        if (tmEventHandler != null) {
                            tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CONNECT, obj).sendToTarget();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;



                case SocketConstants.TmSocketEvents.SOCKET_TM_ERROR:
                    try {
                        JSONObject errorObject = (JSONObject) msg.obj;
                        if (errorObject != null) {
                            Helper.getInstance().LogDetails("SOCKET_TM_ERROR APPSOCKET", "tm event called " + errorObject.toString());

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_ACESS_TOKEN:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null)
                        {

                            String accessToken=jsonObject.optString("access_token");
                            Helper.getInstance().LogDetails("SOCKET_ACESS_TOKEN APPSOCKET","tm event called "+jsonObject.toString()+" "+accessToken);
                            Session.saveAccessToken(AppSocket.this,accessToken);
                            if(HandlerHolder.mainActivityUiHandler!=null){
                            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.ACCESS_TOKEN_UPDATED);
                            }
                            if(HandlerHolder.mainActivityUiHandler!=null)
                            {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.MESSAGES_SYNC_COMPLETED);
                            }
                            SyncData.callMessagesApi(AppSocket.this);
                            SyncData.syncChat(AppSocket.this);
                            SyncData.uploadAttachment(AppSocket.this);
                            SyncData.getMissingMessages(AppSocket.this);
                            SyncData.getOfflineMessages(AppSocket.this);


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_DISCONNECT:
                    try {
                        Helper.getInstance().LogDetails("SOCKET_DISCONNECT APPSOCKET", "tm event called ");
                        JSONObject obj1 = (JSONObject) msg.obj;
                        if (tmEventHandler != null) {
                            tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_DISCONNECT, obj1).sendToTarget();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT:
                    try {
                        JSONObject object = (JSONObject) msg.obj;

                        if (object != null) {
                            Helper.getInstance().LogDetails("***SOCKET_MESSAGE_SENT APPSOCKET", "tm event called " + object.toString());
                            SyncData.updateChat(AppSocket.this, object);
                            SyncData.addSelfMessage(AppSocket.this, object);
                            if (tmEventHandler != null) {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT, object).sendToTarget();
                            }

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE:

                    break;

                case SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null)
                        {
                            Helper.getInstance().LogDetails("SOCKET_RECEIVE_MESSAGE APPSOCKET","event called "+jsonObject.toString());
                            SyncData.addMessage(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,jsonObject).sendToTarget();
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;

                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("***SOCKET_MESSAGE_DELIVERED APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.updateDeliveredMessage(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;

                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("***SOCKET_MESSAGE_READ APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.updateReadMessage(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;


                case SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;


                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_SYNC_OFFLINE_MESSAGES APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.updateChat(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;


                case SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES:
                    try{
                        JSONArray jsonArray = (JSONArray) msg.obj;

                        if(jsonArray!=null){

                            for(int i=0;i<jsonArray.length();i++){
                                Helper.getInstance().LogDetails("SOCKET_GET_MISSING_MESSAGES APPSOCKET getMissingMessages***",jsonArray.getJSONObject(i).optString("message"));
                            }

                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES,jsonArray).sendToTarget();
                            }
                            Helper.getInstance().LogDetails("SOCKET_GET_MISSING_MESSAGES APPSOCKET","tm event called "+jsonArray.toString());

                           /* if(jsonArray!=null && jsonArray.length()>0)
                            {

                                ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                                conversationTableAsynckTask.execute(context,jsonArray);

                            }*/
                             //SyncData.addMessage(AppSocket.this,jsonObject);


                        }
                        /*JSONObject jsonObject = (JSONObject) msg.obj;

                        if(jsonObject!=null){

                            SyncData.addMessage(MyApplication.this,jsonObject);
                            if(handler!=null)
                            {
                                handler.obtainMessage(SocketConstants.SOCKET_GET_MISSING_MESSAGES,jsonObject).sendToTarget();
                            }

                        }*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_GET_OFFLINE_MESSAGES:
                    try{
                        JSONArray jsonArray = (JSONArray) msg.obj;

                        if(jsonArray!=null){

                            for(int i=0;i<jsonArray.length();i++){
                                Helper.getInstance().LogDetails("SOCKET_GET_OFFLINE_MESSAGES APPSOCKET getMissingMessages***",jsonArray.getJSONObject(i).optString("message"));
                            }

                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_OFFLINE_MESSAGES,jsonArray).sendToTarget();
                            }
                            Helper.getInstance().LogDetails("SOCKET_GET_OFFLINE_MESSAGES APPSOCKET","tm event called "+jsonArray.toString());

                           /* if(jsonArray!=null && jsonArray.length()>0)
                            {

                                ConversationTableAsynckTask conversationTableAsynckTask=new ConversationTableAsynckTask();
                                conversationTableAsynckTask.execute(context,jsonArray);

                            }*/
                            //SyncData.addMessage(AppSocket.this,jsonObject);


                        }
                        /*JSONObject jsonObject = (JSONObject) msg.obj;

                        if(jsonObject!=null){

                            SyncData.addMessage(MyApplication.this,jsonObject);
                            if(handler!=null)
                            {
                                handler.obtainMessage(SocketConstants.SOCKET_GET_MISSING_MESSAGES,jsonObject).sendToTarget();
                            }

                        }*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_CHANGE_USER_AVAILABILITY_STATUS APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.updateAvailabilityStatus(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                    case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_MESSAGE_READ_BY_ME APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.updateReadMessage(AppSocket.this,jsonObject);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_USER_AVAILABILITY_STATUS APPSOCKET","tm event called "+jsonObject.toString());
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_USER_OFFLINE APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.userOffline(jsonObject,AppSocket.this);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("SOCKET_USER_ONLINE APPSOCKET","tm event called "+jsonObject.toString());
                            SyncData.userOnline(jsonObject,AppSocket.this);
                            if(tmEventHandler!=null)
                            {
                                tmEventHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;


            }
        }
    };


    public void socketDestroy() {
        try {

            Helper.getInstance().LogDetails("socketDestroy","called");
            if (mSocket != null) {

                Session.saveAccessToken(AppSocket.this,"");

                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                //mounika
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATED, agentStatusUpdated);
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATE, agentStatusUpdate);
                mSocket.off(SocketConstants.USER_CHAT_ENDED, userChatEnded);
                mSocket.off(SocketConstants.CHAT_ENDED_AGENT, chatEndedAgent);
                mSocket.off(SocketConstants.NEW_ONLINE, newOnline);
                mSocket.off(SocketConstants.CHECK_AGENT_STATUS, checkAgentStatus);
                mSocket.off(SocketConstants.AGENT_STATUS_INFO, agentStatusInfo);
                mSocket.off(SocketConstants.USER_TYPING_TO_AGENT, userTypingToAgent);
                mSocket.off(SocketConstants.AGENT_REQUEST, agentRequest);
                mSocket.off(SocketConstants.CONTACT_UPDATE, contactUpdate);
                mSocket.off(SocketConstants.AGENT_ACTIVITY, agentActivity);
                mSocket.off(SocketConstants.AGENT_CHAT_ENDED, agentChatEnded);
                mSocket.off(SocketConstants.GET_AGENT_CHATS, getAgentChats);
                mSocket.off(SocketConstants.AGENT_LOGFF_EVERYWHERE, agentLogoffEverywhere);
                mSocket.off(SocketConstants.PUSH_AGENT_OFFLINE, pushAgentOffline);
                mSocket.off(SocketConstants.AGENT_TYPING, agentTyping);

                if(mSocket.connected()) {
                    mSocket.disconnect();

                }
                mSocket.close();
                mSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void socketConnection() {
        try {

            if(mSocket!=null){

                connectSocket();

                Helper.getInstance().LogDetails("getSocket","socketConnection");

                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

                //mounika
                mSocket.on(SocketConstants.AGENT_STATUS_UPDATED, agentStatusUpdated);
                mSocket.on(SocketConstants.AGENT_STATUS_UPDATE, agentStatusUpdate);
                mSocket.on(SocketConstants.USER_CHAT_ENDED, userChatEnded);
                mSocket.on(SocketConstants.CHAT_ENDED_AGENT, chatEndedAgent);
                mSocket.on(SocketConstants.NEW_ONLINE, newOnline);
                mSocket.on(SocketConstants.CHECK_AGENT_STATUS, checkAgentStatus);
                mSocket.on(SocketConstants.AGENT_STATUS_INFO, agentStatusInfo);
                mSocket.on(SocketConstants.USER_TYPING_TO_AGENT, userTypingToAgent);
                mSocket.on(SocketConstants.AGENT_REQUEST, agentRequest);
                mSocket.on(SocketConstants.CONTACT_UPDATE, contactUpdate);
                mSocket.on(SocketConstants.AGENT_ACTIVITY, agentActivity);
                mSocket.on(SocketConstants.AGENT_CHAT_ENDED, agentChatEnded);
                mSocket.on(SocketConstants.GET_AGENT_CHATS, getAgentChats);
                mSocket.on(SocketConstants.AGENT_LOGFF_EVERYWHERE, agentLogoffEverywhere);
                mSocket.on(SocketConstants.PUSH_AGENT_OFFLINE, pushAgentOffline);
                mSocket.on(SocketConstants.AGENT_TYPING, agentTyping);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
