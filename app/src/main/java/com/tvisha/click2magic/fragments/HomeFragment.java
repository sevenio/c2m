package com.tvisha.click2magic.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvisha.click2magic.DataBase.ActiveChatsTable;
import com.tvisha.click2magic.DataBase.AgentsTable;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.DataBaseValues;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.DataBase.TypingMessageTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.VisitorCompanyAdapter;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.ActiveAgentsApi;
import com.tvisha.click2magic.api.post.ActiveChatsApi;
import com.tvisha.click2magic.api.post.SiteAgentsData;
import com.tvisha.click2magic.api.post.SiteData;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.socket.SocketConstants;
import com.tvisha.click2magic.ui.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import retrofit2.Callback;
import retrofit2.Response;




public class HomeFragment extends Fragment  {

    View rootView;

    @BindView(R.id.selected_agent_name_tv)
    TextView selected_agent_name_tv;

    @BindView(R.id.visitor_company_recycler_view)
    RecyclerView visitor_company_recycler_view;

    @BindView(R.id.agentNameLayout)
    RelativeLayout agentNameLayout;


    String selectedPositionAgentId="0",selectedPositionSiteId="0",selectedPositionRole="",selectedAgentName="",
            user_name,user_display_name, site_id, role = "0", user_id, tmUserId = "", account_id = "", apiRole = "0", agent_id = "",
            company_token = "", loginUserId = "";
    ActiveAgent activeAgent=null,all=null,agent=null;
    ConversationTable conversationTable = null;
    TypingMessageTable typingMessageTable = null;
    ActiveChatsTable activeChatsTable=null;

    SitesTable sitesTable=null;
    AgentsTable agentsTable=null;
    Context context;
    Activity activity;
    public static io.socket.client.Socket mSocket, tmSocket;
    public static List<SitesInfo> sitesInfoList = new ArrayList<>();
    VisitorCompanyAdapter visitorCompanyAdapter;
    Dialog dialog = null;

    boolean isSelf=true,isRestart=false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_home, container, false);
        context=HomeActivity.getContext();
        activity=HomeActivity.getActivity();
        ButterKnife.bind(this, rootView);
        if (conversationTable == null) {
            conversationTable = new ConversationTable(context);
        }
        conversationTable.getTheUnreadCountOfAll(context);
        progressDialog();
        getSharedPreferenceData();
        getSiteData();
        initViews();
        processFragment();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Helper.getInstance().LogDetails("setUserVisibleHint","called"+isVisibleToUser);
        if(isVisibleToUser){
            if(HandlerHolder.mainActivityUiHandler!=null){
                HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.PAGE_CHANGED,0).sendToTarget();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }



    @Override
    public void onResume() {
        if (visitorCompanyAdapter != null) {
            visitorCompanyAdapter.selectionMode = true;
            visitorCompanyAdapter.notifyDataSetChanged();
        }
        HandlerHolder.homeFragmentUiHandler = uiHandler;
        HandlerHolder.fragmentSocketHandler=socketHandler;
        HandlerHolder.fragmentTmSocketHandler=tmSocketHandler;

        Helper.getInstance().LogDetails("HomeFragment===","onResume called");
        initzC2mSocket();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (visitorCompanyAdapter != null) {
            visitorCompanyAdapter.selectionMode = true;
            visitorCompanyAdapter.notifyDataSetChanged();
        }
        Helper.getInstance().LogDetails("HomeFragment===","onPause called");
        super.onPause();
    }





    private void processFragment() {

        HandlerHolder.fragmentSocketHandler=socketHandler;
        HandlerHolder.fragmentTmSocketHandler=tmSocketHandler;
        HandlerHolder.homeFragmentUiHandler = uiHandler;

        setData();
    }
    private void setData() {
        try{

            Helper.getInstance().LogDetails("setData update", "" + isSelf);
            if (!isSelf) {
                Session.setIsSelf(context,true);
                disconnectTmSocket();
                tmSocketConnection();
                getSharedPreferenceData();
                getSiteData();
            }




            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                Helper.getInstance().LogDetails("sitesInfoList Intent data", "sp size " + sitesInfoList.size() + "");


                if(isSelf){
                    updateSiteListWithLocalDb();
                    setUnreadCount();
                }
                else
                {
                    getActiveChatsApi();
                }

            } else {
                logout();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void logout() {
        try {




            Session.endLoginSession(context);
            Session.logout(context);

            if (conversationTable == null) {
                conversationTable = new ConversationTable(context);

            }
            conversationTable.clearDb();
            typingMessageTable.clearDb();

            //  conversationTable.dropTable();
            disconnectSockets();
            Navigation.getInstance().openLoginPage(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void getSiteData() {
        try{
            Helper.getInstance().LogDetails("getLogin details getSiteData","called"+isSelf);
            if(sitesTable==null){
                sitesTable=new SitesTable(context);
            }
            if(sitesInfoList!=null && sitesInfoList.size()>0){
                sitesInfoList.clear();
                if(visitorCompanyAdapter!=null){
                    visitorCompanyAdapter.notifyDataSetChanged();
                }
            }
            sitesInfoList.addAll(sitesTable.getSitesOfUser(selectedPositionSiteId,isSelf));

/*
            if (isSelf) {
                sitesInfoList.addAll(sitesTable.getSites());

            } else {
                sitesInfoList.addAll(sitesTable.getSites());

            }*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initViews() {

        conversationTable=new ConversationTable(context);
        typingMessageTable=new TypingMessageTable(context);
        activeChatsTable=new ActiveChatsTable(context);
        agentsTable=new AgentsTable(context);
        sitesTable=new SitesTable(context);

        if (visitor_company_recycler_view != null) {
            LinearLayoutManager layoutManager5 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            visitor_company_recycler_view.setLayoutManager(layoutManager5);
            visitor_company_recycler_view.setNestedScrollingEnabled(false);

            visitorCompanyAdapter = new VisitorCompanyAdapter(context, sitesInfoList, isSelf,HomeFragment.this);
            visitorCompanyAdapter.selectionMode = true;
            visitor_company_recycler_view.setAdapter(visitorCompanyAdapter);
        }

        initzC2mSocket();


    }
    AppSocket application;
    private void initzC2mSocket(){

        if(mSocket==null){
            if(application==null)
            application = (AppSocket) activity. getApplication();
            mSocket=application.getSocketInstance();
            Helper.getInstance().LogDetails("HomeFragment initzC2mSocket", "socket init called");
            if(mSocket==null){



                Helper.getInstance().LogDetails("HomeFragment initzC2mSocket", "socket null called");
            }
            else
            {
                if(!mSocket.connected())
                {
                   application.connectSocket();
                }
                Helper.getInstance().LogDetails("HomeFragment initzC2mSocket", "socket not null called");
            }
        }
    }
    private void updateSiteListWithLocalDb() {

        try{
                checkAgentStatus();
                Helper.getInstance().LogDetails("HomeFragment updateSiteListWithLocalDb", "size " +" "+selectedPositionSiteId);

                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    for (int j = 0; j < sitesInfoList.size(); j++) {
                        String sid = sitesInfoList.get(j).getSiteId();

                        boolean isPresent = false;

                            if (sid != null && !sid.trim().isEmpty() ) {
                                isPresent = true;
                                if(activeChatsTable==null){
                                    activeChatsTable=new ActiveChatsTable(context);
                                }
                                List<ActiveChat> activeChatArrayList = activeChatsTable.getActiveChatList(Integer.parseInt(sid));
                                Helper.getInstance().LogDetails("updateSiteListWithLocalDb"," size ====="+activeChatArrayList.size());
                                List<ActiveChat> temp = setUnreadCount(activeChatArrayList);
                                if (sitesInfoList.get(j).getActiveChats() != null && sitesInfoList.get(j).getActiveChats().size() > 0) {
                                    sitesInfoList.get(j).getActiveChats().clear();
                                    sitesInfoList.get(j).setActiveChats(temp);
                                } else {
                                    sitesInfoList.get(j).setActiveChats(temp);
                                }
                                if(agentsTable==null){
                                    agentsTable=new AgentsTable(context);
                                }

                             sitesInfoList.get(j).setAgentsCount(agentsTable.getTheAgentCount(Integer.parseInt(sid)));
                             sitesInfoList.get(j).setActiveAgentsCount(agentsTable.getTheActiveAgentCount(Integer.parseInt(sid)));

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(visitorCompanyAdapter!=null){
                                            visitorCompanyAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }


                        if (!isPresent) {
                            List<ActiveChat> activeChatArrayList = new ArrayList<>();
                            sitesInfoList.get(j).setActiveChats(activeChatArrayList);
                        }

                    }
                }

            saveSiteData();
            if(visitorCompanyAdapter!=null){
                visitorCompanyAdapter.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void getActiveChatsApi() {

        try{


            Helper.getInstance().LogDetails("getActiveChatsApi ", ApiEndPoint.token + " " + user_id + " " + site_id + " " + account_id + " " + apiRole + " == " + company_token);

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                updateSiteList(null);
                return;
            }

            if (!isRestart) {
              //  isRestart=true;
              //  openProgess();
            }

            retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call = ActiveChatsApi.getApiService().getActiveChats(ApiEndPoint.token, company_token, user_id, site_id, account_id, role);

            call.enqueue(new Callback<ActiveChatsApi.ActiveChatsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call, @NonNull Response<ActiveChatsApi.ActiveChatsResponse> response) {

                    closeProgress();

                    ActiveChatsApi.ActiveChatsResponse data = response.body();

                    if (data != null) {
                        if (data.isSuccess()) {
                            Helper.getInstance().LogDetails("getActiveChatsApi ", "true");
                            if (data.getData() != null) {
                                Helper.getInstance().LogDetails("getActiveChatsApi ", data.getData().size() + "");
                                updateSiteList(data.getData());
                                setUnreadCount();
                            } else {
                                Helper.getInstance().LogDetails("getActiveChatsApi ", "false");
                              
                            }

                        } else {
                            Helper.getInstance().pushToast(context, data.getMessage());
                           
                        }
                    } else {
                        Helper.getInstance().pushToast(context, "Server connection failed");
                       
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ActiveChatsApi.ActiveChatsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
           
        }
    }

    private void getActiveAgentsApi() {

        try{

            Helper.getInstance().LogDetails("getActiveAgentsApi", "updateAgentsSiteList ==== "+ApiEndPoint.token + " " + loginUserId + " " +company_token);

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }


            retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call = ActiveAgentsApi.getApiService().getActiveAgents(ApiEndPoint.token, company_token, loginUserId);

            call.enqueue(new Callback<ActiveAgentsApi.ActiveAgentsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call, @NonNull Response<ActiveAgentsApi.ActiveAgentsResponse> response) {
                    closeProgress();

                    ActiveAgentsApi.ActiveAgentsResponse data = response.body();

                    if (data != null) {
                        if (data.isSuccess()) {
                            Helper.getInstance().LogDetails("getActiveAgentsApi", "true");
                            if (data.getData() != null && data.getData().size() > 0) {
                                Helper.getInstance().LogDetails("getActiveAgentsApi =====", "size " + data.getData().size());
                                updateAgentsSiteList(data.getData());
                            } else {
                                Helper.getInstance().LogDetails("getActiveAgentsApi", "size 0");
                                updateAgentsSiteList(null);
                            }

                        } else {
                            Helper.getInstance().LogDetails("getActiveAgentsApi", "false");
                            Helper.getInstance().pushToast(context, data.getMessage());
                            updateAgentsSiteList(null);
                        }
                    } else {

                        Helper.getInstance().LogDetails("getActiveAgentsApi", "res null");
                        Helper.getInstance().pushToast(context, "Server connection failed");
                        updateAgentsSiteList(null);
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Helper.getInstance().LogDetails("getActiveAgentsApi =====", "onFailure " + t.getLocalizedMessage() + " " + t.getCause());
                    updateAgentsSiteList(null);
                    closeProgress();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void  updateAgentsSiteList(List<SiteAgentsData> data) {

        try{

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int j = 0; j < sitesInfoList.size(); j++) {
                    List<ActiveAgent> activeAgents = new ArrayList<>();
                    sitesInfoList.get(j).setActiveAgents(activeAgents);
                    List<ActiveAgent> allAgents = new ArrayList<>();
                    sitesInfoList.get(j).setAllAgents(allAgents);
                    List<ActiveAgent> agents = new ArrayList<>();
                    sitesInfoList.get(j).setAgents(agents);
                }
            }
            if (data != null && data.size() > 0) {

                Helper.getInstance().LogDetails("updateAgentsSiteList", "size " + data.size());

                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    for (int i = 0; i < data.size(); i++) {

                        String siteId = data.get(i).getSiteId();
                        Helper.getInstance().LogDetails("updateAgentsSiteList", "siteId " + siteId);


                        for (int j = 0; j < sitesInfoList.size(); j++) {

                            String sid = sitesInfoList.get(j).getSiteId();


                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                List<ActiveAgent> activeAgentsList = data.get(i).getActiveAgents();
                                Helper.getInstance().LogDetails("updateAgentsSiteList****", data.get(i).getActiveAgents().size()+"");
                                List<ActiveAgent> agents = new ArrayList<>();
                                agents.addAll(data.get(i).getActiveAgents());

                                if (sitesInfoList.get(j).getActiveAgents() != null && sitesInfoList.get(j).getActiveAgents().size() > 0) {
                                    sitesInfoList.get(j).getActiveAgents().add(activeAgentsList.get(0));

                                } else {
                                    List<ActiveAgent> l=new ArrayList<>();
                                    if(activeAgentsList !=null && activeAgentsList.size()>0){
                                        for(int m=0;m<activeAgentsList.size();m++){
                                            if(activeAgentsList.get(m).getIsOnline()!=null ){
                                                if(activeAgentsList.get(m).getIsOnline().equals("1"))
                                                {
                                                    l.add(activeAgentsList.get(m));
                                                }
                                            }
                                        }
                                        sitesInfoList.get(j).setActiveAgents(l);
                                        sitesInfoList.get(j).setAllAgents(activeAgentsList);
                                        sitesInfoList.get(j).setAgents(agents);
                                    }
                                    else
                                    {
                                        sitesInfoList.get(j).setActiveAgents(l);
                                        sitesInfoList.get(j).setAllAgents(activeAgentsList);
                                        sitesInfoList.get(j).setAgents(agents);
                                    }

                                }

                                Helper.getInstance().LogDetails("updateAgentsSiteList==", "sid size" + sid+" "+sitesInfoList.get(j).getAllAgents().size());


                            }


                        }

                    }

                    visitorCompanyAdapter.notifyDataSetChanged();

                    if (role != null && !role.trim().isEmpty()) {
                        if (Integer.parseInt(role) != Values.UserRoles.AGENT) {

                           // setSelectedAgentUserData();
                        }
                    }
                }


            } else {
                Helper.getInstance().LogDetails("updateAgentsSiteList", "size empty" + role);
                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                    for (int j = 0; j < sitesInfoList.size(); j++) {
                        List<ActiveAgent> activeAgents = new ArrayList<>();
                        sitesInfoList.get(j).setActiveAgents(activeAgents);
                        List<ActiveAgent> allAgents = new ArrayList<>();
                        sitesInfoList.get(j).setAllAgents(allAgents);
                        List<ActiveAgent> agents = new ArrayList<>();
                        sitesInfoList.get(j).setAgents(agents);
                    }


                    visitorCompanyAdapter.notifyDataSetChanged();

                }

                if (role != null && !role.trim().isEmpty()) {
                    if (Integer.parseInt(role) != Values.UserRoles.AGENT) {
                        Helper.getInstance().LogDetails("updateAgentsSiteList", "calling spinner" + role);
                        //  initModeSpinner();
                        //setSelectedAgentUserData();
                    }
                }
            }

            saveSiteData();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void saveSiteData() {
        try{
            if (isSelf) {
                Session.saveSiteInfoList(context, sitesInfoList, Session.SP_SITE_INFO);
            } else {
               Session.saveSiteInfoList(context, sitesInfoList, Session.SP_OTHERS_SITE_INFO);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUnreadCount() {
        try{
            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();
                    int count = 0;
                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                        for (int j = 0; j < activeChatArrayList.size(); j++) {
                            if (activeChatArrayList.get(j).getTmVisitorId() != null && !activeChatArrayList.get(j).getTmVisitorId().isEmpty()) {
                                Helper.getInstance().LogDetails("updateUnreadCount", "called " + activeChatArrayList.get(j).getTmVisitorId() + " " + tmUserId + " " + activeChatArrayList.get(j).getChatReferenceId());
                                int unreadCount = conversationTable.getTheUnreadMessageAvailable(Integer.parseInt(activeChatArrayList.get(j).getTmVisitorId()), Integer.parseInt(tmUserId), activeChatArrayList.get(j).getChatReferenceId());
                                activeChatArrayList.get(j).setUnread_message_count(unreadCount);
                                count = count + unreadCount;
                            /*ChatModel model=conversationTable.getLatestMessage(activeChatArrayList.get(j).getChatReferenceId());
                            if(model.getCreated_at()==null || model.getCreated_at().trim().isEmpty())
                            {
                                model.setCreated_at("0000-00-00 00:00:00");
                            }
                            activeChatArrayList.get(j).setChatModel(model);*/
                            }
                        }

                    }
                    sitesInfoList.get(i).setUnread_message_count(count);

                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitorCompanyAdapter.notifyDataSetChanged();
                    }
                });

                saveSiteData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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

                    
                        dialog.cancel();
                    
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateSiteList(List<SiteData> data) {

        try{

            checkAgentStatus();
           // EmitGetAgentChats();
            if (data != null && data.size() > 0) {

                Helper.getInstance().LogDetails("updateSiteList", "size " + data.size() +" "+selectedPositionSiteId);

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
                                        Helper.getInstance().LogDetails("updateSiteList=============",sid+" "+separated[i]+" "+selectedPositionSiteId);
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
                            else if(selectedPositionRole!=null && !selectedPositionRole.trim().isEmpty() && Integer.parseInt(selectedPositionRole)==Values.UserRoles.ADMIN){
                                sitesInfoList.get(j).setPresent(true);
                            }
                            else{
                                sitesInfoList.get(j).setPresent(false);
                            }
                        }
                        boolean isPresent = false;
                        for (int i = 0; i < data.size(); i++) {
                            String siteId = data.get(i).getSiteId();

                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                isPresent = true;
                                List<ActiveChat> activeChatArrayList = data.get(i).getActiveChats();
                                List<ActiveChat> temp = setUnreadCount(activeChatArrayList);
                                if (sitesInfoList.get(j).getActiveChats() != null && sitesInfoList.get(j).getActiveChats().size() > 0) {
                                    sitesInfoList.get(j).getActiveChats().clear();
                                    sitesInfoList.get(j).setActiveChats(temp);
                                } else {
                                    sitesInfoList.get(j).setActiveChats(temp);
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(visitorCompanyAdapter!=null){
                                            visitorCompanyAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }

                        }
                        if (!isPresent) {
                            List<ActiveChat> activeChatArrayList = new ArrayList<>();
                            sitesInfoList.get(j).setActiveChats(activeChatArrayList);
                        }

                    }
                }


            } else {
                setSitesToAgent();
            }

            saveSiteData();
            if(visitorCompanyAdapter!=null){
                visitorCompanyAdapter.notifyDataSetChanged();
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private List<ActiveChat> setUnreadCount(List<ActiveChat> activeChatArrayList) {
        try{

            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                if (conversationTable == null) {
                    conversationTable = new ConversationTable(context);
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

    private void getSharedPreferenceData() {

        try{


            isSelf = Session.getIsSelf(context);
            if (isSelf) {
                user_id =  Session.getUserID(context);
                loginUserId = Session.getUserID(context);
                agent_id =  Session.getUserID(context);
                tmUserId = Session.getTmUserId(context);
                site_id = Session.getSiteId(context);
                account_id =   Session.getAccountId(context);
                role =  Session.getUserRole(context);
                apiRole = role;
                user_name =  Session.getUserName(context);
                user_display_name =  Session.getUserDisplayName(context);

                company_token =  Session.getCompanyToken(context);

                selectedPositionAgentId="";
                selectedPositionSiteId="";
                selectedPositionRole="";

                if(activeAgent==null)
                {

                    activeAgent=new ActiveAgent();

                    activeAgent.setAccountId(account_id);
                    activeAgent.setAgentId(loginUserId);
                    activeAgent.setSiteId(site_id);
                    activeAgent.setRole(role);
                    activeAgent.setUserName(user_name);
                    activeAgent.setTmUserId(tmUserId);
                    activeAgent.setUserId(loginUserId);
                }


                if(agent==null){
                    agent=new ActiveAgent();
                    agent.setAccountId(account_id);
                    agent.setAgentId(loginUserId);
                    agent.setSiteId(site_id);
                    agent.setRole(role);
                    agent.setUserName("Self");
                    agent.setTmUserId(tmUserId);
                    agent.setUserId(loginUserId);

                }

                if(all==null){
                    all=new ActiveAgent();
                    all.setAccountId(account_id);
                    all.setAgentId("");
                    all.setSiteId(site_id);
                    all.setRole(role);
                    all.setUserName("All");
                    all.setTmUserId(tmUserId);
                    all.setUserId(loginUserId);
                }

            } else {
                user_id = Session.getOtherUserId(context);
                loginUserId =  Session.getUserID(context);
                tmUserId = Session.getOtherUserTmUserId(context);
                site_id = Session.getOtherUserSiteId(context);
                account_id =  Session.getOtherUserAccountId(context);
                apiRole =  Session.getOtherUserRole(context);
                role =  Session.getUserRole(context);
                user_name = Session.getUserName(context);
                user_display_name =  Session.getUserDisplayName(context);
                company_token =  Session.getCompanyToken(context);
                selectedAgentName = Session.getOtherUserDisplayName(context);
                selectedPositionAgentId=user_id;
                selectedPositionSiteId=site_id;
                selectedPositionRole=apiRole;

                if(sitesTable==null){
                    sitesTable=new SitesTable(context);
                }

              sitesTable.getSitesOfUser(selectedPositionSiteId,isSelf);
             // activeChatsTable.getActiveChatsOfSites();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void checkAgentStatus() {
        try{

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    if(mSocket==null){
                        initzC2mSocket();
                    }
                    if(mSocket!=null && !mSocket.connected())
                    {
                        mSocket.connected();
                    }
                    if (mSocket != null && mSocket.connected()) {
                         Helper.getInstance().LogDetails("HomeFragment updateAgentStatusInfo checkAgentStatus","called");
                        EmitCheckAgentStatus(sitesInfoList.get(i).getSiteToken(), sitesInfoList.get(i).getSiteId());
                    } else {
                          Helper.getInstance().LogDetails("HomeFragment updateAgentStatusInfo checkAgentStatus","not called");
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void EmitCheckAgentStatus(String siteToken, String siteId) {

        Helper.getInstance().LogDetails("HomeFragment EmitCheckAgentStatus","called");

        /*{site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token}; c2mSocket.emit("check_agent_status",agentStatusData);*/
        try {
            if (agent_id != null && !agent_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("site_token", siteToken);
                if(isSelf){
                    object.put("agent_id", Integer.parseInt(agent_id));
                }
                else
                {
                    object.put("agent_id", Integer.parseInt(selectedPositionAgentId));
                }



                object.put("site_id", siteId);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("HomeFragment EmitCheckAgentStatus ",object.toString() +" "+isSelf);
                    mSocket.emit(SocketConstants.CHECK_AGENT_STATUS, object);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void emitOffline() {

        Helper.getInstance().LogDetails("HomeFragment emitOffline","called");


        try {
            if (agent_id != null && !agent_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();

                if(isSelf){
                    object.put("agent_id", agent_id);

                }
                else
                {
                    object.put("agent_id", selectedPositionAgentId);
                }

                object.put("account_id", account_id);
                object.put("site_token", Session.getSiteToken(context));

                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("HomeFragment emitOffline ",object.toString() +" "+isSelf);
                    mSocket.emit(SocketConstants.AGENT_LOGFF_EVERYWHERE, object);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void disconnectSockets() {
        disconnectSocket();
        disconnectTmSocket();
        mSocket = null;
        tmSocket = null;
    }
    private void disconnectSocket() {
        try{
            AppSocket application = (AppSocket) activity. getApplication();
            application.disconnectSocket();
            mSocket=null;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void disconnectTmSocket() {
        try{
            AppSocket application = (AppSocket)activity .getApplication();
            application.disconnectTmSocket();
            tmSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler socketHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            //Helper.getInstance().LogDetails("HomeFragment","handleMessage called"+msg.what+" "+msg.obj.toString());
            try {
                switch (msg.what) {
                    case SocketConstants.SocketEvents.EVENT_CONNECT:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_CONNECT called");
                        socketOnConnect();

                        break;
                    case SocketConstants.SocketEvents.EVENT_DISCONNECT:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_DISCONNECT called"+msg.toString());
                        socketOnDisconnect();
                        break;

                    case SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_AGENT_STATUS_UPDATED called"+msg.toString());
                        agentStatusUpdated((JSONObject) msg.obj);

                        break;
                    case SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_USER_CHAT_ENDED called"+msg.toString());
                        userChatEnded((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_CHAT_ENDED_AGENT called"+msg.toString());
                        chatEndedAgent((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_CONTACT_UPDATE called"+msg.toString());
                        contactUpdate((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_AGENT_ACTIVITY called"+msg.toString());
                        agentActivity((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_NEW_ONLINE:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_NEW_ONLINE called"+msg.toString());
                        newOnline((JSONObject) msg.obj);
                        break;

                    case SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_CHECK_AGENT_STATUS_INFO called"+msg.toString());
                        agentStatusInfo((JSONArray) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_USER_TYPING_TO_AGENT called"+msg.toString());
                        userTypingMessage((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_AGENT_CHAT_ENDED called"+msg.toString());
                        agentChatEnded((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_AGENT_LOGFF_EVERYWHERE:
                        Helper.getInstance().LogDetails("HomeFragment","EVENT_AGENT_LOGFF_EVERYWHERE called"+msg.toString());
                       setOfflineAllSites();
                        break;




                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void tmSocketConnection() {
        Helper.getInstance().LogDetails("CHECK BaseActivity ","tmSocketConnection called");

        try{
            if(application==null)
           application = (AppSocket)activity. getApplication();

            if (tmSocket == null) {
                Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection", "tmSocket null");
                application.disconnectTmSocket();
                AppSocket.tmEventHandler=tmSocketHandler;
                tmSocket = application.initTmSocket();
            }
            if (tmSocket != null) {
                if(  !tmSocket.connected())
                {
                    Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket not connected");
                    application.connectTmSocket();
                }
                else
                {
                    Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket connected");
                }

            } else {
                Helper.getInstance().LogDetails("CHECK BaseActivity tmSocketConnection after", "tmSocket null");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void socketOnConnect() {
        try{
            if(mSocket==null){
                AppSocket application = (AppSocket) activity. getApplication();
                mSocket=application.getSocketInstance();
                Helper.getInstance().LogDetails("HomeFragment socketOnConnect", "socket null called");
            }

            if(mSocket!=null && !mSocket.connected())
            {
               application.connectSocket();
            }

            if (mSocket != null && mSocket.connected()) {
                Helper.getInstance().LogDetails("HomeFragment checkAgentStatus", "called");
                checkAgentStatus();
                Helper.getInstance().LogDetails("HomeFragment updateSiteListWithLocalDb","calling");
                if(isSelf)
                {
                    updateSiteListWithLocalDb();
                }

                EmitGetAgentChats();

            }
            else
            {
                Helper.getInstance().LogDetails("HomeFragment checkAgentStatus", "not called");
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void socketOnDisconnect(){

    }

    private void agentStatusUpdated(JSONObject object){
        try{
            Helper.getInstance().LogDetails("EmitAgentStatusUpdated  updateAgentStatusupdated ", object.toString() +isSelf+" "+agent_id  +selectedPositionAgentId);
            if (object != null) {

                String agentid = object.optString("agent_id");
                String siteId = object.optString("site_id");
                String site_token = object.optString("site_token");
                int is_online = object.optInt("is_online");
                int account_id = object.optInt("account_id");

                if (object.optString("agent_id") != null ) {
                    if ( (isSelf && agent_id != null && object.optString("agent_id").equals(agent_id)) ||  (!isSelf && selectedPositionAgentId!=null && object.optString("agent_id").equals(selectedPositionAgentId))  ) {
                        if (sitesInfoList != null && sitesInfoList.size() > 0) {
                            for (int i = 0; i < sitesInfoList.size(); i++) {
                                String sid = sitesInfoList.get(i).getSiteId();
                                if (siteId.equals(sid)) {
                                    sitesInfoList.get(i).setOnline_status(is_online);

                                    Helper.getInstance().LogDetails("EmitAgentStatusUpdated  updateAgentStatusupdated=== ",  isSelf+" "+agent_id  +"  "+selectedPositionAgentId+" "+object.optString("agent_id")+sitesInfoList.get(i).getSiteName()+" "+is_online);

                                   activity. runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            visitorCompanyAdapter.notifyDataSetChanged();

                                        }
                                    });

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

    private void setOfflineAllSites(){
        try{


            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    String sid = sitesInfoList.get(i).getSiteId();

                        sitesInfoList.get(i).setOnline_status(0);


                        activity. runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                visitorCompanyAdapter.notifyDataSetChanged();

                            }
                        });


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void userChatEnded(JSONObject jsonObject){
        try{
            if (jsonObject != null) {
                String agentId = jsonObject.optString("agent_id");
                Helper.getInstance().LogDetails("updateUserChatEnded", jsonObject.toString());
                if (agentId != null && user_id != null && agentId.equals(user_id)) {

                    String siteId = jsonObject.optString("site_id");


                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sid = sitesInfoList.get(i).getSiteId();

                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {

                                List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                    for (int j = 0; j < activeChatArrayList.size(); j++) {

                                        if (activeChatArrayList.get(j).getChatId() != null && !activeChatArrayList.get(j).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {
                                            if (jsonObject.optString("chat_id").equals(activeChatArrayList.get(j).getChatId())) {

                                                activeChatArrayList.get(j).setChatStatus("2");

                                                activity.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        visitorCompanyAdapter.notifyDataSetChanged();

                                                    }
                                                });


                                                saveSiteData();


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

    private void chatEndedAgent(JSONObject jsonObject){

    }

    private void contactUpdate(JSONObject jsonObject){

        try{
            if (jsonObject != null) {
                String agentId = jsonObject.optString("agent_id");
                JSONObject data=  jsonObject.optJSONObject("data");
                String type=data.getString("type");
                String value=data.getString("value");
                if(data!=null ){
                    Helper.getInstance().LogDetails("updateContact","=== "+data.toString() +"  "+type+"  "+value);
                }
                Helper.getInstance().LogDetails("updateContact", jsonObject.toString());
                if (agentId != null && user_id != null && agentId.equals(user_id)) {

                    String siteToken = jsonObject.optString("DJdZj6NIMFU1Q");
                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sToken = sitesInfoList.get(i).getSiteToken();

                            if (siteToken != null && !siteToken.trim().isEmpty() && sToken != null && !sToken.trim().isEmpty() && siteToken.equals(sToken)) {

                                List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                    for (int j = 0; j < activeChatArrayList.size(); j++) {

                                        if (activeChatArrayList.get(j).getChatReferenceId() != null && !activeChatArrayList.get(j).getChatReferenceId().isEmpty() && jsonObject.optString("chatReference") != null && !jsonObject.optString("chatReference").isEmpty()) {
                                            if (jsonObject.optString("chatReference").equals(activeChatArrayList.get(j).getChatReferenceId())) {


                                                if(type.equals("1")){
                                                    activeChatArrayList.get(j).setMobile(value);

                                                }
                                                else
                                                {
                                                    activeChatArrayList.get(j).setEmail(value);

                                                }
                                                activity.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        visitorCompanyAdapter.notifyDataSetChanged();

                                                    }
                                                });


                                                saveSiteData();


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

    private void agentActivity(JSONObject jsonObject){

        try {


            if (jsonObject != null) {
                String user_id = jsonObject.optString("user_id");
                String site_id = jsonObject.optString("site_id");
                String agent_staus = jsonObject.optString("agent_staus");
                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    int position = -1;
                    boolean isPresent = false;
                    Helper.getInstance().LogDetails("updateAgentOnlineStatus", "called" + jsonObject.toString());
                    List<ActiveAgent> activeAgents = new ArrayList<>();
                    List<ActiveAgent> allAgents = new ArrayList<>();

                    for (int i = 0; i < sitesInfoList.size(); i++) {
                        String sid = sitesInfoList.get(i).getSiteId();
                        if (site_id != null && !site_id.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && site_id.equals(sid)) {
                            activeAgents = sitesInfoList.get(i).getActiveAgents();
                            allAgents = sitesInfoList.get(i).getAllAgents();
                            position = i;
                            break;
                        }
                    }

                    if (user_id != null && !user_id.trim().isEmpty()) {
                        if (activeAgents != null && activeAgents.size() > 0) {

                            for (int i = 0; i < activeAgents.size(); i++) {
                                if (activeAgents.get(i).getAgentId() != null && !activeAgents.get(i).getAgentId().trim().isEmpty() && user_id.equals(activeAgents.get(i).getAgentId())) {
                                    Helper.getInstance().LogDetails("updateAgentOnlineStatus", "called" + user_id + " " + activeAgents.get(i).getAgentId());
                                    isPresent = true;
                                    if (agent_staus.equals("0")) {
                                        activeAgents.remove(i);
                                    } else {
                                        activeAgents.get(i).setIsOnline(agent_staus);
                                    }

                                    sitesInfoList.get(position).setActiveAgents(activeAgents);

                                    break;
                                }
                            }

                        } else {
                            isPresent = false;
                        }

                        if (!isPresent) {
                            if (allAgents != null && allAgents.size() > 0) {

                                for (int i = 0; i < allAgents.size(); i++) {
                                    if (allAgents.get(i).getAgentId() != null && !allAgents.get(i).getAgentId().trim().isEmpty() && user_id.equals(allAgents.get(i).getAgentId())) {
                                        Helper.getInstance().LogDetails("updateAgentOnlineStatus", "else called" + user_id + " " + allAgents.get(i).getAgentId());
                                        isPresent = true;
                                        allAgents.get(i).setIsOnline(agent_staus);
                                        if (activeAgents != null && activeAgents.size() >= 0 && agent_staus.equals("1")) {
                                      /* if(allAgents.get(i).getRole()!=null && Integer.parseInt(allAgents.get(i).getRole())!=Values.UserRoles.ADMIN)
                                       {*/
                                            activeAgents.add(allAgents.get(i));
                                            sitesInfoList.get(position).setActiveAgents(activeAgents);
                                            /* }*/

                                        } else if (activeAgents == null && agent_staus.equals("1")) {
                                     /*  if(allAgents.get(i).getRole()!=null && Integer.parseInt(allAgents.get(i).getRole())!=Values.UserRoles.ADMIN)
                                       {*/
                                            activeAgents = new ArrayList<>();
                                            activeAgents.add(allAgents.get(i));
                                            sitesInfoList.get(position).setActiveAgents(activeAgents);
                                            /* }*/


                                        }

                                        break;
                                    }
                                }

                            }
                        }

                        activity.runOnUiThread(new Runnable() {
                            public void run() {

                                if (visitorCompanyAdapter != null)
                                    visitorCompanyAdapter.notifyDataSetChanged();
                            }
                        });

                        saveSiteData();


                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void newOnline(JSONObject jsonObject){

        try{


            if (jsonObject != null) {
                Helper.getInstance().LogDetails("HomeFragment","updateNewOnlineUser called"+user_id+"  "+jsonObject.toString());
                String agentId = jsonObject.optString("agent_id");
                if (agentId != null && user_id != null && agentId.equals(user_id)) {

                    boolean isPresent = false;
                    int position = -1;
                    Helper.getInstance().LogDetails("updateNewOnlineUser", "called" + jsonObject.toString());

                    if (sitesInfoList != null && sitesInfoList.size() > 0) {

                        String chat_id, tm_visitor_id, site_id;
                        chat_id = jsonObject.optString("chat_id");
                        tm_visitor_id = jsonObject.optString("tm_visitor_id");
                        site_id = jsonObject.optString("site_id");

                        Helper.getInstance().LogDetails("updateNewOnlineUser", "called" + isPresent);
                        List<ActiveChat> activeChatArrayList = new ArrayList<>();
                        if (sitesInfoList != null && sitesInfoList.size() > 0) {
                            for (int i = 0; i < sitesInfoList.size(); i++) {
                                String sid = sitesInfoList.get(i).getSiteId();
                                if (site_id != null && !site_id.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && site_id.equals(sid)) {
                                    activeChatArrayList = sitesInfoList.get(i).getActiveChats();
                                    position = i;
                                    break;
                                }
                            }
                        }
                        if (chat_id != null && !chat_id.trim().isEmpty()) {
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

                        if (!isPresent) {
                            ActiveChat activeChat = setActiveUserData(jsonObject);

                            if (activeChat != null) {

                                //sitesInfoList.get(position).getActiveChatsApi().add(0, activeChat);
                                if (conversationTable == null) {
                                    conversationTable = new ConversationTable(context);
                                }
                           /* ChatModel model=conversationTable.getLatestMessage(activeChat.getChatReferenceId());
                            if(model.getCreated_at()==null || model.getCreated_at().trim().isEmpty())
                            {
                                model.setCreated_at(getCurrentDate());
                            }
                            activeChat.setChatModel(model);*/

                                if (sitesInfoList != null && sitesInfoList.get(position).getActiveChats() != null) {

                                    sitesInfoList.get(position).getActiveChats().add(0, activeChat);
                                } else {
                                    List<ActiveChat> activeChatList = new ArrayList<>();
                                    activeChatList.add(0, activeChat);
                                    sitesInfoList.get(position).setActiveChats(activeChatList);
                                }


                                activity.runOnUiThread(new Runnable() {
                                    public void run() {

                                        visitorCompanyAdapter.notifyDataSetChanged();

                                    }
                                });

                                saveSiteData();
                            }
                        }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void agentStatusInfo(JSONArray jsonArray){
        try{

            if (jsonArray != null && jsonArray.length() > 0) {
                Helper.getInstance().LogDetails("updateAgentStatusInfo jsonArray", jsonArray.toString());

                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject != null) {


                        String siteId = jsonObject.getString("site_id");
                        int isOnline = jsonObject.getInt("is_online");

                        Helper.getInstance().LogDetails("updateAgentStatusInfo ", siteId + " " + isOnline+" "+isSelf+" "+agent_id+" "+selectedPositionAgentId);
                        if(jsonObject.optString("agent_id")!=null) {
                            if ((isSelf && agent_id != null && jsonObject.optString("agent_id").equals(agent_id)) ||
                                    (!isSelf && selectedPositionAgentId != null && jsonObject.optString("agent_id").equals(selectedPositionAgentId))) {
                                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                                    for (int i = 0; i < sitesInfoList.size(); i++) {
                                        String sid = sitesInfoList.get(i).getSiteId();
                                        if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                            sitesInfoList.get(i).setOnline_status(isOnline);
                                            Helper.getInstance().LogDetails("updateAgentStatusInfo ", siteId + " " + isOnline + " " + sitesInfoList.get(i).getSiteName());
                                        }
                                    }

                                }

                            }
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                visitorCompanyAdapter.notifyDataSetChanged();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Helper.getInstance().LogDetails("updateAgentStatusInfo ", "jsonArray empty");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void userTypingMessage(JSONObject jsonObject){

    }

    private void  agentChatEnded(JSONObject jsonObject){

        try{
            if (jsonObject != null) {
                String agentId = jsonObject.optString("agent_id");
                Helper.getInstance().LogDetails("updateAgentChatEnded", jsonObject.toString());
                Helper.getInstance().LogDetails("HomeFragment", user_id+" "+jsonObject.toString());
                if (agentId != null && user_id != null && agentId.equals(user_id)) {

                    String siteId = jsonObject.optString("site_id");


                    if (sitesInfoList != null && sitesInfoList.size() > 0) {
                        for (int i = 0; i < sitesInfoList.size(); i++) {

                            String sid = sitesInfoList.get(i).getSiteId();

                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {

                                List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();

                                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                                    for (int j = 0; j < activeChatArrayList.size(); j++) {

                                        if (activeChatArrayList.get(j).getChatId() != null && !activeChatArrayList.get(j).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {
                                            if (jsonObject.optString("chat_id").equals(activeChatArrayList.get(j).getChatId())) {

                                                activeChatArrayList.remove(j);
                                                activity.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        visitorCompanyAdapter.notifyDataSetChanged();

                                                    }
                                                });


                                                saveSiteData();


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
    private ActiveChat setActiveUserData(JSONObject jsonObject) {

        Helper.getInstance().LogDetails("setActiveUserData", jsonObject.toString());


        ActiveChat activeChat = null;
        try {
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


    @SuppressLint("HandlerLeak")
    Handler tmSocketHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);



            super.handleMessage(msg);

                    try {
                        switch (msg.what) {
                            case SocketConstants.TmSocketEvents.SOCKET_CONNECT:
                                socketTmOnConnect();
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_DISCONNECT:
                                socketOnDisconnect();
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_SENT:
                                JSONObject object = (JSONObject) msg.obj;
                                if (object != null) {

                                    messageSent(object);

                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_SEND_MESSAGE:
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_RECEIVE_MESSAGE:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {
                                        messageReceived(jsonObject);

                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_DELIVERED:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {


                                        messageDelivered(jsonObject);


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {


                                        messageRead(jsonObject);


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_SYNC_OFFLINE_MESSAGES:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        syncOfflineMessages();
                                        //  SyncData.syncNonReadMessage(BasicActivity.this, Integer.parseInt(receiver_tm_user_id),Integer.parseInt(tmUserId));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;


                            case SocketConstants.TmSocketEvents.SOCKET_GET_MISSING_MESSAGES:
                                try {
                                    JSONArray jsonArray = (JSONArray) msg.obj;
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        getMissingMessages();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_MESSAGE_READ_BY_ME:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    if (jsonObject != null && jsonObject.length() > 0) {
                                        messageReadByMe(jsonObject);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;


                            case SocketConstants.TmSocketEvents.SOCKET_CHANGE_USER_AVAILABILITY_STATUS:
                                try {
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case SocketConstants.TmSocketEvents.SOCKET_USER_AVAILABILITY_STATUS:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userAvailabilityStatus(jsonObject);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_USER_OFFLINE:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userOffline(jsonObject);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case SocketConstants.TmSocketEvents.SOCKET_USER_ONLINE:
                                try {
                                    Helper.getInstance().LogDetails("checkUserStatus Base","called");
                                    JSONObject jsonObject = (JSONObject) msg.obj;

                                    if (jsonObject != null) {

                                        userOnline(jsonObject);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;


                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            };

    private void socketTmOnConnect(){

    }

    private void messageSent(JSONObject jsonObject){

    }

    private void messageReceived(JSONObject jsonObject){
        updateUnreadCount(jsonObject);
        setUnreadCount();
    }

    private void messageDelivered(JSONObject jsonObject){

    }

    private void messageRead(JSONObject jsonObject){
        if (isSelf) {

            updateUnreadCount(jsonObject);
            setUnreadCount();
        }
    }

    private void syncOfflineMessages(){

    }

    private void getMissingMessages(){

    }
    private void messageReadByMe(JSONObject jsonObject){
        if (isSelf) {
            Helper.getInstance().LogDetails("***messageReadByMe", "called");
            updateUnreadCount(jsonObject);
            setUnreadCount();
        }
    }

    private void userAvailabilityStatus(JSONObject jsonObject){

    }

    private void userOffline(JSONObject jsonObject){

    }

    private void userOnline(JSONObject jsonObject){

    }

    public void updateUnreadCount(JSONObject jsonObject) {

        /*{"sender_id":"1187","receiver_id":2,"message_type":0,
                "message_id":14823,"created_at":"2019-05-27 12:38:03","reference_id":"",
                "is_group":0,"is_reply":0,"message":"ok","conversation_reference_id":"cb85738654e5b8c46c"}*/
    /*    {"sender_id":"1190","receiver_id":2,"message_type":1,"message_id":15593,"created_at":"2019-05-28 11:42:26","reference_id":"","is_group":0,"is_reply":0,"attachment":"https:\/\/s3.amazonaws.com\/files.c2m\/user\/28052019114225\/IMG-20190522-WA0003.jpg.jpeg",
                "attachment_extension":"jpeg","attachment_name":"IMG-20190522-WA0003.jpg.jpeg",
                "conversation_reference_id":"696b87eacbc8b67d55"}*/

        /*{"message_id":29805,"conversation_reference_id":"6e150406a78e5d0dee","is_read_by_all":1,"is_group":0,"sender_id":3451}*/

        try {
            Helper.getInstance().LogDetails("message received called updateUnreadCount", tmUserId + " " + jsonObject.toString());

            if (jsonObject != null && sitesInfoList != null && sitesInfoList.size() > 0) {

                int receiverId = jsonObject.optInt("receiver_id");
                int senderId = jsonObject.optInt("sender_id");
                String conversationReferenceId = jsonObject.optString("conversation_reference_id");
                for (int j = 0; j < sitesInfoList.size(); j++) {
                    List<ActiveChat> activeChatArrayList = sitesInfoList.get(j).getActiveChats();
                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                        for (int i = 0; i < activeChatArrayList.size(); i++) {

                            String tmUid, crid;
                            tmUid = activeChatArrayList.get(i).getTmVisitorId();
                            crid = activeChatArrayList.get(i).getChatReferenceId();

                            if (conversationReferenceId != null && tmUid != null && !tmUid.trim().isEmpty() && tmUserId != null && !tmUserId.isEmpty()) {
                                if (receiverId == Integer.parseInt(tmUserId) && senderId == Integer.parseInt(tmUid) && crid != null && !crid.trim().isEmpty() && crid.equals(conversationReferenceId)) {
                                    int unreadCount = conversationTable.getTheUnreadMessageAvailable(senderId, Integer.parseInt(tmUserId), conversationReferenceId);
                                    Helper.getInstance().LogDetails("message received called updateUnreadCount", tmUserId + " " + activeChatArrayList.get(i).getTmVisitorId() + " " + unreadCount);
                                    activeChatArrayList.get(i).setUnread_message_count(unreadCount);
                                    if (unreadCount == 1) {
                                       /* ChatModel model=conversationTable.getLatestMessage(activeChatArrayList.get(j).getChatReferenceId());
                                        if(model.getCreated_at()==null || model.getCreated_at().trim().isEmpty())
                                        {
                                            model.setCreated_at("0000-00-00 00:00:00");
                                        }

                                        activeChatArrayList.get(i).setChatModel(model);*/

                                       /* ActiveChat temp=activeChatArrayList.get(i);
                                        activeChatArrayList.remove(i);
                                        activeChatArrayList.add(0,temp);*/

                                       /* ActiveChat temp1= sitesInfoList.get(j).getActiveChats().get(i);
                                        sitesInfoList.get(j).getActiveChats().remove(i);
                                        sitesInfoList.get(j).getActiveChats().add(0,temp1);
                                        saveSiteData();*/
                                    }
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            visitorCompanyAdapter.notifyDataSetChanged();

                                        }

                                    });
                                    break;

                                } else {
                                    if (crid != null && !crid.trim().isEmpty() && crid.equals(conversationReferenceId)) {
                                        int unreadCount = conversationTable.getTheUnreadMessageAvailable(senderId, Integer.parseInt(tmUserId), conversationReferenceId);
                                        Helper.getInstance().LogDetails("message received called updateUnreadCount", tmUserId + " " + activeChatArrayList.get(i).getTmVisitorId() + " " + unreadCount);
                                        activeChatArrayList.get(i).setUnread_message_count(unreadCount);
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                visitorCompanyAdapter.notifyDataSetChanged();

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void EmitGetAgentChats() {

        /*{site_id:site_id,agent_id:LOGIN_USER_ID,site_token:site_token}; c2mSocket.emit("check_agent_status",agentStatusData);*/
        Helper.getInstance().LogDetails("EmitGetAgentChats ","called ");
        try {
            if (agent_id != null && !agent_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                // object.put("site_token", siteToken);
                if(isSelf){
                    object.put("agent_id", Integer.parseInt(agent_id));
                }
                else
                {
                    object.put("agent_id", Integer.parseInt(selectedPositionAgentId));
                }

                //   object.put("site_id", siteId);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("EmitGetAgentChats ",object.toString());
                    mSocket.emit(SocketConstants.GET_AGENT_CHATS, object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Helper.getInstance().LogDetails("EmitGetAgentChats ","res "+args[0].toString());
                            JSONArray jsonArray=(JSONArray) args[0];
                           // getAgentChats(jsonArray);
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
                        List<ActiveChat> activeChatArrayList = sitesInfoList.get(i).getActiveChats();
                        if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                            int removedIndex=-1;
                            for(int k=0;k<activeChatArrayList.size();k++)
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
                                            if (activeChatArrayList.get(k).getChatId() != null && !activeChatArrayList.get(k).getChatId().isEmpty() && jsonObject.optString("chat_id") != null && !jsonObject.optString("chat_id").isEmpty()) {
                                                if (jsonObject.optString("chat_id").equals(activeChatArrayList.get(k).getChatId())) {

                                                    isPresent=true;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                }
                                if(!isPresent){
                                    activeChatArrayList.remove(k);
                                    removedIndex=k;
                                    saveSiteData();
                                    Helper.getInstance().LogDetails("removeDeletedChats","removed "+k);

                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            visitorCompanyAdapter.notifyDataSetChanged();

                                        }
                                    });
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

    private void getAgentChats(JSONArray jsonArray)
    {
        try{
            if(jsonArray!=null && jsonArray.length()>0)
            {
                Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats",""+jsonArray.length());
                for(int j=0; j<jsonArray.length();j++){
                    JSONObject jsonObject=jsonArray.optJSONObject(j);
                    ActiveChat activeChat = setActiveUserData(jsonObject);

                    if (jsonObject != null) {
                        Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats","object "+jsonObject.toString());
                        String agentId = jsonObject.optString("agent_id");
                        if (agentId != null && user_id != null && agentId.equals(user_id)) {

                            boolean isPresent = false;
                            int position = -1;
                            Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats", "called" + jsonObject.toString());

                            if (sitesInfoList != null && sitesInfoList.size() > 0) {

                                String chat_id, tm_visitor_id, site_id;
                                chat_id = jsonObject.optString("chat_id");
                                tm_visitor_id = jsonObject.optString("tm_visitor_id");
                                site_id = jsonObject.optString("site_id");

                                Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats", "called" + isPresent);
                                List<ActiveChat> activeChatArrayList = new ArrayList<>();
                                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                                    for (int i = 0; i < sitesInfoList.size(); i++) {
                                        String sid = sitesInfoList.get(i).getSiteId();
                                        if (site_id != null && !site_id.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && site_id.equals(sid)) {
                                            activeChatArrayList = sitesInfoList.get(i).getActiveChats();
                                            position = i;
                                            break;
                                        }
                                    }
                                }
                                if (chat_id != null && !chat_id.trim().isEmpty()) {
                                    if (activeChatArrayList != null && activeChatArrayList.size() > 0) {

                                        for (int i = 0; i < activeChatArrayList.size(); i++) {
                                            if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().trim().isEmpty() && tm_visitor_id.equals(activeChatArrayList.get(i).getTmVisitorId())) {
                                                Helper.getInstance().LogDetails("EmitGetAgentChats getAgentChats", "called" + tm_visitor_id + " " + activeChatArrayList.get(i).getTmVisitorId());
                                                isPresent = true;
                                                break;
                                            }
                                        }

                                    } else {
                                        isPresent = false;
                                    }

                                }

                                if (!isPresent) {

                                    if (activeChat != null) {


                                        if (conversationTable == null) {
                                            conversationTable = new ConversationTable(activity);
                                        }


                                        if (sitesInfoList != null && sitesInfoList.get(position).getActiveChats() != null) {

                                            sitesInfoList.get(position).getActiveChats().add(0, activeChat);
                                        } else {
                                            List<ActiveChat> activeChatList = new ArrayList<>();
                                            activeChatList.add(0, activeChat);
                                            sitesInfoList.get(position).setActiveChats(activeChatList);
                                        }


                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {

                                                visitorCompanyAdapter.notifyDataSetChanged();

                                            }
                                        });
                                        saveSiteData();
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

    private void deleteSite(int siteId){
        Helper.getInstance().LogDetails("deleteSite Called",siteId+" ");
        if(sitesInfoList!=null && sitesInfoList.size()>0)
        {
            for(int i=0;i<sitesInfoList.size();i++){
                int sid= Integer.parseInt(sitesInfoList.get(i).getSiteId());
                if(sid==siteId){
                    sitesInfoList.remove(i);
                    saveSiteData();
                    if(visitorCompanyAdapter!=null)
                    visitorCompanyAdapter.notifyDataSetChanged();
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
                if(visitorCompanyAdapter!=null)
                    visitorCompanyAdapter.notifyDataSetChanged();
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
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {

                    case Values.RecentList.MESSAGES_SYNC_COMPLETED:
                        setUnreadCount();
                        break;
                    case Values.RecentList.SITE_DELETED:
                        int siteId=(int)msg.obj;
                        deleteSite(siteId);
                        break;
                    case Values.RecentList.SITE_ADDED:
                        JSONObject jsonObject=(JSONObject) msg.obj;
                        addSite(jsonObject);
                        break;
                    case Values.RecentList.SITES_SYNC_COMPLETED:
                        Helper.getInstance().LogDetails("SitesTableAsynckTask SITES_SYNC_COMPLETED","called");
                      /*  if(sitesInfoList!=null && sitesInfoList.size()>0){
                            sitesInfoList.clear();
                            if(visitorCompanyAdapter!=null){
                                visitorCompanyAdapter.notifyDataSetChanged();
                            }
                        }

                        getSiteData();
                        if (sitesInfoList != null && sitesInfoList.size() > 0) {
                            Helper.getInstance().LogDetails("sitesInfoList Intent data", "sp size " + sitesInfoList.size() + "");
                            if(visitorCompanyAdapter!=null){
                                visitorCompanyAdapter.notifyDataSetChanged();
                            }

                            if(isSelf){
                                updateSiteListWithLocalDb();
                                setUnreadCount();
                            }
                            else
                            {
                                getActiveChatsApi();
                            }

                        }*/
                        break;
                    case Values.RecentList.ACTIVE_CHATS_SYNC_COMPLETED:
                        Helper.getInstance().LogDetails("ACTIVE_CHATS_SYNC_COMPLETED","called");
                        if(isSelf)
                        {
                            updateSiteListWithLocalDb();
                        }

                        break;
                    case Values.RecentList.ACCESS_TOKEN_UPDATED:
                        break;
                    case Values.RecentList.AGENT_CLICKED:
                        Helper.getInstance().LogDetails("AGENT_CLICKED","called");
                        boolean self=isSelf;
                        getSharedPreferenceData();
                        if((self && !isSelf) || (!self && isSelf) )
                        {
                            setSitesToAgent();
                            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                                for (int i = 0; i < sitesInfoList.size(); i++) {
                                    if (sitesInfoList.get(i).getActiveChats() != null && sitesInfoList.get(i).getActiveChats().size() > 0) {
                                        sitesInfoList.get(i).getActiveChats().clear();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(visitorCompanyAdapter!=null){
                                                    visitorCompanyAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                                    }
                                    if(sitesInfoList.get(i).getActiveAgents()!=null && sitesInfoList.get(i).getActiveAgents().size()>0){
                                        sitesInfoList.get(i).getActiveAgents().clear();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(visitorCompanyAdapter!=null){
                                                    visitorCompanyAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                                    }
                                }
                                saveSiteData();
                            }
                        }

                        if(isSelf){
                            agentNameLayout.setVisibility(View.GONE);
                            selected_agent_name_tv.setText("");
                           // backtoolbar.setVisibility(View.GONE);
                            //toolbar.setVisibility(View.VISIBLE);
                            //other_agent_name_tv.setText("");

                            if(visitorCompanyAdapter!=null)
                            {

                                visitorCompanyAdapter.setIsSelfStatus(true);
                                visitorCompanyAdapter.notifyDataSetChanged();
                            }
                        }
                        else
                        {

                            agentNameLayout.setVisibility(View.GONE);
                            selected_agent_name_tv.setText(selectedAgentName);
                           // backtoolbar.setVisibility(View.VISIBLE);
                            //toolbar.setVisibility(View.GONE);

                           // other_agent_name_tv.setText(selectedAgentName);
                            if(visitorCompanyAdapter!=null)
                            {
                                visitorCompanyAdapter.agentSiteId(selectedPositionSiteId);
                                visitorCompanyAdapter.setIsSelfStatus(false);
                                visitorCompanyAdapter.notifyDataSetChanged();
                            }
                        }
                        if(isSelf){
                            updateSiteListWithLocalDb();
                            setUnreadCount();
                        }
                        else
                        {
                            getActiveChatsApi();
                        }
                        //getActiveChatsApi();//***
                        //getActiveAgentsApi();

                        break;
                    case Values.RecentList.TAB_CLICKED:

                        Helper.getInstance().LogDetails("TAB_CLICKED","called");

                        if(isSelf)
                        {
                            updateSiteListWithLocalDb();
                            setUnreadCount();
                           // getActiveChatsApi();//***
                           // getActiveAgentsApi();
                        }

                        break;
                    case Values.RecentList.LOGOUT:
                        Helper.getInstance().LogDetails("LOGOUT","called");
                         mSocket=null;
                        tmSocket=null;
                        break;
                    case Values.RecentList.OFFLINE:
                        Helper.getInstance().LogDetails("OFFLINE","called");
                        emitOffline();
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void setSitesToAgent(){
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
            if(visitorCompanyAdapter!=null){
                visitorCompanyAdapter.notifyDataSetChanged();
            }
        }
    }

}


