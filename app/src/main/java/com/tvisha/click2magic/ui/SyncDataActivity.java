package com.tvisha.click2magic.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Navigation;
import com.tvisha.click2magic.Helper.SyncData;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.socket.AppSocket;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SyncDataActivity extends BasicActivity implements View.OnClickListener {

    @BindView(R.id.sync_image)
    ImageView sync_image;

    @BindView(R.id.info_textview)
    TextView info_textview;

    @BindView(R.id.loadingprogress)
    ProgressBar loadingprogress;

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try{
            switch (msg.what) {

                case Values.RecentList.RECENT_NETWORK_CONNECTED:
                    if (Helper.getConnectivityStatus(SyncDataActivity.this)) {
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
                    if (Helper.getConnectivityStatus(SyncDataActivity.this)) {
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
                case Values.RecentList.MESSAGES_SYNC_COMPLETED:
                    Helper.getInstance().LogDetails("MESSAGES_SYNC_COMPLETED", "called");
                    Navigation.getInstance().openHomePage(SyncDataActivity.this);
                    break;
                case Values.RecentList.SYNC_FAILED:
                    Helper.getInstance().LogDetails("SYNC_FAILED", "called");
                    if (sync_image != null) {
                        sync_image.setVisibility(View.VISIBLE);
                        loadingprogress.setVisibility(View.GONE);
                        info_textview.setVisibility(View.GONE);
                    }

                    break;

            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.getInstance().LogDetails("SyncDataActivity","onCreate called");
        setContentView(setView());
        ButterKnife.bind(this);
    }

    @Override
    public int setView() {
        return R.layout.activity_sync_data;
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

        //socketConnection();

    }

    @Override
    public void connectToTmSocket() {

        // tmSocketConnection();
    }

    @Override
    public void handleIntent() {
        HandlerHolder.mainActivityUiHandler = uiHandler;
    }

    @Override
    public void initViews() {

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

    @Override
    public void onClick(View v) {
        try{
        if (v.getId() != 0) {
            switch (v.getId()) {
                case R.id.sync_image:
                    sync_image.setVisibility(View.GONE);
                    loadingprogress.setVisibility(View.VISIBLE);
                    info_textview.setVisibility(View.VISIBLE);
                   // SyncData.getMissingMessages(SyncDataActivity.this);
                    break;
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
