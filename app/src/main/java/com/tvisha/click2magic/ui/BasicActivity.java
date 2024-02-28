package com.tvisha.click2magic.ui;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import io.socket.client.Socket;

public abstract class BasicActivity extends AppCompatActivity {

    public static String userId;
    public Handler socketHandler, tmSocketHandler;
    public static Socket mSocket, tmSocket;
    public static AppSocket appSocket;

    JobScheduler jobScheduler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{


        setContentView(setView());
        ButterKnife.bind(this);

            AppSocket.eventHandler=socketHandler;
            AppSocket.tmEventHandler = tmSocketHandler;

        userId =  Session.getUserID(BasicActivity.this);

        if (!Session.getLoginStatus(BasicActivity.this)) {
            Navigation.getInstance().openLoginPage(this);
            finish();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (Helper.getInstance().isMyServiceRunning(BackgroundServiceForOreo.class, this)) {
                    if (HandlerHolder.backgroundservice != null) {
                        HandlerHolder.backgroundservice.sendEmptyMessage(SocketConstants.TmSocketEvents.DISCONNECT_SOCKET);
                    }
                    jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    if (jobScheduler != null) {
                        jobScheduler.cancelAll();
                    }
                }
            } else {

                if (Helper.getInstance().isMyServiceRunning(SocketService.class, BasicActivity.this)) {
                    Helper.getInstance().LogDetails("Background===", "SocketService running");
                    Intent myService = new Intent(BasicActivity.this, SocketService.class);
                    stopService(myService);
                } else {
                    Helper.getInstance().LogDetails("Background===", "SocketService not running");
                }
            }


            initialiseHandler();
            initialiseTmHandler();
            socketConnection();
            tmSocketConnection();
            connectToSocket();
            connectToTmSocket();
            handleIntent();
            initViews();




        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        AppSocket.eventHandler=socketHandler;
        AppSocket.tmEventHandler=tmSocketHandler;
        super.onResume();
    }

    public abstract int setView();

    public abstract void socketOnConnect();

    public abstract void socketTmOnConnect();

    public abstract void socketOnDisconnect();

    public abstract void socketTmOnDisconnect();

    public abstract void connectToSocket();

    public abstract void connectToTmSocket();

    public abstract void handleIntent();

    public abstract void initViews();

    public void socketConnection() {
        try {

            Helper.getInstance().LogDetails("CHECK BaseActivity ","socketConnection called");

            if(appSocket==null)
            appSocket = (AppSocket) getApplication();

            mSocket=appSocket.getSocketInstance();
            if (mSocket == null) {
                appSocket.disconnectSocket();
                Helper.getInstance().LogDetails("CHECK BaseActivity socketConnection", "mSocket null");
                mSocket = appSocket.getSocket();
            }
            if (mSocket != null ) {
                if( !mSocket.connected())
                {
                    Helper.getInstance().LogDetails("CHECK BaseActivity socketConnection after", "mSocket not connected");
                    appSocket.connectSocket();

                }
                else
                {
                    Helper.getInstance().LogDetails("CHECK BaseActivity socketConnection after", "mSocket connected");
                }

            } else {
                Helper.getInstance().LogDetails("CHECK BaseActivity socketConnection after", "mSocket null--");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tmSocketConnection() {
        Helper.getInstance().LogDetails("CHECK BaseActivity ","tmSocketConnection called");

        try{
            if(appSocket==null)
                appSocket = (AppSocket) getApplication();

              tmSocket=appSocket.getTmSocketInstance();

        if (tmSocket == null) {
            Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection", "tmSocket null");
            appSocket.disconnectTmSocket();
            AppSocket.tmEventHandler=tmSocketHandler;
            tmSocket = appSocket.initTmSocket();
        }
        if (tmSocket != null) {
            if( !tmSocket.connected())
            {
                Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket not connected");
                appSocket.connectTmSocket();
            }
            else
            {
                Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket connected");
            }

        } else {
            Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket null");
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressLint("HandlerLeak")
    public void initialiseHandler() {
        if (socketHandler == null) {
            socketHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    try {
                        switch (msg.what) {
                            case SocketConstants.SocketEvents.EVENT_CONNECT:

                                socketOnConnect();
                                if (HandlerHolder.mainActivityUiHandler != null) {
                                    HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.SOCKETCONNECT).sendToTarget();
                                }

                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CONNECT).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CONNECT).sendToTarget();
                                }

                                break;

                            case SocketConstants.SocketEvents.EVENT_DISCONNECT:
                                socketOnDisconnect();
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_DISCONNECT).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_DISCONNECT).sendToTarget();
                                }
                                break;

                            case SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED:
                                Helper.getInstance().LogDetails("agentStatusUpdated baseactivity ", "called");
                                agentStatusUpdated((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED,(JSONObject) msg.obj).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED,(JSONObject) msg.obj).sendToTarget();
                                }

                                break;
                            case SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED:
                                userChatEnded((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED,(JSONObject) msg.obj).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT:
                                chatEndedAgent((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT,(JSONObject) msg.obj).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE:
                                contactUpdate((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE,(JSONObject) msg.obj).sendToTarget();
                                }if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY:
                                agentActivity((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY,(JSONObject) msg.obj).sendToTarget();
                                }
                                if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_NEW_ONLINE:
                                newOnline((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_NEW_ONLINE,(JSONObject) msg.obj).sendToTarget();
                                } if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_NEW_ONLINE,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;

                            case SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO:
                                agentStatusInfo((JSONArray) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO,(JSONArray) msg.obj).sendToTarget();
                                } if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO,(JSONArray) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT:
                                userTypingMessage((JSONObject) msg.obj);
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT,(JSONObject) msg.obj).sendToTarget();
                                }if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT,(JSONObject) msg.obj).sendToTarget();
                                }
                                break;
                            case SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED:

                                agentChatEnded((JSONObject) msg.obj);
                                JSONObject object=(JSONObject) msg.obj;
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED,object).sendToTarget();
                                }if(HandlerHolder.agentfragmentSocketHandler!=null){
                                    HandlerHolder.agentfragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED,object).sendToTarget();
                                }
                                break;

                            case SocketConstants.SocketEvents.EVENT_AGENT_LOGFF_EVERYWHERE:
                                Helper.getInstance().LogDetails("emitOffline EVENT_AGENT_LOGFF_EVERYWHERE baseactivity ", "called");
                                if(HandlerHolder.fragmentSocketHandler!=null){
                                    HandlerHolder.fragmentSocketHandler.obtainMessage(SocketConstants.SocketEvents.EVENT_AGENT_LOGFF_EVERYWHERE).sendToTarget();
                                }
                              //  logOffEverywhere();
                                break;




                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    protected abstract void userTypingMessage(JSONObject obj);

    protected abstract void agentChatEnded(JSONObject obj);

    protected abstract void chatEndedAgent(JSONObject obj);


    protected abstract void contactUpdate(JSONObject obj);
    protected abstract void agentActivity(JSONObject obj);
    //protected abstract void logOffEverywhere();



    @SuppressLint("HandlerLeak")
    public void initialiseTmHandler() {
        if (tmSocketHandler == null) {
            tmSocketHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    try {
                        switch (msg.what) {
                            case SocketConstants.TmSocketEvents.SOCKET_CONNECT:
                                if(HandlerHolder.fragmentTmSocketHandler!=null){
                                    HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CONNECT).sendToTarget();
                                }
                                socketTmOnConnect();
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_DISCONNECT:
                                if(HandlerHolder.fragmentTmSocketHandler!=null){
                                    HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_DISCONNECT).sendToTarget();
                                }
                                socketOnDisconnect();
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT:
                                JSONObject object = (JSONObject) msg.obj;
                                if (object != null) {

                                    messageSent(object);
                                    if (HandlerHolder.mainActivityUiHandler != null) {
                                        HandlerHolder.mainActivityUiHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT, object).sendToTarget();
                                    }

                                    if(HandlerHolder.fragmentTmSocketHandler!=null){
                                        HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT,object).sendToTarget();
                                    }

                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE:
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {


                                        messageReceived(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,jsonObject).sendToTarget();
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


                                        messageDelivered(jsonObject);

                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED,jsonObject).sendToTarget();
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


                                        messageRead(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ,jsonObject).sendToTarget();
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

                                        syncOfflineMessages();
                                        //  SyncData.syncNonReadMessage(BasicActivity.this, Integer.parseInt(receiver_tm_user_id),Integer.parseInt(tmUserId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;


                            case SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES:
                                try {
                                    JSONArray jsonArray = (JSONArray) msg.obj;
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        getMissingMessages();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    if (jsonObject != null && jsonObject.length() > 0) {
                                        messageReadByMe(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME,jsonObject).sendToTarget();
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

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userAvailabilityStatus(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS,jsonObject).sendToTarget();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userOffline(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE,jsonObject).sendToTarget();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userOnline(jsonObject);
                                        if(HandlerHolder.fragmentTmSocketHandler!=null){
                                            HandlerHolder.fragmentTmSocketHandler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE,jsonObject).sendToTarget();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;


                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            };
        }
    }

    protected abstract void syncOfflineMessages();

    protected abstract void getMissingMessages();


    protected abstract void agentStatusUpdated(JSONObject jsonObject);

    protected abstract void userChatEnded(JSONObject jsonObject);

    protected abstract void newOnline(JSONObject jsonObject);

    protected abstract void agentStatusInfo(JSONArray jsonObject);


    public abstract void messageSent(JSONObject object);

    public abstract void messageReceived(JSONObject object);

    public abstract void messageDelivered(JSONObject object);

    public abstract void messageRead(JSONObject object);

    public abstract void messageReadByMe(JSONObject object);

    public abstract void userAvailabilityStatus(JSONObject object);


    public abstract void userOffline(JSONObject object);
    public abstract void userOnline(JSONObject object);



}
