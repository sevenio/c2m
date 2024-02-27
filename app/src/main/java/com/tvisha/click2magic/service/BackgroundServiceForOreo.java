package com.tvisha.click2magic.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class BackgroundServiceForOreo extends JobIntentService {

    static final int JOB_ID = 1000;

    String loginUserId,tmUserId;
    boolean isSelf=true;
    public static Socket mSocket,tmSocket;
    public  static boolean isAccessTokenUpdated=false;

    static boolean service_start = false;

    public static void startJob() {
        if (HandlerHolder.backgroundservice == null) {
            service_start = true;
            if(AppSocket.context!=null ){
                enqueueWork(AppSocket.context, BackgroundServiceForOreo.class, JOB_ID, new Intent());

                Helper.getInstance().LogDetails("BackgroundServiceForOreo ","startJob if");
            }
            else
            {

                Helper.getInstance().LogDetails("BackgroundServiceForOreo ","startJob else");
            }

        }
    }

    @Override
    public void onDestroy() {
        //tmSocketDestroy();
        try {
            Thread.sleep(1000);
            tmSocketDestroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static Socket getSocketon() {
        return mSocket;
    }
    public static Socket getTmSocketon() {
        return tmSocket;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        try {
            if (HandlerHolder.backgroundservice == null && !Helper.getInstance().isAppForground(BackgroundServiceForOreo.this)) {

                Helper.getInstance().LogDetails("BackgroundServiceForOreo ","==== create ");

                isSelf=Session.getIsSelf(BackgroundServiceForOreo.this);
                if(isSelf){
                    loginUserId = Session.getUserID(BackgroundServiceForOreo.this);
                }
                else
                {

                    loginUserId =  Session.getUserID(BackgroundServiceForOreo.this);
                }


                HandlerHolder.backgroundservice = socketHandler;

                if (Session.getLoginStatus(BackgroundServiceForOreo.this)) {

                    if (Helper.getConnectivityStatus(BackgroundServiceForOreo.this) && !Helper.getInstance().isAppForground(BackgroundServiceForOreo.this) && HandlerHolder.mainActivityUiHandler == null) {
                        if (tmSocket != null && !tmSocket.connected()) {
                           connectTmSocket();
                        } else if (tmSocket == null) {
                            connectToTmSocket();
                        }


                    }
                }
            }
            else
            {
                Helper.getInstance().LogDetails("BackgroundServiceForOreo ","tmSocketDestroy ");
                tmSocketDestroy();
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

    }

    private void connectToSocket(){

        try {

            HandlerHolder.backgroundservice = socketHandler;
            if (mSocket != null && mSocket.connected()) {
                return;
            }

            if (!Helper.getInstance().isAppForground(BackgroundServiceForOreo.this) && HandlerHolder.mainActivityUiHandler == null) {
                updateAndroidSecurityProvider(BackgroundServiceForOreo.this);


                if (Session.getLoginStatus(BackgroundServiceForOreo.this)) {
                    try {


                        try {
                            io.socket.client.IO.Options opts = new io.socket.client.IO.Options();


                            String siteToken="",agent="agent";
                            siteToken =  Session.getSiteToken(BackgroundServiceForOreo.this);

                            opts.query = "DJdZj6NIMFU1Q=" + siteToken + "&guest_id="+loginUserId+"&type="+agent;

                            opts.reconnection = true;
                            try {
                                mSocket = io.socket.client.IO.socket(ApiEndPoint.SOCKET_PATH, opts);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                           connectSocket();
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }

                       // socketConnection();


                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    long mLastTimeInMills=0;

    private void connectSocket(){
        if(SystemClock.elapsedRealtime()-mLastTimeInMills<500){
            Helper.getInstance().LogDetails("AppSocket bgservice connectSocket ","return called");
            return;
        }
        mLastTimeInMills=SystemClock.elapsedRealtime();
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
        }
    }




    private void connectToTmSocket() {
        try {

            HandlerHolder.backgroundservice = socketHandler;
            if (tmSocket != null && tmSocket.connected()) {
                return;
            }

            if (!Helper.getInstance().isAppForground(BackgroundServiceForOreo.this) && HandlerHolder.mainActivityUiHandler == null) {
                updateAndroidSecurityProvider(BackgroundServiceForOreo.this);

                 isSelf=Session.getIsSelf(BackgroundServiceForOreo.this);

                if (Session.getLoginStatus(BackgroundServiceForOreo.this)) {
                    try {

                        try {

                            if(isSelf){
                                tmUserId= Session.getTmUserId(BackgroundServiceForOreo.this);
                            }
                            else
                            {
                                tmUserId=Session.getOtherUserTmUserId(BackgroundServiceForOreo.this);
                            }
                            if(tmSocket==null){
                                io.socket.client.IO.Options opts = new io.socket.client.IO.Options();
                                String tmToken =ApiEndPoint.TM_SERVER_SOCKET_TOKEN;
                                String  platform="2";
                                Helper.getInstance().LogDetails("socket params:token",tmToken+"tmUserId "+tmUserId);

                                opts.query = "user_id="+tmUserId+"&token="+tmToken+"&platform="+platform;

                                opts.reconnection = true;
                                try {

                                    tmSocket = io.socket.client.IO.socket(ApiEndPoint.TM_SERVER_SOCKET_PATH, opts);
                                    initTmSocketConnection();

                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                connectTmSocket();
                            }

                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }





                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    long mTmLastTimeInMills=0;
    private void connectTmSocket(){
        if(SystemClock.elapsedRealtime()-mTmLastTimeInMills<800){
            Helper.getInstance().LogDetails("AppSocket connectTmSocket ","return called");
            return;
        }
        mTmLastTimeInMills=SystemClock.elapsedRealtime();
        if (tmSocket != null && !tmSocket.connected()) {
            tmSocket.connect();
        }
    }


    @SuppressLint("HandlerLeak")
    public Handler socketHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SocketConstants.TmSocketEvents.SOCKET_CONNECT:
                    Helper.getInstance().LogDetails("socket connected tm BackgroundServiceForOreo","");
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_TM_ERROR:
                    JSONObject errorObject = (JSONObject) msg.obj;
                    if (errorObject != null) {
                        Helper.getInstance().LogDetails("chat error: BackgroundServiceForOreo" , errorObject.toString());
                    }

                    break;
                case SocketConstants.TmSocketEvents.SOCKET_ACESS_TOKEN:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if (jsonObject != null) {

                            String accessToken = jsonObject.optString("access_token");
                            Helper.getInstance().LogDetails("chat access token BackgroundServiceForOreo" + "  /  " , jsonObject.toString() + " " + accessToken);
                            Session.saveAccessToken(BackgroundServiceForOreo.this,accessToken);
                            isAccessTokenUpdated=true;
                            SyncData.syncChat(BackgroundServiceForOreo.this);
                            SyncData.syncReceivedMessage(BackgroundServiceForOreo.this);
                            SyncData.uploadAttachment(BackgroundServiceForOreo.this);
                           // SyncData.getMissingMessages(BackgroundServiceForOreo.this);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_DISCONNECT:
                    Helper.getInstance().LogDetails("socket disconnected BackgroundServiceForOreo","");
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT:
                    JSONObject object = (JSONObject) msg.obj;

                    if (object != null) {
                        Helper.getInstance().LogDetails("chat message sent call back BackgroundServiceForOreo" , object.toString());
                        SyncData.updateChat(BackgroundServiceForOreo.this, object);
                        if (HandlerHolder.backgroundservice != null) {
                            HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT, object).sendToTarget();
                        }

                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE:
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat receive call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.addMessage(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE, jsonObject).sendToTarget();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;

                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat message deliver call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.updateDeliveredMessage(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED, jsonObject).sendToTarget();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;

                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat message read call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.updateReadMessage(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ, jsonObject).sendToTarget();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;

                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat message SOCKET_MESSAGE_READ_BY_ME call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.updateReadMessage(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME, jsonObject).sendToTarget();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;


                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat sync offline messages call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.updateChat(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES, jsonObject).sendToTarget();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES:
                    try {
                        JSONArray jsonArray = (JSONArray) msg.obj;

                        if (jsonArray != null) {
                            Helper.getInstance().LogDetails("chat get missing messages  call back BackgroundServiceForOreo" ,jsonArray.toString());
                            //SyncData.addMessage(AppSocket.this,jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES, jsonArray).sendToTarget();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS:
                    try {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if (jsonObject != null) {
                            Helper.getInstance().LogDetails("chat available status call back BackgroundServiceForOreo" , jsonObject.toString());
                            SyncData.updateAvailabilityStatus(BackgroundServiceForOreo.this, jsonObject);
                            if (HandlerHolder.backgroundservice != null) {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS, jsonObject).sendToTarget();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.DISCONNECT_SOCKET:
                    Helper.getInstance().LogDetails("BackgroundServiceForOreo ","DISCONNECT_SOCKET ");
                    HandlerHolder.backgroundservice=null;
                    tmSocketDestroy();
                    break;


            }
        }
    };





    private Emitter.Listener getMissingMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("Get missing message","");

                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES,args[0]).sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener getOfflineMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("getOfflineMessages Get missing message","");

                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_OFFLINE_MESSAGES,args[0]).sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("Socket connect c2m","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.sendEmptyMessage(SocketConstants.SocketEvents.EVENT_CONNECT);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("Socket On connect error c2m","");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket On Disconnect c2m","");
            if (HandlerHolder.transfer_dialog!=null){
                HandlerHolder.transfer_dialog.obtainMessage(Values.RecentList.SOCKETDISCONNECT).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };




    private  Emitter.Listener agentStatusUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket agentStatusUpdated called  " , args[0].toString());
            if(HandlerHolder.backgroundservice!=null)
            {
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };
    private  Emitter.Listener agentStatusUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket agentStatusUpdate called  " , args[0].toString());
            if(HandlerHolder.backgroundservice!=null)
            {
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATE,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };


    private  Emitter.Listener userChatEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket userChatEnded called  " , args[0].toString());
            if(HandlerHolder.backgroundservice!=null)
            {
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private  Emitter.Listener chatEndedAgent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            /* {"chat_id":98,"agent_id":"19","chat_reference_id":"c35045637cf2867567"}*/
            try{
            Helper.getInstance().LogDetails("Socket chatEndedAgent called  " ,args[0].toString());
            if(HandlerHolder.backgroundservice!=null)
            {
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private  Emitter.Listener newOnline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket newOnline called  " , args[0].toString());
            if(HandlerHolder.backgroundservice!=null)
            {
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_NEW_ONLINE,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener checkAgentStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("checkOnlineStatus AppSocket called","");
            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };


    private  Emitter.Listener agentStatusInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("agentStatusInfo AppSocket called","");
            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private  Emitter.Listener userTypingToAgent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("userTypingToAgent AppSocket called"," "+args[0].toString());
            // [{"id":4,"agent_id":18,"site_id":1,"account_id":1,"is_online":1,"created_at":"2019-05-17T11:55:08.000Z","updated_at":"2019-05-17T11:55:08.000Z"}]
//      [{"site_id":"2","is_online":0}]
            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private  Emitter.Listener agentRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("agentRequest AppSocket called"," "+args[0].toString());

            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_REQUEST,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private  Emitter.Listener contactUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("contactUpdate AppSocket called"," "+args[0].toString());

            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener agentActivity = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("agentActivity AppSocket called"," "+args[0].toString());

            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener agentChatEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("agentChatEnded AppSocket called"," "+args[0].toString());
            try{
            if(HandlerHolder.backgroundservice!=null){
                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED,args[0]).sendToTarget();
            }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener getAgentChats = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("getAgentChats AppSocket called"," "+args[0].toString());
            try{
                if(HandlerHolder.backgroundservice!=null){
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_GET_AGENT_CHATS,args[0]).sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener agentOfflineEverywhere = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //AGENT_LOGFF_EVERYWHERE
            Helper.getInstance().LogDetails("agentOfflineEverywhere AppSocket called"," "+args[0].toString());
            try{
                if(HandlerHolder.backgroundservice!=null){
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_LOGFF_EVERYWHERE,args[0]).sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener pushAgentOffline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //AGENT_LOGFF_EVERYWHERE
            Helper.getInstance().LogDetails("pushAgentOffline AppSocket called"," "+args[0].toString());
            try{
                if(HandlerHolder.backgroundservice!=null){
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_PUSH_AGENT_OFFLINE,args[0]).sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    private  Emitter.Listener agentTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //AGENT_LOGFF_EVERYWHERE
            Helper.getInstance().LogDetails("agentTyping AppSocket called"," "+args[0].toString());
            try{
                if(HandlerHolder.backgroundservice!=null){
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_TYPING,args[0]).sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
    //tm
    private Emitter.Listener sendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{

                Helper.getInstance().LogDetails("chat send message call back",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener sendAttachment = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat send message call back",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SEND_ATTACHMENT,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener syncOfflineMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat syncOfflineMessages call back",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener messageDelivered = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat message delivered call back","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener messageRead = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat message read call back","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener messageReadByMe = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat messageReadByMe call back socketIo ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };

    private Emitter.Listener getUserAvailabilityStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat getUserAvailabilityStatus call back socketIo ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };

    private Emitter.Listener userAvailabilityStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat userAvailabilityStatus call back socketIo ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };


    private Emitter.Listener userOffline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat userOffline call back socketIo ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };


    private Emitter.Listener userOnline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat userOnline call back socketIo ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };




    private Emitter.Listener receiveMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat recive message call back",""+args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener tmError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat error===> ",args[0].toString());
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_TM_ERROR,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener accessToken = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat access token call back","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_ACESS_TOKEN,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener changeAvailabilityStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat available sent call back ","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };


    private Emitter.Listener messageSent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat message sent call back ","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };


    private void updateAndroidSecurityProvider(Context callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);

            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLEngine engine = sslContext.createSSLEngine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (GooglePlayServicesRepairableException e) {

            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), (Activity) callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available. BackgroundServiceForOreo");
        }
    }


    public void tmSocketDestroy(){

        if(tmSocket!=null)
        {
            Helper.getInstance().LogDetails("BackgroundServiceForOreo ****","tmSocketDestroy tmSocket not null ");


            tmSocket.off(Socket.EVENT_CONNECT,onConnect);
            tmSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);
            tmSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            tmSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            tmSocket.off(SocketConstants.EVENT_SEND_MESSAGE, sendMessage);
            tmSocket.off(SocketConstants.EVENT_SEND_ATTACHMENT, sendAttachment);
            tmSocket.off(SocketConstants.EVENT_MESSAGE_DELIVERED, messageDelivered);
            tmSocket.off(SocketConstants.EVENT_TM_ERROR, tmError);
            tmSocket.off(SocketConstants.EVENT_SENT_MESSAGE,messageSent);
            tmSocket.off(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES, syncOfflineMessages);
            tmSocket.off(SocketConstants.EVENT_GET_MISSING_MESSAGES, getMissingMessages);
            tmSocket.off(SocketConstants.EVENT_ACCESS_TOKEN,accessToken);
            tmSocket.off(SocketConstants.EVENT_RECEIVE_MESSAGE,receiveMessage);
            tmSocket.off(SocketConstants.EVENT_CHANGE_USER_AVAILABILITY_STATUS,changeAvailabilityStatus);
            tmSocket.off(SocketConstants.EVENT_MESSAGE_READ_BY_ME,messageReadByMe);
            tmSocket.off(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS,getUserAvailabilityStatus);
            tmSocket.off(SocketConstants.EVENT_USER_AVAILABILITY_STATUS,userAvailabilityStatus);
            tmSocket.off(SocketConstants.EVENT_USER_OFFLINE,userOffline);
            tmSocket.off(SocketConstants.EVENT_USER_ONLINE,userOnline);
            tmSocket.off(SocketConstants.EVENT_GET_OFFLINE_MESSAGES, getOfflineMessages);

            if(tmSocket.connected()) {
                tmSocket.disconnect();

            }
            tmSocket.close();
            tmSocket=null;
        }

    }

    public void socketConnection() {
        try {
            if(mSocket!=null){

                connectSocket();

                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);



                //mounika
                mSocket.on(SocketConstants.AGENT_STATUS_UPDATED, agentStatusUpdated);
                mSocket.on(SocketConstants.USER_CHAT_ENDED, userChatEnded);
                mSocket.on(SocketConstants.AGENT_STATUS_UPDATE, agentStatusUpdate);
                mSocket.on(SocketConstants.CHAT_ENDED_AGENT, chatEndedAgent);
                mSocket.on(SocketConstants.NEW_ONLINE, newOnline) ;
                mSocket.on(SocketConstants.CHECK_AGENT_STATUS, checkAgentStatus);
                mSocket.on(SocketConstants.AGENT_STATUS_INFO, agentStatusInfo);
                mSocket.on(SocketConstants.USER_TYPING_TO_AGENT, userTypingToAgent);
                mSocket.on(SocketConstants.AGENT_REQUEST, agentRequest);
                mSocket.on(SocketConstants.CONTACT_UPDATE, contactUpdate);
                mSocket.on(SocketConstants.AGENT_ACTIVITY, agentActivity);
                mSocket.on(SocketConstants.AGENT_CHAT_ENDED, agentChatEnded);
                mSocket.on(SocketConstants.GET_AGENT_CHATS, getAgentChats);
                mSocket.on(SocketConstants.AGENT_LOGFF_EVERYWHERE, agentOfflineEverywhere);
                mSocket.on(SocketConstants.PUSH_AGENT_OFFLINE, pushAgentOffline);
                mSocket.on(SocketConstants.AGENT_TYPING, agentActivity);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initTmSocketConnection(){

        try{
            if(tmSocket!=null )
            {
                connectTmSocket();

                tmSocket.on(Socket.EVENT_CONNECT,onConnect);
                tmSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
                tmSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//                tmSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                tmSocket.on(SocketConstants.EVENT_SEND_MESSAGE, sendMessage);
                tmSocket.on(SocketConstants.EVENT_SEND_ATTACHMENT, sendAttachment);
                tmSocket.on(SocketConstants.EVENT_MESSAGE_DELIVERED, messageDelivered);
                tmSocket.on(SocketConstants.EVENT_MESSAGE_READ, messageRead);
                tmSocket.on(SocketConstants.EVENT_TM_ERROR, tmError);
                tmSocket.on(SocketConstants.EVENT_SENT_MESSAGE,messageSent);
                tmSocket.on(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES, syncOfflineMessages);
                tmSocket.on(SocketConstants.EVENT_GET_MISSING_MESSAGES, getMissingMessages);
                tmSocket.on(SocketConstants.EVENT_ACCESS_TOKEN,accessToken);
                tmSocket.on(SocketConstants.EVENT_RECEIVE_MESSAGE,receiveMessage);
                tmSocket.on(SocketConstants.EVENT_CHANGE_USER_AVAILABILITY_STATUS,changeAvailabilityStatus);
                tmSocket.on(SocketConstants.EVENT_MESSAGE_READ_BY_ME,messageReadByMe);
                tmSocket.on(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS,getUserAvailabilityStatus);
                tmSocket.on(SocketConstants.EVENT_USER_AVAILABILITY_STATUS,userAvailabilityStatus);
                tmSocket.on(SocketConstants.EVENT_USER_OFFLINE,userOffline);
                tmSocket.on(SocketConstants.EVENT_USER_ONLINE,userOnline);
                tmSocket.on(SocketConstants.EVENT_GET_OFFLINE_MESSAGES, getOfflineMessages);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void socketDestroy() {
        try {
            if (mSocket != null) {

                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

                //mounika
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATED, agentStatusUpdated);
                mSocket.off(SocketConstants.USER_CHAT_ENDED, userChatEnded);
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATE, agentStatusUpdate);
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
                mSocket.off(SocketConstants.AGENT_LOGFF_EVERYWHERE, agentOfflineEverywhere);
                mSocket.off(SocketConstants.PUSH_AGENT_OFFLINE, pushAgentOffline);
                mSocket.off(SocketConstants.AGENT_TYPING, agentActivity);

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


}
