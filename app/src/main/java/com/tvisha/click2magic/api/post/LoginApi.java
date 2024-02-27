package com.tvisha.click2magic.api.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.post.model.Data;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class LoginApi extends ApiClient {

    public static interface ApiInterface{

        @FormUrlEncoded
        @POST(ApiEndPoint.API_LOGIN)
        Call<LoginResponse> getLoginDetails(

                @Field("email") String email,
                @Field("password") String password,
                @Field("token") String token,
                @Field("fcm_token") String fcm_token,
                @Field("device_type") String deviceType


        );
    }


    public class LoginResponse implements Parcelable
    {

        @SerializedName("success")
        @Expose
        private boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("sites_info")
        @Expose
        private List<SitesInfo> sitesInfo = null;
        public final  Parcelable.Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public LoginResponse createFromParcel(Parcel in) {
                return new LoginResponse(in);
            }

            public LoginResponse[] newArray(int size) {
                return (new LoginResponse[size]);
            }

        }
                ;

        protected LoginResponse(Parcel in) {
            this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
            this.message = ((String) in.readValue((String.class.getClassLoader())));
            this.data = ((Data) in.readValue((Data.class.getClassLoader())));
            in.readList(this.sitesInfo, (SitesInfo.class.getClassLoader()));
        }

        public LoginResponse() {
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

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public List<SitesInfo> getSitesInfo() {
            return sitesInfo;
        }

        public void setSitesInfo(List<SitesInfo> sitesInfo) {
            this.sitesInfo = sitesInfo;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(success);
            dest.writeValue(message);
            dest.writeValue(data);
            dest.writeList(sitesInfo);
        }

        public int describeContents() {
            return 0;
        }

    }
    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }

}
