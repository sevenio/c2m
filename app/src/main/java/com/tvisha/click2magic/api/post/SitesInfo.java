package com.tvisha.click2magic.api.post;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.DataBase.ChatModel;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.Data;

import java.util.ArrayList;
import java.util.List;

public class SitesInfo implements Parcelable
{

    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("site_name")
    @Expose
    private String siteName;
    @SerializedName("site_token")
    @Expose
    private String siteToken;
    @SerializedName("account_id")
    @Expose
    private String accountId;

    private int unread_message_count=0;
    private int agentsCount=0;
    private int activeAgentsCount=0;



    private boolean isChecked=false;
    private boolean isPresent=true;
    private List<ActiveChat> activeChats = new ArrayList<>();


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private List<ActiveAgent> activeAgents = new ArrayList<>();
    private List<ActiveAgent> allAgents = new ArrayList<>();
    private List<ActiveAgent> agents = new ArrayList<>();

    private int online_status=0;
    public final static Parcelable.Creator<SitesInfo> CREATOR = new Creator<SitesInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SitesInfo createFromParcel(Parcel in) {
            return new SitesInfo(in);
        }

        public SitesInfo[] newArray(int size) {
            return (new SitesInfo[size]);
        }

    }
            ;

    public List<ActiveAgent> getAllAgents() {
        return allAgents;
    }

    public void setAllAgents(List<ActiveAgent> allAgents) {
        this.allAgents = allAgents;
    }

    protected SitesInfo(Parcel in) {
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        this.siteName = ((String) in.readValue((String.class.getClassLoader())));
        this.siteToken = ((String) in.readValue((String.class.getClassLoader())));
        this.accountId = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.activeChats, (ActiveChat.class.getClassLoader()));
        this.online_status = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.activeAgents, (ActiveChat.class.getClassLoader()));
        in.readList(this.allAgents, (ActiveChat.class.getClassLoader()));
        in.readList(this.agents, (ActiveChat.class.getClassLoader()));
        this.unread_message_count = ((int) in.readValue((int.class.getClassLoader())));
        this.agentsCount = ((int) in.readValue((int.class.getClassLoader())));
        this.activeAgentsCount = ((int) in.readValue((int.class.getClassLoader())));
        this.isChecked = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.isPresent = ((boolean) in.readValue((boolean.class.getClassLoader())));

    }

    public SitesInfo() {
    }



    public int getOnline_status() {
        return online_status;
    }

    public void setOnline_status(int online_status) {
        this.online_status = online_status;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public List<ActiveAgent> getAgents() {
        return agents;
    }

    public void setAgents(List<ActiveAgent> agents) {
        this.agents = agents;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getAgentsCount() {
        return agentsCount;
    }

    public void setAgentsCount(int agentsCount) {
        this.agentsCount = agentsCount;
    }

    public String getSiteToken() {
        return siteToken;
    }

    public void setSiteToken(String siteToken) {
        this.siteToken = siteToken;
    }

    public int getUnread_message_count() {
        return unread_message_count;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.unread_message_count = unread_message_count;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getActiveAgentsCount() {
        return activeAgentsCount;
    }

    public void setActiveAgentsCount(int activeAgentsCount) {
        this.activeAgentsCount = activeAgentsCount;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<ActiveChat> getActiveChats() {
        return activeChats;
    }

    public List<ActiveAgent> getActiveAgents() {
        return activeAgents;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public void setActiveAgents(List<ActiveAgent> activeAgents) {
        this.activeAgents = activeAgents;
    }

    public void setActiveChats(List<ActiveChat> activeChats) {

        this.activeChats = activeChats;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(siteId);
        dest.writeValue(siteName);
        dest.writeValue(siteToken);
        dest.writeValue(accountId);
        dest.writeList(activeChats);
        dest.writeValue(online_status);
        dest.writeList(activeAgents);
        dest.writeList(allAgents);
        dest.writeList(agents);
        dest.writeValue(unread_message_count);
        dest.writeValue(agentsCount);
        dest.writeValue(activeAgentsCount);
        dest.writeValue(isChecked);
        dest.writeValue(isPresent);

    }

    public int describeContents() {
        return 0;
    }

}