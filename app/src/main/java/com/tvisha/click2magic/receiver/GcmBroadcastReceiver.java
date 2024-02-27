package com.tvisha.click2magic.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.GroupNotificationHelper;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.NotificationHelper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;
import com.tvisha.click2magic.ui.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;


public class GcmBroadcastReceiver extends BroadcastReceiver {

    public static int count = 0;
    String message,title,data,sender_name;

    boolean isLogin=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {

            if (intent.getAction()!=null &&    intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")){


                isLogin= Session.getLoginStatus(context);
                Helper.getInstance().LogDetails("notification =============> onReceive called",isLogin+"");

                if(isLogin)
                {
                    Bundle bundle = intent.getExtras();
                    JSONObject object = new JSONObject();
                    Set<String> keys = bundle.keySet();
                    for (String key : keys) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                object.put(key, JSONObject.wrap(bundle.get(key)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    checkSocketConnection(context,object);


                    Helper.getInstance().LogDetails("notification =============> onReceive called",object.toString());

                    data= object.optString("data");
                    title=object.optString("title");
                    message=object.optString("message");
                    sender_name=object.optString("sender_name");

                    if(sender_name!=null && !sender_name.isEmpty() && sender_name.length()>1)
                    {
                  /*      {"receiver_id":"3715","google.delivered_priority":"high","message_id":"79243",
                                "google.sent_time":1583909681609,"google.ttl":2419200,"google.original_priority":"high",
                                "msg_type":"0","conversation_reference_id":"2650e69c1c55883858","reference_id":"",
                                "gcm.notification.content_available":"true","user_id":"3715","from":"987943255713",
                                "sender_id":"259","is_group":"0","is_reply":"0","google.message_id":"0:1583909681621002%ce538a87f9fd7ecd",
                                "sender_name":"mounika","message":"88","google.c.sender.id":"987943255713","created_at":"2020-03-11 12:24:41"}*/

                        title=object.optString("sender_name");
                        //NotificationHelper.getInstance().sendTmNotification(context,title,message,object);
                        GroupNotificationHelper.getInstance().sendNotifictionNew(context,true);
                    }
                    else
                    {

             /*           {"receiver_id":"3715","google.delivered_priority":"high","message_id":"79242","google.sent_time":1583909339179,
                                "google.ttl":2419200,"google.original_priority":"high","msg_type":"12",
                                "conversation_reference_id":"2650e69c1c55883858","reference_id":"","gcm.notification.content_available":"true",
                                "user_id":"3715","from":"987943255713","sender_id":"259","is_group":"0","is_reply":"0",
                                "google.message_id":"0:1583909339186939%ce538a87f9fd7ecd","sender_name":"mounika",
                                "message":"{\"type\":1,\"message\":\"user confirmed mobile number\"}","google.c.sender.id":"987943255713",
                                "created_at":"2020-03-11 12:18:59"}*/
                        int notification_type=object.optInt("notification_type");
                        NotificationHelper.getInstance().sendC2mNotification(context,title,message,notification_type,data,object);
                    }


                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void checkSocketConnection(Context context,JSONObject object) {
        try
        {
            if (!Helper.getInstance().isAppForground(context)) {
                if (HandlerHolder.mainActivityUiHandler == null) {
                    if (HandlerHolder.backgroundservice == null) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            try {
                                BackgroundServiceForOreo.startJob();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            context.startService(new Intent(context, SocketService.class));
                        }
                    } else {
                        if (HandlerHolder.backgroundservice != null) {
                            HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.FCM_TRIGGER, object).sendToTarget();
                        }
                    }
                } else {
                    try {
                        if (HandlerHolder.mainActivityUiHandler != null) {
                            HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.FCM_TRIGGER, object).sendToTarget();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                if (HandlerHolder.mainActivityUiHandler != null) {
                    HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.FCM_TRIGGER, object).sendToTarget();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }




    }

    public void sendNotification(Context context, String messageTitle, String messageBody) {



        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notification = new Notification.Builder(context);
        notification.setSmallIcon(getNotificationIcon());
        notification.setContentTitle(messageTitle);
        notification.setContentText(messageBody);
        notification.setAutoCancel(true);
        notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),getNotificationLargeIcon())).setSound(defaultSoundUri).setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setColor(context.getResources().getColor( R.color.colorPrimaryDark));
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationManager.notify(count,notification.build());
        }
        count++;
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_firebase_smallnotification : R.drawable.ic_firebase_smallnotification;
    }

    private int getNotificationLargeIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_icon : R.drawable.app_icon;
    }




}
