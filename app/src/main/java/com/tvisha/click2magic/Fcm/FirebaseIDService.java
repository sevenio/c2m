package com.tvisha.click2magic.Fcm;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class FirebaseIDService extends  FirebaseMessagingService{

    private static final String TAG = "FirebaseIDService";
    public static String token = "",tm_user_id="",user_id="",company_token="";
    boolean isLogin=false,isUpdatedFcmToken=false;


    public static String getRefreshToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    static String deviceTokens =null;
    private static String getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                deviceTokens = instanceIdResult.getToken();
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            }
        });
        return deviceTokens;
    }


}
