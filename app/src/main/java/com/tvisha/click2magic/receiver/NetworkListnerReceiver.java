package com.tvisha.click2magic.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.NetworkUtil;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;


import java.text.SimpleDateFormat;
import java.util.Calendar;

/*import com.tvisha.troopmessenger.service.BackgroundLocationServiveForOreo;*/

public class NetworkListnerReceiver extends BroadcastReceiver {
    static Context context;
    public int TIME_DELAY = 5000;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context ctxt, Intent intent) {
        context = ctxt;

        final String status = NetworkUtil.getConnectivityStatusString(context);

        /*if (!Helper.getInstance().isAppForground(ctxt)) {
            if (!Helper.getInstance().isMyServiceRunning(SocketService.class, ctxt)){
                ctxt.startService(new Intent(ctxt,SocketService.class));
            }
        }*/
        if (HandlerHolder.mainActivityUiHandler==null){
            if (!Helper.getInstance().isAppForground(context)) {
                if (HandlerHolder.backgroundservice == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        BackgroundServiceForOreo.startJob();
                    } else {
                        context.startService(new Intent(context, SocketService.class));
                    }

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        *//*BackgroundLocationServiveForOreo.startJob();*//*
                    } else {
                        context.startService(new Intent(context, LocationService.class));
                    }*/
                }
            }
        }


        if (!Helper.getConnectivityStatus(context)){
            storeTheLastSyncMessages();
        }

        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (HandlerHolder.mainActivityUiHandler != null) {
                            if (Helper.getConnectivityStatus(context)) {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.RECENT_NETWORK_CONNECTED);
                            } else {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.RECENT_NETWORK_DISCONNECTED);
                            }
                        }else {
                            if (HandlerHolder.backgroundservice != null &&  Helper.getConnectivityStatus(context)) {
                                HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.RECENT_NETWORK_CONNECTED).sendToTarget();
                            }else{
                                if (HandlerHolder.backgroundservice!=null) {
                                    HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.RECENT_NETWORK_DISCONNECTED).sendToTarget();
                                }
                            }
                        }
                    }
                }, 3000);
            }
        }.start();
    }

    private void storeTheLastSyncMessages() {
        try {

            if (Session.getLoginStatus(context)) {
                if ( (Session.getSyncMessageByTime(context)== null ||Session.getSyncMessageByTime(context).trim().isEmpty() || Session.getSyncMessageByTime(context).equals("null")) || Session.getSyncMessageByTime(context).equals("")) {
                    Calendar c = Calendar.getInstance();
                    String date_string = df.format(c.getTime());
                    if (date_string != null && !date_string.trim().isEmpty() && !date_string.equals("null")) {

                        String local_time_to_india = Helper.getInstance().localTimeToIndiaTime(date_string);
                        Session.saveSyncMessageTime(context,local_time_to_india);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
