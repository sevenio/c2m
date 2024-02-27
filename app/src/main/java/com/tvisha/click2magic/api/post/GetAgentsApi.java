package com.tvisha.click2magic.api.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.post.model.Agent;
import com.tvisha.click2magic.api.post.model.Data;
import com.tvisha.click2magic.constants.ApiEndPoint;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class GetAgentsApi extends ApiClient {

    public static interface ApiInterface{

        @FormUrlEncoded
        @POST(ApiEndPoint.API_GET_AGENTS)
        Call<GetAgentsApiRes> getAgents(
                @Field("agentId") String agentId,
                @Field("companyToken") String companyToken,
                @Field("securityToken") String securityToken,
                @Field("websiteIds") JSONArray fields
        );
    }

    public class GetAgentsApiRes {

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

        public List<Agent> getAgents() {
            return agents;
        }

        public void setAgents(List<Agent> agents) {
            this.agents = agents;
        }

        @SerializedName("data")
        @Expose
        private List<Agent> agents;


    }
    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }

}
