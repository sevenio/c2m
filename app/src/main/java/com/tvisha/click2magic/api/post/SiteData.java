package com.tvisha.click2magic.api.post;
/*

public class SiteData {
}
*/

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.post.model.ActiveChat;

public class SiteData implements Parcelable
{

    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("active_chats")
    @Expose
    private List<ActiveChat> activeChats = null;
    public final  Parcelable.Creator<SiteData> CREATOR = new Creator<SiteData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SiteData createFromParcel(Parcel in) {
            return new SiteData(in);
        }

        public SiteData[] newArray(int size) {
            return (new SiteData[size]);
        }

    }
            ;

    protected SiteData(Parcel in) {
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.activeChats, (ActiveChat.class.getClassLoader()));
    }

    public SiteData() {
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<ActiveChat> getActiveChats() {
        return activeChats;
    }

    public void setActiveChats(List<ActiveChat> activeChats) {
        this.activeChats = activeChats;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(siteId);
        dest.writeList(activeChats);
    }

    public int describeContents() {
        return 0;
    }

}