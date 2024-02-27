package com.tvisha.click2magic.Helper;

//NotificationHelperStore




import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.text.SpannableString;


import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.ui.ChatActivity;
import com.tvisha.click2magic.ui.HomeActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;



public class NotificationHelperStore {
    private static NotificationHelperStore ourInstance = new NotificationHelperStore();
    public static final String CHANNEL_ID = "com.tvisha.click2magic";
    public static final String CHANNEL_NAME = "click2chat";
    Context context;
    public static NotificationHelperStore getInstance() {
        if (ourInstance == null) {
            ourInstance = new NotificationHelperStore();
        }
        return ourInstance;
    }

    private NotificationHelperStore() {

    }



    public void sendTmNotification(Context context, String messageTitle, String messageBody,JSONObject obj) {

/*        {"receiver_id":"2","attachment":"https:\/\/s3.amazonaws.com\/files.c2m\/user\/31052019123419\/download (1).jpeg",
                "google.delivered_priority":"high","message_id":"18579",
                "google.sent_time":1559286261081,"google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.e":"1","msg_type":"1","conversation_reference_id":"0c2b02f617977cce5",
                "attachment_name":"download (1).jpeg","attachment_extension":"jpeg","gcm.notification.sound":"default",
                "gcm.notification.title":"mrr","from":"987943255713","sender_id":"1501",
            :"default","is_group":"0","is_reply":"0","google.message_id":"0:1559286261091666%ce538a87ce538a87",
                "gcm.notification.body":"sent you a attachment","sender_name":"mrr","google.c.a.e":"1",
                "created_at":"2019-05-31 12:34:20","collapse_key":"com.tvisha.click2magic"}*/

        try {
            String userName = "", tmUserId = "",sender_id="";
            ActiveChat activeChat =null;
            Intent notificationIntent = null;


            boolean isLogin=Session.getLoginStatus(context);
            boolean isMain = true;


            if(Helper.getInstance().isAppForground(context))
            {

            }
            else
            {


                SyncData.addMessageFromNotification(context,obj);
            }


            StringBuilder userNewBuilder = new StringBuilder();
            int messageType=obj.optInt("msg_type");
            if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
            {

                String attachmentType=obj.optString("attachment_extension");
                String msg="";
                if(attachmentType!=null && !attachmentType.trim().isEmpty())
                {
                    if(attachmentType.toLowerCase().equals("png") ||attachmentType.toLowerCase().equals("jpg")  || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") ||attachmentType.toLowerCase().equals("gif") )
                    {
                        msg = "\uD83D\uDCF8 image";

                    }
                    else
                    {
                        msg = "\uD83D\uDCCE attachment";
                    }
                }
                else
                {
                    msg = "\uD83D\uDCCE attachment";
                }
                SpannableString sb = new SpannableString(msg);
                userNewBuilder.append(sb);

            }

            if(isLogin){


                try {
                    sender_id=obj.optString("sender_id");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<SitesInfo> list=  Session.getSiteInfoList(context,Session.SP_SITE_INFO);



                activeChat = getActiveUserData(sender_id,list);





                tmUserId= Session.getTmUserId(context);


                notificationIntent = new Intent(context, ChatActivity.class);
                notificationIntent.putExtra(Values.Notification.single_notification, true);
                notificationIntent.putExtra(Values.Notification.notification, true);
                notificationIntent.putExtra(Values.Notification.fcmnotification, true);
                notificationIntent.putExtra(Values.Notification.tm_user_id, tmUserId);
                notificationIntent.putExtra(Values.Notification.user_name, userName);
                notificationIntent.putExtra(Values.Notification.userData, activeChat);
                notificationIntent.putExtra(Values.Notification.receiver_id, sender_id);
                notificationIntent.putExtra(Values.MyActions.NOTIFICATION, true);
                notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, PendingIntent.FLAG_ONE_SHOT);

                Notification.Builder notificationBuilder = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createChannels(context);
                    notificationBuilder = new Notification.Builder(context, CHANNEL_ID);
                    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                    notificationBuilder.setContentTitle(messageTitle);
                    //inboxStyle.setBigContentTitle(messageTitle);
                    // inboxStyle.setSummaryText(messageTitle);
                    if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                    {
                        notificationBuilder.setContentText(userNewBuilder);
                        inboxStyle.addLine(userNewBuilder);
                    }
                    else
                    {
                        notificationBuilder.setContentText(messageBody);
                        inboxStyle.addLine(messageBody);
                    }

                    notificationBuilder.setStyle(inboxStyle);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notificationBuilder = new Notification.Builder(context);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        }


                        notificationBuilder.setContentTitle(messageTitle);
                        if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                        {

                            notificationBuilder.setContentText(userNewBuilder);
                        }
                        else
                        {

                            notificationBuilder.setContentText(messageBody);
                        }

                    }
                }
                PendingIntent single_pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);



                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                notificationBuilder.setSmallIcon(getSmallNotificationIcon());
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
                //notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),getNotificationLargeIcon()));




                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setWhen(System.currentTimeMillis());
                notificationBuilder.setShowWhen(true);
                notificationBuilder.setOnlyAlertOnce(true);
                notificationBuilder.setContentIntent(single_pendingIntent);
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                notificationBuilder.setPriority(Notification.PRIORITY_MAX);

                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build();
                        notificationBuilder.setSound(defaultSoundUri,audioAttributes);
                    }
                    else
                    {
                        notificationBuilder.setSound(defaultSoundUri);
                    }

                }
                else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {

                    if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                    {

                    }
                    else
                    {
                        Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        v1.vibrate(100);

                    }

                }
                // notificationBuilder.setSound(defaultSoundUri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }

                long currentTime = System.currentTimeMillis();
                int notificationId = (int) currentTime;


                if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                {
                    return;
                }
                else
                {
                    if(sender_id!=null && !sender_id.equals(tmUserId))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(notificationId, notificationBuilder.build());


                        }
                        else
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(notificationId, notificationBuilder.build());
                            }
                        }

                    }

                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }





    public void sendC2mNotification(final Context context, String messageTitle, String messageBody,int notification_type, String data, JSONObject object) {


/*
        {"subtitle":"DOOOOo","google.delivered_priority":"high",
                "google.sent_time":1559370348496,"smallIcon":"small_icon",
                "notification_type":"1","google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.content_available":"true",
                "data":"{\"chat_status\":1,\"visitor_browser\":\"Chrome (74.0.3729.169)\"," +
                "\"visitor_query\":\"asfasfasdf\",\"guest_name\":\"DOOOOo\",\"agent_id\":\"18\"," +
                "\"chat_rating\":\"\",\"visitor_id\":\"732\",\"end_time\":\"0000-00-00 00:00:00\"," +
                "\"created_at\":\"2019-06-01 11:54:24\",\"visitor_url\":\"http:\\\/\\\/192.168.1.10\\\/thankyou.php\"," +
                "\"chat_id\":\"1417\",\"start_time\":\"2019-06-01 11:55:48\",\"visitor_os\":\"Macintosh (macOS Mojave)\"," +
                "\"account_id\":\"1\",\"updated_at\":\"2019-06-01 11:54:24\",\"chat_reference_id\":\"17f4c7c38512a22270\"," +
                "\"site_id\":\"1\",\"location\":\"1\",\"visitor_ip\":\"192.168.7.148\",\"status\":\"1\"}","from":"987943255713",
                "sound":"1","title":"DOOOOo","vibrate":"1","google.message_id":"0:1559370348500806%ce538a87f9fd7ecd","largeIcon":"large_icon",
                "message":"New User Assigned","tickerText":"Click2Magic"}*/


        try {
            String userName = "", tmUserId = "";
            ActiveChat activeChat = new ActiveChat();
            Intent notificationIntent = null;
            boolean isMain = true;

            try {
                final JSONObject obj = new JSONObject(data);

                activeChat = setUserData(obj);
                userName = obj.optString("guest_name");
                tmUserId = obj.optString("tm_visitor_id");

                if (notification_type==Values.NotificationType.CHAT_ENDED_BY_USER) {

                    isMain = true;
                    List<SitesInfo> list=  Session.getSiteInfoList(context,Session.SP_SITE_INFO);
                    if(list!=null && list.size()>0){
                        updateUserChatEnded(obj,list);
                    }

                } else {
                    isMain = false;
                    List<SitesInfo> list=  Session.getSiteInfoList(context,Session.SP_SITE_INFO);
                    if(list!=null && list.size()>0){
                        updateNewOnlineUser(context,obj,list);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }





            boolean isLogin=Session.getLoginStatus(context);

            if(isLogin)
            {

                if (isMain) {
                    notificationIntent = new Intent(context, HomeActivity.class);
                } else {
                    notificationIntent = new Intent(context, ChatActivity.class);
                }

                notificationIntent.putExtra(Values.Notification.single_notification, true);
                notificationIntent.putExtra(Values.Notification.notification, true);
                notificationIntent.putExtra(Values.Notification.fcmnotification, true);
                notificationIntent.putExtra(Values.Notification.tm_user_id, tmUserId);
                notificationIntent.putExtra(Values.Notification.user_name, userName);
                notificationIntent.putExtra(Values.Notification.userData, activeChat);
                notificationIntent.putExtra(Values.Notification.receiver_id, activeChat.getTmVisitorId());
                notificationIntent.putExtra(Values.MyActions.NOTIFICATION, true);
                notificationIntent.setAction(Long.toString(System.currentTimeMillis()));

                PendingIntent single_pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                Uri defaultSoundUri=null;
                if (messageBody.equals("Chat ended By user")) {
                    defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    // defaultSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
                }
                else
                {
                    if(!Helper.getInstance().isAppForground(context))
                    {

                        defaultSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/"
                                + R.raw.ringtone);
                        Ringtone r = RingtoneManager.getRingtone(context, defaultSoundUri);
                        r.play();

                    }
                    else
                    {
                       /* defaultSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/"
                                + R.raw.ringtone);*/

                        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    }

                }

                Notification.Builder notificationBuilder = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createChannels(context);
                    notificationBuilder = new Notification.Builder(context, CHANNEL_ID);
                    // notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
                    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                    //inboxStyle.setBigContentTitle(messageTitle);
                    notificationBuilder.setContentTitle(messageTitle);
                    notificationBuilder.setContentText(messageBody);
                    inboxStyle.addLine(messageBody);
                    notificationBuilder.setStyle(inboxStyle);
                    notificationBuilder.setChannelId(CHANNEL_ID);

                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notificationBuilder = new Notification.Builder(context);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        }

                        notificationBuilder.setContentTitle(messageTitle);
                        notificationBuilder.setContentText(messageBody);

                    }
                }


                notificationBuilder.setSmallIcon(getSmallNotificationIcon());
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setWhen(System.currentTimeMillis());
                notificationBuilder.setShowWhen(true);
                notificationBuilder.setOnlyAlertOnce(true);
                notificationBuilder.setContentIntent(single_pendingIntent);
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                notificationBuilder.setPriority(Notification.PRIORITY_MAX);

                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AudioAttributes attributes=null;
                        attributes  = new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                .build();

                        notificationBuilder.setSound(defaultSoundUri,attributes);

                    }
                    else
                    {
                        notificationBuilder.setSound(defaultSoundUri);
                    }
                }
                else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {

                    Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v1.vibrate(100);
                }




                long currentTime = System.currentTimeMillis();
                final int random = new Random().nextInt(61) + 20;
                int notificationId = (int) currentTime +random;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(notificationId, notificationBuilder.build());

                }
                else
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(notificationId, notificationBuilder.build());
                    }
                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }

    }


    private ActiveChat getActiveUserData(String sender_id, List<SitesInfo> sitesInfoList) {
        if(sender_id!=null ){

            if(sitesInfoList !=null && sitesInfoList.size()>0){
                for(int i = 0; i<sitesInfoList.size(); i++){
                    List<ActiveChat> activeChatList=sitesInfoList.get(i).getActiveChats();
                    if(activeChatList!=null && activeChatList.size()>0){
                        for(int j=0;j<activeChatList.size();j++){
                            ActiveChat activeChat =activeChatList.get(j);
                            if(activeChat !=null && activeChat.getTmVisitorId()!=null){
                                if(sender_id.equals(activeChat.getTmVisitorId()))
                                {

                                    return activeChat;
                                }
                            }
                        }
                    }

                }
            }
        }
        return null;
    }






    public void clearNotifications(Context context) {
        try {
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nMgr != null) {
                nMgr.cancelAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long mLastClickTimeInMills = 0;


    private long getMessageTime(String date) throws ParseException {
        Date d = null;
        try {
            if (date != null && !date.equals("null") && !date.isEmpty() && date.contains(".")) {
                d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(date);
            } else {
                d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            }
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }




    private int getSmallNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        // return useWhiteIcon ? R.drawable.ic_firebase_smallnotification : R.drawable.ic_firebase_smallnotification;
        return useWhiteIcon ? R.drawable.logo_icon : R.drawable.ic_firebase_smallnotification;

      /*  Notification notification = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.icon_transperent);
            notification.setColor(getResources().getColor(R.color.notification_color));
        } else {
            notification.setSmallIcon(R.drawable.icon);
        }*/

    }








    NotificationManager notificationManager;

    public NotificationManager getManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }


    @RequiresApi(api = 26)
    public void createChannels(Context ctx) {

        NotificationChannel message_notification = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        message_notification.enableLights(true);
        message_notification.setLightColor(Color.RED);
        message_notification.setShowBadge(true);
        message_notification.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager(ctx).createNotificationChannel(message_notification);


    }




    private void updateUserChatEnded(JSONObject jsonObject, List<SitesInfo> sitesInfoList) {

     /*
        {"subtitle":"DOOOOo","google.delivered_priority":"high",
                "google.sent_time":1559370348496,"smallIcon":"small_icon",
                "notification_type":"1","google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.content_available":"true",
                "data":"{\"chat_status\":1,\"visitor_browser\":\"Chrome (74.0.3729.169)\"," +
                "\"visitor_query\":\"asfasfasdf\",\"guest_name\":\"DOOOOo\",\"agent_id\":\"18\"," +
                "\"chat_rating\":\"\",\"visitor_id\":\"732\",\"end_time\":\"0000-00-00 00:00:00\"," +
                "\"created_at\":\"2019-06-01 11:54:24\",\"visitor_url\":\"http:\\\/\\\/192.168.1.10\\\/thankyou.php\"," +
                "\"chat_id\":\"1417\",\"start_time\":\"2019-06-01 11:55:48\",\"visitor_os\":\"Macintosh (macOS Mojave)\"," +
                "\"account_id\":\"1\",\"updated_at\":\"2019-06-01 11:54:24\",\"chat_reference_id\":\"17f4c7c38512a22270\"," +
                "\"site_id\":\"1\",\"location\":\"1\",\"visitor_ip\":\"192.168.7.148\",\"status\":\"1\"}","from":"987943255713",
                "sound":"1","title":"DOOOOo","vibrate":"1","google.message_id":"0:1559370348500806%ce538a87f9fd7ecd","largeIcon":"large_icon",
                "message":"New User Assigned","tickerText":"Click2Magic"}*/


        if(jsonObject!=null)
        {
            String site_id=jsonObject.optString("site_id");

            if(sitesInfoList !=null && sitesInfoList.size()>0){
                for(int i = 0; i< sitesInfoList.size(); i++){
                    String sid=sitesInfoList.get(i).getSiteId();
                    if(site_id!=null && sid!=null && site_id.equals(sid))
                    {
                        if(sitesInfoList.get(i).getActiveChats()!=null)
                        {
                            for(int j=0;j<sitesInfoList.get(i).getActiveChats().size();j++){
                                if(sitesInfoList.get(i).getActiveChats().get(j).getChatReferenceId()!=null && !sitesInfoList.get(i).getActiveChats().get(j).getChatReferenceId().isEmpty() && jsonObject.optString("chat_reference_id")!=null && !jsonObject.optString("chat_reference_id").isEmpty() )
                                {
                                    if(jsonObject.optString("chat_reference_id").equals(sitesInfoList.get(i).getActiveChats().get(j).getChatReferenceId()))
                                    {
                                        sitesInfoList.get(i).getActiveChats().remove(j);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }



                }
            }

        }


    }

    private ActiveChat setUserData(JSONObject obj){


/*
        {"subtitle":"DOOOOo","google.delivered_priority":"high",
                "google.sent_time":1559370348496,"smallIcon":"small_icon",
                "notification_type":"1","google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.content_available":"true",
                "data":"{\"chat_status\":1,\"visitor_browser\":\"Chrome (74.0.3729.169)\"," +
                "\"visitor_query\":\"asfasfasdf\",\"guest_name\":\"DOOOOo\",\"agent_id\":\"18\"," +
                "\"chat_rating\":\"\",\"visitor_id\":\"732\",\"end_time\":\"0000-00-00 00:00:00\"," +
                "\"created_at\":\"2019-06-01 11:54:24\",\"visitor_url\":\"http:\\\/\\\/192.168.1.10\\\/thankyou.php\"," +
                "\"chat_id\":\"1417\",\"start_time\":\"2019-06-01 11:55:48\",\"visitor_os\":\"Macintosh (macOS Mojave)\"," +
                "\"account_id\":\"1\",\"updated_at\":\"2019-06-01 11:54:24\",\"chat_reference_id\":\"17f4c7c38512a22270\"," +
                "\"site_id\":\"1\",\"location\":\"1\",\"visitor_ip\":\"192.168.7.148\",\"status\":\"1\"}","from":"987943255713",
                "sound":"1","title":"DOOOOo","vibrate":"1","google.message_id":"0:1559370348500806%ce538a87f9fd7ecd","largeIcon":"large_icon",
                "message":"New User Assigned","tickerText":"Click2Magic"}*/

        ActiveChat activeChat =new ActiveChat();

        if(obj!=null)
        {
            activeChat.setChatStatus(obj.optString("chat_status"));
            activeChat.setVisitorBrowser(obj.optString("visitor_browser"));
            activeChat.setAgentId(obj.optString("agent_id"));
            activeChat.setName(obj.optString("name"));
            activeChat.setVisitorId(obj.optString("visitor_id"));
            activeChat.setVisitorOs(obj.optString("visitor_os"));
            activeChat.setChatReferenceId(obj.optString("chat_reference_id"));
            activeChat.setVisitorIp(obj.optString("visitor_ip"));
            activeChat.setEmail(obj.optString("email"));
            activeChat.setVisitCount(obj.optString("visit_count"));
            activeChat.setVisitorQuery(obj.optString("visitor_query"));
            activeChat.setGuestName(obj.optString("guest_name"));
            activeChat.setChatRating(obj.optString("chat_rating"));
            activeChat.setEndTime(obj.optString("end_time"));
            activeChat.setMobile(obj.optString("mobile"));
            activeChat.setChatId(obj.optString("chat_id"));
            activeChat.setStartTime(obj.optString("start_time"));
            activeChat.setAccountId(obj.optString("account_id"));
            activeChat.setTmVisitorId(obj.optString("tm_visitor_id"));
            activeChat.setSiteId(obj.optString("site_id"));
            activeChat.setLocation(obj.optString("location"));
            activeChat.setStatus(obj.optString("status"));
        }

        return activeChat;
    }

    private void updateNewOnlineUser(Context context, JSONObject jsonObject, List<SitesInfo> sitesInfoList) {

        /*
        {"subtitle":"DOOOOo","google.delivered_priority":"high",
                "google.sent_time":1559370348496,"smallIcon":"small_icon",
                "notification_type":"1","google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.content_available":"true",
                "data":"{\"chat_status\":1,\"visitor_browser\":\"Chrome (74.0.3729.169)\"," +
                "\"visitor_query\":\"asfasfasdf\",\"guest_name\":\"DOOOOo\",\"agent_id\":\"18\"," +
                "\"chat_rating\":\"\",\"visitor_id\":\"732\",\"end_time\":\"0000-00-00 00:00:00\"," +
                "\"created_at\":\"2019-06-01 11:54:24\",\"visitor_url\":\"http:\\\/\\\/192.168.1.10\\\/thankyou.php\"," +
                "\"chat_id\":\"1417\",\"start_time\":\"2019-06-01 11:55:48\",\"visitor_os\":\"Macintosh (macOS Mojave)\"," +
                "\"account_id\":\"1\",\"updated_at\":\"2019-06-01 11:54:24\",\"chat_reference_id\":\"17f4c7c38512a22270\"," +
                "\"site_id\":\"1\",\"location\":\"1\",\"visitor_ip\":\"192.168.7.148\",\"status\":\"1\"}","from":"987943255713",
                "sound":"1","title":"DOOOOo","vibrate":"1","google.message_id":"0:1559370348500806%ce538a87f9fd7ecd","largeIcon":"large_icon",
                "message":"New User Assigned","tickerText":"Click2Magic"}*/

        if(jsonObject!=null)
        {
            boolean isPresent=false;
            int position=-1;

            if(sitesInfoList !=null && sitesInfoList.size()>0){
                String chat_id ,tm_visitor_id,site_id;
                chat_id=jsonObject.optString("chat_id");
                tm_visitor_id=jsonObject.optString("tm_visitor_id");
                site_id=jsonObject.optString("site_id");

                if(tm_visitor_id!=null && !tm_visitor_id.isEmpty())
                {
                    if(sitesInfoList !=null && sitesInfoList.size()>0)
                    {

                        for(int i = 0; i<sitesInfoList.size(); i++){
                            String sid=sitesInfoList.get(i).getSiteId();
                            if(site_id!=null && sid!=null && site_id.equals(sid))
                            {
                                position=i;
                                if(sitesInfoList.get(i).getActiveChats()!=null){
                                    for(int j=0;j<sitesInfoList.get(i).getActiveChats().size();j++){
                                        if(sitesInfoList.get(i).getActiveChats().get(j).getTmVisitorId()!=null && !sitesInfoList.get(i).getActiveChats().get(j).getTmVisitorId().trim().isEmpty() && tm_visitor_id.equals(sitesInfoList.get(i).getActiveChats().get(j).getTmVisitorId()))
                                        {
                                            isPresent=true;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }


                        }
                    }
                    else
                    {
                        isPresent=false;
                    }

                }
                if(!isPresent)
                {
                    ActiveChat activeChat =setUserData(jsonObject);
                    if(activeChat !=null){

                        if(position!=-1 && sitesInfoList.get(position).getActiveChats()!=null)
                        {
                            sitesInfoList.get(position).getActiveChats().add(0, activeChat);
                        }
                        else
                        {
                            List<ActiveChat> l=new ArrayList<>();
                            l.add(activeChat);
                            sitesInfoList.get(position).setActiveChats(l);
                        }
                        Session.saveSiteInfoList(context,sitesInfoList,Session.SP_SITE_INFO);
                    }

                }
            }


        }

    }


    public void sendTmNotificationTest(Context context, String messageTitle, String messageBody,JSONObject obj) {

     /*   {"receiver_id":"2","attachment":"https:\/\/s3.amazonaws.com\/files.c2m\/user\/31052019123419\/download (1).jpeg",
                "google.delivered_priority":"high","message_id":"18579",
                "google.sent_time":1559286261081,"google.ttl":2419200,"google.original_priority":"high",
                "gcm.notification.e":"1","msg_type":"1","conversation_reference_id":"0c2b02f617977cce5",
                "attachment_name":"download (1).jpeg","attachment_extension":"jpeg","gcm.notification.sound":"default",
                "gcm.notification.title":"mrr","from":"987943255713","sender_id":"1501",
            :"default","is_group":"0","is_reply":"0","google.message_id":"0:1559286261091666%ce538a87ce538a87",
                "gcm.notification.body":"sent you a attachment","sender_name":"mrr","google.c.a.e":"1",
                "created_at":"2019-05-31 12:34:20","collapse_key":"com.tvisha.click2magic"}*/

        try {
            String userName = "", tmUserId = "",sender_id="";
            ActiveChat activeChat =null;
            Intent notificationIntent = null;

            boolean isLogin=Session.getLoginStatus(context);
            boolean isMain = true;


            if(Helper.getInstance().isAppForground(context))
            {

            }
            else
            {


                SyncData.addMessageFromNotification(context,obj);
            }


            StringBuilder userNewBuilder = new StringBuilder();
            int messageType=obj.optInt("msg_type");
            if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
            {

                String attachmentType=obj.optString("attachment_extension");
                String msg="";
                if(attachmentType!=null && !attachmentType.trim().isEmpty())
                {
                    if(attachmentType.toLowerCase().equals("png") ||attachmentType.toLowerCase().equals("jpg")  || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") ||attachmentType.toLowerCase().equals("gif") )
                    {
                        msg = "\uD83D\uDCF8 image";

                    }
                    else
                    {
                        msg = "\uD83D\uDCCE attachment";
                    }
                }
                else
                {
                    msg = "\uD83D\uDCCE attachment";
                }
                SpannableString sb = new SpannableString(msg);
                userNewBuilder.append(sb);

            }









            if(isLogin){


                try {
                    sender_id=obj.optString("sender_id");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<SitesInfo> list=  Session.getSiteInfoList(context,Session.SP_SITE_INFO);



                activeChat = getActiveUserData(sender_id,list);



                tmUserId= Session.getTmUserId(context);


                notificationIntent = new Intent(context, ChatActivity.class);
                notificationIntent.putExtra(Values.Notification.single_notification, true);
                notificationIntent.putExtra(Values.Notification.notification, true);
                notificationIntent.putExtra(Values.Notification.fcmnotification, true);
                notificationIntent.putExtra(Values.Notification.tm_user_id, tmUserId);
                notificationIntent.putExtra(Values.Notification.user_name, userName);
                notificationIntent.putExtra(Values.Notification.userData, activeChat);
                notificationIntent.putExtra(Values.Notification.receiver_id, sender_id);
                notificationIntent.putExtra(Values.MyActions.NOTIFICATION, true);
                notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, PendingIntent.FLAG_ONE_SHOT);

                PendingIntent single_pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createChannels(context);
                    NotificationCompat.Builder notificationBuilder = null;
                    notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
                    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                    notificationBuilder.setContentTitle(messageTitle);
                    //inboxStyle.setBigContentTitle(messageTitle);
                    // inboxStyle.setSummaryText(messageTitle);
                    if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                    {
                        notificationBuilder.setContentText(userNewBuilder);
                        inboxStyle.addLine(userNewBuilder);
                    }
                    else
                    {
                        notificationBuilder.setContentText(messageBody);
                        inboxStyle.addLine(messageBody);
                    }

                    // notificationBuilder.setStyle(inboxStyle);


                    notificationBuilder.setSmallIcon(getSmallNotificationIcon());
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
                    //notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),getNotificationLargeIcon()));
                    notificationBuilder.setAutoCancel(true);
                    notificationBuilder.setWhen(System.currentTimeMillis());
                    notificationBuilder.setShowWhen(true);
                    notificationBuilder.setOnlyAlertOnce(true);
                    notificationBuilder.setContentIntent(single_pendingIntent);
                    notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                    notificationBuilder.setPriority(Notification.PRIORITY_MAX);

                    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .build();
                            //  notificationBuilder.setSound(defaultSoundUri,audioAttributes);
                            notificationBuilder.setSound(defaultSoundUri);

                        }
                        else
                        {
                            notificationBuilder.setSound(defaultSoundUri);
                        }

                    }
                    else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {

                        if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                        {

                        }
                        else
                        {
                            Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v1.vibrate(100);

                        }

                    }
                    // notificationBuilder.setSound(defaultSoundUri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    }

                    long currentTime = System.currentTimeMillis();
                    int notificationId = (int) currentTime;


                    if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                    {
                        return;
                    }
                    else
                    {
                        if(sender_id!=null && !sender_id.equals(tmUserId))
                        {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(notificationId, notificationBuilder.build());

                            }
                            else
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notificationId, notificationBuilder.build());
                                }
                            }

                        }

                    }
                } else {
                    Notification.Builder notificationBuilder = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  notificationBuilder.setSmallIcon(getSmallNotificationIcon());
                        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
                        //notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),getNotificationLargeIcon()));
                        notificationBuilder.setAutoCancel(true);
                        notificationBuilder.setWhen(System.currentTimeMillis());
                        notificationBuilder.setShowWhen(true);
                        notificationBuilder.setOnlyAlertOnce(true);
                        notificationBuilder.setContentIntent(single_pendingIntent);
                        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
                        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_ALARM)
                                        .build();
                                notificationBuilder.setSound(defaultSoundUri,audioAttributes);
                            }
                            else
                            {
                                notificationBuilder.setSound(defaultSoundUri);
                            }

                        }
                        else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {

                            if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                            {

                            }
                            else
                            {
                                Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                v1.vibrate(100);

                            }

                        }
                        // notificationBuilder.setSound(defaultSoundUri);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        }

                        long currentTime = System.currentTimeMillis();
                        int notificationId = (int) currentTime;


                        if(sender_id!=null && AppSocket.SOCKET_OPENED_ACTIVITY==Values.AppActivities.ACTIVITY_CHAT && ChatActivity.receiver_id!=null && !ChatActivity.receiver_id.trim().isEmpty() && ChatActivity.receiver_id.equals(sender_id) && Helper.getInstance().isAppForground(context))
                        {
                            return;
                        }
                        else
                        {
                            if(sender_id!=null && !sender_id.equals(tmUserId))
                            {


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(notificationId, notificationBuilder.build());

                                }
                                else
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        notificationManager.notify(notificationId, notificationBuilder.build());
                                    }
                                }

                            }

                        }
                        notificationBuilder = new Notification.Builder(context);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        }


                        notificationBuilder.setContentTitle(messageTitle);
                        if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                        {

                            notificationBuilder.setContentText(userNewBuilder);
                        }
                        else
                        {

                            notificationBuilder.setContentText(messageBody);
                        }




                    }
                }






            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
