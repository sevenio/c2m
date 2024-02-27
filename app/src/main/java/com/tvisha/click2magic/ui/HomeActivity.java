package com.tvisha.click2magic.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tvisha.click2magic.BuildConfig;
import com.tvisha.click2magic.DataBase.ActiveChatsTable;
import com.tvisha.click2magic.DataBase.AgentsTable;
import com.tvisha.click2magic.DataBase.ArchiveChatsTable;
import com.tvisha.click2magic.DataBase.CategoriesTable;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.DataBase.TypingMessageTable;
import com.tvisha.click2magic.Fcm.FirebaseIDService;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.CustomViewPager;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.NotificationHelper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.ViewPagerAdapter;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.UpdateFcmApi;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.fragments.AgentsFragment;
import com.tvisha.click2magic.fragments.ArchivesFragment;
import com.tvisha.click2magic.fragments.HomeFragment;
import com.tvisha.click2magic.fragments.SettingsFragment;
import com.tvisha.click2magic.service.ClosingService;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BasicActivity implements TabLayout.OnTabSelectedListener,
        InstallStateUpdatedListener {

    @BindView(R.id.navigation)
    TabLayout navigation;

    @BindView(R.id.other_agent_name_tv)
    TextView other_agent_name_tv;

    @BindView(R.id.offlineImage)
    ImageView offlineImage;

    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    @BindView(R.id.backtoolbar)
    RelativeLayout backtoolbar;

    @BindView(R.id.settingsToolbar)
    RelativeLayout settingsToolbar;

    @BindView(R.id.logout_layout)
    LinearLayout logout_layout;

    @BindView(R.id.backLayout)
    LinearLayout backLayout;

    @BindView(R.id.viewPager)
    CustomViewPager viewPager;

    @OnClick(R.id.offlineImage)
    void offline(){
        Helper.getInstance().LogDetails("OFFLINE","clicked");
        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.obtainMessage(Values.RecentList.OFFLINE).sendToTarget();
        }
    }

    @OnClick(R.id.logout_layout)
    void logout(){
        if(HandlerHolder.homeFragmentUiHandler!=null)
        {
            HandlerHolder.homeFragmentUiHandler.obtainMessage(Values.RecentList.LOGOUT).sendToTarget();
        }
        try {


            List<SitesInfo> l=new ArrayList<>();

            Session.saveSiteInfoList(context, sitesInfoList, Session.SP_SITE_INFO);

            Session.endLoginSession(HomeActivity.this);
            Session.logout(context);

            if (conversationTable == null) {
                conversationTable = new ConversationTable(context);

            }
            if(typingMessageTable==null){
                typingMessageTable = new TypingMessageTable(context);
            }
            if(sitesTable==null){
                sitesTable=new SitesTable(context);
            }
            if(agentsTable==null){
                agentsTable=new AgentsTable(context);
            }
            if(activeChatsTable==null){
                activeChatsTable=new ActiveChatsTable(context);
            }
            if(archiveChatsTable==null){
                archiveChatsTable=new ArchiveChatsTable(context);
            }
            if(categoriesTable==null){
                categoriesTable=new CategoriesTable(context);
            }
            conversationTable.clearDb();
            typingMessageTable.clearDb();
            sitesTable.clearDb();
            agentsTable.clearDb();
            activeChatsTable.clearDb();
            archiveChatsTable.clearDb();
            categoriesTable.clearDb();

            NotificationHelper.getInstance().clearNotifications(HomeActivity.this);

            //  conversationTable.dropTable();
            disconnectSockets();
            Navigation.getInstance().openLoginPage(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @OnClick(R.id.backLayout)
     void back(){
        TabLayout.Tab tab = navigation.getTabAt(1);
        tab.select();
    }


    TabLayout.Tab selectedTab=null;
    static Context context;
    static Activity activity;
    public static List<SitesInfo> sitesInfoList = new ArrayList<>();

    boolean isSelf=true,isOtherAgent=false;
    Intent CamIntent, GalIntent;
    Uri selectedFileuri;
    String selectedImagePath;



    ViewPagerAdapter adapter;
    ConversationTable conversationTable=null;
    CategoriesTable categoriesTable=null;
    SitesTable sitesTable=null;
    TypingMessageTable typingMessageTable=null;
    AgentsTable agentsTable=null;
    ActiveChatsTable activeChatsTable=null;
    ArchiveChatsTable archiveChatsTable=null;



    static String appCheck="";
    String user_name,user_display_name, site_id, role = "0", siteToken = "", user_id, tmUserId = "", account_id = "", apiRole = "0", agent_id = "", accessToken = "",
            company_token = "", loginUserId = "", user_avatar = "";;
            String selectedPositionAgentId="",selectedPositionSiteId="",selectedPositionRole="";

    private String[] navLabels = {
            "Self",
            "Agents",
            "Archive",
            "Settings"
    };

    private int[] navIconsActive = {
            R.drawable.avatar_active,
            R.drawable.support_active,
            R.drawable.archive_active,
            R.drawable.settings_active

    };
    private int[] navIconsInActive = {
            R.drawable.avatar,
            R.drawable.support,
            R.drawable.archive,
            R.drawable.settings

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLogin = Session.getLoginStatus(HomeActivity.this);
        if (!isLogin) {
             //disconnectTmSocket();
            //Navigation.getInstance().openLoginPage(HomeActivity.this);
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
            finish();
        }

        context=HomeActivity.this;
        activity=HomeActivity.this;
        if (!Helper.getInstance().isMyServiceRunning(ClosingService.class, this))
        startService(new Intent(this, ClosingService.class));

        getSharedPreferenceData();
        storeFcmToken();
        getSiteData();
        initBottomMenuBar();
        processActivity();
        HandlerHolder.mainActivityUiHandler = uiHandler;

        if(appCheck==null){
            appCheck="";
            Helper.getInstance().LogDetails("appCheck","null");
            if(typingMessageTable==null){
                typingMessageTable=new TypingMessageTable(HomeActivity.this);
                typingMessageTable.clearDb();
            }
        }
        else
        {
            Helper.getInstance().LogDetails("appCheck","not null");
        }

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null ){
            if(bundle.containsKey(Values.MyActions.NOTIFICATION))
            {
                boolean isNotification=bundle.getBoolean(Values.MyActions.NOTIFICATION);
                if(isNotification){
                    NotificationHelper.getInstance().clearNotifications(HomeActivity.this);
                }
            }
        }

        checkAppUpdate();

    }



    private void checkAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfoTask != null) {
                Helper.getInstance().LogDetails("task====>", "" + appUpdateInfo.updateAvailability() + " " + appUpdateInfoTask.isSuccessful() + " versio " + BuildConfig.VERSION_NAME+" "+appUpdateInfo.availableVersionCode());
            }
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.unregisterListener(HomeActivity.this);
                    appUpdateManager.registerListener(HomeActivity.this);
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            HomeActivity.this,
                            Values.MyActionsRequestCode.APP_UPDATE);


                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    AppUpdateManager appUpdateManager;
    @Override
    public void onStateUpdate(InstallState state) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        if (appUpdateInfo.updateAvailability()
                                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//If an in-app update is already running, resume the update.
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        AppUpdateType.IMMEDIATE,
                                        this,
                                        Values.MyActionsRequestCode.APP_UPDATE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {

        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                    System.out.println("onBackPressed" + "This is last activity in the stack");

                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Are you sure?")
                            .setMessage("Do you really want to close the app?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();

                                }

                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {


                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onRestart();

                                }

                            })
                            .show();
                } else {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Helper.getInstance().LogDetails("HomeActivity***","onResume called");
        HandlerHolder.mainActivityUiHandler = uiHandler;
        SyncData.getArchievesApi(HomeActivity.this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Helper.getInstance().LogDetails("HomeActivity***","onRestart called");
        HandlerHolder.mainActivityUiHandler = uiHandler;
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Helper.getInstance().LogDetails("HomeActivity***","onDestroy called");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Helper.getInstance().LogDetails("HomeActivity***","onPause called");
        super.onPause();
    }

    @Override
    public int setView() {
        Helper.getInstance().LogDetails("setView","called");

        return R.layout.activity_home;


    }

    @Override
    public void socketOnConnect() {

    }

    @Override
    public void socketTmOnConnect() {

    }

    @Override
    public void socketOnDisconnect() {

    }

    @Override
    public void socketTmOnDisconnect() {

    }

    @Override
    public void connectToSocket() {

    }

    @Override
    public void connectToTmSocket() {

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void initViews() {

    }

    private void processActivity() {

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new AgentsFragment());
        fragmentList.add(new ArchivesFragment());
        fragmentList.add(new SettingsFragment());

        List<String> titleList = new ArrayList<>();
        titleList.add("Self");
        titleList.add("Agents");
        titleList.add("Archives");
        titleList.add("Settings");

        adapter = new ViewPagerAdapter(getSupportFragmentManager(),fragmentList, titleList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());



        //navigation.setupWithViewPager(viewPager);

        for(int tabIndex = 0; tabIndex < 4; tabIndex++){
            TabLayout.Tab tab = navigation.getTabAt(tabIndex);
            if (tab != null && tabIndex < navIconsActive.length){
                tab.setIcon(navIconsActive[tabIndex]);
            }
        }



//        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(navigation));

        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(isSelf)
                {
                    return false;
                }
                else
                {
                    return true;
                }

            }
        });



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }



            @Override
            public void onPageSelected(int i) {


            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Helper.getInstance().LogDetails("addOnPageChangeListener onPageScrollStateChanged","called "+i);
              /*  if(HandlerHolder.mainActivityUiHandler!=null){
                    HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.PAGE_CHANGED,i).sendToTarget();
                }*/
            }
        });
    }

    private void pageSelected(int pos){
        Helper.getInstance().LogDetails("addOnPageChangeListener pageSelected","called"+pos);
        swipeOn();
        if(!isSelf && isOtherAgent && pos!=0)
        {
            isOtherAgent=false;
            setSelfData();
        }
        else
        {
            if(pos==0){
                if(HandlerHolder.homeFragmentUiHandler!=null){
                    HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.AGENT_CLICKED);
                }
            }
        }

       switch (pos){
           case 0:
               if(isSelf)
               {
                   toolbar.setBackground(getResources().getDrawable(R.drawable.bottom_shadow_view));
                   toolbar.setVisibility(View.VISIBLE);
                   offlineImage.setVisibility(View.VISIBLE);
                   backLayout.setVisibility(View.GONE);
               }
               else
               {
                   toolbar.setVisibility(View.GONE);
                   backLayout.setVisibility(View.VISIBLE);

               }

               settingsToolbar.setVisibility(View.GONE);
               break;
           case 1:
               toolbar.setBackground(getResources().getDrawable(R.drawable.bottom_shadow_view));
               toolbar.setVisibility(View.VISIBLE);
               offlineImage.setVisibility(View.GONE);
               backLayout.setVisibility(View.GONE);
               settingsToolbar.setVisibility(View.GONE);
               if(HandlerHolder.agentsFragmentUiHandler!=null){
                   HandlerHolder.agentsFragmentUiHandler.sendEmptyMessage(Values.RecentList.TAB_CLICKED);
               }
               break;
           case 2:
               settingsToolbar.setVisibility(View.GONE);
               toolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_white_bg));
               toolbar.setVisibility(View.VISIBLE);
               offlineImage.setVisibility(View.GONE);
               backLayout.setVisibility(View.GONE);
               if(HandlerHolder.archiveFragmentHandler!=null){
                   HandlerHolder.archiveFragmentHandler.sendEmptyMessage(Values.RecentList.TAB_CLICKED);
               }
               break;
           case 3:

               toolbar.setVisibility(View.GONE);
               backLayout.setVisibility(View.GONE);
               settingsToolbar.setVisibility(View.VISIBLE);
               if(HandlerHolder.settingsFragmentHandler!=null){
                   HandlerHolder.settingsFragmentHandler.sendEmptyMessage(Values.RecentList.TAB_CLICKED);
               }
               break;
       }



    }




    @Override
    protected void userTypingMessage(JSONObject obj) {

    }

    @Override
    protected void agentChatEnded(JSONObject obj) {

    }

    @Override
    protected void chatEndedAgent(JSONObject obj) {

    }

    @Override
    protected void contactUpdate(JSONObject obj) {

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
    protected void agentStatusUpdated(JSONObject jsonObject) {

    }

    @Override
    protected void userChatEnded(JSONObject jsonObject) {

    }

    @Override
    protected void newOnline(JSONObject jsonObject) {

    }

    @Override
    protected void agentStatusInfo(JSONArray jsonObject) {

    }

    @Override
    public void messageSent(JSONObject object) {

    }

    @Override
    public void messageReceived(JSONObject object) {

    }

    @Override
    public void messageDelivered(JSONObject object) {

    }

    @Override
    public void messageRead(JSONObject object) {

    }

    @Override
    public void messageReadByMe(JSONObject object) {

    }

    @Override
    public void userAvailabilityStatus(JSONObject object) {

    }

    @Override
    public void userOffline(JSONObject object) {

    }

    @Override
    public void userOnline(JSONObject object) {

    }

    private void initBottomMenuBar() {

        try{

            for (int i = 0; i < navLabels.length; i++) {

                LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
                TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);

                tab_label.setText(navLabels[i]);

                if (i == 0) {

                    tab_label.setTextColor(getResources().getColor(R.color.tabActiveColor));
                    tab_icon.setImageResource(navIconsActive[i]);

                } else {
                    tab_label.setTextColor(getResources().getColor(R.color.tabInActiveColor));
                    tab_icon.setImageResource(navIconsInActive[i]);
                }
                navigation.addTab(navigation.newTab().setCustomView(tab));
            }



            navigation.addOnTabSelectedListener(this);
            navigation.addOnTabSelectedListener(HomeActivity.this);


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        try{

            View tabView = tab.getCustomView();
            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
            tab_label.setTextColor(getResources().getColor(R.color.tabActiveColor));
            tab_icon.setImageResource(navIconsActive[tab.getPosition()]);

          //  float h=getResources().getDimension(R.dimen.home_tab_indicator_height);
           // navigation.setSelectedTabIndicatorHeight((int)h);

            selectedTab=tab;
            Helper.getInstance().closeKeyboard1(context,activity);
            Helper.getInstance().LogDetails("TAB_CLICKED===","onTabSelected"+isOtherAgent+" "+isSelf+" ");
            viewPager.setCurrentItem(tab.getPosition(),false);

            //pageSelected(tab.getPosition());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        try{

            Helper.getInstance().LogDetails("TAB_CLICKED","onTabUnselected");

            View tabView = tab.getCustomView();
            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
            tab_label.setTextColor(getResources().getColor(R.color.tabInActiveColor));
            tab_icon.setImageResource(navIconsInActive[tab.getPosition()]);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        try{

            Helper.getInstance().LogDetails("TAB_CLICKED","onTabReselected"+isSelf);

            View tabView = tab.getCustomView();
            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
            tab_label.setTextColor(getResources().getColor(R.color.tabActiveColor));
            tab_icon.setImageResource(navIconsActive[tab.getPosition()]);
            //float h = getResources().getDimension(R.dimen.home_tab_indicator_height);
           // navigation.setSelectedTabIndicatorHeight((int) h);

            selectedTab = tab;
         //   changeTabColor(tab.getPosition());

            viewPager.setCurrentItem(tab.getPosition());


            if(!isSelf && isOtherAgent)
            {
                isOtherAgent=false;
                setSelfData();
            }
            else
            {
                if(tab.getPosition()==0){
                    if(HandlerHolder.homeFragmentUiHandler!=null){
                        HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.AGENT_CLICKED);
                    }
                }
            }

           //0 setFragment(tab.getPosition());


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void changeTabColor(int pos) {
        try {
            for (int i = 0; i < navLabels.length; i++) {

                LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
                TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);

                tab_label.setText(navLabels[i]);

                if (i == pos) {
                    tab_label.setTextColor(getResources().getColor(R.color.tabActiveColor));
                    tab_icon.setImageResource(navIconsActive[i]);

                } else {
                    tab_label.setTextColor(getResources().getColor(R.color.tabInActiveColor));
                    tab_icon.setImageResource(navIconsInActive[i]);
                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static Context  getContext(){
        if(context!=null)
        {
            return context;
        }
        else
        {
            return null;
        }

    }
    public static Activity  getActivity(){
        if(activity!=null)
        {
            return activity;
        }
        else
        {
            return null;
        }

    }

    private void disconnectTmSocket() {
        try{
            AppSocket application = (AppSocket) getApplication();
            application.disconnectTmSocket();
            tmSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void EmitAgentStatusUpdated(String checked, String siteToken, String site_id, String account_id) {
        try {

            // {site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token,is_online:agentStatus,account_id:account_id};
            if (site_id != null && !site_id.trim().isEmpty() && account_id != null && !account_id.trim().isEmpty() && checked != null && !checked.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("site_token", siteToken);
                object.put("site_id", Integer.parseInt(site_id));
                object.put("account_id", Integer.parseInt(account_id));
                object.put("agent_id", agent_id);



                object.put("is_online", Integer.parseInt(checked));
                if (mSocket != null && mSocket.connected()) {
                    String socketId=mSocket.id();
                    Helper.getInstance().LogDetails("Socket On connect c2m socket params: EmitAgentStatusUpdated",socketId+"");
                    Helper.getInstance().LogDetails("EmitAgentStatusUpdated ", object.toString());
                    mSocket.emit(SocketConstants.AGENT_STATUS_UPDATE, object);


                } else {
                    Helper.getInstance().LogDetails("EmitAgentStatusUpdated ", "socket dissconnected");

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSharedPreferenceData() {

        try{
            if(context==null){
                context=HomeActivity.this;
                activity=HomeActivity.this;
            }

            isSelf = Session.getIsSelf(context);
            if (isSelf) {
                user_id =  Session.getUserID(context);
                loginUserId =  Session.getUserID(context);
                agent_id =  Session.getUserID(context);
                tmUserId =  Session.getTmUserId(context);
                site_id =  Session.getSiteId(context);
                account_id =  Session.getAccountId(context);
                role =  Session.getUserRole(context);
                apiRole = role;
                user_name =  Session.getUserName(context);
                user_display_name =  Session.getUserDisplayName(context);

                company_token =  Session.getCompanyToken(context);



            } else {
                user_id = Session.getOtherUserId(context);
                loginUserId =  Session.getUserID(context);
                tmUserId = Session.getOtherUserTmUserId(context);
                site_id =Session.getOtherUserSiteId(context);
                account_id =  Session.getOtherUserAccountId(context);
                apiRole = Session.getOtherUserRole(context);
                role = Session.getUserRole(context);
                user_name =  Session.getUserName(context);
                user_display_name =  Session.getUserDisplayName(context);
                company_token =  Session.getCompanyToken(context);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void showAgentView(ActiveAgent activeAgent,boolean isClicked){


        Helper.getInstance().LogDetails("updateOtherDetails","item clicked "+activeAgent.toString());



        if(activeAgent!=null  )
        {
            if(!selectedPositionAgentId.equals(activeAgent.getUserId()) || isClicked)
            {
                selectedPositionAgentId=activeAgent.getAgentId();
                selectedPositionSiteId=activeAgent.getSiteId();
                selectedPositionRole=activeAgent.getRole();

                updateOtherDetails(activeAgent);

            }

        }




    }

    private void setSelfData()
    {

        try{

            Helper.getInstance().LogDetails("updateOtherDetails setSelfData Method", "ActiveAgent " );



            toolbar.setVisibility(View.VISIBLE);
            backtoolbar.setVisibility(View.GONE);

                    isSelf = true;
                    Session.setIsSelf(context,true);


                disconnectTmSocket();
                tmSocketConnection();

                getSharedPreferenceData();

                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                    for (int i = 0; i < sitesInfoList.size(); i++) {
                        if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                            sitesInfoList.get(i).getActiveChats().clear();
                        }
                        if(sitesInfoList.get(i).getActiveAgents()!=null && sitesInfoList.get(i).getActiveAgents().size()>0){
                            sitesInfoList.get(i).getActiveAgents().clear();
                        }
                    }
                    saveSiteData();
                }


              /*  TabLayout.Tab tab = navigation.getTabAt(0);
                tab.select();*/


                if(HandlerHolder.homeFragmentUiHandler!=null){
                    HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.AGENT_CLICKED);
                }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateOtherDetails(ActiveAgent activeAgent) {
        try{

            Helper.getInstance().LogDetails("updateOtherDetails Method", "ActiveAgent " + activeAgent.toString() +" ");

            if (activeAgent != null) {

                if (activeAgent.getUserId()!=null && activeAgent.getUserId().equals(loginUserId)) {


                    isSelf = true;
                    Session.setIsSelf(context,true);
                    toolbar.setVisibility(View.VISIBLE);
                    backtoolbar.setVisibility(View.GONE);


                } else {

                    toolbar.setVisibility(View.GONE);
                    backtoolbar.setVisibility(View.VISIBLE);

                    isSelf = false;

                    Session.setIsSelf(context,false);
                    Session.saveOtherUserId(context,activeAgent.getAgentId());
                    Session.saveOtherUserDisplayName(context,activeAgent.getUserName());
                    Session.saveOtherUserAccountId(context,activeAgent.getAccountId());
                    Session.saveOtherUserSiteId(context,activeAgent.getSiteId());
                    Session.saveOtherUserRole(context,activeAgent.getRole());
                    Session.saveOtherUserTmUserId(context,activeAgent.getTmUserId());

                    other_agent_name_tv.setText(Helper.getInstance().capitalize(activeAgent.getUserName()));

                }

                disconnectTmSocket();
                tmSocketConnection();

                getSharedPreferenceData();

                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                    for (int i = 0; i < sitesInfoList.size(); i++) {
                        if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                            sitesInfoList.get(i).getActiveChats().clear();
                        }
                    if(sitesInfoList.get(i).getActiveAgents()!=null && sitesInfoList.get(i).getActiveAgents().size()>0){
                        sitesInfoList.get(i).getActiveAgents().clear();
                    }
                    }
                    saveSiteData();
                }





                TabLayout.Tab tab = navigation.getTabAt(0);
                tab.select();

                if(!isSelf)
                {
                    try{
                        View tabView = selectedTab.getCustomView();
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                        tab_label.setTextColor(getResources().getColor(R.color.tabInActiveColor));
                        tab_icon.setImageResource(navIconsInActive[selectedTab.getPosition()]);
                      //  navigation.setSelectedTabIndicatorHeight(0);
                        isOtherAgent=true;



                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }




                if(HandlerHolder.homeFragmentUiHandler!=null){
                    HandlerHolder.homeFragmentUiHandler.sendEmptyMessage(Values.RecentList.AGENT_CLICKED);

                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveSiteData() {
        try{
            if (isSelf) {
               Session.saveSiteInfoList(HomeActivity.this, sitesInfoList, Session.SP_SITE_INFO);
            } else {
               Session.saveSiteInfoList(HomeActivity.this, sitesInfoList, Session.SP_OTHERS_SITE_INFO);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getSiteData() {
        try{
            if(sitesTable==null){
                sitesTable=new SitesTable(HomeActivity.this);
            }
            if (isSelf) {
                sitesInfoList=sitesTable.getSites();

            } else {
                sitesInfoList=sitesTable.getSites();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void camera() {
        try{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Apps");
            selectedFileuri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            List<Intent> yourIntentsList = new ArrayList<Intent>();
            CamIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            CamIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedFileuri);
            Intent chooser = Intent.createChooser(CamIntent, "Choose App");
            startActivityForResult(chooser, Values.MyActionsRequestCode.CAMERA_IMAGE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gallery() {
        try{
            GalIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            GalIntent.setType("image/*");
            Intent chooser = Intent.createChooser(GalIntent, "Choose App");
            startActivityForResult(chooser, Values.MyActionsRequestCode.GALLERY_IMAGE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Helper.getInstance().LogDetails("onActivityResult","HomeActivity called");
        try{
            switch (requestCode) {

                case Values.MyActionsRequestCode.CAMERA_IMAGE:
                    Helper.getInstance().LogDetails("onActivityResult","CAMERA_IMAGE");
                    if (selectedFileuri != null) {
                        selectedImagePath = getRealPathFromURI(selectedFileuri);
                        //  profileImage.setImageURI(selectedFileuri);
                        if (selectedImagePath != null) {
                            cropImage();
                            // uploadImageAWS(selectedImagePath);
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
                        //  profileImage.setImageURI(selectedImageUri);
                        selectedFileuri = selectedImageUri;
                        if (selectedFileuri != null) {
                            cropImage();
                        }
                        if (selectedImagePath != null) {
                            // uploadImageAWS(selectedImagePath);
                        }
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    Helper.getInstance().LogDetails("onActivityResult","CROP_IMAGE_ACTIVITY_REQUEST_CODE");
                    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        try {


                            File file = new File(result.getUri().getPath());
                            if (result.getUri().getPath() != null) {
                                //uploadImageAWS(result.getUri().getPath());
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
                                            if(HandlerHolder.settingsFragmentHandler!=null){
                                                JSONObject jsonObject=new JSONObject();
                                                jsonObject.put("path",compressedImageFile.getPath());
                                                HandlerHolder.settingsFragmentHandler.obtainMessage(Values.RecentList.CROP_RESULT,jsonObject).sendToTarget();
                                            }
                                           // uploadImageAWS(compressedImageFile.getPath());
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

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case Values.RecentList.RECENT_NETWORK_CONNECTED:
                        if (Helper.getConnectivityStatus(HomeActivity.this)) {
                            Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "called");
                            if (mSocket == null) {
                                Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "null called");
                                socketConnection();
                            } else if (mSocket != null && !mSocket.connected()) {
                                appSocket.connectSocket();
                                Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "reconnect called");
                            }

                            if (tmSocket == null) {
                                tmSocketConnection();
                            } else if (tmSocket != null && !tmSocket.connected()) {
                                appSocket.connectTmSocket();
                            }

                        }
                        break;
                    case Values.RecentList.FCM_TRIGGER:
                        if (Helper.getConnectivityStatus(HomeActivity.this)) {
                            Helper.getInstance().LogDetails("FCM_TRIGGER", "called");
                            if (mSocket == null) {
                                Helper.getInstance().LogDetails("FCM_TRIGGER", "null called");
                                socketConnection();
                            } else if (mSocket != null && !mSocket.connected()) {
                                Helper.getInstance().LogDetails("FCM_TRIGGER", "reconnect called");
                                appSocket.connectSocket();
                            }

                            if (tmSocket == null) {
                                tmSocketConnection();
                            } else if (tmSocket != null && !tmSocket.connected()) {
                               appSocket.connectTmSocket();
                            }
                        }
                        break;

                    case Values.RecentList.OPEN_CAMERA:
                        Helper.getInstance().LogDetails("onActivityResult","OPEN_CAMERA");
                        camera();
                        break;
                    case Values.RecentList.OPEN_GALLERY:
                        Helper.getInstance().LogDetails("onActivityResult","OPEN_GALLERY");
                        gallery();
                        break;
                    case Values.RecentList.OPEN_AGENT_TAB:
                        TabLayout.Tab tab = navigation.getTabAt(1);
                        tab.select();
                        break;
                    case Values.RecentList.PAGE_CHANGED:
                            int i=(int) msg.obj;
                            pageSelected(i);
                            break;
                    case Values.RecentList.ENABLE_SWIPE:
                        swipeOn();
                        break;
                    case Values.RecentList.DISABLE_SWIPE:
                        swipeOff();
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };



    private void disconnectSockets() {
        disconnectSocket();
        disconnectTmSocket();

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
    String deviceId="",device_fcm_token="";
    private void storeFcmToken() {

        String tm_user_id="",user_id="",company_token="";

        boolean  isLogin=Session.getLoginStatus(HomeActivity.this);
        tm_user_id= Session.getTmUserId(context);
        user_id= Session.getUserID(context);
        company_token= Session.getCompanyToken(context);
        boolean isFcmUpdated=Session.getFcmUpdate(HomeActivity.this);
        deviceId=Session.getDeviceId(HomeActivity.this);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if(isFcmUpdated || !isFcmUpdated){

            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseApp.initializeApp(HomeActivity.this);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Helper.getInstance().LogDetails("callUpdateFcmApi storeFcmToken", "getInstanceId failed"+ task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Session.saveFcmToken(HomeActivity.this,token);
                            Helper.getInstance().LogDetails("callUpdateFcmApi storeFcmToken","token "+token);

                        }
                    });



            Helper.getInstance().LogDetails("callUpdateFcmApi storeFcmToken","called");

            device_fcm_token=Session.getFcmToken(HomeActivity.this);

            if (device_fcm_token == null  || device_fcm_token.trim().isEmpty() ) {
                if (FirebaseInstanceId.getInstance() != null && FirebaseInstanceId.getInstance().getToken() != null) {
                    device_fcm_token = FirebaseInstanceId.getInstance().getToken();
                    Session.saveFcmToken(HomeActivity.this,device_fcm_token);
                } else {

                    device_fcm_token = FirebaseIDService.getRefreshToken();
                    Session.saveFcmToken(HomeActivity.this,device_fcm_token);
                }
            }


            if (device_fcm_token != null && deviceId!=null  && !isFcmUpdated) {
                Helper.getInstance().LogDetails("callUpdateFcmApi device_fcm_token api calling", ""+device_fcm_token  +device_fcm_token+" deviceId "+deviceId+" isFcmUpdated "+isFcmUpdated);

                if(isLogin)
                {
                    callUpdateFcmApi(user_id,tm_user_id,company_token,device_fcm_token);
                }

            }
            else
            {
                if(deviceId!=null){
                    Helper.getInstance().LogDetails("callUpdateFcmApi device_fcm_token", " dev  "+deviceId+" "+isFcmUpdated);
                }
                if(device_fcm_token!=null){
                    Helper.getInstance().LogDetails("callUpdateFcmApi device_fcm_token", "fcm  "+device_fcm_token+" "+isFcmUpdated);
                }

            }
        }



    }
    private void callUpdateFcmApi(String user_id,String tm_user_id,String company_token,String device_fcm_token) {

        if (Utilities.getConnectivityStatus(getApplicationContext()) <= 0){
            Helper.getInstance().pushToast(getApplicationContext(), "Please check your network connection...");
            return;
        }




        retrofit2.Call<UpdateFcmApi.UpdateFcmResponse> call = UpdateFcmApi.getApiService().updateFcm(user_id,tm_user_id, ApiEndPoint.token,company_token,device_fcm_token,"2");

        call.enqueue(new Callback<UpdateFcmApi.UpdateFcmResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<UpdateFcmApi.UpdateFcmResponse> call, @NonNull Response<UpdateFcmApi.UpdateFcmResponse> response) {


                UpdateFcmApi.UpdateFcmResponse data = response.body();

                if (data != null){

                    Helper.getInstance().LogDetails("callUpdateFcmApi  data ===> ",data.toString());

                    if (data.getSuccess()) {

                        Session.saveFcmUpdate(HomeActivity.this,true);

                    }
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<UpdateFcmApi.UpdateFcmResponse> call, @NonNull Throwable t) {
                Helper.getInstance().LogDetails("callUpdateFcmApi onFailure ",t.getCause()+" "+t.getLocalizedMessage());
                t.printStackTrace();

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Helper.getInstance().LogDetails("onRequestPermissionsResult home","called "+requestCode);
        try {
            switch (requestCode) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public  void  swipeOn() {
        Helper.getInstance().LogDetails("swipeOn ","called");
        viewPager.setSwipeable(true);
    }

    public  void  swipeOff() {
        Helper.getInstance().LogDetails("swipeOn swipeOff","called");
        viewPager.setSwipeable(false);
    }
}
