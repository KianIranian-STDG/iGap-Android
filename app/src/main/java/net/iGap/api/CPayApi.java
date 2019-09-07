package net.iGap.api;

import net.iGap.model.cPay.CPayWalletAmountModel;
import net.iGap.model.cPay.ChargeWalletBodyModel;
import net.iGap.model.cPay.ChargeWalletModel;
import net.iGap.model.cPay.PlaqueBodyModel;
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
    Call<PlaqueInfoModel> getPlaqueInfo(@Body PlaqueBodyModel body);

    @POST("account-inventory")
    Call<CPayWalletAmountModel> getCPayWalletAmount(@Body PlaqueBodyModel body);

    @POST("charge")
    Call<ChargeWalletModel> getChargeWallet(@Body ChargeWalletBodyModel body);

}
