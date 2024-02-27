package com.tvisha.click2magic.api.post;

import com.google.gson.JsonObject;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.api.ApiResponse;
import com.tvisha.click2magic.constants.ApiEndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ArchievsApi  extends ApiClient {

        public static interface ApiInterface{
            @FormUrlEncoded
            @POST(ApiEndPoint.API_ARCHIVES)
            Call<ArchievsResponse> getArchievs(
                    @Field("token") String token,
                    @Field("company_token") String company_token,
                    @Field("user_id") String user_id,
                    @Field("site_id") String site_id,
                    @Field("role") String role,
                    @Field("limit") int limit,
                    @Field("offset") int offset,
                    @Field("agent_ids") String agent_ids,
                    @Field("site_ids") String site_ids,
                    @Field("from_date") String from_date,
                    @Field("to_date") String to_date


            );
        }





        public static com.tvisha.click2magic.api.post.ArchievsApi.ApiInterface getApiService() {
            return getClient().create(com.tvisha.click2magic.api.post.ArchievsApi.ApiInterface.class);
        }



}
