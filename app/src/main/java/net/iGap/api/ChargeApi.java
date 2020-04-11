package net.iGap.api;

import net.iGap.model.MciPurchaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChargeApi {

    @FormUrlEncoded
    @POST("{operator}/topup/purchase")
    Call<MciPurchaseResponse> topUpPurchase(@Path("operator") String operator,
                                            @Field("type") String type,
                                            @Field("tel_num") String phoneNumber,
                                            @Field("cost") int cost);

}
