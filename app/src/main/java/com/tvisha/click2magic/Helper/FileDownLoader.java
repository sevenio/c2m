package com.tvisha.click2magic.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.tvisha.click2magic.DataBase.ConversationTable;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;


public class FileDownLoader extends AsyncTask<String, String, String> {

    //CONSTANTS
    public static final int DOWNLOAD_ERROR=0;
    public static final int DOWNLOAD_COMPETED=1;
    public static final int DOWNLOAD_START=2;
    public static final int DOWNLOAD_STARTED=3;
    public static final int DOWNLOAD_PROGRESS=4;
    public static final int DOWNLOAD_ERROR_NETWORK = 5;

    Context context;
    String msg_id;

    Handler handler;
    String downloadedPath;
    String userName = "",conversationReferenceId="";

    public FileDownLoader(Handler handler, Context context, String msg_id, String userName,String conversationReferenceId) {
        this.context = context;
        this.msg_id = msg_id;
        this.handler=handler;
        this.userName=userName;
        this.conversationReferenceId=conversationReferenceId;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // showDialog(progress_bar_type);
        handler.sendEmptyMessage(DOWNLOAD_STARTED);
        updateProgress("0%");
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        try {
            Helper.getInstance().LogDetails("FileDownLoader downloadImagesToSdCard","called");
            downloadImagesToSdCard(f_url[0]);
        } catch (Exception e) {
            Helper.getInstance().LogDetails("FileDownLoader downloadImagesToSdCard",e.getMessage());
        }
        return null;
    }

    public void updateProgress(String progress){
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("msgId",msg_id);
            jsonObject.put("progress",progress);
            handler.obtainMessage(DOWNLOAD_PROGRESS,jsonObject).sendToTarget();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String downloadImagesToSdCard(String downloadUrl) {
        try {
            Helper.getInstance().LogDetails("FileDownLoader downloadImagesToSdCard",downloadUrl);
            updateAndroidSecurityProvider(context);
            String raw_data_path[] = downloadUrl.split("/");
            String file_name = raw_data_path[raw_data_path.length - 1].replaceAll("[^A-Za-z0-9 .]","_");
            String[] file_name2 = file_name.split("/");
            if ((file_name2.length-1)>0) {
                file_name = file_name2[file_name2.length - 1];
            }else {
                file_name = raw_data_path[raw_data_path.length - 1].replaceAll("[^A-Za-z0-9 .]","_");
            }
            if (file_name.length()>0) {
                String[] file = file_name.split("\\.");
                if (file[0]==null || file[0].trim().isEmpty() || file[0].trim().equals("null")) {
                    file_name = "click_two_magic"+"."+file_name;
                }
            }
            if (userName==null || userName.trim().isEmpty() || userName.equals("null")) {
                userName="c2m";
            }

            URL url = new URL(downloadUrl.replaceAll(" ", "%20"));

            File file = FileFormatHelper.getInstance().createImageFile(file_name,userName,context);

            if (file.exists()) {
                Random random = new Random();
                int code = (100000 + random.nextInt(900000));
                file = FileFormatHelper.getInstance().createImageFile(code+"_"+file_name,userName,context);
            }
            if (!file.exists()) {

                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                if (!file.exists())
                    file.createNewFile();

                //file.createNewFile();
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLEngine engine = sslContext.createSSLEngine();

                URLConnection ucon = url.openConnection();
                InputStream inputStream = null;
                HttpURLConnection httpConn = (HttpURLConnection) ucon;
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }
                FileOutputStream fos = new FileOutputStream(file);
                int totalSize = httpConn.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[totalSize];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    updateProgress(Helper.getInstance().getPersentage(downloadedSize,totalSize)+"%");
                }
                fos.close();
            }
            if((new ConversationTable(context)).updateAttachment(file.getPath(), msg_id,downloadUrl,conversationReferenceId)){
                (new FileFormatHelper()).galleryAddPic(file,context);
                downloadedPath= file.getPath();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (Helper.getConnectivityStatus(context)) {
                handler.obtainMessage(DOWNLOAD_ERROR, msg_id).sendToTarget();
            }else {
                handler.obtainMessage(DOWNLOAD_ERROR_NETWORK, msg_id).sendToTarget();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {

        try{
            if(downloadedPath!=null){   
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("msgId",msg_id);
                jsonObject.put("path",downloadedPath);

                handler.obtainMessage(DOWNLOAD_COMPETED,jsonObject).sendToTarget();
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
            Helper.getInstance().LogDetails("FileDownLoader SecurityException", "Google Play Services not available.");
        }
    }
}

