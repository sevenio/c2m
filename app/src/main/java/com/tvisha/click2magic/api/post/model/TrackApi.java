package com.tvisha.click2magic.api.post.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class TrackApi extends ApiClient {

    public static interface ApiInterface{
        @FormUrlEncoded
        @POST(ApiEndPoint.API_GET_TRACK)
        //user_id,token,company_token,track_code,site_id,chat_date
        Call<TrackResponse> getTrack(
                @Field("token") String token,
                @Field("user_id") String user_id,
                @Field("company_token") String companyToken,
                @Field("track_code") String track_code,
                @Field("site_id") String site_id,
                @Field("chat_date") String chat_date


        );
    }

    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }
}
