package com.tvisha.click2magic.model;

/**
 * Created by root on 5/7/17.
 */

public class GalleryModel {

    String path;
    String fileName;
    String fileSize;
    String link;
    String imageUrl;

    int type;
    private String id;
    private String createdAt;
    private String receiverid;
    private String senderid;
    private int entity;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String messageID;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setEntity(int entity) {
        this.entity = entity;
    }
    public int getEntity(){
        return entity;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;

    }
}
