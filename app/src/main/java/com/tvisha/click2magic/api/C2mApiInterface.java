package com.tvisha.click2magic.api;


import com.google.gson.JsonObject;
import com.tvisha.click2magic.api.post.ActiveAgentsApi;
import com.tvisha.click2magic.api.post.model.AgentsResponse;
import com.tvisha.click2magic.api.post.model.GetAwsConfigResponse;
import com.tvisha.click2magic.api.post.model.ProfileResponse;
import com.tvisha.click2magic.api.post.model.SiteAssetsResponse;
import com.tvisha.click2magic.api.post.model.SitesResponse;
import com.tvisha.click2magic.api.post.model.TrackResponse;
import com.tvisha.click2magic.constants.ApiEndPoint;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface C2mApiInterface {
/*

    @FormUrlEncoded
    @POST("login")
    Call<ApiResponse> getOtp(@Field("username") String username, @Field("token") String token, @Field("device_id") String deviceId);
*/
/*
    @Headers("user-key: 9900a9720d31dfd5fdb4352700c")
    @GET("api/v2.1/search")
    Call<ApiResponse> getRestaurantsBySearch(@Query("entity_id") String entity_id, @Query("entity_type") String entity_type, @Query("q") String query);
*/




    @FormUrlEncoded
    @POST(ApiEndPoint.API_GET_AWS_KEYS)
    Call<GetAwsConfigResponse> getAwsConfig(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("profile")
    Call<ProfileResponse> getProfile(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("get-agents")
    Call<AgentsResponse> getAllAgents(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id
    );
    @FormUrlEncoded
    @POST(ApiEndPoint.API_SITE_ASSETS)
    Call<SiteAssetsResponse> getSiteAssets(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST(ApiEndPoint.API_SITES)
    Call<SitesResponse> getSites(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST(ApiEndPoint.API_SITE_ASSETS)
    Call<JsonObject> getSiteAssets1(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id
    );


    @FormUrlEncoded
    @POST("edit-profile")
    Call<ApiResponse> updateProfile(
            @Field("token") String token,
            @Field("company_token") String company_token,
            @Field("user_id") String user_id,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("profile_pic") String profile_pic,
            @Field("display_name") String display_name
    );

    @FormUrlEncoded
    @POST("change-password")
    Call<ApiResponse> changePassword(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("mobile") String mobile,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password
    );

    @FormUrlEncoded
    @POST(ApiEndPoint.API_GET_TRACK)
    Call<TrackResponse> getTrack(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("company_token") String companyToken,
            @Field("site_id") String site_id,
            @Field("track_code") String track_code,
            @Field("chat_date") String chat_date

    );




}
