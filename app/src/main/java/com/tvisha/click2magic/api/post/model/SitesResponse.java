package com.tvisha.click2magic.api.post.model;
/*
public class SitesResponse {
}*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.post.LoginApi;
import com.tvisha.click2magic.api.post.SitesInfo;

import java.util.List;

public class SitesResponse implements Parcelable
{

    @Override
    public String toString() {
        return "SitesResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", sitesInfo=" + sitesInfo +
                ", CREATOR=" + CREATOR +
                '}';
    }

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sites_info")
    @Expose
    private List<SitesInfo> sitesInfo = null;
    public final  Parcelable.Creator<SitesResponse> CREATOR = new Creator<SitesResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SitesResponse createFromParcel(Parcel in) {
            return new SitesResponse(in);
        }

        public SitesResponse[] newArray(int size) {
            return (new SitesResponse[size]);
        }

    }
            ;

    protected SitesResponse(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.sitesInfo, (SitesInfo.class.getClassLoader()));
    }

    public SitesResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public List<SitesInfo> getSitesInfo() {
        return sitesInfo;
    }

    public void setSitesInfo(List<SitesInfo> sitesInfo) {
        this.sitesInfo = sitesInfo;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(message);
        dest.writeList(sitesInfo);
    }

    public int describeContents() {
        return 0;
    }

}