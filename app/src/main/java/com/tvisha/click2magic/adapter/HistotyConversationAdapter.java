package com.tvisha.click2magic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;
import com.tvisha.click2magic.DataBase.ChatModel;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.Helper.FileDownLoader;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.ToastUtil;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.ui.HistotyChatActivity;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistotyConversationAdapter extends RecyclerView.Adapter<HistotyConversationAdapter.ViewHolder> {
    public String tmUserId = "", receiverId = "", endTime = "", endDate = "", agent_id, user_id, receiverName, userName;
    public boolean today = false;
    public String nextDate = "";
    public int nextPosition = -1;
    int hour, min, AM_PM, endhour, endmin, END_AM_PM;
    String dateFormat = "HH:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String dateFormat1 = "dd MMM yyyy";
    SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, Locale.US);
    String dateFormat2 = "dd MMM yy HH:mm";
    SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat2, Locale.US);
    private List<ChatModel> items = new ArrayList<>();
    private Context context;
    boolean selectionmode=true;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {
                case FileDownLoader.DOWNLOAD_START:

                    try {

                        String messageId = (String) msg.obj;
                        Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_START" + msg.obj.toString());
                        if (messageId != null) {
                            final ChatModel message = getSelectedDownloadingObject(Integer.parseInt(messageId));
                            if (message != null) {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        (new FileDownLoader(handler, context, message.getMessage_id() + "", receiverName, message.getConversation_reference_id())).execute(message.getAttachment().replace("\"", ""));
                                    }
                                };
                                Thread thread = new Thread(runnable);
                                thread.start();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FileDownLoader.DOWNLOAD_STARTED:
                    Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_STARTED");
                    break;
                case FileDownLoader.DOWNLOAD_PROGRESS:

                    try {
                        JSONObject progress = (JSONObject) msg.obj;
                        Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_PROGRESS" + progress.toString());
                        setDownloadProgress(progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FileDownLoader.DOWNLOAD_COMPETED:
                    Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_COMPETED");
                    try {
                        JSONObject object = (JSONObject) msg.obj;
                        onDownloadCompleted(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FileDownLoader.DOWNLOAD_ERROR:
                    Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_ERROR");
                    try {
                        String msgId = (String) msg.obj;
                        onDownloadError(msgId, "Error While downloading");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FileDownLoader.DOWNLOAD_ERROR_NETWORK:
                    Helper.getInstance().LogDetails("FileDownLoader ", "DOWNLOAD_ERROR_NETWORK");
                    try {
                        String msgId = (String) msg.obj;
                        onDownloadError(msgId, Values.ErrorMessages.NETWORK_ERROR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    private long mLastClickTime = 0;
    private Handler mainHandler;

    public HistotyConversationAdapter(Context context, List<ChatModel> items, String receiverId, String receiverName, Handler handler) {
        this.items = items;
        this.context = context;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.mainHandler = handler;

    }

    public void setSelectionmode(boolean selectionmode){
        this.selectionmode=selectionmode;
    }
    @Override
    public HistotyConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_row_design, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        try {

            Helper.getInstance().LogDetails("callGetHistoryApi","adaptet called");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (items.get(i).getMessage_type() == Values.MessageType.MESSAGE_TYPE_ATTACHMENT) {
                        try {
                            if (getItem(i) != null) {
                                if (!getItem(i).isAttachmentDownloaded()) {
                                    dowloadTheFile(i);
                                } else {
                                    viewHolder.receive_message_tv.setVisibility(View.GONE);
                                    String path = items.get(i).getAttachment();

                                    String attachmentType = items.get(i).getAttachment_extension();
                                    if (attachmentType == null || attachmentType.trim().isEmpty()) {
                                        int index = path.lastIndexOf(".");
                                        attachmentType = path.substring(index + 1);
                                    }
                                    if (attachmentType != null && !attachmentType.trim().isEmpty()) {
                                        if (attachmentType.toLowerCase().equals("png") || attachmentType.toLowerCase().equals("jpg") || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") || attachmentType.toLowerCase().equals("gif")) {

                                            //  mainHandler.obtainMessage(Values.SingleUserChat.CHAT_IMAGE_PREVIEW, getItem(i).getAttachment()).sendToTarget();
                                      /*  if(context instanceof HistotyChatActivity){

                                            ((HistotyChatActivity) context).loadAttachment(items.get(i).getAttachment(),items.get(i).getAttachmentDevicePath());

                                        }*/
                                            if (getItem(i).getAttachmentDevicePath() != null) {
                                                if(selectionmode){
                                                    selectionmode=false;
                                                    mainHandler.obtainMessage(Values.SingleUserChat.CHAT_IMAGE_PREVIEW, getItem(i).getAttachmentDevicePath()).sendToTarget();
                                                }

                                            } else {

                                                ConversationTable conversationTable = new ConversationTable(context);
                                                String devicePath = conversationTable.getTheDocumentPath(getItem(i).getConversation_reference_id(), getItem(i).getMessage_id(), true);
                                                Helper.getInstance().LogDetails("openFiles", "=== " + devicePath);
                                                if(selectionmode){
                                                    selectionmode=false;
                                                    mainHandler.obtainMessage(Values.SingleUserChat.CHAT_IMAGE_PREVIEW, devicePath).sendToTarget();
                                                }



                                            }

                                            // Navigation.getInstance().openFiles(context, getItem(i).getAttachmentDevicePath(), getItem(i).getId()+"");
                                        } else {
                                            //  Navigation.getInstance().openFiles(context, getItem(i).getAttachmentDevicePath(),getItem(i).getAttachment(), getItem(i).getConversation_reference_id()+"",getItem(i).getMessage_id(),HistotyChatActivity.receiver_name,HistotyChatActivity.user_name);

                                            if (getItem(i).getAttachmentDevicePath() != null) {
                                                Navigation.getInstance().openFiles(context, getItem(i).getAttachmentDevicePath(), getItem(i).getAttachment(), getItem(i).getConversation_reference_id() + "", getItem(i).getMessage_id(), receiverName, userName);
                                            } else {

                                                ConversationTable conversationTable = new ConversationTable(context);
                                                String devicePath = conversationTable.getTheDocumentPath(getItem(i).getConversation_reference_id(), getItem(i).getMessage_id(), true);
                                                Helper.getInstance().LogDetails("openFiles", "=== " + devicePath);
                                                Navigation.getInstance().openFiles(context, devicePath, getItem(i).getAttachment(), getItem(i).getConversation_reference_id() + "", getItem(i).getMessage_id(), receiverName, userName);

                                            }


                                        }
                                    }


                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


            String senderId = "", createdDate = "", currentDate = "", messageDate = "", chatEndDate = "";
            int is_read = 0, is_delivered = 0, is_sync = 0,messageType=0;


            senderId = String.valueOf(items.get(i).getSender_id());
            is_read = items.get(i).getIs_read();
            is_delivered = items.get(i).getIs_delivered();
            is_sync = items.get(i).getIs_sync();
            createdDate = items.get(i).getCreated_at();
             messageType  = items.get(i).getMessage_type();

            Helper.getInstance().LogDetails("***************","pos "+messageType+" "+createdDate);


            if (senderId != null && tmUserId != null && !senderId.trim().isEmpty() && !tmUserId.trim().isEmpty()  && !senderId.equals("0") && !senderId.trim().equals(tmUserId.trim()) ) {


                viewHolder.sendMessageLayout.setVisibility(View.VISIBLE);
                viewHolder.receiveMessageLayout.setVisibility(View.GONE);
                viewHolder.sendImageLayout.setVisibility(View.GONE);
                viewHolder.sendPdfLayout.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);




                switch (messageType) {
                    case Values.MessageType.MESSAGE_TYPE_TEXT:
                        viewHolder.send_message_tv.setVisibility(View.VISIBLE);
                        viewHolder.sendImageLayout.setVisibility(View.GONE);
                        viewHolder.sendPdfLayout.setVisibility(View.GONE);
                        if(items.get(i).getMessage()!=null)
                        {
                            viewHolder.send_message_tv.setText(items.get(i).getMessage().trim());
                        }

                        break;
                    case Values.MessageType.MESSAGE_TYPE_ATTACHMENT:
                        Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "called");

                        viewHolder.send_message_tv.setVisibility(View.GONE);
                        String devicePath = items.get(i).getAttachmentDevicePath();
                        String path = items.get(i).getAttachment();

                        String attachmentType = items.get(i).getAttachment_extension();
                        String attachmentName = items.get(i).getAttachment_name();
                        if (attachmentType == null || attachmentType.trim().isEmpty()) {
                            int index = path.lastIndexOf(".");
                            String fileType = path.substring(index + 1);
                            if (fileType != null && !fileType.trim().isEmpty()) {
                                attachmentType = fileType.replace(" ", "");
                            }


                            Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "attachmentType null" + index + " " + path + attachmentType);

                        }
                        if (attachmentName == null || attachmentName.trim().isEmpty()) {
                            int index = path.lastIndexOf("/");
                            String name = path.substring(index + 1);
                            if (name != null && !name.trim().isEmpty()) {
                                items.get(i).setAttachment_name(name);
                                attachmentName = name;
                            }
                        }


                        if (attachmentType != null && !attachmentType.trim().isEmpty()) {
                            Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", messageType + " " + items.get(i).getAttachment() + " " + attachmentType + " " + items.get(i).isAttachmentDownloaded());
                            if (attachmentType.toLowerCase().equals("png") || attachmentType.toLowerCase().equals("jpg") || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") || attachmentType.toLowerCase().equals("gif")) {
                                viewHolder.sendImageLayout.setVisibility(View.VISIBLE);
                                viewHolder.sendPdfLayout.setVisibility(View.GONE);

                                if (!items.get(i).isAttachmentDownloaded()) {

                                    if (items.get(i).getDownloadProgress() != null) {
                                        viewHolder.sendDownloadImage.setVisibility(View.GONE);
                                        viewHolder.send_image_download_progress.setText(items.get(i).getDownloadProgress());
                                        if (items.get(i).getDownloadProgress().equals("100%")) {
                                            viewHolder.send_image_download_progress.setVisibility(View.GONE);
                                        } else {
                                            viewHolder.send_image_download_progress.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        viewHolder.sendDownloadImage.setVisibility(View.VISIBLE);
                                        viewHolder.send_image_download_progress.setVisibility(View.GONE);
                                    }

                                } else {
                                    viewHolder.send_image_download_progress.setVisibility(View.GONE);
                                    viewHolder.send_image_download_progress.setText("");
                                    viewHolder.sendDownloadImage.setVisibility(View.GONE);
                                }
                                if (devicePath != null && !devicePath.trim().isEmpty()) {

                                    File imgFile = new File(devicePath);
                                    if (imgFile.exists()) {

                                       // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        if ( !attachmentType.toLowerCase().equals("gif")) {
                                           // viewHolder.send_image_preview.setImageBitmap(myBitmap);
                                            viewHolder.senderImageProgressbar.setVisibility(View.VISIBLE);
                                            RequestOptions options = new RequestOptions()
                                                    .error(R.drawable.ic_attachment_img)
                                                    .disallowHardwareConfig()
                                                    .priority(Priority.HIGH);
                                            Glide.with(context)
                                                    .load(devicePath)
                                                    .apply(options)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(viewHolder.send_image_preview);
                                        } else {
                                            if (path != null && !path.trim().isEmpty()) {
                                                path = path.replace("\"", "");
                                                viewHolder.senderImageProgressbar.setVisibility(View.VISIBLE);
                                                RequestOptions options = new RequestOptions()
                                                        .error(R.drawable.ic_attachment_img)
                                                        .disallowHardwareConfig()
                                                        .priority(Priority.HIGH);
                                                Glide.with(context)
                                                        .load(path)
                                                        .apply(options)
                                                        .listener(new RequestListener<Drawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                                return false;
                                                            }
                                                        })
                                                        .into(viewHolder.send_image_preview);

                                            } else {
                                                viewHolder.senderImageProgressbar.setVisibility(View.VISIBLE);
                                                viewHolder.send_image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                            }
                                        }

                                    } else {
                                        if (path != null && !path.trim().isEmpty()) {
                                            path = path.replace("\"", "");
                                            viewHolder.senderImageProgressbar.setVisibility(View.VISIBLE);
                                            RequestOptions options = new RequestOptions()

                                                    .error(R.drawable.ic_attachment_img)
                                                    .disallowHardwareConfig()
                                                    .priority(Priority.HIGH);
                                            Glide.with(context)
                                                    .load(path)
                                                    .apply(options)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            viewHolder.senderImageProgressbar.setVisibility(View.GONE);

                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(viewHolder.send_image_preview);

                                        } else {
                                            viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                            viewHolder.send_image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                        }
                                    }
                                } else if (path != null && !path.trim().isEmpty()) {
                                    path = path.replace("\"", "");
                                    viewHolder.senderImageProgressbar.setVisibility(View.VISIBLE);
                                    RequestOptions options = new RequestOptions()

                                            .error(R.drawable.ic_attachment_img)
                                            .disallowHardwareConfig()
                                            .priority(Priority.HIGH);
                                    Glide.with(context)
                                            .load(path)
                                            .apply(options)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(viewHolder.send_image_preview);

                                } else {
                                    viewHolder.senderImageProgressbar.setVisibility(View.GONE);
                                    viewHolder.send_image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                }
                            } else {
                                viewHolder.sendImageLayout.setVisibility(View.GONE);
                                viewHolder.sendPdfLayout.setVisibility(View.VISIBLE);
                                if (attachmentName != null) {
                                    if (attachmentName.length() > 20) {
                                        int len = attachmentName.length();
                                        viewHolder.send_pdf_name_tv.setText(attachmentName.substring(0, 7) + "..." + attachmentName.substring(len - 8));
                                    } else {
                                        viewHolder.send_pdf_name_tv.setText(attachmentName);
                                    }
                                }


                                if (!items.get(i).isAttachmentDownloaded()) {

                                    if (items.get(i).getDownloadProgress() != null) {
                                        viewHolder.sendDownloadAttachment.setVisibility(View.GONE);
                                        viewHolder.send_attachment_download_progress.setText(items.get(i).getDownloadProgress());
                                        if (items.get(i).getDownloadProgress().equals("100%")) {
                                            viewHolder.send_attachment_download_progress.setVisibility(View.GONE);
                                        } else {

                                            viewHolder.send_attachment_download_progress.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        viewHolder.sendDownloadAttachment.setVisibility(View.VISIBLE);
                                        viewHolder.send_attachment_download_progress.setVisibility(View.GONE);
                                    }

                                } else {
                                    viewHolder.send_attachment_download_progress.setVisibility(View.GONE);
                                    viewHolder.send_attachment_download_progress.setText("");
                                    viewHolder.sendDownloadAttachment.setVisibility(View.GONE);
                                }

                                //"jpg", "jpeg", "png", "gif", "bmp","docx","pdf","odt","xls","doc"
                                if (attachmentType.toLowerCase().equals("pdf")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
                                } else if (attachmentType.toLowerCase().equals("doc") || attachmentType.toLowerCase().equals("docx")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc));
                                } else if (attachmentType.toLowerCase().equals("odt")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("bmp")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("xls")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
                                } else if (attachmentType.toLowerCase().equals("xml")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml));
                                } else if (attachmentType.toLowerCase().equals("css")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_css));
                                } else if (attachmentType.toLowerCase().equals("cad")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cad));
                                } else if (attachmentType.toLowerCase().equals("sql")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sql));
                                } else if (attachmentType.toLowerCase().equals("aac")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aac));
                                } else if (attachmentType.toLowerCase().equals("avi")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avi));
                                } else if (attachmentType.toLowerCase().equals("gif")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_gif));
                                } else if (attachmentType.toLowerCase().equals("dat")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dat));
                                } else if (attachmentType.toLowerCase().equals("dll")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dll));
                                } else if (attachmentType.toLowerCase().equals("dmg")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dmg));
                                } else if (attachmentType.toLowerCase().equals("eps")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eps));
                                } else if (attachmentType.toLowerCase().equals("fla")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fla));
                                } else if (attachmentType.toLowerCase().equals("flv")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flv));
                                } else if (attachmentType.toLowerCase().equals("html")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
                                } else if (attachmentType.toLowerCase().equals("iso")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_iso));
                                } else if (attachmentType.toLowerCase().equals("js")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_js));
                                } else if (attachmentType.toLowerCase().equals("mov")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mov));
                                } else if (attachmentType.toLowerCase().equals("mp3")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp3));
                                } else if (attachmentType.toLowerCase().equals("mp4")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp4));
                                } else if (attachmentType.toLowerCase().equals("mpg")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mpg));
                                } else if (attachmentType.toLowerCase().equals("php")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_php));
                                } else if (attachmentType.toLowerCase().equals("ppt") || attachmentType.toLowerCase().equals("pptx")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt));
                                } else if (attachmentType.toLowerCase().equals("ps")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ps));
                                } else if (attachmentType.toLowerCase().equals("psd")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_psd));
                                } else if (attachmentType.toLowerCase().equals("rar")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar));
                                } else if (attachmentType.toLowerCase().equals("txt")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
                                } else if (attachmentType.toLowerCase().equals("video")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
                                } else if (attachmentType.toLowerCase().equals("zip")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip));
                                } else if (attachmentType.toLowerCase().equals("wmv")) {
                                    viewHolder.sendAttachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wmv));
                                }


                            }
                        } else {
                            viewHolder.sendImageLayout.setVisibility(View.GONE);
                            viewHolder.sendPdfLayout.setVisibility(View.GONE);
                            viewHolder.sendMessageLayout.setVisibility(View.GONE);
                        }


                        break;

                }

                if (is_delivered == 1 && is_read == 1) {
                    viewHolder.sendStatusImage.setImageResource(R.drawable.read);
                } else if (is_delivered == 1) {
                    viewHolder.sendStatusImage.setImageResource(R.drawable.delivered);
                } else if (is_sync == 1) {
                    viewHolder.sendStatusImage.setImageResource(R.drawable.sent);
                } else {
                    viewHolder.sendStatusImage.setImageResource(R.drawable.ic_msg_pending);
                }

                try {

                    if (createdDate != null) {
                        String dt = getTime(createdDate);
                        if (AM_PM == 1) {
                            if (hour == 0) {
                                hour = 12;
                            }
                            if (hour < 10) {
                                if (min < 10) {
                                    viewHolder.send_message_time.setText(" 0" + hour + ":0" + min + " PM");
                                } else {
                                    viewHolder.send_message_time.setText(" 0" + hour + ":" + min + " PM");
                                }

                            } else {
                                if (min < 10) {
                                    viewHolder.send_message_time.setText(" " + hour + ":0" + min + " PM");
                                } else {
                                    viewHolder.send_message_time.setText(" " + hour + ":" + min + " PM");
                                }

                            }

                        } else {
                            if (hour == 0) {
                                hour = 12;
                            }
                            if (hour < 10) {
                                if (min < 10) {
                                    viewHolder.send_message_time.setText(" 0" + hour + ":0" + min + " AM");
                                } else {
                                    viewHolder.send_message_time.setText(" 0" + hour + ":" + min + " AM");
                                }

                            } else {
                                if (min < 10) {
                                    viewHolder.send_message_time.setText(" " + hour + ":0" + min + " AM");
                                } else {
                                    viewHolder.send_message_time.setText(" " + hour + ":" + min + " AM");
                                }

                            }

                        }

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } else {




                viewHolder.sendMessageLayout.setVisibility(View.GONE);

                viewHolder.receiveStatusImage.setImageResource(R.drawable.read);
                switch (messageType) {
                    case Values.MessageType.MESSAGE_TYPE_TEXT:
                        if(senderId!=null && senderId.equals("0")){
                            viewHolder.receiveMessageLayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            viewHolder.receiveMessageLayout.setVisibility(View.VISIBLE);
                        }

                        viewHolder.contactLayout.setVisibility(View.GONE);
                        Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "called" + messageType+" "+createdDate);
                        viewHolder.receive_message_tv.setVisibility(View.VISIBLE);
                        viewHolder.imageLayout.setVisibility(View.GONE);
                        viewHolder.pdfLayout.setVisibility(View.GONE);
                        if(items.get(i).getMessage()!=null && !items.get(i).getMessage().trim().isEmpty())
                        {
                            viewHolder.receive_message_tv.setText(items.get(i).getMessage().trim());
                        }

                        break;
                       case Values.MessageType.MESSAGE_TYPE_REQUEST:

                        viewHolder.receiveMessageLayout.setVisibility(View.GONE);
                        viewHolder.contactLayout.setVisibility(View.VISIBLE);

                           try {

                               if (createdDate != null) {
                                   String dt = getTime(createdDate);
                                   if (AM_PM == 1) {
                                       if (hour == 0) {
                                           hour = 12;
                                       }
                                       if (hour < 10) {
                                           if (min < 10) {
                                               viewHolder.contact_message_time_tv.setText("@0" + hour + ":0" + min + " PM");
                                           } else {
                                               viewHolder.contact_message_time_tv.setText("@0" + hour + ":" + min + " PM");
                                           }

                                       } else {
                                           if (min < 10) {
                                               viewHolder.contact_message_time_tv.setText("@" + hour + ":0" + min + " PM");
                                           } else {
                                               viewHolder.contact_message_time_tv.setText("@" + hour + ":" + min + " PM");
                                           }

                                       }

                                   } else {
                                       if (hour == 0) {
                                           hour = 12;
                                       }
                                       if (hour < 10) {
                                           if (min < 10) {
                                               viewHolder.contact_message_time_tv.setText("@0" + hour + ":0" + min + " AM");
                                           } else {
                                               viewHolder.contact_message_time_tv.setText("@0" + hour + ":" + min + " AM");
                                           }

                                       } else {
                                           if (min < 10) {
                                               viewHolder.contact_message_time_tv.setText("@" + hour + ":0" + min + " AM");
                                           } else {
                                               viewHolder.contact_message_time_tv.setText("@" + hour + ":" + min + " AM");
                                           }

                                       }

                                   }
                               }

                           } catch (ParseException e) {
                               e.printStackTrace();
                           }

                        try{
                            if(items.get(i).getMessage()!=null){
                                Helper.getInstance().LogDetails("MESSAGE_TYPE_REQUEST","message "+items.get(i).getMessage()+" "+createdDate);



                                JSONObject jsonObject=new JSONObject(items.get(i).getMessage().toString());
                                if(jsonObject!=null){
                                    viewHolder.contact_message_tv.setText(Helper.getInstance().capitalize(jsonObject.getString("message"))+" ");
                                }
                                Helper.getInstance().LogDetails("MESSAGE_TYPE_REQUEST","obj "+jsonObject.toString()+" "+createdDate);
                            }


                        }catch (Exception e){
                            Helper.getInstance().LogDetails("MESSAGE_TYPE_REQUEST","exception "+e.getCause()+" "+e.getLocalizedMessage());
                        }






                    break;
                    case Values.MessageType.MESSAGE_TYPE_ATTACHMENT:
                        viewHolder.receiveMessageLayout.setVisibility(View.VISIBLE);
                        viewHolder.contactLayout.setVisibility(View.GONE);
                        Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "called" + messageType+" "+createdDate);

                        viewHolder.receive_message_tv.setVisibility(View.GONE);
                        String devicePath = items.get(i).getAttachmentDevicePath();
                        String path = items.get(i).getAttachment();

                        String attachmentType = items.get(i).getAttachment_extension();
                        String attachmentName = items.get(i).getAttachment_name();
                        if (attachmentType == null || attachmentType.trim().isEmpty()) {
                            int index = path.lastIndexOf(".");
                            String fileType = path.substring(index + 1);
                            if (fileType != null && !fileType.trim().isEmpty()) {
                                attachmentType = fileType.replace(" ", "");
                            }


                            Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "attachmentType null" + index + " " + path + attachmentType);

                        }
                        if (attachmentName == null || attachmentName.trim().isEmpty()) {
                            int index = path.lastIndexOf("/");
                            String name = path.substring(index + 1);
                            if (name != null && !name.trim().isEmpty()) {
                                items.get(i).setAttachment_name(name);
                                attachmentName = name;
                            }
                        }


                        if (attachmentType != null && !attachmentType.trim().isEmpty()) {
                            Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", messageType + " " + items.get(i).getAttachment() + " " + attachmentType + " " + items.get(i).isAttachmentDownloaded());
                            if (attachmentType.toLowerCase().equals("png") || attachmentType.toLowerCase().equals("jpg") || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") || attachmentType.toLowerCase().equals("gif")) {
                                viewHolder.imageLayout.setVisibility(View.VISIBLE);
                                viewHolder.pdfLayout.setVisibility(View.GONE);

                                if (!items.get(i).isAttachmentDownloaded()) {

                                    if (items.get(i).getDownloadProgress() != null) {
                                        viewHolder.downloadImage.setVisibility(View.GONE);
                                        viewHolder.image_download_progress.setText(items.get(i).getDownloadProgress());
                                        if (items.get(i).getDownloadProgress().equals("100%")) {
                                            viewHolder.image_download_progress.setVisibility(View.GONE);
                                        } else {
                                            viewHolder.image_download_progress.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        viewHolder.downloadImage.setVisibility(View.VISIBLE);
                                        viewHolder.image_download_progress.setVisibility(View.GONE);
                                    }

                                } else {
                                    viewHolder.image_download_progress.setVisibility(View.GONE);
                                    viewHolder.image_download_progress.setText("");
                                    viewHolder.downloadImage.setVisibility(View.GONE);
                                }
                                if (devicePath != null && !devicePath.trim().isEmpty()) {

                                    File imgFile = new File(devicePath);
                                    if (imgFile.exists()) {

                                       // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        if (!attachmentType.toLowerCase().equals("gif")) {
                                           // viewHolder.image_preview.setImageBitmap(myBitmap);
                                            viewHolder.receiverImageProgressbar.setVisibility(View.VISIBLE);
                                            RequestOptions options = new RequestOptions()

                                                    .error(R.drawable.ic_attachment_img)
                                                    .disallowHardwareConfig()
                                                    .priority(Priority.HIGH);
                                            Glide.with(context)
                                                    .load(devicePath)
                                                    .apply(options)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            viewHolder.receiverImageProgressbar.setVisibility(View.GONE);

                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(viewHolder.image_preview);
                                        } else {
                                            if (path != null && !path.trim().isEmpty()) {
                                                path = path.replace("\"", "");
                                                viewHolder.receiverImageProgressbar.setVisibility(View.VISIBLE);
                                                RequestOptions options = new RequestOptions()

                                                        .error(R.drawable.ic_attachment_img)
                                                        .disallowHardwareConfig()
                                                        .priority(Priority.HIGH);
                                                Glide.with(context)
                                                        .load(path)
                                                        .apply(options)


                                                        .listener(new RequestListener<Drawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                viewHolder.receiverImageProgressbar.setVisibility(View.GONE);

                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                                                return false;
                                                            }
                                                        })
                                                        .into(viewHolder.image_preview);

                                            } else {
                                                viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                                viewHolder.image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                            }
                                        }

                                    } else {
                                        if (path != null && !path.trim().isEmpty()) {
                                            path = path.replace("\"", "");
                                            viewHolder.receiverImageProgressbar.setVisibility(View.VISIBLE);
                                            RequestOptions options = new RequestOptions()

                                                    .error(R.drawable.ic_attachment_img)
                                                    .disallowHardwareConfig()
                                                    .priority(Priority.HIGH);
                                            Glide.with(context)
                                                    .load(path)
                                                    .apply(options)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            viewHolder.receiverImageProgressbar.setVisibility(View.GONE);

                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(viewHolder.image_preview);

                                        } else {
                                            viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                            viewHolder.image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                        }
                                    }
                                } else if (path != null && !path.trim().isEmpty()) {
                                    path = path.replace("\"", "");
                                    viewHolder.receiverImageProgressbar.setVisibility(View.VISIBLE);
                                    RequestOptions options = new RequestOptions()

                                            .error(R.drawable.ic_attachment_img)
                                            .disallowHardwareConfig()
                                            .priority(Priority.HIGH);
                                    Glide.with(context)
                                            .load(path)
                                            .apply(options)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    viewHolder.receiverImageProgressbar.setVisibility(View.GONE);

                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(viewHolder.image_preview);

                                } else {
                                    viewHolder.receiverImageProgressbar.setVisibility(View.GONE);
                                    viewHolder.image_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                                }
                            } else {
                                viewHolder.imageLayout.setVisibility(View.GONE);
                                viewHolder.pdfLayout.setVisibility(View.VISIBLE);
                                if (attachmentName != null) {
                                    if (attachmentName.length() > 20) {
                                        int len = attachmentName.length();
                                        viewHolder.pdf_name_tv.setText(attachmentName.substring(0, 7) + "..." + attachmentName.substring(len - 8));
                                    } else {
                                        viewHolder.pdf_name_tv.setText(attachmentName);
                                    }
                                }


                                if (!items.get(i).isAttachmentDownloaded()) {

                                    if (items.get(i).getDownloadProgress() != null) {
                                        viewHolder.downloadAttachment.setVisibility(View.GONE);
                                        viewHolder.attachment_download_progress.setText(items.get(i).getDownloadProgress());
                                        if (items.get(i).getDownloadProgress().equals("100%")) {
                                            viewHolder.attachment_download_progress.setVisibility(View.GONE);
                                        } else {

                                            viewHolder.attachment_download_progress.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        viewHolder.downloadAttachment.setVisibility(View.VISIBLE);
                                        viewHolder.attachment_download_progress.setVisibility(View.GONE);
                                    }

                                } else {
                                    viewHolder.attachment_download_progress.setVisibility(View.GONE);
                                    viewHolder.attachment_download_progress.setText("");
                                    viewHolder.downloadAttachment.setVisibility(View.GONE);
                                }
                                //"jpg", "jpeg", "png", "gif", "bmp","docx","pdf","odt","xls","doc"
                                if (attachmentType.toLowerCase().equals("pdf")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
                                } else if (attachmentType.toLowerCase().equals("doc") || attachmentType.toLowerCase().equals("docx")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc));
                                } else if (attachmentType.toLowerCase().equals("odt")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("bmp")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("xls")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
                                } else if (attachmentType.toLowerCase().equals("xml")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml));
                                } else if (attachmentType.toLowerCase().equals("css")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_css));
                                } else if (attachmentType.toLowerCase().equals("cad")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cad));
                                } else if (attachmentType.toLowerCase().equals("sql")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sql));
                                } else if (attachmentType.toLowerCase().equals("aac")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aac));
                                } else if (attachmentType.toLowerCase().equals("avi")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avi));
                                } else if (attachmentType.toLowerCase().equals("gif")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_gif));
                                } else if (attachmentType.toLowerCase().equals("dat")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dat));
                                } else if (attachmentType.toLowerCase().equals("dll")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dll));
                                } else if (attachmentType.toLowerCase().equals("dmg")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dmg));
                                } else if (attachmentType.toLowerCase().equals("eps")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eps));
                                } else if (attachmentType.toLowerCase().equals("fla")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fla));
                                } else if (attachmentType.toLowerCase().equals("flv")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flv));
                                } else if (attachmentType.toLowerCase().equals("html")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
                                } else if (attachmentType.toLowerCase().equals("iso")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_iso));
                                } else if (attachmentType.toLowerCase().equals("js")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_js));
                                } else if (attachmentType.toLowerCase().equals("mov")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mov));
                                } else if (attachmentType.toLowerCase().equals("mp3")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp3));
                                } else if (attachmentType.toLowerCase().equals("mp4")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp4));
                                } else if (attachmentType.toLowerCase().equals("mpg")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mpg));
                                } else if (attachmentType.toLowerCase().equals("php")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_php));
                                } else if (attachmentType.toLowerCase().equals("ppt") || attachmentType.toLowerCase().equals("pptx")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt));
                                } else if (attachmentType.toLowerCase().equals("ps")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ps));
                                } else if (attachmentType.toLowerCase().equals("psd")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_psd));
                                } else if (attachmentType.toLowerCase().equals("rar")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar));
                                } else if (attachmentType.toLowerCase().equals("txt")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
                                } else if (attachmentType.toLowerCase().equals("video")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
                                } else if (attachmentType.toLowerCase().equals("zip")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip));
                                } else if (attachmentType.toLowerCase().equals("wmv")) {
                                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wmv));
                                }


                            }
                        } else {
                            viewHolder.imageLayout.setVisibility(View.GONE);
                            viewHolder.pdfLayout.setVisibility(View.GONE);
                            viewHolder.receiveMessageLayout.setVisibility(View.GONE);
                        }


                        break;

                }


                try {

                    if (createdDate != null && !createdDate.trim().isEmpty()) {
                        String dt = getTime(createdDate);
                        if (AM_PM == 1) {
                            if (hour == 0) {
                                hour = 12;
                            }
                            if (hour < 10) {
                                if (min < 10) {
                                    viewHolder.receive_message_time.setText(" 0" + hour + ":0" + min + " PM");
                                } else {
                                    viewHolder.receive_message_time.setText(" 0" + hour + ":" + min + " PM");
                                }

                            } else {
                                if (min < 10) {
                                    viewHolder.receive_message_time.setText(" " + hour + ":0" + min + " PM");
                                } else {
                                    viewHolder.receive_message_time.setText(" " + hour + ":" + min + " PM");
                                }

                            }

                        } else {
                            if (hour == 0) {
                                hour = 12;
                            }
                            if (hour < 10) {
                                if (min < 10) {
                                    viewHolder.receive_message_time.setText(" 0" + hour + ":0" + min + " AM");
                                } else {
                                    viewHolder.receive_message_time.setText(" 0" + hour + ":" + min + " AM");
                                }

                            } else {
                                if (min < 10) {
                                    viewHolder.receive_message_time.setText(" " + hour + ":0" + min + " AM");
                                } else {
                                    viewHolder.receive_message_time.setText(" " + hour + ":" + min + " AM");
                                }

                            }

                        }

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }


            Calendar calendar = Calendar.getInstance();
            currentDate = sdf1.format(calendar.getTime());


            try {
                boolean isChatEnd = false;
                if (context instanceof HistotyChatActivity) {
                    if (i == 0 ) {
                        if (endTime != null && !endTime.trim().isEmpty() && !endTime.equals("0000-00-00 00:00:00")) {
                            endDate = getDateTime(endTime);
                            isChatEnd = true;

                            viewHolder.endDateLayout.setVisibility(View.VISIBLE);

                            Helper.getInstance().LogDetails("chat details ", endTime + " " + endDate);

                            if (endDate != null) {
                                viewHolder.end_date_tv.setText( endDate.substring(0,2)+"'"+endDate.substring(3,9)+" | "+endDate.substring(10) );


                            }

                        } else {
                            viewHolder.endDateLayout.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.endDateLayout.setVisibility(View.GONE);
                    }

                } else {
                    viewHolder.endDateLayout.setVisibility(View.GONE);
                }


                if(createdDate!=null && !createdDate.trim().isEmpty())
                {
                    messageDate = getDate(createdDate);
                    if (currentDate.equals(messageDate) && i == getItemCount() - 1) {
                        if (!today) {
                            viewHolder.dateLayout.setVisibility(View.VISIBLE);
                            viewHolder.date_tv.setText("Today");
                            Helper.getInstance().LogDetails("header data if", "called");
                            //today=true;
                        }
                    } else if (!currentDate.equals(messageDate) && i == getItemCount() - 1) {
                        viewHolder.dateLayout.setVisibility(View.VISIBLE);
                        viewHolder.date_tv.setText(messageDate);
                        Helper.getInstance().LogDetails("header data else if", "called");
                    }
                }
                else
                {
                    viewHolder.dateLayout.setVisibility(View.GONE);
                }




                nextPosition = i + 1;
                if (nextPosition < getItemCount()) {


                    nextDate = getDate(items.get(nextPosition).getCreated_at());
                    Helper.getInstance().LogDetails("header data", " if called" + nextDate + " " + currentDate + " " + messageDate);
                    if (!nextDate.equals(messageDate)) {
                        viewHolder.dateLayout.setVisibility(View.VISIBLE);
                        if (currentDate.equals(messageDate)) {
                            if (!today) {
                                viewHolder.date_tv.setText("Today");
                                Helper.getInstance().LogDetails("header data", "called");
                                today = true;
                            } else {
                                // viewHolder.dateLayout.setVisibility(View.GONE);
                            }

                        } else {
                            viewHolder.date_tv.setText(messageDate);
                        }

                    } else {

                        viewHolder.dateLayout.setVisibility(View.GONE);


                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (items != null && items.size() > 0) {
            return items.size();
        } else {
            return 0;
        }


    }

    public void setList(List<ChatModel> entries) {
        try{
        if (items != null && items.size() > 0) {
            items.clear();
        }
        items.addAll(entries);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getTime(String date) throws ParseException {

        try{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        min = c.get(Calendar.MINUTE);
        hour = c.get(Calendar.HOUR);

        AM_PM = c.get(Calendar.AM_PM);
        Log.e("date:==", date);
        Log.e("date:", hour + " " + min + "  " + AM_PM);

        return sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private String getDate(String date) throws ParseException {
        try{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return sdf1.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private String getDateTime(String date) throws ParseException {

        try{

        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        endmin = c.get(Calendar.MINUTE);
        endhour = c.get(Calendar.HOUR);
        END_AM_PM = c.get(Calendar.AM_PM);

        return sdf2.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private ChatModel getItem(int position) {
        try {
            if (position > -1 && items.size() > 0) {
                try {
                    return items.get(position);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void dowloadTheFile(int position) {
        try {
            if (Helper.getConnectivityStatus(context)) {
                if (getItem(position) != null && !getItem(position).isAttachmentDownloaded()) {
                    if (Helper.getInstance().checkStoragePermissions(context)) {

                        handler.obtainMessage(FileDownLoader.DOWNLOAD_START, getItem(position).getMessage_id() + "").sendToTarget();
                    } else {
                        if (context instanceof HistotyChatActivity) {
                            ((HistotyChatActivity) context).askStoragePermission(getItem(position).getId(), getItem(position).getMessage_id());
                        }
                    }
                }
            } else {
                ToastUtil.getInstance().showToast(context, Values.ErrorMessages.NETWORK_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDownloadProgress(JSONObject progress) {
        try {
            if (progress != null) {
                int i = -1;
                for (ChatModel model : items) {
                    i++;
                    if (model.getMessage_id() != 0) {
                        if (model.getMessage_id() == (progress.getInt("msgId"))) {
                            items.get(i).setDownloadProgress(progress.getString("progress"));
                            notifyItemChanged(i, items.get(i));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChatModel getSelectedDownloadingObject(int rowId) {
        try {
            for (ChatModel model : items) {
                if (model.getMessage_id() == (rowId)) {
                    return model;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onDownloadError(String msgId, String message) {
        try {
            if (msgId != null) {
                int i = -1;
                for (ChatModel model : items) {
                    i++;
                    if (model.getMessage_id() != 0) {
                        if (model.getMessage_id() == Integer.parseInt(msgId)) {
                            items.get(i).setDownloadProgress(null);
                            items.get(i).setAttachmentDownloaded(false);
                            notifyItemChanged(i, items.get(i));
                            ToastUtil.getInstance().showToast(context, message);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDownloadCompleted(JSONObject object) {
        try {
            if (object != null && object.getString("path") != null) {
                int i = -1;
                for (ChatModel model : items) {
                    i++;

                    if (model.getMessage_id() != 0 && model.getMessage_id() == (object.getInt("msgId"))) {
                        items.get(i).setAttachmentDownloaded(true);
                        items.get(i).setAttachmentDevicePath(object.getString("path"));
                        if (context instanceof HistotyChatActivity) {
                            ((HistotyChatActivity) context).updateDownloadFileStatus(items.get(i).getMessage_id(), items.get(i).getSender_id(), items.get(i).getReceiver_id(), items.get(i).getConversation_reference_id(), object.optString("path"));
                        }
                        notifyItemChanged(i, items.get(i));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String tm_userid, String receiver_id, String end_time, String receiver_name, String user_name) {
        try{
        tmUserId = tm_userid;
        receiverId = receiver_id;
        endTime = end_time;
        receiverName = receiver_name;
        userName = user_name;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void dowloadTheFileAfterGivenStoragePermission(int messageId) {
        try {
            if (Helper.getConnectivityStatus(context)) {
                if (messageId != 0) {
                    if (Helper.getInstance().checkStoragePermissions(context)) {

                        handler.obtainMessage(FileDownLoader.DOWNLOAD_START,
                                messageId + "").sendToTarget();
                    }
                }
            } else {
                ToastUtil.getInstance().showToast(context, Values.ErrorMessages.NETWORK_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.send_message_tv)
        TextView send_message_tv;

        @BindView(R.id.receive_message_tv)
        TextView receive_message_tv;

        @BindView(R.id.date_tv)
        TextView date_tv;

        @BindView(R.id.send_message_time)
        TextView send_message_time;

        @BindView(R.id.receive_message_time)
        TextView receive_message_time;

        @BindView(R.id.end_date_tv)
        TextView end_date_tv;

        @BindView(R.id.pdf_name_tv)
        TextView pdf_name_tv;

        @BindView(R.id.image_download_progress)
        TextView image_download_progress;

        @BindView(R.id.attachment_download_progress)
        TextView attachment_download_progress;

        @BindView(R.id.send_image_download_progress)
        TextView send_image_download_progress;

        @BindView(R.id.send_pdf_name_tv)
        TextView send_pdf_name_tv;

        @BindView(R.id.send_attachment_download_progress)
        TextView send_attachment_download_progress;

        @BindView(R.id.contact_message_tv)
        TextView contact_message_tv;

        @BindView(R.id.contact_message_time_tv)
        TextView contact_message_time_tv;

        @BindView(R.id.linearLayout)
        LinearLayout linearLayout;

        @BindView(R.id.pdfLayout)
        LinearLayout pdfLayout;

        @BindView(R.id.sendPdfLayout)
        LinearLayout sendPdfLayout;

        @BindView(R.id.sendMessageLayout)
        LinearLayout sendMessageLayout;

        @BindView(R.id.receiveMessageLayout)
        LinearLayout receiveMessageLayout;

        @BindView(R.id.dateLayout)
        LinearLayout dateLayout;

        @BindView(R.id.endDateLayout)
        LinearLayout endDateLayout;

        @BindView(R.id.contactLayout)
        LinearLayout contactLayout;

        @BindView(R.id.sendStatusImage)
        ImageView sendStatusImage;

        @BindView(R.id.receiveStatusImage)
        ImageView receiveStatusImage;

        @BindView(R.id.image_preview)
        ImageView image_preview;


        @BindView(R.id.attachmentImage)
        ImageView attachmentImage;

        @BindView(R.id.downloadImage)
        ImageView downloadImage;

        @BindView(R.id.downloadAttachment)
        ImageView downloadAttachment;

        @BindView(R.id.sendDownloadImage)
        ImageView sendDownloadImage;

        @BindView(R.id.send_image_preview)
        ImageView send_image_preview;

        @BindView(R.id.sendAttachmentImage)
        ImageView sendAttachmentImage;

        @BindView(R.id.sendDownloadAttachment)
        ImageView sendDownloadAttachment;

        @BindView(R.id.imageLayout)
        RelativeLayout imageLayout;

        @BindView(R.id.sendImageLayout)
        RelativeLayout sendImageLayout;

        @BindView(R.id.senderImageProgressbar)
        ProgressBar senderImageProgressbar;

        @BindView(R.id.receiverImageProgressbar)
        ProgressBar receiverImageProgressbar;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}

