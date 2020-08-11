package net.iGap.api;

import net.iGap.model.paymentPackage.MciPurchaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CharityApi {

    @FormUrlEncoded
    @POST("help/{charityId}")
    Call<MciPurchaseResponse> sendRequestGetCharity(@Path("charityId") String charityId,
                                                    @Field("amount") int amount);
}
