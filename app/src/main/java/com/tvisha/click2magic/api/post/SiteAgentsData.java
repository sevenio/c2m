package com.tvisha.click2magic.api.post;
/*

public class SiteAgentsData {
}
*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import java.util.List;

public class SiteAgentsData implements Parcelable {

    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("active_agents")
    @Expose
    private List<ActiveAgent> activeAgents = null;
    public final Parcelable.Creator<SiteAgentsData> CREATOR = new Creator<SiteAgentsData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SiteAgentsData createFromParcel(Parcel in) {
            return new SiteAgentsData(in);
        }

        public SiteAgentsData[] newArray(int size) {
            return (new SiteAgentsData[size]);
        }

    };

    protected SiteAgentsData(Parcel in) {
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.activeAgents, (ActiveChat.class.getClassLoader()));
    }

    public SiteAgentsData() {
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<ActiveAgent> getActiveAgents() {
        return activeAgents;
    }

    public void setActiveAgents(List<ActiveAgent> activeAgents) {
        this.activeAgents = activeAgents;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(siteId);
        dest.writeList(activeAgents);
    }

    public int describeContents() {
        return 0;
    }
}

