package com.tvisha.click2magic.api.post.model;

import java.io.Serializable;

/**
 * Created by tvisha on 17/7/18.
 */

public class Chat implements Serializable{
    private boolean header;
    private String chatTimeHeaderLable;
    private String stcky_header;
    private int is_download;
    private int message_type;
    private String message;
    private String attachment;
    private int reuest_type;
    private int is_synced;
    private int is_read;
    private int is_delivered;
    private String messaeCreatedAt;
    private int sender_id;
    private int receiver_id;
    private int message_id;
    private String created_at;
    private String filePath;
    private String updated_at;
    private int unreadCount;
    private String headerLable;
    private int userRole;

    public void setHeader(boolean header) {
        this.header = header;
    }

    public void setChatTimeHeaderLable(String chatTimeHeaderLable) {
        this.chatTimeHeaderLable = chatTimeHeaderLable;
    }

    public void setStcky_header(String stcky_header) {
        this.stcky_header = stcky_header;
    }

    public void setIs_download(int is_download) {
        this.is_download = is_download;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public void setReuest_type(int reuest_type) {
        this.reuest_type = reuest_type;
    }

    public void setIs_synced(int is_synced) {
        this.is_synced = is_synced;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public void setIs_delivered(int is_delivered) {
        this.is_delivered = is_delivered;
    }

    public void setMessaeCreatedAt(String messaeCreatedAt) {
        this.messaeCreatedAt = messaeCreatedAt;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getMessage_id() {
        return message_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public String getMessage() {
        return message;
    }

    public int getMessage_type() {
        return message_type;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getIs_download() {
        return is_download;
    }

    public boolean isHeader() {
        return header;
    }

    public String getChatTimeHeaderLable() {
        return chatTimeHeaderLable;
    }

    public String getStcky_header() {
        return stcky_header;
    }

    public int getReuest_type() {
        return reuest_type;
    }

    public int getIs_synced() {
        return is_synced;
    }

    public int getIs_read() {
        return is_read;
    }

    public int getIs_delivered() {
        return is_delivered;
    }

    public String getMessaeCreatedAt() {
        return messaeCreatedAt;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setHeaderLable(String headerLable) {
        this.headerLable = headerLable;
    }

    public String getHeaderLable() {

        return headerLable;
    }

    public int getUserRole() {
        return userRole;
    }
}
