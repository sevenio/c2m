package com.tvisha.click2magic.api.post;
/*
public class ActiveAgentsApi {
}*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tvisha.click2magic.api.ApiClient;
import com.tvisha.click2magic.constants.ApiEndPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ActiveAgentsApi extends ApiClient {


/*    public static interface ApiInterface {
        @FormUrlEncoded
        @POST(ApiEndPoint.API_ACTIVE_AGENTS)
        Call<ActiveAgentsResponse> getActiveAgents(
                @Field("token") String token,
                @Field("company_token") String company_token,
                @Field("user_id") String user_id,
                @Field("site_id") String site_id,
                @Field("account_id") String account_id,
                @Field("role") String role

        );
    }*/

    public static interface ApiInterface {
        @FormUrlEncoded
        @POST(ApiEndPoint.API_AGENTS)
        Call<ActiveAgentsResponse> getActiveAgents(
                @Field("token") String token,
                @Field("company_token") String company_token,
                @Field("user_id") String user_id


        );
    }


    public class ActiveAgentsResponse implements Parcelable {

        @SerializedName("success")
        @Expose
        private boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("data")
        @Expose
        private List<SiteAgentsData> data = null;
        public final Parcelable.Creator<ActiveAgentsResponse> CREATOR = new Creator<ActiveAgentsResponse>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public ActiveAgentsResponse createFromParcel(Parcel in) {
                return new ActiveAgentsResponse(in);
            }

            public ActiveAgentsResponse[] newArray(int size) {
                return (new ActiveAgentsResponse[size]);
            }

        };

        protected ActiveAgentsResponse(Parcel in) {
            this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
            this.message = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(this.data, (SiteData.class.getClassLoader()));
        }

        public ActiveAgentsResponse() {
        }

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

        public List<SiteAgentsData> getData() {
            return data;
        }

        public void setData(List<SiteAgentsData> data) {
            this.data = data;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(success);
            dest.writeValue(message);
            dest.writeList(data);
        }

        public int describeContents() {
            return 0;
        }

    }

    public static ActiveAgentsApi.ApiInterface getApiService() {
        return getClient().create(ActiveAgentsApi.ApiInterface.class);
    }
}