package com.tvisha.click2magic.api.post;

/*
public class AgentStatusUpdated {


}
*/


import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentStatusUpdated extends ApiClient {


    public static interface ApiInterface{
        @FormUrlEncoded
        @POST(ApiEndPoint.API_AGENT_STATUS)
        Call<AgentStatusUpdated.AgentStatusUpdatedResponse> updateAgentStatus(
                @Field("token") String token,
                @Field("user_id") String user_id,
                @Field("pxve7dwjnHTaqhI") boolean agentStatus


        );
    }


    public class AgentStatusUpdatedResponse {

        @SerializedName("success")
        @Expose
        private boolean success;
        @SerializedName("message")
        @Expose
        private String message;


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



    }

    public static AgentStatusUpdated.ApiInterface getApiService() {
        return getClient().create(AgentStatusUpdated.ApiInterface.class);
    }

}

