package net.iGap.api;

import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CPayApi {

    @POST("register")
    Call<RegisterPlaqueModel> getRegisterNewPlaque(@Body RegisterPlaqueBodyModel body);

}
