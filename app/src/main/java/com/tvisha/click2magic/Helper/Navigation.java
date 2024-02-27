package com.tvisha.click2magic.Helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.FragmentManager;

import android.widget.Toast;


import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.attachmentViewer.DocumentViewerFragment;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.ui.ArchivesActivity;
import com.tvisha.click2magic.ui.AttachmentViewActivity;
import com.tvisha.click2magic.ui.ChatActivity;
import com.tvisha.click2magic.ui.DocsActivity;
import com.tvisha.click2magic.ui.FilterActivity;
import com.tvisha.click2magic.ui.HomeActivity;
import com.tvisha.click2magic.ui.ImageviewActivity;
import com.tvisha.click2magic.ui.LoginActivity;
import com.tvisha.click2magic.ui.SyncDataActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Navigation
{
    private static Navigation ourInstance = new Navigation();
    Context context;

    public static Navigation getInstance() {
        if (ourInstance == null) {
            ourInstance = new Navigation();
        }
        return ourInstance;
    }


    public void openHomePage(Context context)
    {
        try{

            Intent intent = new Intent(context,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void openRestorePage(Context context)
    {
        try{
            Intent intent = new Intent(context,SyncDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void openLoginPage(Context context){
        try {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void openArchiesActivity(Context context){
        try {
            Intent intent = new Intent(context, ArchivesActivity.class);
            context.startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void openAttachmentActivity(Context context,String attachMentPath) {
        Intent intent=new Intent(context,AttachmentViewActivity.class);
        intent.putExtra(Values.IntentData.ATTACHMENT_PATH,attachMentPath);
        ((Activity) context).startActivityForResult(intent, Values.MyActionsRequestCode.DISPLAY_ATTACHMENT);
    }

    public void imageViewer(Context context, String imagePath, String receiver_id,String sender_id  ,String conversationReferenceId,String receiver_name,String user_name) {

        Intent intent = new Intent(context, ImageviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Values.IntentData.IMAGE_PATH, imagePath);
        intent.putExtra(Values.IntentData.RECEIVER_ID, receiver_id);
        intent.putExtra(Values.IntentData.SENDER_ID,sender_id);
        intent.putExtra(Values.IntentData.RECEIVER_NAME,receiver_name);
        intent.putExtra(Values.IntentData.SENDER_NAME,user_name);
        intent.putExtra(Values.IntentData.CONVERSATION_REFERENCE_ID,conversationReferenceId);


        context.startActivity(intent);

    }

    public void openFilterActivity(Context context, List<ActiveAgent> activeAgents,List<SitesInfo> sitesInfoList){
        try {
            Intent intent = new Intent(context, FilterActivity.class);
            if(activeAgents!=null && activeAgents.size()>0){
                intent.putParcelableArrayListExtra(Values.IntentData.ACTIVE_AGENT_LIST, (ArrayList<? extends Parcelable>) activeAgents);
                intent.putParcelableArrayListExtra(Values.IntentData.SITE_LIST, (ArrayList<? extends Parcelable>) sitesInfoList);
            }
            else
            {
                activeAgents=new ArrayList<>();
                intent.putParcelableArrayListExtra(Values.IntentData.ACTIVE_AGENT_LIST, (ArrayList<? extends Parcelable>) activeAgents);
            }

           // context.startActivity(intent);
            ((Activity) context).startActivityForResult(intent, Values.MyActionsRequestCode.FILTER_ARCHIVES);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void openDocsActivity(Context context){
        Intent intent = new Intent(context, DocsActivity.class);
        ((Activity) context).startActivityForResult(intent, Values.MyActionsRequestCode.PICK_DOC);
    }
    public void openFiles(Context ctx, String path,String url, String conversationReferenceId,int messageId ,String receiver_name,String user_name) {

        Helper.getInstance().LogDetails("openFiles",path+"  "+url+"  "+conversationReferenceId+" ");
        context = ctx;
        if(path==null){
            Toast.makeText(context,"Path is empty",Toast.LENGTH_LONG).show();
            return;
        }
        String extention = FileFormatHelper.getInstance().getFileExtentonFromPath(path);
        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.GALLARY_PAGE;
        try {
            Uri uri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = AppFileProvider.getUriForFile(context, "com.tvisha.click2magic.fileprovider", new File(path));
            } else {
                uri = Uri.fromFile(new File(path));
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (extention.endsWith("doc") || extention.endsWith("docx")) {// Word document
                intent.setDataAndType(uri, "application/msword");

            } else if (extention.trim().endsWith("pdf")) {// PDF file
                intent.setDataAndType(uri, "application/pdf");

            } else if (extention.endsWith("ppt") || extention.endsWith("pptx")) {// Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

            } else if (extention.endsWith("xls") || extention.endsWith("xlsx")) {// Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");

            } else if (extention.endsWith("zip")) {// ZIP audio file
                intent.setDataAndType(uri, "application/x-wav");

            } else if (extention.endsWith("rar")) {// rar audio file
                intent.setDataAndType(uri, "application/rar");

            } else if (extention.endsWith("rtf")) {// RTF file
                intent.setDataAndType(uri, "application/rtf");

            } else if (extention.endsWith("wav")) {// WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");

            } else if (extention.endsWith("mp3")) {// MP3 audio file
                intent.setDataAndType(uri, "audio/mp3");

            } else if (extention.endsWith("gif")) {// GIF file
                intent.setDataAndType(uri, "image/gif");

            } else if (extention.endsWith("jpg") || extention.endsWith("jpeg") || extention.endsWith("png")) { // JPG/png file
                intent.setDataAndType(uri, "image/jpeg");

            } else if (extention.endsWith("txt")) {// Text file
                intent.setDataAndType(uri, "text/plain");

            } else if (extention.endsWith("3gp") || extention.endsWith("mpg") || extention.endsWith("mpeg") || extention.endsWith("mpe") || extention.endsWith("mp4") || extention.endsWith("avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else if (extention.endsWith("x-vcard") || extention.endsWith("vcf")) {
                intent.setDataAndType(uri, "text/x-vcard");
            } else if (extention.endsWith("apk")) {
                File file = new File(path);

                if (file.exists() && file.length() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = AppFileProvider.getUriForFile(context, "com.tvisha.click2magic.fileprovider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                } else {
                    String paths = FilePathLocator.getPath(context, Uri.parse(path));
                    File file1 = new File(paths);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = AppFileProvider.getUriForFile(context, "com.tvisha.click2magic.fileprovider", file1);
                    } else {
                        uri = Uri.fromFile(file1);
                    }
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = AppFileProvider.getUriForFile(context, "com.tvisha.click2magic.fileprovider", new File(path));
                } else {
                    uri = Uri.fromFile(new File(path));
                }
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);


        } catch (ActivityNotFoundException e) {
            ToastUtil.getInstance().showToast(context, "No application found to complete this action");
            if (path != null && !path.isEmpty() && !path.equals("null")) {
                ConversationTable conversationTables = new ConversationTable(context);
                String paths = conversationTables.getTheDocumentPath(conversationReferenceId,messageId, false);
                if (paths == null || paths.trim().isEmpty() || paths.equals("null")) {
                    paths = conversationTables.getTheDocumentPath(conversationReferenceId,messageId, true);
                }
                int type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(paths);

                if (type != Values.Gallery.GALLERY_ZIP) {

                    if (context instanceof ChatActivity) {
                        if (Helper.getConnectivityStatus(context)) {
                            FragmentManager fragmentManager = ((ChatActivity) context).getSupportFragmentManager();
                            ConversationTable conversationTable = new ConversationTable(context);
                            if (conversationTable != null) {
                                path = conversationTables.getTheDocumentPath(conversationReferenceId,messageId, false);
                                if (path == null || path.trim().isEmpty() || path.equals("null")) {
                                    path = conversationTables.getTheDocumentPath(conversationReferenceId, messageId,true);
                                }
                            }
                            docViewer(fragmentManager, path,url, conversationReferenceId,messageId,receiver_name,user_name);
                        } else {
                            ToastUtil.getInstance().showToast(context, Values.ErrorMessages.NETWORK_ERROR);
                        }
                    }
                } else {
                    ToastUtil.getInstance().showToast(context, "No application found to complete this action");
                }
            } else {
                ToastUtil.getInstance().showToast(context, "Fail to open file..! ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (path != null && !path.isEmpty() && !path.equals("null")) {
                ConversationTable conversationTables = new ConversationTable(context);
                String paths = conversationTables.getTheDocumentPath(conversationReferenceId, messageId,false);
                if (paths == null || paths.trim().isEmpty() || paths.equals("null")) {
                    paths = conversationTables.getTheDocumentPath(conversationReferenceId, messageId,true);
                }
                int type = FileFormatHelper.getInstance().getFileTypeCodeFromPath(paths);

                if (type != Values.Gallery.GALLERY_ZIP) {
                    if (context instanceof ChatActivity) {
                        FragmentManager fragmentManager = ((ChatActivity) context).getSupportFragmentManager();
                        ConversationTable conversationTable = new ConversationTable(context);
                        if (conversationTable != null) {
                            path = conversationTables.getTheDocumentPath(conversationReferenceId,messageId, false);
                            if (path == null || path.trim().isEmpty() || path.equals("null")) {
                                path = conversationTables.getTheDocumentPath(conversationReferenceId,messageId, true);
                            }
                        }
                        docViewer(fragmentManager, path,url, conversationReferenceId,messageId,receiver_name,user_name);
                    }
                } else {
                    ToastUtil.getInstance().showToast(context, "Fail to open file..! ");
                }
            } else {
                ToastUtil.getInstance().showToast(context, "Fail to open file..! ");
            }
        }
    }
    public void docViewer(FragmentManager fragmentManager, String path,String url, String conversationReferenceId,int messageId,String receiver_name ,String user_name) {
        DocumentViewerFragment viewerFragment = new DocumentViewerFragment();
        Bundle args = new Bundle();
        args.putString(Values.IntentData.ATTACHMENT_PATH, path);
        args.putString(Values.IntentData.IMAGE_PATH, url);
        args.putString(Values.IntentData.IMAGE_PATH, url);
        args.putString(Values.IntentData.RECEIVER_NAME,receiver_name);
        args.putString(Values.IntentData.SENDER_NAME,user_name);
        args.putString(Values.IntentData.CONVERSATION_REFERENCE_ID, conversationReferenceId);
        args.putInt(Values.IntentData.MESSAGE_ID, messageId);
        viewerFragment.setArguments(args);
        viewerFragment.show(fragmentManager, viewerFragment.getTag());
    }

}
