package com.tvisha.click2magic.api.post.model;


import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentsResponse implements Parcelable
{

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<AgentInfo> data = null;
    public final static Parcelable.Creator<AgentsResponse> CREATOR = new Creator<AgentsResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AgentsResponse createFromParcel(Parcel in) {
            return new AgentsResponse(in);
        }

        public AgentsResponse[] newArray(int size) {
            return (new AgentsResponse[size]);
        }

    }
            ;

    protected AgentsResponse(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (AgentInfo.class.getClassLoader()));
    }

    public AgentsResponse() {
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

    public List<AgentInfo> getData() {
        return data;
    }

    public void setData(List<AgentInfo> data) {
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
