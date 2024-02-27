package com.tvisha.click2magic.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.ArchivesAdapter;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.socket.AppSocket;

import java.util.ArrayList;
import io.socket.client.Socket;


public class ArchivesActivity extends AppCompatActivity implements View.OnClickListener {


    public Socket mSocket, tmSocket;
    RecyclerView archiev_chat_recycler_view;
    TextView actionLable;
    ImageView no_chats_image;
    LinearLayout back_lay;
    ArrayList<ActiveChat> activeChatArrayList = new ArrayList<>();
    ArchivesAdapter archiceChatAdapter;
    int limit = 1000, offset = 0, oldOffSet = 0;

    String user_id = "", site_id = "", role = "", user_name = "", company_token = "";
    Dialog dialog = null;
    boolean isAvailble = true, isFirstTime = true;
    long mLastClickTime = 0;
    boolean isSelf = true;

    Activity activity;
    Context context;



    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case Values.RecentList.RECENT_NETWORK_CONNECTED:
                    if (Helper.getConnectivityStatus(ArchivesActivity.this)) {
                        Helper.getInstance().LogDetails("RECENT_NETWORK_CONNECTED", "called");
                        socketConnections();

                    }
                    break;
                case Values.RecentList.FCM_TRIGGER:
                    if (Helper.getConnectivityStatus(ArchivesActivity.this)) {
                        socketConnections();
                    }

            }
        }
    };

    AppSocket appSocket;

    private void socketConnections(){
        if(appSocket==null){
            appSocket=(AppSocket) getApplication();
        }
        if (mSocket != null && !mSocket.connected()) {
           appSocket.connectSocket();
        }

        if (tmSocket != null && !tmSocket.connected()) {
            appSocket.connectTmSocket();
        }
    }

    @Override
    protected void onRestart() {
        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_ARCHIVES;
        if (archiceChatAdapter != null) {
            archiceChatAdapter.selectionMode = true;
        }
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archies);
        activity=ArchivesActivity.this;
        context=ArchivesActivity.this;
        sharePreferencesData();
        progressDialog();
        initViews();
        initListeners();


    }

    private void initListeners() {
        back_lay.setOnClickListener(this);
    }

    private void sharePreferencesData() {

        isSelf = Session.getIsSelf(context);
        if (isSelf) {
            user_id =  Session.getUserID(ArchivesActivity.this);
            site_id =  Session.getSiteId(context);
            role =  Session.getUserRole(context);
            user_name =  Session.getUserName(context);
            company_token =  Session.getCompanyToken(context);
        } else {
            user_id = Session.getOtherUserId(context);
            site_id = Session.getOtherUserSiteId(context);
            role =  Session.getOtherUserRole(context);
            user_name = Session.getOtherUserDisplayName(context);
            company_token = Session.getCompanyToken(context);
        }

    }

    private void initViews() {
        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.ACTIVITY_ARCHIVES;
        HandlerHolder.mainActivityUiHandler = uiHandler;
        AppSocket appSocket = (AppSocket) getApplication();
        mSocket = appSocket.getSocketInstance();
        tmSocket = appSocket.getTmSocketInstance();
        archiev_chat_recycler_view = findViewById(R.id.archiev_chat_recycler_view);
        actionLable = findViewById(R.id.actionLable);
        no_chats_image = findViewById(R.id.no_chats_image);
        actionLable.setText("Archive Action");
        back_lay = findViewById(R.id.back_lay);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(ArchivesActivity.this, LinearLayoutManager.VERTICAL, false);
        archiev_chat_recycler_view.addItemDecoration(new DividerItemDecoration(ArchivesActivity.this, DividerItemDecoration.VERTICAL));
        archiev_chat_recycler_view.setLayoutManager(layoutManager3);
        archiev_chat_recycler_view.setNestedScrollingEnabled(false);
        archiceChatAdapter = new ArchivesAdapter(ArchivesActivity.this, activeChatArrayList);
        archiev_chat_recycler_view.setAdapter(archiceChatAdapter);


        getArchieves();

        /*archiev_chat_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerview, int dx, int dy) {
                super.onScrolled(recyclerview, dx, dy);

                LinearLayoutManager myLayoutManager = (LinearLayoutManager) archiev_chat_recycler_view.getLayoutManager();
                int  totalItemCount = myLayoutManager.getItemCount();
                int pastVisiblesItems = myLayoutManager.findLastVisibleItemPosition();
                int scrollPosition = myLayoutManager.findLastVisibleItemPosition();
                if (dy > 0) {

                    Helper.getInstance().LogDetails("findLastCompletelyVisibleItemPosition",myLayoutManager.findLastCompletelyVisibleItemPosition() +" "+sitesInfoList.size()+" "+offset);

                    if(  sitesInfoList.size()==offset && myLayoutManager.findLastCompletelyVisibleItemPosition() == sitesInfoList.size() -1){
                        //bottom of agentsList!
                        if(Helper.getConnectivityStatus(ArchivesActivity.this))
                        {
                          getArchieves();
                        }
                    }


                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView agents_recyclerView, int newState) {
                super.onScrollStateChanged(agents_recyclerView, newState);
            }
        });
*/

    }

    private void getArchieves() {


        if (Utilities.getConnectivityStatus(ArchivesActivity.this) <= 0) {
            Helper.getInstance().pushToast(ArchivesActivity.this, "Please check your network connection...");
            return;
        }

        if (isFirstTime) {
            isFirstTime = false;
            mLastClickTime = SystemClock.elapsedRealtime();
            openProgess();
        }


        Helper.getInstance().LogDetails("getArchieves ", ApiEndPoint.token + " " + user_id + " " + site_id + " " + role + " " + limit + " " + offset + " ");

        /*retrofit2.Call<ArchievsResponse> call = ArchievsApi.getApiService().getArchievs(ApiEndPoint.token,company_token, user_id, site_id, role,limit,offset,"","","","");

        call.enqueue(new Callback<ArchievsResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Response<ArchievsResponse> response) {
                closeProgress();

                ArchievsResponse data = response.body();
                oldOffSet=offset;
                offset=offset+limit;
                Helper.getInstance().LogDetails("getArchieves after",ApiEndPoint.token+" "+user_id+" "+site_id+" "+role+" "+limit+" "+offset+" ");
                if (data != null) {
                    if (data.isSuccess()) {

                            if(data.getData()!=null && data.getData().size()>0){

                               if(activeChatArrayList ==null || activeChatArrayList.size()==0)
                               {
                                   activeChatArrayList.addAll(data.getData());



                               }
                               else
                               {
                                   tempData.addAll(data.getData());
                                   updateList(tempData);
                               }


                                if(activeChatArrayList !=null && activeChatArrayList.size()>0){

                                    archiceChatAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    no_chats_image.setVisibility(View.VISIBLE);
                                }

                            }
                            else
                            {
                                if(activeChatArrayList !=null && activeChatArrayList.size()>0)
                                {

                                }
                                else
                                {
                                    no_chats_image.setVisibility(View.VISIBLE);
                                }

                            }

                    } else {
                        Helper.getInstance().pushToast(ArchivesActivity.this, data.getMessage());
                    }
                } else {
                    Helper.getInstance().pushToast(ArchivesActivity.this, "Server connection failed");
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ArchievsResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                closeProgress();
            }
        });*/



   /*     retrofit2.Call<JsonObject> call = ArchievsApi.getApiService().getArchievs(ApiEndPoint.token, user_id, site_id, role,limit,offset);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progressBar.dismiss();

                JsonObject data = response.body();

                Helper.getInstance().LogDetails("History data ",data.toString());




            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                progressBar.dismiss();
            }
        });*/


    }

    private void updateList(ArrayList<ActiveChat> tempData) {
        if (tempData != null && tempData.size() > 0) {
            if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                for (int i = 0; i < tempData.size(); i++) {
                    boolean isExists = false;
                    for (int j = 0; j < activeChatArrayList.size(); j++) {
                        if (tempData.get(i).getChatId() != null && activeChatArrayList.get(j).getChatId() != null && tempData.get(i).getChatId().equals(activeChatArrayList.get(j))) {
                            isExists = true;
                        }
                    }
                    if (!isExists) {
                        activeChatArrayList.add(tempData.get(i));

                    }
                }
                archiceChatAdapter.notifyDataSetChanged();
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_lay:
                onBackPressed();
                break;
        }
    }

    private void progressDialog() {

        try {
            dialog = new Dialog(ArchivesActivity.this, R.style.DialogTheme);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openProgess() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void closeProgress() {
        if (dialog != null) {
            dialog.cancel();
            ;
        }
    }

}
