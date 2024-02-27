package com.tvisha.click2magic.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.tvisha.click2magic.DataBase.AgentsTable;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.DataBase.TypingMessageTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.AgentCompanyAdapter;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.ActiveAgentsApi;
import com.tvisha.click2magic.api.post.SiteAgentsData;
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


public class AgentsFragment extends Fragment {


   @BindView(R.id.agent_company_recycler_view)
    RecyclerView agent_company_recycler_view;

    View rootView;
    Context context;
    Activity activity;
    public static io.socket.client.Socket mSocket, tmSocket;
    public static List<SitesInfo> sitesInfoList = new ArrayList<>();

    AgentCompanyAdapter agentCompanyAdapter;
    Dialog dialog = null;
    boolean isSelf=true,isRestart=false;


    String selectedPositionAgentId="0", role = "0", user_id, agent_id = "", loginUserId = "";
    ActiveAgent activeAgent=null,all=null,agent=null;

    ConversationTable conversationTable = null;
    TypingMessageTable typingMessageTable = null;
    AgentsTable agentsTable = null;
    SitesTable sitesTable = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_agents, container, false);
        context=HomeActivity.getContext();
        activity=HomeActivity.getActivity();
        ButterKnife.bind(this, rootView);
        progressDialog();
        getSharedPreferenceData();
        getSiteData();
        initViews();
        processFragment();
        HandlerHolder.agentsFragmentUiHandler = uiHandler;
        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Helper.getInstance().LogDetails("setUserVisibleHint","called"+isVisibleToUser);
        if(isVisibleToUser){
            if(HandlerHolder.mainActivityUiHandler!=null){
                HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.PAGE_CHANGED,1).sendToTarget();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        HandlerHolder.agentsFragmentUiHandler = uiHandler;
        HandlerHolder.agentfragmentSocketHandler=socketHandler;
        super.onResume();
    }

    private void processFragment() {
        HandlerHolder.agentfragmentSocketHandler=socketHandler;
        setData();

    }
    private void setData() {
        try{

            Helper.getInstance().LogDetails("setData update", "" + isSelf);

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                Helper.getInstance().LogDetails("sitesInfoList Intent data", "sp size " + sitesInfoList.size() + "");

                updateAgentsSiteListWithLocalDb();
                checkAgentStatus();
               // getActiveAgentsApi();

            } else {
                logout();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void logout() {
        try {


            Session.endLoginSession(activity);
            Session.logout(context);
            if (conversationTable == null) {
                conversationTable = new ConversationTable(context);
            }

            conversationTable.clearDb();
            typingMessageTable.clearDb();

            disconnectSockets();
            Navigation.getInstance().openLoginPage(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void getSiteData() {

        try{
            if(sitesTable==null){
                sitesTable=new SitesTable(context);
            }
            if(sitesInfoList!=null && sitesInfoList.size()>0){
                sitesInfoList.clear();
                if(agentCompanyAdapter!=null){
                    agentCompanyAdapter.notifyDataSetChanged();
                }
            }
            sitesInfoList.addAll(sitesTable.getSites());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initViews() {

        conversationTable=new ConversationTable(context);
        typingMessageTable=new TypingMessageTable(context);
        agentsTable=new AgentsTable(context);
        sitesTable=new SitesTable(context);
        if (agent_company_recycler_view != null) {

            LinearLayoutManager layoutManager5 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            agent_company_recycler_view.setLayoutManager(layoutManager5);
            agent_company_recycler_view.setNestedScrollingEnabled(false);
            agentCompanyAdapter = new AgentCompanyAdapter(context, sitesInfoList, isSelf,AgentsFragment.this);
            agentCompanyAdapter.selectionMode = true;
            agent_company_recycler_view.setAdapter(agentCompanyAdapter);
        }

        getSocket();
    }
    AppSocket application;
    private void getSocket(){

        if(mSocket==null){
            application = (AppSocket) activity. getApplication();
            mSocket=application.getSocketInstance();
            Helper.getInstance().LogDetails("AgentsFragment initViews", "socket init called");
            if(mSocket==null){

                Helper.getInstance().LogDetails("AgentsFragment initViews", "socket null called");
            }
            else
            {
                if(!mSocket.connected())
                {
                   application.connectSocket();
                }
                Helper.getInstance().LogDetails("AgentsFragment initViews", "socket not null called");
            }
        }
    }



    private void getActiveAgentsApi() {

        try{


            Helper.getInstance().LogDetails("getActiveAgentsApi", "updateAgentsSiteList ==== "+ApiEndPoint.token + " " + loginUserId + " " +Session.getCompanyToken(context));

            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                return;
            }


            if (!isRestart) {
               // isRestart = true;

                Helper.getInstance().LogDetails("getActiveAgentsApi", "open progress bar");
               // openProgess();
            }

            retrofit2.Call<ActiveAgentsApi.ActiveAgentsResponse> call = ActiveAgentsApi.getApiService().getActiveAgents(ApiEndPoint.token, Session.getCompanyToken(context), loginUserId);

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
                                checkAgentStatus();
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
    private void  updateAgentsSiteListWithLocalDb() {

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


                Helper.getInstance().LogDetails("updateAgentsSiteListWithLocalDb", "size " );

                if (sitesInfoList != null && sitesInfoList.size() > 0) {


                        Helper.getInstance().LogDetails("updateAgentsSiteListWithLocalDb", "siteId " );

                        for (int j = 0; j < sitesInfoList.size(); j++) {

                            String sid = sitesInfoList.get(j).getSiteId();
                            if ( sid != null && !sid.trim().isEmpty()) {
                                List<ActiveAgent> activeAgentsList = agentsTable.getAgentList(Integer.parseInt(sid));
                                Helper.getInstance().LogDetails("updateAgentsSiteListWithLocalDb****", activeAgentsList.size()+"");
                                List<ActiveAgent> agents = new ArrayList<>();
                                agents.addAll(agentsTable.getAgentList(Integer.parseInt(sid)));

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

                                Helper.getInstance().LogDetails("updateAgentsSiteListWithLocalDb==", "sid size" + sid+" "+sitesInfoList.get(j).getAllAgents().size());
                                if(agentsTable==null){
                                    agentsTable=new AgentsTable(context);
                                }
                                sitesInfoList.get(j).setAgentsCount(agentsTable.getTheAgentCount(Integer.parseInt(sid)));
                                sitesInfoList.get(j).setActiveAgentsCount(agentsTable.getTheActiveAgentCount(Integer.parseInt(sid)));

                            }
                        }

                    agentCompanyAdapter.notifyDataSetChanged();

                }




            saveSiteData();
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

                    agentCompanyAdapter.notifyDataSetChanged();

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


                    agentCompanyAdapter.notifyDataSetChanged();

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



    private void progressDialog() {

        try {
            if (!(activity).isFinishing()) {
                dialog = new Dialog(activity, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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



    private List<ActiveChat> setUnreadCount(List<ActiveChat> activeChatArrayList) {
        try{

            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                if (conversationTable == null) {
                    conversationTable = new ConversationTable(context);
                }
                for (int i = 0; i < activeChatArrayList.size(); i++) {

                    if (activeChatArrayList.get(i).getTmVisitorId() != null && !activeChatArrayList.get(i).getTmVisitorId().isEmpty()) {
                        int unreadCount = conversationTable.getTheUnreadMessageAvailable(Integer.parseInt(activeChatArrayList.get(i).getTmVisitorId()), Integer.parseInt(Session.getTmUserId(context)), activeChatArrayList.get(i).getChatReferenceId());
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
                user_id =  Session.getUserID(context);
                loginUserId =  Session.getUserID(context);
                agent_id =  Session.getUserID(context);
                role =  Session.getUserRole(context);

                if(activeAgent==null)
                {
                    activeAgent=new ActiveAgent();
                    activeAgent=getAgent(activeAgent,Session.getAccountId(context),loginUserId,Session.getSiteId(context),role,Session.getUserName(context),Session.getTmUserId(context),loginUserId);
                }


                if(agent==null){
                    agent=new ActiveAgent();
                    agent=getAgent(agent,Session.getAccountId(context),loginUserId,Session.getSiteId(context),role,"Self",Session.getTmUserId(context),loginUserId);
                }

                if(all==null){
                    all=new ActiveAgent();
                    all=getAgent(all,Session.getAccountId(context),"",Session.getSiteId(context),role,"All",Session.getTmUserId(context),loginUserId);
                }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private ActiveAgent getAgent(ActiveAgent activeAgent,String account_id,String agent_id,String site_id,String role,String user_name,String tmUserId,String user_id){

        activeAgent.setAccountId(account_id);
        activeAgent.setAgentId(agent_id);
        activeAgent.setSiteId(site_id);
        activeAgent.setRole(role);
        activeAgent.setUserName(user_name);
        activeAgent.setTmUserId(tmUserId);
        activeAgent.setUserId(user_id);
        return activeAgent;
    }
    private void checkAgentStatus() {
        try{

            if (sitesInfoList != null && sitesInfoList.size() > 0) {
                for (int i = 0; i < sitesInfoList.size(); i++) {
                    if (mSocket != null && mSocket.connected()) {
                        Helper.getInstance().LogDetails("AgentsFragment updateAgentStatusInfo checkAgentStatus","called");
                        EmitCheckAgentStatus(sitesInfoList.get(i).getSiteToken(), sitesInfoList.get(i).getSiteId());
                    } else {
                        Helper.getInstance().LogDetails("AgentsFragment updateAgentStatusInfo checkAgentStatus","not called");
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void EmitCheckAgentStatus(String siteToken, String siteId) {

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
                    Helper.getInstance().LogDetails("AgentsFragment EmitCheckAgentStatus ",object.toString());
                    mSocket.emit(SocketConstants.CHECK_AGENT_STATUS, object);

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
            try {
                switch (msg.what) {
                    case SocketConstants.SocketEvents.EVENT_CONNECT:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_CONNECT called");
                        socketOnConnect();

                        break;
                    case SocketConstants.SocketEvents.EVENT_DISCONNECT:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_DISCONNECT called");
                        socketOnDisconnect();
                        break;

                    case SocketConstants.SocketEvents.EVENT_AGENT_STATUS_UPDATED:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_AGENT_STATUS_UPDATED called");
                        agentStatusUpdated((JSONObject) msg.obj);

                        break;
                    case SocketConstants.SocketEvents.EVENT_USER_CHAT_ENDED:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_USER_CHAT_ENDED called");
                        userChatEnded((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_CHAT_ENDED_AGENT:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_CHAT_ENDED_AGENT called");
                        chatEndedAgent((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_CONTACT_UPDATE:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_CONTACT_UPDATE called");
                        contactUpdate((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_AGENT_ACTIVITY:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_AGENT_ACTIVITY called");
                        agentActivity((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_NEW_ONLINE:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_NEW_ONLINE called");
                        newOnline((JSONObject) msg.obj);
                        break;

                    case SocketConstants.SocketEvents.EVENT_CHECK_AGENT_STATUS_INFO:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_CHECK_AGENT_STATUS_INFO called");
                        agentStatusInfo((JSONArray) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_USER_TYPING_TO_AGENT:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_USER_TYPING_TO_AGENT called");
                        userTypingMessage((JSONObject) msg.obj);
                        break;
                    case SocketConstants.SocketEvents.EVENT_AGENT_CHAT_ENDED:
                        Helper.getInstance().LogDetails("AgentsFragment","EVENT_AGENT_CHAT_ENDED called");
                        agentChatEnded((JSONObject) msg.obj);
                        break;




                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void socketOnConnect() {
        try{
            if(mSocket==null){
                AppSocket application = (AppSocket) activity. getApplication();
                mSocket=application.getSocketInstance();
                Helper.getInstance().LogDetails("AgentsFragment socketOnConnect", "socket null called");
            }

            if (mSocket != null && mSocket.connected()) {
                Helper.getInstance().LogDetails("AgentsFragment checkAgentStatus", "called");
                checkAgentStatus();
            }
            else
            {
                Helper.getInstance().LogDetails("AgentsFragment checkAgentStatus", "not called");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void socketOnDisconnect(){

    }

    private void agentStatusUpdated(JSONObject object){
        try{
            Helper.getInstance().LogDetails("EmitAgentStatusUpdated  updateAgentStatusupdated ", object.toString());
            if (object != null) {


                String siteId = object.optString("site_id");
                int is_online = object.optInt("is_online");

                if (object.optString("agent_id") != null ) {
                    if ( (isSelf && agent_id != null && object.optString("agent_id").equals(agent_id)) ||  (!isSelf && selectedPositionAgentId!=null && object.optString("agent_id").equals(selectedPositionAgentId))  ) {
                        if (sitesInfoList != null && sitesInfoList.size() > 0) {
                            for (int i = 0; i < sitesInfoList.size(); i++) {
                                String sid = sitesInfoList.get(i).getSiteId();
                                if (siteId.equals(sid)) {
                                    sitesInfoList.get(i).setOnline_status(is_online);

                                    activity. runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            agentCompanyAdapter.notifyDataSetChanged();

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
                                                        agentCompanyAdapter.notifyDataSetChanged();

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
            Helper.getInstance().LogDetails("pushAgentOffline agentActivity updateAgentOnlineStatus", "called" + jsonObject.toString());

            if (jsonObject != null) {
                String user_id = jsonObject.optString("user_id");
                String site_id = jsonObject.optString("site_id");
                String agent_staus = jsonObject.optString("agent_staus");
                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    int position = -1;
                    boolean isPresent = false;

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

                                            activeAgents.add(allAgents.get(i));
                                            sitesInfoList.get(position).setActiveAgents(activeAgents);


                                        } else if (activeAgents == null && agent_staus.equals("1")) {
                                      activeAgents = new ArrayList<>();
                                            activeAgents.add(allAgents.get(i));
                                            sitesInfoList.get(position).setActiveAgents(activeAgents);

                                        }

                                        break;
                                    }
                                }

                            }
                        }

                        activity.runOnUiThread(new Runnable() {
                            public void run() {

                                if (agentCompanyAdapter != null)
                                    agentCompanyAdapter.notifyDataSetChanged();
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


                                if (conversationTable == null) {
                                    conversationTable = new ConversationTable(context);
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

                                        agentCompanyAdapter.notifyDataSetChanged();

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

                        Helper.getInstance().LogDetails("updateAgentStatusInfo ", siteId + " " + isOnline);


                                if (sitesInfoList != null && sitesInfoList.size() > 0) {
                                    for (int i = 0; i < sitesInfoList.size(); i++) {
                                        String sid = sitesInfoList.get(i).getSiteId();
                                        if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                            sitesInfoList.get(i).setOnline_status(isOnline);
                                            Helper.getInstance().LogDetails("updateAgentStatusInfo ", siteId + " " + isOnline + " " + sitesInfoList.get(i).getOnline_status());
                                        }
                                    }

                                }



                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                agentCompanyAdapter.notifyDataSetChanged();
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
                                                        agentCompanyAdapter.notifyDataSetChanged();

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

    public void showConfirmDialog(String userId,int pos,String siteId,String siteToken) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Are you sure?")
                .setMessage("Do you really want to send offline?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushAgentOffline(userId,pos,siteId,siteToken);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    public void pushAgentOffline(String userId,int pos,String siteId,String siteToken) {
        Helper.getInstance().LogDetails("pushAgentOffline ","called ");
        try {
            if (agent_id != null && !agent_id.trim().isEmpty()) {
                JSONObject object = new JSONObject();
                object.put("agent_id", userId);
                object.put("account_id", Session.getAccountId(context));
                object.put("user_id", agent_id);
                object.put("site_id", siteId);
                object.put("site_token", siteToken);
                if (mSocket != null && mSocket.connected()) {
                    Helper.getInstance().LogDetails("pushAgentOffline ",object.toString());
                    mSocket.emit(SocketConstants.PUSH_AGENT_OFFLINE, object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Helper.getInstance().LogDetails("pushAgentOffline ","res "+args[0].toString());
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                if(agentCompanyAdapter!=null)
                    agentCompanyAdapter.notifyDataSetChanged();
               /* if(isSelf){
                    updateSiteListWithLocalDb();
                    setUnreadCount();
                }*/
            }
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
                    if(agentCompanyAdapter!=null)
                        agentCompanyAdapter.notifyDataSetChanged();
                    break;
                }
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

                    case Values.RecentList.TAB_CLICKED:
                        Helper.getInstance().LogDetails("TAB_CLICKED","called");
                        updateAgentsSiteListWithLocalDb();
                        checkAgentStatus();
                        break;
                    case Values.RecentList.SITES_SYNC_COMPLETED:
                        break;

                        case Values.RecentList.ACTIVE_AGENTS_SYNC_COMPLETED:
                        updateAgentsSiteListWithLocalDb();
                        break;
                    case Values.RecentList.SITE_DELETED:
                        int siteId=(int)msg.obj;
                        deleteSite(siteId);
                        break;
                    case Values.RecentList.SITE_ADDED:
                        JSONObject jsonObject=(JSONObject) msg.obj;
                        addSite(jsonObject);
                        break;


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
