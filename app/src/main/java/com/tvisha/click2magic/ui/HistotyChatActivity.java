package com.tvisha.click2magic.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvisha.click2magic.DataBase.ChatModel;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.AppFileProvider;
import com.tvisha.click2magic.Helper.DateUtils;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.HistotyConversationAdapter;
import com.tvisha.click2magic.adapter.VisitPagesAdapter;
import com.tvisha.click2magic.api.C2mApiInterface;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.Chat;
import com.tvisha.click2magic.api.post.model.TrackData;
import com.tvisha.click2magic.api.post.model.TrackResponse;
import com.tvisha.click2magic.chatApi.ApiClient;
import com.tvisha.click2magic.chatApi.ApiInterface;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.model.GetMessagesResponse;
import com.tvisha.click2magic.model.Message;
import com.tvisha.click2magic.socket.AppSocket;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HistotyChatActivity extends AppCompatActivity  {

    public String receiver_name = "", user_name = "";
    public Socket mSocket, tmSocket;


    @BindView(R.id.visitPagesLayout)
    LinearLayout visitPagesLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipe_refresh;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @BindView(R.id.no_chats_image)
    ImageView no_chats_image;

    @BindView(R.id.info_icon)
    ImageView info_icon;

    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;

    @BindView(R.id.visit_pages_recycleView)
    RecyclerView visit_pages_recycleView;

    @BindView(R.id.browser_name_tv)
    TextView browser_name_tv;

    @BindView(R.id.os_name_tv)
    TextView os_name_tv;

    @BindView(R.id.actionLable)
    TextView actionLable;

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

    @BindView(R.id.start_time_tv)
    TextView start_time_tv;

   // @BindView(R.id.actionLable)
    TextView agent_name_tv;


    @BindView(R.id.chat_layout)
    RelativeLayout chat_layout;

    @BindView(R.id.info_layout)
    RelativeLayout info_layout;

    @OnClick(R.id.back_lay)
    void back(){
        onBackPressed();
    }

    @OnClick(R.id.info_icon)
    void showInfo(){
        if(infoDialog==null || (infoDialog!=null && !infoDialog.isShowing()))
            showCustomerInfoDialog();
    }

    @OnClick(R.id.phoneNumberLayout)
    void call(){
        if(activeChat.getMobile()!=null && !activeChat.getMobile().trim().isEmpty())
        {
            makeCall(activeChat.getMobile());
        }

    }

    @OnClick(R.id.phone_number_tv)
    void call1(){
        if(activeChat.getMobile()!=null && !activeChat.getMobile().trim().isEmpty())
        {
            makeCall(activeChat.getMobile());
        }

    }

    @OnClick(R.id.emailLayout)
    void email(){
        sendEmail();
    }

    @OnClick(R.id.email_tv)
    void email1(){
        sendEmail();
    }



    String lastMessageId = "", role = "", agent_id = "",company_token="", conversation_reference_id = "", accessToken = "", socketId = "",
            endTime = "", limit = "50", receiver_email = "", user_id = "", tmUserId = "", receiver_id = "",
            track_code="",chat_start_date="",current_site_id="";;
    boolean isSelf = true,isApiCalled = false,screenshotClicked=false;
    int downloadAtachMentMessageId = 0,startHour, startMin, START_AM_PM;
    String dateFormat1 = "dd MMM yyyy";
    SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, Locale.US);
    Dialog infoDialog=null;



    C2mApiInterface c2mApiService;
    ApiInterface apiService;
    ActiveChat activeChat = null;
    Dialog dialog = null;
    LinearLayoutManager visitPagesManager;
    VisitPagesAdapter visitPagesAdapter;
    ConversationTable conversationTable;
    HistotyConversationAdapter adapter;
    List<ChatModel> list = new ArrayList<>();
    List<Message> messageList = new ArrayList<>();
    List<TrackData> visitPages=new ArrayList<>();
    ArrayList<Chat> chatList = new ArrayList<>();
    LinearLayoutManager chatLayoutManager;

    Activity activity;
    Context context;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {

                case Values.SingleUserChat.CHAT_IMAGE_PREVIEW:
                    try {
                        Navigation.getInstance().imageViewer(HistotyChatActivity.this, String.valueOf(msg.obj), receiver_id, tmUserId, conversation_reference_id, receiver_name, user_name);
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

    @Override
    protected void onRestart() {
        if(adapter!=null){
            adapter.setSelectionmode(true);
        }
        super.onRestart();
    }

    private SharedPreferences preferences;
   AppSocket appSocket ;
    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {

                case Values.RecentList.RECENT_NETWORK_CONNECTED:
                    if (Helper.getConnectivityStatus(HistotyChatActivity.this)) {
                        Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "called");
                        socketConnections();
                    }
                    break;
                case Values.RecentList.FCM_TRIGGER:
                    if (Helper.getConnectivityStatus(HistotyChatActivity.this)) {
                        socketConnections();
                    }
                    break;
                case Values.RecentList.ACCESS_TOKEN_UPDATED:
                    getAccessToken();
                    break;

            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void socketConnections(){
        if(appSocket==null){
            appSocket=(AppSocket) getApplication();
        }
        if(mSocket==null){
            mSocket=appSocket.getSocketInstance();
        }
        if (mSocket != null && !mSocket.connected()) {
            appSocket.connectSocket();
        }

        if(tmSocket==null)
        {
            tmSocket=appSocket.getTmSocketInstance();
        }

        if (tmSocket != null && !tmSocket.connected()) {
          appSocket.connectTmSocket();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histoty_chat);
        ButterKnife.bind(this);
        activity=HistotyChatActivity.this;
        context=HistotyChatActivity.this;
        handleIntent();
        initViews();


    }

    private void handleIntent() {

        try{

        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_HISTORY_CHAT;
        HandlerHolder.mainActivityUiHandler = uiHandler;
        AppSocket appSocket = (AppSocket) getApplication();
        mSocket = appSocket.getSocketInstance();
        tmSocket = appSocket.getTmSocketInstance();
        conversationTable = new ConversationTable(HistotyChatActivity.this);
        c2mApiService = ApiClient.getClient().create(C2mApiInterface.class);
        getSharedPreferenceData();

        apiService = ApiClient.getClient().create(ApiInterface.class);

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                activeChat = bundle.getParcelable("userData");
                if (activeChat != null) {
                    receiver_id = activeChat.getTmVisitorId();
                    receiver_email = activeChat.getEmail();
                    receiver_name = activeChat.getName();
                    conversation_reference_id = activeChat.getChatReferenceId();
                    endTime = activeChat.getEndTime();
                    agent_id = activeChat.getAgentId();
                    //tmUserId=activeChat.getTm_visitor_id();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getSharedPreferenceData() {
        try{

        isSelf = Session.getIsSelf(context);
        if (isSelf) {
            user_id = Session.getUserID(context);
            tmUserId =  Session.getTmUserId(context);
            role =  Session.getUserRole(context);
            user_name =  Session.getUserName(context);


        } else {
            user_id = Session.getOtherUserId(context);
            tmUserId =Session.getOtherUserTmUserId(context);
            role =  Session.getUserRole(context);
            user_name = Session.getOtherUserDisplayName(context);

        }

        accessToken = Session.getAccessToken(context);
        socketId = Session.getSocketId(context);
        company_token = Session.getCompanyToken(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    boolean isRefresh=false;
    private void initViews() {
        try{
        progressDialog();

            info_icon.setVisibility(View.VISIBLE);

        agent_name_tv=findViewById(R.id.actionLable);
        chatLayoutManager = new LinearLayoutManager(HistotyChatActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new HistotyConversationAdapter(HistotyChatActivity.this, list, receiver_id, receiver_name, handler);
        chatLayoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.setNestedScrollingEnabled(true);
        chatRecyclerView.setAdapter(adapter);

        if (adapter != null) {
            adapter.setData(activeChat.getTmVisitorId(), receiver_id, endTime, receiver_name, user_name);
        }


            if (visit_pages_recycleView != null) {

                visitPagesManager = new LinearLayoutManager(HistotyChatActivity.this, LinearLayoutManager.VERTICAL, false);
                visit_pages_recycleView.setLayoutManager(visitPagesManager);
                visit_pages_recycleView.setNestedScrollingEnabled(false);
                visitPagesAdapter = new VisitPagesAdapter(HistotyChatActivity.this, visitPages, isSelf);
                visitPagesAdapter.selectionMode = true;
                visit_pages_recycleView.setAdapter(visitPagesAdapter);
            }


            getHistory();
            if (activeChat.getUser_name() != null && !activeChat.getUser_name().trim().isEmpty()) {
                agent_name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
            } else {
                agent_name_tv.setText("-");
            }

            swipe_refresh.setEnabled(false);

            chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerview, int dx, int dy) {
                    super.onScrolled(recyclerview, dx, dy);

                    int totalItemCount = chatLayoutManager.getItemCount();
                    int pastVisiblesItems = chatLayoutManager.findLastVisibleItemPosition();
                    int completeVisiblesItems = chatLayoutManager.findLastCompletelyVisibleItemPosition();


                    Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener chatRecyclerView",totalItemCount+" "+pastVisiblesItems+" "+completeVisiblesItems+" "+dy);


                    if (dy != 0 && dy<0) {

                        Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener","if down calledd"+dy);
                        if (list != null && list.size() > 0) {

                            if (pastVisiblesItems+15>=totalItemCount) {
                                Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener api calling*** ", pastVisiblesItems + " " );
                                if(!isApiCalled){
                                    isApiCalled=true;
                                    isRefresh=true;
                                    //swipe_refresh.setRefreshing(true);
                                   // swipe_refresh.setColorSchemeResources(R.color.colorAccent, android.R.color.holo_blue_bright, R.color.colorPrimaryDark, android.R.color.holo_green_light);
                                    progressBar.setVisibility(View.VISIBLE);
                                    callGetConversationsApi();
                                }

                            } else {
                                Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener else ", pastVisiblesItems + " ");
                            }
                        }
                    }
                    else
                    {
                        Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener","else down calledd"+dy);


                    }

                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView agents_recyclerView, int newState) {
                    super.onScrollStateChanged(agents_recyclerView, newState);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }


    }



    private void callGetConversationsApi() {

        Helper.getInstance().LogDetails("HistotyChatActivity addOnScrollListener callGetConversationsApi", "called " + ApiEndPoint.TM_SERVER_SOCKET_TOKEN + "   accessToken " + accessToken + "  socketId  " + socketId + "   tmUserId " + tmUserId + "  receiver_id  " + receiver_id + "   lastMessageId " + lastMessageId + "  conversation_reference_id    " + conversation_reference_id);
        try{



        if(isRefresh)
        {
            isRefresh=false;

        }
        else
        {
            openProgess();
        }

        Call<GetMessagesResponse> call = apiService.getConversation(ApiEndPoint.TM_SERVER_SOCKET_TOKEN, tmUserId, socketId, accessToken, receiver_id, limit, lastMessageId, 0, conversation_reference_id);
        call.enqueue(new Callback<GetMessagesResponse>() {
            @Override
            public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                GetMessagesResponse apiResponse = response.body();
                closeProgress();

                if (apiResponse != null) {

                    if (apiResponse.isSuccess()) {
                        Helper.getInstance().LogDetails("callGetConversationsApi", "called true");

                        if (apiResponse.getMessages() != null && apiResponse.getMessages().size() > 0) {
                            no_chats_image.setVisibility(View.GONE);
                            messageList = apiResponse.getMessages();
                            Helper.getInstance().LogDetails("callGetConversationsApi", "called" + messageList.size());
                            updateChatData(messageList);
                            for (int i = 0; i < messageList.size(); i++) {
                                Helper.getInstance().LogDetails("callGetConversationsApi", "called" + messageList.get(i).getMessage_type()+" "+messageList.get(i).getMessage());
                            }

                        } else {
                            Helper.getInstance().LogDetails("callGetConversationsApi", "called empty");
                            if (accessToken == null || accessToken.trim().isEmpty()) {
                                isApiCalled = true;
                                getAccessToken();
                            }

                            if(activeChat.getCreatedAt()!=null && !activeChat.getCreatedAt().equals("0000-00-00 00:00:00")){
                                Helper.getInstance().LogDetails("callGetConversationsApi", "called empty"+activeChat.getCreatedAt());

                                ChatModel model=new ChatModel();
                                list.add(model);
                                adapter.notifyDataSetChanged();
                            }

                        }

                    } else {
                        Helper.getInstance().LogDetails("callGetConversationsApi", "called fail");

                    }
                }
                if(list!=null && list.size()>0){
                    no_chats_image.setVisibility(View.GONE);
                }
                else
                {
                    no_chats_image.setVisibility(View.VISIBLE);
                }
                swipe_refresh.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                isApiCalled=false;
                closeProgress();
            }

            @Override
            public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
                Helper.getInstance().LogDetails("callGetConversationsApi", "called exc" + t.getLocalizedMessage());
                swipe_refresh.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                isApiCalled=false;
                closeProgress();
            }

        });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callGetHistoryApi() {

        try{

        Helper.getInstance().LogDetails("callGetHistoryApi","called"+conversation_reference_id+" "+activeChat.getTmVisitorId());

        openProgess();
        Call<GetMessagesResponse> call = apiService.getChatHistoty(ApiEndPoint.TM_SERVER_SOCKET_TOKEN, conversation_reference_id, limit);
        call.enqueue(new Callback<GetMessagesResponse>() {
            @Override
            public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {
                GetMessagesResponse apiResponse = response.body();
                closeProgress();

                if (apiResponse != null) {

                    if (apiResponse.isSuccess()) {

                        if (apiResponse.getMessages() != null && apiResponse.getMessages().size() > 0) {
                            no_chats_image.setVisibility(View.GONE);
                            messageList = apiResponse.getMessages();
                            updateChatData(messageList);
                            for (int i = 0; i < messageList.size(); i++) {
                                Helper.getInstance().LogDetails("callGetHistoryApi", "called" + messageList.get(i).getMessage_type()+" "+messageList.get(i).getMessage());
                            }


                        } else {
                            if (accessToken == null || accessToken.trim().isEmpty()) {
                                isApiCalled = true;
                                getAccessToken();
                            }
                            if(activeChat.getCreatedAt()!=null && !activeChat.getCreatedAt().equals("0000-00-00 00:00:00")){
                                Helper.getInstance().LogDetails("callGetHistoryApi", "called empty"+activeChat.getCreatedAt());

                                ChatModel model = new ChatModel();
                                list.add(model);
                                adapter.notifyDataSetChanged();


                            }


                        }

                    }
                }

                if(list!=null && list.size()>0){
                    no_chats_image.setVisibility(View.GONE);
                }
                else
                {
                    no_chats_image.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
                closeProgress();
            }

        });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getHistory() {

        try{

        if (receiver_id != null && tmUserId != null && !receiver_id.trim().isEmpty() && !tmUserId.trim().isEmpty()) {

            if (Helper.getConnectivityStatus(HistotyChatActivity.this)) {

                if (activeChat != null) {

                    if ( Integer.parseInt(role) ==Values.UserRoles.AGENT) {
                        //  Helper.getInstance().LogDetails("history ========","callGetConversationsApi ");
                        callGetConversationsApi();
                        getTrack();

                    } else {
                        // Helper.getInstance().LogDetails("history ========","callGetHistoryApi ");
                       // callGetConversationsApi();
                        callGetHistoryApi();
                        getTrack();
                    }
                }
                //

            } else {
                Toast.makeText(HistotyChatActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
            }

        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateChatData(List<Message> messages) {

        try{
        if(messages!=null && messages.size()>0){



        for (int i = 0; i < messages.size(); i++) {

            Message message = messages.get(i);
            ChatModel model = new ChatModel();


            if (i == messages.size() - 1) {
                lastMessageId = String.valueOf(message.getMessage_id());
            }

            model.setMessage_id(message.getMessage_id());
            model.setSender_id(message.getSender_id());
            model.setReceiver_id(message.getReceiver_id());
            model.setMessage(message.getMessage().toString());
            model.setIs_group(0);
            model.setIs_delivered(message.getIs_delivered());
            model.setIs_read(1);
            model.setConversation_reference_id(message.getConversation_reference_id());
            model.setAttachment(message.getAttachment());
            model.setAttachment_name(message.getAttachment_name());
            model.setAttachment_extension(message.getAttachment_extension());
            model.setCaption("");
            model.setOriginal_message("");
            model.setAttachmentDownloaded(false);
            model.setIs_downloaded(0);
            model.setIs_sync(1);
            model.setMessage_type(message.getMessage_type());
            model.setReference_id(message.getReference_id());
            model.setCreated_at(message.getCreated_at());

            ConversationTable table = new ConversationTable(HistotyChatActivity.this);
            boolean status = table.checkMessageIdAndInsertMessage(model);

            model.setAttachmentDownloaded(status);

            list.add(model);
            scrollItem();


        }
        //adapter.setList(agentsList);
        adapter.notifyDataSetChanged();
        }
        //closeProgress();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void scrollItem() {

        try{

        if (chatRecyclerView != null) {
            chatRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    chatRecyclerView.scrollToPosition(0);

                }
            });
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


private void setVisitPagesLayyou(){
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
             try{
            if(visitPages!=null && visitPages.size()>0){
                visitPagesLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                visitPagesLayout.setVisibility(View.GONE);
            }
             }catch (Exception e){
                 e.printStackTrace();
             }
        }
    });
}
    private void showCustomerInfo() {
        try {
            if(visitPages!=null && visitPages.size()>0){
                visitPagesLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                visitPagesLayout.setVisibility(View.GONE);
            }
            chat_layout.setVisibility(View.GONE);
            info_layout.setVisibility(View.VISIBLE);
            customer_name_tv.setVisibility(View.GONE);
            if (activeChat != null) {
                Helper.getInstance().LogDetails("showCustomerInfo",activeChat.toString()+"");
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
    private void showCustomerInfoDialog() {

        try {
            LinearLayout visitPagesLayout,phoneNumberLayout,emailLayout;
            ImageView info_close_icon,screenshotImage;

            TextView customer_name_tv,browser_name_tv,info_agent_name_tv,os_name_tv,email_tv,phone_number_tv,
                    entry_url_tv,history_tv,location_tv,ip_tv,start_time_tv;
            if(infoDialog==null)
            {
                infoDialog = new Dialog(HistotyChatActivity.this, R.style.DialogTheme);


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

                Helper.getInstance().closeKeyboard1(HistotyChatActivity.this, HistotyChatActivity.this);




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

                    if (activeChat.getStartTime() != null && !activeChat.getStartTime().trim().isEmpty()) {
                        String startTime = activeChat.getStartTime().trim();
                        if (startTime != null && !startTime.trim().isEmpty() && !startTime.equals("0000-00-00 00:00:00") && !activeChat.getStartTime().contains("Z")) {
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
                    if (verifyStoragePermissions(HistotyChatActivity.this)) {
                        takeScreenshot();
                    }

                }
            });



            infoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {


                }
            });

            infoDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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
             //   View v1 = getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                File imageFile = new File(mPath);

                if (imageFile != null) {

                    Toast.makeText(HistotyChatActivity.this, "Screenshot Saved.", Toast.LENGTH_LONG).show();
                    Helper.getInstance().LogDetails("takeScreenshot", "exists" + mPath);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    addImageToGallery(mPath, HistotyChatActivity.this);
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
                uri = AppFileProvider.getUriForFile(HistotyChatActivity.this, "com.tvisha.click2magic.fileprovider", imageFile);
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

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();


        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    private void getAccessToken() {
        try{

        accessToken = Session.getAccessToken(context);
        socketId = Session.getSocketId(context);
        if (isApiCalled) {
            isApiCalled = false;
            getHistory();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void makeCall(String number) {
        try{
        if (ActivityCompat.checkSelfPermission(HistotyChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HistotyChatActivity.this,
                    Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(HistotyChatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Values.Permissions.CAll_PERMISSION);
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
                    Toast.makeText(HistotyChatActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e){
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
                    makeCall(activeChat.getMobile());
                    break;
                case Values.Permissions.STORAGE_PERMISSION:
                    try {
                        if (adapter != null) {
                            adapter.dowloadTheFileAfterGivenStoragePermission(downloadAtachMentMessageId);
                        }
                        if (screenshotClicked) {
                            screenshotClicked=false;
                            takeScreenshot();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateDownloadFileStatus(int messageId, int senderId, int receiverId, String conversation_reference_id, String path) {
        try {
            if (conversationTable == null) {
                conversationTable = new ConversationTable(HistotyChatActivity.this);
            }
            conversationTable.updateDownloadFileStatus(messageId, senderId, receiverId, conversation_reference_id, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void progressDialog() {

        try {
            if (!(HistotyChatActivity.this).isFinishing()) {
                dialog = new Dialog(HistotyChatActivity.this, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
                dialog.setContentView(R.layout.custom_progress_bar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openProgess() {
        try{
        if (!(HistotyChatActivity.this).isFinishing()) {
            if (dialog != null) {
                dialog.show();
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeProgress() {
        try{
        if (!(HistotyChatActivity.this).isFinishing()) {
            if (dialog != null) {
                dialog.cancel();
                ;
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getTrack() {
        try {

            if (Utilities.getConnectivityStatus(HistotyChatActivity.this) <= 0) {
                Helper.getInstance().pushToast(HistotyChatActivity.this, "Please check your network connection...");
                return;
            }



            if(activeChat!=null){
                track_code=activeChat.getTrack_code();
                chat_start_date=activeChat.getStartTime();
                current_site_id=activeChat.getSiteId();

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
                            if(data.getData() != null && data.getData().size()>0 ){
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
                            Helper.getInstance().LogDetails("getTrack","fal"+data.getMessage());
                            //Helper.getInstance().pushToast(HistotyChatActivity.this, data.getMessage());
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

    private String getDateStartTime(String date) throws ParseException {

        try {

            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            startMin = c.get(Calendar.MINUTE);
            startHour = c.get(Calendar.HOUR);
            START_AM_PM = c.get(Calendar.AM_PM);

            return sdf1.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return  "";
        }
    }
}
