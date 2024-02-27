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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tvisha.click2magic.DataBase.AgentsTable;
import com.tvisha.click2magic.DataBase.ArchiveChatsTable;
import com.tvisha.click2magic.DataBase.SitesTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.ArchiveAgentsAdapter;
import com.tvisha.click2magic.adapter.ArchiveSitesAdapter;
import com.tvisha.click2magic.adapter.ArchivesAdapter;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.ActiveAgentsApi;
import com.tvisha.click2magic.api.post.ArchievsApi;
import com.tvisha.click2magic.api.post.ArchievsResponse;
import com.tvisha.click2magic.api.post.SiteAgentsData;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;
import com.tvisha.click2magic.ui.HomeActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;


public class ArchivesFragment extends Fragment {



    @BindView(R.id.archiev_chat_recycler_view)
    RecyclerView archiev_chat_recycler_view;

    @BindView(R.id.companyRecyclerView1)
    RecyclerView  companyRecyclerView;

    @BindView(R.id.topRecyclerView1)
    RecyclerView topRecyclerView;

    @BindView(R.id.no_chats_image)
    ImageView no_chats_image;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipe_refresh;

    @BindView(R.id.archiveLine)
    View archiveLine;

    @BindView(R.id.archiveAgentLayout)
    RelativeLayout archiveAgentLayout;


    Context context;
    Activity activity;
    Dialog dialog = null;
    public static io.socket.client.Socket mSocket, tmSocket;
    public static List<SitesInfo> sitesInfoList = new ArrayList<>();
    View rootView;

    boolean isSelf=true,isFirstTime=true,isRestart=false,isRefresh=false;
    ArchivesAdapter archiceChatAdapter;
    ArchiveSitesAdapter companyAdapter;
    ArchiveAgentsAdapter topAdapter;
    List<ActiveAgent> archiveAgentList = new ArrayList<>();
    public static String  current_site_id="",archive_active_agent_id="0";
    int limit = 50, offset = 0, oldOffSet = 0;
    ArrayList<ActiveChat> archiveChatArrayList = new ArrayList<>();
    String fromDate = "", toDate = "", agent_ids = "", site_ids = "",
            user_name,user_display_name, site_id, role = "0", user_id, tmUserId = "", account_id = "", apiRole = "0", agent_id = "",
            company_token = "", loginUserId = "";


    ActiveAgent activeAgent=null,all=null,agent=null;
    ArchiveChatsTable archiveChatsTable;
    AgentsTable agentsTable;
    SitesTable sitesTable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_archives, container, false);
        context=HomeActivity.getContext();
        activity=HomeActivity.getActivity();

        ButterKnife.bind(this, rootView);
        progressDialog();
        getSharedPreferenceData();
        getSiteData();
        initViews();
        processFragment();
        HandlerHolder.archiveFragmentHandler = uiHandler;
        return rootView;
    }

    @Override
    public void onResume() {
        if (archiceChatAdapter != null) {
            archiceChatAdapter.selectionMode = true;
            archiceChatAdapter.notifyDataSetChanged();
        }
        HandlerHolder.archiveFragmentHandler = uiHandler;
        Helper.getInstance().LogDetails("ArchivesFragment===","onResume called");
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Helper.getInstance().LogDetails("ArchivesFragment setUserVisibleHint","called"+isVisibleToUser);
        if(isVisibleToUser){
            if(HandlerHolder.mainActivityUiHandler!=null){
                HandlerHolder.mainActivityUiHandler.obtainMessage(Values.RecentList.PAGE_CHANGED,2).sendToTarget();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onPause() {
        if (archiceChatAdapter != null) {
            archiceChatAdapter.notifyDataSetChanged();
        }
        Helper.getInstance().LogDetails("ArchivesFragment===","onPause called");
        super.onPause();
    }
    private void processFragment(){
        //getActiveAgentsApi();//***
        updateAgentsSiteListWithLocalDb();

        setView();
       // Helper.getInstance().LogDetails("getArchievesApi ","processFragment called"+sitesInfoList.get(0).getSiteId());

    }
    private void getSiteData() {
        try{
            if(sitesTable==null){
                sitesTable=new SitesTable(context);
            }
            if(sitesInfoList!=null && sitesInfoList.size()>0){
                sitesInfoList.clear();
                if(companyAdapter!=null){
                    companyAdapter.notifyDataSetChanged();
                }
            }
            if (isSelf) {
                sitesInfoList.addAll(sitesTable.getSites());

            } else {
                sitesInfoList.addAll(sitesTable.getSites());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getSharedPreferenceData() {

        try{

            isSelf = Session.getIsSelf(context);

                user_id =  Session.getUserID(context);
                loginUserId =  Session.getUserID(context);
                agent_id =  Session.getUserID(context);
                tmUserId = Session.getTmUserId(context);
                site_id =  Session.getSiteId(context);
                account_id =   Session.getAccountId(context);
                role =  Session.getUserRole(context);
                apiRole = role;
                user_name =  Session.getUserName(context);
                user_display_name = Session.getUserDisplayName(context);
                company_token =  Session.getCompanyToken(context);

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


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initViews() {
        archiveChatsTable=new ArchiveChatsTable(context);
        agentsTable=new AgentsTable(context);
        sitesTable=new SitesTable(context);


        if (archiev_chat_recycler_view != null) {
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
          //  archiev_chat_recycler_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            archiev_chat_recycler_view.setLayoutManager(layoutManager3);
            archiev_chat_recycler_view.setNestedScrollingEnabled(false);
            archiceChatAdapter = new ArchivesAdapter(context, archiveChatArrayList);
            archiev_chat_recycler_view.setAdapter(archiceChatAdapter);
        }

        if (companyRecyclerView != null) {

            Helper.getInstance().LogDetails("ArchivesFragment ArchiveSitesAdapter","size "+sitesInfoList.size()+"");

            LinearLayoutManager layoutManager5 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            companyRecyclerView.setLayoutManager(layoutManager5);
            companyRecyclerView.setNestedScrollingEnabled(false);
            companyAdapter = new ArchiveSitesAdapter(context, sitesInfoList, isSelf,ArchivesFragment.this);
            companyAdapter.selectionMode = true;
            companyRecyclerView.setAdapter(companyAdapter);


            if(sitesInfoList!=null && sitesInfoList.size()>0){
                current_site_id=sitesInfoList.get(0).getSiteId();
                /*
               List<ActiveAgent> activeAgentArrayList = sitesInfoList.get(0).getAllAgents();
                archiveAgentList=activeAgentArrayList;*/
                archiveAgentList=sitesInfoList.get(0).getAllAgents();
                if(archiveAgentList!=null && archiveAgentList.size()>0){
                    boolean isPresent=false;
                    for(int i=0;i<archiveAgentList.size();i++){
                        if(agent!=null && agent.getAgentId()!=null &&  archiveAgentList.get(i) !=null && archiveAgentList.get(i).getAgentId()!=null && agent.getAgentId()!=null && archiveAgentList.get(i).getAgentId().equals(agent.getAgentId()))
                        {
                            archiveAgentList.get(i).setUserName("Self");
                            isPresent=true;
                            break;
                        }
                    }
                    if(!isPresent)
                    {
                        if(agent!=null && agent.getAgentId()!=null && archiveAgentList.get(0) !=null && archiveAgentList.get(0).getAgentId()!=null && agent.getAgentId()!=null && !archiveAgentList.get(0).getAgentId().equals(agent.getAgentId()))
                        {
                            if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
                            {
                                if(agent!=null){
                                    archiveAgentList.add(0,agent);
                                    archive_active_agent_id=archiveAgentList.get(0).getAgentId();
                                }

                            }
                        }
                    }

                    boolean isAllPresent=false;
                    if(archiveAgentList!=null && archiveAgentList.size()>0){
                        for(int i=0;i<archiveAgentList.size();i++){
                            if(archiveAgentList.get(i)!=null && archiveAgentList.get(i).getUserName()!=null  && archiveAgentList.get(i).getUserName().equals("All"))
                            {
                                isAllPresent=true;
                                break;
                            }
                        }
                    }

                    if(!isAllPresent)
                    {
                        if(archiveAgentList==null){
                            archiveAgentList=new ArrayList<>();
                        }
                        if(archiveAgentList!=null)
                        {
                            if(Integer.parseInt(role)!=Values.UserRoles.AGENT)
                            {
                                if(all!=null){
                                    archiveAgentList.add(0,all);
                                    archive_active_agent_id=archiveAgentList.get(0).getAgentId();

                                }

                            }
                        }
                    }


                }
            }


        }

        if(topRecyclerView!=null){

            Helper.getInstance().LogDetails("ArchivesFragment archiveAgentList","init size "+archiveAgentList.size()+"  role "+role);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            topRecyclerView.setLayoutManager(layoutManager1);
            topRecyclerView.setNestedScrollingEnabled(false);
            topAdapter = new ArchiveAgentsAdapter(context, archiveAgentList, isSelf,ArchivesFragment.this);
            topRecyclerView.setAdapter(topAdapter);
            if(archiveAgentList!=null && archiveAgentList.size()>0){
                if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.AGENT)
                {
                    archiveLine.setVisibility(View.GONE);
                    archiveAgentLayout.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                archiveLine.setVisibility(View.VISIBLE);
                archiveAgentLayout.setVisibility(View.GONE);
            }
        }

        topRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        Helper.getInstance().LogDetails("topRecyclerView Touch","called ");
                        //rv.getParent().requestDisallowInterceptTouchEvent(true);
                        if(HandlerHolder.mainActivityUiHandler!=null){
                            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.DISABLE_SWIPE);
                        }

                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        companyRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        Helper.getInstance().LogDetails("topRecyclerView Touch","called ");
                        //rv.getParent().requestDisallowInterceptTouchEvent(true);
                        if(HandlerHolder.mainActivityUiHandler!=null){
                            HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.DISABLE_SWIPE);
                        }

                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });



        archiev_chat_recycler_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                if(HandlerHolder.mainActivityUiHandler!=null){
                    HandlerHolder.mainActivityUiHandler.sendEmptyMessage(Values.RecentList.ENABLE_SWIPE);
                }


                LinearLayoutManager myLayoutManager = (LinearLayoutManager) archiev_chat_recycler_view.getLayoutManager();
                int totalItemCount = myLayoutManager.getItemCount();
                int pastVisiblesItems = myLayoutManager.findLastVisibleItemPosition();
                int scrollPosition = myLayoutManager.findLastVisibleItemPosition();

                if(scrollPosition<5)
                {
                    Helper.getInstance().LogDetails("ArchivesFragment addOnItemTouchListener","if down called"+totalItemCount+" "+scrollPosition+" "+pastVisiblesItems);
                    swipe_refresh.setEnabled(true);
                }
                else
                {

                    Helper.getInstance().LogDetails("ArchivesFragment addOnItemTouchListener","else down called"+totalItemCount+" "+scrollPosition+" "+pastVisiblesItems);
                    swipe_refresh.setEnabled(false);
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        archiev_chat_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerview, int dx, int dy) {
                super.onScrolled(recyclerview, dx, dy);

                LinearLayoutManager myLayoutManager = (LinearLayoutManager) archiev_chat_recycler_view.getLayoutManager();
                int totalItemCount = myLayoutManager.getItemCount();
                int pastVisiblesItems = myLayoutManager.findLastVisibleItemPosition();
                int scrollPosition = myLayoutManager.findLastVisibleItemPosition();
                if (dy > 0) {

                    Helper.getInstance().LogDetails("ArchivesFragment addOnScrollListener","if down calledd"+dy);


                    if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {

                        if (archiveChatArrayList.size()>10 && myLayoutManager.findLastCompletelyVisibleItemPosition() == archiveChatArrayList.size() - 10) {
                            Helper.getInstance().LogDetails("ArchivesFragment findLastCompletelyVisibleItemPosition if ", myLayoutManager.findLastCompletelyVisibleItemPosition() + " " + archiveChatArrayList.size() + " " + offset);
                            getArchievesApi();//***

                        } else {
                            Helper.getInstance().LogDetails("ArchivesFragment findLastCompletelyVisibleItemPosition else ", myLayoutManager.findLastCompletelyVisibleItemPosition() + " " + archiveChatArrayList.size() + " " + offset);
                        }
                    }


                }
                else
                {

                    Helper.getInstance().LogDetails("ArchivesFragment addOnScrollListener","else down calledd"+dy);
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView agents_recyclerView, int newState) {
                super.onScrollStateChanged(agents_recyclerView, newState);
            }
        });

        if(mSocket==null){
            AppSocket application = (AppSocket) activity. getApplication();
            mSocket=application.getSocketInstance();
            Helper.getInstance().LogDetails("ArchivesFragment initViews", "socket init called");
            if(mSocket==null){

                Helper.getInstance().LogDetails("ArchivesFragment initViews", "socket null called");
            }
            else
            {
                if(!mSocket.connected())
                {
                    application.connectSocket();
                }
                Helper.getInstance().LogDetails("ArchivesFragment initViews", "socket not null called");
            }
        }

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refereshList();
            }
        });
    }

    public void refereshList(){
        isRefresh=true;
        swipe_refresh.setRefreshing(true);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent, android.R.color.holo_blue_bright, R.color.colorPrimaryDark, android.R.color.holo_green_light);
        getArchievesApi();
    }

    public void scrollList(int position) {
        try{
            Helper.getInstance().LogDetails("ArchivesFragment scrollList", "called" + position + " " + archiveAgentList.size());
            if (archiveAgentList != null && position < archiveAgentList.size()) {
                Helper.getInstance().LogDetails("scrollList", "if called ");
                if (topRecyclerView != null) {
                    Helper.getInstance().LogDetails("ArchivesFragment scrollList", "inner if called ");
                    topRecyclerView.smoothScrollToPosition(position);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getArchievesApi() {

        try {

            if(isRefresh){

                offset=0;
            }
            else
            {
                if(archiveChatArrayList!=null && archiveChatArrayList.size()>0){
                    offset=archiveChatArrayList.size();
                }
            }



            if (Utilities.getConnectivityStatus(context) <= 0) {
                Helper.getInstance().pushToast(context, "Please check your network connection...");
                isRefresh=false;
                swipe_refresh.setRefreshing(false);
                return;
            }

            if (!isRestart) {
               // isRestart = true;
                //openProgess();
            }

            //getArchievesApi before data === RdFgcWB7HpZn3Meo user_id 7 site_id ,1, role 1 limit 50 offset 0 0 agent_ids  site_ids 7 fromDate  toDate  company_token 5d826b7d1ae7df8a6f2852b4ee00fec2e422b428e47ffe05126208316d20935e3/+wedkLisuuzfKVnJYfylDshW3sov73xdXlakTTBVE=
            Helper.getInstance().LogDetails("ArchivesFragment getArchievesApi before", ApiEndPoint.token + " user_id " + loginUserId + " site_id " + site_id + " role " + role + " limit " + limit + " offset " + offset + " oldoffset  " + oldOffSet + " agent_ids " + agent_ids + " site_ids " + site_ids + " fromDate " + fromDate + " toDate " + toDate + " company_token " + company_token);

            retrofit2.Call<ArchievsResponse> call = ArchievsApi.getApiService().getArchievs(ApiEndPoint.token, company_token, loginUserId, site_id, role, limit, offset, agent_ids, site_ids, fromDate, toDate);

            call.enqueue(new Callback<ArchievsResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Response<ArchievsResponse> response) {
                    closeProgress();


                    ArchievsResponse data = response.body();

                   //oldOffSet = offset;
                    // offset=offset+limit;

                    if (data != null) {
                        if (data.isSuccess()) {

                            if (data.getData() != null && data.getData().size() > 0) {



                                Helper.getInstance().LogDetails("ArchivesFragment getArchievesApi success", data.getData().size() + " ");

                                offset = offset + data.getData().size();
                                Helper.getInstance().LogDetails("ArchivesFragment getArchievesApi after", ApiEndPoint.token + " " + user_id + " " + site_id + " " + role + " " + limit + " " + offset + " " + oldOffSet);
                                if (archiveChatArrayList == null || archiveChatArrayList.size() == 0) {
                                    archiveChatArrayList.addAll(data.getData());
                                    SyncData.insertArchiveChats(data.getData(),context);

                                } else {
                                    if(isRefresh){

                                        if(archiveChatArrayList!=null && archiveChatArrayList.size()>0){
                                            archiveChatArrayList.clear();
                                            archiceChatAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    archiveChatArrayList.addAll(data.getData());
                                    archiceChatAdapter.notifyDataSetChanged();
                                    SyncData.insertArchiveChats(data.getData(),context);
                                    // tempData.addAll(data.getData());
                                    //  updateList(tempData);
                                }


                                if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {

                                    archiceChatAdapter.notifyDataSetChanged();

                                }



                            }
                            isRefresh=false;

                        }
                    }
                    changeViews();
                    swipe_refresh.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Helper.getInstance().LogDetails("ArchivesFragment getArchievesApi", "onFailure" + t.getLocalizedMessage() + " " + t.getMessage());
                    changeViews();
                    swipe_refresh.setRefreshing(false);
                    isRefresh=false;
                    closeProgress();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            swipe_refresh.setRefreshing(false);
            isRefresh=false;
        }

    }

    private void changeViews(){
        if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {
            no_chats_image.setVisibility(View.GONE);
        } else {

            no_chats_image.setVisibility(View.VISIBLE);
        }
    }



    public  void  updateArchivesList(ActiveAgent activeAgent,int pos){
        if(activeAgent!=null){
            limit = 50;
            offset = 0;
            oldOffSet = 0;
            fromDate = "";
            toDate = "";
            archive_active_agent_id=activeAgent.getAgentId();
            agent_ids = activeAgent.getAgentId();
            site_ids = current_site_id;
            isFirstTime = true;
            isRestart=false;

            if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {
                archiveChatArrayList.clear();
                archiceChatAdapter.notifyDataSetChanged();
            }

           // getArchievesApi();//***
            getChatListFromLocalDb();
        }
        else{
            limit = 50;
            offset = 0;
            oldOffSet = 0;
            fromDate = "";
            toDate = "";
            archive_active_agent_id=activeAgent.getAgentId();
            agent_ids =agent_id;
            site_ids = current_site_id;
            isFirstTime = true;

            if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {
                archiveChatArrayList.clear();
                archiceChatAdapter.notifyDataSetChanged();
            }

            getChatListFromLocalDb();

           // getArchievesApi();//***
        }

    }

    public void updateUserList(List<ActiveAgent> activeAgentArrayList) {

        if(activeAgentArrayList!=null){
            Helper.getInstance().LogDetails("ArchivesFragment updateUserList","called if"+activeAgentArrayList.size());
        }
        else
        {
            Helper.getInstance().LogDetails("ArchivesFragment updateUserList","called else");
        }

        if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)==Values.UserRoles.AGENT){
            updateArchivesList(agent, 0);
            archiveAgentLayout.setVisibility(View.GONE);
            archiveLine.setVisibility(View.VISIBLE);
            return;
        }


        try {

            if (activeAgentArrayList != null && activeAgentArrayList.size() > 0) {

                if (archiveAgentList == null) {
                    archiveAgentList = new ArrayList<>();
                }
                archiveAgentList = activeAgentArrayList;

                boolean isPresent=false;
                int position=0;
                if(archiveAgentList!=null && archiveAgentList.size()>0){
                    for(int k=0;k<archiveAgentList.size();k++){
                        if(archiveAgentList.get(k).getAgentId()!=null && agent.getAgentId()!=null && archiveAgentList.get(k).getAgentId().equals(agent.getAgentId()))
                        {
                            isPresent=true;
                            archiveAgentList.get(k).setUserName("Self");
                            position=k;
                            break;
                        }
                    }
                    if(isPresent &&position!=0){
                        ActiveAgent activeAgent=archiveAgentList.get(position);
                        archiveAgentList.remove(position);
                        archiveAgentList.add(0,activeAgent);
                        position=0;
                    }
                }



                if(!isPresent)
                {
                    if(archiveAgentList!=null && archiveAgentList.size()>0 && archiveAgentList.get(0).getAgentId()!=null && activeAgent.getAgentId()!=null && !archiveAgentList.get(0).getAgentId().equals(activeAgent.getAgentId()))
                    {
                        if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR) {
                            if(agent!=null){
                                archiveAgentList.add(0, agent);
                                position = 0;
                            }

                        }
                    }
                }


                boolean isAllPresent=false;
                int pos=0;
                if(archiveAgentList!=null && archiveAgentList.size()>0) {
                    for (int i = 0; i < archiveAgentList.size(); i++) {
                        if (archiveAgentList.get(i).getUserName() != null && archiveAgentList.get(i).getUserName().equals("All")) {
                            isAllPresent = true;
                            pos=i;
                            break;
                        }
                    }
                    if(isAllPresent &&pos!=0){
                        ActiveAgent activeAgent=archiveAgentList.get(pos);
                        archiveAgentList.remove(pos);
                        archiveAgentList.add(0,activeAgent);
                        position=0;
                    }
                }
                if(!isAllPresent)
                {
                    if(archiveAgentList==null){
                        archiveAgentList=new ArrayList<>();
                    }
                    if(archiveAgentList!=null)
                    {
                        if(Integer.parseInt(role)!=Values.UserRoles.AGENT)
                        {
                            if(all!=null){
                                archiveAgentList.add(0,all);
                                archive_active_agent_id=archiveAgentList.get(0).getAgentId();
                                position=0;
                            }

                        }
                    }
                }





                topAdapter.setList(archiveAgentList);
                topAdapter.notifyDataSetChanged();

                if(archiveAgentList!=null && archiveAgentList.size()>0){

                    updateArchivesList(archiveAgentList.get(position), position);
                    if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.AGENT)
                    {
                        archiveAgentLayout.setVisibility(View.VISIBLE);
                        archiveLine.setVisibility(View.GONE);
                    }
                }
                else
                {
                    archiveAgentLayout.setVisibility(View.GONE);
                    archiveLine.setVisibility(View.VISIBLE);
                    if(archiveChatArrayList!=null && archiveChatArrayList.size()>0){
                        archiveChatArrayList.clear();
                        archiceChatAdapter.notifyDataSetChanged();
                        changeViews();
                    }
                }


                Helper.getInstance().LogDetails("ArchivesFragment updateUserList", archiveAgentList.size() + " " + activeAgentArrayList.size());



            } else {
                Helper.getInstance().LogDetails("ArchivesFragment updateUserList", "else called");

                archiveAgentList = activeAgentArrayList;
                boolean isPresent=false;
                int position=0;
                if (archiveAgentList == null || activeAgentArrayList.size()==0) {
                    archiveAgentList = new ArrayList<>();


                    if(archiveAgentList!=null && archiveAgentList.size()>0){
                        for(int k=0;k<archiveAgentList.size();k++){
                            if(archiveAgentList.get(k).getAgentId()!=null && agent.getAgentId()!=null && archiveAgentList.get(k).getAgentId().equals(agent.getAgentId()))
                            {
                                archiveAgentList.get(k).setUserName("Self");
                                position=k;
                                isPresent=true;
                                break;
                            }
                        }

                        if(isPresent &&position!=0){
                            ActiveAgent activeAgent=archiveAgentList.get(position);
                            archiveAgentList.remove(position);
                            archiveAgentList.add(0,activeAgent);
                            position=0;
                        }
                    }

                    if(!isPresent){
                        if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
                        {
                            if(agent!=null){
                                archiveAgentList.add(0,agent);
                                position=0;
                            }

                        }

                    }


                    boolean isAllPresent=false;
                    int pos=0;
                    if(archiveAgentList!=null && archiveAgentList.size()>0) {
                        for (int i = 0; i < archiveAgentList.size(); i++) {
                            if (archiveAgentList.get(i).getUserName() != null && archiveAgentList.get(i).getUserName().equals("All")) {
                                isAllPresent = true;
                                pos=i;
                                break;
                            }
                        }

                        if(isAllPresent &&pos!=0){
                            ActiveAgent activeAgent=archiveAgentList.get(pos);
                            archiveAgentList.remove(pos);
                            archiveAgentList.add(0,activeAgent);
                            position=0;
                        }
                    }
                    if(!isAllPresent)
                    {
                        if(archiveAgentList==null){
                            archiveAgentList=new ArrayList<>();
                        }
                        if(archiveAgentList!=null)
                        {
                            if(Integer.parseInt(role)!=Values.UserRoles.AGENT)
                            {
                                if(all!=null){
                                    archiveAgentList.add(0,all);
                                    archive_active_agent_id=archiveAgentList.get(0).getAgentId();
                                    position=0;
                                }

                            }
                        }
                    }

                }

                if(archiveAgentList!=null && archiveAgentList.size()>0){

                    updateArchivesList(archiveAgentList.get(position), position);
                    if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.AGENT) {
                        archiveAgentLayout.setVisibility(View.VISIBLE);
                        archiveLine.setVisibility(View.GONE);
                    }
                }
                else
                {
                    archiveAgentLayout.setVisibility(View.GONE);
                    archiveLine.setVisibility(View.VISIBLE);
                    archive_active_agent_id = "0";
                    if(archiveChatArrayList!=null && archiveChatArrayList.size()>0){
                        archiveChatArrayList.clear();
                        archiceChatAdapter.notifyDataSetChanged();
                        changeViews();
                    }
                }

                topAdapter.setList(archiveAgentList);
                topAdapter.notifyDataSetChanged();

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

    public void scrollCompanyList(int position) {
        try{
            Helper.getInstance().LogDetails("ArchivesFragment scrollCompanyList", "called" + position + " " + sitesInfoList.size());
            if (sitesInfoList != null && position < sitesInfoList.size()) {
                Helper.getInstance().LogDetails("ArchivesFragment scrollCompanyList", "if called ");
                if (companyRecyclerView != null) {
                    Helper.getInstance().LogDetails("ArchivesFragment scrollCompanyList", "inner if called ");
                    companyRecyclerView.smoothScrollToPosition(position);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    };

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



                if (sitesInfoList != null && sitesInfoList.size() > 0) {


                        for (int j = 0; j < sitesInfoList.size(); j++) {

                            String sid = sitesInfoList.get(j).getSiteId();


                            if ( sid != null && !sid.trim().isEmpty()) {
                                List<ActiveAgent> activeAgentsList = agentsTable.getAgentList(Integer.parseInt(sid));
                                Helper.getInstance().LogDetails("ArchivesFragment updateAgentsSiteList****", activeAgentsList.size() + "");
                                List<ActiveAgent> agents = new ArrayList<>();
                                agents.addAll(agentsTable.getAgentList(Integer.parseInt(sid)));

                                if (sitesInfoList.get(j).getActiveAgents() != null && sitesInfoList.get(j).getActiveAgents().size() > 0) {
                                    sitesInfoList.get(j).getActiveAgents().add(activeAgentsList.get(0));

                                } else {
                                    List<ActiveAgent> l = new ArrayList<>();
                                    if (activeAgentsList != null && activeAgentsList.size() > 0) {
                                        for (int m = 0; m < activeAgentsList.size(); m++) {
                                            if (activeAgentsList.get(m).getIsOnline() != null) {
                                                if (activeAgentsList.get(m).getIsOnline().equals("1")) {
                                                    l.add(activeAgentsList.get(m));
                                                }
                                            }
                                        }
                                        sitesInfoList.get(j).setActiveAgents(l);
                                        sitesInfoList.get(j).setAllAgents(activeAgentsList);
                                        sitesInfoList.get(j).setAgents(agents);
                                    } else {
                                        sitesInfoList.get(j).setActiveAgents(l);
                                        sitesInfoList.get(j).setAllAgents(activeAgentsList);
                                        sitesInfoList.get(j).setAgents(agents);
                                    }

                                }

                                Helper.getInstance().LogDetails("ArchivesFragment updateAgentsSiteList==", "sid size" + sid + " " + sitesInfoList.get(j).getAllAgents().size());

                                if (current_site_id != null && !current_site_id.trim().isEmpty()) {
                                    if (sid != null && !sid.trim().isEmpty() && current_site_id.equals(sid)) {
                                        if (archiveAgentList == null) {
                                            archiveAgentList = new ArrayList<>();
                                        }
                                        if (archiveAgentList != null) {

                                            archiveAgentList = sitesInfoList.get(j).getAllAgents();
                                            boolean isPresent = false;
                                            int position = 0;


                                            if (archiveAgentList != null && archiveAgentList.size() > 0) {


                                                for (int k = 0; k < archiveAgentList.size(); k++) {
                                                    if (archiveAgentList.get(k).getAgentId() != null && agent.getAgentId() != null && archiveAgentList.get(k).getAgentId().equals(agent.getAgentId())) {
                                                        isPresent = true;
                                                        archiveAgentList.get(k).setUserName("Self");
                                                        position = k;
                                                        break;
                                                    }
                                                }
                                                if (position != 0) {

                                                    ActiveAgent activeAgent = archiveAgentList.get(position);
                                                    activeAgentsList.remove(position);
                                                    archiveAgentList.add(0, activeAgent);
                                                    position = 0;
                                                }
                                                if (!isPresent) {
                                                    if (archiveAgentList.get(0).getAgentId() != null && activeAgent.getAgentId() != null && !archiveAgentList.get(0).getAgentId().equals(activeAgent.getAgentId())) {
                                                        if (Integer.parseInt(role) != Values.UserRoles.MODERATOR) {
                                                            if (agent != null) {
                                                                archiveAgentList.add(0, agent);
                                                                position = 0;
                                                            }

                                                        }

                                                    }

                                                }
                                                if (archive_active_agent_id.equals("0")) {
                                                    archive_active_agent_id = archiveAgentList.get(position).getAgentId();
                                                }


                                                boolean isAllPresent = false;
                                                int pos = 0;
                                                for (int l = 0; l < archiveAgentList.size(); l++) {
                                                    if (archiveAgentList.get(l).getUserName() != null && archiveAgentList.get(l).getUserName().equals("All")) {
                                                        isAllPresent = true;
                                                        pos = l
                                                        ;
                                                        break;
                                                    }
                                                }
                                         /*   if(pos!=0){

                                                ActiveAgent activeAgent=archiveAgentList.get(pos);
                                                activeAgentsList.remove(pos);
                                                archiveAgentList.add(0,activeAgent);
                                                position=0;
                                            }*/
                                                if (!isAllPresent) {
                                                    if (archiveAgentList == null) {
                                                        archiveAgentList = new ArrayList<>();
                                                    }
                                                    if (archiveAgentList != null) {
                                                        if (Integer.parseInt(role) != Values.UserRoles.AGENT) {
                                                            if (all != null) {
                                                                archiveAgentList.add(0, all);
                                                                // archive_active_agent_id=archiveAgentList.get(0).getAgentId();
                                                                position = 0;
                                                            }

                                                        }
                                                    }
                                                }


                                            }
                                        }
                                        if (archiveAgentList != null && archiveAgentList.size() > 0) {
                                            if (role != null && !role.trim().isEmpty() && Integer.parseInt(role) != Values.UserRoles.AGENT) {
                                                archiveAgentLayout.setVisibility(View.VISIBLE);
                                                archiveLine.setVisibility(View.GONE);
                                            }
                                        } else {
                                            archiveAgentLayout.setVisibility(View.GONE);
                                            archiveLine.setVisibility(View.VISIBLE);
                                        }

                                        topAdapter.setList(archiveAgentList);
                                        topAdapter.notifyDataSetChanged();
                                    }
                                }


                            }

                            Helper.getInstance().LogDetails("ArchivesFragment archiveAgentList", "updateAgentsSiteList ==== " + sid + "   " + archiveAgentList.size() + " " + sitesInfoList.get(j).getAllAgents().size() + " " + sitesInfoList.get(j).getAgents().size() + " ");
                        }





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

                Helper.getInstance().LogDetails("ArchivesFragment updateAgentsSiteList", "size " + data.size());

                if (sitesInfoList != null && sitesInfoList.size() > 0) {

                    for (int i = 0; i < data.size(); i++) {

                        String siteId = data.get(i).getSiteId();
                        Helper.getInstance().LogDetails("ArchivesFragment updateAgentsSiteList", "siteId " + siteId);


                        for (int j = 0; j < sitesInfoList.size(); j++) {

                            String sid = sitesInfoList.get(j).getSiteId();


                            if (siteId != null && !siteId.trim().isEmpty() && sid != null && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                List<ActiveAgent> activeAgentsList = data.get(i).getActiveAgents();
                                Helper.getInstance().LogDetails("ArchivesFragment updateAgentsSiteList****", data.get(i).getActiveAgents().size()+"");
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

                                if(current_site_id!=null && !current_site_id.trim().isEmpty())
                                {
                                    if(sid!=null && !sid.trim().isEmpty() && current_site_id.equals(sid))
                                    {
                                        if(archiveAgentList==null){
                                            archiveAgentList=new ArrayList<>();
                                        }
                                        if(archiveAgentList!=null ){

                                            archiveAgentList=sitesInfoList.get(j).getAllAgents();
                                            boolean isPresent=false;
                                            int position=0;


                                            if(archiveAgentList!=null && archiveAgentList.size()>0){


                                                for(int k=0;k<archiveAgentList.size();k++){
                                                    if(archiveAgentList.get(k).getAgentId()!=null && agent.getAgentId()!=null && archiveAgentList.get(k).getAgentId().equals(agent.getAgentId()))
                                                    {
                                                        archiveAgentList.get(k).setUserName("Self");
                                                        isPresent=true;
                                                        position=k;
                                                        break;
                                                    }
                                                }
                                                if(position!=0){

                                                    ActiveAgent activeAgent=archiveAgentList.get(position);
                                                    activeAgentsList.remove(position);
                                                    archiveAgentList.add(0,activeAgent);
                                                    position=0;
                                                }
                                                if(!isPresent){
                                                    if(archiveAgentList.get(0).getAgentId()!=null && activeAgent.getAgentId()!=null && !archiveAgentList.get(0).getAgentId().equals(activeAgent.getAgentId()))
                                                    {
                                                        if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
                                                        {
                                                            if(agent!=null){
                                                                archiveAgentList.add(0,agent);
                                                                position=0;
                                                            }

                                                        }

                                                    }

                                                }
                                                if(archive_active_agent_id.equals("0")){
                                                    archive_active_agent_id=archiveAgentList.get(position).getAgentId();
                                                }



                                                boolean isAllPresent=false;
                                                int pos=0;
                                                for(int l=0;l<archiveAgentList.size();l++){
                                                    if(archiveAgentList.get(l).getUserName()!=null  && archiveAgentList.get(l).getUserName().equals("All"))
                                                    {
                                                        isAllPresent=true;
                                                        pos=l
                                                        ;                                                    break;
                                                    }
                                                }
                                         /*   if(pos!=0){

                                                ActiveAgent activeAgent=archiveAgentList.get(pos);
                                                activeAgentsList.remove(pos);
                                                archiveAgentList.add(0,activeAgent);
                                                position=0;
                                            }*/
                                                if(!isAllPresent)
                                                {
                                                    if(archiveAgentList==null){
                                                        archiveAgentList=new ArrayList<>();
                                                    }
                                                    if(archiveAgentList!=null)
                                                    {
                                                        if(Integer.parseInt(role)!=Values.UserRoles.AGENT)
                                                        {
                                                            if(all!=null){
                                                                archiveAgentList.add(0,all);
                                                                // archive_active_agent_id=archiveAgentList.get(0).getAgentId();
                                                                position=0;
                                                            }

                                                        }
                                                    }
                                                }




                                            }
                                        }
                                        if(archiveAgentList!=null && archiveAgentList.size()>0){
                                            if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.AGENT) {
                                                archiveAgentLayout.setVisibility(View.VISIBLE);
                                                archiveLine.setVisibility(View.GONE);
                                            }
                                        }
                                        else
                                        {
                                            archiveAgentLayout.setVisibility(View.GONE);
                                            archiveLine.setVisibility(View.VISIBLE);
                                        }

                                        topAdapter.setList(archiveAgentList);
                                        topAdapter.notifyDataSetChanged();
                                    }
                                }


                            }

                            Helper.getInstance().LogDetails("archiveAgentList","updateAgentsSiteList ==== "+sid+"   "+archiveAgentList.size()+" "+sitesInfoList.get(j).getAllAgents().size()+" "+sitesInfoList.get(j).getAgents().size()+" "+ data.get(i).getActiveAgents().size());
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
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {


                    case Values.RecentList.TAB_CLICKED:
                        Helper.getInstance().LogDetails("ArchivesFragment TAB_CLICKED ArchivesFragment","called");
                        //getActiveAgentsApi();//***
                        updateAgentsSiteListWithLocalDb();
                        setView();
                        refereshList();
                        break;
                    case Values.RecentList.SITES_SYNC_COMPLETED:

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

    private void setView(){
        isRestart=false;
        if(archiveAgentList!=null && archiveAgentList.size()>0){
            if(role!=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.AGENT)
            {
                archiveAgentLayout.setVisibility(View.VISIBLE);
                archiveLine.setVisibility(View.GONE);
            }

        }
        else
        {

            archiveLine.setVisibility(View.VISIBLE);
            archiveAgentLayout.setVisibility(View.GONE);
        }

        companyAdapter.notifyDataSetChanged();
        topAdapter.notifyDataSetChanged();



        limit = 50;
        offset = 0;
        oldOffSet = 0;
        fromDate = "";
        toDate = "";



        if(sitesInfoList!=null && sitesInfoList.size()>0){
            if(Integer.parseInt(role)!=Values.UserRoles.AGENT)
            {
                archive_active_agent_id="";
                agent_ids = "";
            }
            else
            {
                archive_active_agent_id=agent_id;
                agent_ids = agent_id;
            }

            site_ids = sitesInfoList.get(0).getSiteId();
            current_site_id=sitesInfoList.get(0).getSiteId();
        }
        else
        {
            archive_active_agent_id=agent_id;
            agent_ids = agent_id;
            site_ids = current_site_id;
        }






        isFirstTime = true;

        if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {
            archiveChatArrayList.clear();
            archiceChatAdapter.notifyDataSetChanged();
        }

        if(role !=null && !role.trim().isEmpty() && Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
        {

           // getArchievesApi();//***

            getChatListFromLocalDb();



            // showAgentView(agent,false);
        }
        else
        {
            if(archiveAgentList!=null && archiveAgentList.size()>0){
                boolean isPresent=false;
                int position=0;
                if(archiveAgentList!=null && archiveAgentList.size()>0){
                    for(int k=0;k<archiveAgentList.size();k++){
                        if(archiveAgentList.get(k).getAgentId()!=null && agent.getAgentId()!=null && archiveAgentList.get(k).getAgentId().equals(agent.getAgentId()))
                        {
                            archiveAgentList.get(k).setUserName("Self");
                            isPresent=true;
                            position=k;
                            break;
                        }
                    }
                    if(isPresent && position!=0){
                        ActiveAgent activeAgent=archiveAgentList.get(position);
                        archiveAgentList.remove(position);
                        archiveAgentList.add(0,activeAgent);
                        position=0;
                    }
                }
                updateArchivesList(archiveAgentList.get(position),position);
            }
            else
            {
              changeViews();
            }

        }

    }

    private void getChatListFromLocalDb(){
        List<ActiveChat> list=null;
        Helper.getInstance().LogDetails("ArchivesFragment getChatListFromLocalDb===","called "+current_site_id +" "+agent_ids);
        swipe_refresh.setEnabled(true);
        if(agent_ids!=null && !agent_ids.replace(" ","").trim().isEmpty())
        {
             list=archiveChatsTable.getArchiveChatListWithAgentId(Integer.parseInt(current_site_id), Integer.parseInt(agent_ids));
        }
        else
        {
             list=archiveChatsTable.getArchiveChatList(Integer.parseInt(current_site_id));
        }


        Helper.getInstance().LogDetails("ArchivesFragment getChatListFromLocalDb===","size "+list.size());

        if(list!=null && list.size()>0 ){


            if (archiveChatArrayList == null ) {
               archiveChatArrayList=new ArrayList<>();

            }

            if(archiveChatArrayList!=null && archiveChatArrayList.size()>0){
                archiveChatArrayList.clear();
                archiceChatAdapter.notifyDataSetChanged();
            }
                archiveChatArrayList.addAll(list);
                archiceChatAdapter.notifyDataSetChanged();
                changeViews();
                // tempData.addAll(data.getData());
                //  updateList(tempData);



        }
        else
        {
            if(Utilities.getConnectivityStatus(context)>0){

                getArchievesApi();
            }
            else
            {

                if (archiveChatArrayList != null && archiveChatArrayList.size() > 0) {
                    archiceChatAdapter.notifyDataSetChanged();
                }
                changeViews();
            }


        }
    }

}
