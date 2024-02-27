package com.tvisha.click2magic.api.post.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("display_name")
    @Expose
    private String display_name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;

    @Override
    public String toString() {
        return "ProfileData{" +
                "userName='" + userName + '\'' +
                ", display_name='" + display_name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
