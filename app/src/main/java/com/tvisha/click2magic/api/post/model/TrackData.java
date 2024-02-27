package com.tvisha.click2magic.api.post.model;
/*
public class TrackData {
}*/

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackData implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("track_code")
    @Expose
    private String trackCode;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("referrer_link")
    @Expose
    private String referrerLink;
    @SerializedName("time_spent")
    @Expose
    private String timeSpent;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("session")
    @Expose
    private String session;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    public final static Parcelable.Creator<TrackData> CREATOR = new Creator<TrackData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrackData createFromParcel(Parcel in) {
            return new TrackData(in);
        }

        public TrackData[] newArray(int size) {
            return (new TrackData[size]);
        }

    }
            ;

    protected TrackData(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.trackCode = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.referrerLink = ((String) in.readValue((String.class.getClassLoader())));
        this.timeSpent = ((String) in.readValue((String.class.getClassLoader())));
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        this.accountId = ((String) in.readValue((String.class.getClassLoader())));
        this.session = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TrackData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferrerLink() {
        return referrerLink;
    }

    public void setReferrerLink(String referrerLink) {
        this.referrerLink = referrerLink;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
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

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCreatedAt() {
        return createdAt;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(trackCode);
        dest.writeValue(title);
        dest.writeValue(url);
        dest.writeValue(referrerLink);
        dest.writeValue(timeSpent);
        dest.writeValue(siteId);
        dest.writeValue(accountId);
        dest.writeValue(session);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
    }

    public int describeContents() {
        return 0;
    }

}