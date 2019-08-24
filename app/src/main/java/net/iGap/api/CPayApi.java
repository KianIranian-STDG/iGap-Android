package net.iGap.api;

import retrofit2.Call;
import retrofit2.http.POST;

public interface CPayApi {

    @POST("register")
    Call<String> getRegisterNewPlaque();

}
