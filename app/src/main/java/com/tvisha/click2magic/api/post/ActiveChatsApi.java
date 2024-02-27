package com.tvisha.click2magic.api.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveChatsApi extends ApiClient {


    public static interface ApiInterface{
        @FormUrlEncoded
        @POST(ApiEndPoint.API_ACTIVE_CHATS)
        Call<ActiveChatsApi.ActiveChatsResponse> getActiveChats(
                @Field("token") String token,
                @Field("company_token") String company_token,
                @Field("user_id") String user_id,
                @Field("site_id") String site_id,
                @Field("account_id") String account_id,
                @Field("role") String role

        );
    }




    public class ActiveChatsResponse implements Parcelable
    {

        @SerializedName("success")
        @Expose
        private boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("data")
        @Expose
        private List<SiteData> data = null;
        public final  Parcelable.Creator<ActiveChatsResponse> CREATOR = new Creator<ActiveChatsResponse>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public ActiveChatsResponse createFromParcel(Parcel in) {
                return new ActiveChatsResponse(in);
            }

            public ActiveChatsResponse[] newArray(int size) {
                return (new ActiveChatsResponse[size]);
            }

        }
                ;

        protected ActiveChatsResponse(Parcel in) {
            this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
            this.message = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(this.data, (SiteData.class.getClassLoader()));
        }

        public ActiveChatsResponse() {
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

        public List<SiteData> getData() {
            return data;
        }

        public void setData(List<SiteData> data) {
            this.data = data;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(success);
            dest.writeValue(message);
            dest.writeList(data);
        }

        public int describeContents() {
            return 0;
        }

    }

    public static ActiveChatsApi.ApiInterface getApiService() {
        return getClient().create(ActiveChatsApi.ApiInterface.class);
    }

}

