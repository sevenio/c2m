package com.tvisha.click2magic.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.socket.AppSocket;


public class ClosingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Helper.getInstance().LogDetails("ClosingService onTaskRemoved ","Called");
            AppSocket appSocket = (AppSocket) getApplication();
            appSocket.socketDestroy();
            startSocketService();

            stopSelf();
            super.onTaskRemoved(rootIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helper.getInstance().LogDetails("ClosingService onStartCommand ","Called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
    public void startSocketService() {
        JobInfo job = null;
        try {
            try {
                if (HandlerHolder.mainActivityUiHandler==null && HandlerHolder.backgroundservice==null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        BackgroundServiceForOreo.startJob();
                    } else {
                        if (!isMyServiceRunning(SocketService.class)) {
                            Intent serviceIntent = new Intent(getApplicationContext(), SocketService.class);
                            startService(serviceIntent);
                        }
                    }

                }

            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
        private boolean isMyServiceRunning(Class<?> serviceClass) {
            try {
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (manager != null) {
                    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                        if (serviceClass.getName().equals(service.service.getClassName())) {
                            return true;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
}