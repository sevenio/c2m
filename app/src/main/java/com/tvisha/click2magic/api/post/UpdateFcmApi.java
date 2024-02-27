package com.tvisha.click2magic.api.post;

/*
public class UpdateFcmApi {
}
*/


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.post.model.Data;
import com.tvisha.click2magic.constants.ApiEndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class UpdateFcmApi extends ApiClient {

    public static interface ApiInterface{

     /*   token,fcm_token,device_type,tm_user_id*/

        @FormUrlEncoded
        @POST(ApiEndPoint.API_UPDATE_FCM)
        Call<UpdateFcmResponse> updateFcm(

                @Field("user_id") String user_id,
                @Field("tm_user_id") String tm_user_id,
                @Field("token") String token,
                @Field("company_token") String company_token,
                @Field("fcm_token") String fcm_token,
                @Field("device_type") String deviceType


        );
    }

    public class UpdateFcmResponse {



        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;


        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "UpdateFcmResponse{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }

}
