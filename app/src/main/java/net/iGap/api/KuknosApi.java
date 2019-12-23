package net.iGap.api;

import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosSubmitM;
import net.iGap.kuknos.service.model.KuknoscheckUserM;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KuknosApi {

    @FormUrlEncoded
    @POST("create-account")
    Call<KuknosResponseModel> createAccount(@Field("first_name") String firstName,
                                            @Field("last_name") String lastName,
                                            @Field("phone_number") String phoneNumber,
                                            @Field("national_code") String NID,
                                            @Field("mail") String email,
                                            @Field("public_key") String publicKey);

    @FormUrlEncoded
    @POST("get-account-assets")
    Call<KuknosResponseModel<KuknosBalance>> getUserAsset(@Field("public_key") String publicKey);

    @FormUrlEncoded
    @POST("activate-account")
    Call<KuknosInfoM> activateAccount(@Field("initial_balance") int initialBalance,
                                      @Field("receipt_number") String recNumber,
                                      @Field("public_key") String publicKey,
                                      @Field("fee") int fee,
                                      @Field("description") String description);

    @FormUrlEncoded
    @POST("account-status")
    Call<KuknoscheckUserM> accountStatus(@Field("public_key") String publicKey);

    @FormUrlEncoded
    @POST("charge-wallet")
    Call<KuknosSubmitM> chargeWallet(@Field("xdr") String XDR,
                                     @Field("public_key") String publicKey,
                                     @Field("fee") int fee,
                                     @Field("description") String description);

    @FormUrlEncoded
    @POST("charge-wallet")
    Call<KuknosSubmitM> walletHistory(@Field("public_key") String publicKey,
                                     @Field("limit") String limit,
                                     @Field("order") int order);

}
