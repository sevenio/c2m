package com.tvisha.click2magic.Fcm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.service.BackgroundServiceForOreo;
import com.tvisha.click2magic.service.SocketService;


//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String TAG = "MyFirebaseMsgService";
//
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//
//        if (remoteMessage != null) {
//
//
//            if (HandlerHolder.mainActivityUiHandler == null && HandlerHolder.backgroundservice == null) {
//                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        BackgroundServiceForOreo.startJob();
//                    } else {
//                        if (!isMyServiceRunning(SocketService.class)) {
//                            Intent serviceIntent = new Intent(getApplicationContext(), SocketService.class);
//                            startService(serviceIntent);
//                        }
//                    }
//                } catch (OutOfMemoryError e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//
//
//    }
//
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        try {
//            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//            if (manager != null) {
//                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//                    if (serviceClass.getName().equals(service.service.getClassName())) {
//                        return true;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//
//
//}