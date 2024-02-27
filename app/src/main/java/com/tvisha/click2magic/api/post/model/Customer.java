package com.tvisha.click2magic.api.post.model;

/**
 * Created by tvisha on 23/7/18.
 */

public class Customer {
    private int user_id;
    private int id;
    private String name;
    private String email;
    private int chatId;
    private int userRole;
    private String profilePic;
    private String deviceName;
    private String userOs;
    private String visit;
    private String entry;
    private String socketID;
    private String mobile;
    private int message_count;

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public String getMessege() {
        return messege;
    }

    private int agent_id;
    private String messege;

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setUserOs(String userOs) {
        this.userOs = userOs;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getChatId() {
        return chatId;
    }

    public int getUserRole() {
        return userRole;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getUserOs() {
        return userOs;
    }

    public String getVisit() {
        return visit;
    }

    public String getEntry() {
        return entry;
    }

    public String getSocketID() {
        return socketID;
    }

    public String getMobile() {
        return mobile;
    }

    public int getMessage_count() {
        return message_count;
    }

    public void setSocketID(String socketID) {
        this.socketID = socketID;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }
}
