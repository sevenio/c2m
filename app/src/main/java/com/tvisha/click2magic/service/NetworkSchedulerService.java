package com.tvisha.click2magic.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;


import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.receiver.ConnectivityReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
    ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    private ConnectivityReceiver mConnectivityReceiver;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    JobInfo job = null;
    @Override
    public void onCreate() {
        super.onCreate();

        mConnectivityReceiver = new ConnectivityReceiver(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            registerReceiver(mConnectivityReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        try {
            unregisterReceiver(mConnectivityReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(final boolean isConnected) {
        try {
            try {
                if (HandlerHolder.mainActivityUiHandler==null){
                    if (!Helper.getInstance().isAppForground(NetworkSchedulerService.this)) {
                        if (HandlerHolder.backgroundservice == null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                BackgroundServiceForOreo.startJob();
                            } else {
                                startService(new Intent(NetworkSchedulerService.this, SocketService.class));
                            }
                        }
                    }
                }
                if (!Helper.getConnectivityStatus(getApplicationContext())){
                    storeTheLastSyncMessages();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (HandlerHolder.mainActivityUiHandler != null) {
                            if (Helper.getConnectivityStatus(getApplicationContext()) || isConnected) {
                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.RECENT_NETWORK_CONNECTED);
                            } else {

                                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.RECENT_NETWORK_DISCONNECTED);
                            }
                        }else {
                            if (HandlerHolder.backgroundservice != null && (isConnected || Helper.getConnectivityStatus(NetworkSchedulerService.this))) {
                                HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.RECENT_NETWORK_CONNECTED).sendToTarget();
                            }else {

                                if (HandlerHolder.backgroundservice!=null) {
                                    HandlerHolder.backgroundservice.obtainMessage(Values.RecentList.RECENT_NETWORK_DISCONNECTED).sendToTarget();
                                }
                            }
                        }
                    }
                }, 3000);
            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void storeTheLastSyncMessages() {
        try {

            if (Session.getLoginStatus(NetworkSchedulerService.this)) {
                if ( (Session.getSyncMessageByTime(NetworkSchedulerService.this) == null || Session.getSyncMessageByTime(NetworkSchedulerService.this).trim().isEmpty() || Session.getSyncMessageByTime(NetworkSchedulerService.this).equals("null")) || Session.getSyncMessageByTime(NetworkSchedulerService.this).equals("")) {
                    Calendar c = Calendar.getInstance();
                    String date_string = df.format(c.getTime());
                    if (date_string != null && !date_string.trim().isEmpty() && !date_string.equals("null")) {
                        String local_time_to_india = Helper.getInstance().localTimeToIndiaTime(date_string);

                        Session.saveSyncMessageTime(NetworkSchedulerService.this,local_time_to_india);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
