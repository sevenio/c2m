package com.tvisha.click2magic.api.post.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*

public class TrackResponse {
}
*/
public class TrackResponse implements Parcelable
{

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<TrackData> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    public final static Parcelable.Creator<TrackResponse> CREATOR = new Creator<TrackResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrackResponse createFromParcel(Parcel in) {
            return new TrackResponse(in);
        }

        public TrackResponse[] newArray(int size) {
            return (new TrackResponse[size]);
        }

    }
            ;

    protected TrackResponse(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        in.readList(this.data, (TrackData.class.getClassLoader()));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TrackResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<TrackData> getData() {
        return data;
    }

    public void setData(List<TrackData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TrackResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeList(data);
        dest.writeValue(message);
    }

    public int describeContents() {
        return 0;
    }

}