package net.iGap.api;

import net.iGap.model.MciPurchaseResponse;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.internetPackage.InternetPackage;
import net.iGap.model.internetPackage.InternetPackageFilter;
import net.iGap.model.internetPackage.MciInternetPackageFilter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChargeApi {

    @FormUrlEncoded
    @POST("{operator}/topup/purchase")
    Call<MciPurchaseResponse> topUpPurchase(@Path("operator") String operator,
                                            @Field("type") String type,
                                            @Field("tel_num") String phoneNumber,
                                            @Field("cost") int cost);

    @GET("{operator}/internet-package/categories")
    Call<List<MciInternetPackageFilter>> getInternetPackageFilterList(@Path("operator") String operator);

    @GET("{operator}/internet-package/packages/categorized")
    Call<BaseIGashtResponse<InternetPackage>> getInternetPackageList(@Path("operator") String operator,
                                                                     @Query("type") String filter);

    @GET("internet-package/charge-types")
    Call<BaseIGashtResponse<InternetPackageFilter>> getInternetPackageFilters();

    @FormUrlEncoded
    @POST("{operator}/internet-package/purchase")
    Call<MciPurchaseResponse> internetPackagePurchase(@Path("operator") String operator,
                                                      @Field("tel_num") String phoneNumber,
                                                      @Field("type") String internetPackageType);
}