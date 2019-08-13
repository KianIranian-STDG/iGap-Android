package net.iGap.api;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.internetpackage.InternetPackage;
import net.iGap.internetpackage.MciInternetPackageFilterResponse;
import net.iGap.model.MciPurchaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MciApi {

    @FormUrlEncoded
    @POST("auth/token")
    Call<Object> getAuth(@Field("refresh_token") String refresh_token);

    @FormUrlEncoded
    @POST("topup/purchase")
    Call<MciPurchaseResponse> topUpPurchase(@Field("tel_num") String phoneNumber,
                                            @Field("cost") int cost);

    @GET("internet-package/sub-filter")
    Call<MciInternetPackageFilterResponse> getInternetPackageFilterList();

    @GET("internet-package/packages")
    Call<BaseIGashtResponse<InternetPackage>> getInternetPackageList();
}
