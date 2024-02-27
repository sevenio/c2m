package com.tvisha.click2magic.api.post.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.post.model.Category;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class CategoriesApi extends ApiClient {

    public static interface ApiInterface{
        @FormUrlEncoded
        @POST(ApiEndPoint.API_FEEDBACK_CATEGORIES)
        Call<CategoriesResponse> getCategories(
                @Field("token") String token,
                @Field("user_id") String user_id,
                @Field("company_token") String companyToken


        );
    }

    public class CategoriesResponse{

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("data")
        @Expose
        private ArrayList<Category> data = null;

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

        public ArrayList<Category> getData() {
            return data;
        }

        public void setData(ArrayList<Category> data) {
            this.data = data;
        }
    }

    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }
}
