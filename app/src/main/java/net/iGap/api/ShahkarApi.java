package net.iGap.api;

import net.iGap.fragments.giftStickers.enterNationalCode.CheckNationalCodeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ShahkarApi {

    @FormUrlEncoded
    @POST("users/check-mobile-number")
    Call<CheckNationalCodeResponse> checkNationalCode(@Field("national_code") String nationalCode,
                                                      @Field("mobile_number") String mobileNumber);

}
