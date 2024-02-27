package com.tvisha.click2magic.api.post.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class ChatRatingUpdateResponse extends ApiClient {


    public static interface ApiInterface{
        @FormUrlEncoded
        @POST(ApiEndPoint.API_CHAT_RATING_UPDATE)
        Call<RatingUpdateResponse> updateRating(
                @Field("token") String token,
                @Field("user_id") String user_id,
                @Field("company_token") String companyToken,
                @Field("category") String category,
                @Field("rating") String rating,
                @Field("chatId") String chatId


        );
    }

    public class RatingUpdateResponse{

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


    }

    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }
}