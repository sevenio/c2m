package com.tvisha.click2magic.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;



public class StopService extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent!=null && intent.getAction()!=null && (intent.getAction().equals("ac.in.ActivityRecognition.RestartSensor") || intent.getAction().equals("com.google.android.gms.measurement.START"))){
            if (HandlerHolder.mainActivityUiHandler==null && HandlerHolder.backgroundservice==null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        BackgroundServiceForOreo.startJob();


                    } else {
                        if (!isMyServiceRunning(SocketService.class)) {
                            Intent serviceIntent = new Intent(context, SocketService.class);
                            context.startService(serviceIntent);
                        }

                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
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
