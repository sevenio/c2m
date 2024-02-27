package com.tvisha.click2magic.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMessagesResponse {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("messages")
    @Expose
    private List<Message> messages = null;
    @SerializedName("errorCode")
    @Expose
    private int errorCode ;
    @SerializedName("message")
    @Expose
    private String message ;

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "GetMessagesResponse{" +
                "success=" + success +
                ", messages=" + messages +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}