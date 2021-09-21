package net.iGap.api.apiService;

import com.google.gson.JsonObject;

import net.iGap.model.qrCodePayment.MerchantInfo;
import net.iGap.model.qrCodePayment.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PecQRApi {

    @GET("merchants/{qrCode}")
    Call<MerchantInfo> getMerchantInfo(@Path("qrCode") String qrCode);

    @POST("merchants/money-transfer")
    Call<Token> getPaymentToken(@Body JsonObject jsonObject);
}
