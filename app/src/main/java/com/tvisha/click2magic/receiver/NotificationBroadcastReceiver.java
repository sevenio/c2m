package com.tvisha.click2magic.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.RemoteInput;


import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;

import org.json.JSONObject;


public class NotificationBroadcastReceiver extends BroadcastReceiver {
    String reply = "reply";
    int notification_id = -1,status=0;
    String receiver_id="";
    String entity_type ="",workspace_id;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null && reply.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = getReplyMessage(intent);
            if (intent.getExtras()!=null){
                receiver_id = intent.getExtras().getString("receiver_id");
                notification_id = intent.getExtras().getInt("notification_id");
                status = intent.getExtras().getInt("status");

            }

            if (notification_id!=-1) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notification_id);
            }

            if (receiver_id!=null && !receiver_id.trim().isEmpty() && !receiver_id.trim().equals("null") && message!=null) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("receiver_id", receiver_id);
                    object.put("entity_type",entity_type);
                    object.put("status",status);
                    object.put("message",message.toString());
                    object.put("lollipop",false);
                    object.put("workspace_id",workspace_id);
                    if (HandlerHolder.mainActivityUiHandler != null) {
                        HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.ADD_NEW_NOTIFICATION_REPLY_MESSAGE,object).sendToTarget();
                    }else if (HandlerHolder.backgroundservice!=null){
                        HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.ADD_NEW_NOTIFICATION_REPLY_MESSAGE,object).sendToTarget();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Helper.getInstance().getListOfNotifications(context);
            }
        }
    }


    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence("reply");
        }
        return null;
    }
}
