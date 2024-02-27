package com.tvisha.click2magic.api.post.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.DataBase.ChatModel;

public class ActiveChat implements Parcelable
{

/*
    {
        "chat_id": "1040",
            "visitor_id": "876",
            "visitor_tm_id": "0",
            "guest_name": "two",
            "start_time": "2019-07-19 15:03:56",
            "end_time": "2019-07-19 15:18:11",
            "visitor_os": "Linux (unknown)",
            "visitor_url": "http://192.168.2.77/kranthi.php",
            "chat_rating": "",
            "quality": "NA",
            "category": "15",
            "visitor_ip": "192.168.4.133",
            "visitor_browser": "Chrome (65.0.3325.181)",
            "agent_id": "10",
            "chat_reference_id": "7b7357580013e013d4",
            "location": ",,",
            "status": "0",
            "visitor_query": "",
            "chat_status": "0",
            "account_id": "53",
            "site_id": "1",
            "track_code": "3604T51CS6P57",
            "created_at": "2019-07-19 15:03:56",
            "updated_at": "2019-07-19 15:18:20",
            "name": "two",
            "email": "mail@email.com",
            "mobile": "9222222222",
            "visit_count": "1st",
            "tm_visitor_id": "5553",
            "user_name": "Admin",
            "tm_user_id": "3715",
            "tag_name": "NA",
            "rating": "NA"
    }*/

    @SerializedName("chat_id")
    @Expose
    private String chatId;
    @SerializedName("visitor_id")
    @Expose
    private String visitorId;
    @SerializedName("guest_name")
    @Expose
    private String guestName;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("visitor_os")
    @Expose
    private String visitorOs;
    @SerializedName("visitor_url")
    @Expose
    private String visitorUrl;
    @SerializedName("chat_rating")
    @Expose
    private String chatRating;
    @SerializedName("visitor_ip")
    @Expose
    private String visitorIp;
    @SerializedName("visitor_browser")
    @Expose
    private String visitorBrowser;
    @SerializedName("agent_id")
    @Expose
    private String agentId;
    @SerializedName("chat_reference_id")
    @Expose
    private String chatReferenceId;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("visitor_query")
    @Expose
    private String visitorQuery;
    @SerializedName("chat_status")
    @Expose
    private String chatStatus;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("visit_count")
    @Expose
    private String visitCount;
    @SerializedName("tm_visitor_id")
    @Expose
    private String tmVisitorId;
    @SerializedName("user_name")
    @Expose
    private String user_name;
    @SerializedName("tag_name")
    @Expose
    private String tag_name;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("track_code")
    @Expose
    private String track_code;

    private int unread_message_count=0;
    private int online=1;
    private String typing_message="";

    private ChatModel chatModel=new ChatModel();


    public final static Parcelable.Creator<ActiveChat> CREATOR = new Creator<ActiveChat>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ActiveChat createFromParcel(Parcel in) {
            return new ActiveChat(in);
        }

        public ActiveChat[] newArray(int size) {
            return (new ActiveChat[size]);
        }

    }
            ;

    protected ActiveChat(Parcel in) {
        this.chatId = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorId = ((String) in.readValue((String.class.getClassLoader())));
        this.guestName = ((String) in.readValue((String.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorOs = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.chatRating = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorIp = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorBrowser = ((String) in.readValue((String.class.getClassLoader())));
        this.agentId = ((String) in.readValue((String.class.getClassLoader())));
        this.chatReferenceId = ((String) in.readValue((String.class.getClassLoader())));
        this.location = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.visitorQuery = ((String) in.readValue((String.class.getClassLoader())));
        this.chatStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.accountId = ((String) in.readValue((String.class.getClassLoader())));
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.mobile = ((String) in.readValue((String.class.getClassLoader())));
        this.visitCount = ((String) in.readValue((String.class.getClassLoader())));
        this.tmVisitorId = ((String) in.readValue((String.class.getClassLoader())));
        this.user_name = ((String) in.readValue((String.class.getClassLoader())));
        this.unread_message_count = ((int) in.readValue((int.class.getClassLoader())));
        this.chatModel = ((ChatModel) in.readValue((Data.class.getClassLoader())));
        this.tag_name = ((String) in.readValue((String.class.getClassLoader())));
        this.rating = ((String) in.readValue((String.class.getClassLoader())));
        this.track_code = ((String) in.readValue((String.class.getClassLoader())));
        this.online = ((int) in.readValue((int.class.getClassLoader())));
        this.typing_message = ((String) in.readValue((String.class.getClassLoader())));

    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(chatId);
        dest.writeValue(visitorId);
        dest.writeValue(guestName);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
        dest.writeValue(visitorOs);
        dest.writeValue(visitorUrl);
        dest.writeValue(chatRating);
        dest.writeValue(visitorIp);
        dest.writeValue(visitorBrowser);
        dest.writeValue(agentId);
        dest.writeValue(chatReferenceId);
        dest.writeValue(location);
        dest.writeValue(status);
        dest.writeValue(visitorQuery);
        dest.writeValue(chatStatus);
        dest.writeValue(accountId);
        dest.writeValue(siteId);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(name);
        dest.writeValue(email);
        dest.writeValue(mobile);
        dest.writeValue(visitCount);
        dest.writeValue(tmVisitorId);
        dest.writeValue(user_name);
        dest.writeValue(unread_message_count);
        dest.writeValue(chatModel);
        dest.writeValue(tag_name);
        dest.writeValue(rating);
        dest.writeValue(track_code);
        dest.writeValue(online);
        dest.writeValue(typing_message);


    }


    public String getTyping_message() {
        return typing_message;
    }

    public void setTyping_message(String typing_message) {
        this.typing_message = typing_message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public ActiveChat() {
    }

    public int getUnread_message_count() {
        return unread_message_count;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.unread_message_count = unread_message_count;
    }
    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVisitorOs() {
        return visitorOs;
    }

    public void setVisitorOs(String visitorOs) {
        this.visitorOs = visitorOs;
    }

    public String getVisitorUrl() {
        return visitorUrl;
    }

    public void setVisitorUrl(String visitorUrl) {
        this.visitorUrl = visitorUrl;
    }

    public String getChatRating() {
        return chatRating;
    }

    public void setChatRating(String chatRating) {
        this.chatRating = chatRating;
    }

    public String getVisitorIp() {
        return visitorIp;
    }

    public void setVisitorIp(String visitorIp) {
        this.visitorIp = visitorIp;
    }

    public String getVisitorBrowser() {
        return visitorBrowser;
    }

    public void setVisitorBrowser(String visitorBrowser) {
        this.visitorBrowser = visitorBrowser;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getChatReferenceId() {
        return chatReferenceId;
    }

    public void setChatReferenceId(String chatReferenceId) {
        this.chatReferenceId = chatReferenceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVisitorQuery() {
        return visitorQuery;
    }

    public void setVisitorQuery(String visitorQuery) {
        this.visitorQuery = visitorQuery;
    }

    public String getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(String chatStatus) {
        this.chatStatus = chatStatus;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getTrack_code() {
        return track_code;
    }

    public void setTrack_code(String track_code) {
        this.track_code = track_code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }



    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    public String getTmVisitorId() {
        return tmVisitorId;
    }

    public void setTmVisitorId(String tmVisitorId) {
        this.tmVisitorId = tmVisitorId;
    }

    @Override
    public String toString() {
        return "ActiveChat{" +
                "chatId='" + chatId + '\'' +
                ", visitorId='" + visitorId + '\'' +
                ", guestName='" + guestName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", visitorOs='" + visitorOs + '\'' +
                ", visitorUrl='" + visitorUrl + '\'' +
                ", chatRating='" + chatRating + '\'' +
                ", visitorIp='" + visitorIp + '\'' +
                ", visitorBrowser='" + visitorBrowser + '\'' +
                ", agentId='" + agentId + '\'' +
                ", chatReferenceId='" + chatReferenceId + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", visitorQuery='" + visitorQuery + '\'' +
                ", chatStatus='" + chatStatus + '\'' +
                ", accountId='" + accountId + '\'' +
                ", siteId='" + siteId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", visitCount='" + visitCount + '\'' +
                ", tmVisitorId='" + tmVisitorId + '\'' +
                ", user_name='" + user_name + '\'' +
                ", tag_name='" + tag_name + '\'' +
                ", rating='" + rating + '\'' +
                ", track_code='" + track_code + '\'' +
                ", unread_message_count=" + unread_message_count +
                ", chatModel=" + chatModel +
                '}';
    }


    public int describeContents() {
        return 0;
    }

}