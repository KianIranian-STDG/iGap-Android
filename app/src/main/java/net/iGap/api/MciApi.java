package net.iGap.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MciApi {

    @FormUrlEncoded
    @POST("auth/token")
    Call<Object> getAuth(@Field("refresh_token") String refresh_token);
}
