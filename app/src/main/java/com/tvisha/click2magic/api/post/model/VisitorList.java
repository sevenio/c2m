package com.tvisha.click2magic.api.post.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tvisha on 16/3/18.
 */

public class VisitorList
{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("agentAvatar")
    @Expose
    private String agentAvatar;
    @SerializedName("count")
    @Expose
    private Integer count;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgentAvatar() {
        return agentAvatar;
    }

    public void setAgentAvatar(String agentAvatar) {
        this.agentAvatar = agentAvatar;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
