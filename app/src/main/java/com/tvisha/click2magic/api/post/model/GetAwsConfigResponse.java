package com.tvisha.click2magic.api.post.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAwsConfigResponse {


    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "GetAwsConfigResponse{" +
                "success=" + success +
                ", data='" + data + '\'' +
                ", message='" + message + '\'' +
                '}';
    }



    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}