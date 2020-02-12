package net.iGap.api;

import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.internetPackage.InternetPackage;
import net.iGap.model.internetPackage.MciInternetPackageFilter;
import net.iGap.model.MciPurchaseResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MciApi {

    @FormUrlEncoded
    @POST("topup/purchase")
    Call<MciPurchaseResponse> topUpPurchase(@Field("tel_num") String phoneNumber,
                                            @Field("cost") int cost);

    @GET("internet-package/categories")
    Call<List<MciInternetPackageFilter>> getInternetPackageFilterList();

    @GET("internet-package/packages/categorized")
    Call<BaseIGashtResponse<InternetPackage>> getInternetPackageList();

    @FormUrlEncoded
    @POST("internet-package/purchase")
    Call<MciPurchaseResponse> internetPackagePurchase(@Field("tel_num") String phoneNumber,
                                                      @Field("type") String internetPackageType);
}
