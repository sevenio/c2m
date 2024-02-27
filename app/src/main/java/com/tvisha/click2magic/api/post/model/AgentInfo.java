package com.tvisha.click2magic.api.post.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentInfo implements Parcelable
{

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;

    private boolean isChecked=false;
    public final static Parcelable.Creator<AgentInfo> CREATOR = new Creator<AgentInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AgentInfo createFromParcel(Parcel in) {
            return new AgentInfo(in);
        }

        public AgentInfo[] newArray(int size) {
            return (new AgentInfo[size]);
        }

    }
            ;

    protected AgentInfo(Parcel in) {
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
        this.isChecked = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public AgentInfo() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userId);
        dest.writeValue(userName);
        dest.writeValue(isChecked);
    }

    public int describeContents() {
        return 0;
    }

}