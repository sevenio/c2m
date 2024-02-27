package com.tvisha.click2magic.Helper;

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
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;


import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.model.Notifications;
import com.tvisha.click2magic.ui.ChatActivity;
import com.tvisha.click2magic.ui.HomeActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.NOTIFICATION_SERVICE;


public class GroupNotificationHelper {
    private static GroupNotificationHelper ourInstance = new GroupNotificationHelper();





    String CHANNEL_ID = "com.tvisha.click2magic";
    String CHANNEL_NAME = "click2chat";
    String GROUP_ID = "click2chatGroup";

    private String REPLACE_STRING = " daiw ";
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";
    static boolean isFirstTime = true, isVibrateFirstTime = true;
    int groupNotificationId=1000;

    public static GroupNotificationHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new GroupNotificationHelper();
        }
        return ourInstance;
    }

    String KEY_NOTIFICATION_GROUP = "com.tvisha.click2magic.MESSAGES";

    private GroupNotificationHelper() {
    }

    public long mLastClickTimedss = 0;

    public void sendNotifictionNew(Context context, boolean updating) {
        try {

            if (SystemClock.elapsedRealtime() - mLastClickTimedss < 800) {
                return;
            }
            mLastClickTimedss = SystemClock.elapsedRealtime();


            List<Notifications> notifications = new ArrayList<>();

            JSONObject object = new ConversationTable(context).getTheUnreadCountOfAll(context);




            if (object != null) {

                JSONObject user_object = object.optJSONObject("user");
                JSONObject group_object = object.optJSONObject("group");

                Helper.getInstance().LogDetails("sendNotifictionNew ",user_object.toString());


                Iterator<String> u_keys = user_object.keys();
                while (u_keys.hasNext()) {
                    String key = u_keys.next();

                    JSONArray u_array = user_object.optJSONArray(key);
                    if (u_array != null && u_array.length() > 0) {
                        Notifications usermodel = new Notifications();
                        usermodel.setMessageTime(u_array.optJSONObject(0).optString("created_at"));
                        usermodel.setGroupName(u_array.optJSONObject(0).optString("name"));
                        usermodel.setUserId(u_array.optJSONObject(0).optString("user_id"));
                        usermodel.setSenderName(u_array.optJSONObject(0).optString("name"));
                        usermodel.setSiteId(u_array.optJSONObject(0).optString("site_id"));
                        usermodel.setConversation_reference_id(u_array.optJSONObject(0).optString("conversation_reference_id"));
                        usermodel.setSiteName(u_array.optJSONObject(0).optString("site_name"));


                        usermodel.setTotalMessage(u_array.length());
                        usermodel.setEntity(1);
                        StringBuilder userbuilder = new StringBuilder();
                        StringBuilder userNewBuilder = new StringBuilder();
                        if (u_array.length() < 6) {
                            for (int j = u_array.length() - 1; j >= 0; j--) {

                                userbuilder.append(u_array.optJSONObject(j).optString("message")).append(REPLACE_STRING).append("\n");
                                userNewBuilder.append(u_array.optJSONObject(j).optString("message")).append(REPLACE_STRING).append("\n");
                            }

                        } else {
                            for (int j = 6 - 1; j >= 0; j--) {
                                userbuilder.append(u_array.optJSONObject(j).optString("message")).append(REPLACE_STRING).append("\n");
                                userNewBuilder.append(u_array.optJSONObject(j).optString("message")).append(REPLACE_STRING).append("\n");
                            }

                        }
                        usermodel.setMessage(userbuilder.toString());
                        usermodel.setNewMessage(userNewBuilder.toString());

                        notifications.add(usermodel);
                    }
                }


            }

            if (notifications != null && notifications.size() > 0) {
                pushNotificationNew(context, notifications, updating);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushNotificationNew(Context context, List<Notifications> notifications, boolean updating) {
        try {

            Notification.Builder notificationBuilder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannels(context, 100);
                notificationBuilder = new Notification.Builder(context, CHANNEL_ID);
            } else {
                notificationBuilder = new Notification.Builder(context);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                showMarshmallowAboveNotifications(context, notifications, notificationBuilder, updating);
            }
           /* else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                showNotificationMarshMallow(context, notifications, notificationBuilder, updating);
            }*/
            else {
                showBelowMarshmallow(context, notifications, notificationBuilder, updating);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMarshmallowAboveNotifications(Context context, List<Notifications> unreadChats, Notification.Builder notificationBuilder, boolean updating) {

        //clearNotificationsOnId(context);
        String senderId = "";
        int isGroup = -1;

        senderId = "";
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_firebase_smallnotification));
            notificationBuilder.setContentTitle("Click 2 Magic");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            JSONArray array = new JSONArray();
            if (unreadChats != null && unreadChats.size() > 0) {
                for (int i = 0; i < unreadChats.size(); i++) {

                    if (unreadChats.get(i).getMessage() != null && !unreadChats.get(i).getMessage().trim().isEmpty()) {

                        String notification_tag = unreadChats.get(i).getSiteId() + "_" + unreadChats.get(i).getEntityId() + "_" + unreadChats.get(i).getEntity();
                        List<SitesInfo> list= Session.getSiteInfoList(context,Session.SP_SITE_INFO);

                        ActiveChat  activeChat = getActiveUserData( unreadChats.get(i).getUserId(),unreadChats.get(i).getConversation_reference_id(),list);
                        List<ActiveChat> activeChatList = getActiveUserDataList(unreadChats.get(i).getUserId(),unreadChats.get(i).getConversation_reference_id(),list);

                       Helper.getInstance().LogDetails("showMarshmallowAboveNotifications ",i+"  "+activeChat.toString());

                        Intent singleIntent = new Intent(context, ChatActivity.class);
                        singleIntent.putExtra(Values.Notification.single_notification, true);
                        singleIntent.putExtra(Values.Notification.notification, true);
                        singleIntent.putExtra(Values.Notification.fcmnotification, true);
                        singleIntent.putExtra(Values.Notification.tmnotification, true);
                        singleIntent.putExtra(Values.Notification.tm_user_id, Session.getTmUserId(context));
                        singleIntent.putExtra(Values.Notification.user_name, unreadChats.get(i).getSenderName());
                        singleIntent.putExtra(Values.Notification.userData, activeChat);
                        singleIntent.putParcelableArrayListExtra(Values.IntentData.USER_LIST, (ArrayList<? extends Parcelable>) activeChatList);
                        singleIntent.putExtra(Values.Notification.receiver_id,  unreadChats.get(i).getUserId());
                        singleIntent.putExtra(Values.MyActions.NOTIFICATION, true);

                        singleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent single_pendingIntent = PendingIntent.getActivity(context, i, singleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder = new Notification.Builder(context, CHANNEL_ID);
                        } else {
                            builder = new Notification.Builder(context);
                        }

                        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_firebase_smallnotification));


                        if (unreadChats.get(i).getTotalMessage() >= 6) {
                            builder.setContentTitle(unreadChats.get(i).getSiteName()+" : "+unreadChats.get(i).getGroupName() + " (" + unreadChats.get(i).getTotalMessage() + " messages )");
                        } else {
                            builder.setContentTitle(unreadChats.get(i).getSiteName()+" : "+unreadChats.get(i).getGroupName());
                        }


                        String message_split[] = unreadChats.get(i).getMessage().replace("\n", "").split(REPLACE_STRING);


                        if (message_split.length > 0) {
                            builder.setContentText(message_split[message_split.length - 1].replace(REPLACE_STRING, ""));
                            builder.setStyle(new Notification.BigTextStyle().bigText(message_split[0].replace(REPLACE_STRING, "")));
                        } else {
                            builder.setContentText(unreadChats.get(i).getMessage().replace(REPLACE_STRING, ""));
                            builder.setStyle(new Notification.BigTextStyle().bigText(unreadChats.get(i).getMessage().replace(REPLACE_STRING, "")));
                        }

                        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();


                        if (unreadChats.get(i).getTotalMessage() > 6) {
                            inboxStyle.setBigContentTitle(unreadChats.get(i).getSiteName()+" : "+unreadChats.get(i).getGroupName() + " (" + unreadChats.get(i).getTotalMessage() + " messages )");
                        } else {
                            inboxStyle.setBigContentTitle(unreadChats.get(i).getSiteName()+" : "+unreadChats.get(i).getGroupName());
                        }


                        String[] strings = unreadChats.get(i).getNewMessage().split(REPLACE_STRING);


                        for (int s = 0; s < strings.length; s++) {

                            inboxStyle.addLine(strings[s].replace(REPLACE_STRING, ""));
                           /* if(s==0){
                                inboxStyle.addLine(strings[s]);
                            }
                            else
                            {
                                inboxStyle.addLine(strings[s].replace(REPLACE_STRING, ""));
                            }*/

                        }
                        builder.setStyle(inboxStyle);
                        builder.setAutoCancel(true);
                        builder.setContentIntent(single_pendingIntent);
                         builder.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND );




                        builder.setSmallIcon(getNotificationIcon());
                        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        builder.setGroup(KEY_NOTIFICATION_GROUP);
                        builder.setWhen(getMessageTime(unreadChats.get(i).getMessageTime()));
                        builder.setShowWhen(true);
                        builder.setOnlyAlertOnce(true);
                        // builder.setSound(null);

                        Helper.getInstance().LogDetails("showMarshmallowAboveNotifications messageTime",unreadChats.get(i).getMessageTime());


                        JSONObject object = new JSONObject();
                        object.put("notification", builder);
                        object.put("notification_id", Integer.parseInt(unreadChats.get(i).getUserId()));
                        object.put("notification_tag", notification_tag);
                        object.put("message_time", unreadChats.get(i).getMessageTime());
                        object.put("sender_id", unreadChats.get(i).getUserId());
                        object.put("site_id", unreadChats.get(i).getSiteId());
                        object.put("is_group", unreadChats.get(i).getIsGroup());
                        array.put(object);
                    }

                }
            }

            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("single_notification", false);
            intent.putExtra("notification", true);
            intent.putExtra("fcmnotification", true);
            intent.putExtra(Values.MyActions.NOTIFICATION, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder summaryNotification =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle("Click 2 Magic")
                            .setContentIntent(pendingIntent)
                            .setContentText("" + unreadChats.size() + "  new chat messages")
                            .setSmallIcon(getNotificationIcon())
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .setBigContentTitle("" + unreadChats.size() + " new chatmessages")
                                    .setSummaryText("Click 2 Magic"))
                            .setGroup(KEY_NOTIFICATION_GROUP)
                            .setShowWhen(true)
                            .setWhen(Calendar.getInstance().getTimeInMillis())
                            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                            .setGroupSummary(true);

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


            try {
                summaryNotification.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);
                summaryNotification.setPriority(Notification.PRIORITY_MAX);
                summaryNotification.setOnlyAlertOnce(true);

                if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    summaryNotification.setSound(alarmSound);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            for (int i = 0; i < array.length(); i++) {

                Long messageTimeInMills = getMessageTime(array.optJSONObject(i).optString("message_time"));
                int notificationId = array.optJSONObject(i).optInt("notification_id");
                String notification_tag = array.optJSONObject(i).optString("notification_tag");

                Notification.Builder build = (Notification.Builder) array.optJSONObject(i).opt("notification");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    build.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);

                    build.setWhen(messageTimeInMills);
                    build.setShowWhen(true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    build.setWhen(messageTimeInMills);
                    build.setShowWhen(true);
                }


                boolean isAlive = false;

                NotificationManager notificationManager1 = context.getSystemService(NotificationManager.class);
                StatusBarNotification[] notifications = notificationManager1.getActiveNotifications();
                if (notifications != null && notifications.length > 0) {

                    for (StatusBarNotification notification : notifications) {
                        if (notification.getId() == notificationId) {
                            isAlive = true;
                            break;
                        }
                    }
                }

                if (isAlive) {

                    isVibrateFirstTime = true;
                    Long mLastClickTimeInMills, currentTimeInMills;

                    mLastClickTimeInMills =Session.getNotificationTime(context);
                    currentTimeInMills = new Date().getTime();

                    Long diff = currentTimeInMills - mLastClickTimeInMills;
                    if (diff < 5000) {
                        summaryNotification.setOnlyAlertOnce(true);
                    } else {
                        summaryNotification.setOnlyAlertOnce(false);
                        if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                            Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v1.vibrate(100);
                        }
                    }


                } else {

                    if (notifications != null && notifications.length > 0 && !isAlive) {
                         summaryNotification.setOnlyAlertOnce(false);

                    } else {

                        isVibrateFirstTime = true;

                    }

                }

                if (array.optJSONObject(i).optString("sender_id").equals(senderId) && array.optJSONObject(i).optInt("is_group") == isGroup) {
                    notificationManager.notify(notification_tag, notificationId, build.build());


                } else {

                    if (isFirstTime) {
                        Helper.getInstance().LogDetails("notificaton trigger","1");
                        notificationManager.notify(notification_tag, notificationId, build.build());

                    } else {
                        if (!isAlive) {
                            Helper.getInstance().LogDetails("notificaton trigger","2");
                            notificationManager.notify(notification_tag, notificationId, build.build());

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("notificaton trigger","3");
                            notificationManager.notify(notification_tag, notificationId, build.build());
                        }
                    }

                }
                notificationManager.notify(groupNotificationId, summaryNotification.build());
            }
            isFirstTime = false;

        } catch (Exception e) {
            e.printStackTrace();
        }


        mLastClickTime1 = SystemClock.elapsedRealtime();
        mLastClickTimeInMills = Calendar.getInstance().getTimeInMillis();
        Session.saveNotificationTime(context,mLastClickTimeInMills);


    }


    private void showBelowMarshmallow(Context context, List<Notifications> unreadChats, Notification.Builder notificationBuilder, boolean updating) {
        try {
            if (notificationBuilder != null) {
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }




                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_firebase_smallnotification));

                notificationBuilder.setContentTitle("Click 2 Magic");

                if (unreadChats.size() == 1) {
                    if (unreadChats.get(0).getMessage() != null && !unreadChats.get(0).getMessage().trim().isEmpty()) {
                        notificationBuilder.setContentText(unreadChats.get(unreadChats.size() - 1).getMessage().replace(REPLACE_STRING, ""));
                        notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(unreadChats.get(0).getMessage().replace(REPLACE_STRING, "")));
                        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                        inboxStyle.setBigContentTitle("Click 2 Magic");
                        String[] strings = unreadChats.get(0).getMessage().split(REPLACE_STRING);
                        for (int s = 0; s < strings.length; s++) {
                            if (s == 0) {

                                    inboxStyle.addLine(unreadChats.get(0).getGroupName() + " : " + strings[s].replace(REPLACE_STRING, ""));
                                    notificationBuilder.setContentText(unreadChats.get(0).getSiteName()+" : "+unreadChats.get(0).getGroupName() + " : " + strings[s].replace(REPLACE_STRING, ""));


                            } else {
                                inboxStyle.addLine(strings[s].replace(REPLACE_STRING, ""));
                            }
                        }
                        // notificationBuilder.setContentText("You have one chat messages ");
                        notificationBuilder.setStyle(inboxStyle);


                        if (isFirstTime) {
                            notificationBuilder.setOnlyAlertOnce(true); //newly added
                            isFirstTime = false;
                        } else {
                            Long mLastClickTimeInMills, currentTimeInMills;
                           // mLastClickTimeInMills = sharedPreferences.getLong(SharedPreferenceConstants.SP_FCM_TIME_IN_MILLS, 0);
                            mLastClickTimeInMills = 0l;
                            currentTimeInMills = new Date().getTime();

                            Long diff = currentTimeInMills - mLastClickTimeInMills;
                            if (diff < 1000) {
                                notificationBuilder.setOnlyAlertOnce(true);
                            } else {
                                notificationBuilder.setOnlyAlertOnce(false);
                            }
                        }

                        intent = new Intent(context, HomeActivity.class);
                        intent.putExtra("notification", true);
                        intent.putExtra("fcmnotification", true);
                        intent.setAction(Long.toString(System.currentTimeMillis()));
                        intent.putExtra(Values.MyActions.NOTIFICATION, true);
                        intent.putExtra("single_notification", true);


                        intent.putExtra("entityType", unreadChats.get(0).getEntity());
                        intent.putExtra("receiver_id", unreadChats.get(0).getUserId());
                        intent.putExtra("workspace_id", unreadChats.get(0).getSiteId());
                        intent.putExtra("unread_count", unreadChats.size());

                     //   intent.putExtra(Values.Media.MEDIA_INTENT_FROM_SHARE, false);
                      //  intent.putExtra(Values.MyActions.FORWORD_MESSAGES, false);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }


                } else if (unreadChats.size() >= 2) {


                    intent = new Intent(context, HomeActivity.class);
                    intent.putExtra(Values.MyActions.NOTIFICATION, true);
                    intent.putExtra("notification", true);
                    intent.putExtra("single_notification", false);
                    intent.putExtra("fcmnotification", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                    inboxStyle.setBigContentTitle("Click 2 Magic");
                    if (unreadChats.size() > 0) {
                        boolean group = false;
                        for (int i = 0; i < (unreadChats.size() > 2 ? 2 : unreadChats.size()); i++) {

                            if (unreadChats.get(i).getMessage() != null && !unreadChats.get(i).getMessage().trim().isEmpty()) {

                                String[] strings = unreadChats.get(i).getMessage().split(REPLACE_STRING);

                                if (strings.length > 0) {
                                    for (int s = 0; s < strings.length; s++) {



                                            if (s == 0) {

                                                    inboxStyle.addLine(unreadChats.get(i).getSiteName()+" : "+unreadChats.get(i).getGroupName() + " : " + strings[s].replace(REPLACE_STRING, ""));
                                            } else {
                                                inboxStyle.addLine(strings[s].replace(REPLACE_STRING, ""));
                                            }

                                    }
                                } else {
                                    inboxStyle.addLine(unreadChats.get(i).getMessage());
                                }
                            }
                        }
                        if (unreadChats.size() > 2) {
                            inboxStyle.setSummaryText("+" + (unreadChats.size() - 2) + " more chat messages");
                        }
                    } else {
                        inboxStyle.setSummaryText("You have " + unreadChats.size() + " chat messages");
                    }
                    notificationBuilder.setStyle(inboxStyle);

                    if (isFirstTime) {
                        notificationBuilder.setOnlyAlertOnce(true); //newly added
                        isFirstTime = false;
                    } else {
                        Long mLastClickTimeInMills, currentTimeInMills;

                       // mLastClickTimeInMills = sharedPreferences.getLong(SharedPreferenceConstants.SP_FCM_TIME_IN_MILLS, 0);
                        mLastClickTimeInMills = 0l;
                        currentTimeInMills = new Date().getTime();

                        Long diff = currentTimeInMills - mLastClickTimeInMills;
                        if (diff < 5000) {
                            notificationBuilder.setOnlyAlertOnce(true);
                        } else {
                            notificationBuilder.setOnlyAlertOnce(false);
                        }
                    }
                    notificationBuilder.setContentText("You have " + unreadChats.size() + " chat messages ");
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (pendingIntent != null) {
                    notificationBuilder.setContentIntent(pendingIntent);
                }
                notificationBuilder.setAutoCancel(true);
                if (!updating) {


                    if (SystemClock.elapsedRealtime() - mLastClickTime < 10) {


                        try {
                            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                                notificationBuilder.setSound(null);

                            } else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                                Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                v1.vibrate(100);
                                notificationBuilder.setVibrate(new long[]{0L});

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {


                        try {
                            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {

                                notificationBuilder.setSound(alarmSound);
                            } else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                                Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                v1.vibrate(200);
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }

                } else {

                    notificationBuilder.setVibrate(null);
                }

                mLastClickTime = SystemClock.elapsedRealtime();
                //notificationBuilder.setActions(action);
                if (!updating) {
                    notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
                } else {
                    notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
                }
                notificationBuilder.setSmallIcon(getNotificationIcon());


                getManager(context).notify(0, notificationBuilder.build());

                mLastClickTimeInMills = Calendar.getInstance().getTimeInMillis();
               // sharedPreferences.edit().putLong(SharedPreferenceConstants.SP_FCM_TIME_IN_MILLS, mLastClickTimeInMills).apply();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long mLastClickTimemLastClickTime = 0;




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

    public void clearNotificationsOnId(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(groupNotificationId);
    }

    public long mLastClickTimeInMills = 0;
    public long mLastClickTime = 0;
    public long mLastClickTime1 = 0;

    NotificationManager notificationManager;

    public NotificationManager getManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public long mLastClickTimed = 0;


    @RequiresApi(api = 26)
    public void createChannels(Context ctx, int i) {


        NotificationChannel message_notification = null;

        message_notification = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        message_notification.enableLights(true);
        message_notification.setLightColor(Color.RED);
        message_notification.setShowBadge(true);
        message_notification.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        Uri alarmSound = null;

        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (isVibrateFirstTime) {


            message_notification.enableVibration(false);
            message_notification.setImportance(NotificationManager.IMPORTANCE_HIGH);

            // Vibrator v1 = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            //v1.vibrate(300);
        } else {

            message_notification.enableVibration(false);
            message_notification.setImportance(NotificationManager.IMPORTANCE_HIGH);
            Vibrator v1 = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            v1.vibrate(300);

        }
        if (SystemClock.elapsedRealtime() - mLastClickTimed < 8000) {
            try {
                message_notification.setSound(null, null);
                message_notification.setVibrationPattern(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    message_notification.setSound(alarmSound, attributes);

                } else if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    long[] v = {300};
                    //  message_notification.setVibrationPattern(v);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mLastClickTimed = SystemClock.elapsedRealtime();
        //message_notification.setVibrationPattern(new long[]{0L});
        getManager(ctx).createNotificationChannel(message_notification);

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.logo_icon : R.drawable.ic_firebase_smallnotification;
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
    public long mLastClickTimeBurnout = 0;



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




    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private ActiveChat getActiveUserData(String sender_id, String conversation_reference_id, List<SitesInfo> sitesInfoList) {
        if(sender_id!=null ){

            if(sitesInfoList !=null && sitesInfoList.size()>0){
                Helper.getInstance().LogDetails("getActiveUserData","size "+sitesInfoList.size()+"  ***"+sender_id+" "+conversation_reference_id);
                for(int i = 0; i<sitesInfoList.size(); i++){
                    List<ActiveChat> activeChatList=sitesInfoList.get(i).getActiveChats();
                    if(activeChatList!=null && activeChatList.size()>0){
                        for(int j=0;j<activeChatList.size();j++){
                            ActiveChat activeChat =activeChatList.get(j);
                            if(activeChat !=null && activeChat.getTmVisitorId()!=null){
                                Helper.getInstance().LogDetails("getActiveUserData","check "+sender_id+" "+activeChat.getTmVisitorId()+"   ==="+j);
                                if(sender_id.equals(activeChat.getTmVisitorId()))
                                {

                                    return activeChat;
                                }
                            }
                        }
                    }

                }
            }
            else
            {
                Helper.getInstance().LogDetails("getActiveUserData","size empty");
            }
        }
        else
        {
            Helper.getInstance().LogDetails("getActiveUserData","sender id null");
            return null;
        }
        return null;
    }

    private List<ActiveChat> getActiveUserDataList(String sender_id, String conversation_reference_id,List<SitesInfo> sitesInfoList) {
        if(sender_id!=null ){

            if(sitesInfoList !=null && sitesInfoList.size()>0){
                Helper.getInstance().LogDetails("getActiveUserData","size "+sitesInfoList.size()+"  ***"+sender_id+" "+conversation_reference_id);
                for(int i = 0; i<sitesInfoList.size(); i++){
                    List<ActiveChat> activeChatList=sitesInfoList.get(i).getActiveChats();
                    if(activeChatList!=null && activeChatList.size()>0){
                        for(int j=0;j<activeChatList.size();j++){
                            ActiveChat activeChat =activeChatList.get(j);
                            if(activeChat !=null && activeChat.getTmVisitorId()!=null){
                                Helper.getInstance().LogDetails("getActiveUserData","check "+sender_id+" "+activeChat.getTmVisitorId()+"   ==="+j);
                                if(sender_id.equals(activeChat.getTmVisitorId()))
                                {

                                    return activeChatList;
                                }
                            }
                        }
                    }

                }
            }
            else
            {
                Helper.getInstance().LogDetails("getActiveUserData","size empty");
            }
        }
        else
        {
            Helper.getInstance().LogDetails("getActiveUserData","sender id null");
            return null;
        }
        return null;
    }



}
