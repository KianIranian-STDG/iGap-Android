package net.iGap.api;

import net.iGap.model.cPay.PlaqueInfoBodyModel;
import net.iGap.model.cPay.PlaqueInfoModel;
import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;
import net.iGap.model.cPay.UserPlaquesModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CPayApi {

    @POST("register")
    Call<RegisterPlaqueModel> getRegisterNewPlaque(@Body RegisterPlaqueBodyModel body);

    @GET("user-plates")
    Call<UserPlaquesModel> getUserPlaques();

    @POST("plate-info")
    Call<PlaqueInfoModel> getPlaqueInfo(@Body PlaqueInfoBodyModel body);
}
