package com.tvisha.click2magic.api.post.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteAssetsResponse implements Parcelable
{

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SiteAssetData data;
    @SerializedName("s3url")
    @Expose
    private String s3url;
    public final static Parcelable.Creator<SiteAssetsResponse> CREATOR = new Creator<SiteAssetsResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SiteAssetsResponse createFromParcel(Parcel in) {
            return new SiteAssetsResponse(in);
        }

        public SiteAssetsResponse[] newArray(int size) {
            return (new SiteAssetsResponse[size]);
        }

    }
            ;

    protected SiteAssetsResponse(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.data = ((SiteAssetData) in.readValue((SiteAssetData.class.getClassLoader())));
        this.s3url = ((String) in.readValue((String.class.getClassLoader())));
    }
    public String getS3url() {
        return s3url;
    }

    public void setS3url(String s3url) {
        this.s3url = s3url;
    }
    public SiteAssetsResponse() {
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

    public SiteAssetData getData() {
        return data;
    }

    public void setData(SiteAssetData data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(message);
        dest.writeValue(data);
        dest.writeValue(s3url);
    }

    @Override
    public String toString() {
        return "SiteAssetsResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", s3url='" + s3url + '\'' +
                '}';
    }

    public int describeContents() {
        return 0;
    }

}