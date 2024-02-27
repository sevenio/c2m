package com.tvisha.click2magic.receiver;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;


public class AutoStart extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (HandlerHolder.mainActivityUiHandler==null && HandlerHolder.backgroundservice==null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        BackgroundServiceForOreo.startJob();
                    } else {
                        Intent serviceIntent = new Intent(context, SocketService.class);
                        context.startService(serviceIntent);
                    }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
