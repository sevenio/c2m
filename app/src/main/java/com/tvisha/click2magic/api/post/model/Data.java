
package com.tvisha.click2magic.api.post.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;


public class Data implements Parcelable
{

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("display_name")
    @Expose
    private String display_name;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("tm_user_id")
    @Expose
    private String tmUserId;
    @SerializedName("company_token")
    @Expose
    private String company_token;
    @SerializedName("company_socket_key")
    @Expose
    private String company_socket_key;
    public final static Parcelable.Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    }
            ;

    protected Data(Parcel in) {
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
        this.display_name = ((String) in.readValue((String.class.getClassLoader())));
        this.companyName = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.role = ((String) in.readValue((String.class.getClassLoader())));
        this.accountId = ((String) in.readValue((String.class.getClassLoader())));
        this.siteId = ((String) in.readValue((String.class.getClassLoader())));
        this.tmUserId = ((String) in.readValue((String.class.getClassLoader())));
        this.company_token = ((String) in.readValue((String.class.getClassLoader())));
        this.company_socket_key = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Data() {
    }

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

    public String getCompanyName() {
        return companyName;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTmUserId() {
        return tmUserId;
    }

    public void setTmUserId(String tmUserId) {
        this.tmUserId = tmUserId;
    }

    public String getCompany_socket_key() {
        return company_socket_key;
    }

    public void setCompany_socket_key(String company_socket_key) {
        this.company_socket_key = company_socket_key;
    }

    public String getCompany_token() {
        return company_token;

    }

    public void setCompany_token(String company_token) {
        this.company_token = company_token;
    }


    @Override
    public String toString() {
        return "Data{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userName='" + display_name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", accountId='" + accountId + '\'' +
                ", siteId='" + siteId + '\'' +
                ", tmUserId='" + tmUserId + '\'' +
                ", company_token='" + company_token + '\'' +
                ", company_socket_key='" + company_socket_key + '\'' +
                '}';
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userId);
        dest.writeValue(userName);
        dest.writeValue(display_name);
        dest.writeValue(companyName);
        dest.writeValue(email);
        dest.writeValue(role);
        dest.writeValue(accountId);
        dest.writeValue(siteId);
        dest.writeValue(tmUserId);
        dest.writeValue(company_token);
        dest.writeValue(company_socket_key);
    }

    public int describeContents() {
        return 0;
    }

}

