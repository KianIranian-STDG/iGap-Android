package net.iGap.api;

import com.google.gson.JsonObject;

import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.paymentPackage.Config;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.model.paymentPackage.InternetPackageFilter;
import net.iGap.model.paymentPackage.MciInternetPackageFilter;
import net.iGap.model.paymentPackage.MciPurchaseResponse;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChargeApi {

    @FormUrlEncoded
    @POST("{operator}/topup/purchase")
    Single<MciPurchaseResponse> topUpPurchase(@Path("operator") String operator,
                                              @Field("type") String type,
                                              @Field("tel_num") String rechargeableNumber,
                                              @Field("tel_charger") String chargerNumber,
                                              @Field("cost") int cost);

    @POST("{operator}/topup/set-favorite")
    Call<ResponseBody> setFavoriteChargeNumber(@Path("operator") String operator, @Body JsonObject jsonObject);

    @POST("{operator}/internet-package/set-favorite")
    Call<ResponseBody> setFavoriteInternetPackage(@Path("operator") String operator, @Body JsonObject jsonObject);

    @GET("topup/get-favorite")
    Call<GetFavoriteNumber> getFavoriteChargeNUmber();

    @GET("internet-package/get-favorite")
    Call<GetFavoriteNumber> getFavoriteInternetPackage();

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
                                                      @Field("tel_num") String rechargeableNumber,
                                                      @Field("tel_charger") String chargerNumber,
                                                      @Field("type") String internetPackageType);

    @GET("configs")
    Call<Config> getConfigs(@Header("Authorization") String userToken);
}
