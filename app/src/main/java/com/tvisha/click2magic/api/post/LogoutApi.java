package com.tvisha.click2magic.api.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by tvisha on 16/7/18.
 */

public class LogoutApi extends ApiClient
{
    public static interface ApiInterface{

        @FormUrlEncoded
        @POST(ApiEndPoint.API_LOGOUT)
        Call<LogoutApiResponce> getLogoutDetails(
                @Field("agentId") String agentId,
                @Field("securityToken") String securityToken,
                @Field("companyToken") String companyToken
        );
    }
    public class LogoutApiResponce {
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

    }
    public static LogoutApi.ApiInterface getApiService() {
        return getClient().create(LogoutApi.ApiInterface.class);
    }
}
