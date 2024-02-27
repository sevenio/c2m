package com.tvisha.click2magic.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.Fcm.FirebaseIDService;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.Helper.progressButton.ProgressButton;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.LoginApi;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity  {

    @BindView(R.id.logoImage)
    ImageView logoImage;

    @BindView(R.id.image_view_temp)
    ImageView image_view_temp;

    @BindView(R.id.email_edit_txt)
    EditText email_edit_txt;

    @BindView(R.id.password_edit_txt)
    EditText password_edit_txt;

    @BindView(R.id.login_btn)
    ProgressButton login_btn;


    @OnClick(R.id.login_btn)
    void login(){
        if (validate()) {
            getLogin();
        }
    }

    Context context;


    SitesTable sitesTable;

    Dialog dialog = null;
    public static String deviceId = "", device_fcm_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;

        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_LOGIN;;

        boolean isLogin = Session.getLoginStatus(LoginActivity.this);
        if (isLogin) {
                disconnectC2mSocket();
                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
                finish();

        }
        else
        {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                disableAutoFill();
            }
            progressDialog();
            disconnectC2mSocket();
            disconnectTmSocket();
            initializeViews();
            storeFcmToken();
        }



          getDeviceId();


    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    private void disconnectTmSocket() {
        try{
            AppSocket application = (AppSocket) getApplication();
            application.disconnectTmSocket();
            BasicActivity.tmSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void disconnectC2mSocket() {
        try{
            AppSocket application = (AppSocket) getApplication();
            application.disconnectSocket();
            BasicActivity.mSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void storeFcmToken() {
        FirebaseApp.initializeApp(LoginActivity.this);
        if (Session.getFcmToken(context) != null && !Session.getFcmToken(context).trim().isEmpty()) {
            device_fcm_token = Session.getFcmToken(context);
        } else if (FirebaseInstanceId.getInstance() != null && FirebaseInstanceId.getInstance().getToken() != null) {
            device_fcm_token = FirebaseInstanceId.getInstance().getToken();
        } else {
            device_fcm_token = FirebaseIDService.getRefreshToken();
        }

        if (device_fcm_token != null) {
            Helper.getInstance().LogDetails("device_fcm_token", device_fcm_token);
            Session.saveFcmToken(LoginActivity.this,device_fcm_token);
        }
    }

    private void getDeviceId() {

        deviceId =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Session.saveDeviceId(LoginActivity.this,deviceId);
    }


    private void initializeViews() {

        // HandlerHolder.mainActivityUiHandler=uiHandler;

        sitesTable=new SitesTable(LoginActivity.this);


        RequestOptions options = new RequestOptions()
                .error(R.drawable.profile)
                .disallowHardwareConfig()
                .priority(Priority.HIGH);
        Glide.with(LoginActivity.this)
                .load(R.drawable.click2magiclogo)
                .apply(options)
                .into(logoImage);
       Glide.with(LoginActivity.this)
               .load(R.drawable.img_background_sign_in)
               .apply(options)
               .into(image_view_temp);


        password_edit_txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (validate()) {
                        getLogin();
                    }
                }
                return false;
            }
        });
    }


    private boolean validate() {

        String email_id = email_edit_txt.getText().toString();
        String password = password_edit_txt.getText().toString();

        if (email_id.isEmpty()) {
            email_edit_txt.requestFocus();
            Helper.getInstance().pushToast(this, "Please enter email address");
            return false;
        } else if (!Helper.validEmail(email_id)) {
            email_edit_txt.requestFocus();
            Helper.getInstance().pushToast(this, "Please enter valid email address");
            return false;
        } else if (password.isEmpty() || password.equals("null")) {
            password_edit_txt.requestFocus();
            Helper.getInstance().pushToast(LoginActivity.this, "Please enter your password");
            return false;
        } else {
            return true;
        }
    }

    private void getLogin() {


        if (Utilities.getConnectivityStatus(LoginActivity.this) <= 0) {
            Helper.getInstance().pushToast(LoginActivity.this, "Please check your network connection...");
            return;
        }

        String email_id = email_edit_txt.getText().toString().trim();
        String password = password_edit_txt.getText().toString().trim();

        openProgess();

        if(device_fcm_token==null || device_fcm_token.equals("null"))
        {
            device_fcm_token="";
        }

        Helper.getInstance().LogDetails("getLogin details", email_id + " " + password + " " +  ApiEndPoint.token + " " + device_fcm_token + " ");

        Helper.getInstance().LogDetails("FCM TOKEN ", device_fcm_token);

        retrofit2.Call<LoginApi.LoginResponse> call = LoginApi.getApiService().getLoginDetails(email_id, password, ApiEndPoint.token, device_fcm_token, "2");

        call.enqueue(new Callback<LoginApi.LoginResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<LoginApi.LoginResponse> call, @NonNull Response<LoginApi.LoginResponse> response) {
                closeProgress();

                LoginApi.LoginResponse data = response.body();

                if (data != null) {

                    if (data.isSuccess()) {

                        Helper.getInstance().LogDetails("getLogin details", "res " + data.getData().toString());


                        Session.createLoginSession(LoginActivity.this);
                        Session.saveUserID(context,data.getData().getUserId()+"");
                        Session.saveUserName(context,data.getData().getUserName());
                        Session.saveUserDisplayName(context,data.getData().getDisplay_name());
                        Session.saveUserEmail(context,data.getData().getEmail());
                        Session.saveUserRole(context,data.getData().getRole()+"");
                        Session.saveTmUserId(context,data.getData().getTmUserId());
                        Session.saveAccountId(context,data.getData().getAccountId());
                        Session.saveSiteId(context,data.getData().getSiteId());
                        Session.saveCompanyToken(context,data.getData().getCompany_token());
                        Session.saveCompanySocketToken(context,data.getData().getCompany_socket_key());
                        Session.saveCompanyName(context,data.getData().getCompanyName());
                        Session.setIsSelf(context,true);


                        List<SitesInfo> sitesInfoList = new ArrayList<>();
                        Session.saveSiteInfoList(LoginActivity.this, sitesInfoList, Session.SP_SITE_INFO);
                        if (data.getSitesInfo() != null && data.getSitesInfo().size() > 0) {
                            sitesInfoList = data.getSitesInfo();

                            String all_site_ids="";
                            for(int i=0;i<sitesInfoList.size();i++){
                                if(i==0){
                                    Session.saveSiteToken(LoginActivity.this,sitesInfoList.get(i).getSiteToken());
                                }
                                sitesTable.insertSites(sitesInfoList.get(i));
                                all_site_ids=all_site_ids+sitesInfoList.get(i).getSiteId()+",";
                                Helper.getInstance().LogDetails("getLogin details ","site name"+sitesInfoList.get(i).getSiteName());
                            }
                            Helper.getInstance().LogDetails("getLogin details ","size "+sitesInfoList.size()+" "+all_site_ids);

                            Session.saveAllSiteIds(context,all_site_ids);

                           Session.saveSiteInfoList(LoginActivity.this, sitesInfoList, Session.SP_SITE_INFO);

                        }



                        Navigation.getInstance().openRestorePage(LoginActivity.this);
                        finish();



                    } else {
                        Helper.getInstance().pushToast(LoginActivity.this, data.getMessage());
                    }
                } else {
                    Helper.getInstance().LogDetails("getLogin details", "res null");
                    Helper.getInstance().pushToast(LoginActivity.this, "Server connection failed");
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<LoginApi.LoginResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Helper.getInstance().LogDetails("getLogin details", "exception " + t.getLocalizedMessage() + " " + t.getMessage());
                closeProgress();
            }
        });

    }



    private void progressDialog() {

        try {
            if (!(LoginActivity.this).isFinishing()) {
                dialog = new Dialog(LoginActivity.this, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.custom_progress_bar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openProgess() {
        if (!(LoginActivity.this).isFinishing()) {
           /* if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }*/
           login_btn.enableLoadingState();
        }

    }

    private void closeProgress() {
        if (!(LoginActivity.this).isFinishing()) {
           /* if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
                ;
            }*/
           login_btn.disableLoadingState();
        }
    }
}
