package com.tvisha.click2magic.chatApi;


import com.google.gson.JsonObject;
import com.tvisha.click2magic.constants.ApiEndPoint;
import com.tvisha.click2magic.model.GetMessagesResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

/*

    @Headers("user-key: 9900a9720d31dfd5fdb4352700clll")
    @GET("api/v2.1/search")
    Call<ApiResponse> getRestaurantsBySearch(@Query("entity_id") String entity_id, @Query("entity_type") String entity_type, @Query("q") String query);
*/


    @FormUrlEncoded
    @POST(ApiEndPoint.getMessages)
    Call<GetMessagesResponse> getMessages(@Field("token") String token,
                                          @Field("user_id") int user_id,
                                          @Field("socket_id") String socket_id,
                                          @Field("access_token") String access_token,
                                          @Field("conversation_reference_id") String conversation_reference_id
    );

    @FormUrlEncoded
    @POST(ApiEndPoint.getOfflineMessages)
    Call<GetMessagesResponse> getOfflineMessages(@Field("token") String token,
                                          @Field("user_id") int user_id,
                                          @Field("socket_id") String socket_id,
                                          @Field("last_message_id") int last_message_id,
                                          @Field("access_token") String access_token,
                                          @Field("conversation_reference_id") String conversation_reference_id
    );

    @FormUrlEncoded
    @POST(ApiEndPoint.getMessages)
    Call<JsonObject> getMessages1(@Field("token") String token,
                                  @Field("user_id") int user_id,
                                  @Field("socket_id") String socket_id,
                                  @Field("access_token") String access_token
    );


    @FormUrlEncoded
    @POST(ApiEndPoint.getConversation)
    Call<GetMessagesResponse> getConversation(@Field("token") String token,
                                              @Field("user_id") String user_id,
                                              @Field("socket_id") String socket_id,
                                              @Field("access_token") String access_token,
                                              @Field("entity_id") String entity_id,
                                              @Field("limit") String limit,
                                              @Field("last_message_id") String last_message_id,
                                              @Field("is_group") int is_group,
                                              @Field("conversation_reference_id") String conversation_reference_id);

    @FormUrlEncoded
    @POST(ApiEndPoint.API_CONVERSATION_HISTOTY)
    Call<GetMessagesResponse> getChatHistoty(@Field("token") String token,
                                          @Field("conversation_reference_id") String conversation_reference_id,
                                          @Field("limit") String limit

    );


}
