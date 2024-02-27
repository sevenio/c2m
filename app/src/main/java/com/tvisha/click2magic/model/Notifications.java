package com.tvisha.click2magic.model;


public class Notifications {
    String userId;
    String userName;
    int entity,idGroup,status;
    String message;
    String newMessage;
    String contentText;
    String unreadCount;
    private String siteId;
    private String conversation_reference_id;
    private String siteName;
    private int totalMessage;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsGroup() {
        return idGroup;
    }

    public void setIsGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    private String groupName;
    private String senderName;
    private String senderId;
    private String entityId;

    public String getConversation_reference_id() {
        return conversation_reference_id;
    }

    public void setConversation_reference_id(String conversation_reference_id) {
        this.conversation_reference_id = conversation_reference_id;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getMessageTime() {
        return messageTime;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", entity=" + entity +
                ", idGroup=" + idGroup +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", unreadCount='" + unreadCount + '\'' +
                ", groupName='" + groupName + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderId='" + senderId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", messageTime='" + messageTime + '\'' +
                '}';
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    private String messageTime;

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

    public int getEntity() {
        return entity;
    }

    public void setEntity(int entity) {
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public void setEntityId(int receiver_id) {
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getSiteName() {
        return siteName;
    }

    public int getTotalMessage() {
        return totalMessage;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setTotalMessage(int totalMessage) {
        this.totalMessage = totalMessage;
    }
}
