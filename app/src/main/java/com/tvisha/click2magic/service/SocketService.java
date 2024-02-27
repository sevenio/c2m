package com.tvisha.click2magic.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.tvisha.click2magic.DataBase.ConversationTable;
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
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;



public class SocketService extends Service {


    public static Socket tmSocket,mSocket;
    public  static boolean isAccessTokenUpdated=false;
    String loginUserId="",tmUserId="";
    ConversationTable conversationTable;
    boolean isSelf=true;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //return START_STICKY;
        Helper.getInstance().LogDetails("SocketService","onStartCommand called");
        return  START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {


        Helper.getInstance().LogDetails("Background===","socket service onTaskRemoved called");
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            HandlerHolder.mainActivityUiHandler = null;
            try {
                AppSocket appSocket = (AppSocket) getApplicationContext();
                if (appSocket != null) {
                    appSocket.socketDestroy();
                    appSocket.tmSocketDestroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            HandlerHolder.mainActivityUiHandler = null;
            try {
                AppSocket appSocket = (AppSocket) getApplicationContext();
                if (appSocket != null) {
                   appSocket.socketDestroy();
                   appSocket.tmSocketDestroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!Helper.getInstance().isAppForground(SocketService.this)) {
                if ((mSocket != null && !mSocket.connected()) || (mSocket == null)) {
                    Intent restartService = new Intent(getApplicationContext(),
                            this.getClass());
                    restartService.setPackage(getPackageName());
                    PendingIntent restartServicePI = PendingIntent.getService(
                            getApplicationContext(), 1, restartService,
                            PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
                }
                if ((tmSocket != null && !tmSocket.connected()) || (tmSocket == null)) {
                    Intent restartService = new Intent(getApplicationContext(),
                            this.getClass());
                    restartService.setPackage(getPackageName());
                    PendingIntent restartServicePI = PendingIntent.getService(
                            getApplicationContext(), 1, restartService,
                            PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
                }

            }
        }
    }


    @Override
    public void onCreate() {
        try {

            Helper.getInstance().LogDetails("Background===", "socket service onCreate called");
            if (HandlerHolder.backgroundservice == null) {

                isSelf = Session.getIsSelf(SocketService.this);

                if (isSelf) {
                    loginUserId =  Session.getUserID(SocketService.this);
                } else {

                    loginUserId =  Session.getUserID(SocketService.this);
                }


                if (Session.getLoginStatus(SocketService.this)) {


                    conversationTable = new ConversationTable(this);
                    HandlerHolder.backgroundservice = socketHandler;


                    if (!Helper.getInstance().isAppForground(this) && Helper.getConnectivityStatus(SocketService.this) && HandlerHolder.mainActivityUiHandler == null) {

                        if (tmSocket != null && !tmSocket.connected()) {
                          connectTmSocket();
                            //  connectToTmSocket();
                        } else if (tmSocket == null) {
                            Helper.getInstance().LogDetails("Background===", "socket service connectToTmSocket called");
                            connectToTmSocket();
                        }






                    } else {

                        Helper.getInstance().LogDetails("Background===", "socket service tmSocketDestroy called");

                        //socketDestroy();
                        tmSocketDestroy();

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onCreate();
    }

    DeadObjectException deadObjectException=new DeadObjectException();

    public DeadObjectException getDeadObjectException() {

        return deadObjectException;
    }

    @Override
    public void onDestroy() {
        try {

            Helper.getInstance().LogDetails("Background===","socket service onDestroy called");
            tmSocketDestroy();
          //  socketDestroy();
          //  socketDestroy();

         } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public  boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void connectToSocket(){
        try {

            HandlerHolder.backgroundservice = socketHandler;
            if (mSocket != null && mSocket.connected()) {
                return;
            }

            if (!Helper.getInstance().isAppForground(SocketService.this) && HandlerHolder.mainActivityUiHandler == null) {
                updateAndroidSecurityProvider(SocketService.this);


                if (Session.getLoginStatus(SocketService.this)) {
                    try {
                        /*if (socket == null) {
                            socket = (AppSocket) getApplication();
                        }*/
                        try {
                            if(mSocket==null){
                                io.socket.client.IO.Options opts = new io.socket.client.IO.Options();
                                String siteToken="",agent="agent";
                                siteToken =  Session.getSiteToken(SocketService.this);
                                opts.query = "DJdZj6NIMFU1Q=" + siteToken + "&guest_id="+loginUserId+"&type="+agent;
                                opts.reconnection = true;
                                try {
                                    mSocket = io.socket.client.IO.socket(ApiEndPoint.SOCKET_PATH, opts);
                                    socketConnection();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                if (mSocket != null && !mSocket.connected()) {
                                    connectSocket();
                                }
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




    private void connectToTmSocket() {
        try {

            HandlerHolder.backgroundservice = socketHandler;
            if (tmSocket != null && tmSocket.connected()) {
                return;
            }

            if (!Helper.getInstance().isAppForground(SocketService.this) && HandlerHolder.mainActivityUiHandler == null) {
                updateAndroidSecurityProvider(SocketService.this);


                if (Session.getLoginStatus(SocketService.this)) {
                    try {
                        /*if (socket == null) {
                            socket = (AppSocket) getApplication();
                        }*/
                        try {
                            io.socket.client.IO.Options opts = new io.socket.client.IO.Options();
                            isSelf=Session.getIsSelf(SocketService.this);
                            if(isSelf){
                                tmUserId= Session.getTmUserId(SocketService.this);
                            }
                            else
                            {
                                tmUserId=Session.getOtherUserTmUserId(SocketService.this);
                            }

                            if(tmSocket==null){
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
                                if (tmSocket != null && !tmSocket.connected()) {
                                    connectTmSocket();
                                }
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


    @SuppressLint("HandlerLeak")
    public Handler socketHandler = new Handler()
    {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {

                case SocketConstants.TmSocketEvents.SOCKET_CONNECT:
                    Helper.getInstance().LogDetails("socket connected tm SocketService","");
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_TM_ERROR:
                    try {
                        JSONObject errorObject = (JSONObject) msg.obj;
                        if (errorObject != null) {
                            Helper.getInstance().LogDetails("chat error: SocketService", errorObject.toString());
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
                            Helper.getInstance().LogDetails("chat access token SocketService"+"  /  ",jsonObject.toString()+" "+accessToken);
                            Session.saveAccessToken(SocketService.this,accessToken);
                            isAccessTokenUpdated=true;
                            SyncData.syncChat(SocketService.this);
                            SyncData.syncReceivedMessage(SocketService.this);
                            SyncData.uploadAttachment(SocketService.this);

                       //     SyncData.getMissingMessages(SocketService.this);


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_DISCONNECT:
                    Helper.getInstance().LogDetails("socket disconnected SocketService","");
                    break;
                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT:
                    try{
                    JSONObject object = (JSONObject) msg.obj;

                    if(object!=null){
                        Helper.getInstance().LogDetails("chat message sent call back SocketService","");
                        SyncData.updateChat(SocketService.this,object);
                        if(HandlerHolder.backgroundservice!=null)
                        {
                            HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT,object).sendToTarget();
                        }

                    }
                    }catch (Exception e){
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
                            Helper.getInstance().LogDetails("chat receive call back SocketService",""+jsonObject.toString());
                            SyncData.addMessage(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,jsonObject).sendToTarget();
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        if(jsonObject!=null)
                        {
                            Helper.getInstance().LogDetails("chat receive call back SocketService",""+jsonObject.toString());
                            SyncData.addMessage(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME,jsonObject).sendToTarget();
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
                            Helper.getInstance().LogDetails("chat message deliver call back SocketService"  ,"");
                            SyncData.updateDeliveredMessage(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED,jsonObject).sendToTarget();
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
                            Helper.getInstance().LogDetails("chat message read call back SocketService" ,"");
                            SyncData.updateReadMessage(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ,jsonObject).sendToTarget();
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
                            Helper.getInstance().LogDetails("chat sync offline messages call back SocketService"  ,"");
                            SyncData.updateChat(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES,jsonObject).sendToTarget();
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
                            Helper.getInstance().LogDetails("chat get missing messages  call back SocketService" ,"" );
                            //SyncData.addMessage(AppSocket.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES,jsonArray).sendToTarget();
                            }

                        }
                        /*JSONObject jsonObject = (JSONObject) msg.obj;

                        if(jsonObject!=null){
                            Helper.getInstance().LogDetails("chat get missing messages  call back"  +jsonObject.toString());
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
                            Helper.getInstance().LogDetails("chat available status call back SocketService","" );
                            SyncData.updateAvailabilityStatus(SocketService.this,jsonObject);
                            if(HandlerHolder.backgroundservice!=null)
                            {
                                HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS,jsonObject).sendToTarget();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;



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
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), (Activity) callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available. SocketService");
        }
    }








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
                Helper.getInstance().LogDetails("getOfflineMessages ","");
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
            Helper.getInstance().LogDetails("Socket On Disconnect c2m","");
            if (HandlerHolder.transfer_dialog!=null){
                HandlerHolder.transfer_dialog.obtainMessage(Values.RecentList.SOCKETDISCONNECT).sendToTarget();
            }
        }
    };





    private  Emitter.Listener agentStatusUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
            Helper.getInstance().LogDetails("Socket agentStatusUpdated called  " + args[0].toString(),"");
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
            Helper.getInstance().LogDetails("Socket agentStatusUpdated called  " , args[0].toString());
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
            Helper.getInstance().LogDetails("Socket chatEndedAgent called  " , args[0].toString());
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
            Helper.getInstance().LogDetails("Socket newOnline called  " ,args[0].toString());
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
            try{
            Helper.getInstance().LogDetails("agentChatEnded AppSocket called"," "+args[0].toString());
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
            try{
                Helper.getInstance().LogDetails("getAgentChats AppSocket called"," "+args[0].toString());
                if(HandlerHolder.backgroundservice!=null){
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.SocketEvents.EVENT_GET_AGENT_CHATS,args[0]).sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private  Emitter.Listener agentLogoffEverywhere = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                //AGENT_LOGFF_EVERYWHERE
                Helper.getInstance().LogDetails("agentLogoffEverywhere AppSocket called"," "+args[0].toString());
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
            try{
                //AGENT_LOGFF_EVERYWHERE
                Helper.getInstance().LogDetails("pushAgentOffline AppSocket called"," "+args[0].toString());
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
            try{
                //AGENT_LOGFF_EVERYWHERE
                Helper.getInstance().LogDetails("agentTyping AppSocket called"," "+args[0].toString());
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

    private Emitter.Listener receiveMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat recive message call back","");
                if(HandlerHolder.backgroundservice!=null)
                {
                    HandlerHolder.backgroundservice.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,args[0]).sendToTarget();
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

    public void socketConnection() {
        try {
            if(mSocket!=null){

                connectSocket();

                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

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
                mSocket.on(SocketConstants.AGENT_LOGFF_EVERYWHERE,agentLogoffEverywhere);
                mSocket.on(SocketConstants.PUSH_AGENT_OFFLINE,pushAgentOffline);
                mSocket.on(SocketConstants.AGENT_TYPING,agentTyping);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    long mLastTimeInMills=0;

    private void connectSocket(){

        if(SystemClock.elapsedRealtime()-mLastTimeInMills<500){
            Helper.getInstance().LogDetails("AppSocket socket service connectSocket ","return called");
            return;
        }
        mLastTimeInMills=SystemClock.elapsedRealtime();
        if (!mSocket.connected()) {
            mSocket.connect();
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
                tmSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
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
    long mTmLastTimeInMills=0;
    private void connectTmSocket(){
        if(SystemClock.elapsedRealtime()-mTmLastTimeInMills<800){
            Helper.getInstance().LogDetails("AppSocket connectTmSocket ","return called");
            return;
        }
        mTmLastTimeInMills=SystemClock.elapsedRealtime();
        if (!tmSocket.connected()) {
            tmSocket.connect();
        }
    }

    public void socketDestroy() {
        try {
            if (mSocket != null) {



                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

                //mounika
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATED, agentStatusUpdated);
                mSocket.off(SocketConstants.USER_CHAT_ENDED, userChatEnded);
                mSocket.off(SocketConstants.AGENT_STATUS_UPDATE, agentStatusUpdate);
                mSocket.off(SocketConstants.CHAT_ENDED_AGENT, chatEndedAgent);
                mSocket.off(SocketConstants.NEW_ONLINE, newOnline);
                mSocket.off(SocketConstants.CHECK_AGENT_STATUS, checkAgentStatus);
                mSocket.off(SocketConstants.AGENT_STATUS_INFO, agentStatusInfo);
                mSocket.off(SocketConstants.USER_TYPING_TO_AGENT, userTypingToAgent);
                mSocket.off(SocketConstants.AGENT_REQUEST,agentRequest );
                mSocket.off(SocketConstants.CONTACT_UPDATE, contactUpdate);
                mSocket.off(SocketConstants.AGENT_ACTIVITY, agentActivity);
                mSocket.off(SocketConstants.AGENT_CHAT_ENDED, agentChatEnded);
                mSocket.off(SocketConstants.GET_AGENT_CHATS, getAgentChats);
                mSocket.off(SocketConstants.AGENT_LOGFF_EVERYWHERE,agentLogoffEverywhere);
                mSocket.off(SocketConstants.PUSH_AGENT_OFFLINE,pushAgentOffline);
                mSocket.off(SocketConstants.AGENT_TYPING,agentTyping);
                if(mSocket.connected()) {
                    mSocket.disconnect();
                    mSocket.close();
                }
                mSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void tmSocketDestroy(){
        try {

            if (tmSocket != null) {


                tmSocket.off(Socket.EVENT_CONNECT, onConnect);
                tmSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                tmSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                tmSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                tmSocket.off(SocketConstants.EVENT_SEND_MESSAGE, sendMessage);
                tmSocket.off(SocketConstants.EVENT_SEND_ATTACHMENT, sendAttachment);
                tmSocket.off(SocketConstants.EVENT_MESSAGE_DELIVERED, messageDelivered);
                tmSocket.off(SocketConstants.EVENT_TM_ERROR, tmError);
                tmSocket.off(SocketConstants.EVENT_SENT_MESSAGE, messageSent);
                tmSocket.off(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES, syncOfflineMessages);
                tmSocket.off(SocketConstants.EVENT_GET_MISSING_MESSAGES, getMissingMessages);
                tmSocket.off(SocketConstants.EVENT_ACCESS_TOKEN, accessToken);
                tmSocket.off(SocketConstants.EVENT_RECEIVE_MESSAGE, receiveMessage);
                tmSocket.off(SocketConstants.EVENT_CHANGE_USER_AVAILABILITY_STATUS, changeAvailabilityStatus);
                tmSocket.off(SocketConstants.EVENT_MESSAGE_READ_BY_ME, messageReadByMe);
                tmSocket.off(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS, getUserAvailabilityStatus);
                tmSocket.off(SocketConstants.EVENT_USER_AVAILABILITY_STATUS, userAvailabilityStatus);
                tmSocket.off(SocketConstants.EVENT_USER_OFFLINE, userOffline);
                tmSocket.off(SocketConstants.EVENT_USER_ONLINE, userOnline);
                tmSocket.off(SocketConstants.EVENT_GET_OFFLINE_MESSAGES, getOfflineMessages);
                if (tmSocket.connected()) {
                    tmSocket.disconnect();
                    tmSocket.close();
                }
                tmSocket = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
