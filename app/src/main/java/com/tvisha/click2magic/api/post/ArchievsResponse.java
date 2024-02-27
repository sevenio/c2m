package com.tvisha.click2magic.api.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import java.util.ArrayList;
import java.util.List;


public class ArchievsResponse implements Parcelable
{

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ActiveChat> data = new ArrayList<>();
    public final  Parcelable.Creator<ArchievsResponse> CREATOR = new Creator<ArchievsResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ArchievsResponse createFromParcel(Parcel in) {
            return new ArchievsResponse(in);
        }

        public ArchievsResponse[] newArray(int size) {
            return (new ArchievsResponse[size]);
        }

    }
            ;

    protected ArchievsResponse(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (SiteData.class.getClassLoader()));
    }

    public ArchievsResponse() {
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

    public List<ActiveChat> getData() {
        return data;
    }

    public void setData(List<ActiveChat> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(message);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }

}
