package com.tvisha.click2magic.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tvisha.click2magic.DataBase.ActiveChatsTable;
import com.tvisha.click2magic.DataBase.CategoriesTable;
import com.tvisha.click2magic.DataBase.ChatModel;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.SiteAssetsTable;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.DataBase.TypingMessage;
import com.tvisha.click2magic.DataBase.TypingMessageTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.AppFileProvider;
import com.tvisha.click2magic.Helper.DateUtils;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.NotificationHelper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.Helper.progressButton.ProgressButton;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.CannedMessageAdapter;
import com.tvisha.click2magic.adapter.CategoriesFlowAdapter;
import com.tvisha.click2magic.adapter.CollateralMessageAdapter;
import com.tvisha.click2magic.adapter.CompanyAdapter;
import com.tvisha.click2magic.adapter.ConversationAdapter;
import com.tvisha.click2magic.adapter.ImageMessageAdapter;
import com.tvisha.click2magic.adapter.LinkMessageAdapter;
import com.tvisha.click2magic.adapter.SmartRepliesMessageAdapter;
import com.tvisha.click2magic.adapter.TopRecyclerAdapter;
import com.tvisha.click2magic.adapter.VisitPagesAdapter;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.ActiveChatsApi;
import com.tvisha.click2magic.api.post.SiteData;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.CannedResponse;
import com.tvisha.click2magic.api.post.model.CategoriesApi;
import com.tvisha.click2magic.api.post.model.Category;
import com.tvisha.click2magic.api.post.model.ChatRatingUpdateResponse;
import com.tvisha.click2magic.api.post.model.Collateral;
import com.tvisha.click2magic.api.post.model.GetAwsConfigResponse;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.api.post.model.Link;
import com.tvisha.click2magic.api.post.model.SiteAssetData;
import com.tvisha.click2magic.api.post.model.SiteAssetsResponse;
import com.tvisha.click2magic.api.post.model.TrackData;
import com.tvisha.click2magic.api.post.model.TrackResponse;
import com.tvisha.click2magic.chatApi.ApiClient;
import com.tvisha.click2magic.chatApi.ApiInterface;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.model.GetMessagesResponse;
import com.tvisha.click2magic.model.Message;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.gauriinfotech.commons.Commons;
import io.socket.client.Ack;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ChatActivity extends BasicActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener,CategoriesFlowAdapter.OnCategoryClickListener {



    LinearLayout transferLayout,screenshotLayout,infoLayout,chatLayout,endChatLayout,cannedMsglLayout,collateralLayout,
            linkslLayout,contactLayout,cancelCollateralLayout;


    @BindView(R.id.cancelCannedLayout)
    LinearLayout cancelCannedLayout;

    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;

    @BindView(R.id.closeInfoLayout)
    RelativeLayout closeInfoLayout;

    @BindView(R.id.phoneNumberLayout)
    LinearLayout phoneNumberLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;





    LinearLayout attachmentLayout,shareLayout;

    @BindView(R.id.emailLayout)
    LinearLayout emailLayout;

    @BindView(R.id.agentNameLay)
    LinearLayout agentNameLay;


    @BindView(R.id.back_lay)
    LinearLayout back_lay;

    @BindView(R.id.copyNumberLayout)
    LinearLayout copyNumberLayout;


    @BindView(R.id.confirmNumberLayout)
    LinearLayout confirmNumberLayout;

    @BindView(R.id.requestNumberLayout)
    LinearLayout requestNumberLayout;

    @BindView(R.id.copyEmailLayout)
    LinearLayout copyEmailLayout;


    @BindView(R.id.confirmEmailLayout)
    LinearLayout confirmEmailLayout;

    @BindView(R.id.cannedMessageSearchLayout)
    LinearLayout cannedMessageSearchLayout;

    @BindView(R.id.visitPagesLayout)
    LinearLayout visitPagesLayout;

    @BindView(R.id.smartRepliesLayout)
    LinearLayout smartRepliesLayout;


    @BindView(R.id.requestEmailLayout)
    LinearLayout requestEmailLayout;


    @BindView(R.id.closeContactLayout)
    LinearLayout closeContactLayout;

    @BindView(R.id.userEndedChatLayout)
    LinearLayout userEndedChatLayout;

    @BindView(R.id.cancelReplyLayout)
    LinearLayout cancelReplyLayout;

    @BindView(R.id.replyAttachmentLayout)
    LinearLayout replyAttachmentLayout;

    @BindView(R.id.info_close_icon)
    ImageView info_close_icon;

    @BindView(R.id.image_preview)
    ImageView image_preview;

    @BindView(R.id.attachment_close_icon)
    ImageView attachment_close_icon;

    @BindView(R.id.actionLable)
    ImageView actionLable;

    @BindView(R.id.back_icon)
    ImageView back_icon;

    @BindView(R.id.send_icon)
    ImageView send_icon;

    @BindView(R.id.more_icon)
    ImageView more_icon;

    @BindView(R.id.noDataImage)
    ImageView noDataImage;

    @BindView(R.id.attachments)
    ImageView attachments;

    @BindView(R.id.attachmentImage)
    ImageView attachmentImage;

    @BindView(R.id.info_close)
    TextView info_close;

    @BindView(R.id.contact_close)
    TextView contact_close;

    @BindView(R.id.contact_number_tv)
    TextView contact_number_tv;

    @BindView(R.id.contact_email_tv)
    TextView contact_email_tv;

    @BindView(R.id.contact_name_tv)
    TextView contact_name_tv;

    @BindView(R.id.reply_message_name_tv)
    TextView reply_message_name_tv;

    @BindView(R.id.reply_message_tv)
    TextView reply_message_tv;

    @BindView(R.id.attachment_name_tv)
    TextView attachment_name_tv;

    @BindView(R.id.browser_name_tv)
    TextView browser_name_tv;

    @BindView(R.id.no_data_msg)
    TextView no_data_msg;

    @BindView(R.id.os_name_tv)
    TextView os_name_tv;

    @BindView(R.id.email_tv)
    TextView email_tv;

    @BindView(R.id.phone_number_tv)
    TextView phone_number_tv;

    @BindView(R.id.entry_url_tv)
    TextView entry_url_tv;

    @BindView(R.id.history_tv)
    TextView history_tv;

    @BindView(R.id.customer_name_tv)
    TextView customer_name_tv;

    @BindView(R.id.location_tv)
    TextView location_tv;

    @BindView(R.id.ip_tv)
    TextView ip_tv;

    @BindView(R.id.agent_name_tv)
    TextView agent_name_tv;

    @BindView(R.id.start_time_tv)
    TextView start_time_tv;

    @BindView(R.id.info_agent_name_tv)
    TextView info_agent_name_tv;

    //@BindView(R.id.canned_message_recyclerView)
    RecyclerView canned_message_recyclerView;

  //  @BindView(R.id.collateral_message_recyclerView)
    RecyclerView collateral_message_recyclerView;

    //@BindView(R.id.link_message_recyclerView)
    RecyclerView link_message_recyclerView;

    //@BindView(R.id.image_message_recyclerView)
    RecyclerView image_message_recyclerView;

    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;

  //  @BindView(R.id.rv_categories)
    RecyclerView rvCategory;

    @BindView(R.id.visit_pages_recycleView)
    RecyclerView visit_pages_recycleView;

    @BindView(R.id.smartRepliesRecyclerView)
    RecyclerView smartRepliesRecyclerView;

    @BindView(R.id.topRecyclerView)
    RecyclerView topRecyclerView;


    @BindView(R.id.companyRecyclerView)
    RecyclerView companyRecyclerView;


    @BindView(R.id.chat_edit_txt)
    EditText chat_edit_txt;

    EditText canned_message_search_et;

    //@BindView(R.id.navigation)
    TabLayout navigation;

    @BindView(R.id.usersLayout)
    RelativeLayout usersLayout;

    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    @BindView(R.id.bottom_lay_card)
    RelativeLayout bottom_lay_card;

    @BindView(R.id.chat_layout)
    RelativeLayout chat_layout;

    @BindView(R.id.info_layout)
    RelativeLayout info_layout;

    @BindView(R.id.contact_layout)
    RelativeLayout contact_layout;

    @BindView(R.id.main_rl_view)
    RelativeLayout main_rl_view;

    @BindView(R.id.attachmentViewLayout)
    RelativeLayout attachmentViewLayout;

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    @BindView(R.id.reply_message_layout)
    RelativeLayout reply_message_layout;

    @BindView(R.id.moreLayoutLine)
    View moreLayoutLine;

    @OnClick(R.id.cancelReplyLayout)
    void cancelReply(){
        isReplyMessage=false;
        reply_message_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.send_icon)
    void send(){
        send_icon.setVisibility(View.VISIBLE);

        if (chat_edit_txt.getText() != null) {
            String text = chat_edit_txt.getText().toString().trim();
            if (!text.trim().isEmpty() && !text.trim().equals("")) {
                message = text;
                chat_edit_txt.setText("");
                if (tmUserId != null && !tmUserId.trim().isEmpty() && receiver_id != null && !receiver_id.trim().isEmpty()) {
                    if(!isReplyMessage)
                    {
                        sendMessage();
                    }else{
                        sendReplyMessage();
                    }

                }

            }
        }
    }

    @OnClick(R.id.back_icon)
    void backC(){
        Helper.getInstance().closeKeyBoard(ChatActivity.this, chat_edit_txt);
        onBackPressed();
    }

    @OnClick(R.id.more_icon)
    void more(){
        if (chat_edit_txt != null) {
            Helper.getInstance().closeKeyBoard(ChatActivity.this, chat_edit_txt);
        }

        openMoreOptionsDialg();
    }

    @OnClick(R.id.info_close_icon)
    void info(){
        chatRecyclerView.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        info_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.info_close)
    void closeInfo(){
        isInfoClicked=false;
        chatRecyclerView.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        info_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.contact_close)
    void contactClose(){
         isContactCliked=false;
         chatRecyclerView.setVisibility(View.VISIBLE);
         bottomLayout.setVisibility(View.VISIBLE);
         contact_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.closeContactLayout)
    void closeContactLay(){
         contactClose();
    }

    @OnClick(R.id.attachment_close_icon)
    void closeAttachment(){
        mainLayout.setVisibility(View.VISIBLE);
        attachmentViewLayout.setVisibility(View.GONE);
    }


    @OnClick(R.id.chatRecyclerView)
    void closeKey(){
        Helper.getInstance().closeKeyBoard(ChatActivity.this, chatRecyclerView);
    }

    @OnClick(R.id.phoneNumberLayout)
    void call(){
        if(activeChat!=null && activeChat.getMobile()!=null && !activeChat.getMobile().trim().isEmpty())
        {
            makeCall(activeChat.getMobile());
        }
    }
  @OnClick(R.id.phone_number_tv)
    void call1(){
      call();
    }

    @OnClick(R.id.emailLayout)
    void mail(){
        sendEmail();
    }

    @OnClick(R.id.email_tv)
    void mail1(){
        sendEmail();
    }



    @OnClick(R.id.copyNumberLayout)
    void copyN(){
        copyNumer();
    }

    @OnClick(R.id.confirmNumberLayout)
    void confirmNumberL(){
        if(Utilities.getConnectivityStatus(ChatActivity.this)>0)
        {
            confirmNumber();

        }
        else
        {
            Toast.makeText(ChatActivity.this,"Please check internet connection",Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.requestNumberLayout)
    void request(){
        if(Utilities.getConnectivityStatus(ChatActivity.this)>0)
        {
            requestNumber();
        }
        else
        {
            Toast.makeText(ChatActivity.this,"Please check internet connection",Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.copyEmailLayout)
    void copyE()
    {
        copyEmail();
    }

    @OnClick(R.id.confirmEmailLayout)
    void confirmE(){
        if(Utilities.getConnectivityStatus(ChatActivity.this)>0)
        {
            confirmEmail();

        }
        else
        {
            Toast.makeText(ChatActivity.this,"Please check internet connection",Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.requestEmailLayout)
    void requestE(){
        if(Utilities.getConnectivityStatus(ChatActivity.this)>0)
        {
            requestEmail();
        }
        else
        {
            Toast.makeText(ChatActivity.this,"Please check internet connection",Toast.LENGTH_LONG).show();
        }
    }

    String lastMessageId = "", oldLastMessageId = "", tmToken = "", socketId = "", accessToken = "",
            limit = "10", message = "", agent_id = "",categoryId="",rating="",
            selectedPositionSiteId="0",user_id = "", site_id = "", role = "", apiRole = "", account_id = "", company_token = "",
            track_code="",chat_start_date="", selectedImagePath = "";

    int scrollPosition=0;

    boolean isInfoClicked = false,isContactCliked=false, isSelf = true, isTabChanged = false,isAwsApiCalled = false,
            isNavigatedFromNotification = false, screenshotClicked = false, isRefresh = false,isAttachmentClicked=false,isReplyMessage=false;

    Dialog dialog = null, moreOptionsDialog =null,feedBackDialog =null,  cannedMessagedialog=null,collateralDialog=null,
            attachmentOptionsDialog=null,shareOptionsDialog=null,infoDialog=null;

    int startHour, startMin, START_AM_PM,position = 0,downloadAtachMentMessageId = 0,activeChatPosition=0;
    String dateFormat1 = "dd MMM yyyy";
    SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, Locale.US);
    Intent CamIntent, GalIntent;
    Uri selectedFileuri;
    String AWS_KEY = "", AWS_SECRET_KEY = "", AWS_BUCKET = "", AWS_REGION = "", AWS_BASE_URL = "";
    ChatModel replyMessageObj=new ChatModel();

    private LinearLayoutManager chatLayoutManager;
    private List<ActiveChat> activeChatArrayList = new ArrayList<>();

    private String[] navLabels = {
            "Collateral",
            "Links",
            "Images"

    };

    public static String s3Url = "";
    public static String receiver_id = "",chat_id="", receiver_name, receiver_email = "", conversation_reference_id = "",current_site_id = "",current_site_name="", user_name = "", siteToken = "", tmUserId = "";
    public static ActiveChat activeChat = null;
    public static List<SitesInfo> sitesInfoList = new ArrayList<>();
    public static ArrayList<Category> categoryArrayList = new ArrayList<>();

    static Context context;
    static Activity activity;

     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     ConversationTable conversationTable;
     SitesTable sitesTable=null;
     CategoriesTable categoriesTable=null;

     ActiveChatsTable activeChatsTable;
     TypingMessageTable typingMessageTable;
     ConversationAdapter adapter;
    LinearLayoutManager visitPagesManager;
    VisitPagesAdapter visitPagesAdapter;

    CompanyAdapter companyAdapter;
    CannedMessageAdapter cannedMessageAdapter = null;;
    SmartRepliesMessageAdapter smartRepliesMessageAdapter = null;;
    CategoriesFlowAdapter categoriesFlowAdapter=null;
    CollateralMessageAdapter collateralMessageAdapter = null;
    LinkMessageAdapter linkMessageAdapter = null;
    ImageMessageAdapter imageMessageAdapter = null;

    List<CannedResponse> cannedMessageList = new ArrayList<>();
    List<CannedResponse> cannedTempMessageList = new ArrayList<>();
    List<Link> linksMessageList = new ArrayList<>();
    List<Link> linksTempMessageList = new ArrayList<>();
    List<Image> imageList = new ArrayList<>();
    List<Image> imageTempList = new ArrayList<>();
    List<String> smartRepliesList = new ArrayList<>();
    List<Collateral> collateralMessageList = new ArrayList<>();
    List<Collateral> collateralTempMessageList = new ArrayList<>();
    List<TrackData> visitPages=new ArrayList<>();
    List<ChatModel> list = new ArrayList<>();
    List<ChatModel> localMessages = new ArrayList<>();
    List<Message> messageList = new ArrayList<>();

    ApiInterface apiService;
    C2mApiInterface c2mApiService;
    TopRecyclerAdapter topAdapter;
    LinearLayoutManager layoutManager1 = null;




    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {

                case Values.SingleUserChat.CHAT_IMAGE_PREVIEW:
                    try {
                        Navigation.getInstance().imageViewer(ChatActivity.this, String.valueOf(msg.obj), receiver_id, tmUserId, conversation_reference_id, receiver_name, user_name);
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

    private void deleteSite(int siteId){
        Helper.getInstance().LogDetails("deleteSite Called",siteId+" ");
        if(sitesInfoList!=null && sitesInfoList.size()>0)
        {
            for(int i=0;i<sitesInfoList.size();i++){
                int sid= Integer.parseInt(sitesInfoList.get(i).getSiteId());
                if(sid==siteId){
                    sitesInfoList.remove(i);
                    saveSiteData();
                    if(companyAdapter!=null)
                        companyAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private void addSite(JSONObject jsonObject){
        int siteId= Integer.parseInt(jsonObject.optString("site_id"));
        SitesInfo sitesInfo=new SitesInfo();
        sitesInfo.setSiteId(jsonObject.optString("site_id"));
        sitesInfo.setSiteName( jsonObject.optString("site_name"));
        sitesInfo.setSiteToken(jsonObject.optString("site_token"));
        sitesInfo.setAccountId( jsonObject.optString("account_id"));
        Helper.getInstance().LogDetails("addSite Called",siteId+" ");
        if(sitesInfoList!=null && sitesInfoList.size()>0)
        {
            boolean isExists=false;
            for(int i=0;i<sitesInfoList.size();i++){
                int sid= Integer.parseInt(sitesInfoList.get(i).getSiteId());
                if(sid==siteId){
                    isExists=true;
                    break;
                }
            }
            if(!isExists)
            {
                Helper.getInstance().LogDetails("addSite Called",siteId+" "+"true");
                sitesInfoList.add(sitesInfo);
                saveSiteData();
                if(companyAdapter!=null)
                    companyAdapter.notifyDataSetChanged();
               /* if(isSelf){
                    updateSiteListWithLocalDb();
                    setUnreadCount();
                }*/
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {

                case Values.RecentList.RECENT_NETWORK_CONNECTED:
                    if (Helper.getConnectivityStatus(ChatActivity.this)) {
                        Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "called");
                        if (mSocket == null) {
                            connectToSocket();
                        } else if (mSocket != null && !mSocket.connected()) {
                            appSocket.connectSocket();
                        }
                        if (tmSocket == null) {
                            connectToTmSocket();
                        } else if (tmSocket != null && !tmSocket.connected()) {
                           appSocket.connectTmSocket();
                        }
                    }
                    break;
                case Values.RecentList.FCM_TRIGGER:
                    if (Helper.getConnectivityStatus(ChatActivity.this)) {

                        if (mSocket == null) {
                            connectToSocket();
                        } else if (mSocket != null && !mSocket.connected()) {
                           appSocket.connectSocket();
                        }

                        if (tmSocket == null) {
                            connectToTmSocket();
                        } else if (tmSocket != null && !tmSocket.connected()) {
                           appSocket.connectTmSocket();
                        }
                    }
                    break;
                case Values.RecentList.MESSAGES_SYNC_COMPLETED:
                    Helper.getInstance().LogDetails("MESSAGES_SYNC_COMPLETED", "ChatActiviyt");
                    break;
                case Values.RecentList.SITES_SYNC_COMPLETED:
                    Helper.getInstance().LogDetails("SITES_SYNC_COMPLETED", "ChatActiviyt");
                    break;
                case Values.RecentList.ACTIVE_CHATS_SYNC_COMPLETED:
                    if(isSelf)
                    {
                        updateSiteListWithLocalDb();
                    }

                    break;
                case Values.RecentList.SITE_DELETED:
                    int siteId=(int)msg.obj;
                    deleteSite(siteId);
                    break;
                case Values.RecentList.SITE_ADDED:
                    JSONObject jsonObject=(JSONObject) msg.obj;
                    addSite(jsonObject);
                    break;
                case Values.RecentList.MESSAGES_READ_COMPLETED:
                    Helper.getInstance().LogDetails("MESSAGES_SYNC_COMPLETED", "ChatActiviyt");
                    break;
                case Values.RecentList.ACCESS_TOKEN_UPDATED:
                    Helper.getInstance().LogDetails("SOCKET_ACESS_TOKEN", "ChatActiviyt");
                    getAccessToken();
                    lastMessageId = "0";
                    isRefresh = true;
                    if (Helper.getConnectivityStatus(ChatActivity.this)) {

                        callGetConversationsApi();
                    } else {
                        Toast.makeText(ChatActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                    }

                   // getHistory();
                    if (receiver_id != null && tmUserId != null && !receiver_id.isEmpty() && !tmUserId.isEmpty()) {
                        updateReadMessageStatus();
                    }

                    break;
                case Values.RecentList.UPDATE_UNREAD_COUNT:
                    Helper.getInstance().LogDetails("UPDATE_UNREAD_COUNT", "ChatActiviyt");

                    if (receiver_id != null && tmUserId != null && !receiver_id.isEmpty() && !tmUserId.isEmpty()) {
                        updateReadMessageStatus();
                    }
                    setUnreadCount();
                    break;


            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();


        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(setView());
        ButterKnife.bind(this);
        context=ChatActivity.this;
        activity=ChatActivity.this;
        noDataImage=findViewById(R.id.noDataImage);
        initializeViews();
        progressDialog();

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        Helper.getInstance().LogDetails("onPause onBackPressed", "called");
        try{
            saveTypingMessage();
            updateReadMessageStatus();
        if (chat_edit_txt != null) {
            Helper.getInstance().closeKeyBoard(ChatActivity.this, chat_edit_txt);
        }
        if (sitesInfoList != null && sitesInfoList.size() > 0) {
            Helper.getInstance().LogDetails("sitesInfoList onBackPressed ", sitesInfoList.size() + "");
            saveSiteData();
        }

        if (isNavigatedFromNotification) {
            Helper.getInstance().LogDetails("sitesInfoList onBackPressed ", "create main");
            isNavigatedFromNotification = false;

            Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Helper.getInstance().LogDetails("sitesInfoList onBackPressed ", "back");
            super.onBackPressed();
        }
        receiver_id="0";
        conversation_reference_id="0";
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        saveTypingMessage();
        super.onPause();
    }

    @Override
    public int setView() {
        return R.layout.activity_chat;
    }


    @Override
    public void socketOnConnect() {

        try{
        if (mSocket == null) {
            Helper.getInstance().LogDetails("CHECK socketOnConnect", "null");
        } else if (!mSocket.connected()) {
           appSocket.connectSocket();
            Helper.getInstance().LogDetails("CHECK socketOnConnect", "connected");
        } else {

            Helper.getInstance().LogDetails("CHECK socketOnConnect", " connected");
        }
            Helper.getInstance().LogDetails("CHECK socketOnConnect checkAgentStatus", "called");
        checkAgentStatus();
            EmitGetAgentChats();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void socketTmOnConnect() {

        Helper.getInstance().LogDetails("CHECK socketTmOnConnect", "socketTmOnConnect");

    }

    @Override
    public void socketOnDisconnect() {
        Helper.getInstance().LogDetails("CHECK socketOnDisconnect", "socketOnDisconnect");
    }

    @Override
    public void socketTmOnDisconnect() {
        Helper.getInstance().LogDetails("CHECK socketTmOnDisconnect", "socketTmOnDisconnect");
    }

    @Override
    public void connectToSocket() {
        Helper.getInstance().LogDetails("Socket c2m", "connecting...");
        // socketConnection();
    }

    @Override
    public void connectToTmSocket() {
        Helper.getInstance().LogDetails("Socket tm", "connecting...");
        // tmSocketConnection();
    }

    private void updateReadMessageStatus() {

        try{

        if (Helper.getInstance().isAppForground(ChatActivity.this)) {
            if (receiver_id != null && !receiver_id.trim().isEmpty() && tmUserId != null && !tmUserId.trim().isEmpty() && conversation_reference_id != null && !conversation_reference_id.trim().isEmpty()) {
                if (tmSocket != null && tmSocket.connected()) {
                    Helper.getInstance().LogDetails("updateReadMessageStatus", "socket connedted");
                    SyncData.syncNonReadMessage(ChatActivity.this, Integer.parseInt(receiver_id), Integer.parseInt(tmUserId), conversation_reference_id);
                } else {
                    Helper.getInstance().LogDetails("updateReadMessageStatus", "socket not connedted");
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    @Override
    protected void onRestart() {
        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_CHAT;
        AppSocket.eventHandler = socketHandler;
        AppSocket.tmEventHandler = tmSocketHandler;
        HandlerHolder.mainActivityUiHandler = uiHandler;
        if(adapter!=null){
            adapter.setSelectionmode(true);
        }
        updateReadMessageStatus();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        AppSocket.eventHandler = socketHandler;
        AppSocket.tmEventHandler = tmSocketHandler;
        HandlerHolder.mainActivityUiHandler = uiHandler;
        Helper.getInstance().LogDetails("ChatResume","called");
        if(mSocket!=null && !mSocket.connected())
        {
            Helper.getInstance().LogDetails("ChatResume","called c2m");
           appSocket.connectSocket();
        }
        if(tmSocket!=null && !tmSocket.connected())
        {
            Helper.getInstance().LogDetails("ChatResume","called tm");
            appSocket.connectTmSocket();
        }
        super.onResume();
    }



    @Override
    public void handleIntent() {

        try{





        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_CHAT;
        AppSocket.eventHandler = socketHandler;
        AppSocket.tmEventHandler = tmSocketHandler;

        conversationTable = new ConversationTable(ChatActivity.this);
        sitesTable = new SitesTable(ChatActivity.this);
        activeChatsTable = new ActiveChatsTable(ChatActivity.this);
        typingMessageTable = new TypingMessageTable(ChatActivity.this);
        categoriesTable = new CategoriesTable(ChatActivity.this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);



        HandlerHolder.mainActivityUiHandler = uiHandler;
        getShredPreferenceData();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {


                if (!bundle.getBoolean(Values.MyActions.NOTIFICATION, false)) {


                    activeChat = bundle.getParcelable(Values.IntentData.USER_DATA);
                    position = bundle.getInt(Values.IntentData.POSITION);
                    activeChatPosition=position;
                    selectedPositionSiteId = bundle.getString(Values.IntentData.SELECTED_AGENT_SITE_ID);
                   /* List<ActiveChat> temp = new ArrayList<>();
                    temp = bundle.getParcelableArrayList(Values.IntentData.USER_LIST);
                    activeChatArrayList = temp;*/

                    ActiveChat ac=activeChatsTable.getActiveChat(Integer.parseInt(activeChat.getAgentId()),Integer.parseInt(activeChat.getSiteId()));

                    Helper.getInstance().LogDetails("Intent data activeChat ",activeChat.getChatStatus()+" "+activeChat.getAgentId()+" "+ac.getChatStatus()+" "+ac.getAgentId());
                    List<ActiveChat> temp = new ArrayList<>();
                    temp = activeChatsTable.getActiveChatList(Integer.parseInt(activeChat.getSiteId()));
                    activeChatArrayList = temp;




                    if (activeChat != null) {

                        Helper.getInstance().LogDetails("Intent data activeChat if", "called " + activeChat.getTmVisitorId()+" "+activeChat.getChatStatus());

                        receiver_id = activeChat.getTmVisitorId();
                        receiver_email = activeChat.getEmail();
                        receiver_name = activeChat.getName();
                        chat_id = activeChat.getChatId();
                        agent_id = activeChat.getAgentId();
                        current_site_id = activeChat.getSiteId();
                        conversation_reference_id = activeChat.getChatReferenceId();

                        updateReadMessageStatus();

                    }


                    getSiteData();
                    //setSites();
                    setUnreadCount();
                    if(isSelf){

                        updateSiteListWithLocalDb();
                        getTypingMessage();
                        checkUserStatus();


                    }
                    else
                    {
                        getActiveChatsApi();
                    }


                    //getSiteAssetsApi();
                    if(current_site_id!=null && !current_site_id.isEmpty())
                    updateSiteAssetDataWithLocalDb(Integer.parseInt(current_site_id));


                } else if (bundle.getBoolean(Values.MyActions.NOTIFICATION, false)) {
                    NotificationHelper.getInstance().clearNotifications(ChatActivity.this);

                    Helper.getInstance().LogDetails("Intent data activeChat else if", "called " + isSelf);
                    isNavigatedFromNotification = true;
                    boolean isEnter = false;
                    if (!isSelf) {

                        disconnectTmSocket();
                        getShredPreferenceData();
                        tmSocketConnection();
                        isEnter = true;

                    }


                    if (NotificationHelper.r != null && NotificationHelper.r.isPlaying()) {
                        NotificationHelper.r.stop();
                    }
                    Helper.getInstance().LogDetails("Intent data activeChat not name", " " + bundle.get(Values.Notification.user_name)+" "+bundle.get(Values.Notification.receiver_id));

                    activeChat = bundle.getParcelable(Values.IntentData.USER_DATA);
                    if (activeChat != null) {

                        Helper.getInstance().LogDetails("Intent data activeChat not null", " " + activeChat.getTmVisitorId());
                        receiver_id = activeChat.getTmVisitorId();
                        receiver_email = activeChat.getEmail();
                        receiver_name = activeChat.getName();
                        chat_id = activeChat.getChatId();
                        agent_id = activeChat.getAgentId();
                        current_site_id = activeChat.getSiteId();
                        conversation_reference_id = activeChat.getChatReferenceId();
                        // updateReadMessageStatus();

                        Helper.getInstance().LogDetails("Intent data activeChat not null", " " + activeChat.toString());
                    }

                    getSiteData();
                    setUnreadCount();
                    if(isSelf)
                    {
                        /*updateSiteListWithLocalDb();
                        getTypingMessage();
                        checkUserStatus();*/
                        getActiveChatsApi();
                    }
                    else
                    {
                        getActiveChatsApi();
                    }

                    //getSiteAssetsApi();
                    if(current_site_id!=null && !current_site_id.isEmpty())
                        updateSiteAssetDataWithLocalDb(Integer.parseInt(current_site_id));


                } else {
                    Helper.getInstance().LogDetails("Intent data activeChat else ===", "called ");
                }


                if (activeChatArrayList != null) {
                    if (topAdapter == null) {
                        topAdapter = new TopRecyclerAdapter(ChatActivity.this, activeChatArrayList, isSelf);
                    }
                }


                if (topAdapter != null && activeChatArrayList != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topAdapter.notifyDataSetChanged();
                        }
                    });
                }
                //  getHistory();


            }

            back();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 /*   private void setSites() {
        try{
            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int j = 0; j < sitesInfoList.size(); j++) {
                    List<ActiveChat> activeChatArrayList = new ArrayList<>();
                    sitesInfoList.get(j).setActiveChats(activeChatArrayList);

                    String sid = sitesInfoList.get(j).getSiteId();
                    if(isSelf){
                        sitesInfoList.get(j).setPresent(true);
                    }
                    else
                    {
                        if(selectedPositionSiteId!=null && !selectedPositionSiteId.trim().isEmpty())
                        {
                            boolean isExists=false;
                            String[] separated = selectedPositionSiteId.split(",");
                            if(separated!=null && separated.length>0){
                                for(int i=0;i<separated.length;i++){
                                    Helper.getInstance().LogDetails("updateSiteList =============",sid+" "+separated[i]+" "+selectedPositionSiteId);
                                    if(sid.equals(separated[i].trim())){
                                        isExists=true;
                                        break;
                                    }
                                }
                                if(isExists){
                                    sitesInfoList.get(j).setPresent(true);
                                }
                                else{
                                    sitesInfoList.get(j).setPresent(false);
                                }

                            }
                            else
                            {

                                sitesInfoList.get(j).setPresent(false);
                            }
                        }
                        else if(selectedPositionRole!=null && !selectedPositionRole.trim().isEmpty() && Integer.parseInt(selectedPositionRole)==Values.UserRoles.ADMIN)
                        {
                            sitesInfoList.get(j).setPresent(true);
                        }
                        else{

                            sitesInfoList.get(j).setPresent(false);
                        }
                    }
                }
                if(companyAdapter!=null){
                    companyAdapter.notifyDataSetChanged();
                }
            }
        }catch (Exception e){

        }
    }*/


    private void setActiveList() {
        if (sitesInfoList != null && sitesInfoList.size() > 0) {

            removeDuplicates();

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    String sid = sitesInfoList.get(i).getSiteId();
                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                        activeChatArrayList.clear();
                    }
                    if (sid!=null && current_site_id != null && current_site_id.equals(sid)) {
                        List<ActiveChat> temp = new ArrayList<>();
                        temp = sitesInfoList.get(i).getActiveChats();
                        if(temp!=null){
                            activeChatArrayList.addAll(temp);
                        }

                    }
                }
            }
            if (topAdapter != null) {
                topAdapter.notifyDataSetChanged();
            }




        }
    }

    private void getSiteData() {
        try{
            Helper.getInstance().LogDetails("getSiteData","called "+isSelf);
            if(sitesTable==null){
                sitesTable=new SitesTable(ChatActivity.this);
            }

        if (isSelf) {
            sitesInfoList=sitesTable.getSites();

        } else {
            sitesInfoList=sitesTable.getSites();

        }
        if(sitesInfoList!=null && sitesInfoList.size()>0){
            for(int i=0;i<sitesInfoList.size();i++){
                Helper.getInstance().LogDetails("getSiteData","status  "+sitesInfoList.get(i).getSiteName()+" "+sitesInfoList.get(i).isPresent());
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveSiteData() {
        try{
            Helper.getInstance().LogDetails("saveSiteData","called "+isSelf);
            if (isSelf) {
               Session.saveSiteInfoList(ChatActivity.this, sitesInfoList, Session.SP_SITE_INFO);
            } else {
               Session.saveSiteInfoList(ChatActivity.this, sitesInfoList, Session.SP_OTHERS_SITE_INFO);
            }

            if(sitesInfoList!=null && sitesInfoList.size()>0){
                for(int i=0;i<sitesInfoList.size();i++){
                    Helper.getInstance().LogDetails("saveSiteData","status  "+sitesInfoList.get(i).getSiteName()+" "+sitesInfoList.get(i).isPresent());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removeDuplicates() {
        try{
        if (sitesInfoList != null && sitesInfoList.size() > 0) {
            for (int k = 0; k < sitesInfoList.size(); k++) {
                if (sitesInfoList.get(k).getActiveChats() != null && sitesInfoList.get(k).getActiveChats().size() > 0) {
                    for (int i = 0; i < sitesInfoList.get(k).getActiveChats().size(); i++) {
                        for (int j = i + 1; j < sitesInfoList.get(k).getActiveChats().size(); j++) {

                            if (sitesInfoList.get(k).getActiveChats().get(i).getTmVisitorId() != null && sitesInfoList.get(k).getActiveChats().get(j).getTmVisitorId() != null) {
                                if (sitesInfoList.get(k).getActiveChats().get(i).getTmVisitorId().equals(sitesInfoList.get(k).getActiveChats().get(j).getTmVisitorId())) {
                                    sitesInfoList.get(k).getActiveChats().remove(j);

                                }
                            }

                        }
                    }
                }

            }

        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkUserAvailable() {

        try{

        if (activeChat != null) {

            Helper.getInstance().LogDetails("checkUserAvailable Intent data ", activeChat.toString() + " " + activeChatArrayList.size());
            ActiveChat data = getActiveUserData(activeChat.getChatReferenceId());

            if (data != null) {

                activeChat=data;

                Helper.getInstance().LogDetails("checkUserAvailable","if caled");

                receiver_id = activeChat.getTmVisitorId();
                receiver_email = activeChat.getEmail();
                receiver_name = activeChat.getName();
                chat_id = activeChat.getChatId();
                agent_id = activeChat.getAgentId();
                conversation_reference_id = activeChat.getChatReferenceId();


                if (conversationTable == null) {
                    conversationTable = new ConversationTable(ChatActivity.this);
                }



                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                    for (int i = 0; i < activeChatArrayList.size(); i++) {
                        if (activeChatArrayList.get(i) != null && activeChatArrayList.get(i).getChatReferenceId() != null) {
                            if (conversation_reference_id != null && conversation_reference_id.equals(activeChatArrayList.get(i).getChatReferenceId())) {
                                if(i<activeChatArrayList.size())
                                {
                                    activeChatArrayList.get(i).setUnread_message_count(0);

                                }

                                Helper.getInstance().LogDetails("checkUserAvailable","calling back");
                                back();
                                break;
                            }
                        }
                    }


                    if (topAdapter == null) {
                        topAdapter = new TopRecyclerAdapter(ChatActivity.this, activeChatArrayList, isSelf);
                    }

                    if (topAdapter != null) {
                        topAdapter.notifyDataSetChanged();
                    }


                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeChatPosition=-1;
                            list.clear();
                            activeChat = null;
                            receiver_id = "0";
                            chat_id = "0";
                            //agent_id = "0";
                            lastMessageId = "0";
                            conversation_reference_id = "";
                            if (noDataImage != null) {
                                if(list!=null && list.size()>0){
                                    list.clear();
                                    if(adapter!=null)
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                noDataImage.setVisibility(View.VISIBLE);
                                if (chatRecyclerView != null) {
                                    chatRecyclerView.setVisibility(View.GONE);
                                }
                                if (usersLayout != null) {
                                    usersLayout.setVisibility(View.GONE);


                                }
                                if (bottomLayout != null) {
                                    bottomLayout.setVisibility(View.GONE);
                                }

                            }
                        }
                    });
                }

            } else {

                Helper.getInstance().LogDetails("checkUserAvailable","else caled");
                Helper.getInstance().LogDetails("checkUserAvailable Intent data ", "toast 1");


                setAnotherUser();



            }
        } else {
            Helper.getInstance().LogDetails("checkUserAvailable","else eee caled");
            Helper.getInstance().LogDetails("checkUserAvailable Intent data ", "toast 1");

            setAnotherUser();



        }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setAnotherUser(){

        if(activeChatArrayList!=null && activeChatArrayList.size()>0){
            Helper.getInstance().LogDetails("checkUserAvailable updateAgentChatEnded removeChat","called updateChatList");
            activeChatPosition=-1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(reply_message_layout==null){
                        reply_message_layout=findViewById(R.id.reply_message_layout);
                    }
                    reply_message_layout.setVisibility(View.GONE);
                    isReplyMessage=false;
                }
            });
            updateChatList(activeChatArrayList.get(0),0);
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (noDataImage != null) {
                                                                            /*if(list!=null && list.size()>0){
                                                                                list.clear();
                                                                                if(adapter!=null)
                                                                                {
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                       }*/
                        noDataImage.setVisibility(View.VISIBLE);
                        if (chatRecyclerView != null) {
                            chatRecyclerView.setVisibility(View.GONE);
                        }
                        if (usersLayout != null) {
                            usersLayout.setVisibility(View.GONE);


                        }
                        if (bottomLayout != null) {
                            bottomLayout.setVisibility(View.GONE);
                        }

                        reply_message_layout.setVisibility(View.GONE);
                        isReplyMessage=false;

                    }
                }
            });
        }
    }

    private void back(){

        if(activeChat!=null){
            Helper.getInstance().LogDetails("updateSiteListWithLocalDb backMethod","called "+activeChat.toString());
            String chatStatus=activeChat.getChatStatus();
            if(chatStatus!=null && chatStatus.equals(String.valueOf(Values.ChatStatus.CHAT_USER_ENDED))){
                if(chat_edit_txt==null){
                    chat_edit_txt=findViewById(R.id.chat_edit_txt);
                }
                if(send_icon==null){
                    send_icon=findViewById(R.id.send_icon);

                }
                if(userEndedChatLayout==null){
                    userEndedChatLayout=findViewById(R.id.userEndedChatLayout);
                }
                chat_edit_txt.setHint("You can't send messages");
                chat_edit_txt.setEnabled(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        send_icon.setVisibility(View.GONE);
                        Helper.getInstance().LogDetails("userEndedChatLayout","trigger 3");
                        Helper.getInstance().LogDetails("updateSiteListWithLocalDb backMethod","end chat visible called ");
                        userEndedChatLayout.setVisibility(View.VISIBLE);
                    }
                });



            }

        }

    }


    private void initializeViews(){


        actionLable.setVisibility(View.GONE);

        companyRecyclerView.setVisibility(View.VISIBLE);
        // back_icon.setImageResource(R.drawable.back_arrow);
        Glide.with(ChatActivity.this).load(R.drawable.left_arrow).into(back_icon);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
        smartRepliesRecyclerView.setLayoutManager(layoutManager1);
        smartRepliesRecyclerView.setNestedScrollingEnabled(false);
        smartRepliesMessageAdapter = new SmartRepliesMessageAdapter(ChatActivity.this, smartRepliesList);
        smartRepliesMessageAdapter.selectionMode = true;
        smartRepliesRecyclerView.setAdapter(smartRepliesMessageAdapter);

        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerview, int dx, int dy) {
                super.onScrolled(recyclerview, dx, dy);


                int totalItemCount = chatLayoutManager.getItemCount();
                int pastVisiblesItems = chatLayoutManager.findLastVisibleItemPosition();
               int  lastScrollPosition = chatLayoutManager.findLastVisibleItemPosition();
               scrollPosition   =   chatLayoutManager.findLastCompletelyVisibleItemPosition();

                Helper.getInstance().LogDetails("ChatActivity addOnScrollListener",totalItemCount+" "+pastVisiblesItems+" "+lastScrollPosition+" "+scrollPosition);


                if (dy != 0 && dy<0) {

                    Helper.getInstance().LogDetails("ChatActivity addOnScrollListener","if down calledd"+dy);


                    if (list != null && list.size() > 0) {
                        if (pastVisiblesItems+15>totalItemCount) {
                            Helper.getInstance().LogDetails("ChatActivity addOnScrollListener if ", scrollPosition + " " );

                            if(!isApiCalled){
                                isRefresh=true;
                                isApiCalled=true;
                                progressBar.setVisibility(View.VISIBLE);
                                callGetConversationsApi();
                            }


                        } else {
                            Helper.getInstance().LogDetails("ChatActivity addOnScrollListener else ", scrollPosition + " ");
                        }
                    }


                }
                else
                {

                    Helper.getInstance().LogDetails("ChatActivity addOnScrollListener","else down calledd"+dy);
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView agents_recyclerView, int newState) {
                super.onScrollStateChanged(agents_recyclerView, newState);
            }
        });

    }
    @Override
    public void initViews() {

        try{


        getShredPreferenceData();
        TopRecyclerAdapter.tmUserId = tmUserId;
        ConversationAdapter.tmUserId = tmUserId;

        initializeViews();
        //toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        setupUI(chatRecyclerView);
        setupUI(topRecyclerView);


        if (isSelf) {

            if((role!=null && !role.trim().isEmpty() && Integer.parseInt(role)==Values.UserRoles.MODERATOR))
            {
                send_icon.setVisibility(View.GONE);
                chat_edit_txt.setHint("You can't send messages");
                chat_edit_txt.setEnabled(false);
                agentNameLay.setVisibility(View.VISIBLE);
                agent_name_tv.setText(Helper.getInstance().capitalize(user_name));
                bottom_lay_card.setVisibility(View.VISIBLE);
              //  moreLayoutLine.setVisibility(View.VISIBLE);
            }

            agentNameLay.setVisibility(View.GONE);

        } else {


            send_icon.setVisibility(View.GONE);
            chat_edit_txt.setHint("You can't send messages");
            chat_edit_txt.setEnabled(false);
            agentNameLay.setVisibility(View.VISIBLE);
            agent_name_tv.setText(Helper.getInstance().capitalize(user_name));
            bottom_lay_card.setVisibility(View.VISIBLE);
          //  moreLayoutLine.setVisibility(View.VISIBLE);

        }

        if (companyRecyclerView != null) {

            LinearLayoutManager layoutManager5 = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
            companyRecyclerView.setLayoutManager(layoutManager5);
            companyRecyclerView.setNestedScrollingEnabled(false);
            companyAdapter = new CompanyAdapter(ChatActivity.this, sitesInfoList, isSelf);
            companyAdapter.selectionMode = true;
            companyRecyclerView.setAdapter(companyAdapter);
        }
            visit_pages_recycleView = findViewById(R.id.visit_pages_recycleView);
            if (visit_pages_recycleView != null) {

                visitPagesManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
                visit_pages_recycleView.setLayoutManager(visitPagesManager);
                visit_pages_recycleView.setNestedScrollingEnabled(false);
                visitPagesAdapter = new VisitPagesAdapter(ChatActivity.this, visitPages, isSelf);
                visitPagesAdapter.selectionMode = true;
                visit_pages_recycleView.setAdapter(visitPagesAdapter);
            }


        layoutManager1 = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
        topRecyclerView.setLayoutManager(layoutManager1);
        topRecyclerView.setNestedScrollingEnabled(false);
        topAdapter = new TopRecyclerAdapter(ChatActivity.this, activeChatArrayList, isSelf);
        topRecyclerView.setAdapter(topAdapter);

        scrollList(position);

        chatLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new ConversationAdapter(ChatActivity.this, list, receiver_id, receiver_name, handler);
        chatLayoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.setNestedScrollingEnabled(false);
        chatRecyclerView.setAdapter(adapter);




        chat_edit_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                more_icon.setImageDrawable(getResources().getDrawable(R.drawable.plus));


                return false;
            }
        });

        chat_edit_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                emitAgentTyping();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

  /*      final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff >Helper. dpToPx(ChatActivity.this, 100)) { // if more than 200 dp, it's probably a keyboard...
                    if(isSelf)
                    {
                        chat_edit_txt.requestFocus();
                        Utilities.openKeyboard(ChatActivity.this, chat_edit_txt);
                    }

                }
            }
        });*/
            if(!isAwsApiCalled){
                if (Helper.getConnectivityStatus(ChatActivity.this)) {
                    callGetAwsConfig();
                }
            }


            back();



        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void sortUserList() {
        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
            Collections.sort(activeChatArrayList, new Comparator<ActiveChat>() {
                public int compare(ActiveChat o1, ActiveChat o2) {
                    try {

                        return getDate(o2.getChatModel().getCreated_at()).compareTo(getDate(o1.getChatModel().getCreated_at()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
    }


    private void getAccessToken() {

            context=ChatActivity.this;
            accessToken = Session.getAccessToken(context);
            socketId = Session.getSocketId(context);
            checkUserStatus();

    }

    private void getShredPreferenceData() {
        try{

            context=ChatActivity.this;


        isSelf = Session.getIsSelf(context);
        if (isNavigatedFromNotification) {
            Session.setIsSelf(context,true);
            isSelf = true;
        }
        if (isSelf) {
            user_id =  Session.getUserID(context);
            agent_id =  Session.getUserID(context);
            tmUserId =  Session.getTmUserId(context);

            site_id =  Session.getSiteId(context);
            role =  Session.getUserRole(context);
            apiRole =  Session.getUserRole(context);
            account_id =  Session.getAccountId(context);
            company_token =  Session.getCompanyToken(context);
            user_name = Session.getUserName(context);
        } else {
            user_id = Session.getOtherUserId(context);
            tmUserId = Session.getOtherUserSiteId(context);
            site_id = Session.getOtherUserSiteId(context);
            role = Session.getOtherUserRole(context);
            apiRole =  Session.getOtherUserRole(context);
            account_id = Session.getOtherUserAccountId(context);
            company_token = Session.getCompanyToken(context);
            user_name = Session.getOtherUserDisplayName(context);
        }
        accessToken =Session.getAccessToken(context);
        socketId =  Session.getSocketId(context);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sortList() {
        try {
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<ChatModel>() {
                    @Override
                    public int compare(ChatModel c1, ChatModel c2) {
                        return c1.getMessage_id() - c2.getMessage_id(); // Ascending
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void sortMessageListByTime(){


        try {
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<ChatModel>() {
                    @Override
                    public int compare(ChatModel c1, ChatModel c2) {
                        try {
                            return getDateCreatedTime(c2.getCreated_at()).compareTo(getDateCreatedTime(c1.getCreated_at())); // Ascending
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date getDateCreatedTime(String date) throws ParseException {

        try{


            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            return d;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    private void getHistory() {





        try {
            Helper.getInstance().LogDetails("checkUserAvailable getHistory","called");

            if (receiver_id != null && tmUserId != null && !receiver_id.trim().isEmpty() && !tmUserId.trim().isEmpty()) {
                Helper.getInstance().LogDetails("checkUserAvailable getHistory","called"+receiver_id+" "+tmUserId +conversation_reference_id);
                Helper.getInstance().LogDetails("getHistoty", "called" + receiver_id + " " + tmUserId + " " + conversation_reference_id);


                //  updateReadMessageStatus();
                if (conversationTable == null) {
                    conversationTable = new ConversationTable(ChatActivity.this);
                }


                List<ChatModel> chatModels=conversationTable.getLatestChat(Integer.parseInt(receiver_id),Integer.parseInt(tmUserId),conversation_reference_id);

                if(chatModels!=null && chatModels.size()>0){
                    if(list!=null && list.size()>0){
                        list.clear();
                    }

                   // Helper.getInstance().LogDetails("getHistotyMessageId",chatModels.get(0).getMessage_id()+" "+chatModels.get(1).getMessage_id()+" "+lastMessageId);
                    lastMessageId= String.valueOf(chatModels.get(chatModels.size()-1).getMessage_id());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter!=null)
                                adapter.notifyDataSetChanged();
                        }
                    });
                    Helper.getInstance().LogDetails("checkUserAvailable getHistory","local called"+chatModels.size());
                    list.addAll(chatModels);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter!=null)
                            adapter.notifyDataSetChanged();
                        }
                    });
                    updateReadMessageStatus();

                    //getSmartReplies();

                }
                else
                {
                    Helper.getInstance().LogDetails("checkUserAvailable getHistory","api called");
                    if (Helper.getConnectivityStatus(ChatActivity.this)) {

                        callGetConversationsApi();
                    } else {
                        Toast.makeText(ChatActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                    }

                }


                if (info_layout != null && info_layout.getVisibility() == View.VISIBLE) {
                   // showCustomerInfo();
                    showCustomerInfoDialog();
                }
                else if(isInfoClicked)
                {

                    //showCustomerInfo();
                    showCustomerInfoDialog();
                }

                if (contact_layout != null && contact_layout.getVisibility() == View.VISIBLE) {
                   showContactInfo();
                }
                else if(isContactCliked)
                {

                    showContactInfo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*  private void getHistory() {
        try{

            Helper.getInstance().LogDetails("getHistoty","called"+receiver_id+" "+tmUserId+" "+conversation_reference_id);
            if(receiver_id!=null && tmUserId!=null && !receiver_id.isEmpty() && !tmUserId.isEmpty())
            {

                updateReadMessageStatus();


                    if(conversationTable==null){
                        conversationTable=new ConversationTable(ChatActivity.this);
                    }
                    if(agentsList!=null && agentsList.size()>0){
                        agentsList.clear();
                        if(adapter!=null)
                        {
                            if(isTabChanged)
                            {
                                adapter.setList(agentsList);

                            }
                            adapter.notifyDataSetChanged();
                        }

                    }


                    agentsList= conversationTable.getLatestChat(Integer.parseInt(receiver_id), Integer.parseInt(tmUserId),conversation_reference_id);
                    if(agentsList!=null && agentsList.size()>0){
                        Helper.getInstance().LogDetails("getHistoty"," agentsList size "+agentsList.size());
                        lastMessageId= String.valueOf(agentsList.get(agentsList.size()-1).getMessage_id());
                        if(isTabChanged)
                        {
                            adapter.setList(agentsList);
                        }

                        if(adapter==null){
                            adapter=new ConversationAdapter(ChatActivity.this,agentsList,receiver_id,receiver_name,handler);
                        }
                        runOnUiThread(new Runnable(){
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                        scrollItem();

                    }
                    else
                    {

                        if(Helper.getConnectivityStatus(ChatActivity.this)){
                            callGetConversationsApi();
                        }
                        else
                        {
                            Toast.makeText(ChatActivity.this,"Please check internet connection",Toast.LENGTH_LONG).show();
                        }
                    }



                if(info_layout!=null && info_layout.getVisibility()==View.VISIBLE)
                {
                    showCustomerInfo();
                }



            }
        }catch (Exception e){
                e.printStackTrace();
            }

    }*/
    private void requestLayout() {
        chatRecyclerView.requestLayout();
    }

    @Override
    protected void chatEndedAgent(JSONObject obj) {


    }

    @Override
    protected void contactUpdate(JSONObject obj) {
        if(obj!=null){
            updateContact(obj);
        }
    }

    @Override
    protected void agentActivity(JSONObject obj) {

    }


    @Override
    protected void syncOfflineMessages() {

    }

    @Override
    protected void getMissingMessages() {


    }

    @Override
    protected void agentStatusUpdated(final JSONObject jsonObject) {

    }

    @Override
    protected void userChatEnded(final JSONObject jsonObject) {
        updateUserChatEnded(jsonObject);


    }

    @Override
    protected void newOnline(final JSONObject jsonObject) {
        updateNewOnlineUser(jsonObject);



    }

    @Override
    protected void userTypingMessage(JSONObject obj) {

        updateUserTypingMessage(obj);
    }

    @Override
    protected void agentChatEnded(JSONObject obj) {
        updateAgentChatEnded(obj);
    }

    private void updateUserTypingMessage(JSONObject obj) {
         /*  {"DJdZj6NIMFU1Q":"o03janiz7emplhbp0lvekz6sudkbyxwz5c543cfc12e7f766213878",
                "chatReference":"93816558ce7507934c","agent_id":"18",
                "gfEO3lTn9rAo1hGi":1232,"type_message":"hhhhhhh"}*/
         try{

        if (obj != null) {

            try {
                String siteToken = "", message = "", chatReferenceId = "", agentId = "";
                siteToken = obj.getString("DJdZj6NIMFU1Q");
                message = obj.getString("type_message");
                chatReferenceId = obj.getString("chatReference");
                agentId = obj.getString("agent_id");
                if (activeChat != null && activeChat.getChatReferenceId() != null && chatReferenceId!=null && activeChat.getChatReferenceId().equals(chatReferenceId)) {
                    ChatModel model = new ChatModel();
                    if (agentId != null && user_id != null && agentId.equals(user_id)) {


                        model.setMessage(message);
                        model.setIs_delivered(0);
                        model.setIs_received(1);
                        model.setIs_read(1);
                        model.setMessage_type(Values.MessageType.MESSAGE_TYPE_TYPING);
                        model.setReference_id(chatReferenceId);


                        if (list != null && list.size() > 0) {
                            boolean present = false;

                            for (int i = 0; i < list.size(); i++) {
                                String rid = list.get(i).getReference_id();
                                Helper.getInstance().LogDetails("updateUserTypingMessage check", rid + " " + chatReferenceId);
                                if (rid != null && chatReferenceId != null && !rid.isEmpty() && !chatReferenceId.isEmpty() && rid.equals(chatReferenceId)) {
                                    present = true;
                                    if (message != null && !message.trim().isEmpty()) {

                                        list.get(i).setMessage(message);
                                    } else {
                                        list.remove(i);

                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });


                                    return;

                                }
                            }
                            if (!present) {

                                if (message != null && !message.trim().isEmpty()) {
                                    list.add(0, model);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    scrollItem();
                                }

                            }
                        } else {

                            if (message != null && !message.trim().isEmpty()) {
                                list.add(0, model);


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                scrollItem();
                            }

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    @Override
    protected void agentStatusInfo(JSONArray jsonObject) {

    }

    @Override
    public void messageSent(final JSONObject object) {
        Helper.getInstance().LogDetails("messageSent", "called chat");

        newMessageSent(object);


    }

    @Override
    public void messageReceived(final JSONObject object) {


        newMessageReceived(object);
        updateReadMessageStatus();
        updateUnreadCount(object);
        setUnreadCount();


    }

    @Override
    public void messageDelivered(final JSONObject object) {



        newMessageDelivered(object);

    }

    @Override
    public void messageRead(final JSONObject object) {

        if (isSelf) {
            newMessageRead(object);
            updateUnreadCount(object);
            setUnreadCount();
        }
        else{
            newMessageRead(object);
            updateUnreadCount(object);
            setUnreadCount();
        }





    }

    @Override
    public void messageReadByMe(JSONObject object) {
        if (isSelf) {
            newMessageRead(object);
            updateUnreadCount(object);
            setUnreadCount();
        }

    }

    @Override
    public void userAvailabilityStatus(JSONObject object) {
        updateUserOnlineStatus(object);
    }

    @Override
    public void userOffline(JSONObject object) {
        updateUserOffline(object);
    }

    @Override
    public void userOnline(JSONObject object) {
        updateUserOnline(object);
    }

    private void updateUserOnline(JSONObject jsonObject){
        try{


        if(jsonObject!=null){
            //{"user_id":"827","reference_token":""}
            if(activeChatArrayList!=null && activeChatArrayList.size()>0){
                Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",activeChatArrayList.size()+"");
                String tmUserId=jsonObject.optString("user_id");
                String chatReferenceId=jsonObject.optString("reference_token");
                if(chatReferenceId!=null && !chatReferenceId.trim().isEmpty() ){


                    for (int i = 0; i < activeChatArrayList.size(); i++) {
                        if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty() && tmUserId != null && !tmUserId.isEmpty() && activeChatArrayList.get(i).getTmVisitorId().equals(tmUserId)) {
                            String tmUid="",chatRef="";
                            tmUid = activeChatArrayList.get(i).getTmVisitorId();
                            chatRef = activeChatArrayList.get(i).getChatReferenceId();
                            if(tmUid!=null && !tmUid.trim().isEmpty())
                            {
                                Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",tmUid+" "+chatReferenceId+" "+chatRef+" "+tmUserId);
                                if (chatReferenceId != null && chatRef != null && !chatReferenceId.trim().isEmpty() && chatReferenceId.equals(chatRef)) {

                                    activeChatArrayList.get(i).setOnline(1);

                                }
                            }

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topAdapter.notifyDataSetChanged();
                        }
                    });

                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateUserOffline(JSONObject jsonObject){
        try{

        if(jsonObject!=null){
                //{"user_id":"827","reference_token":""}
            if(activeChatArrayList!=null && activeChatArrayList.size()>0){
                Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",activeChatArrayList.size()+"");
                String tmUserId=jsonObject.optString("user_id");
                String chatReferenceId=jsonObject.optString("reference_token");
                if(chatReferenceId!=null && !chatReferenceId.trim().isEmpty() ){


                    for (int i = 0; i < activeChatArrayList.size(); i++) {
                        if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty() && tmUserId != null && !tmUserId.isEmpty() && activeChatArrayList.get(i).getTmVisitorId().equals(tmUserId)) {
                            String tmUid="",chatRef="";
                            tmUid = activeChatArrayList.get(i).getTmVisitorId();
                            chatRef = activeChatArrayList.get(i).getChatReferenceId();
                            if(tmUid!=null && !tmUid.trim().isEmpty())
                            {
                                Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",tmUid+" "+chatReferenceId+" "+chatRef+" "+tmUserId);
                                if (chatReferenceId != null && chatRef != null && !chatReferenceId.trim().isEmpty() && chatReferenceId.equals(chatRef)) {

                                    activeChatArrayList.get(i).setOnline(0);

                                }
                            }

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topAdapter.notifyDataSetChanged();
                        }
                    });

                }

            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateUserOnlineStatus(JSONObject jsonObject){
        try {





            if (jsonObject != null ) {

                Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",jsonObject.toString());

                   /*{"online":[5771,5927,5931,5936,5938,5765,5918,5906,3715,5604,3753,100],"dnd":[],
                    "online_reference":{"100":[""],"3715":[""],"3753":[""],"5604":[""],"5765":[""],
                    "5771":[""],"5906":[""],"5918":[""],"5927":[""],"5931":["13d560409827c049a3"],
                    "5936":["98505844fd6a53d555"],"5938":["bd26f4620b375a19f"]}}*/

                   if(activeChatArrayList!=null && activeChatArrayList.size()>0){
                       Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",activeChatArrayList.size()+"");
                       JSONObject online_reference=jsonObject.optJSONObject("online_reference");
                       if(online_reference!=null ){


                               for (int i = 0; i < activeChatArrayList.size(); i++) {
                                   if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty() && tmUserId != null && !tmUserId.isEmpty()) {
                                       String tmUid="",chatRef="",charReferenceId="",tempCharReferenceId="";
                                       tmUid = activeChatArrayList.get(i).getTmVisitorId();
                                       chatRef = activeChatArrayList.get(i).getChatReferenceId();
                                       activeChatArrayList.get(i).setOnline(0);
                                       if(tmUid!=null && !tmUid.trim().isEmpty())
                                       {
                                           tempCharReferenceId=online_reference.optString(tmUid);
                                           if(tempCharReferenceId!=null && !tempCharReferenceId.trim().isEmpty())
                                           {
                                               charReferenceId=tempCharReferenceId.replace("[\"","").replace("\"]","");
                                               Helper.getInstance().LogDetails("checkUserStatus updateUserOnlineStatus",tmUid+" "+charReferenceId+" "+chatRef);
                                               if (charReferenceId != null && chatRef != null && !charReferenceId.trim().isEmpty() && charReferenceId.equals(chatRef)) {

                                                   activeChatArrayList.get(i).setOnline(1);

                                               }

                                           }


                                       }

                                   }
                               }


                       }
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               topAdapter.notifyDataSetChanged();
                           }
                       });
                   }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateUnreadCount(JSONObject jsonObject) {

        try {

            if (jsonObject != null && activeChatArrayList.size() > 0) {

                int receiverId = jsonObject.optInt("receiver_id");
                int senderId = jsonObject.optInt("sender_id");
                String conversationReferenceId = jsonObject.optString("conversation_reference_id");

                for (int i = 0; i < activeChatArrayList.size(); i++) {
                    if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty() && tmUserId != null && !tmUserId.isEmpty()) {
                        String tmUid, crid;
                        tmUid = activeChatArrayList.get(i).getTmVisitorId();
                        crid = activeChatArrayList.get(i).getChatReferenceId();
                        if (tmUid != null && crid != null && tmUserId != null && !tmUserId.trim().isEmpty() && receiverId == Integer.parseInt(tmUserId) && senderId == Integer.parseInt(tmUid) && conversationReferenceId!=null &&crid.equals(conversationReferenceId)) {
                            int unreadCount = conversationTable.getTheUnreadMessageAvailable(senderId, Integer.parseInt(tmUserId), conversationReferenceId);
                            if(i<activeChatArrayList.size())
                            {
                                activeChatArrayList.get(i).setUnread_message_count(unreadCount);

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    topAdapter.notifyDataSetChanged();
                                }
                            });

                        } else {
                            if (crid != null && crid.equals(conversationReferenceId)) {
                                int unreadCount = conversationTable.getTheUnreadMessageAvailable(senderId, Integer.parseInt(tmUserId), conversationReferenceId);
                               if(i<activeChatArrayList.size()){
                                   activeChatArrayList.get(i).setUnread_message_count(unreadCount);
                               }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        topAdapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }
                    }
                }


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        topAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setUnreadCount() {
        try{
        if (sitesInfoList != null && sitesInfoList.size() > 0) {
            if (conversationTable == null) {
                conversationTable = new ConversationTable(ChatActivity.this);
            }
            for (int i = 0; i < sitesInfoList.size(); i++) {
                List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();
                int count = 0;
                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                    for (int j = 0; j < activeChatArrayList.size(); j++) {
                        if (activeChatArrayList.get(j).getTmVisitorId() != null && !activeChatArrayList.get(j).getTmVisitorId().isEmpty()) {
                            if (tmUserId != null && !tmUserId.trim().isEmpty()) {
                                int unreadCount = conversationTable.getTheUnreadMessageAvailable(Integer.parseInt(activeChatArrayList.get(j).getTmVisitorId()), Integer.parseInt(tmUserId), activeChatArrayList.get(j).getChatReferenceId());
                                if(j<activeChatArrayList.size())
                                {
                                    activeChatArrayList.get(j).setUnread_message_count(unreadCount);
                                    Helper.getInstance().LogDetails("UPDATE_UNREAD_COUNT setUnreadCount", activeChatArrayList.get(j).getGuestName() + " " + unreadCount);
                                    //count=count+unreadCount;
                                    if (unreadCount > 0) {
                                        count = count + 1;
                                    }

                                }



                            }


                        }
                    }
                    Helper.getInstance().LogDetails("UPDATE_UNREAD_COUNT setUnreadCount", sitesInfoList.get(i).getSiteName() + " " + count);

                }
                sitesInfoList.get(i).setUnread_message_count(count);
            }
            //sortUserList();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (companyAdapter != null) {
                        companyAdapter.notifyDataSetChanged();
                    }
                    if (topAdapter != null) {
                        topAdapter.notifyDataSetChanged();
                    }


                }
            });

            saveSiteData();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void updateContact(JSONObject jsonObject) {
        //{"DJdZj6NIMFU1Q":"1hsg1wawm9h8daowd8qw7r0n04c6b12w5d0383ae8046d114385937","chatReference":"0260d5861d2891303e","agent_id":"10","visitorId":"854","data":{"type":1,"value":"9000000000"}}
        //{"DJdZj6NIMFU1Q":"1hsg1wawm9h8daowd8qw7r0n04c6b12w5d0383ae8046d114385937","chatReference":"0260d5861d2891303e","agent_id":"10","visitorId":"854","data":{"type":2,"value":"mounika@tekula.com"}}
        Helper.getInstance().LogDetails("updateContact", "##"+jsonObject.toString());
        try{

            if (jsonObject != null) {
                String agentId = jsonObject.optString("agent_id");
                JSONObject data=  jsonObject.optJSONObject("data");
                if(data!=null){

                String type=data.optString("type");
                String value=data.optString("value");

                    Helper.getInstance().LogDetails("updateContact","=== "+data.toString() +"  "+type+"  "+value);

                if (agentId != null && user_id != null && agentId.equals(user_id)) {
                    String siteToken = jsonObject.optString("DJdZj6NIMFU1Q");
                    Helper.getInstance().LogDetails("updateContact", jsonObject.toString());
                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sTkn = sitesInfoList.get(i).getSiteToken();

                            if (siteToken != null && !siteToken.trim().isEmpty() && sTkn != null && !sTkn.trim().isEmpty() && siteToken.equals(sTkn)) {

                                String siteId=sitesInfoList.get(i).getSiteId();
                                List<ActiveChat> activeChatList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatList != null && activeChatList.size() > 0) {
                                    for (int j = 0; j < activeChatList.size(); j++) {

                                        if (activeChatList.get(j).getChatReferenceId() != null && !activeChatList.get(j).getChatReferenceId().isEmpty() && jsonObject.optString("chatReference") != null && !jsonObject.optString("chatReference").isEmpty()) {

                                            if (jsonObject.optString("chatReference").equals(activeChatList.get(j).getChatReferenceId())) {

                                                Helper.getInstance().LogDetails("updateContact", "check " + jsonObject.optString("chatReference") + " " + activeChatList.get(j).getChatReferenceId());
                                                if (activeChat != null) {

                                                    if (jsonObject.optString("chatReference").equals(activeChat.getChatReferenceId())) {

                                                        if(type!=null && type.equals("1")){
                                                          activeChat.setMobile(value);
                                                          contact_number_tv.setText(value);
                                                        }
                                                        else
                                                        {
                                                            activeChat.setEmail(value);
                                                            contact_email_tv.setText(value);
                                                        }

                                                    }

                                                }


                                                if (sitesInfoList != null && i < sitesInfoList.size()) {
                                                    if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                                                        if(type!=null && type.equals("1")){
                                                            sitesInfoList.get(i).getActiveChats().get(j).setMobile(value);
                                                        }
                                                        else
                                                        {
                                                            sitesInfoList.get(i).getActiveChats().get(j).setEmail(value);
                                                        }
                                                    }

                                                }
                                                saveSiteData();
                                                if (current_site_id != null && siteId.equals(current_site_id)) {
                                                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                                        for (int k = 0; k < activeChatArrayList.size(); k++) {
                                                            if (jsonObject.optString("chatReference").equals(activeChatArrayList.get(k).getChatReferenceId())) {
                                                                Helper.getInstance().LogDetails("updateContact", "check local" + jsonObject.optString("chatReference") + " " + activeChatArrayList.get(k).getChatReferenceId());
                                                                if(type!=null && type.equals("1")){
                                                                    activeChatArrayList.get(k).setMobile(value);
                                                                }
                                                                else
                                                                {
                                                                    activeChatArrayList.get(k).setEmail(value);
                                                                }
                                                            }
                                                        }

                                                    }

                                                }
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        topAdapter.notifyDataSetChanged();

                                                    }
                                                });


                                                break;
                                            }

                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateAgentChatEnded(JSONObject jsonObject) {
       /* {"agent_id":"1","site_id":2,"chat_id":"1196","refance":"4e624a6501d0249118",
                "site_token":"dz0wjikeehrtn818kle9ux9yo1mytbyc5d0384107e65a800069838"}*/



        try{

            boolean isBack = false;

            if (jsonObject != null) {
                String agentId = jsonObject.optString("agent_id");
                if (agentId != null && user_id != null && agentId.equals(user_id)) {
                    String siteId = jsonObject.optString("site_id");
                    int chat_status=jsonObject.optInt("chat_status");
                    Helper.getInstance().LogDetails("updateaAgentChatEnded", jsonObject.toString());
                    Helper.getInstance().LogDetails("emitChatEndedAgent updateAgentChatEnded", jsonObject.toString() +current_site_id);

                    if (current_site_id != null && jsonObject.optString("site_id").equals(current_site_id)) {
                        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                            for (int k = 0; k < activeChatArrayList.size(); k++) {
                                if (jsonObject.optString("chat_id").equals(activeChatArrayList.get(k).getChatId())) {
                                    Helper.getInstance().LogDetails("updateAgentChatEnded", "check local" + jsonObject.optString("chat_id") + " " + activeChatArrayList.get(k).getChatId()+" "+activeChatArrayList.size());
                                    activeChatArrayList.remove(k);
                                    Helper.getInstance().LogDetails("updateAgentChatEnded after remove",activeChatArrayList.size()+"");
                                    if(isTabChanged){
                                        topAdapter.setList(activeChatArrayList);
                                        Helper.getInstance().LogDetails("updateAgentChatEnded after setlist",activeChatArrayList.size()+"");
                                    }

                                    break;

                                }
                            }

                        }

                    }

                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sid = sitesInfoList.get(i).getSiteId();

                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {


                                List<ActiveChat> activeChatList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatList != null && activeChatList.size() > 0) {
                                    for (int j = 0; j < activeChatList.size(); j++) {

                                        if (activeChatList.get(j).getChatId() != null && !activeChatList.get(j).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {

                                            Helper.getInstance().LogDetails("updateAgentChatEnded",activeChatList.get(j).getChatId()+"");
                                            if (jsonObject.optString("chat_id").equals(activeChatList.get(j).getChatId())) {

                                                if (sitesInfoList != null && i < sitesInfoList.size()) {
                                                    if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                                                        Helper.getInstance().LogDetails("updateAgentChatEnded",activeChatList.get(j).getChatId()+" removed");
                                                        sitesInfoList.get(i).getActiveChats().remove(j);

                                                    }

                                                }
                                                saveSiteData();
                                                break;
                                            }
                                        }
                                    }
                                }


                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        topAdapter.notifyDataSetChanged();

                                                    }
                                                });

                                if (activeChat != null) {

                                    if (jsonObject.optString("chat_id").equals(activeChat.getChatId())) {
                                        isBack = true;
                                        closeDialogs();
                                    }


                                }

                                                if (isBack) {



                                                    Helper.getInstance().LogDetails("updateAgentChatEnded updateAgentChatEnded","called "+false);

                                                    if(activeChatArrayList!=null && activeChatArrayList.size()>0){
                                                        Helper.getInstance().LogDetails("updateAgentChatEnded removeChat","called updateChatList"+isBack);
                                                        activeChatPosition=-1;
                                                        updateChatList(activeChatArrayList.get(0),0);
                                                    }
                                                    else
                                                    {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                activeChatPosition=-1;
                                                                list.clear();
                                                                activeChat = null;
                                                                receiver_id = "0";
                                                                chat_id = "0";
                                                                //agent_id = "0";
                                                                lastMessageId = "0";
                                                                conversation_reference_id = "";
                                                                if (noDataImage != null) {
                                                                            if(list!=null && list.size()>0){
                                                                                list.clear();
                                                                                if(adapter!=null)
                                                                                {
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                            }

                                                                    noDataImage.setVisibility(View.VISIBLE);
                                                                    reply_message_layout.setVisibility(View.GONE);
                                                                    isReplyMessage=false;
                                                                    if (chatRecyclerView != null) {
                                                                        chatRecyclerView.setVisibility(View.GONE);
                                                                    }
                                                                    if (usersLayout != null) {
                                                                        usersLayout.setVisibility(View.GONE);


                                                                    }
                                                                    if (bottomLayout != null) {
                                                                        bottomLayout.setVisibility(View.GONE);
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }


                                                }


                            }


                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateUserChatEnded(JSONObject jsonObject) {

        /* {"chat_id":934,"agent_id":"18","chat_reference_id":"ca89522e43059f42b3","site_id":1}*/

/*        {"chat_id":1180,"visitor_id":1017,"visitor_tm_id":0,
                "guest_name":"6768787878777","start_time":"2019-07-31T06:10:16.000Z",
                "end_time":"2019-07-31T07:04:35.000Z","visitor_os":"Linux (unknown)",
                "visitor_url":"http:\/\/192.168.7.3\/kranthi3.php","chat_rating":"","quality":"0",
                "category":2,"visitor_ip":"192.168.2.38","visitor_browser":"Chrome (70.0.3538.77)",
                "agent_id":"1","chat_reference_id":"bf103ca8403f55d05","location":",,","status":0,
                "visitor_query":"","chat_status":0,"account_id":53,"site_id":2,"track_code":"81AMG2Y6156145",
                "created_at":"2019-07-31T06:10:16.000Z","updated_at":"2019-07-31T07:06:00.000Z"}*/

        try{

        boolean isBack = false;

        if (jsonObject != null) {
            String agentId = jsonObject.optString("agent_id");
            if (agentId != null && user_id != null && agentId.equals(user_id)) {
                String siteId = jsonObject.optString("site_id");
                int chat_status=jsonObject.optInt("chat_status");
                Helper.getInstance().LogDetails("updateUserChatEnded", jsonObject.toString());
                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                    for (int i = 0; i < sitesInfoList.size(); i++) {

                        String sid = sitesInfoList.get(i).getSiteId();

                        if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {


                            List<ActiveChat> activeChatList = sitesInfoList.get(i).getActiveChats();

                            if (activeChatList != null && activeChatList.size() > 0) {
                                for (int j = 0; j < activeChatList.size(); j++) {

                                    if (activeChatList.get(j).getChatId() != null && !activeChatList.get(j).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {

                                        if (jsonObject.optString("chat_id").equals(activeChatList.get(j).getChatId())) {

                                            Helper.getInstance().LogDetails("updateUserChatEnded", "check " + jsonObject.optString("chat_id") + " " + activeChatList.get(j).getChatId());



                                            if (sitesInfoList != null && i < sitesInfoList.size()) {
                                                if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                                                   // sitesInfoList.get(i).getActiveChats().remove(j);
                                                    sitesInfoList.get(i).getActiveChats().get(j).setChatStatus("2");

                                                }

                                            }
                                            saveSiteData();
                                            break;
                                        }

                                    }
                                }
                            }
                                            if (activeChat != null) {

                                                if (jsonObject.optString("chat_id").equals(activeChat.getChatId())) {
                                                    isBack = true;
                                                }


                                            }
                                            if (current_site_id != null && siteId.equals(current_site_id)) {
                                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                                    for (int k = 0; k < activeChatArrayList.size(); k++) {
                                                        if (jsonObject.optString("chat_id").equals(activeChatArrayList.get(k).getChatId())) {
                                                            Helper.getInstance().LogDetails("updateUserChatEnded", "check local" + jsonObject.optString("chat_id") + " " + activeChatArrayList.get(k).getChatId());
                                                           // activeChatArrayList.remove(k);
                                                            activeChatArrayList.get(k).setChatStatus("2");
                                                           closeDialogs();
                                                            break;
                                                        }
                                                    }

                                                }

                                            }
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    topAdapter.notifyDataSetChanged();

                                                }
                                            });

                                            if (isBack) {

                                                Helper.getInstance().LogDetails("updateUserChatEnded FeedBackDialogBox updateUserChatEnded","called "+false);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        send_icon.setVisibility(View.GONE);
                                                        chat_edit_txt.setHint("You can't send messages");
                                                        chat_edit_txt.setText("");
                                                        chat_edit_txt.setEnabled(false);
                                                        Helper.getInstance().LogDetails("userEndedChatLayout","trigger 3");
                                                        userEndedChatLayout.setVisibility(View.VISIBLE);
                                                        reply_message_layout.setVisibility(View.GONE);
                                                        isReplyMessage=false;
                                                    }
                                                });

                                               // getFeedBackCategory(false,activeChat);

                                            }

                        }


                    }
                }
            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeDialogs(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeMoreOtionsDialog();
                if(cannedMessagedialog!=null && cannedMessagedialog.isShowing())
                {
                    cannedMessagedialog.cancel();
                }
                if(feedBackDialog!=null && feedBackDialog.isShowing())
                {
                    feedBackDialog.cancel();
                }
                if(collateralDialog!=null && collateralDialog.isShowing())
                {
                    collateralDialog.cancel();
                }

                if(attachmentOptionsDialog!=null && attachmentOptionsDialog.isShowing())
                {
                    attachmentOptionsDialog.cancel();
                }

                reply_message_layout.setVisibility(View.GONE);
                isReplyMessage=false;

            }
        });

    }



    private void removeChat(ActiveChat chat) {

        /* {"chat_id":934,"agent_id":"18","chat_reference_id":"ca89522e43059f42b3","site_id":1}*/

        try{

            boolean isBack = false;

            if (chat != null) {
                String agentId = chat.getAgentId();
                if (agentId != null && user_id != null && agentId.equals(user_id)) {
                    String siteId = chat.getSiteId();

                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sid = sitesInfoList.get(i).getSiteId();
                            Helper.getInstance().LogDetails("updateUserChatEnded", chat.getAgentId()+" "+ chat.getSiteId()+" "+sid);

                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {

                                Helper.getInstance().LogDetails("updateUserChatEnded", "site matched"+ chat.getAgentId()+" "+ chat.getSiteId()+" "+sid+" "+ chat.getChatId());
                                List<ActiveChat> activeChatList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatList != null && activeChatList.size() > 0) {
                                    Helper.getInstance().LogDetails("updateUserChatEnded","size "+activeChatList.size()+"");
                                    for (int j = 0; j < activeChatList.size(); j++) {

                                        if (activeChatList.get(j).getChatId() != null && !activeChatList.get(j).getChatId().isEmpty() && chat.getChatId() != null && !chat.getChatId().trim().isEmpty()) {

                                            Helper.getInstance().LogDetails("updateUserChatEnded ","chat_id "+ chat.getChatId()+" "+activeChatList.get(j).getChatId());
                                            if (chat.getChatId().equals(activeChatList.get(j).getChatId())) {



                                                Helper.getInstance().LogDetails("updateUserChatEnded", "check " + chat.getChatId() + " " + activeChatList.get(j).getChatId());

                                                if (sitesInfoList != null && i < sitesInfoList.size()) {
                                                    if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                                                        sitesInfoList.get(i).getActiveChats().remove(j);
                                                    }
                                                }
                                                saveSiteData();
                                                if (current_site_id != null && siteId.equals(current_site_id)) {
                                                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                                        for (int k = 0; k < activeChatArrayList.size(); k++) {
                                                            if (chat.getChatId().equals(activeChatArrayList.get(k).getChatId())) {
                                                                Helper.getInstance().LogDetails("updateUserChatEnded", "check local" + chat.getChatId()+ " " + activeChatArrayList.get(k).getChatId());
                                                                 activeChatArrayList.remove(k);

                                                                 break;

                                                            }
                                                        }

                                                    }

                                                }
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        topAdapter.notifyDataSetChanged();

                                                    }
                                                });


                                                    Helper.getInstance().LogDetails("updateUserChatEnded removeChat","called "+isBack);

                                                    if(activeChat.getChatId()!=null && chat.getChatId()!=null && activeChat.getChatId().equals(chat.getChatId())){
                                                        if(activeChatArrayList!=null && activeChatArrayList.size()>0){

                                                            Helper.getInstance().LogDetails("updateUserChatEnded removeChat","called updateChatList"+isBack);
                                                            updateChatList(activeChatArrayList.get(0),0);
                                                        }
                                                        else
                                                        {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (noDataImage != null) {
                                                                            /*if(list!=null && list.size()>0){
                                                                                list.clear();
                                                                                if(adapter!=null)
                                                                                {
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                            }*/

                                                                        noDataImage.setVisibility(View.VISIBLE);
                                                                        if (chatRecyclerView != null) {
                                                                            chatRecyclerView.setVisibility(View.GONE);
                                                                        }
                                                                        if (usersLayout != null) {
                                                                            usersLayout.setVisibility(View.GONE);


                                                                        }
                                                                        if (bottomLayout != null) {
                                                                            bottomLayout.setVisibility(View.GONE);
                                                                        }

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }





                                                break;
                                            }

                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            }
            else
            {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateNewOnlineUser(JSONObject jsonObject) {
        try{
        Helper.getInstance().LogDetails("emitChatEndedAgent updateNewOnlineUser ChatActivity", "called" + jsonObject.toString());
        boolean isPresent = false;
        if (jsonObject != null && isSelf) {
            String agentId = jsonObject.optString("agent_id");
            if (agentId != null && user_id != null && agentId.equals(user_id)) {
                int position = -1;

                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    String chatid, tm_visitor_id, site_id;
                    chatid = jsonObject.optString("chat_id");
                    tm_visitor_id = jsonObject.optString("tm_visitor_id");
                    site_id = jsonObject.optString("site_id");
                    //  boolean isPresent= checkIsUserExists(jsonObject.optString("chat_id"),jsonObject.optString("tm_visitor_id"),jsonObject.optString("site_id"));
                    Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity before", "called" + isPresent  +chatid+" "+tm_visitor_id+" "+site_id+" "+current_site_id);
                    List<ActiveChat> activeChatList = new ArrayList<>();
                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {
                            String sid = sitesInfoList.get(i).getSiteId();
                            if (site_id != null && !site_id.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && site_id.equals(sid)) {
                                activeChatList = sitesInfoList.get(i).getActiveChats();
                                position = i;
                                break;
                            }
                        }
                    }
                    if (chatid != null && !chatid.trim().isEmpty()) {
                        if (activeChatList != null && activeChatList.size() > 0) {

                            for (int i = 0; i < activeChatList.size(); i++) {
                                if (activeChatList.get(i).getTmVisitorId() != null && !activeChatList.get(i).getTmVisitorId().trim().isEmpty() && tm_visitor_id.equals(activeChatList.get(i).getTmVisitorId())) {
                                    Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity", "called" + tm_visitor_id + " " + activeChatList.get(i).getTmVisitorId());
                                    isPresent = true;
                                    break;
                                }
                            }

                        } else {
                            isPresent = false;
                        }

                    }

                    if (!isPresent) {
                        Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity after", "called" + isPresent  +chatid+" "+tm_visitor_id+" "+site_id+" "+current_site_id);
                        final ActiveChat activeChat1 = setActiveUserData(jsonObject);
                        if (activeChat1 != null && position != -1) {

                            if (conversationTable == null) {
                                conversationTable = new ConversationTable(ChatActivity.this);
                            }


                            if (sitesInfoList != null && sitesInfoList.get(position).getActiveChats() != null) {
                                sitesInfoList.get(position).getActiveChats().add(0, activeChat1);
                            } else {
                                List<ActiveChat> l = new ArrayList<>();
                                activeChatList.add(0, activeChat1);
                                sitesInfoList.get(position).setActiveChats(l);

                            }

                            if (current_site_id != null && activeChat1.getSiteId() != null && current_site_id.equals(activeChat1.getSiteId())) {
                             Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity  if",current_site_id+" "+activeChat1.getSiteId());

                             if(activeChatPosition==0){
                                 activeChatPosition=1;
                             }
                                if (activeChatArrayList == null) {
                                    activeChatArrayList = new ArrayList<>();
                                }
                                if (activeChatArrayList != null ) {
                                    Helper.getInstance().LogDetails("updateNewOnlineUser before add","size"+activeChatArrayList.size() +isTabChanged);
                                    activeChatArrayList.add(0, activeChat1);
                                    Helper.getInstance().LogDetails("updateNewOnlineUser before remove","size"+activeChatArrayList.size());
                                    removeDuplicates();

                                    Helper.getInstance().LogDetails("updateNewOnlineUser after remove","size"+activeChatArrayList.size());
                                    if(isTabChanged){

                                  /*List<ActiveChat> activeChatArrayList = new ArrayList<>();
                                        List<ActiveChat> temp = sitesInfoList.get(position).getActiveChats();
                                        topAdapter.setList(temp);*/

                                        topAdapter.setList(activeChatArrayList);
                                    }

                                    Helper.getInstance().LogDetails("updateNewOnlineUser after setlist","size"+activeChatArrayList.size());
                                }
                               /* if (activeChatArrayList != null && activeChatArrayList.size() == 1) {
                                    Helper.getInstance().LogDetails("testing===", "called" + activeChatArrayList.get(0).getGuestName() + " " + activeChatArrayList.size());
                                    updateChatList(activeChat1, 0);
                                    List<ActiveChat> temp = sitesInfoList.get(position).getActiveChats();
                                    topAdapter.setList(temp);
                                    if(isTabChanged){
                                        topAdapter.setList(activeChatArrayList);
                                    }

                                }*/

                                if (activeChatArrayList != null &&  activeChatArrayList.size() == 1) {
                                    Helper.getInstance().LogDetails("updateNewOnlineUser =====", "called" + activeChatArrayList.get(0).getGuestName() + " " + activeChatArrayList.size());
                                    updateChatList(activeChat1, 0);


                                }

                            }
                            else
                            {
                                Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity  else",current_site_id+" "+activeChat1.getSiteId());
                            }



                            runOnUiThread(new Runnable() {
                                public void run() {


                                    if (topAdapter != null) {
                                        topAdapter.notifyDataSetChanged();
                                    }
                                    if (noDataImage != null) {
                                        if (current_site_id != null && activeChat1.getSiteId() != null && current_site_id.equals(activeChat1.getSiteId())) {
                                            if (noDataImage.getVisibility() == View.VISIBLE) {

                                                noDataImage.setVisibility(View.GONE);
                                                if (chatRecyclerView != null) {
                                                    chatRecyclerView.setVisibility(View.VISIBLE);
                                                }
                                                if (usersLayout != null) {
                                                    usersLayout.setVisibility(View.VISIBLE);


                                                }
                                                if (bottomLayout != null) {
                                                    bottomLayout.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }

                                    }


                                }
                            });

                            saveSiteData();

                        }
                    }
                    else
                    {
                        Helper.getInstance().LogDetails("updateNewOnlineUser","present "+isPresent);
                    }
                }

            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getAgentChats(JSONArray jsonArray)
    {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(j);
                    boolean isPresent = false;
                    if (jsonObject != null ) {
                        String agentId = jsonObject.optString("agent_id");
                        if (agentId != null && user_id != null && agentId.equals(user_id)) {
                            int position = -1;

                            if (sitesInfoList != null && sitesInfoList.size() > 0) {

                                String chatid, tm_visitor_id, site_id,chat_status;
                                chatid = jsonObject.optString("chat_id");
                                tm_visitor_id = jsonObject.optString("tm_visitor_id");
                                site_id = jsonObject.optString("site_id");
                                chat_status = jsonObject.optString("chat_status");



                                //  boolean isPresent= checkIsUserExists(jsonObject.optString("chat_id"),jsonObject.optString("tm_visitor_id"),jsonObject.optString("site_id"));
                                Helper.getInstance().LogDetails("getAgentChats ChatActivity before", "called" + isPresent  +chatid+" "+tm_visitor_id+" "+site_id+" "+current_site_id);
                                List<ActiveChat> activeChatList = new ArrayList<>();
                                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                                    for (int i = 0; i < sitesInfoList.size(); i++) {
                                        String sid = sitesInfoList.get(i).getSiteId();
                                        if (site_id != null && !site_id.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && site_id.equals(sid)) {
                                            activeChatList = sitesInfoList.get(i).getActiveChats();
                                            position = i;
                                            break;
                                        }
                                    }
                                }
                                if (chatid != null && !chatid.trim().isEmpty()) {
                                    if (activeChatList != null && activeChatList.size() > 0) {

                                        for (int i = 0; i < activeChatList.size(); i++) {
                                            if (activeChatList.get(i).getTmVisitorId() != null && !activeChatList.get(i).getTmVisitorId().trim().isEmpty() && tm_visitor_id.equals(activeChatList.get(i).getTmVisitorId())) {
                                                Helper.getInstance().LogDetails("getAgentChats ChatActivity", "called" + tm_visitor_id + " " + activeChatList.get(i).getTmVisitorId());
                                                isPresent = true;
                                                if(chat_status!=null && !chat_status.trim().isEmpty() && chat_status.equals("2"))
                                                {
                                                    activeChatList.get(i).setChatStatus("2");
                                                }
                                                break;
                                            }
                                        }

                                    } else {
                                        isPresent = false;
                                    }

                                }
                                boolean isActiveChat=false;
                                if (activeChat != null) {

                                    if (jsonObject.optString("chat_id").equals(activeChat.getChatId())) {
                                        if(chat_status!=null && !chat_status.trim().isEmpty() && chat_status.equals("2")){
                                            isActiveChat = true;
                                        }

                                    }


                                }

                                if (isActiveChat) {

                                    Helper.getInstance().LogDetails("getAgentChats active chat update","called "+false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            send_icon.setVisibility(View.GONE);
                                            chat_edit_txt.setHint("You can't send messages");
                                            chat_edit_txt.setText("");
                                            chat_edit_txt.setEnabled(false);
                                            Helper.getInstance().LogDetails("userEndedChatLayout","trigger 4");
                                            userEndedChatLayout.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    // getFeedBackCategory(false,activeChat);

                                }

                                if (!isPresent) {
                                    Helper.getInstance().LogDetails("getAgentChats ChatActivity after", "called" + isPresent  +chatid+" "+tm_visitor_id+" "+site_id+" "+current_site_id);
                                    final ActiveChat activeChat1 = setActiveUserData(jsonObject);
                                    if (activeChat1 != null && position != -1) {

                                        if (conversationTable == null) {
                                            conversationTable = new ConversationTable(ChatActivity.this);
                                        }


                                        if (sitesInfoList != null && sitesInfoList.get(position).getActiveChats() != null) {
                                            sitesInfoList.get(position).getActiveChats().add(0, activeChat1);
                                        } else {
                                            List<ActiveChat> l = new ArrayList<>();
                                            activeChatList.add(0, activeChat1);
                                            sitesInfoList.get(position).setActiveChats(l);

                                        }

                                        if (current_site_id != null && activeChat1.getSiteId() != null && current_site_id.equals(activeChat1.getSiteId())) {
                                            Helper.getInstance().LogDetails("getAgentChats ChatActivity  if",current_site_id+" "+activeChat1.getSiteId());

                                            if(activeChatPosition==-1){
                                                if(list!=null && list.size()>0){
                                                                                list.clear();
                                                                                if(adapter!=null)
                                                                                {
                                                                                    runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            adapter.notifyDataSetChanged();
                                                                                        }
                                                                                    });

                                                                                }
                                                                            }
                                            }
                                            if(activeChatPosition==0){
                                                activeChatPosition=1;
                                            }

                                            if (activeChatArrayList == null) {
                                                activeChatArrayList = new ArrayList<>();
                                            }
                                            if (activeChatArrayList != null ) {
                                                Helper.getInstance().LogDetails("getAgentChats before add","size"+activeChatArrayList.size() +isTabChanged);
                                                activeChatArrayList.add(0, activeChat1);

                                                Helper.getInstance().LogDetails("getAgentChats before remove","size"+activeChatArrayList.size());
                                                removeDuplicates();

                                                Helper.getInstance().LogDetails("getAgentChats after remove","size"+activeChatArrayList.size());
                                                if(isTabChanged){

                                  /*List<ActiveChat> activeChatArrayList = new ArrayList<>();
                                        List<ActiveChat> temp = sitesInfoList.get(position).getActiveChats();
                                        topAdapter.setList(temp);*/

                                                    topAdapter.setList(activeChatArrayList);
                                                }

                                                Helper.getInstance().LogDetails("getAgentChats after setlist","size"+activeChatArrayList.size());
                                            }
                               /* if (activeChatArrayList != null && activeChatArrayList.size() == 1) {
                                    Helper.getInstance().LogDetails("testing===", "called" + activeChatArrayList.get(0).getGuestName() + " " + activeChatArrayList.size());
                                    updateChatList(activeChat1, 0);
                                    List<ActiveChat> temp = sitesInfoList.get(position).getActiveChats();
                                    topAdapter.setList(temp);
                                    if(isTabChanged){
                                        topAdapter.setList(activeChatArrayList);
                                    }

                                }*/

                                            if (activeChatArrayList != null &&  activeChatArrayList.size() == 1) {
                                                Helper.getInstance().LogDetails("getAgentChats =====", "called" + activeChatArrayList.get(0).getGuestName() + " " + activeChatArrayList.size());

                                                updateChatList(activeChat1, 0);

                                            }

                                        }
                                        else
                                        {
                                            Helper.getInstance().LogDetails("updateNewOnlineUser ChatActivity  else",current_site_id+" "+activeChat1.getSiteId());
                                        }



                                        runOnUiThread(new Runnable() {
                                            public void run() {


                                                if (topAdapter != null) {
                                                    topAdapter.notifyDataSetChanged();
                                                }
                                                if (noDataImage != null) {
                                                    if (current_site_id != null && activeChat1.getSiteId() != null && current_site_id.equals(activeChat1.getSiteId())) {
                                                        if (noDataImage.getVisibility() == View.VISIBLE) {

                                                            noDataImage.setVisibility(View.GONE);
                                                            if (chatRecyclerView != null) {
                                                                chatRecyclerView.setVisibility(View.VISIBLE);
                                                            }
                                                            if (usersLayout != null) {
                                                                usersLayout.setVisibility(View.VISIBLE);


                                                            }
                                                            if (bottomLayout != null) {
                                                                bottomLayout.setVisibility(View.VISIBLE);
                                                            }

                                                        }
                                                    }

                                                }


                                            }
                                        });

                                        saveSiteData();

                                    }
                                }
                                else
                                {
                                    Helper.getInstance().LogDetails("getAgentChats","present "+isPresent);
                                }
                            }

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private ActiveChat setActiveUserData(JSONObject jsonObject) {

        ActiveChat activeChat = null;

        try{
        if (jsonObject != null) {

            activeChat = new ActiveChat();
            activeChat.setChatId(jsonObject.optString("chat_id"));
            activeChat.setVisitorOs(jsonObject.optString("visitor_os"));
            activeChat.setVisitorUrl(jsonObject.optString("visitor_url"));
            activeChat.setVisitorIp(jsonObject.optString("visitor_ip"));
            activeChat.setVisitorBrowser(jsonObject.optString("visitor_browser"));
            activeChat.setAgentId(jsonObject.optString("agent_id"));
            activeChat.setChatReferenceId(jsonObject.optString("chat_reference_id"));
            activeChat.setLocation(jsonObject.optString("location"));
            activeChat.setGuestName(jsonObject.optString("guest_name"));
            activeChat.setVisitorId(jsonObject.optString("visitor_id"));
            activeChat.setAccountId(jsonObject.optString("account_id"));
            activeChat.setSiteId(jsonObject.optString("site_id"));
            activeChat.setEmail(jsonObject.optString("email"));
            activeChat.setMobile(jsonObject.optString("mobile"));
            activeChat.setTmVisitorId(jsonObject.optString("tm_visitor_id"));
            activeChat.setVisitCount(jsonObject.optString("visit_count"));
            activeChat.setTrack_code(jsonObject.optString("track_code"));
            activeChat.setChatStatus(jsonObject.optString("chat_status"));

        }
        }catch (Exception e){
            e.printStackTrace();
        }
        return activeChat;
    }

    private boolean checkIsUserExists(String chat_id, String tm_visitor_id) {
        boolean isPresent = false;
        if (chat_id != null && !chat_id.isEmpty()) {
            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {

                for (int i = 0; i < activeChatArrayList.size(); i++) {
                    if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().trim().isEmpty() && tm_visitor_id.equals(activeChatArrayList.get(i).getTmVisitorId())) {
                        Helper.getInstance().LogDetails("updateNewOnlineUser", "called" + tm_visitor_id + " " + activeChatArrayList.get(i).getTmVisitorId());
                        isPresent = true;
                        break;
                    }
                }
            } else {
                isPresent = false;
            }

        }
        return isPresent;
    }

    public void sendCannedMessage(String text) {
        try{
        closeMoreOtionsDialog();
        if(cancelCannedLayout!=null)
        {
            cancelCannedLayout.performClick();
        }

        message = text;
        if(!isReplyMessage)
        {
            sendMessage();
        }
        else
        {
            sendReplyMessage();
        }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void clearSmartReplies(){
        if(smartRepliesList!=null && smartRepliesList.size()>0){
            smartRepliesList.clear();
            smartRepliesLayout.setVisibility(View.GONE);
            smartRepliesMessageAdapter.notifyDataSetChanged();
        }
    }

    public void sendSmartRepliesMessage(String text) {
        try{

            message = text;
            if(!isReplyMessage)
            {
                sendMessage();
            }
            else
            {
                sendReplyMessage();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendLinkMessage(String text) {
        try{
       closeMoreOtionsDialog();
        cancelCollateralLayout.performClick();
        message = text;
        if(!isReplyMessage)
        {
            sendMessage();
        }
        else
        {
            sendReplyMessage();
        }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendCollateral(String path, String attachMentName, String attachMentExtension) {
        try{
        closeMoreOtionsDialog();
        cancelCollateralLayout.performClick();
        if(!isReplyMessage)
        {
            sendAttachMentMessage(path, attachMentName, attachMentExtension, "");

        }
        else
        {
            sendReplyAttachMentMessage(path, attachMentName, attachMentExtension, "");
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendAttachMent(String path, String attachMentName, String attachMentExtension, String devicePath) {
        closeMoreOtionsDialog();
        if(!isReplyMessage)
        {
            sendAttachMentMessage(path, attachMentName, attachMentExtension, devicePath);
        }
        else
        {
            sendReplyAttachMentMessage(path, attachMentName, attachMentExtension, devicePath);

        }

    }

    private void closeMoreOtionsDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(moreOptionsDialog!=null){
                    moreOptionsDialog.cancel();
                }
            }
        });

    }



    private void closeMoreOptionsDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(moreOptionsDialog!=null){
                    moreOptionsDialog.cancel();
                }
            }
        });

    }

    private void confirmNumber(){
        try {
           // var data = {request: request, type: type, value: value};

            if(contact_number_tv!=null && contact_number_tv.getText()!=null && !contact_number_tv.getText().toString().trim().isEmpty() && contact_number_tv.getText().toString().trim().length()>1) {

                JSONObject data = new JSONObject();
                data.put("request", 1);
                data.put("type", 1);
                data.put("value", contact_number_tv.getText().toString());

                JSONObject object = new JSONObject();

                object.put("site_token", siteToken);
                object.put("reference", conversation_reference_id);
                object.put("data", data);


                Helper.getInstance().LogDetails("confirmNumber", "called " + object.toString());
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("confirmNumber", "socket called " );
                    mSocket.emit(SocketConstants.AGENT_REQUEST, object);
                    Toast.makeText(ChatActivity.this,"Request sent",Toast.LENGTH_LONG).show();

                }
            }
            else
            {
                Toast.makeText(ChatActivity.this,"Nothing to confirm",Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmEmail(){
        try {
            // var data = {request: request, type: type, value: value};

            if(contact_email_tv!=null && contact_email_tv.getText()!=null && !contact_email_tv.getText().toString().trim().isEmpty() && contact_email_tv.getText().toString().trim().length()>1) {

                JSONObject data = new JSONObject();
                data.put("request", 2);
                data.put("type", 1);
                data.put("value", contact_email_tv.getText().toString());

                JSONObject object = new JSONObject();

                object.put("site_token", siteToken);
                object.put("reference", conversation_reference_id);
                object.put("data", data);


                Helper.getInstance().LogDetails("confirmEmail", "called " + object.toString());
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("confirmEmail", "socket called " );
                    mSocket.emit(SocketConstants.AGENT_REQUEST, object);
                    Toast.makeText(ChatActivity.this,"Request sent",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(ChatActivity.this,"Nothing to confirm",Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void requestNumber(){
        try {
            // var data = {request: request, type: type, value: value};

                JSONObject data = new JSONObject();
                data.put("request", 1);
                data.put("type", 0);
                data.put("value", "");

                JSONObject object = new JSONObject();

                object.put("site_token", siteToken);
                object.put("reference", conversation_reference_id);
                object.put("data", data);


                Helper.getInstance().LogDetails("requestNumber", "called " + object.toString());
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("requestNumber", "socket called " );
                    mSocket.emit(SocketConstants.AGENT_REQUEST, object);
                    Toast.makeText(ChatActivity.this,"Request sent",Toast.LENGTH_LONG).show();

                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestEmail(){
        try {
            // var data = {request: request, type: type, value: value};

            JSONObject data = new JSONObject();
            data.put("request", 2);
            data.put("type", 0);
            data.put("value", "");

            JSONObject object = new JSONObject();

            object.put("site_token", siteToken);
            object.put("reference", conversation_reference_id);
            object.put("data", data);


            Helper.getInstance().LogDetails("requestEmail", "called " + object.toString());
            if (mSocket != null && mSocket.connected()) {
                Helper.getInstance().LogDetails("requestEmail", "socket called " );
                mSocket.emit(SocketConstants.AGENT_REQUEST, object);
                Toast.makeText(ChatActivity.this,"Request sent",Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyNumer(){
        if(contact_number_tv!=null && contact_number_tv.getText()!=null && !contact_number_tv.getText().toString().trim().isEmpty() && contact_number_tv.getText().toString().trim().length()>1)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", contact_number_tv.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ChatActivity.this,"Copied",Toast.LENGTH_LONG).show();
            Helper.getInstance().LogDetails("copyNumer","called "+clipboard.getPrimaryClip()+"    "+contact_number_tv.getText().toString());
        }
        else{
            Toast.makeText(ChatActivity.this,"Nothing to copy",Toast.LENGTH_LONG).show();
        }

    }

    private void copyEmail(){
        if(contact_email_tv!=null && contact_email_tv.getText()!=null && !contact_email_tv.getText().toString().trim().isEmpty() && contact_email_tv.getText().toString().trim().length()>1) {

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", contact_email_tv.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ChatActivity.this, "Copied", Toast.LENGTH_LONG).show();
            Helper.getInstance().LogDetails("copyEmail", "called " + clipboard.getPrimaryClip() + "    " + contact_email_tv.getText().toString());
        }
        else
        {
            Toast.makeText(ChatActivity.this,"Nothing to copy",Toast.LENGTH_LONG).show();
        }
        }

    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        try{
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            requestAppPermissions(Values.Permissions.STORAGE_PERMISSIONS_LIST, Values.Permissions.STORAGE_PERMISSION);
            return false;
        } else {
            return true;
        }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showChatEndConfirmationDialog() {
        try{
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Are you sure you want to end this chat?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //emitChatEndedAgent();
                        Helper.getInstance().LogDetails("FeedBackDialogBox showChatEndConfirmationDialog","called "+true);
                        if(categoryArrayList==null || categoryArrayList.size()==0)
                        categoryArrayList.addAll(categoriesTable.getCategoriesList());
                        if(categoryArrayList!=null && categoryArrayList.size()>0)
                            FeedBackDialogBox(true,activeChat);
                        else
                        getFeedBackCategory(true,activeChat);

                       /* if(categoryArrayList==null || categoryArrayList.size()==0){
                            getFeedBackCategory(true);
                        }
                        else
                        {
                            FeedBackDialogBox(true);
                        }
                        */


                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void emitChatEndedAgent() {

        try {
            JSONObject object = new JSONObject();
            object.put("chat_id", chat_id);
            object.put("site_token", siteToken);
            object.put("refance", conversation_reference_id);
            object.put("agent_id", agent_id);
            object.put("site_id", current_site_id);

            Helper.getInstance().LogDetails("emitChatEndedAgent", "called " );
            if (mSocket != null && mSocket.connected()) {
                Helper.getInstance().LogDetails("emitChatEndedAgent", "called " + object.toString()+" "+activeChat.getGuestName());
                mSocket.emit(SocketConstants.CHAT_ENDED_AGENT, object);
               // removeUserFromActiveUserList(chat_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void emitAgentTyping() {

        try {
            JSONObject object = new JSONObject();
          //  object.put("chat_id", chat_id);
            object.put("site_token", siteToken);
            object.put("reference", conversation_reference_id);
           // object.put("agent_id", agent_id);
           // object.put("site_id", current_site_id);

            //site_token: TOKENS[siteId],
            //reference: ref,

            Helper.getInstance().LogDetails("emitAgentTyping", "called " );
            if (mSocket != null && mSocket.connected()) {
                Helper.getInstance().LogDetails("emitAgentTyping", "called " + object.toString()+" "+activeChat.getGuestName());
                mSocket.emit(SocketConstants.AGENT_TYPING, object);
                // removeUserFromActiveUserList(chat_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeUserFromActiveUserList(String chat_id) {
        try{
        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
            for (int i = 0; i < activeChatArrayList.size(); i++) {
                if (activeChatArrayList.get(i).getChatId() != null && !activeChatArrayList.get(i).getChatId().isEmpty() && chat_id != null && !chat_id.isEmpty()) {
                    if (chat_id.equals(activeChatArrayList.get(i).getChatId())) {
                        String siteId = activeChatArrayList.get(i).getSiteId();
                        activeChatArrayList.remove(i);
                        if (sitesInfoList != null && sitesInfoList.size() > 0) {
                            for (int j = 0; j < sitesInfoList.size(); j++) {

                                String sid = sitesInfoList.get(j).getSiteId();

                                if (siteId != null && sid != null && siteId.equals(sid)) {

                                    if (sitesInfoList.get(j).getActiveChats() != null && sitesInfoList.get(j).getActiveChats().size() > 0) {
                                        for (int k = 0; k < sitesInfoList.get(j).getActiveChats().size(); k++) {
                                            if (sitesInfoList.get(j).getActiveChats().get(k).getChatId() != null && sitesInfoList.get(j).getActiveChats().get(k).getChatId().equals(chat_id)) {
                                                sitesInfoList.get(j).getActiveChats().remove(k);
                                                break;
                                            }
                                        }
                                    }


                                }


                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topAdapter.notifyDataSetChanged();
                            }
                        });
                        saveSiteData();


                    }
                }
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showContactInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Helper.getInstance().closeKeyboard1(ChatActivity.this, ChatActivity.this);
                isContactCliked = true;
                chatRecyclerView.setVisibility(View.GONE);
                noDataImage.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                contact_layout.setVisibility(View.VISIBLE);

                if(activeChat!=null){
                    if (activeChat.getEmail() != null && !activeChat.getEmail().trim().isEmpty()) {
                        if(!activeChat.getEmail().contains("@click2magic.com"))
                        {
                            contact_email_tv.setText(activeChat.getEmail());
                        }
                        else
                        {
                           // contact_email_tv.setText(activeChat.getEmail());
                            contact_email_tv.setText("-");
                        }

                    } else {
                        contact_email_tv.setText("-");
                    }
                    if (activeChat.getMobile() != null && !activeChat.getMobile().trim().isEmpty()) {
                        contact_number_tv.setText(activeChat.getMobile());
                    } else {
                        contact_number_tv.setText("-");
                    }

                    if (activeChat.getUser_name() != null && !activeChat.getUser_name().trim().isEmpty()) {
                        contact_name_tv.setText(activeChat.getUser_name());
                    } else {
                        contact_name_tv.setText("-");
                    }

                    if (activeChat.getGuestName() != null && !activeChat.getGuestName().trim().isEmpty()) {
                        contact_name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
                    } else {
                        contact_name_tv.setText("-");
                    }
                }
            }
        });
    }

    private void setVisitPagesLayyou(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(visitPages!=null && visitPages.size()>0){
                    visitPagesLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    visitPagesLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showCustomerInfo() {



        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    if(visitPages!=null && visitPages.size()>0){
                        visitPagesLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        visitPagesLayout.setVisibility(View.GONE);
                    }

                    Helper.getInstance().closeKeyboard1(ChatActivity.this, ChatActivity.this);

                    isInfoClicked = true;
                  //  chat_layout.setVisibility(View.GONE);
                    chatRecyclerView.setVisibility(View.GONE);
                    noDataImage.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                    info_layout.setVisibility(View.VISIBLE);


                    if (activeChat != null) {
                        if (activeChat.getGuestName() != null && !activeChat.getGuestName().trim().isEmpty()) {
                            customer_name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
                        } else {
                            customer_name_tv.setText("-");
                        }
                        if (activeChat.getVisitorBrowser() != null && !activeChat.getVisitorBrowser().trim().isEmpty()) {
                            browser_name_tv.setText(activeChat.getVisitorBrowser());
                        } else {
                            browser_name_tv.setText("-");
                        }
                        if (user_name != null && !user_name.trim().isEmpty()) {
                            info_agent_name_tv.setText(Helper.getInstance().capitalize(user_name));
                        } else {
                            info_agent_name_tv.setText("-");
                        }
                        if (activeChat.getVisitorOs() != null && !activeChat.getVisitorOs().trim().isEmpty()) {
                            os_name_tv.setText(activeChat.getVisitorOs());
                        } else {
                            os_name_tv.setText("-");
                        }
                        if (activeChat.getEmail() != null && !activeChat.getEmail().trim().isEmpty()) {
                            if(!activeChat.getEmail().contains("@click2magic.com"))
                            {
                                email_tv.setText(activeChat.getEmail());
                            }
                            else
                            {
                                email_tv.setText("-");
                            }

                        } else {
                            email_tv.setText("-");
                        }
                        if (activeChat.getMobile() != null && !activeChat.getMobile().trim().isEmpty()) {
                            phone_number_tv.setText(activeChat.getMobile());
                        } else {
                            phone_number_tv.setText("-");
                        }

                        if (activeChat.getVisitorUrl() != null && !activeChat.getVisitorUrl().trim().isEmpty()) {
                            entry_url_tv.setText(activeChat.getVisitorUrl());
                        } else {
                            entry_url_tv.setText("-");
                        }
                        if (activeChat.getVisitCount() != null && !activeChat.getVisitCount().trim().isEmpty()) {
                            history_tv.setText(activeChat.getVisitCount() + " Visit");
                        } else {
                            history_tv.setText("-");
                        }
                        if (activeChat.getLocation() != null && !activeChat.getLocation().trim().isEmpty()) {
                            location_tv.setText(activeChat.getLocation());
                        } else {
                            location_tv.setText("-");
                        }
                        if (activeChat.getVisitorIp() != null && !activeChat.getVisitorIp().trim().isEmpty()) {
                            ip_tv.setText(activeChat.getVisitorIp());
                        } else {
                            ip_tv.setText("-");
                        }

                        //yyyy-MM-dd'T'HH:mm:ssZ
                        //2020-02-17T13:43:28.000Z
                        if (activeChat.getStartTime() != null && !activeChat.getStartTime().trim().isEmpty()) {
                            String startTime = activeChat.getStartTime().trim();

                            if (startTime != null && !startTime.trim().isEmpty() && !startTime.equals("0000-00-00 00:00:00")) {
                                String startDate = getDateStartTime(startTime);


                                if (startDate != null) {

                                    int hour = startHour;
                                    int min = startMin;

                                    if (START_AM_PM == 1) {

                                        if (hour == 0) {
                                            hour = 12;
                                        }
                                        if (hour < 10) {
                                            if (min < 10) {
                                                start_time_tv.setText(startDate + " 0" + hour + ":0" + min + " PM");
                                            } else {
                                                start_time_tv.setText(startDate + " 0" + hour + ":" + min + " PM");
                                            }

                                        } else {
                                            if (min < 10) {
                                                start_time_tv.setText(startDate + " " + hour + ":0" + min + " PM");
                                            } else {
                                                start_time_tv.setText(startDate + " " + hour + ":" + min + " PM");
                                            }

                                        }

                                    } else {
                                        if (hour == 0) {
                                            hour = 12;
                                        }
                                        if (hour < 10) {
                                            if (min < 10) {
                                                start_time_tv.setText(startDate + " 0" + hour + ":0" + min + " AM");
                                            } else {
                                                start_time_tv.setText(startDate + " 0" + hour + ":" + min + " AM");
                                            }

                                        } else {
                                            if (min < 10) {
                                                start_time_tv.setText(startDate + " " + hour + ":0" + min + " AM");
                                            } else {
                                                start_time_tv.setText(startDate + " " + hour + ":" + min + " AM");
                                            }

                                        }

                                    }

                                }

                            }
                        } else {
                            start_time_tv.setText("");
                        }



                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void showCustomerInfoDialog() {

        try {
            LinearLayout visitPagesLayout,phoneNumberLayout,emailLayout;
            ImageView info_close_icon,screenshotImage;

            TextView customer_name_tv,browser_name_tv,info_agent_name_tv,os_name_tv,email_tv,phone_number_tv,
                    entry_url_tv,history_tv,location_tv,ip_tv,start_time_tv;
            if(infoDialog==null)
            {
                infoDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);


                infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                infoDialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(infoDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                //  lp.windowAnimations = R.style.slide_left_right;
                infoDialog.getWindow().setAttributes(lp);
                infoDialog.setContentView(R.layout.customer_info_layout);




            }

            visitPagesLayout = infoDialog.findViewById(R.id.visitPagesLayout);
            phoneNumberLayout = infoDialog.findViewById(R.id.phoneNumberLayout);
            emailLayout = infoDialog.findViewById(R.id.emailLayout);
            customer_name_tv = infoDialog.findViewById(R.id.customer_name_tv);
            browser_name_tv = infoDialog.findViewById(R.id.browser_name_tv);
            info_agent_name_tv = infoDialog.findViewById(R.id.info_agent_name_tv);
            os_name_tv = infoDialog.findViewById(R.id.os_name_tv);
            email_tv = infoDialog.findViewById(R.id.email_tv);
            phone_number_tv = infoDialog.findViewById(R.id.phone_number_tv);
            entry_url_tv = infoDialog.findViewById(R.id.entry_url_tv);
            history_tv = infoDialog.findViewById(R.id.history_tv);
            location_tv = infoDialog.findViewById(R.id.location_tv);
            ip_tv = infoDialog.findViewById(R.id.ip_tv);
            start_time_tv = infoDialog.findViewById(R.id.start_time_tv);
            info_close_icon = infoDialog.findViewById(R.id.info_close_icon);
            screenshotImage = infoDialog.findViewById(R.id.screenshotImage);



            try {

                if(visitPages!=null && visitPages.size()>0){
                    visitPagesLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    visitPagesLayout.setVisibility(View.GONE);
                }

                Helper.getInstance().closeKeyboard1(ChatActivity.this, ChatActivity.this);

                isInfoClicked = true;



                if (activeChat != null) {
                    if (activeChat.getGuestName() != null && !activeChat.getGuestName().trim().isEmpty()) {
                        customer_name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
                    } else {
                        customer_name_tv.setText("-");
                    }
                    if (activeChat.getVisitorBrowser() != null && !activeChat.getVisitorBrowser().trim().isEmpty()) {
                        browser_name_tv.setText(activeChat.getVisitorBrowser());
                    } else {
                        browser_name_tv.setText("-");
                    }
                    if (activeChat.getGuestName() != null && !activeChat.getGuestName().trim().isEmpty()) {
                        info_agent_name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
                    } else {
                        info_agent_name_tv.setText("-");
                    }
                    if (activeChat.getVisitorOs() != null && !activeChat.getVisitorOs().trim().isEmpty()) {
                        os_name_tv.setText(activeChat.getVisitorOs());
                    } else {
                        os_name_tv.setText("-");
                    }
                    if (activeChat.getEmail() != null && !activeChat.getEmail().trim().isEmpty()) {
                        if(!activeChat.getEmail().contains("@click2magic.com"))
                        {
                            email_tv.setText(activeChat.getEmail());
                        }
                        else
                        {
                            email_tv.setText("-");
                        }

                    } else {
                        email_tv.setText("-");
                    }
                    if (activeChat.getMobile() != null && !activeChat.getMobile().trim().isEmpty()) {
                        phone_number_tv.setText(activeChat.getMobile());
                    } else {
                        phone_number_tv.setText("-");
                    }

                    if (activeChat.getVisitorUrl() != null && !activeChat.getVisitorUrl().trim().isEmpty()) {
                        entry_url_tv.setText(activeChat.getVisitorUrl());
                    } else {
                        entry_url_tv.setText("-");
                    }
                    if (activeChat.getVisitCount() != null && !activeChat.getVisitCount().trim().isEmpty()) {
                        history_tv.setText(activeChat.getVisitCount() + " Visit");
                    } else {
                        history_tv.setText("-");
                    }
                    if (activeChat.getLocation() != null && !activeChat.getLocation().trim().isEmpty()) {
                        location_tv.setText(activeChat.getLocation());
                    } else {
                        location_tv.setText("-");
                    }
                    if (activeChat.getVisitorIp() != null && !activeChat.getVisitorIp().trim().isEmpty()) {
                        ip_tv.setText(activeChat.getVisitorIp());
                    } else {
                        ip_tv.setText("-");
                    }

                    Helper.getInstance().LogDetails("showCustomerInfoDialog startTime ",activeChat.getStartTime()+" ");

                    if (activeChat.getStartTime() != null && !activeChat.getStartTime().trim().isEmpty()  && !activeChat.getStartTime().contains("Z")) {

                        String startTime = activeChat.getStartTime().trim();
                        if (startTime != null && !startTime.trim().isEmpty() && !startTime.equals("0000-00-00 00:00:00")) {
                            String startDate = getDateStartTime(startTime);


                            if (startDate != null) {

                                int hour = startHour;
                                int min = startMin;

                                if (START_AM_PM == 1) {

                                    if (hour == 0) {
                                        hour = 12;
                                    }
                                    if (hour < 10) {
                                        if (min < 10) {
                                            start_time_tv.setText(startDate + " 0" + hour + ":0" + min + " PM");
                                        } else {
                                            start_time_tv.setText(startDate + " 0" + hour + ":" + min + " PM");
                                        }

                                    } else {
                                        if (min < 10) {
                                            start_time_tv.setText(startDate + " " + hour + ":0" + min + " PM");
                                        } else {
                                            start_time_tv.setText(startDate + " " + hour + ":" + min + " PM");
                                        }

                                    }

                                } else {
                                    if (hour == 0) {
                                        hour = 12;
                                    }
                                    if (hour < 10) {
                                        if (min < 10) {
                                            start_time_tv.setText(startDate + " 0" + hour + ":0" + min + " AM");
                                        } else {
                                            start_time_tv.setText(startDate + " 0" + hour + ":" + min + " AM");
                                        }

                                    } else {
                                        if (min < 10) {
                                            start_time_tv.setText(startDate + " " + hour + ":0" + min + " AM");
                                        } else {
                                            start_time_tv.setText(startDate + " " + hour + ":" + min + " AM");
                                        }

                                    }

                                }

                            }

                        }
                    }
                    else   if(activeChat.getStartTime() != null && !activeChat.getStartTime().trim().isEmpty() && activeChat.getStartTime().contains("Z")){
                        Helper.getInstance().LogDetails("showCustomerInfoDialog startTime 1",activeChat.getStartTime()+" ");
                        String date=activeChat.getStartTime();

                            DateUtils dateUtils = new DateUtils(DateUtils.DATE, date, TimeZone.getTimeZone("UTC"));
                            String dateTime = dateUtils.getCustomFormat(DateUtils.START_DATE) ;
                            Helper.getInstance().LogDetails("showCustomerInfoDialog startTime  ",dateTime);
                            start_time_tv.setText(dateTime);


                    }else {
                        start_time_tv.setText("");
                    }





                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            info_close_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialog.cancel();
                    isInfoClicked = false;
                }
            });

            phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call();
                }
            });

            phone_number_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call();
                }
            });

            emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail();
                }
            });

            email_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail();
                }
            });

            screenshotImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    screenshotClicked=true;
                    if (verifyStoragePermissions(ChatActivity.this)) {
                        takeScreenshot();
                    }

                }
            });



            infoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                      isInfoClicked=false;

                }
            });

            infoDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTheMessageBoxSize(int line_count) {
        try {
            if (line_count > 2) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottom_lay_card.getLayoutParams();
                params.height = getResources().getDimensionPixelSize(R.dimen.groupProfilePic);
                bottom_lay_card.setLayoutParams(params);
            } else if (line_count > 1) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottom_lay_card.getLayoutParams();
                params.height = getResources().getDimensionPixelSize(R.dimen.galleryDocLinkItemheight);
                bottom_lay_card.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottom_lay_card.getLayoutParams();
                params.height = getResources().getDimensionPixelSize(R.dimen.chatbox_height);
                bottom_lay_card.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try{


            clearSmartReplies();

        Helper.getInstance().LogDetails("sendMessage", "called" + accessToken + " " + company_token + " " + tmUserId + " " + receiver_id);

        if(tmUserId!=null && receiver_id!=null && !tmUserId.isEmpty() && !receiver_id.isEmpty() )
        {

            ChatModel model = new ChatModel();
            model.setMessage_id(0);
            model.setSender_id(Integer.parseInt(tmUserId));
            model.setReceiver_id(Integer.parseInt(receiver_id));
            model.setMessage(message);
            model.setIs_group(0);
            model.setIs_received(0);
            model.setIs_sync(0);
            model.setAttachment("");
            model.setAttachment_extension("");
            model.setAttachment_name("");
            model.setAttachmentDevicePath("");
            model.setCaption("");
            model.setOriginal_message("");
            model.setIs_delivered(0);
            model.setIs_downloaded(0);
            model.setMessage_type(Values.MessageType.MESSAGE_TYPE_TEXT);
            long currentTime = System.currentTimeMillis();
            model.setReference_id(currentTime + userId);
            model.setConversation_reference_id(conversation_reference_id);
            model.setCreated_at(getCurrentDate());
            String referenceId = conversationTable.insertChat(model);
            model.setReference_id(referenceId);

            if (list != null && list.size() > 0) {

                if (list.get(0).getMessage_type() == Values.MessageType.MESSAGE_TYPE_TYPING) {
                    ChatModel chatModel = list.get(0);
                    list.remove(0);
                    list.add(0, model);
                    list.add(0, chatModel);

                } else {
                    list.add(0, model);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        if(adapter!=null)
                            adapter.notifyDataSetChanged();
                    }
                });
                scrollItem();
            } else {
                list.add(0, model);

                runOnUiThread(new Runnable() {
                    public void run() {
                        if(adapter!=null)
                            adapter.notifyDataSetChanged();
                    }
                });

                scrollItem();

            }

            send_icon.setClickable(true);
        }

        }catch (Exception e){
            Helper.getInstance().LogDetails("sendMessage","exception "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }


    private void sendReplyMessage() {
        try{

            clearSmartReplies();

            Helper.getInstance().LogDetails("sendMessage", "called" + accessToken + " " + company_token + " " + tmUserId + " " + receiver_id);

            if(tmUserId!=null && receiver_id!=null && !tmUserId.isEmpty() && !receiver_id.isEmpty() ) {


                isReplyMessage = false;
                reply_message_layout.setVisibility(View.GONE);


                ChatModel model = new ChatModel();
                model.setMessage_id(0);
                model.setSender_id(Integer.parseInt(tmUserId));
                model.setReceiver_id(Integer.parseInt(receiver_id));
                model.setMessage(message);
                model.setIs_group(0);
                model.setIs_received(0);
                model.setIs_sync(0);
                model.setIs_reply(1);
                model.setAttachment("");
                model.setAttachment_extension("");
                model.setAttachment_name("");
                model.setAttachmentDevicePath("");
                model.setCaption("");

                if (replyMessageObj != null) {
                    if (replyMessageObj.getMessage() != null) {
                        model.setOriginal_message(replyMessageObj.getMessage());
                    } else {
                        model.setOriginal_message("");
                    }
                    int senderId = replyMessageObj.getSender_id();
                    int messageId = replyMessageObj.getMessage_id();
                    if (tmUserId != null && !tmUserId.trim().isEmpty() && senderId == Integer.parseInt(tmUserId)) {

                        model.setName("Me");
                    } else {
                        if (receiver_name != null && !receiver_name.trim().isEmpty()) {
                            model.setName(Helper.getInstance().capitalize(receiver_name));
                        }

                    }

                    model.setOriginal_message_id(messageId);


                }


                model.setIs_delivered(0);
                model.setIs_downloaded(0);
                model.setMessage_type(Values.MessageType.MESSAGE_TYPE_TEXT);
                long currentTime = System.currentTimeMillis();
                model.setReference_id(currentTime + userId);
                model.setConversation_reference_id(conversation_reference_id);
                model.setCreated_at(getCurrentDate());
                String referenceId = conversationTable.insertChat(model);
                model.setReference_id(referenceId);

                if (list != null && list.size() > 0) {

                    if (list.get(0).getMessage_type() == Values.MessageType.MESSAGE_TYPE_TYPING) {
                        ChatModel chatModel = list.get(0);
                        list.remove(0);
                        list.add(0, model);
                        list.add(0, chatModel);

                    } else {
                        list.add(0, model);
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();

                        }
                    });
                    scrollItem();
                } else {
                    list.add(0, model);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();

                        }
                    });

                    scrollItem();

                }

                send_icon.setClickable(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendAttachMentMessage(String path, String attachMentName, String attachMentExtension, String devicePath) {
        try{

            if(activeChat!=null && activeChat.getChatStatus()!=null && activeChat.getChatStatus().equals("2"))
            {
                return;
            }


            clearSmartReplies();



        Helper.getInstance().LogDetails("sendAttachMentMessage", "called" + accessToken +" "+path +" "+devicePath);

        ChatModel model = new ChatModel();
        model.setMessage_id(0);
        model.setSender_id(Integer.parseInt(tmUserId));
        model.setReceiver_id(Integer.parseInt(receiver_id));
        model.setMessage("");
        model.setIs_group(0);
        model.setIs_received(0);
        model.setIs_sync(0);
        model.setAttachment(path);
        model.setAttachment_extension(attachMentExtension);
        model.setAttachment_name(attachMentName);
        model.setAttachmentDevicePath(devicePath);
        model.setCaption("");
        model.setOriginal_message("");
        model.setIs_delivered(0);
        model.setMessage_type(Values.MessageType.MESSAGE_TYPE_ATTACHMENT);
        if (devicePath != null && !devicePath.trim().isEmpty()) {
            model.setIs_downloaded(1);
            model.setAttachmentDownloaded(true);
        } else {
            model.setIs_downloaded(0);
            model.setAttachmentDownloaded(false);
        }
        long currentTime = System.currentTimeMillis();
        model.setReference_id(currentTime + userId);
        model.setConversation_reference_id(conversation_reference_id);
        model.setCreated_at(getCurrentDate());
        String referenceId = conversationTable.insertChat(model);
        //getHistory();
        model.setReference_id(referenceId);

        if (list != null && list.size() > 0) {

            if (list.get(0).getMessage_type() == Values.MessageType.MESSAGE_TYPE_TYPING) {
                Helper.getInstance().LogDetails("sendAttachMentMessage","MESSAGE_TYPE_TYPING");
                ChatModel chatModel = list.get(0);
                list.remove(0);
                list.add(0, model);
                list.add(0, chatModel);

            } else {
                Helper.getInstance().LogDetails("sendAttachMentMessage","NO MESSAGE_TYPE_TYPING");
                list.add(0, model);
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            scrollItem();
        } else {
            list.add(0, model);

            runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            scrollItem();

        }

        send_icon.setClickable(true);

        if(path==null || path.trim().isEmpty())
        {
            if(adapter!=null){

                adapter.uploadFile(0,AWS_KEY,AWS_SECRET_KEY,AWS_BASE_URL,AWS_REGION,AWS_BUCKET,s3Url);
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendReplyAttachMentMessage(String path, String attachMentName, String attachMentExtension, String devicePath) {
        try{
            isReplyMessage=false;
            reply_message_layout.setVisibility(View.GONE);

            clearSmartReplies();


            if(activeChat!=null && activeChat.getChatStatus()!=null && activeChat.getChatStatus().equals("2"))
            {
                return;
            }



            Helper.getInstance().LogDetails("sendAttachMentMessage", "called" + accessToken +" "+path +" "+devicePath);

            ChatModel model = new ChatModel();
            model.setMessage_id(0);
            model.setSender_id(Integer.parseInt(tmUserId));
            model.setReceiver_id(Integer.parseInt(receiver_id));
            model.setMessage("");
            model.setIs_group(0);
            model.setIs_received(0);
            model.setIs_sync(0);
            model.setIs_reply(1);
            model.setAttachment(path);
            model.setAttachment_extension(attachMentExtension);
            model.setAttachment_name(attachMentName);
            model.setAttachmentDevicePath(devicePath);
            model.setCaption("");
            model.setOriginal_message("");
            model.setIs_delivered(0);
            model.setMessage_type(Values.MessageType.MESSAGE_TYPE_ATTACHMENT);
            if (devicePath != null && !devicePath.trim().isEmpty()) {
                model.setIs_downloaded(1);
                model.setAttachmentDownloaded(true);
            } else {
                model.setIs_downloaded(0);
                model.setAttachmentDownloaded(false);
            }

            if(replyMessageObj!=null){
                if(replyMessageObj.getMessage()!=null){
                    model.setOriginal_message(replyMessageObj.getMessage());
                }else
                {
                    model.setOriginal_message("");
                }
                int senderId=replyMessageObj.getSender_id();
                int messageId=replyMessageObj.getMessage_id();
                if(tmUserId!=null && !tmUserId.trim().isEmpty() && senderId==Integer.parseInt(tmUserId))
                {

                    model.setName("Me");
                }
                else
                {
                    if(receiver_name!=null && !receiver_name.trim().isEmpty()){
                        model.setName(Helper.getInstance().capitalize(receiver_name));
                    }

                }

                model.setOriginal_message_id(messageId);
            }
            long currentTime = System.currentTimeMillis();
            model.setReference_id(currentTime + userId);
            model.setConversation_reference_id(conversation_reference_id);
            model.setCreated_at(getCurrentDate());
            String referenceId = conversationTable.insertChat(model);
            getHistory();
            model.setReference_id(referenceId);

            if (list != null && list.size() > 0) {

                if (list.get(0).getMessage_type() == Values.MessageType.MESSAGE_TYPE_TYPING) {
                    ChatModel chatModel = list.get(0);
                    list.remove(0);
                    list.add(0, model);
                    list.add(0, chatModel);

                } else {
                    list.add(0, model);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                scrollItem();
            } else {
                list.add(0, model);

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                scrollItem();

            }

            send_icon.setClickable(true);

            if(path==null || path.trim().isEmpty())
            {
                if(adapter!=null){

                    adapter.uploadFile(0,AWS_KEY,AWS_SECRET_KEY,AWS_BASE_URL,AWS_REGION,AWS_BUCKET,s3Url);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean isApiCalled=false;
    private void callGetConversationsApi() {

        Helper.getInstance().LogDetails("checkUserAvailable callGetConversationsApi", "called=====" + ApiEndPoint.TM_SERVER_SOCKET_TOKEN + " " + tmUserId + " " + socketId + " " + " " + accessToken + " " + receiver_id + " " + limit + " " + lastMessageId + " " + conversation_reference_id);

        try {
            limit = "50";


            if (apiService == null) {
                apiService = ApiClient.getClient().create(ApiInterface.class);
            }
            if(!isInfoClicked && !isContactCliked && !isRefresh){
               // isRefresh=false;
               // openProgess();
            }

            if(isRefresh){
                isRefresh=false;
            }


            Call<GetMessagesResponse> call = apiService.getConversation(ApiEndPoint.TM_SERVER_SOCKET_TOKEN, tmUserId, socketId, accessToken, receiver_id, limit, lastMessageId, 0, conversation_reference_id);
            call.enqueue(new Callback<GetMessagesResponse>() {
                @Override
                public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                    GetMessagesResponse apiResponse = response.body();


                    if (apiResponse != null) {

                        if (apiResponse.isSuccess()) {

                            Helper.getInstance().LogDetails("checkUserAvailable callGetConversationsApi ", "called " + true);

                            if (apiResponse.getMessages() != null && apiResponse.getMessages().size() > 0) {

                                Helper.getInstance().LogDetails("checkUserAvailable callGetConversationsApi ", "size " + apiResponse.getMessages().size());
                                if (messageList != null && messageList.size() > 0) {
                                    messageList.clear();
                                }

                                messageList = apiResponse.getMessages();
                                if(messageList!=null && messageList.size()>0){
                                    for(int i=0;i<messageList.size();i++){
                                        Helper.getInstance().LogDetails("checkUserAvailable callGetConversationsApi msg",messageList.get(i).getMessage().toString());
                                    }
                                }

                                if (list != null && list.size() > 0) {
                                    list.clear();
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            if (adapter != null) {
                                                adapter.notifyDataSetChanged();
                                            }

                                        }
                                    });
                                }
                                updateChatData(messageList);
                                checkLocalMessages();


                            }

                        } else {

                            Helper.getInstance().LogDetails("callGetConversationsApi ", "called " + false);
                        }
                    }
                    closeProgress();
                    isApiCalled=false;
                    progressBar.setVisibility(View.INVISIBLE);
                }


                @Override
                public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
                    closeProgress();
                    isApiCalled=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Helper.getInstance().LogDetails("callGetConversationsApi ", "onFailure  " + t.getLocalizedMessage() + " " + t.getCause());

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();


        }
    }

    private void callGetConversationsApi1() {

        Helper.getInstance().LogDetails("callGetConversationsApi", "called" + ApiEndPoint.TM_SERVER_SOCKET_TOKEN + " " + tmUserId + " " + socketId + " " + " " + accessToken + " " + receiver_id + " " + limit + " " + lastMessageId + " " + conversation_reference_id);

        try {
            limit = "50";


            if (apiService == null) {
                apiService = ApiClient.getClient().create(ApiInterface.class);
            }

            if (!isRefresh) {
                isRefresh = false;
               // openProgess();

            }


            Call<GetMessagesResponse> call = apiService.getChatHistoty(ApiEndPoint.TM_SERVER_SOCKET_TOKEN, conversation_reference_id, limit);
            call.enqueue(new Callback<GetMessagesResponse>() {
                @Override
                public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                    GetMessagesResponse apiResponse = response.body();


                    if (apiResponse != null) {

                        if (apiResponse.isSuccess()) {

                            Helper.getInstance().LogDetails("callGetConversationsApi ", "called " + true);

                            if (apiResponse.getMessages() != null && apiResponse.getMessages().size() > 0) {

                                Helper.getInstance().LogDetails("callGetConversationsApi ", "size " + apiResponse.getMessages().size());
                                if (messageList != null && messageList.size() > 0) {
                                    messageList.clear();
                                }
                                messageList = apiResponse.getMessages();

                                if (list != null && list.size() > 0) {
                                    list.clear();
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            if (adapter != null) {
                                                adapter.notifyDataSetChanged();
                                            }

                                        }
                                    });
                                }
                                updateChatData(messageList);


                            } else {
                                closeProgress();
                            }

                        } else {
                            closeProgress();
                            Helper.getInstance().LogDetails("callGetConversationsApi ", "called " + false);
                        }
                    } else {
                        closeProgress();

                    }
                }


                @Override
                public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
                    closeProgress();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();


        }
    }

    private void callGetMoreConversationsApi() {

        try {
            String limit = "50";
            if (apiService == null) {
                apiService = ApiClient.getClient().create(ApiInterface.class);
            }
            Call<GetMessagesResponse> call = apiService.getConversation(tmToken, tmUserId, socketId, accessToken, receiver_id, limit, lastMessageId, 0, "");
            call.enqueue(new Callback<GetMessagesResponse>() {
                @Override
                public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                    GetMessagesResponse apiResponse = response.body();

                    if (apiResponse != null) {


                        if (apiResponse.isSuccess()) {


                            if (apiResponse.getMessages() != null && apiResponse.getMessages().size() > 0) {

                                messageList = apiResponse.getMessages();
                                updateMoreChatData(messageList);

                            }

                        }

                    }

                }

                @Override
                public void onFailure(Call<GetMessagesResponse> call, Throwable t) {

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMoreChatData(List<Message> messages) {

        try {
            if (messages != null && messages.size() > 0) {
                int size = messages.size();
                Message message = null;
                for (int i = 0; i < messages.size(); i++) {
                    message = messages.get(size - 1);
                    lastMessageId = String.valueOf(message.getMessage_id());
                    if (!lastMessageId.trim().isEmpty() && !oldLastMessageId.trim().isEmpty()) {
                        if (oldLastMessageId.equals(lastMessageId)) {
                            break;
                        }
                    }
                    oldLastMessageId = String.valueOf(message.getMessage_id());
                    message = messages.get(i);
                    ChatModel model = new ChatModel();
                    model.setMessage_id(message.getMessage_id());
                    model.setSender_id(message.getSender_id());
                    model.setReceiver_id(message.getReceiver_id());

                    model.setMessage(message.getMessage().toString());
                    model.setIs_group(0);
                    model.setIs_delivered(message.getIs_delivered());
                    model.setIs_read(message.getIs_read());
                    model.setConversation_reference_id(message.getConversation_reference_id());
                    model.setAttachment(message.getAttachment());
                    model.setAttachment_name(message.getAttachment_name());
                    model.setAttachment_extension(message.getAttachment_extension());
                    model.setCaption("");
                    model.setOriginal_message("");
                    model.setReference_id("");
                    model.setCreated_at(message.getCreated_at());

                    int messageId = model.getMessage_id();
                    if (list != null && list.size() > 0) {

                        boolean present = false;

                        for (int j = 0; j < list.size(); j++) {
                            int mid = list.get(j).getMessage_id();
                            if (mid == messageId) {
                                present = true;

                                break;

                            }
                        }
                        if (!present) {

                            list.add(model);
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChatData(List<Message> messages) {
        try {
            Helper.getInstance().LogDetails("checkUserAvailable updateChatData msg","called");


            if (messages != null && messages.size() > 0) {
                for (int i = 0; i < messages.size(); i++) {

                    Message message = messages.get(i);
                    ChatModel model = new ChatModel();


                    if (i == messages.size() - 1) {
                        lastMessageId = String.valueOf(message.getMessage_id());
                    }

                    model.setMessage_id(message.getMessage_id());
                    model.setSender_id(message.getSender_id());
                    model.setReceiver_id(message.getReceiver_id());

                    if(message.getMessage_type()==Values.MessageType.MESSAGE_TYPE_REQUEST){
                        Helper.getInstance().LogDetails("MESSAGE_TYPE_REQUEST","----"+message.getMessage());

                        Object obj=message.getMessage();
                        Gson gson = new Gson();
                        String json = gson.toJson(obj);
                        model.setMessage(json.toString());
                    }
                    else
                    {
                        model.setMessage(message.getMessage().toString());
                    }

                    model.setIs_group(0);
                    model.setIs_delivered(message.getIs_delivered());
                    model.setIs_read(message.getIs_read());
                    model.setConversation_reference_id(message.getConversation_reference_id());
                    model.setAttachment(message.getAttachment());
                    model.setAttachment_name(message.getAttachment_name());
                    model.setAttachment_extension(message.getAttachment_extension());
                   // model.setIs_downloaded(0);
                   // model.setAttachmentDownloaded(false);
                    model.setCaption("");
                    model.setOriginal_message("");
                    model.setReference_id(message.getReference_id());
                    model.setCreated_at(message.getCreated_at());
                    model.setIs_sync(1);
                    model.setMessage_type(message.getMessage_type());

                    ConversationTable table = new ConversationTable(ChatActivity.this);
                    boolean status = table.checkMessageIdAndInsertMessage(model);

                    model.setAttachmentDownloaded(status);
                    if(status){
                        model.setIs_downloaded(1);
                    }
                    else
                    {
                        model.setIs_downloaded(0);
                    }

                    list.add(model);
                    scrollItem();


                }

                if (list != null && list.size() > 0) {
                    Helper.getInstance().LogDetails("checkUserAvailable updateChatData msg","called"+messages.size()+" "+list.size());
                    if (adapter == null) {
                        adapter = new ConversationAdapter(ChatActivity.this, list, receiver_id, receiver_name, handler);
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                closeProgress();

                updateReadMessageStatus();
              //  getSmartReplies();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void checkLocalMessages(){
        if (conversationTable == null) {
            conversationTable = new ConversationTable(ChatActivity.this);
        }
        if(localMessages!=null && localMessages.size()>0){
            localMessages.clear();
        }
        if(localMessages==null){
            localMessages=new ArrayList<>();
        }
        localMessages=  conversationTable.getLocalMessages(Integer.parseInt(receiver_id),Integer.parseInt(tmUserId),conversation_reference_id);
        if(localMessages!=null && localMessages.size()>0){
            Helper.getInstance().LogDetails("getLocalMessages","checkLocalMessages size "+localMessages.size());
            if(list!=null){
                if(list.size()==0){
                    list.addAll(localMessages);
                }
                else
                {
                    for(int j=0;j<localMessages.size();j++){
                        String localRef=localMessages.get(j).getReference_id();
                        boolean isExists=false;
                        if(list.size()>0){
                            for(int i=0;i<list.size();i++){
                                String  ref=list.get(i).getReference_id();
                                if(localRef!=null && ref!=null && localRef.equals(ref))
                                {
                                    isExists=true;
                                    break;
                                }
                            }
                        }


                        if(!isExists){
                            list.add(0,localMessages.get(j));

                        }
                    }



                }
                sortMessageListByTime();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                });




            }

        }
    }

    public void newMessageReceived(JSONObject jsonObject) {

        try {
            Helper.getInstance().LogDetails("newMessageReceived message received called", jsonObject.toString());

          /*    {"sender_id":"1190","receiver_id":2,"message_type":1,"message_id":15593,"created_at":"2019-05-28 11:42:26","reference_id":"","is_group":0,"is_reply":0,"attachment":"https:\/\/s3.amazonaws.com\/files.c2m\/user\/28052019114225\/IMG-20190522-WA0003.jpg.jpeg",
                "attachment_extension":"jpeg","attachment_name":"IMG-20190522-WA0003.jpg.jpeg",
                "conversation_reference_id":"696b87eacbc8b67d55"}*/

  /*          {"sender_id":"5522","receiver_id":3715,
                    "message_type":12,"message_id":45987,"created_at":"2019-07-18 15:23:03",
                    "reference_id":"","is_group":0,"is_reply":0,
                    "message":{"type":1,"message":"user confirmed mobile number"},"conversation_reference_id":"3e83463d66de465090"}*/

            if (jsonObject != null) {

                ChatModel model = new ChatModel();

                int receiverId = jsonObject.optInt("receiver_id");
                int senderId = jsonObject.optInt("sender_id");
                String referenceId = jsonObject.optString("reference_id");
                String conversationReferenceId = jsonObject.optString("conversation_reference_id");


                updateReadMessageStatus();


                if (conversation_reference_id != null && !conversation_reference_id.trim().isEmpty() && conversationReferenceId!=null && conversation_reference_id.equals(conversationReferenceId)) {


                    model.setSender_id(jsonObject.optInt("sender_id"));
                    model.setReceiver_id(jsonObject.optInt("receiver_id"));
                    model.setMessage_type(jsonObject.optInt("message_type"));
                    model.setMessage_id(jsonObject.optInt("message_id"));
                    model.setReference_id(jsonObject.optString("reference_id"));
                    model.setIs_reply(jsonObject.optInt("is_reply"));
                    model.setMessage(jsonObject.optString("message"));
                    model.setCreated_at(jsonObject.optString("created_at"));
                    model.setAttachment(jsonObject.optString("attachment"));
                    model.setAttachment_name(jsonObject.optString("attachment_name"));
                    model.setAttachment_extension(jsonObject.optString("attachment_extension"));
                    model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                    model.setIs_group(jsonObject.optInt("is_group"));
                    model.setAttachmentDownloaded(false);
                    model.setIs_downloaded(0);
                    model.setIs_delivered(0);
                    model.setIs_received(1);
                    model.setIs_read(1);

                    if (list != null && list.size() > 0) {
                        boolean present = false;

                        for (int i = 0; i < list.size(); i++) {
                            String rid = list.get(i).getReference_id();
                            int messageType = list.get(i).getMessage_type();
                            if (messageType == Values.MessageType.MESSAGE_TYPE_TYPING) {
                                list.remove(i);

                                adapter.notifyDataSetChanged();
                            }
                            if (rid != null && referenceId != null && !rid.isEmpty() && !referenceId.isEmpty() && rid.equals(referenceId)) {
                                present = true;

                                break;
                            }
                        }
                        if (!present) {

                            list.add(0, model);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    adapter.notifyDataSetChanged();

                                }
                            });
                            scrollItem();
                        }
                    } else {


                        list.add(0, model);

                        lastMessageId = String.valueOf(model.getMessage_id());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();

                            }
                        });
                        scrollItem();
                    }

                    //getSmartReplies();




                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSmartReplies(){


        List<ChatModel> chatModels=new ArrayList<>();

        if(list!=null && list.size()>0){

                for(int i=0;i<list.size();i++){
                    if(i<2) {
                        ChatModel model = list.get(i);
                        if(model.getMessage()!=null && !model.getMessage().isEmpty())
                        {
                            chatModels.add(model);
                        }
                    }
                }
                Collections.reverse(chatModels);
                sendHistoryToFirebase(chatModels);
        }
    }

    public void newMessageDelivered(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                Helper.getInstance().LogDetails("newMessageDelivered","called "+jsonObject.toString());
                int receiver_id = jsonObject.optInt("receiver_id");
                int is_delivered = jsonObject.optInt("is_delivered_to_all");
                int message_id = jsonObject.optInt("message_id");
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        int messageId = list.get(i).getMessage_id();
                        if (message_id == messageId) {

                            list.get(i).setIs_delivered(1);
                            Helper.getInstance().LogDetails("message delivered info", message_id + " " + is_delivered + " " + receiver_id + " " + list.get(i).getIs_delivered());
                            break;
                        }
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(adapter!=null)
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void newMessageRead(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {

                int is_read = jsonObject.optInt("is_read_by_all");
                int message_id = jsonObject.optInt("message_id");
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        int messageId = list.get(i).getMessage_id();
                        if (message_id == messageId) {
                            Helper.getInstance().LogDetails("message delivered", message_id + " " + is_read + " ");
                            list.get(i).setIs_delivered(1);
                            list.get(i).setIs_read(1);
                            break;
                        }

                    }
               /* if(isTabChanged){
                    adapter.setList(agentsList);
                }*/
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(adapter!=null)
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void newMessageSent(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("newMessageSent***",jsonObject.toString());
        try {
            if (jsonObject != null) {
                if (jsonObject.has("reference_id")) {
                    String rid = jsonObject.optString("reference_id");
                    String conversationReferenceId = jsonObject.optString("conversation_reference_id");
                    try {

                        if (rid != null && !rid.trim().isEmpty()) {
                            if (jsonObject.optString("sender_id") != null && tmUserId != null && jsonObject.optString("sender_id").equals(tmUserId) && jsonObject.optString("receiver_id") != null && receiver_id != null && jsonObject.optString("receiver_id").equals(receiver_id) && conversationReferenceId != null && !conversationReferenceId.trim().isEmpty() && conversationReferenceId.equals(conversation_reference_id)) {

                                boolean isPresent = false;
                                int message_id = jsonObject.optInt("message_id");
                                if (list != null && list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        String reference_id = list.get(i).getReference_id();
                                        if (reference_id.equals(rid)) {

                                            list.get(i).setMessage_id(message_id);
                                            list.get(i).setIs_sync(1);
                                            isPresent = true;
                                            break;
                                        }

                                    }
                              /*  if(isTabChanged){
                                    adapter.setList(agentsList);
                                }*/
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if(adapter!=null)
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    scrollItem();
                                }

                                if (!isPresent) {

                                    ChatModel model = new ChatModel();
                                    model.setMessage_id(jsonObject.optInt("message_id"));
                                    model.setSender_id(Integer.parseInt(jsonObject.optString("sender_id")));
                                    model.setReceiver_id(Integer.parseInt(jsonObject.optString("receiver_id")));
                                    model.setMessage(jsonObject.optString("message"));
                                    model.setIs_group(jsonObject.optInt("is_group"));
                                    model.setIs_received(0);
                                    model.setIs_sync(1);
                                    model.setIs_reply(jsonObject.optInt("is_reply"));
                                    model.setAttachment(jsonObject.optString("attachment"));
                                    model.setAttachment_name(jsonObject.optString("attachment_name"));
                                    model.setAttachment_extension(jsonObject.optString("attachment_extension"));
                                    model.setCaption("");
                                    model.setOriginal_message("");
                                    model.setIs_delivered(0);
                                    model.setIs_read(1);
                                    model.setMessage_type(jsonObject.optInt("message_type"));
                                    model.setReference_id(jsonObject.optString("reference_id"));
                                    model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                                    model.setCreated_at(jsonObject.optString("created_at"));
                                  //  model.setIs_downloaded(1);
                                 //   model.setAttachmentDownloaded(true);

                                    if(jsonObject.optInt("message_type")==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                                    {

                                        ConversationTable table = new ConversationTable(ChatActivity.this);
                                        boolean status = table.checkMessageIdAndInsertMessage(model);

                                        Helper.getInstance().LogDetails("newMessageSent","if "+status);

                                        model.setAttachmentDownloaded(status);
                                        if(status){
                                            model.setIs_downloaded(1);
                                        }
                                        else
                                        {
                                            model.setIs_downloaded(0);
                                        }

                                    }

                                        ChatModel typeingModel=null;
                                        if(list!=null && list.size()>0){
                                            int messageType = list.get(0).getMessage_type();
                                            if (messageType == Values.MessageType.MESSAGE_TYPE_TYPING) {
                                                typeingModel=list.get(0);
                                                list.remove(0);
                                                //adapter.notifyDataSetChanged();
                                            }
                                        }

                                     list.add(0, model);

                                    // Helper.getInstance().LogDetails("newMessageSent","called"+tmUserId+" "+receiver_id);
                            /*    if(isTabChanged){
                                    adapter.setList(agentsList);
                                }*/
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                    if(typeingModel!=null){
                                        list.add(0, typeingModel);
                                    }

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if(adapter!=null)
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                    scrollItem();
                                }


                            }
                        } else {


                            if (jsonObject.optString("sender_id") != null && tmUserId != null && jsonObject.optString("sender_id").equals(tmUserId) && jsonObject.optString("receiver_id") != null && receiver_id != null && jsonObject.optString("receiver_id").equals(receiver_id)) {

                                boolean isPresent = false;
                                int message_id = jsonObject.optInt("message_id");
                                if (list != null && list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        int mid = list.get(i).getMessage_id();
                                        if (message_id == mid) {

                                            list.get(i).setMessage_id(message_id);
                                            list.get(i).setIs_sync(1);
                                            isPresent = true;
                                            break;
                                        }

                                    }
                              /*  if(isTabChanged){
                                    adapter.setList(agentsList);
                                }*/
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    scrollItem();
                                }

                                if (!isPresent) {

                                    ChatModel model = new ChatModel();
                                    model.setMessage_id(jsonObject.optInt("message_id"));
                                    model.setSender_id(Integer.parseInt(jsonObject.optString("sender_id")));
                                    model.setReceiver_id(Integer.parseInt(jsonObject.optString("receiver_id")));
                                    model.setMessage(jsonObject.optString("message"));
                                    model.setIs_group(jsonObject.optInt("is_group"));
                                    model.setIs_received(0);
                                    model.setIs_sync(1);
                                    model.setIs_reply(jsonObject.optInt("is_reply"));
                                    model.setAttachment(jsonObject.optString("attachment"));
                                    model.setAttachment_name(jsonObject.optString("attachment_name"));
                                    model.setAttachment_extension(jsonObject.optString("attachment_extension"));
                                    model.setCaption("");
                                    model.setOriginal_message("");
                                    model.setIs_delivered(0);
                                    model.setMessage_type(jsonObject.optInt("message_type"));
                                    model.setReference_id(jsonObject.optString("reference_id"));
                                    model.setConversation_reference_id(jsonObject.optString("conversation_reference_id"));
                                    model.setCreated_at(jsonObject.optString("created_at"));
                                    model.setIs_downloaded(0);
                                    model.setAttachmentDownloaded(false);

                                    if(jsonObject.optInt("message_type")==Values.MessageType.MESSAGE_TYPE_ATTACHMENT)
                                    {

                                        ConversationTable table = new ConversationTable(ChatActivity.this);
                                        boolean status = table.checkMessageIdAndInsertMessage(model);

                                        Helper.getInstance().LogDetails("newMessageSent","else "+status);

                                        model.setAttachmentDownloaded(status);
                                        if(status){
                                            model.setIs_downloaded(1);
                                        }
                                        else
                                        {
                                            model.setIs_downloaded(0);
                                        }

                                    }



                                    list.add(0, model);

                            /*    if(isTabChanged){
                                    adapter.setList(agentsList);
                                }*/
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if(adapter!=null)
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                    scrollItem();
                                }


                            }
                        }

                    } catch (Exception e) {
                        Helper.getInstance().LogDetails("chat update:", e.getMessage() + " " + e.getLocalizedMessage() + " " + e.getCause());
                        e.printStackTrace();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void scrollItem() {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (chatRecyclerView != null) {
                        chatRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                Helper.getInstance().LogDetails("chatRecyclerView chatRecyclerView scrollItem"," "+scrollPosition);

                                //if(scrollPosition<5)
                               // chatRecyclerView.smoothScrollToPosition(0);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  void scrollListToPosition(int position) {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (chatRecyclerView != null) {
                        chatRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(list!=null && position<list.size())
                                {
                                  //  if(scrollPosition<5)
                                   // chatRecyclerView.smoothScrollToPosition(position);
                                }

                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openMoreInfo() {

        more_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        //bottom_lay_card.setVisibility(View.GONE);
        // moreLayoutLine.setVisibility(View.VISIBLE);
        if (!isSelf) {
            endChatLayout.setVisibility(View.GONE);
        }
    }

    private void openMoreInfoDialog() {

        try {
            final Dialog dialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
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
            dialog.setContentView(R.layout.chat_more_layout);

            LinearLayout cancelInfoDialog, endChatLayout, chatInfoLayout;
            View line;

            endChatLayout = dialog.findViewById(R.id.endChatLayout);
            chatInfoLayout = dialog.findViewById(R.id.chatInfoLayout);
            line = dialog.findViewById(R.id.line);

            if (!isSelf) {
                endChatLayout.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }


            cancelInfoDialog = dialog.findViewById(R.id.cancelInfoDialog);
            cancelInfoDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //isInfoClicked=false;
                    // chat_layout.setVisibility(View.VISIBLE);
                    dialog.cancel();
                }
            });



          /*  case R.id.close_icon:
                showChatEndConfirmationDialog();
                break;
            case R.id.info_icon:
                showCustomerInfo();
                break;*/

            chatInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                  //  showCustomerInfo();
                    showCustomerInfoDialog();
                }
            });

            endChatLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    showChatEndConfirmationDialog();
                }
            });


            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //  isInfoClicked=false;
                    // chat_layout.setVisibility(View.VISIBLE);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCurrentDate() {

        try{

        String dateFormat1 = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1);
        Calendar c = Calendar.getInstance();
        return sdf1.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public void updateChatList(final ActiveChat userData, int position) {

        clearSmartReplies();

       saveTypingMessage();

        activeChatPosition=position;
        Helper.getInstance().LogDetails("checkUserAvailable updateChatList","called"+userData.toString());

        try{


        updateReadMessageStatus();
        if (list != null && list.size() > 0) {

            list.clear();
            if (adapter != null) {
               /* if(isTabChanged)
                {
                    adapter.setList(agentsList);
                }*/
                adapter.notifyDataSetChanged();

            }
        }

        if (userData != null) {
            scrollList(position);

            try {

                try {

                    if (userData != null) {
                        activeChat = userData;
                        receiver_id = activeChat.getTmVisitorId();
                        chat_id = activeChat.getChatId();
                        agent_id = activeChat.getAgentId();
                        lastMessageId = "0";
                        conversation_reference_id = activeChat.getChatReferenceId();
                        current_site_id = activeChat.getSiteId();
                        updateReadMessageStatus();
                        setUnreadCount();
                        getHistory();
                        getTrack();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                topAdapter.notifyDataSetChanged();
                            }
                        });
                        if(activeChat!=null){
                            String chatStatus=activeChat.getChatStatus();
                            if(chatStatus!=null && chatStatus.equals("2")){


                                back();
                            }
                            else
                            {
                              if(isSelf){
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          send_icon.setVisibility(View.VISIBLE);
                                      }
                                  });

                                  chat_edit_txt.setHint("Type your Message...");
                                  getTypingMessage();
                                  chat_edit_txt.setEnabled(true);
                              }else
                              {
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          send_icon.setVisibility(View.GONE);
                                      }
                                  });

                                  chat_edit_txt.setHint("You can't send messages");
                                  chat_edit_txt.setText(null);
                                  chat_edit_txt.setEnabled(false);
                              }


                              /*  if(activeChat.getTyping_message()!=null && !activeChat.getTyping_message().trim().isEmpty())
                                {
                                    chat_edit_txt.setText(activeChat.getTyping_message());
                                    chat_edit_txt.setSelection(activeChat.getTyping_message().length());
                                }
                                else
                                {
                                    chat_edit_txt.setText("");
                                }*/

                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Helper.getInstance().LogDetails("userEndedChatLayout","trigger 2");
                                      userEndedChatLayout.setVisibility(View.GONE);
                                  }
                              });



                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

          /*  new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();*/

        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getActiveChatsApi() {

        try{

        if (Utilities.getConnectivityStatus(ChatActivity.this) <= 0) {
            Helper.getInstance().pushToast(ChatActivity.this, "Please check your network connection...");
            return;
        }
        // openProgess();

        Helper.getInstance().LogDetails("getActiveChatsApi", "show called" + ApiEndPoint.token + " company_token " + company_token + " user_id " + user_id  + " site_id " + site_id + " account_id " + account_id + "  apiRole " + apiRole);

        retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call = ActiveChatsApi.getApiService().getActiveChats(ApiEndPoint.token, company_token, user_id, site_id, account_id, apiRole);

        call.enqueue(new Callback<ActiveChatsApi.ActiveChatsResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call, @NonNull Response<ActiveChatsApi.ActiveChatsResponse> response) {

                Helper.getInstance().LogDetails("Intent data getActiveChatsApi", "dismiss called");
               // closeProgress();

                ActiveChatsApi.ActiveChatsResponse data = response.body();

                if (data != null) {
                    if (data.isSuccess()) {
                        if (data.getData() != null) {
                            Helper.getInstance().LogDetails("getActiveChatsApi", "success " + data.getData().size());
                            updateSiteList(data.getData());
                            getTypingMessage();
                            checkUserStatus();


                        } else {
                            Helper.getInstance().LogDetails("getActiveChatsApi", "data null");
                        }
                    } else {
                        Helper.getInstance().pushToast(ChatActivity.this, data.getMessage());
                    }
                } else {
                    Helper.getInstance().LogDetails("getActiveChatsApi", "res null");
                }
            }


            @Override
            public void onFailure(@NonNull retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                closeProgress();
                Helper.getInstance().LogDetails("getActiveChatsApi", "exception " + t.getLocalizedMessage() + " " + t.getMessage());
              //  closeProgress();
            }
        });
        }catch (Exception e){
            e.printStackTrace();
            closeProgress();
        }


    }
    private void checkUserStatus() {
        try{

            if (tmSocket != null && tmSocket.connected()) {
                // Helper.getInstance().LogDetails("updateAgentStatusInfo checkAgentStatus","called");
                JSONObject object = new JSONObject();
                object.put("access_token", accessToken);
                Helper.getInstance().LogDetails("checkUserStatus ",object.toString());
                tmSocket.emit(SocketConstants.EVENT_GET_USER_AVAILABILITY_STATUS, object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateSiteListWithLocalDb() {

        try{

            Helper.getInstance().LogDetails("updateSiteListWithLocalDb","called");

            if (activeChat != null) {
                current_site_id = activeChat.getSiteId();

                Helper.getInstance().LogDetails("updateSiteListWithLocalDb", "active chat not null  ***" + current_site_id);
            } else {
                Helper.getInstance().LogDetails("updateSiteListWithLocalDb", "active chat null  xxx" + current_site_id);
            }
            Helper.getInstance().LogDetails("updateSiteListWithLocalDb updateSiteList", "data size "  + " ==" + current_site_id);




                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                    for (int j = 0; j < sitesInfoList.size(); j++) {
                        List<ActiveChat> activeChatArrayList = new ArrayList<>();
                        sitesInfoList.get(j).setActiveChats(activeChatArrayList);
                    }
                }

                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    for (int j = 0; j < sitesInfoList.size(); j++) {
                        String sid = sitesInfoList.get(j).getSiteId();
                        if(isSelf){
                            sitesInfoList.get(j).setPresent(true);
                        }
                        else
                        {
                            if(selectedPositionSiteId!=null && !selectedPositionSiteId.trim().isEmpty())
                            {
                                boolean isExists=false;
                                String[] separated = selectedPositionSiteId.split(",");
                                if(separated!=null && separated.length>0){
                                    for(int i=0;i<separated.length;i++){
                                        Helper.getInstance().LogDetails("=============",sid+" "+separated[i]+" "+selectedPositionSiteId);
                                        if(sid.equals(separated[i].trim())){
                                            isExists=true;
                                            break;
                                        }
                                    }
                                    if(isExists){
                                        sitesInfoList.get(j).setPresent(true);
                                    }
                                    else{
                                        sitesInfoList.get(j).setPresent(false);
                                    }

                                }
                                else
                                {
                                    sitesInfoList.get(j).setPresent(false);
                                }
                            }
                            else{
                                sitesInfoList.get(j).setPresent(false);
                            }
                        }
                        boolean isPresent = false;


                            Helper.getInstance().LogDetails("updateSiteListWithLocalDb",  current_site_id+" "+sid);
                            if ( sid != null && !sid.trim().isEmpty() ) {
                                isPresent = true;
                                if(activeChatsTable==null){
                                    activeChatsTable=new ActiveChatsTable(ChatActivity.this);
                                }
                                List<ActiveChat> activeChatList =activeChatsTable.getActiveChatList(Integer.parseInt(sid));
                                List<ActiveChat> temp = setUnreadCount(activeChatList);
                                if (sitesInfoList.get(j).getActiveChats() != null && sitesInfoList.get(j).getActiveChats().size() > 0) {
                                    sitesInfoList.get(j).getActiveChats().clear();
                                    sitesInfoList.get(j).setActiveChats(temp);
                                } else {
                                    sitesInfoList.get(j).setActiveChats(temp);

                                }

                                if (sid != null && current_site_id != null && sid.equals(current_site_id)) {
                                    scrollCompanyList(j);
                                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                        activeChatArrayList.clear();
                                    }
                                    if (activeChatArrayList == null) {
                                        activeChatArrayList = new ArrayList<>();
                                    }
                                    List<ActiveChat> temp1 = new ArrayList<>();
                                    temp1 = sitesInfoList.get(j).getActiveChats();
                                    activeChatArrayList.addAll(temp1);

                                    Helper.getInstance().LogDetails("updateSiteListWithLocalDb",  "calling back");


                                    if (topAdapter == null) {
                                        topAdapter = new TopRecyclerAdapter(ChatActivity.this, activeChatArrayList, isSelf);
                                    }

                                    if (topAdapter != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                topAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                }

                            }



                        if (!isPresent) {
                            List<ActiveChat> activeChatList = new ArrayList<>();
                            sitesInfoList.get(j).setActiveChats(activeChatList);
                            if (sid != null && current_site_id != null && sid.equals(current_site_id)) {
                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                    activeChatArrayList.clear();
                                }
                                if (activeChatArrayList == null) {
                                    activeChatArrayList = new ArrayList<>();
                                }

                            }
                        }


                    }

                }

                checkUserAvailable();
                getHistory();
                getTrack();
                checkScroll();





            saveSiteData();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (companyAdapter != null) {
                        companyAdapter.notifyDataSetChanged();
                    }
                    if (topAdapter != null) {

                        Helper.getInstance().LogDetails("updateSiteListWithLocalDb", "activeChatArrayList size " + activeChatArrayList.size());
                        topAdapter.notifyDataSetChanged();
                    }


                }
            });
        }catch (Exception e){
            Helper.getInstance().LogDetails("updateSiteListWithLocalDb","exception called"+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private void updateSiteList(List<SiteData> data) {

        try{

        if (activeChat != null) {
            current_site_id = activeChat.getSiteId();

            Helper.getInstance().LogDetails("getActiveChatsApi", "active chat not null  ***" + current_site_id);
        } else {
            Helper.getInstance().LogDetails("getActiveChatsApi", "active chat null  xxx" + current_site_id);
        }
        Helper.getInstance().LogDetails("getActiveChatsApi updateSiteList", "data size " + data.size() + " ==" + current_site_id);

        if (data != null && data.size() > 0) {


            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int j = 0; j < sitesInfoList.size(); j++) {
                    List<ActiveChat> activeChatArrayList = new ArrayList<>();
                    sitesInfoList.get(j).setActiveChats(activeChatArrayList);
                }
            }

            if (sitesInfoList != null && sitesInfoList.size() > 0) {

                for (int j = 0; j < sitesInfoList.size(); j++) {
                    String sid = sitesInfoList.get(j).getSiteId();
                    if(isSelf){
                        sitesInfoList.get(j).setPresent(true);
                    }
                    else
                    {
                        if(selectedPositionSiteId!=null && !selectedPositionSiteId.trim().isEmpty())
                        {
                            boolean isExists=false;
                            String[] separated = selectedPositionSiteId.split(",");
                            if(separated!=null && separated.length>0){
                                for(int i=0;i<separated.length;i++){
                                    Helper.getInstance().LogDetails("=============",sid+" "+separated[i]+" "+selectedPositionSiteId);
                                    if(sid.equals(separated[i].trim())){
                                        isExists=true;
                                        break;
                                    }
                                }
                                if(isExists){
                                    sitesInfoList.get(j).setPresent(true);
                                }
                                else{
                                    sitesInfoList.get(j).setPresent(false);
                                }

                            }
                            else
                            {
                                sitesInfoList.get(j).setPresent(false);
                            }
                        }
                        else{
                            sitesInfoList.get(j).setPresent(false);
                        }
                    }
                    boolean isPresent = false;
                    for (int i = 0; i < data.size(); i++) {
                        String siteId = data.get(i).getSiteId();
                        Helper.getInstance().LogDetails("getActiveChatsApi", siteId + " " + current_site_id);
                        if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                            isPresent = true;
                            List<ActiveChat> activeChatList = data.get(i).getActiveChats();
                            List<ActiveChat> temp = setUnreadCount(activeChatList);
                            if (sitesInfoList.get(j).getActiveChats() != null && sitesInfoList.get(j).getActiveChats().size() > 0) {
                                sitesInfoList.get(j).getActiveChats().clear();
                                sitesInfoList.get(j).setActiveChats(temp);
                            } else {
                                sitesInfoList.get(j).setActiveChats(temp);

                            }

                            if (sid != null && current_site_id != null && sid.equals(current_site_id)) {
                                scrollCompanyList(j);
                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                    activeChatArrayList.clear();
                                }
                                if (activeChatArrayList == null) {
                                    activeChatArrayList = new ArrayList<>();
                                }
                                List<ActiveChat> temp1 = new ArrayList<>();
                                temp1 = sitesInfoList.get(j).getActiveChats();
                                activeChatArrayList.addAll(temp1);


                              /*  if(activeChatList!=null && activeChatList.size()>0){
                                    for(int k=0;k<activeChatList.size();k++){
                                        ChatModel model=conversationTable.getLatestMessage(activeChatArrayList.get(j).getChatReferenceId());
                                        if(model.getCreated_at()==null || model.getCreated_at().trim().isEmpty())
                                        {
                                            model.setCreated_at("0000-00-00 00:00:00");
                                        }
                                        activeChatArrayList.get(k).setChatModel(model);
                                    }
                                    sortUserList();
                                }*/




                                if (topAdapter == null) {
                                    topAdapter = new TopRecyclerAdapter(ChatActivity.this, activeChatArrayList, isSelf);
                                }

                                if (topAdapter != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            topAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }

                            }

                        }


                    }
                    if (!isPresent) {
                        List<ActiveChat> activeChatList = new ArrayList<>();
                        sitesInfoList.get(j).setActiveChats(activeChatList);
                        if (sid != null && current_site_id != null && sid.equals(current_site_id)) {
                            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                activeChatArrayList.clear();
                            }
                            if (activeChatArrayList == null) {
                                activeChatArrayList = new ArrayList<>();
                            }



                        }
                    }


                }

            }



            checkUserAvailable();
            getHistory();
            getTrack();
            checkScroll();


        } else {
            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int j = 0; j < sitesInfoList.size(); j++) {
                    List<ActiveChat> activeChatArrayList = new ArrayList<>();
                    sitesInfoList.get(j).setActiveChats(activeChatArrayList);
                    String sid = sitesInfoList.get(j).getSiteId();
                    if(isSelf){
                        sitesInfoList.get(j).setPresent(true);
                    }
                    else
                    {
                        if(selectedPositionSiteId!=null && !selectedPositionSiteId.trim().isEmpty())
                        {
                            boolean isExists=false;
                            String[] separated = selectedPositionSiteId.split(",");
                            if(separated!=null && separated.length>0){
                                for(int i=0;i<separated.length;i++){
                                    Helper.getInstance().LogDetails("=============",sid+" "+separated[i]+" "+selectedPositionSiteId);
                                    if(sid.equals(separated[i].trim())){
                                        isExists=true;
                                        break;
                                    }
                                }
                                if(isExists){
                                    sitesInfoList.get(j).setPresent(true);
                                }
                                else{
                                    sitesInfoList.get(j).setPresent(false);
                                }

                            }
                            else
                            {
                                sitesInfoList.get(j).setPresent(false);
                            }
                        }
                        else{
                            sitesInfoList.get(j).setPresent(false);
                        }
                    }
                }

            }

            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                activeChatArrayList.clear();
            }
            if (activeChatArrayList == null) {
                activeChatArrayList = new ArrayList<>();
            }

            if (isNavigatedFromNotification) {

                /*Toast toast = Toast.makeText(ChatActivity.this, "User not available", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/

              checkUserAvailable();
            }



            getHistory();
            getTrack();
        }

        saveSiteData();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (companyAdapter != null) {
                    companyAdapter.notifyDataSetChanged();
                }
                if (topAdapter != null) {

                    Helper.getInstance().LogDetails("updateSiteList", "activeChatArrayList size " + activeChatArrayList.size());
                    topAdapter.notifyDataSetChanged();
                }


            }
        });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private List<ActiveChat> setUnreadCount(List<ActiveChat> activeChatArrayList) {
        try{
        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
            if (conversationTable == null) {
                conversationTable = new ConversationTable(ChatActivity.this);
            }
            for (int i = 0; i < activeChatArrayList.size(); i++) {

                if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty()) {
                    int unreadCount = conversationTable.getTheUnreadMessageAvailable(Integer.parseInt(activeChatArrayList.get(i).getTmVisitorId()), Integer.parseInt(tmUserId), activeChatArrayList.get(i).getChatReferenceId());
                    activeChatArrayList.get(i).setUnread_message_count(unreadCount);
                }
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        return activeChatArrayList;
    }

    private void getUnreadCount() {
        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
            if (conversationTable == null) {
                conversationTable = new ConversationTable(ChatActivity.this);


            }
            for (int i = 0; i < activeChatArrayList.size(); i++) {

                if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty()) {
                    if (receiver_id != null && receiver_id.equals(activeChatArrayList.get(i).getTmVisitorId())) {
                        //continue;
                    } else {
                        int unreadCount = conversationTable.getTheUnreadMessageAvailable(Integer.parseInt(activeChatArrayList.get(i).getTmVisitorId()), Integer.parseInt(tmUserId), activeChatArrayList.get(i).getChatReferenceId());
                        activeChatArrayList.get(i).setUnread_message_count(unreadCount);
                    }

                }
            }
            if (topAdapter != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        topAdapter.notifyDataSetChanged();
                    }
                });


            }

        }
    }

    private ActiveChat getActiveUserData(String conversationReferenceId) {
        try{
        if (conversationReferenceId != null) {
            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {

                for (int i = 0; i < activeChatArrayList.size(); i++) {
                    Helper.getInstance().LogDetails("Intent data getActiveUserData", conversationReferenceId + " " + activeChatArrayList.get(i).getChatReferenceId());
                    ActiveChat activeChat = activeChatArrayList.get(i);
                    if (activeChat != null && activeChat.getChatReferenceId() != null) {

                        if (conversationReferenceId.equals(activeChat.getChatReferenceId())) {
                            return activeChat;
                        }
                    }
                }
            } else {
                Helper.getInstance().LogDetails("Intent data getActiveUserData", conversationReferenceId);
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setupUI(final View view) {
        try{
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    Helper.getInstance().closeKeyBoard(ChatActivity.this, view);
                    return false;
                }
            });
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void makeCall(String number) {
        try{
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
                    Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Values.Permissions.CAll_PERMISSION);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            //callIntent.setData(Uri.parse(number));
            startActivity(callIntent);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void sendEmail() {
        try{

        // String[] TO = {""};
            if(activeChat!=null && activeChat.getEmail()!=null && !activeChat.getEmail().trim().isEmpty() && !activeChat.getEmail().contains("@click2magic.com"))
            {
                String[] TO = {activeChat.getEmail()};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                // emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                // emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                // emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                   // finish();
                    Log.e("Finished sending email", "");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ChatActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void sendMail() {
        String[] TO = {activeChat.getEmail()};
        String[] CC = {""};
        List<Intent> emailAppLauncherIntents = new ArrayList<>();

        //Intent that only email apps can handle:
        Intent emailAppIntent = new Intent(Intent.ACTION_SENDTO);
        emailAppIntent.setData(Uri.parse("mailto:"));
        // emailAppIntent.setType("text/plain");
        emailAppIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailAppIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        // emailAppIntent.putExtra(Intent.EXTRA_CC, CC);
        // emailAppIntent.putExtra(Intent.EXTRA_TEXT, "");

        PackageManager packageManager = getPackageManager();

        //All installed apps that can handle email intent:
        List<ResolveInfo> emailApps = packageManager.queryIntentActivities(emailAppIntent, PackageManager.MATCH_ALL);

        for (ResolveInfo resolveInfo : emailApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            emailAppLauncherIntents.add(launchIntent);
        }

        //Create chooser
        Intent chooserIntent = Intent.createChooser(new Intent(), "Select email app:");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailAppLauncherIntents.toArray(new Parcelable[emailAppLauncherIntents.size()]));
        startActivity(chooserIntent);
    }



    public void scrollList(int position) {
        try{
        Helper.getInstance().LogDetails("scrollList", "called" + position + " " + activeChatArrayList.size());
        if (activeChatArrayList != null && position < activeChatArrayList.size()) {
            Helper.getInstance().LogDetails("scrollList", "if called ");
            if (topRecyclerView != null) {
                Helper.getInstance().LogDetails("scrollList", "inner if called ");
                topRecyclerView.smoothScrollToPosition(position);
            }

        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void scrollCompanyList(int position) {
        try{
            Helper.getInstance().LogDetails("scrollCompanyList", "called" + position + " " + sitesInfoList.size());
            if (sitesInfoList != null && position < sitesInfoList.size()) {
                Helper.getInstance().LogDetails("scrollCompanyList", "if called ");
                if (companyRecyclerView != null) {
                    Helper.getInstance().LogDetails("scrollCompanyList", "inner if called ");
                    companyRecyclerView.smoothScrollToPosition(position);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    };

    public void checkScroll() {
        try{
        Helper.getInstance().LogDetails("checkScroll", "called");
        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
            Helper.getInstance().LogDetails("checkScroll", "size " + activeChatArrayList.size());
            if (receiver_id != null && !receiver_id.trim().isEmpty()) {
                Helper.getInstance().LogDetails("checkScroll", "receiver_id " + receiver_id);
                for (int i = 0; i < activeChatArrayList.size(); i++) {
                    String rid = activeChatArrayList.get(i).getTmVisitorId();
                    if (rid != null && !rid.trim().isEmpty() && rid.equals(receiver_id)) {
                        scrollList(i);
                    }
                }
            }
        }
        if (topAdapter != null) {
            topAdapter.notifyDataSetChanged();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTypingMessage(){
        if(activeChatArrayList!=null && activeChatArrayList.size()>0){
            if(activeChatPosition!=-1 && activeChatPosition<activeChatArrayList.size()){
                String text=chat_edit_txt.getText().toString();
                activeChatArrayList.get(activeChatPosition).setTyping_message(text);
                if(typingMessageTable==null){
                    typingMessageTable=new TypingMessageTable(ChatActivity.this);
                }
                if(activeChat!=null) {
                    if (activeChat.getChatId() != null && !activeChat.getChatId().trim().isEmpty())
                    {
                        typingMessageTable.insertMessage(Integer.parseInt(activeChat.getChatId()),text);
                    }

                }

            }
        }
    }

    private void getTypingMessage(){
        try {
            if (chat_edit_txt == null) {
                chat_edit_txt = findViewById(R.id.chat_edit_txt);
            }
            if (activeChat != null && activeChat.getChatId() != null && !activeChat.getChatId().trim().isEmpty()) {
                if(typingMessageTable!=null) {
                    TypingMessage typingMessage = typingMessageTable.getTypingMessage(Integer.parseInt(activeChat.getChatId()));
                    if (typingMessage != null) {
                        if (typingMessage.getMessage() != null && !typingMessage.getMessage().trim().isEmpty()) {
                            chat_edit_txt.setText(typingMessage.getMessage());
                            chat_edit_txt.setSelection(typingMessage.getMessage().length());
                        } else {
                            chat_edit_txt.setText("");
                        }
                    } else {
                        chat_edit_txt.setText("");
                    }
                }
            } else {

                chat_edit_txt.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateUserList(final List<ActiveChat> activeChatList) {

        try {
        clearSmartReplies();
       // getSiteAssetsApi();
               if(current_site_id!=null && !current_site_id.isEmpty())
                updateSiteAssetDataWithLocalDb(Integer.parseInt(current_site_id));
        isTabChanged = true;
            saveTypingMessage();
        activeChatPosition=-1;
        if (activeChatList != null && activeChatList.size() > 0) {

            if (activeChatArrayList == null) {
                activeChatArrayList = new ArrayList<>();
            }
            activeChatArrayList = activeChatList;
            topAdapter.setList(activeChatList);
            topAdapter.notifyDataSetChanged();
            noDataImage.setVisibility(View.GONE);
            usersLayout.setVisibility(View.VISIBLE);
            reply_message_layout.setVisibility(View.GONE);
            isReplyMessage=false;
            if(!isInfoClicked && !isContactCliked)
            {
                chatRecyclerView.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
            }

            Helper.getInstance().LogDetails("updateUserList", activeChatList.size() + " " + activeChatArrayList.size());
            checkUserStatus();
            updateChatList(activeChatArrayList.get(0), 0);


        } else {
            Helper.getInstance().LogDetails("updateUserList", "else called");
            updateReadMessageStatus();
            Helper.getInstance().closeKeyBoard(ChatActivity.this,chat_edit_txt);
            activeChatArrayList = activeChatList;
            list.clear();
            activeChat = null;
            receiver_id = "0";
            chat_id = "0";
            //agent_id = "0";
            lastMessageId = "0";
            conversation_reference_id = "";
            info_layout.setVisibility(View.GONE);
            contact_layout.setVisibility(View.GONE);
            noDataImage.setVisibility(View.VISIBLE);
            usersLayout.setVisibility(View.GONE);
            chatRecyclerView.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            reply_message_layout.setVisibility(View.GONE);
            isReplyMessage=false;
            /*if(isTabChanged)
            {
                adapter.setList(agentsList);
            }*/
            adapter.notifyDataSetChanged();
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });*/
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadAttachment(String path, String devicePath) {

        Helper.getInstance().LogDetails("loadAttachment", devicePath + " " + path);

        if (devicePath != null && !devicePath.trim().isEmpty()) {
            mainLayout.setVisibility(View.GONE);
            attachmentViewLayout.setVisibility(View.VISIBLE);
            File imgFile = new File(devicePath);
            if (imgFile.exists()) {


                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (myBitmap != null) {

                    image_preview.setImageBitmap(myBitmap);
                } else {

                }


            } else {

            }
        } else if (path != null && !path.trim().isEmpty()) {
            mainLayout.setVisibility(View.GONE);
            attachmentViewLayout.setVisibility(View.VISIBLE);

            path = path.replace("\"", "");
            Helper.getInstance().LogDetails("loadAttachment", path);
            Glide.with(ChatActivity.this)
                    .load(path)
                    .into(image_preview);


        }
    }

    public void updateDownloadFileStatus(int messageId, int senderId, int receiverId, String conversation_reference_id, String path) {
        try {
            if (conversationTable == null) {
                conversationTable = new ConversationTable(ChatActivity.this);
            }
            conversationTable.updateDownloadFileStatus(messageId, senderId, receiverId, conversation_reference_id, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void askStoragePermission(int id, int messageId) {

        downloadAtachMentMessageId = messageId;
        requestAppPermissions(Values.Permissions.STORAGE_PERMISSIONS_LIST, Values.Permissions.STORAGE_PERMISSION);
    }

    public void requestAppPermissions(String[] permissions, int REQUEST_COIDE) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_COIDE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try{
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted!

            switch (requestCode) {
                case Values.Permissions.CAll_PERMISSION:
                    if(activeChat!=null && activeChat.getMobile()!=null && !activeChat.getMobile().trim().isEmpty()){
                        makeCall(activeChat.getMobile());
                    }

                    break;
                case Values.Permissions.STORAGE_PERMISSION:
                    try {
                        if (adapter != null) {
                            if (downloadAtachMentMessageId != 0) {
                                adapter.dowloadTheFileAfterGivenStoragePermission(downloadAtachMentMessageId);
                                downloadAtachMentMessageId = 0;
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (screenshotClicked) {
                        screenshotClicked=false;
                        takeScreenshot();
                    }

                    break;
                case Values.Permissions.CAMERA_AND_STORAGE_PERMISSION:
                    if (checkAndRequestPermissions()) {
                        openAttachmentOptionsDialog();
                    }
                    break;

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void takeScreenshot() {

        try{

        Helper.getInstance().LogDetails("takeScreenshot", "called");
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        long currentTime = SystemClock.elapsedRealtime();

        try {

            // String mPath = Environment.getExternalStorageDirectory().toString() + "/" + currentTime + ".jpg";

            String mPath = "";

            String root = Environment.getExternalStorageDirectory().toString();
            Log.e("root", root);
            File myDir = new File(root + "/ClickToMagic");

            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String name = System.currentTimeMillis() + ".jpeg";
            mPath = root + "/ClickToMagic/" + name;
            // create bitmap screen capture
            View v1 = infoDialog.getWindow().getDecorView().getRootView();
           // View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            if (imageFile != null) {

                Toast.makeText(ChatActivity.this, "Screenshot Saved.", Toast.LENGTH_LONG).show();
                Helper.getInstance().LogDetails("takeScreenshot", "exists" + mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                addImageToGallery(mPath, ChatActivity.this);
                openScreenshot(imageFile);
            } else {
                Helper.getInstance().LogDetails("takeScreenshot", "not exists");
            }


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Helper.getInstance().LogDetails("takeScreenshot", "excption " + e.getMessage() + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        try{
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = AppFileProvider.getUriForFile(ChatActivity.this, "com.tvisha.click2magic.fileprovider", imageFile);
        } else {
            uri = Uri.fromFile(imageFile);
        }
        intent.setDataAndType(uri, "image/jpeg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void disconnectTmSocket() {
        AppSocket application = (AppSocket) getApplication();
        application.disconnectTmSocket();
        tmSocket = null;
    }

    private void progressDialog() {
        try {

            if (!(ChatActivity.this).isFinishing()) {
                dialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_progress_bar);
                if (Helper.getConnectivityStatus(ChatActivity.this)) {
              /*  if(dialog != null  && !dialog.isShowing())
                {
                    Helper.getInstance().LogDetails("progressDialog","open called");
                    dialog.show();
                }*/
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
      //  progressBar=findViewById(R.id.progressBar);

    }

    private void openProgess() {

        try{
            if (!(ChatActivity.this).isFinishing()) {
                if (dialog != null && !dialog.isShowing()) {
                    Helper.getInstance().LogDetails("progressDialog openProgess", "called");
                    dialog.show();
                }
            }
        }catch (Exception e)
        {

        }


       /*if(progressBar!=null ){
           progressBar.setVisibility(View.VISIBLE);
       }*/
    }

    private void closeProgress() {

        try {
            if (!(ChatActivity.this).isFinishing()) {
                if (dialog != null && dialog.isShowing()) {
                    Helper.getInstance().LogDetails("progressDialog closeProgress", "called");
                    dialog.cancel();
                }
            }

        }catch (Exception e){

        }


       /* if(progressBar!=null ){
            progressBar.setVisibility(View.GONE);
        }*/
    }

    private String getDateStartTime(String date) throws ParseException {


        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        startMin = c.get(Calendar.MINUTE);
        startHour = c.get(Calendar.HOUR);
        START_AM_PM = c.get(Calendar.AM_PM);

        return sdf1.format(c.getTime());
    }



    private void openMoreOptionsDialg() {

        try {
            Helper.getInstance().LogDetails("more_icon","openMoreOptionsDialg clicked");
            moreOptionsDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            moreOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            moreOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            moreOptionsDialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(moreOptionsDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            moreOptionsDialog.getWindow().setAttributes(lp);
            moreOptionsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            moreOptionsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            moreOptionsDialog.setContentView(R.layout.more_options_new_layout);

            ImageView close_icon,send_icon1;
            RelativeLayout rl_view;
            final EditText chat_edit_txt1;
            rl_view= moreOptionsDialog.findViewById(R.id.rl_view);
            close_icon= moreOptionsDialog.findViewById(R.id.close_icon);
            send_icon1= moreOptionsDialog.findViewById(R.id.send_icon1);
            chat_edit_txt1= moreOptionsDialog.findViewById(R.id.chat_edit_txt1);

            collateralLayout = moreOptionsDialog. findViewById(R.id.collateralLayout);
            linkslLayout = moreOptionsDialog.findViewById(R.id.linkslLayout);
            contactLayout = moreOptionsDialog.findViewById(R.id.contactLayout);
            attachmentLayout = moreOptionsDialog.findViewById(R.id.attachmentLayout);
            shareLayout = moreOptionsDialog.findViewById(R.id.shareLayout);
            transferLayout = moreOptionsDialog.findViewById(R.id.transferLayout);
            screenshotLayout = moreOptionsDialog.findViewById(R.id.screenshotLayout);
            cannedMsglLayout = moreOptionsDialog. findViewById(R.id.cannedMsglLayout);
            infoLayout = moreOptionsDialog. findViewById(R.id.infoLayout);
            chatLayout = moreOptionsDialog. findViewById(R.id.chatLayout);
            endChatLayout = moreOptionsDialog. findViewById(R.id.endChatLayout);

            if(isSelf)

            endChatLayout.setOnClickListener(this);
            chatLayout.setOnClickListener(this);
            infoLayout.setOnClickListener(this);
            screenshotLayout.setOnClickListener(this);
            cannedMsglLayout.setOnClickListener(this);
            collateralLayout.setOnClickListener(this);
            linkslLayout.setOnClickListener(this);
            attachmentLayout.setOnClickListener(this);
            contactLayout.setOnClickListener(this);
            shareLayout.setOnClickListener(this);

            if(!isSelf || (isSelf && ( role!=null && !role.trim().isEmpty() && Integer.parseInt(role)==Values.UserRoles.MODERATOR )) ||  (activeChat!=null && activeChat.getChatStatus()!=null && activeChat.getChatStatus().equals("2")) ){

                send_icon1.setVisibility(View.GONE);
                chat_edit_txt1.setHint("You can't send messages");
                chat_edit_txt1.setEnabled(false);
                if(  (activeChat!=null && activeChat.getChatStatus()!=null && activeChat.getChatStatus().equals("2")) && isSelf){
                    endChatLayout.setClickable(true);
                    shareLayout.setClickable(true);
                    endChatLayout.setAlpha(1.0f);
                    shareLayout.setAlpha(1.0f);
                }
                else
                {
                    endChatLayout.setClickable(false);
                    endChatLayout.setAlpha(0.4f);
                }

                cannedMsglLayout.setClickable(false);
                collateralLayout.setClickable(false);
                linkslLayout.setClickable(false);
                contactLayout.setClickable(false);
                attachmentLayout.setClickable(false);



                cannedMsglLayout.setAlpha(0.5f);
                collateralLayout.setAlpha(0.5f);
                linkslLayout.setAlpha(0.5f);
                contactLayout.setAlpha(0.5f);
                attachmentLayout.setAlpha(0.5f);

            }

            rl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreOptionsDialog.cancel();

                }
            });

            close_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreOptionsDialog.cancel();
                }
            });

            chat_edit_txt1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    moreOptionsDialog.cancel();

                    return false;
                }
            });

            moreOptionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(isSelf)
                    {
                        if(info_layout.getVisibility()!=View.VISIBLE && contact_layout.getVisibility()!=View.VISIBLE)
                        {
                           // chat_edit_txt.requestFocus();
                           // Utilities.openKeyboard(ChatActivity.this, chat_edit_txt);
                        }

                    }

                }
            });

            moreOptionsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        try{
            switch (view.getId()) {

                case R.id.attachmentLayout:
                    closeMoreOtionsDialog();

                    if (!isAwsApiCalled) {
                        if (Helper.getConnectivityStatus(ChatActivity.this)) {
                            isAttachmentClicked=true;
                            callGetAwsConfig();
                        } else {
                            // Toast.makeText(ChatActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                        }
                        if (checkAndRequestPermissions()) {
                            // picImage();
                            openAttachmentOptionsDialog();
                        } else {
                            Helper.getInstance().LogDetails("picImage", "not called");
                        }
                    } else {
                        if (checkAndRequestPermissions()) {
                            // picImage();
                            openAttachmentOptionsDialog();
                        } else {
                            Helper.getInstance().LogDetails("picImage", "not called");
                        }
                    }
                    break;

                case R.id.endChatLayout:
                    closeMoreOtionsDialog();
                    if(categoryArrayList==null || categoryArrayList.size()==0)
                        categoryArrayList.addAll(categoriesTable.getCategoriesList());
                    if(categoryArrayList!=null && categoryArrayList.size()>0)
                        FeedBackDialogBox(true,activeChat);
                    else
                        getFeedBackCategory(true,activeChat);

                    // showChatEndConfirmationDialog();
                    break;
                case R.id.infoLayout:
                    closeMoreOtionsDialog();
                    //showCustomerInfo();
                    showCustomerInfoDialog();
                    break;
                case R.id.contactLayout:
                    closeMoreOtionsDialog();
                    showContactInfo();
                    break;
                case R.id.chatLayout:
                    bottom_lay_card.setVisibility(View.VISIBLE);
                    moreLayoutLine.setVisibility(View.GONE);
                    break;
                case R.id.cannedMsglLayout:
                    closeMoreOtionsDialog();
                    openCannedMessageDialog();
                    break;
                case R.id.collateralLayout:
                    closeMoreOtionsDialog();
                    openCollateralLinkMessageDialog(0);
                    break;
                case R.id.linkslLayout:
                    closeMoreOtionsDialog();
                    openCollateralLinkMessageDialog(1);
                    break;
                case R.id.screenshotLayout:
                    screenshotClicked = true;
                    if (verifyStoragePermissions(ChatActivity.this)) {
                        takeScreenshot();
                    }
                    break;
                case R.id.shareLayout:
                    closeMoreOtionsDialog();
                   openShareOptionsDialog();
                    break;


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private String createPdf(){
        String filePath="";
        try {
            String pdfName = activeChat.getGuestName()+"_"+String.valueOf(new Date().getTime());

            String root = Environment.getExternalStorageDirectory().toString();
            //String root = Environment.getDownloadCacheDirectory().toString();
            Log.e("root", root);
            File myDir = new File(root + "/Click2Magic");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String name = "/" + pdfName + ".pdf";
            filePath = root + "/Click2Magic" + name;
            // write the document content
            File file = new File(filePath);

            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(file.getPath()));

          // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Click 2 Magic");
            document.addCreator("C2M");

            //trial
            document.setMarginMirroring(true);



            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            BaseColor mainTitle = new BaseColor(47, 16, 173, 255);
            BaseColor senderNameColor = new BaseColor(113, 48, 75, 255);
            BaseColor receiverNameColor = new BaseColor(216, 64, 218, 255);
            BaseColor senderMessageColor = new BaseColor(216, 117, 15, 255);
            BaseColor senderMessageBgColor = new BaseColor(230, 227, 224, 255);
            BaseColor linkMessageColor = new BaseColor(43, 69, 202, 255);
            BaseColor linkMessageBgColor = new BaseColor(245, 242, 242, 255);
            BaseColor receiverMessageColor = new BaseColor(0, 153, 204, 255);
            BaseColor receiverMessageBgColor = new BaseColor(249, 238, 238, 255);
            float titleFontSize = 36.0f;
            float headingFontSize = 26.0f;
            float mValueFontSize = 20.0f;
              /**
             * How to USE FONT....
               */
            BaseFont title = BaseFont.createFont("assets/fonts/Poppins-SemiBold.ttf", "UTF-8", BaseFont.EMBEDDED);
            BaseFont subTitle = BaseFont.createFont("assets/fonts/Poppins-Medium.ttf", "UTF-8", BaseFont.EMBEDDED);
            BaseFont body = BaseFont.createFont("assets/fonts/Poppins-Regular.ttf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 100));


            // Title Order Details...
            // Adding Title....
            Font titleFont = new Font(title, titleFontSize, Font.NORMAL, mainTitle);
            Font receiverNameFont = new Font(subTitle, headingFontSize, Font.NORMAL, receiverNameColor);
            Font senderNameFont = new Font(subTitle, headingFontSize, Font.NORMAL, senderNameColor);
            Font senderMessageFont = new Font(body, mValueFontSize, Font.NORMAL, senderMessageColor);
            Font receiverMessageFont = new Font(body, mValueFontSize, Font.NORMAL, receiverMessageColor);
            Font linkMessageFont = new Font(body, mValueFontSize, Font.NORMAL, linkMessageColor);


           // Creating Chunk
            Chunk titleChunk = new Chunk("Conversation", titleFont);
           // Creating Paragraph to add...
            Paragraph titleParagraph = new Paragraph(titleChunk);
           // Setting Alignment for Heading
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
          // Finally Adding that Chunk
            document.add(titleParagraph);


            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));



            // Fields of Order Details...
            // Adding Chunks for Title and value

            Chunk receiverNameChunk = new Chunk("Guest :"+Helper.getInstance().capitalize(activeChat.getGuestName()), receiverNameFont);
            Paragraph receiverParagraph = new Paragraph(receiverNameChunk);
            receiverParagraph.setAlignment(Element.ALIGN_LEFT);
           // document.add(receiverParagraph);



            Chunk senderNameChunk = new Chunk("Agent : "+Helper.getInstance().capitalize(user_name), senderNameFont);

            Paragraph senderParagraph = new Paragraph(senderNameChunk);
            senderParagraph.setAlignment(Element.ALIGN_RIGHT);
            //document.add(senderParagraph);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            //table.addCell(getCell("Text to the left", PdfPCell.ALIGN_LEFT));

            PdfPCell rcell = new PdfPCell(new Phrase(receiverNameChunk));
            rcell.setPaddingTop(10);
            rcell.setHorizontalAlignment( PdfPCell.ALIGN_LEFT);
            rcell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(rcell);

            PdfPCell scell = new PdfPCell(new Phrase(senderNameChunk));
            scell.setPaddingTop(10);
            scell.setHorizontalAlignment( PdfPCell.ALIGN_RIGHT);
            scell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(scell);

            document.add(table);

           /* Paragraph para = new Paragraph(new Chunk());
            document.add(para);*/

            Chunk spaceChunck = new Chunk(" ", senderMessageFont);
            Paragraph spaceParagraph = new Paragraph(spaceChunck);
            spaceParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(spaceParagraph);


            List<ChatModel>  list=  conversationTable.getLocalMessagesAll(Integer.parseInt(receiver_id),Integer.parseInt(tmUserId),conversation_reference_id);
            if (list != null && list.size() > 0) {
                int id=0;
                for (int i = list.size()-1; i >=0; i--) {

                    ChatModel chatModel = list.get(i);



                    if(id!=0 && id!=chatModel.getSender_id())
                    {
                        if(chatModel.getSender_id()==Integer.parseInt(tmUserId))
                        {
                            document.add(senderParagraph);
                        }
                        else
                        {
                            document.add(receiverParagraph);
                        }
                    }
                    id=chatModel.getSender_id();

                    if (chatModel.getMessage_type() == Values.MessageType.MESSAGE_TYPE_TEXT) {


                        Helper.getInstance().LogDetails("createPdf ","tm ids "+chatModel.getSender_id()+" "+tmUserId);

                        if(chatModel.getSender_id()==Integer.parseInt(tmUserId))
                        {

                            Chunk senderMessageChunk = new Chunk(chatModel.getMessage(), senderMessageFont);
                            senderMessageChunk.setBackground(senderMessageBgColor,10,10,10,10);
                            Paragraph senderMessageParagraph = new Paragraph(senderMessageChunk);
                            senderMessageParagraph.setSpacingAfter(20);
                            senderMessageParagraph.setLeading(4,2);
                            senderMessageParagraph.setAlignment(Element.ALIGN_RIGHT);
                            document.add(senderMessageParagraph);

                        }
                        else
                        {
                            Chunk receiverMessageChunk = new Chunk(chatModel.getMessage(), receiverMessageFont);
                            receiverMessageChunk.setBackground(receiverMessageBgColor,10,10,10,10);
                            Paragraph receiverMessageParagraph = new Paragraph(receiverMessageChunk);
                            receiverMessageParagraph.setSpacingAfter(20);
                            receiverMessageParagraph.setLeading(4,2);
                            receiverMessageParagraph.setAlignment(Element.ALIGN_LEFT);
                            document.add(receiverMessageParagraph);
                        }

                    }else if (chatModel.getMessage_type() == Values.MessageType.MESSAGE_TYPE_ATTACHMENT) {

                        if(chatModel.getSender_id()==Integer.parseInt(tmUserId))
                        {
                            Chunk senderMessageChunk = new Chunk(chatModel.getAttachment(), linkMessageFont);
                            senderMessageChunk.setBackground(linkMessageBgColor,10,10,10,10);
                            senderMessageChunk.setAnchor(new URL(chatModel.getAttachment()));
                            Paragraph senderMessageParagraph = new Paragraph(senderMessageChunk);
                            senderMessageParagraph.setSpacingAfter(20);
                            senderMessageParagraph.setLeading(4,2);
                            senderMessageParagraph.setAlignment(Element.ALIGN_RIGHT);
                            document.add(senderMessageParagraph);
                        }
                        else
                        {
                            Chunk receiverMessageChunk = new Chunk(chatModel.getAttachment(), linkMessageFont);
                            receiverMessageChunk.setBackground(linkMessageBgColor,10,10,10,10);
                            receiverMessageChunk.setAnchor(new URL(chatModel.getAttachment()));
                            Paragraph receiverMessageParagraph = new Paragraph(receiverMessageChunk);
                            receiverMessageParagraph.setSpacingAfter(20);
                            receiverMessageParagraph.setLeading(4,2);
                            receiverMessageParagraph.setAlignment(Element.ALIGN_LEFT);
                            document.add(receiverMessageParagraph);
                        }

                    }


                }
            }



            document.close();

           // viewPdf(filePath);
        }catch (Exception e){
            Helper.getInstance().LogDetails("createPdf ",e.getLocalizedMessage()+" "+e.getMessage());
            e.printStackTrace();
        }
        return filePath;
    }
    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }





    private void openCannedMessageDialog() {

        try {

             cannedMessagedialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            cannedMessagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cannedMessagedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cannedMessagedialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(cannedMessagedialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            cannedMessagedialog.getWindow().setAttributes(lp);
            cannedMessagedialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            cannedMessagedialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            cannedMessagedialog.setContentView(R.layout.canned_messages_layout);


            cancelCannedLayout = cannedMessagedialog.findViewById(R.id.cancelCannedLayout);
            cannedMessageSearchLayout = cannedMessagedialog.findViewById(R.id.cannedMessageSearchLayout);
            canned_message_recyclerView = cannedMessagedialog.findViewById(R.id.canned_message_recyclerView);
            canned_message_search_et = cannedMessagedialog.findViewById(R.id.canned_message_search_et);
            no_data_msg = cannedMessagedialog.findViewById(R.id.no_data_msg);

           /* cancelCannedLayout=findViewById(R.id.cancelCannedLayout);
            canned_message_recyclerView=findViewById(R.id.canned_message_recyclerView);
            canned_message_search_et=findViewById(R.id.canned_message_search_et);*/

            cancelCannedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.getInstance().closeKeyBoard(ChatActivity.this,canned_message_search_et);

                    cannedMessagedialog.cancel();


                }
            });


            if (cannedMessageList == null || cannedMessageList.size() == 0) {
                no_data_msg.setVisibility(View.VISIBLE);
            } else {
                no_data_msg.setVisibility(View.GONE);
            }


            LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
            canned_message_recyclerView.setLayoutManager(layoutManager);
            canned_message_recyclerView.setNestedScrollingEnabled(false);
            cannedMessageAdapter = new CannedMessageAdapter(ChatActivity.this, cannedMessageList);
            cannedMessageAdapter.selectionMode = true;
            canned_message_recyclerView.setAdapter(cannedMessageAdapter);

            if(cannedMessageList!=null && cannedMessageList.size()>0){
                cannedMessageSearchLayout.setVisibility(View.VISIBLE);
                canned_message_search_et.setEnabled(true);

            }
            else
            {
                cannedMessageSearchLayout.setVisibility(View.GONE);
                canned_message_search_et.setEnabled(false);

            }

            canned_message_search_et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    canned_message_search_et.setFocusable(true);
                    canned_message_search_et.requestFocus();
                    canned_message_search_et.setCursorVisible(true);
                    Utilities.openKeyboard(ChatActivity.this, canned_message_search_et);
                    return false;
                }
            });
            canned_message_search_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && s.length() > 0) {
                        cannedMessageAdapter.search(s.toString());
                    } else {
                        cannedMessageAdapter.resetList(cannedMessageList);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            cannedMessagedialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openCollateralLinkMessageDialog(int pos) {

        try {

           collateralDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            collateralDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            collateralDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            collateralDialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(collateralDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            collateralDialog.getWindow().setAttributes(lp);
            collateralDialog.setContentView(R.layout.collateral_message_layout);


            cancelCollateralLayout = collateralDialog.findViewById(R.id.cancelCollateralLayout);
            navigation = collateralDialog.findViewById(R.id.navigation);
            collateral_message_recyclerView = collateralDialog.findViewById(R.id.collateral_message_recyclerView);
            link_message_recyclerView = collateralDialog.findViewById(R.id.link_message_recyclerView);
            image_message_recyclerView = collateralDialog.findViewById(R.id.image_message_recyclerView);
            no_data_msg = collateralDialog.findViewById(R.id.no_data_msg);

            initLinkMenuBar(pos);


            if (pos == 0) {
                collateral_message_recyclerView.setVisibility(View.VISIBLE);
                link_message_recyclerView.setVisibility(View.GONE);
                image_message_recyclerView.setVisibility(View.GONE);
                if (collateralMessageList == null || collateralMessageList.size() == 0) {
                    no_data_msg.setVisibility(View.VISIBLE);
                }
            } else if (pos == 1) {
                link_message_recyclerView.setVisibility(View.VISIBLE);
                collateral_message_recyclerView.setVisibility(View.GONE);
                image_message_recyclerView.setVisibility(View.GONE);
                if (linksMessageList == null || linksMessageList.size() == 0) {
                    no_data_msg.setVisibility(View.VISIBLE);
                }
            } else {
                link_message_recyclerView.setVisibility(View.GONE);
                collateral_message_recyclerView.setVisibility(View.GONE);
                image_message_recyclerView.setVisibility(View.VISIBLE);
                if (imageList == null || imageList.size() == 0) {
                    no_data_msg.setVisibility(View.VISIBLE);
                }
            }


            cancelCollateralLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collateralDialog.cancel();

                }
            });


            Helper.getInstance().LogDetails("getSiteAssetsApi", collateralMessageList.size() + " " + linksMessageList.size() + " " + imageList.size());



            GridLayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 3);
            collateral_message_recyclerView.setLayoutManager(layoutManager);
            collateral_message_recyclerView.setNestedScrollingEnabled(false);
            collateralMessageAdapter = new CollateralMessageAdapter(ChatActivity.this, collateralMessageList);
            collateralMessageAdapter.selectionMode = true;
            collateral_message_recyclerView.setAdapter(collateralMessageAdapter);


            GridLayoutManager gridLayoutManager = new GridLayoutManager(ChatActivity.this, 3);
            image_message_recyclerView.setLayoutManager(gridLayoutManager);
            image_message_recyclerView.setNestedScrollingEnabled(false);
            imageMessageAdapter = new ImageMessageAdapter(ChatActivity.this, imageList);
            imageMessageAdapter.selectionMode = true;
            image_message_recyclerView.setAdapter(imageMessageAdapter);

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
            link_message_recyclerView.setLayoutManager(layoutManager1);
            link_message_recyclerView.setNestedScrollingEnabled(false);
            linkMessageAdapter = new LinkMessageAdapter(ChatActivity.this, linksMessageList);
            linkMessageAdapter.selectionMode = true;
            link_message_recyclerView.setAdapter(linkMessageAdapter);


            collateralDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initLinkMenuBar(int pos) {
        try{

        for (int i = 0; i < navLabels.length; i++) {

            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.link_nav_tab, null);
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);


            tab_label.setText(navLabels[i]);

            if (i == pos) {
                // tab_label.setTextColor(getResources().getColor(R.color.tab_active_color));
                //tab_icon.setImageResource(navIconsActive[i]);

                tab_label.setTextColor(getResources().getColor(R.color.linkTabActiveColor));


            } else {
                tab_label.setTextColor(getResources().getColor(R.color.linkTabInActiveColor));

            }

            navigation.addTab(navigation.newTab().setCustomView(tab));
        }


        navigation.addOnTabSelectedListener(this);
        navigation.addOnTabSelectedListener(ChatActivity.this);

        TabLayout.Tab tab = navigation.getTabAt(pos);
        tab.select();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View tabView = tab.getCustomView();
        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
        tab_label.setTextColor(getResources().getColor(R.color.linkTabActiveColor));

        changeTabColor(tab.getPosition());
        setFragment(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View tabView = tab.getCustomView();
        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
        tab_label.setTextColor(getResources().getColor(R.color.linkTabInActiveColor));

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void setFragment(int pos) {
        try{
        if (pos == 0) {
            collateral_message_recyclerView.setVisibility(View.VISIBLE);
            link_message_recyclerView.setVisibility(View.GONE);
            image_message_recyclerView.setVisibility(View.GONE);

            if (collateralMessageList == null || collateralMessageList.size() == 0) {
                no_data_msg.setVisibility(View.VISIBLE);
            } else {
                no_data_msg.setVisibility(View.GONE);
            }
        } else if (pos == 1) {
            link_message_recyclerView.setVisibility(View.VISIBLE);
            collateral_message_recyclerView.setVisibility(View.GONE);
            image_message_recyclerView.setVisibility(View.GONE);
            if (linksMessageList == null || linksMessageList.size() == 0) {
                no_data_msg.setVisibility(View.VISIBLE);
            } else {
                no_data_msg.setVisibility(View.GONE);
            }
        } else {
            link_message_recyclerView.setVisibility(View.GONE);
            collateral_message_recyclerView.setVisibility(View.GONE);
            image_message_recyclerView.setVisibility(View.VISIBLE);

            if (imageList == null || imageList.size() == 0) {
                no_data_msg.setVisibility(View.VISIBLE);
            } else {
                no_data_msg.setVisibility(View.GONE);
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeTabColor(int pos) {
        for (int i = 0; i < navLabels.length; i++) {

            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.link_nav_tab, null);
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);

            tab_label.setText(navLabels[i]);

            if (i == pos) {
                tab_label.setTextColor(getResources().getColor(R.color.linkTabActiveColor));


            } else {
                tab_label.setTextColor(getResources().getColor(R.color.linkTabInActiveColor));

            }
        }
    }


    private void getSiteAssetsApi() {

        try{

        Helper.getInstance().LogDetails("getSiteAssetsApi", ApiEndPoint.token + " " + company_token + " " + user_id);
        if (Utilities.getConnectivityStatus(ChatActivity.this) <= 0) {
            Helper.getInstance().pushToast(ChatActivity.this, "Please check your network connection...");
            updateSiteAssetData(null);
            return;
        }
        //  openProgess();
        Call<SiteAssetsResponse> call = c2mApiService.getSiteAssets(ApiEndPoint.token, company_token, user_id);
        call.enqueue(new Callback<SiteAssetsResponse>() {
            @Override
            public void onResponse(Call<SiteAssetsResponse> call, Response<SiteAssetsResponse> response) {
                SiteAssetsResponse apiResponse = response.body();
                //closeProgress();
                if (apiResponse != null) {
                    if (apiResponse.isSuccess()) {

                        s3Url = apiResponse.getS3url();

                        Helper.getInstance().LogDetails("getSiteAssetsApi", "success"+apiResponse.toString());

                        if (apiResponse.getData() != null) {

                            updateSiteAssetData(apiResponse.getData());

                        } else {
                            updateSiteAssetData(null);
                            Helper.getInstance().LogDetails("getSiteAssetsApi", "data empty");
                        }
                    } else {
                        updateSiteAssetData(null);
                        Helper.getInstance().LogDetails("getSiteAssetsApi", "false");
                    }

                } else {
                    updateSiteAssetData(null);
                    Helper.getInstance().LogDetails("getSiteAssetsApi", "null");
                }

            }

            @Override
            public void onFailure(Call<SiteAssetsResponse> call, Throwable t) {
                updateSiteAssetData(null);
                Helper.getInstance().LogDetails("getSiteAssetsApi", "exeption " + t.getLocalizedMessage() + " " + t.getMessage());
                //closeProgress();
            }

        });
        }catch (Exception e){
            updateSiteAssetData(null);
            e.printStackTrace();
        }

    }

    private void updateSiteAssetDataWithLocalDb(int site_id){
        try{

            Helper.getInstance().LogDetails("updateSiteAssetDataWithLocalDb ","called "+site_id);

            if (linksMessageList != null && linksMessageList.size() > 0) {
                linksMessageList.clear();
            }
            if (collateralMessageList != null && collateralMessageList.size() > 0) {
                collateralMessageList.clear();
            }

            if (cannedMessageList != null && cannedMessageList.size() > 0) {
                cannedMessageList.clear();
            }

            if (imageList != null && imageList.size() > 0) {
                imageList.clear();
            }


            if (linksTempMessageList != null && linksTempMessageList.size() > 0) {
                linksTempMessageList.clear();
            }
            if (collateralTempMessageList != null && collateralTempMessageList.size() > 0) {
                collateralTempMessageList.clear();
            }

            if (cannedTempMessageList != null && cannedTempMessageList.size() > 0) {
                cannedTempMessageList.clear();
            }

            if (imageTempList != null && imageTempList.size() > 0) {
                imageTempList.clear();
            }

            SiteAssetsTable siteAssetsTable=new SiteAssetsTable(ChatActivity.this);


            linksTempMessageList = siteAssetsTable.getLinkAssetList(site_id);
            collateralTempMessageList = siteAssetsTable.getCollateralAssetList(site_id);
            cannedTempMessageList =siteAssetsTable.getCannedAssetList(site_id);
            imageTempList = siteAssetsTable.getImageAssetList(site_id);

            List<Link> linkList=new ArrayList<>();
            if(linksTempMessageList!=null && linksTempMessageList.size()>0){
                linkList.addAll(linksTempMessageList);

            }
            linksMessageList=linkList;

            List<Collateral> collateralList=new ArrayList<>();
            if(collateralTempMessageList !=null && collateralTempMessageList.size()>0)
            {
                collateralList.addAll(collateralTempMessageList);
            }
            collateralMessageList=collateralList;


            List<CannedResponse> cannedResponseList=new ArrayList<>();
            if(cannedTempMessageList!=null && cannedTempMessageList.size()>0){
                cannedResponseList.addAll(cannedTempMessageList);
            }
            cannedMessageList=cannedResponseList;

            List<Image> images=new ArrayList<>();
            if(imageTempList!=null && imageTempList.size()>0){
                images.addAll(imageTempList);
            }
            imageList=images;

            Helper.getInstance().LogDetails("updateSiteAssetDataWithLocalDb ","called "+site_id +linksMessageList.size()+" "+collateralMessageList.size()+" "+cannedMessageList.size()+" "+imageList.size());


        }catch (Exception e){

        }
    }

    private void updateSiteAssetData(SiteAssetData data) {

        try{

        if (data != null) {

            if (linksMessageList != null && linksMessageList.size() > 0) {
                linksMessageList.clear();
            }
            if (collateralMessageList != null && collateralMessageList.size() > 0) {
                collateralMessageList.clear();
            }

            if (cannedMessageList != null && cannedMessageList.size() > 0) {
                cannedMessageList.clear();
            }

            if (imageList != null && imageList.size() > 0) {
                imageList.clear();
            }


            if (linksTempMessageList != null && linksTempMessageList.size() > 0) {
                linksTempMessageList.clear();
            }
            if (collateralTempMessageList != null && collateralTempMessageList.size() > 0) {
                collateralTempMessageList.clear();
            }

            if (cannedTempMessageList != null && cannedTempMessageList.size() > 0) {
                cannedTempMessageList.clear();
            }

            if (imageTempList != null && imageTempList.size() > 0) {
                imageTempList.clear();
            }


            linksTempMessageList = data.getLinks();
            collateralTempMessageList = data.getCollateral();
            cannedTempMessageList = data.getCannedResponses();
            imageTempList = data.getImages();




            Helper.getInstance().LogDetails("getSiteAssetsApi","");




            Helper.getInstance().LogDetails("getSiteAssetsApi", "size " +current_site_id+ " --- " + cannedTempMessageList.size() + " " + collateralTempMessageList.size() + " " + linksTempMessageList.size() +"  "+ imageTempList.size());
            List<Link> linkList=new ArrayList<>();
            if (linksTempMessageList != null && linksTempMessageList.size() > 0) {
                if (current_site_id != null && !current_site_id.trim().isEmpty()) {
                    for (int i = 0; i < linksTempMessageList.size(); i++) {
                        String siteId = linksTempMessageList.get(i).getSiteId();
                        if (siteId != null && !siteId.trim().isEmpty()) {
                            if (current_site_id.trim().equals(siteId.trim())) {
                               linkList.add(linksTempMessageList.get(i));
                            }
                        }
                    }
                    Helper.getInstance().LogDetails("getSiteAssetsApi","linksTempMessageList "+linksTempMessageList.get(0).getType());
                    linksMessageList=linkList;
                }
            }

            List<Collateral> collateralList=new ArrayList<>();

            if (collateralTempMessageList != null && collateralTempMessageList.size() > 0) {
                if (current_site_id != null && !current_site_id.trim().isEmpty()) {
                    for (int i = 0; i < collateralTempMessageList.size(); i++) {
                        String siteId = collateralTempMessageList.get(i).getSiteId();
                        Helper.getInstance().LogDetails("getSiteAssetsApi", siteId + " ***" + current_site_id);
                        if (siteId != null && !siteId.trim().isEmpty()) {
                            if (current_site_id.trim().equals(siteId.trim())) {
                                Helper.getInstance().LogDetails("getSiteAssetsApi", siteId + " ===" + current_site_id);
                                collateralList.add(collateralTempMessageList.get(i));

                            }
                        }
                    }
                    Helper.getInstance().LogDetails("getSiteAssetsApi","collateralList "+collateralTempMessageList.get(0).getType());
                    collateralMessageList=collateralList;
                }
            }

            List<CannedResponse> cannedResponseList=new ArrayList<>();


            if (cannedTempMessageList != null && cannedTempMessageList.size() > 0) {
                if (current_site_id != null && !current_site_id.trim().isEmpty()) {
                    for (int i = 0; i < cannedTempMessageList.size(); i++) {

                        String currentString = cannedTempMessageList.get(i).getSiteId();
                        String[] separated = currentString.split(",");
                        if(separated!=null && separated.length>0)
                        {
                            for(int j=0;j<separated.length;j++){
                                String siteId = separated[j];
                                if (siteId != null && !siteId.trim().isEmpty()) {
                                    if (current_site_id.trim().equals(siteId.trim())) {
                                        cannedResponseList.add(cannedTempMessageList.get(i));
                                    }
                                }
                            }
                        }

                    }
                    Helper.getInstance().LogDetails("getSiteAssetsApi","cannedResponseList "+cannedTempMessageList.get(0).getType());
                    cannedMessageList=cannedResponseList;
                }
            }

            List<Image> images=new ArrayList<>();


            if (imageTempList != null && imageTempList.size() > 0) {
                if (current_site_id != null && !current_site_id.trim().isEmpty()) {
                    for (int i = 0; i < imageTempList.size(); i++) {
                        String siteId = imageTempList.get(i).getSiteId();
                        if (siteId != null && !siteId.trim().isEmpty()) {
                            if (current_site_id.trim().equals(siteId.trim())) {
                                images.add(imageTempList.get(i));
                            }
                        }
                    }
                    Helper.getInstance().LogDetails("getSiteAssetsApi","images "+imageTempList.get(0).getType());
                    imageList=images;
                }
            }

            Helper.getInstance().LogDetails("getSiteAssetsApi", "size after"+ " " + cannedMessageList.size() + " " + collateralMessageList.size() + " "  + linksMessageList.size() +" "+ imageList.size());
        }
        else
        {
            if (linksMessageList != null && linksMessageList.size() > 0) {
                linksMessageList.clear();
            }
            if (collateralMessageList != null && collateralMessageList.size() > 0) {
                collateralMessageList.clear();
            }

            if (cannedMessageList != null && cannedMessageList.size() > 0) {
                cannedMessageList.clear();
            }

            if (imageList != null && imageList.size() > 0) {
                imageList.clear();
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openShareOptionsDialog() {

        try {
             shareOptionsDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            shareOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shareOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shareOptionsDialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(shareOptionsDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            shareOptionsDialog.getWindow().setAttributes(lp);
            shareOptionsDialog.setContentView(R.layout.share_options_dialog);

            final TextView contact_detatils_tv, conversation_details_tv;
            View line;



            contact_detatils_tv = shareOptionsDialog.findViewById(R.id.contact_detatils_tv);
            conversation_details_tv = shareOptionsDialog.findViewById(R.id.conversation_details_tv);


            contact_detatils_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAgentContactDetails();
                    shareOptionsDialog.cancel();
                }
            });

            conversation_details_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filePath=createPdf();
                    if(filePath!=null && !filePath.trim().isEmpty())
                    {
                        sharePdf(filePath);
                    }

                    shareOptionsDialog.cancel();
                }
            });


            shareOptionsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //  isInfoClicked=false;
                    // chat_layout.setVisibility(View.VISIBLE);

                }
            });

            shareOptionsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setAgentContactDetails(){

        String email="",phone="",name="",text="";
        email=activeChat.getEmail();
        phone=activeChat.getMobile();
        name=activeChat.getGuestName();



         text="Site Name : "+current_site_name+" \n"+"Name : "+name+" \n";
         if(email!=null && !email.trim().isEmpty())
         {
             text=text+"Email : "+email+" \n";
         }

        if(phone!=null && !phone.trim().isEmpty())
        {
            text=text+"Phone Number : "+phone+" \n";
        }

        Helper.getInstance().LogDetails("setAgentContactDetails ",text);


        shareContactDetails(text);
    }
    private void shareContactDetails(String text)
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contact Details");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Click 2 Magic"));
    }

    private void sharePdf(String filePath){

        try{

            File outputFile = new File(filePath);
            // Uri uri = Uri.fromFile(outputFile);

            Uri uri = FileProvider.getUriForFile(
                    ChatActivity.this,
                    "com.tvisha.click2magic.fileprovider", //(use your app signature + ".provider" )
                    outputFile);

            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.setType("application/pdf");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sharingIntent, "Click 2 Magic"));
        }catch (Exception e){
            Helper.getInstance().LogDetails("sharePdf ",e.getLocalizedMessage()+" "+e.getCause());
            e.printStackTrace();
        }

    }
    private void viewPdf(String file) {

        try {

            File pdfFile = new File(file);
            Uri uri = FileProvider.getUriForFile(
                    ChatActivity.this,
                    "com.tvisha.click2magic.fileprovider", //(use your app signature + ".provider" )
                    pdfFile);

            // Setting the intent for pdf reader
            Helper.getInstance().LogDetails("viewPdf path",file+" == "+uri);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent intent = Intent.createChooser(pdfIntent, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ChatActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                Helper.getInstance().LogDetails("viewPdf exc", e.getLocalizedMessage() + " " + e.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            Helper.getInstance().LogDetails("viewPdf exc 1", e.getLocalizedMessage() + " " + e.getMessage());
        }
    }



    // Method for opening a pdf file

    private void openAttachmentOptionsDialog() {

        try {
            attachmentOptionsDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            attachmentOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            attachmentOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            attachmentOptionsDialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(attachmentOptionsDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            //  lp.windowAnimations = R.style.slide_left_right;
            attachmentOptionsDialog.getWindow().setAttributes(lp);
            attachmentOptionsDialog.setContentView(R.layout.attachment_options_layout);

            final TextView camera, gallary, doc, video;
            View line;

            gallary = attachmentOptionsDialog.findViewById(R.id.gallary);
            doc = attachmentOptionsDialog.findViewById(R.id.doc);
            camera = attachmentOptionsDialog.findViewById(R.id.camera);
            video = attachmentOptionsDialog.findViewById(R.id.video);


            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions()) {
                        camera();
                    }
                    attachmentOptionsDialog.cancel();
                }
            });

            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions()) {
                        video();
                    }
                    attachmentOptionsDialog.cancel();
                }
            });

          /*  case R.id.close_icon:
                showChatEndConfirmationDialog();
                break;
            case R.id.info_icon:
                showCustomerInfo();
                break;*/

            doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachmentOptionsDialog.cancel();
                    Navigation.getInstance().openDocsActivity(ChatActivity.this);
                    // doc();


                }
            });

            gallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions()) {
                        gallery();
                    }
                    attachmentOptionsDialog.cancel();

                }
            });


            attachmentOptionsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //  isInfoClicked=false;
                    // chat_layout.setVisibility(View.VISIBLE);
                }
            });

            attachmentOptionsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Apps");
        selectedFileuri = ChatActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        List<Intent> yourIntentsList = new ArrayList<Intent>();
        CamIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CamIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedFileuri);
        Intent chooser = Intent.createChooser(CamIntent, "Choose App");
        startActivityForResult(chooser, Values.MyActionsRequestCode.CAMERA_IMAGE);
    }

    private void doc() {

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(intent, Values.MyActionsRequestCode.ATTACHMENT_DOC);

    }

    private void video() {

        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("video/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, Values.MyActionsRequestCode.ATTACHMENT_VIDEO);
        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Values.MyActionsRequestCode.ATTACHMENT_VIDEO);
        }

    }

    private void gallery() {
        GalIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        GalIntent.setType("image/*");
        Intent chooser = Intent.createChooser(GalIntent, "Choose App");
        startActivityForResult(chooser, Values.MyActionsRequestCode.GALLERY_IMAGE);
    }

    public boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA);

        int writeStorage = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    listPermissionsNeeded.toArray(new String[0]),
                    Values.Permissions.CAMERA_AND_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    private void callGetAwsConfig() {

        try {

            Helper.getInstance().LogDetails("callGetAwsConfig", ApiEndPoint.token + " " + company_token + " " + user_id);

            // openProgess();
            if(c2mApiService==null){
                c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);
            }
            Call<GetAwsConfigResponse> call = c2mApiService.getAwsConfig(ApiEndPoint.token, company_token, user_id);
            call.enqueue(new Callback<GetAwsConfigResponse>() {
                @Override
                public void onResponse(Call<GetAwsConfigResponse> call, Response<GetAwsConfigResponse> response) {
                    GetAwsConfigResponse apiResponse = response.body();
                   // closeProgress();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {
                            Helper.getInstance().LogDetails("callGetAwsConfig", "success   ");
                            isAwsApiCalled = true;
                            decryptData(apiResponse);
                            if(isAttachmentClicked){
                                isAttachmentClicked=false;
                                if (checkAndRequestPermissions()) {
                                    // picImage();
                                    openAttachmentOptionsDialog();
                                } else {
                                    Helper.getInstance().LogDetails("picImage", "not called");
                                }
                            }

                        }
                        else
                        {
                            Helper.getInstance().LogDetails("callGetAwsConfig", "not success   ");
                        }

                    }

                }

                @Override
                public void onFailure(Call<GetAwsConfigResponse> call, Throwable t) {
                    Helper.getInstance().LogDetails("callGetAwsConfig", "   "+t.getLocalizedMessage());
                   // closeProgress();
                }

            });
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
                   /*{"key":"AKIA5QVMZCPI35BI5JF5",
                                    "secret":"BJy9p\/Qy7Qo91HyRN8GpC+LnOGo6a09AS362oG\/M",
                                    "region":"us-east-1","endpoint":"https:\/\/s3.amazonaws.com","bucket":"files.c2m"}*/
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadImageAWS(final String path) {

        Helper.getInstance().LogDetails("uploadImageAWS "," s3Url  "+s3Url);

        sendAttachMent("", "", "", path);

      /*  try {
            Helper.getInstance().LogDetails("uploadImageAWS", "called" + " " + path);
            File file = new File(path);
            BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
            AmazonS3Client s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(AWS_BASE_URL);
            s3Client.setRegion(Region.getRegion(AWS_REGION));

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(ChatActivity.this)
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            if (file != null && file.exists()) {




                final String fileName = file.getName();
                String AWS_FILE_KEY = "user_avatar/" + fileName.trim().replace(" ", "");
                FILE_NAME = AWS_FILE_KEY;
                Helper.getInstance().LogDetails("uploadImageAWS", " called  " + FILE_NAME);
                TransferObserver uploadObserver = transferUtility.upload(AWS_BUCKET, AWS_FILE_KEY, file, new ObjectMetadata(),
                        CannedAccessControlList.PublicRead);

                openProgess();
                uploadObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState transferState) {
                        try {
                            if (transferState == TransferState.COMPLETED) {

                                AWS_FILE__PATH = FILE_NAME;
                                closeProgress();
                                Helper.getInstance().LogDetails("uploadImageAWS", "completed  " + s3Url + AWS_FILE__PATH);
                                sendAttachMent(s3Url + AWS_FILE__PATH, fileName, "", path);
                            } else {
                                Helper.getInstance().LogDetails("uploadImageAWS", "not completed");
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Helper.getInstance().LogDetails("uploadImageAWS", " exception  " + ex.getLocalizedMessage());
                            closeProgress();

                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Helper.getInstance().LogDetails("uploadImageAWS", "onError " + ex.getLocalizedMessage() + " " + ex.getCause());
                        closeProgress();
                    }

                });
            } else {
                Toast.makeText(getApplicationContext(), "no file found", Toast.LENGTH_LONG).show();

            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        try {

            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case Values.MyActionsRequestCode.ATTACHMENT_DOC:
                        Helper.getInstance().LogDetails("onActivityResult", "ATTACHMENT_DOC");

                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();
                        String displayName = null;

                        if (uri != null) {
                            selectedImagePath = getRealPathFromURI(data.getData());
                            if (selectedImagePath != null) {
                                uploadImageAWS(selectedImagePath);
                            }
                            else
                            {
                                selectedImagePath = Commons.getPath(uri, ChatActivity.this);
                                if (selectedImagePath != null) {
                                    uploadImageAWS(selectedImagePath);
                                }
                            }
                        }

                       /* if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = ChatActivity.this.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }*/

                        break;
                    case Values.MyActionsRequestCode.ATTACHMENT_VIDEO:
                        Helper.getInstance().LogDetails("onActivityResult", "ATTACHMENT_VIDEO");
                        if (data.getData() != null) {

                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null) {
                                selectedImagePath = getRealPathFromURI(data.getData());
                            }
                            selectedFileuri = selectedImageUri;
                            if (selectedFileuri != null) {
                                // cropImage();
                            }
                            if (selectedImagePath != null) {
                                uploadImageAWS(selectedImagePath);
                            }
                            else
                            {
                                selectedImagePath = Commons.getPath(selectedFileuri, ChatActivity.this);
                                if (selectedImagePath != null) {
                                    uploadImageAWS(selectedImagePath);
                                }
                            }

                        }
                        break;

                    case Values.MyActionsRequestCode.CAMERA_IMAGE:
                        if (selectedFileuri != null) {
                            if (selectedFileuri != null) {
                                selectedImagePath = getRealPathFromURI(selectedFileuri);

                                if (selectedImagePath != null) {
                                    // cropImage();
                                    uploadImageAWS(selectedImagePath);
                                }
                                else
                                {
                                    selectedImagePath = Commons.getPath(selectedFileuri, ChatActivity.this);
                                    if (selectedImagePath != null) {
                                        uploadImageAWS(selectedImagePath);
                                    }
                                }
                            }

                        }

                        break;
                    case Values.MyActionsRequestCode.GALLERY_IMAGE:

                        Helper.getInstance().LogDetails("onActivityResult", "GALLERY_IMAGE called");
                        if (data.getData() != null) {

                            Helper.getInstance().LogDetails("onActivityResult", "if GALLERY_IMAGE called");

                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null) {
                                selectedImagePath = getRealPathFromURI(data.getData());
                            }

                            selectedFileuri = selectedImageUri;
                            if(selectedFileuri !=null && isGoogleDriveUri(selectedFileuri) )
                            {
                                String  doc_path=getDriveFilePath(data.getData(),ChatActivity.this);
                                if (doc_path != null) {
                                    Helper.getInstance().LogDetails("onActivityResult", "getDriveFilePath doc_path not null"+doc_path);
                                    uploadImageAWS(doc_path);
                                }
                                else
                                {
                                    Helper.getInstance().LogDetails("onActivityResult", "getDriveFilePath doc_path not null");
                                }
                            }
                            else
                            {
                                Helper.getInstance().LogDetails("onActivityResult", "getDriveFilePath doc_path not null");
                                if (selectedFileuri != null) {
                                    // cropImage();
                                }
                                if (selectedImagePath != null) {
                                    Helper.getInstance().LogDetails("onActivityResult", "selectedImagePath not null");
                                    uploadImageAWS(selectedImagePath);
                                }
                                else
                                {
                                    Helper.getInstance().LogDetails("onActivityResult", "selectedImagePath  null");
                                    if(selectedFileuri!=null){
                                        selectedImagePath = Commons.getPath(selectedFileuri, ChatActivity.this);
                                        if (selectedImagePath != null) {
                                            Helper.getInstance().LogDetails("onActivityResult", "selectedImagePath  if == null");
                                            uploadImageAWS(selectedImagePath);
                                        }
                                        else
                                        {
                                            String  doc_path=getDriveFilePath(data.getData(),ChatActivity.this);
                                            if(doc_path!=null){
                                                uploadImageAWS(doc_path);
                                                Helper.getInstance().LogDetails("onActivityResult", "selectedImagePath  if  ==not  null");
                                            }
                                            else
                                            {
                                                Helper.getInstance().LogDetails("onActivityResult", "selectedImagePath  else == null");
                                            }


                                        }
                                    }
                                    else
                                    {
                                        Helper.getInstance().LogDetails("onActivityResult", "selectedFileuri  null");
                                    }

                                }
                            }


                        }
                        else
                        {
                            Helper.getInstance().LogDetails("onActivityResult", "else GALLERY_IMAGE called");
                        }
                        break;

                    case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                            Bitmap bitmap = null;
                            try {

                                File file = new File(result.getUri().getPath());

                                if (result.getUri().getPath() != null) {

                                    uploadImageAWS(result.getUri().getPath());
                                }

                           /* if(file!=null){

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
                            }

                            // compressedImageFile = new Compressor(this).compressToFile(actualImageFile);

                           */
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        break;
                    case Values.MyActionsRequestCode.PICK_DOC:
                        if(data!=null){
                            String  doc_path=data.getStringExtra(Values.IntentData.DOC_PATH);
                            if(doc_path!=null){
                                uploadImageAWS(doc_path);
                            }
                        }
                        break;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void cropImage() {
        CropImage.activity(selectedFileuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setBorderLineColor(getResources().getColor(R.color.colorAccent))
                .setGuidelinesColor(getResources().getColor(R.color.colorAccent))
                .setBorderCornerColor(Color.BLUE)
                .start(ChatActivity.this);
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        }
    }

    private Date getDate(String date) throws ParseException {
        try{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        Date nextDate = c.getTime();
        return c.getTime();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    private void FeedBackDialogBox(boolean isAgentEndedChat, final ActiveChat activeChat) {
        try {

            Helper.getInstance().closeKeyBoard(ChatActivity.this,chat_edit_txt);
            Helper.getInstance().LogDetails("FeedBackDialogBox","called "+isAgentEndedChat);

            if(feedBackDialog==null)
            {
                feedBackDialog = new Dialog(ChatActivity.this, R.style.DialogTheme);
            }
            if(feedBackDialog!=null && !feedBackDialog.isShowing())
            {

            feedBackDialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(feedBackDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;

            feedBackDialog.getWindow().setAttributes(lp);
            feedBackDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            feedBackDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                feedBackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            feedBackDialog.setContentView(R.layout.buttom_sheet_feed_bac);

            rvCategory = feedBackDialog.findViewById(R.id.rv_categories);
            final SimpleRatingBar ratingBar = feedBackDialog.findViewById(R.id.rating_feed_back);
            final View block = feedBackDialog.findViewById(R.id.relavity_block);
            ProgressButton feedBackSubmitButton = feedBackDialog.findViewById(R.id.feed_back_submit);
            ProgressButton feed_back_cancel = feedBackDialog.findViewById(R.id.feed_back_cancel);
            ImageView close = feedBackDialog.findViewById(R.id.buttom_sheet_close);
            TextView feed_back_title_tv,noCategories;
            noCategories = feedBackDialog.findViewById(R.id.noCategories);
            feed_back_title_tv = feedBackDialog.findViewById(R.id.feed_back_title_tv);
            if(isAgentEndedChat){
                feed_back_title_tv.setText("END CHAT");
            }
            else
            {
                feed_back_title_tv.setText("USER ENDED CHAT");
            }

            FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
            flowLayoutManager.setAutoMeasureEnabled(true);
            rvCategory.setLayoutManager(flowLayoutManager);
            categoriesFlowAdapter = new CategoriesFlowAdapter(categoryArrayList);
            categoriesFlowAdapter.setListener(this);
            rvCategory.setAdapter(categoriesFlowAdapter);


            if(categoryArrayList!=null && categoryArrayList.size()>0){
                noCategories.setVisibility(View.GONE);
                rvCategory.setVisibility(View.VISIBLE);
            }
            else
            {
                noCategories.setVisibility(View.VISIBLE);
                rvCategory.setVisibility(View.GONE);
            }


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedBackDialog.cancel();

                    //emitChatEndedAgent();


                }
            });


          feed_back_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedBackDialog.cancel();
                  //  emitChatEndedAgent();

                }
            });

            feedBackDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                    Helper.getInstance().LogDetails("feedBackDialog","cancel called");
                }
            });

            ratingBar.setStarBackgroundColor(getResources().getColor(R.color.ratingBarInactiveColor));
            ratingBar.setBorderColor(getResources().getColor(R.color.ratingBarInactiveColor));
            ratingBar.setFillColor(getResources().getColor(R.color.ratingBarActiveColor));


            ratingBar.setStarBorderWidth(1);
            ratingBar.setStepSize(1);
            ratingBar.setDrawBorderEnabled(false);
            ratingBar.showContextMenu();




            ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                    String tag=block.getTag().toString();
                    if (tag != null && tag.equals("1") && rating!=0) {
                        block.setTag("0");
                        block.setBackground(getResources().getDrawable(R.drawable.back_grey));
                    }
                    Helper.getInstance().LogDetails("FeedBackDialogBox","rating "+rating);

                }
            });

            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag=block.getTag().toString();
                    if (tag != null && tag.equals("0")) {
                        block.setTag("1");
                        block.setBackground(getResources().getDrawable(R.drawable.block_border_back_lay));
                        ratingBar.setRating(0);
                        rating="NA";

                    }
                    else{
                        block.setTag("0");
                        block.setBackground(getResources().getDrawable(R.drawable.back_grey));
                    }


                }
            });

            feedBackSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tag=block.getTag().toString();
                    int rat=(int)ratingBar.getRating();

                    if((tag!=null && tag.equals("0") )&& rat==0)
                    {

                        Toast.makeText(ChatActivity.this,"Please select rating",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if (tag != null && tag.equals("1")) {
                            rating = "NA";
                        }
                         else
                            {

                                rating=rat+"";

                            }
                        updateRatingApi(feedBackDialog,feedBackSubmitButton,activeChat);

                    }

                }
            });

                feedBackDialog.show();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFeedBackCategory(final boolean isAgentEndedChat, final ActiveChat activeChat) {
        try {

            Helper.getInstance().LogDetails("FeedBackDialogBox getFeedBackCategory","called "+isAgentEndedChat);

            if (Utilities.getConnectivityStatus(ChatActivity.this) <= 0) {
                Helper.getInstance().pushToast(ChatActivity.this, "Please check your network connection...");
                return;
            }

            openProgess();

            retrofit2.Call<CategoriesApi.CategoriesResponse> call =
                    CategoriesApi.getApiService().getCategories(ApiEndPoint.token, user_id, company_token);

            call.enqueue(new Callback<CategoriesApi.CategoriesResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<CategoriesApi.CategoriesResponse> call,
                                       @NonNull Response<CategoriesApi.CategoriesResponse> response) {


                    CategoriesApi.CategoriesResponse data = response.body();

                    closeProgress();

                    if (data != null) {
                        if (data.getSuccess()) {
                            if(data.getData() != null && !data.getData().isEmpty()){
                                if(categoryArrayList!=null && categoryArrayList.size()>0){
                                    categoryArrayList.clear();
                                }
                                categoryArrayList.addAll(data.getData());
                                if(categoriesFlowAdapter!=null){
                                    categoriesFlowAdapter.notifyDataSetChanged();
                                }
                            }
                            FeedBackDialogBox(isAgentEndedChat,activeChat);
                        } else {
                            FeedBackDialogBox(isAgentEndedChat, activeChat);

                            Helper.getInstance().pushToast(ChatActivity.this, data.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<CategoriesApi.CategoriesResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    closeProgress();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();

        }
    }

    private void getTrack() {
        try {

            if (Utilities.getConnectivityStatus(ChatActivity.this) <= 0) {
                Helper.getInstance().pushToast(ChatActivity.this, "Please check your network connection...");
                return;
            }

            if(activeChat!=null){
                track_code=activeChat.getTrack_code();
                chat_start_date=activeChat.getStartTime();
                if(track_code==null || track_code.trim().isEmpty())
                {
                    track_code="0";
                }
            }
            else
            {
                return;
            }

          Helper.getInstance().LogDetails("getTrack","called "+ApiEndPoint.token+" "+user_id+" "+company_token+" "+current_site_id+" "+" "+track_code+" "+chat_start_date);

           Call<TrackResponse> call =
                    c2mApiService.getTrack(ApiEndPoint.token, user_id, company_token,current_site_id,track_code,chat_start_date);

            call.enqueue(new Callback<TrackResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<TrackResponse> call,
                                       @NonNull Response<TrackResponse> response) {


                    TrackResponse data = response.body();



                    if (data != null) {
                        Helper.getInstance().LogDetails("getTrack res",data.toString());
                        if (data.isSuccess()) {

                            Helper.getInstance().LogDetails("getTrack","su");
                            if(data.getData() != null ){
                                Helper.getInstance().LogDetails("getTrack res",data.getData().size()+"");
                                if(visitPages!=null && visitPages.size()>0){
                                    visitPages.clear();
                                }
                                visitPages.addAll(data.getData());
                                if(visitPagesAdapter!=null){
                                    visitPagesAdapter.notifyDataSetChanged();
                                }

                               setVisitPagesLayyou();
                            }

                        } else {
                            setVisitPagesLayyou();
                            Helper.getInstance().LogDetails("getTrack","fal");


                           // Helper.getInstance().pushToast(ChatActivity.this, data.getMessage());
                        }
                    }
                    else
                    {
                        setVisitPagesLayyou();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<TrackResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    setVisitPagesLayyou();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();

        }
    }

    private void updateRatingApi(final Dialog feedBackDialog,ProgressButton feedBackSubmitButton, final ActiveChat activeChat) {
        try {

            if (Utilities.getConnectivityStatus(ChatActivity.this) <= 0) {
                Helper.getInstance().pushToast(ChatActivity.this, "Please check your network connection...");
                return;
            }

            //openProgess();
            feedBackSubmitButton.enableLoadingState();

            String  chat_id=activeChat.getChatId();

            Helper.getInstance().LogDetails("updateRatingApi",ApiEndPoint.token+" "+user_id+" "+company_token+" "+categoryId+" "+rating+"  "+chat_id);

            retrofit2.Call<ChatRatingUpdateResponse.RatingUpdateResponse> call =
                    ChatRatingUpdateResponse.getApiService().updateRating(ApiEndPoint.token, user_id, company_token,categoryId,rating,chat_id);

            call.enqueue(new Callback<ChatRatingUpdateResponse.RatingUpdateResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ChatRatingUpdateResponse.RatingUpdateResponse> call,
                                       @NonNull Response<ChatRatingUpdateResponse.RatingUpdateResponse> response) {


                    ChatRatingUpdateResponse.RatingUpdateResponse data = response.body();

                   // closeProgress();
                    feedBackSubmitButton.disableLoadingState();;

                    if (data != null) {
                        if (data.getSuccess()) {
                            rating="NA";
                            categoryId="0";
                           // emitChatEndedAgent();
                            if(feedBackDialog!=null){
                                feedBackDialog.cancel();
                            }
                            emitChatEndedAgent();
                            //removeChat(activeChat);

                            Helper.getInstance().LogDetails("updateRatingApi","res "+data.getMessage());

                        } else {
                            if(categoryArrayList==null || categoryArrayList.size()==0){
                                if(feedBackDialog!=null){
                                    feedBackDialog.cancel();
                                }
                                emitChatEndedAgent();
                               // removeChat(activeChat);
                            }
                            Helper.getInstance().pushToast(ChatActivity.this, data.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ChatRatingUpdateResponse.RatingUpdateResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    feedBackSubmitButton.disableLoadingState();

                   // closeProgress();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            closeProgress();
        }
    }

    @Override
    public void onCartItemClick(View view, int position) {
        Helper.getInstance().LogDetails("onCartItemClick","pos "+position);
        if(categoryArrayList!=null && categoryArrayList.size()>0){
            if(position<categoryArrayList.size()){
                categoryId=categoryArrayList.get(position).getId();
            }
        }
    }

    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }
    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    private void checkAgentStatus() {
        try{

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    if (mSocket != null && mSocket.connected()) {
                        // Helper.getInstance().LogDetails("updateAgentStatusInfo checkAgentStatus","called");

                        EmitCheckAgentStatus(sitesInfoList.get(i).getSiteToken(), sitesInfoList.get(i).getSiteId());
                    } else {
                        //  Helper.getInstance().LogDetails("updateAgentStatusInfo checkAgentStatus","not called");
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void EmitCheckAgentStatus(String siteToken, String siteId) {

        /*{site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token}; c2mSocket.emit("check_agent_status",agentStatusData);*/
        try {
            if (user_id != null && !user_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("site_token", siteToken);
                if(isSelf){
                    object.put("agent_id", Integer.parseInt(user_id));
                }
                else
                {
                    object.put("agent_id", Integer.parseInt(user_id));
                }



                object.put("site_id", siteId);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("EmitCheckAgentStatus ",object.toString());
                    mSocket.emit(SocketConstants.CHECK_AGENT_STATUS, object);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void EmitGetAgentChats() {

        /*{site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token}; c2mSocket.emit("check_agent_status",agentStatusData);*/
        Helper.getInstance().LogDetails("EmitGetAgentChats ","called "+agent_id+" "+user_id);
        try {
            if (user_id != null && !user_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                // object.put("site_token", siteToken);
                if(isSelf){
                    object.put("agent_id", Integer.parseInt(user_id));
                }
                else
                {
                    if(user_id!=null && !user_id.trim().isEmpty())
                    {
                        object.put("agent_id", Integer.parseInt(user_id));
                    }

                }

                //   object.put("site_id", siteId);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("EmitGetAgentChats ",object.toString());
                    mSocket.emit(SocketConstants.GET_AGENT_CHATS, object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Helper.getInstance().LogDetails("EmitGetAgentChats ","res "+args[0].toString());
                            JSONArray jsonArray=(JSONArray) args[0];
                            Helper.getInstance().LogDetails("getAgentChats before",activeChatPosition+"");
                            //getAgentChats(jsonArray);
                            Helper.getInstance().LogDetails("getAgentChats after",activeChatPosition+"");

                           // removeDeletedChats(jsonArray);
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void removeDeletedChats(JSONArray jsonArray)
    {
        Helper.getInstance().LogDetails("removeDeletedChats","called ");
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    for (int i = 0; i < sitesInfoList.size(); i++) {
                        String sid = sitesInfoList.get(i).getSiteId();
                        List<ActiveChat> activeChatList = sitesInfoList.get(i).getActiveChats();
                        if (activeChatList != null && activeChatList.size() > 0) {
                            int removedIndex=-1;
                            for(int k = 0; k< activeChatList.size(); k++)
                            {
                                if(removedIndex!=-1){
                                    k--;
                                    removedIndex=-1;
                                }


                                boolean isPresent=false;

                                for(int j=0;j<jsonArray.length();j++){
                                    JSONObject jsonObject=jsonArray.optJSONObject(j);

                                    if(jsonObject!=null){
                                        String siteId = jsonObject.optString("site_id");






                                        if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                            if (activeChatList.get(k).getChatId() != null && !activeChatList.get(k).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {
                                                if (jsonObject.optString("chat_id").equals(activeChatList.get(k).getChatId())) {

                                                    isPresent=true;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                }
                                if(!isPresent){
                                    if (current_site_id != null && sid.equals(current_site_id)) {
                                        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                            for (int m = 0; m < activeChatArrayList.size(); m++) {
                                                if (activeChatList.get(k)!=null && activeChatList.get(k).getChatId().equals(activeChatArrayList.get(m).getChatId())) {

                                                    activeChatArrayList.remove(m);
                                                    Helper.getInstance().LogDetails("removeDeletedChats after remove",activeChatArrayList.size()+"");
                                                    if(isTabChanged){
                                                        topAdapter.setList(activeChatArrayList);
                                                        Helper.getInstance().LogDetails("removeDeletedChats after setlist",activeChatArrayList.size()+"");
                                                    }

                                                    break;

                                                }
                                            }

                                        }
                                        boolean  isBack=false;

                                        if (activeChat != null) {

                                            if (activeChatList.get(k)!=null && activeChatList.get(k).getChatId().equals(activeChat.getChatId())) {
                                                isBack = true;
                                                closeDialogs();
                                            }


                                        }

                                        if (isBack) {



                                            Helper.getInstance().LogDetails("removeDeletedChats updateAgentChatEnded","called "+false);

                                            if(activeChatArrayList!=null && activeChatArrayList.size()>0){
                                                Helper.getInstance().LogDetails("removeDeletedChats removeChat","called updateChatList"+isBack);
                                                activeChatPosition=-1;
                                                updateChatList(activeChatArrayList.get(0),0);
                                            }
                                            else
                                            {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        activeChatPosition=-1;
                                                        list.clear();
                                                        activeChat = null;
                                                        receiver_id = "0";
                                                        chat_id = "0";
                                                        //agent_id = "0";
                                                        lastMessageId = "0";
                                                        conversation_reference_id = "";
                                                        if (noDataImage != null) {
                                                            if(list!=null && list.size()>0){
                                                                list.clear();
                                                                if(adapter!=null)
                                                                {
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            }

                                                            noDataImage.setVisibility(View.VISIBLE);
                                                            if (chatRecyclerView != null) {
                                                                chatRecyclerView.setVisibility(View.GONE);
                                                            }
                                                            if (usersLayout != null) {
                                                                usersLayout.setVisibility(View.GONE);


                                                            }
                                                            if (bottomLayout != null) {
                                                                bottomLayout.setVisibility(View.GONE);
                                                            }

                                                        }
                                                    }
                                                });
                                            }


                                        }

                                    }

                                    activeChatList.remove(k);
                                    removedIndex=k;
                                    saveSiteData();
                                    Helper.getInstance().LogDetails("removeDeletedChats","removed "+k);
                                }
                            }


                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showReplyMessageLayout(ChatModel chatModel) {
        try{
            if(isSelf && (activeChat!=null && activeChat.getChatStatus()!=null && !activeChat.getChatStatus().equals(String.valueOf(Values.ChatStatus.CHAT_USER_ENDED)))){

                if(chatModel!=null ){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reply_message_layout.setVisibility(View.VISIBLE);

                        }
                    });
                    isReplyMessage=true;
                    replyMessageObj=chatModel;
                    int senderId=chatModel.getSender_id();
                    if(tmUserId!=null && !tmUserId.trim().isEmpty() && senderId==Integer.parseInt(tmUserId))
                    {
                        reply_message_name_tv.setText("Me");
                    }
                    else
                    {
                        if(receiver_name!=null && !receiver_name.trim().isEmpty()){
                            reply_message_name_tv.setText(Helper.getInstance().capitalize(receiver_name));
                        }

                    }
                    int messageType=chatModel.getMessage_type();
                    if(messageType==Values.MessageType.MESSAGE_TYPE_TEXT)
                    {
                        String message =chatModel.getMessage();
                        if(message!=null){
                            reply_message_tv.setVisibility(View.VISIBLE);
                            replyAttachmentLayout.setVisibility(View.GONE);
                            reply_message_tv.setText(message);
                        }
                        
                    }
                    else if(messageType==Values.MessageType.MESSAGE_TYPE_ATTACHMENT){
                        reply_message_tv.setVisibility(View.GONE);
                        replyAttachmentLayout.setVisibility(View.VISIBLE);
                        String devicePath = chatModel.getAttachmentDevicePath();
                        String path = chatModel.getAttachment();

                        String attachmentType = chatModel.getAttachment_extension();
                        String attachmentName = chatModel.getAttachment_name();
                        if (attachmentType == null || attachmentType.trim().isEmpty()) {
                            if(path!=null && !path.trim().isEmpty())
                            {
                                int index = path.lastIndexOf(".");
                                String fileType = path.substring(index + 1);
                                if (fileType != null && !fileType.trim().isEmpty()) {
                                    attachmentType = fileType.replace(" ", "");
                                }

                                Helper.getInstance().LogDetails("sendReplyMessage MESSAGE_TYPE_ATTACHMENT", "attachmentType null" + index + " " + path + attachmentType);

                            }
                            else if(devicePath!=null && !devicePath.trim().isEmpty())
                            {
                                int index = devicePath.lastIndexOf(".");
                                String fileType = devicePath.substring(index + 1);
                                if (fileType != null && !fileType.trim().isEmpty()) {
                                    attachmentType = fileType.replace(" ", "");
                                }

                                Helper.getInstance().LogDetails("sendReplyMessage MESSAGE_TYPE_ATTACHMENT", "attachmentType null" + index + " " + devicePath + attachmentType);
                            }

                        }
                        if (attachmentName == null || attachmentName.trim().isEmpty()) {
                            if(path!=null && !path.trim().isEmpty())
                            {
                                int index = path.lastIndexOf("/");
                                String name = path.substring(index + 1);
                                if (name != null && !name.trim().isEmpty()) {
                                    chatModel.setAttachment_name(name);
                                    attachmentName = name;
                                }
                            }
                            else if(devicePath!=null && !devicePath.trim().isEmpty())
                            {
                                int index = devicePath.lastIndexOf("/");
                                String name = devicePath.substring(index + 1);
                                if (name != null && !name.trim().isEmpty()) {
                                    chatModel.setAttachment_name(name);
                                    attachmentName = name;
                                }
                            }

                        }

                        if (attachmentName != null) {
                            if (attachmentName.length() > 20) {
                                int len = attachmentName.length();
                                attachment_name_tv.setText(attachmentName.substring(0, 7) + "..." + attachmentName.substring(len - 8));
                            } else {
                                attachment_name_tv.setText(attachmentName);
                            }
                        }


                        if (attachmentType != null && !attachmentType.trim().isEmpty()) {
                            Helper.getInstance().LogDetails("sendReplyMessage MESSAGE_TYPE_ATTACHMENT====", messageType + " " + chatModel.getAttachment() + " " + attachmentType + " " + chatModel.isAttachmentDownloaded()+"  "+chatModel.getAttachmentDevicePath());
                            if (attachmentType.toLowerCase().equals("png") || attachmentType.toLowerCase().equals("jpg") || attachmentType.toLowerCase().equals("jpeg") || attachmentType.toLowerCase().equals("bmp") || attachmentType.toLowerCase().equals("gif")) {
                                
                                if (devicePath != null && !devicePath.trim().isEmpty()) {

                                    File imgFile = new File(devicePath);
                                    if (imgFile.exists()) {

                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        if (myBitmap != null && !attachmentType.toLowerCase().equals("gif")) {
                                           attachmentImage.setImageBitmap(myBitmap);
                                        } else {
                                            if (path != null && !path.trim().isEmpty()) {
                                                path = path.replace("\"", "");
                                                
                                                RequestOptions options = new RequestOptions()
                                                        .error(R.drawable.ic_attachment_img)
                                                        .priority(Priority.HIGH);
                                                Glide.with(ChatActivity.this)
                                                        .load(path)
                                                        .apply(options)
                                                        .listener(new RequestListener<Drawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                               
                                                                Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed","called trigger2");

                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                              
                                                                Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed onResourceReady","called trigger2");
                                                                return false;
                                                            }
                                                        })

                                                        .into(attachmentImage);

                                            }
                                        }

                                    } else {
                                        if (path != null && !path.trim().isEmpty()) {
                                            path = path.replace("\"", "");
                                            
                                            RequestOptions options = new RequestOptions()

                                                    .error(R.drawable.ic_attachment_img)
                                                    .priority(Priority.HIGH);
                                            Glide.with(ChatActivity.this)
                                                    .load(path)
                                                    .apply(options)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                           
                                                            Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed","called trigger3");


                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        
                                                            Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed onResourceReady","called trigger3");
                                                            return false;
                                                        }
                                                    })
                                                    .into(attachmentImage);

                                        } 
                                    }
                                } else if (path != null && !path.trim().isEmpty()) {
                                    path = path.replace("\"", "");
                                  
                                    RequestOptions options = new RequestOptions()

                                            .error(R.drawable.ic_attachment_img)
                                            .priority(Priority.HIGH);
                                    Glide.with(ChatActivity.this)
                                            .load(path)
                                            .apply(options)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                   
                                                    Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed ","called trigger4");


                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                 
                                                    Helper.getInstance().LogDetails("sendReplyMessage onLoadFailed onResourceReady","called trigger4");
                                                    return false;
                                                }
                                            })
                                            .into(attachmentImage);

                                }
                            } else {
                                
                               



                                //"jpg", "jpeg", "png", "gif", "bmp","docx","pdf","odt","xls","doc"
                                if (attachmentType.toLowerCase().equals("pdf")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_pdf));
                                } else if (attachmentType.toLowerCase().equals("doc") || attachmentType.toLowerCase().equals("docx")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_doc));
                                } else if (attachmentType.toLowerCase().equals("odt")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("bmp")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_file));
                                } else if (attachmentType.toLowerCase().equals("xls")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_xls));
                                } else if (attachmentType.toLowerCase().equals("xml")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_xml));
                                } else if (attachmentType.toLowerCase().equals("css")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_css));
                                } else if (attachmentType.toLowerCase().equals("cad")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_cad));
                                } else if (attachmentType.toLowerCase().equals("sql")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_sql));
                                } else if (attachmentType.toLowerCase().equals("aac")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_aac));
                                } else if (attachmentType.toLowerCase().equals("avi")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_avi));
                                } else if (attachmentType.toLowerCase().equals("gif")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_gif));
                                } else if (attachmentType.toLowerCase().equals("dat")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_dat));
                                } else if (attachmentType.toLowerCase().equals("dll")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_dll));
                                } else if (attachmentType.toLowerCase().equals("dmg")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_dmg));
                                } else if (attachmentType.toLowerCase().equals("eps")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_eps));
                                } else if (attachmentType.toLowerCase().equals("fla")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_fla));
                                } else if (attachmentType.toLowerCase().equals("flv")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_flv));
                                } else if (attachmentType.toLowerCase().equals("html")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_html));
                                } else if (attachmentType.toLowerCase().equals("iso")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_iso));
                                } else if (attachmentType.toLowerCase().equals("js")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_js));
                                } else if (attachmentType.toLowerCase().equals("mov")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov));
                                } else if (attachmentType.toLowerCase().equals("mp3")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp3));
                                } else if (attachmentType.toLowerCase().equals("mp4")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4));
                                } else if (attachmentType.toLowerCase().equals("mpg")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_mpg));
                                } else if (attachmentType.toLowerCase().equals("php")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_php));
                                } else if (attachmentType.toLowerCase().equals("ppt") || attachmentType.toLowerCase().equals("pptx")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_ppt));
                                } else if (attachmentType.toLowerCase().equals("ps")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_ps));
                                } else if (attachmentType.toLowerCase().equals("psd")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_psd));
                                } else if (attachmentType.toLowerCase().equals("rar")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_rar));
                                } else if (attachmentType.toLowerCase().equals("txt")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_txt));
                                } else if (attachmentType.toLowerCase().equals("video")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_video));
                                } else if (attachmentType.toLowerCase().equals("zip")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_zip));
                                } else if (attachmentType.toLowerCase().equals("wmv")) {
                                    attachmentImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wmv));
                                }


                            }
                        }
                        
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendHistoryToFirebase(List<ChatModel> list){
        System.out.println("sendHistoryToFirebase called" +list.size());
        List<FirebaseTextMessage> conversation=new ArrayList<>();
      //  conversation.add(FirebaseTextMessage.createForLocalUser(localMsg, System.currentTimeMillis()));//local user
       // conversation.add(FirebaseTextMessage.createForRemoteUser(friendMessage, System.currentTimeMillis(), "ABCDEF"));//friend

        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                int senderId=list.get(i).getSender_id();
                String message=list.get(i).getMessage();
                String conversationReferenceId=list.get(i).getConversation_reference_id();
                System.out.println("sendHistoryToFirebase called" +message+" "+conversationReferenceId+" "+tmUserId+" "+senderId);
                if(tmUserId!=null )
                {
                    if(Integer.parseInt(tmUserId)==senderId)
                    {
                        //local
                        conversation.add(FirebaseTextMessage.createForLocalUser(message, System.currentTimeMillis()));
                    }
                    else
                    {
                        conversation.add(FirebaseTextMessage.createForRemoteUser(message, System.currentTimeMillis(), conversationReferenceId));
                    }
                }
            }

        }


        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            System.out.println("sendHistoryToFirebase"+"STATUS_NOT_SUPPORTED_LANGUAGE");
                            // The conversation's language isn't supported, so the
                            // the result doesn't contain any suggestions.
                            Toast.makeText(ChatActivity.this,"Not supported language",Toast.LENGTH_LONG).show();
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            System.out.println("sendHistoryToFirebase"+"STATUS_SUCCESS");
                            // Task completed successfully
                            // ...
                            setSmartPepliesData(result.getSuggestions());
                            String s="";
                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                String replyText = suggestion.getText();
                                s=s+replyText+", ";
                            }

                            System.out.println("sendHistoryToFirebase   res  "+s);
                            //result_tv.setText(s);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("sendHistoryToFirebase  "+e.getCause()+" "+e.getLocalizedMessage());
                        // Task failed with an exception
                        // ...
                    }
                });


    }

    private void setSmartPepliesData(List<SmartReplySuggestion> suggestions){
        try {
           clearSmartReplies();
            if (suggestions != null && suggestions.size() > 0) {
               for(int i=0;i<suggestions.size();i++){
                   smartRepliesList.add(suggestions.get(i).getText());
               }
               if(smartRepliesList!=null && smartRepliesList.size()>0){
                   smartRepliesLayout.setVisibility(View.VISIBLE);
                   smartRepliesMessageAdapter.notifyDataSetChanged();
               }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
