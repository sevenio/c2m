package com.tvisha.click2magic.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.TypingMessageTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.Helper.progressButton.ProgressButton;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.ApiResponse;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.model.GetAwsConfigResponse;
import com.tvisha.click2magic.api.post.model.ProfileData;
import com.tvisha.click2magic.api.post.model.ProfileResponse;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.ui.BasicActivity;
import com.tvisha.click2magic.ui.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SettingsFragment extends Fragment {

    @BindView(R.id.old_password_et)
    EditText old_password_et;

    @BindView(R.id.password_et)
    EditText password_et;

    @BindView(R.id.confirm_password_et)
    EditText confirm_password_et;

    @BindView(R.id.display_name_et)
    EditText display_name_et;

    @BindView(R.id.profiler_Name)
    TextView profiler_Name;

    @BindView(R.id.profiler_designation)
    TextView profiler_designation;

    @BindView(R.id.display_name_tv)
    TextView display_name_tv;

    @BindView(R.id.profileImage)
    ImageView profileImage;

    @BindView(R.id.actionSave)
    ProgressButton actionSave;

    @BindView(R.id.editLayout)
    RelativeLayout editLayout;


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @OnClick(R.id.editLayout)
      void edit(){
        if (!isAwsApiCalled) {
            if (Helper.getConnectivityStatus(context)) {
                isEditProfileClicked=true;
                callGetAwsConfig();
            } else {
                Toast.makeText(context, "Please check internet connection", Toast.LENGTH_LONG).show();
            }
        } else {
            if (checkAndRequestPermissions()) {
                // picImage();
                if (Helper.getConnectivityStatus(context)) {
                    openEditProfileOptionsDialog();
                }
                else {
                    Toast.makeText(context, "Please check internet connection", Toast.LENGTH_LONG).show();
                }

            } else {
                Helper.getInstance().LogDetails("picImage", "not called");
            }
        }

    }

    @OnClick(R.id.actionSave)
            void save(){
        validateForm();
    }


    Context context;
    Activity activity;
    View rootView;
    Dialog dialog=null;

    C2mApiInterface apiService;
    Uri selectedFileuri;
    boolean isUploadCompleted=true,isRestart=false;
    String  password = "", oldPassword = "",selectedImagePath="",
            user_name,user_display_name,  role = "0",
             user_avatar = "";;
    String AWS_KEY = "", AWS_SECRET_KEY = "", AWS_BUCKET = "", AWS_REGION = "", AWS_BASE_URL = "",s3Url, AWS_FILE__PATH = "", FILE_NAME = "";


    boolean isAwsApiCalled=false,isEditProfileClicked=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       
        rootView=inflater.inflate(R.layout.fragment_settings, container, false);
        context=HomeActivity.getContext();
        activity=HomeActivity.getActivity();
        ButterKnife.bind(this, rootView);
        progressDialog();
        initViews();
        getSharedPreferenceData();
        processFragment();
        HandlerHolder.settingsFragmentHandler = uiHandler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            disableAutoFill();
        }
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
       activity.getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }
    @Override
    public void onResume() {
        HandlerHolder.settingsFragmentHandler = uiHandler;
        super.onResume();
    }

    @Override
    public void onPause() {
        if(display_name_et!=null){
            Helper.getInstance().closeKeyBoard(context,display_name_et);
        }

        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Helper.getInstance().LogDetails("setUserVisibleHint","called"+isVisibleToUser);
        if(isVisibleToUser){
                if(HandlerHolder.mainActivityUiHandler!=null){
                    HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.PAGE_CHANGED,3).sendToTarget();
                }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }




    private void processFragment(){
        updateRole();
        callProfileApi();
        if(!isAwsApiCalled)
        callGetAwsConfig();
    }
    
    private void initViews(){

        apiService = ApiClient.getClient().create(C2mApiInterface.class);
        

        old_password_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                old_password_et.setCursorVisible(true);
                return false;
            }
        });

        confirm_password_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                confirm_password_et.setCursorVisible(true);
                return false;
            }
        });

        display_name_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                display_name_et.setCursorVisible(true);
                return false;
            }
        });

        password_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password_et.setCursorVisible(true);
                return false;
            }
        });


    }

    private void progressDialog() {

        try {
            if (!(activity).isFinishing()) {
                dialog = new Dialog(activity, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_progress_bar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openProgess() {
        try
        {
            if (!(activity).isFinishing()) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    private void closeProgress() {
        try
        {
            if (!(activity).isFinishing()) {
                if (dialog != null && dialog.isShowing()) {

                    if(isUploadCompleted)
                    dialog.cancel();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getSharedPreferenceData() {

        try{
                role = Session.getUserRole(context);
                user_name =  Session.getUserName(context);
                user_display_name =  Session.getUserDisplayName(context);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void validateForm() {
        try{

            if (display_name_et.getText().toString() == null || display_name_et.getText().toString().trim().isEmpty()) {
                Toast.makeText(context, "Please enter display name", Toast.LENGTH_LONG).show();
                Utilities.openKeyboard(context, display_name_et);
                display_name_et.setCursorVisible(true);
                actionSave.setClickable(true);
            }
            else if (  ( old_password_et.getText().toString() != null && !old_password_et.getText().toString().trim().isEmpty())
                    && ( password_et.getText().toString() == null || password_et.getText().toString().trim().isEmpty())
                    ) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_LONG).show();
                Utilities.openKeyboard(context, password_et);
                password_et.setCursorVisible(true);
                actionSave.setClickable(true);
            } else if ( ( old_password_et.getText().toString() != null && !old_password_et.getText().toString().trim().isEmpty())
                    && (password_et.getText().toString() != null && !password_et.getText().toString().trim().isEmpty())
                    && password_et.getText().toString().trim().length() >0 &&  password_et.getText().toString().length() < 8) {
                Toast.makeText(context, "Password must contain atleast 8 characters", Toast.LENGTH_LONG).show();
                Utilities.openKeyboard(context, password_et);
                actionSave.setClickable(true);

            } else if ( ( old_password_et.getText().toString() != null && !old_password_et.getText().toString().trim().isEmpty()) &&

                    (password_et.getText().toString() != null && !password_et.getText().toString().trim().isEmpty())

                    && (  confirm_password_et.getText().toString() == null || confirm_password_et.getText().toString().trim().isEmpty())
                    ){
                Toast.makeText(context, "Please enter confirm password", Toast.LENGTH_LONG).show();
                Utilities.openKeyboard(context, confirm_password_et);
                confirm_password_et.setCursorVisible(true);
                actionSave.setClickable(true);
            } else if (

                    ( old_password_et.getText().toString() != null && !old_password_et.getText().toString().trim().isEmpty()) &&

                            (password_et.getText().toString() != null && !password_et.getText().toString().trim().isEmpty()) &&

                            (confirm_password_et.getText().toString() != null && !confirm_password_et.getText().toString().trim().isEmpty()) &&

                            !confirm_password_et.getText().toString().trim().equals(password_et.getText().toString().trim()) ) {
                Toast.makeText(context, "Password and Confirm password must be same", Toast.LENGTH_LONG).show();
                Utilities.openKeyboard(context, confirm_password_et);
                actionSave.setClickable(true);
            } else {

                Utilities.closeKeyboard(context, activity);
                password = password_et.getText().toString().trim();
                oldPassword = old_password_et.getText().toString().trim();
                if(display_name_et.getText().toString()!=null && !display_name_et.getText().toString().trim().isEmpty())
                {
                    user_display_name=display_name_et.getText().toString().trim();
                }
                else
                {
                    user_display_name="";
                }


                if (Helper.getConnectivityStatus(context)) {
                    //openProgess();
                    actionSave.enableLoadingState();
                    callUpdateProfileApi();
                } else {
                    Toast.makeText(context, "Please check internet connection", Toast.LENGTH_LONG).show();

                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callProfileApi() {
        try{

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }

            Helper.getInstance().LogDetails("callProfileApi", ApiEndPoint.token + " " + Session.getCompanyToken(context) + " " + Session.getUserID(context));
            if(!isRestart)
            {
              //  isRestart=true;
                //openProgess();
            }


            Call<ProfileResponse> call = apiService.getProfile(ApiEndPoint.token, Session.getCompanyToken(context), Session.getUserID(context));
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    ProfileResponse apiResponse = response.body();
                    closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {
                            if (apiResponse.getData() != null) {
                                Helper.getInstance().LogDetails("callProfileApi", "res " + apiResponse.getData().toString());
                                setProfileData(apiResponse.getData());
                            }

                        }

                    }

                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {

                    closeProgress();
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setProfileData(ProfileData data) {
        try{
            Session.saveUserName(context,data.getUserName());
            Session.saveUserDisplayName(context,data.getDisplay_name());
            Session.saveUserEmail(context,data.getEmail());
            Session.saveUserRole(context,data.getRole());
            Session.saveUserPic(context,data.getProfilePic());

            user_name = data.getUserName();
            user_avatar = data.getProfilePic();
            user_display_name=data.getDisplay_name();

            setUserNames();
            updateProfile();
        }catch (Exception e){
            e.printStackTrace();
        }
        //role=data.getRole();
    }

    private void setUserNames(){


        if(user_name!=null && !user_name.trim().isEmpty())
        {
            profiler_Name.setText(Helper.getInstance().capitalize(user_name));
        }
        else{
            profiler_Name.setText("");
        }

        if(user_display_name!=null && !user_display_name.trim().isEmpty())
        {

            display_name_et.setText(Helper.getInstance().capitalize(user_display_name));
        }
        else{

            display_name_et.setText("");
        }

    }



    private void openEditProfileOptionsDialog() {

        try {
            final Dialog dialog = new Dialog(context, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            dialog.getWindow().setAttributes(lp);
            dialog.setContentView(R.layout.edit_profile_options_layout);

            TextView pic_camera, pic_gallary, remove__photo;
            View line;

            pic_gallary = dialog.findViewById(R.id.pic_gallary);
            remove__photo = dialog.findViewById(R.id.remove__photo);
            pic_camera = dialog.findViewById(R.id.pic_camera);


            pic_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    camera();
                    dialog.cancel();
                }
            });


            remove__photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();

                }
            });

            pic_gallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gallery();
                    dialog.cancel();

                }
            });


            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void camera() {
        try{
            if(HandlerHolder.mainActivityUiHandler!=null){
                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.OPEN_CAMERA);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gallery() {
        try{
            if(HandlerHolder.mainActivityUiHandler!=null){
                HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.OPEN_GALLERY);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callUpdateProfileApi() {
        try{

            Helper.getInstance().LogDetails("uploadImageAWS callUpdateProfileApi", "called" + ApiEndPoint.token + Session.getCompanyToken(context) + Session.getUserID(context) + " " + AWS_FILE__PATH+"  "+user_display_name);

            if (!Helper.getConnectivityStatus(context)) {
                Toast.makeText(context, "Please check internet connection", Toast.LENGTH_LONG).show();
                actionSave.disableLoadingState();
                closeProgress();
                return;
            }

            Call<ApiResponse> call = apiService.updateProfile(ApiEndPoint.token, Session.getCompanyToken(context), Session.getUserID(context), oldPassword, password, user_avatar,user_display_name);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    ApiResponse apiResponse = response.body();
                    actionSave.disableLoadingState();
                   closeProgress();
                    if (apiResponse != null) {

                        actionSave.setClickable(true);
                        if (apiResponse.isSuccess()) {

                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            old_password_et.setText("");
                            password_et.setText("");
                            confirm_password_et.setText("");
                            confirm_password_et.setCursorVisible(false);
                            display_name_et.setCursorVisible(false);
                            password_et.setCursorVisible(false);
                            old_password_et.setCursorVisible(false);
                            oldPassword="";
                            password="";
                            Session.saveUserPic(context,user_avatar);
                            updateProfile();

                        } else {
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    actionSave.setClickable(true);
                    actionSave.disableLoadingState();
                    closeProgress();

                }

            });
        }catch (Exception e){
            e.printStackTrace();
            closeProgress();
        }
    }

    private void updateProfile() {
        try{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (user_avatar != null && !user_avatar.isEmpty()) {
                        profileImage.setVisibility(View.VISIBLE);


                        progressBar.setVisibility(View.VISIBLE);

                        RequestOptions options = new RequestOptions()
                                .error(R.drawable.profile)
                                .disallowHardwareConfig()
                                .priority(Priority.HIGH);
                        Glide.with(context)
                                .load(ApiEndPoint.s3Url + user_avatar)
                                .apply(options)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(profileImage);


                    } else {
                        profileImage.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.profile).into(profileImage);
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try{
            switch (requestCode) {
                case Values.MyActionsRequestCode.CAMERA_IMAGE:
                    Helper.getInstance().LogDetails("onActivityResult","CAMERA_IMAGE");
                    if (selectedFileuri != null) {
                        selectedImagePath = getRealPathFromURI(selectedFileuri);

                        if (selectedImagePath != null) {
                            cropImage();

                        }
                    }

                    break;
                case Values.MyActionsRequestCode.GALLERY_IMAGE:
                    Helper.getInstance().LogDetails("onActivityResult","GALLERY_IMAGE");
                    if (data!=null && data.getData() != null) {

                        Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            selectedImagePath = getRealPathFromURI(data.getData());
                        }

                        selectedFileuri = selectedImageUri;
                        if (selectedFileuri != null) {
                            cropImage();
                        }
                        if (selectedImagePath != null) {

                        }

                    }
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    Helper.getInstance().LogDetails("onActivityResult","CROP_IMAGE_ACTIVITY_REQUEST_CODE");
                    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                        CropImage.ActivityResult result = CropImage.getActivityResult(data);

                        Bitmap bitmap = null;
                        try {


                            File file = new File(result.getUri().getPath());
                            if (result.getUri().getPath() != null) {

                                Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","before compress "+result.getUri().getPath()+" "+file.getPath());

                                long fileSizeInBytes = file.length();
                                // Convert  the bytes to Kilobytes (1 KB = 1024 Bytes)
                                long fileSizeInKB = fileSizeInBytes / 1024;
                                // Convert  the KB to MegaBytes (1 MB = 1024 KBytes)
                                long fileSizeInMB = fileSizeInKB / 1024;

                                if(fileSizeInMB>=1)
                                {
                                    Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","before size "+fileSizeInMB+"Mb");

                                }
                                else
                                {
                                    Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","before size "+fileSizeInKB+"Kb");

                                }
                            }



                          /*  if(file!=null){

                                //   Bitmap  compressedImageBitmap = new Compressor(this).compressToBitmap(file);
                                Bitmap  compressedImageBitmap  = new Compressor(this)
                                        .setMaxWidth(400)
                                        .setMaxHeight(400)
                                        .setQuality(100)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToBitmap(file);
                                if(compressedImageBitmap!=null){
                                    Helper.getInstance().LogDetails("onActivityResult","compressedImageBitmap not null");
                                    bitmap=compressedImageBitmap;
                                }
                                else
                                {
                                    Helper.getInstance().LogDetails("onActivityResult","compressedImageBitmap  null");
                                }
                            }*/
                            if(file!=null){
                                File compressedImageFile = new Compressor(context)
                                        .setMaxWidth(400)
                                        .setMaxHeight(400)
                                        .setQuality(100)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .compressToFile(file);

                                if(compressedImageFile!=null){
                                    Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","after compress "+compressedImageFile.getPath()+" "+file.getPath());
                                    long fileSizeInBytes = compressedImageFile.length();
// Convert                      the bytes to Kilobytes (1 KB = 1024 Bytes)
                                    long fileSizeInKB = fileSizeInBytes / 1024;
// Convert                      the KB to MegaBytes (1 MB = 1024 KBytes)
                                    long fileSizeInMB = fileSizeInKB / 1024;

                                    if(fileSizeInKB<=500){

                                        if(compressedImageFile.getPath()!=null)
                                        {
                                            uploadImageAWS(compressedImageFile.getPath());
                                        }

                                    }
                                    else
                                    {
                                        Toast.makeText(context,"You can't upload more than 500kb image",Toast.LENGTH_LONG).show();
                                    }


                                    if(fileSizeInMB>=1)
                                    {
                                        Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","after size "+fileSizeInMB+"Mb");

                                    }
                                    else
                                    {
                                        Helper.getInstance().LogDetails("CROP_IMAGE_ACTIVITY_REQUEST_CODE","after size "+fileSizeInKB+"Kb");

                                    }


                                }


                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    break;

               

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void cropImage() {

        try{
            CropImage.activity(selectedFileuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setBorderLineColor(getResources().getColor(R.color.colorAccent))
                    .setGuidelinesColor(getResources().getColor(R.color.colorAccent))
                    .setMinCropWindowSize(600,600)
                    //  .setMinCropResultSize(1000,1000)
                    // .setMaxCropResultSize(2000,2000)
                    .setAspectRatio(2,2)
                    .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                    .setShowCropOverlay(true)
                    // .setSnapRadius(0)
                    .setBorderCornerColor(Color.BLUE)
                    .setAutoZoomEnabled(false)
                    .start(activity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        }
    }

    private void uploadImageAWS(String path) {

        try
        {

            if(!Helper.getConnectivityStatus(context))
            {
                Toast.makeText(context,"Please check internet connection",Toast.LENGTH_LONG).show();
                return;
            }



            Helper.getInstance().LogDetails("uploadImageAWS", "called" + " " + path);
            File file = new File(path);
            BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
            AmazonS3Client s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(AWS_BASE_URL);
            s3Client.setRegion(Region.getRegion(AWS_REGION));

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(context)
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            if (file != null && file.exists()) {
                String fileName = file.getName();
                String AWS_FILE_KEY = "user_avatar/" + fileName.trim().replace(" ", "");
                FILE_NAME = AWS_FILE_KEY;


                Helper.getInstance().LogDetails("uploadImageAWS", " called  " + FILE_NAME);

                TransferObserver uploadObserver = transferUtility.upload(AWS_BUCKET, AWS_FILE_KEY, file, new ObjectMetadata(),
                        CannedAccessControlList.PublicRead);




                openProgess();
                isUploadCompleted = false;
                uploadObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState transferState) {
                        try {
                            if (transferState == TransferState.COMPLETED) {

                                AWS_FILE__PATH = FILE_NAME;

                                user_avatar=AWS_FILE__PATH;

                                Helper.getInstance().LogDetails("uploadImageAWS", "completed" + AWS_FILE__PATH);
                                isUploadCompleted = true;
                                callUpdateProfileApi();


                            } else {
                                Helper.getInstance().LogDetails("uploadImageAWS", "not completed");
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(context, "Error while uploading file", Toast.LENGTH_LONG).show();
                            Helper.getInstance().LogDetails("uploadImageAWS", " exception  " + ex.getLocalizedMessage());
                            closeProgress();

                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                        Helper.getInstance().LogDetails("uploadImageAWS","per "+percentDone);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                      activity.  runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isUploadCompleted=true;
                                closeProgress();
                            }
                        });

                        Toast.makeText(context, "Error while uploading file", Toast.LENGTH_LONG).show();
                        Helper.getInstance().LogDetails("uploadImageAWS", "onError " + ex.getLocalizedMessage() + " " + ex.getCause());

                    }

                });
            } else {
                closeProgress();
                Toast.makeText(context, "No file found", Toast.LENGTH_LONG).show();

            }
        }catch (Exception e){
            closeProgress();
            Toast.makeText(context, "Error while uploading file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    private void callGetAwsConfig() {
        try
        {
            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }

            Helper.getInstance().LogDetails("callGetAwsConfig", ApiEndPoint.token + " " + Session.getCompanyToken(context) + " " + Session.getUserID(context));

            // openProgess();
            Call<GetAwsConfigResponse> call = apiService.getAwsConfig(ApiEndPoint.token, Session.getCompanyToken(context), Session.getUserID(context));
            call.enqueue(new Callback<GetAwsConfigResponse>() {
                @Override
                public void onResponse(Call<GetAwsConfigResponse> call, Response<GetAwsConfigResponse> response) {
                    GetAwsConfigResponse apiResponse = response.body();
                    // closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {

                            isAwsApiCalled = true;
                            decryptData(apiResponse);
                            if(isEditProfileClicked){
                                isEditProfileClicked=false;
                                if (checkAndRequestPermissions()) {
                                    // picImage();
                                    openEditProfileOptionsDialog();
                                } else {
                                    Helper.getInstance().LogDetails("picImage", "not called");
                                }
                            }


                        }

                    }

                }

                @Override
                public void onFailure(Call<GetAwsConfigResponse> call, Throwable t) {


                    // closeProgress();

                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkAndRequestPermissions() {

        try{
            int permissionCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

            int writeStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }

            if (writeStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[0]),
                        Values.Permissions.CAMERA_AND_STORAGE_PERMISSION);
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Helper.getInstance().LogDetails("onRequestPermissionsResult",requestCode+"");
        try{
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case Values.Permissions.CAMERA_AND_STORAGE_PERMISSION:
                        if (checkAndRequestPermissions()) {
                            openEditProfileOptionsDialog();
                        }
                        break;

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void disconnectSockets() {
        disconnectSocket();
        disconnectTmSocket();

    }

    private void disconnectTmSocket() {
        try{
            AppSocket application = (AppSocket) activity.getApplication();
            application.disconnectTmSocket();
            BasicActivity. tmSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void disconnectSocket() {
        try{
            AppSocket application = (AppSocket) activity.getApplication();
            application.disconnectSocket();
            BasicActivity.mSocket=null;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void decryptData(GetAwsConfigResponse apiResponse) {

        try {

            String base64 = apiResponse.getData();
            byte[] data = Base64.decode(base64, Base64.DEFAULT);

            Helper.getInstance().LogDetails("decryptData", apiResponse.toString());

            try {
                String text = new String(data, "UTF-8");


                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    if (jsonObject != null) {

                        Helper.getInstance().LogDetails("decryptData", jsonObject.toString());

                        AWS_KEY = jsonObject.optString("key");
                        AWS_SECRET_KEY = jsonObject.optString("secret");
                        AWS_BUCKET = jsonObject.optString("bucket");
                        AWS_REGION = jsonObject.optString("region");
                        AWS_BASE_URL = jsonObject.optString("endpoint");
                        if(AWS_BASE_URL!=null && AWS_BUCKET!=null && !AWS_BASE_URL.trim().isEmpty() && !AWS_BUCKET.trim().isEmpty()){
                            s3Url=AWS_BASE_URL.replace("\"","")+"/"+AWS_BUCKET+"/";
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateRole() {
        try{
            if (role != null && !role.isEmpty()) {
                switch (Integer.parseInt(role)) {
                    case Values.UserRoles.ADMIN:
                        profiler_designation.setText("Admin");

                        break;
                    case Values.UserRoles.SUPERVISOR:
                        profiler_designation.setText("Supervisor");

                        break;
                    case Values.UserRoles.AGENT:
                        profiler_designation.setText("Agent");

                        break;
                    case Values.UserRoles.MODERATOR:

                        profiler_designation.setText("Moderator");

                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case Values.RecentList.CROP_RESULT:
                        Helper.getInstance().LogDetails("onActivityResult","CROP_RESULT called ===");
                        JSONObject jsonObject=(JSONObject) msg.obj;
                        if(jsonObject!=null){
                            String path=jsonObject.optString("path");
                            if(path!=null){
                                uploadImageAWS(path);
                            }
                        }
                        break;
                    case Values.RecentList.TAB_CLICKED:
                        Helper.getInstance().LogDetails("TAB_CLICKED","called");
                        callProfileApi();
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
