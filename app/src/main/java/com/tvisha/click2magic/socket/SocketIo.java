package com.tvisha.click2magic.socket;

import static java.util.Collections.singletonMap;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.constants.ApiEndPoint;

import org.json.JSONArray;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;


public class SocketIo {


    private static final SocketIo ourInstance = new SocketIo();
    private Context context;

    public static SocketIo getInstance() {
        return ourInstance;
    }

    String tmUserId="",tmToken="",platform="";
    boolean isSelf=true,accessTokenUpdated=false;
    public static android.os.Handler handler;
    static Socket socket;
    private SocketIo() {
    }

    public Socket getSocket(){
        return socket;
    }

    public Socket connectSocket(){
      try{
          if(socket == null){
               isSelf=Session.getIsSelf(context);

               if(isSelf){
                   tmUserId= Session.getTmUserId(context);
               }
               else{
                   tmUserId=Session.getOtherUserTmUserId(context);
               }

               tmToken =ApiEndPoint.TM_SERVER_SOCKET_TOKEN;
               platform="2";
              Helper.getInstance().LogDetails("socket params:token",tmToken+"tmUserId "+tmUserId);
              IO.Options opts = new IO.Options();
              opts.forceNew = true;
              opts.transports = new String[]{WebSocket.NAME};
//              opts.auth = singletonMap("access_token", tmToken);
              opts.query = "user_id="+tmUserId+"&token="+tmToken+"&platform="+platform;
              opts.reconnection = true;
              socket = IO.socket(ApiEndPoint.TM_SERVER_SOCKET_PATH,opts);

              initSocketListener();
          }
          else
          {
              connectTmSocket();
          }

      }catch (Exception e){
          e.printStackTrace();
      }
      return socket;
    }

    public void connectTmSocket(){
        if(socket!=null && !socket.connected()){
            socket.connect();
        }
    }




    public Boolean isSocketConnected(){
        Boolean status = false;
        if(socket != null){
            status = socket.connected();
        }
        return status;
    }

    public Boolean isAccessToken(){
        return accessTokenUpdated;
    }

    public void initSocketListener(){
        if(socket!=null) {

           connectTmSocket();

            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on(SocketConstants.EVENT_SEND_MESSAGE, sendMessage);
            socket.on(SocketConstants.EVENT_SEND_ATTACHMENT, sendAttachment);
            socket.on(SocketConstants.EVENT_MESSAGE_DELIVERED, messageDelivered);
            socket.on(SocketConstants.EVENT_MESSAGE_READ, messageRead);
            socket.on(SocketConstants.EVENT_TM_ERROR, tmError);
            socket.on(SocketConstants.EVENT_SENT_MESSAGE, messageSent);
            socket.on(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES, syncOfflineMessages);
            socket.on(SocketConstants.EVENT_GET_MISSING_MESSAGES, getMissingMessages);
            socket.on(SocketConstants.EVENT_ACCESS_TOKEN, accessToken);
            socket.on(SocketConstants.EVENT_RECEIVE_MESSAGE, receiveMessage);
            socket.on(SocketConstants.EVENT_CHANGE_USER_AVAILABILITY_STATUS, changeAvailabilityStatus);
            socket.on(SocketConstants.EVENT_MESSAGE_READ_BY_ME, messageReadByMe);
            socket.on(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS, getUserAvailabilityStatus);
            socket.on(SocketConstants.EVENT_USER_AVAILABILITY_STATUS, userAvailabilityStatus);
            socket.on(SocketConstants.EVENT_USER_OFFLINE, userOffline);
            socket.on(SocketConstants.EVENT_USER_ONLINE, userOnline);
            socket.on(SocketConstants.EVENT_GET_OFFLINE_MESSAGES, getOfflineMessages);
        }

    }


    public void removeSocketListener(){

        if(socket!=null){
            socket.off(Socket.EVENT_CONNECT,onConnect);
            socket.off(Socket.EVENT_DISCONNECT,onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.off(SocketConstants.EVENT_SEND_MESSAGE, sendMessage);
            socket.off(SocketConstants.EVENT_MESSAGE_DELIVERED, messageDelivered);
            socket.off(SocketConstants.EVENT_MESSAGE_READ, messageRead);
            socket.off(SocketConstants.EVENT_TM_ERROR, tmError);
            socket.off(SocketConstants.EVENT_SENT_MESSAGE,messageSent);
            socket.off(SocketConstants.EVENT_SYNC_OFFLINE_MESSAGES, syncOfflineMessages);
            socket.off(SocketConstants.EVENT_GET_MISSING_MESSAGES, getMissingMessages);
            socket.off(SocketConstants.EVENT_ACCESS_TOKEN,accessToken);
            socket.off(SocketConstants.EVENT_RECEIVE_MESSAGE,receiveMessage);
            socket.off(SocketConstants.EVENT_CHANGE_USER_AVAILABILITY_STATUS,changeAvailabilityStatus);
            socket.off(SocketConstants.EVENT_MESSAGE_READ_BY_ME,messageReadByMe);
            socket.off(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS,getUserAvailabilityStatus);
            socket.off(SocketConstants.EVENT_USER_AVAILABILITY_STATUS,userAvailabilityStatus);
            socket.off(SocketConstants.EVENT_USER_OFFLINE,userOffline);
            socket.off(SocketConstants.EVENT_USER_ONLINE,userOnline);
            socket.off(SocketConstants.EVENT_GET_OFFLINE_MESSAGES, getOfflineMessages);
            accessTokenUpdated=false;


                socket.disconnect();
                socket.close();
                socket = null;
                //handler=null;

        }




    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("socket connect tm","");
            if(handler!=null){
                handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CONNECT).sendToTarget();
            }

            if(socket!=null){
                String socketId=socket.id();
                if(socketId!=null)
                {
                    Helper.getInstance().LogDetails("socket params:","socket id "+socketId);
                    Session.saveSocketId(context,socketId);
                }
                else
                {
                    Helper.getInstance().LogDetails("socket params:", "socket id null ");
                }
            }



        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Helper.getInstance().LogDetails("socket_disconnect tm","");
            if(handler!=null)
            {
                handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_DISCONNECT).sendToTarget();
            }

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("ganga", new Gson().toJson(args));
            Helper.getInstance().LogDetails("socket_connect error tm",args[0].toString());
        }
    };

    private Emitter.Listener accessToken = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat access token call back socketIo",args[0]+"");
                if(handler!=null)
                {
                    accessTokenUpdated=true;
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_ACESS_TOKEN,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat changeAvailabilityStatus call back socketIo ",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };

    private Emitter.Listener messageReadByMe = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Helper.getInstance().LogDetails("chat messageReadByMe call back socketIo ",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME,args[0]).sendToTarget();
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
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("checkUserStatus chat userAvailabilityStatus call back socketIo ",args[0].toString());

                /*{"online":[5771,5927,5931,5936,5938,5765,5918,5906,3715,5604,3753,100],"dnd":[],
                    "online_reference":{"100":[""],"3715":[""],"3753":[""],"5604":[""],"5765":[""],
                    "5771":[""],"5906":[""],"5918":[""],"5927":[""],"5931":["13d560409827c049a3"],
                    "5936":["98505844fd6a53d555"],"5938":["bd26f4620b375a19f"]}}*/
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS,args[0]).sendToTarget();
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

                //{"user_id":"827","reference_token":""}
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE,args[0]).sendToTarget();
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

                //{"user_id":"827","reference_token":""}
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE,args[0]).sendToTarget();
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
              /*  {"sender_id":"2","receiver_id":"386","message_type":0,"message_id":12264,"created_at":"2019-04-10 20:25:50","reference_id":"",
                        "is_group":0,"is_reply":0,"message":"bgfbhgfhgj","conversation_reference_id":"952dd9b5a501c1e569"}*/
                Helper.getInstance().LogDetails("chat message sent call back socketIo",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.getMessage();
            }
        }
    };

    private Emitter.Listener sendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("chat send message call back socketIo",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat send message call back socketIo",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SEND_ATTACHMENT,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat syncOfflineMessages call back socketIo",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener getMissingMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                 Helper.getInstance().LogDetails("getMissingMessages call back socketIo",args[0].toString());
                JSONArray jsonArray = (JSONArray) args[0];
                 if(handler!=null)
                 {
                     handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES,args[0]).sendToTarget();
                 }


            }catch (Exception e){
                e.printStackTrace();
            }
        }


    };

    private Emitter.Listener getOfflineMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Helper.getInstance().LogDetails("getOfflineMessages call back socketIo",args[0].toString());
                JSONArray jsonArray = (JSONArray) args[0];
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_GET_OFFLINE_MESSAGES,args[0]).sendToTarget();
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
                 Helper.getInstance().LogDetails("chat message delivered call back socketIo",args[0].toString());
                 if(handler!=null){
                     handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat message read call back socketIo",args[0].toString());
                if(handler!=null){
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat recive message call back socketIo",args[0].toString());
                if(handler!=null){
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE,args[0]).sendToTarget();
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
                Helper.getInstance().LogDetails("chat error===> socketIo",args[0].toString());
                if(handler!=null)
                {
                    handler.obtainMessage(SocketConstants.TmSocketEvents.SOCKET_TM_ERROR,args[0]).sendToTarget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public void setHandlers(android.os.Handler socketHandler) {
        handler = socketHandler;
    }

    public void setContext(Context applicationContext) {
        context = applicationContext;
    }
}
