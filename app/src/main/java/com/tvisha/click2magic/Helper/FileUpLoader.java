package com.tvisha.click2magic.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;

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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.model.GetAwsConfigResponse;
import com.tvisha.click2magic.chatApi.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class FileUpLoader extends AsyncTask<String, String, String> {

    //CONSTANTS

    public static final int UPLOAD_ERROR=6;
    public static final int UPLOAD_COMPETED=7;
    public static final int UPLOAD_START=8;
    public static final int UPLOAD_STARTED=9;
    public static final int UPLOAD_PROGRESS=10;
    public static final int UPLOAD_ERROR_NETWORK = 11;

    Context context;
    String referenceId;
    Handler handler;
    String uploadPath;
    String userName = "",conversationReferenceId="";
    String AWS_KEY="",AWS_SECRET_KEY="",AWS_BASE_URL="",AWS_REGION="",AWS_BUCKET="",FILE_NAME="",AWS_FILE__PATH="",s3Url="";


    public FileUpLoader(Handler handler, Context context, String referenceId, String userName, String conversationReferenceId,String AWS_KEY,String AWS_SECRET_KEY
    ,String AWS_BASE_URL,String AWS_REGION,String AWS_BUCKET,String s3Url) {
        this.context = context;
        this.referenceId = referenceId;
        this.handler=handler;
        this.userName=userName;
        this.conversationReferenceId=conversationReferenceId;
        this.AWS_KEY=AWS_KEY;
        this.AWS_SECRET_KEY=AWS_SECRET_KEY;
        this.AWS_BASE_URL=AWS_BASE_URL;
        this.AWS_REGION=AWS_REGION;
        this.AWS_BUCKET=AWS_BUCKET;
        this.s3Url=s3Url;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // showDialog(progress_bar_type);
        if(handler!=null){
            handler.sendEmptyMessage(UPLOAD_STARTED);
        }

        updateProgress("0%");
        Helper.getInstance().LogDetails("FileUpLoader","onPreExecute called");
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        try {
            Helper.getInstance().LogDetails("FileUpLoader doInBackground","called");
            if(AWS_BASE_URL!=null && !AWS_BASE_URL.trim().isEmpty())
            {
                Helper.getInstance().LogDetails("FileUpLoader doInBackground","if called");
                uploadImageAWS(f_url[0]);
            }
            else
            {
                if (Helper.getConnectivityStatus(context)) {
                    Helper.getInstance().LogDetails("FileUpLoader doInBackground","else called");
                    callGetAwsConfig(f_url[0]);
                }

            }

        } catch (Exception e) {
            Helper.getInstance().LogDetails("FileUpLoader downloadImagesToSdCard",e.getMessage());
        }
        return null;
    }

    public void updateProgress(String progress){
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("referenceId",referenceId);
            jsonObject.put("progress",progress);
            if(handler!=null){
                handler.obtainMessage(UPLOAD_PROGRESS,jsonObject).sendToTarget();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callGetAwsConfig(final String path) {

        try {

            String company_token= Session.getCompanyToken(context);
            String user_id= Session.getUserID(context);
            C2mApiInterface   c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);
            Helper.getInstance().LogDetails("FileUpLoader callGetAwsConfig", ApiEndPoint.token + " " + company_token + " " + user_id);
            // openProgess();
            Call<GetAwsConfigResponse> call = c2mApiService.getAwsConfig(ApiEndPoint.token, company_token, user_id);
            call.enqueue(new Callback<GetAwsConfigResponse>() {
                @Override
                public void onResponse(Call<GetAwsConfigResponse> call, Response<GetAwsConfigResponse> response) {
                    GetAwsConfigResponse apiResponse = response.body();
                    // closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {

                            decryptData(apiResponse,path);

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("FileUpLoader callGetAwsConfig", "false");
                        }

                    }
                    else
                    {
                        Helper.getInstance().LogDetails("FileUpLoader callGetAwsConfig", "null");
                    }

                }

                @Override
                public void onFailure(Call<GetAwsConfigResponse> call, Throwable t) {
                    Helper.getInstance().LogDetails("FileUpLoader callGetAwsConfig", "");
                    // closeProgress();
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void decryptData(GetAwsConfigResponse apiResponse, String path) {
        try {
/*{"key":"AKIA5QVMZCPI35BI5JF5",
                                    "secret":"BJy9p\/Qy7Qo91HyRN8GpC+LnOGo6a09AS362oG\/M",
                                    "region":"us-east-1","endpoint":"https:\/\/s3.amazonaws.com","bucket":"files.c2m"}*/
            String base64 = apiResponse.getData();
            byte[] data = Base64.decode(base64, Base64.DEFAULT);
            Helper.getInstance().LogDetails("FileUpLoader decryptData", apiResponse.toString());
            try {
                String text = new String(data, "UTF-8");


                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    if (jsonObject != null) {

                        //s3Url  https://s3.amazonaws.com/files.c2m/

                        Helper.getInstance().LogDetails("FileUpLoader decryptData after", jsonObject.toString());

                        AWS_KEY = jsonObject.optString("key");
                        AWS_SECRET_KEY = jsonObject.optString("secret");
                        AWS_BUCKET = jsonObject.optString("bucket");
                        AWS_REGION = jsonObject.optString("region");
                        AWS_BASE_URL = jsonObject.optString("endpoint");
                        if(AWS_BASE_URL!=null && AWS_BUCKET!=null && !AWS_BASE_URL.trim().isEmpty() && !AWS_BUCKET.trim().isEmpty()){
                            s3Url=AWS_BASE_URL.replace("\"","")+"/"+AWS_BUCKET+"/";
                            Helper.getInstance().LogDetails("FileUpLoader decryptData s3Url", s3Url);
                            uploadImageAWS(path);
                        }


                      //


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadImageAWS(final String path) {
        try {

            Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", "called" + " " + path);
            File file = new File(path);

            updateAndroidSecurityProvider(context);
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

                final String fileName = file.getName();
                String AWS_FILE_KEY = "user_avatar/" + fileName.trim().replace(" ", "");
                FILE_NAME = AWS_FILE_KEY;
                Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", " called  " + FILE_NAME);
                InputStream targetStream = new FileInputStream(file);

                TransferObserver uploadObserver = transferUtility.upload(AWS_BUCKET, AWS_FILE_KEY, file, new ObjectMetadata(), CannedAccessControlList.PublicRead);

                uploadObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState transferState) {
                        try {
                            if (transferState == TransferState.COMPLETED) {

                                AWS_FILE__PATH = FILE_NAME;

                                Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", "completed  " + s3Url + AWS_FILE__PATH);
                                //sendAttachMent(s3Url + AWS_FILE__PATH, fileName, "", path);
                                if((new ConversationTable(context)).updateAwsAttachmentPath(s3Url + AWS_FILE__PATH,referenceId)){
                                   uploadPath=s3Url + AWS_FILE__PATH;
                                }
                            } else {
                                Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", "not completed");
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", " exception  " + ex.getLocalizedMessage());


                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Helper.getInstance().LogDetails("FileUpLoader uploadImageAWS", "onError " + ex.getLocalizedMessage() + " " + ex.getCause());
                        if (Helper.getConnectivityStatus(context)) {
                            if(handler!=null){
                                handler.obtainMessage(UPLOAD_ERROR, referenceId).sendToTarget();
                            }

                        }else {
                            if(handler!=null){
                                handler.obtainMessage(UPLOAD_ERROR_NETWORK, referenceId).sendToTarget();
                            }

                        }
                    }

                });
            } else {

                Helper.getInstance().LogDetails("FileUpLoader no file found", "");


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(String file_url) {

        try{
            if(uploadPath !=null){
                Helper.getInstance().LogDetails("FileUpLoader onPostExecute", "called");
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("referenceId",referenceId);
                jsonObject.put("path", uploadPath);
                if(handler!=null){
                    handler.obtainMessage(UPLOAD_COMPETED,jsonObject).sendToTarget();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateAndroidSecurityProvider(Context context) {
        try {
            ProviderInstaller.installIfNeeded(context);
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLEngine engine = sslContext.createSSLEngine();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            //GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), context, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Helper.getInstance().LogDetails("FileUpLoader SecurityException", "Google Play Services not available.");
        }
    }
}

