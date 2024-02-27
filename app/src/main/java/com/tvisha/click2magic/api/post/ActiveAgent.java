package com.tvisha.click2magic.api.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveAgent implements Parcelable
{



    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("agent_id")
    @Expose
    private String agentId;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("is_online")
    @Expose
    private String isOnline;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("tm_user_id")
    @Expose
    private String tmUserId;
    @SerializedName("active_chat_count")
    @Expose
    private String active_chat_count;
    @SerializedName("user_site_id")
    @Expose
    private String user_site_id;

    private boolean isChecked=false;
    public final static Parcelable.Creator<ActiveAgent> CREATOR = new Creator<ActiveAgent>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ActiveAgent createFromParcel(Parcel in) {
            return new ActiveAgent(in);
        }

        public ActiveAgent[] newArray(int size) {
            return (new ActiveAgent[size]);
        }

    }
            ;

    public boolean isChecked() {
        return isChecked;
    }

    public String getUser_site_id() {
        return user_site_id;
    }

    public void setUser_site_id(String user_site_id) {
        this.user_site_id = user_site_id;
    }

    public void setChecked(boolean checked) {

        isChecked = checked;
    }



    public String getActive_chat_count() {
        return active_chat_count;
    }

    public void setActive_chat_count(String active_chat_count) {
        this.active_chat_count = active_chat_count;
    }

    protected ActiveAgent(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.agentId = ((String) in.readValue((String.class.getClassLoader())));
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        this.accountId = ((String) in.readValue((String.class.getClassLoader())));
        this.isOnline = ((String) in.readValue((String.class.getClassLoader())));

        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
        this.role = ((String) in.readValue((String.class.getClassLoader())));
        this.tmUserId = ((String) in.readValue((String.class.getClassLoader())));
        this.active_chat_count = ((String) in.readValue((String.class.getClassLoader())));
        this.isChecked = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.user_site_id = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ActiveAgent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTmUserId() {
        return tmUserId;
    }

    public void setTmUserId(String tmUserId) {
        this.tmUserId = tmUserId;
    }

    @Override
    public String toString() {
        return "ActiveAgent{" +
                "id='" + id + '\'' +
                ", agentId='" + agentId + '\'' +
                ", siteId='" + siteId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", isOnline='" + isOnline + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", role='" + role + '\'' +
                ", tmUserId='" + tmUserId + '\'' +
                '}';
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(agentId);
        dest.writeValue(siteId);
        dest.writeValue(accountId);
        dest.writeValue(isOnline);

        dest.writeValue(userId);
        dest.writeValue(userName);
        dest.writeValue(role);
        dest.writeValue(tmUserId);
        dest.writeValue(active_chat_count);
        dest.writeValue(isChecked);
        dest.writeValue(user_site_id);
    }

    public int describeContents() {
        return 0;
    }

}