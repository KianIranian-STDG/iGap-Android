package net.iGap.kuknos.Repository;

import net.iGap.api.apiService.IgapRetrofitInterceptor;
import net.iGap.kuknos.Model.KuknosPaymentResponse;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosBankPayment;
import net.iGap.kuknos.Model.Parsian.KuknosFederation;
import net.iGap.kuknos.Model.Parsian.KuknosFeeModel;
import net.iGap.kuknos.Model.Parsian.KuknosHash;
import net.iGap.kuknos.Model.Parsian.KuknosOfferResponse;
import net.iGap.kuknos.Model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.Model.Parsian.KuknosOptionStatus;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosTradeResponse;
import net.iGap.kuknos.Model.Parsian.KuknosTransactionResult;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfo;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.KuknosUsernameStatus;

import org.stellar.sdk.responses.SubmitTransactionResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface KuknosApi {

    /**
     * This api create an account in Parsian network.
     *
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param NID
     * @param email
     * @param publicKey
     * @return
     */
    @FormUrlEncoded
    @POST("create-account")
    @Headers({IgapRetrofitInterceptor.CONNECT_TIMEOUT + ":60000",
            IgapRetrofitInterceptor.READ_TIMEOUT + ":60000",
            IgapRetrofitInterceptor.WRITE_TIMEOUT + ":60000"})
    Call<KuknosResponseModel> createAccount(@Field("first_name") String firstName,
                                            @Field("last_name") String lastName,
                                            @Field("federation_name") String federationName,
                                            @Field("phone_number") String phoneNumber,
                                            @Field("national_code") String NID,
                                            @Field("mail") String email,
                                            @Field("public_key") String publicKey);

    /**
     * This api returns all of user's assets
     *
     * @param publicKey
     * @return
     */
    @FormUrlEncoded
    @POST("get-account-assets")
    Call<KuknosResponseModel<KuknosBalance>> getUserAsset(@Field("public_key") String publicKey);

    /**
     * This api returns all of user's info
     *
     * @param publicKey
     * @return
     */
    @FormUrlEncoded
    @POST("get-account")
    Call<KuknosResponseModel<KuknosUserInfoResponse>> getUserInfo(@Field("public_key") String publicKey);

    /**
     * This api returns a user's asset detail
     *
     * @param publicKey
     * @param assetCode
     * @return
     */
    @FormUrlEncoded
    @POST("get-account-assets")
    Call<KuknosResponseModel<KuknosBalance>> getUserAsset(@Field("public_key") String publicKey,
                                                          @Field("asset_code") String assetCode);

    /**
     * This api returns all of assets that are available in the network
     *
     * @return
     */
    @POST("get-assets")
    Call<KuknosResponseModel<KuknosAsset>> getAllAssets();

    /**
     * this api returns the detail of a specific asset in the network
     *
     * @param assetCode
     * @return
     */
    @FormUrlEncoded
    @POST("get-assets")
    Call<KuknosResponseModel<KuknosAsset>> getAllAssets(@Field("asset_code") String assetCode);

    /**
     * this api submit the new currency in your account
     *
     * @param XDR
     * @return
     */
    @FormUrlEncoded
    @POST("change-trust")
    Call<KuknosResponseModel<KuknosTransactionResult>> changeTrust(@Field("xdr") String XDR);

    /**
     * this api handles payment operation to others
     *
     * @param XDR
     * @return
     */
    @FormUrlEncoded
    @POST("transfer")
    Call<KuknosResponseModel<KuknosHash>> payment(@Field("xdr") String XDR);

    /**
     * this api returns all of history of an account
     *
     * @param publicKey
     * @param limit
     * @param order
     * @return
     */
    @FormUrlEncoded
    @POST("wallet-history")
    Call<KuknosResponseModel<KuknosOperationResponse>> getWalletHistory(@Field("public_key") String publicKey,
                                                                        @Field("limit") int limit,
                                                                        @Field("order") String order);

    /**
     * this api returns all of open trades for an account
     *
     * @param publicKey
     * @param limit
     * @param cursor
     * @param order
     * @return
     */
    @FormUrlEncoded
    @POST("open-offers")
    Call<KuknosResponseModel<KuknosOfferResponse>> getOpenOffers(@Field("public_key") String publicKey,
                                                                 @Field("limit") int limit,
                                                                 @Field("cursor") int cursor,
                                                                 @Field("order") String order);

    /**
     * this api returns all of closed trades for an account
     *
     * @param publicKey
     * @param limit
     * @param cursor
     * @param order
     * @return
     */
    @FormUrlEncoded
    @POST("account-trades")
    Call<KuknosResponseModel<KuknosTradeResponse>> getTradesHistory(@Field("public_key") String publicKey,
                                                                    @Field("limit") int limit,
                                                                    @Field("cursor") int cursor,
                                                                    @Field("order") String order);

    /**
     * this api checks for federations and converts username to public key
     *
     * @param username
     * @return
     */
    @FormUrlEncoded
    @POST("get-public-key")
    Call<KuknosResponseModel<KuknosFederation>> convertFederation(@Field("federation_name") String username,
                                                                  @Field("domain") String domain);

    /**
     * this api returns the user status
     *
     * @param publicKey
     * @return
     */
    @FormUrlEncoded
    @POST("account-status")
    Call<KuknosResponseModel<KuknosUserInfo>> accountStatus(@Field("public_key") String publicKey);

    /**
     * this api returns the user status for options: home domain and inflation add
     *
     * @param publicKey
     * @return
     */
    @FormUrlEncoded
    @POST("is-member")
    Call<KuknosResponseModel<KuknosOptionStatus>> accountOptionsStatus(@Field("public_key") String publicKey);

    /**
     * this api sets Home domain and Inflation destination
     *
     * @param XDR
     * @return
     */
    @FormUrlEncoded
    @POST("set-options")
    Call<KuknosResponseModel<SubmitTransactionResponse>> setOption(@Field("xdr") String XDR);

    /**
     * this api creates a buy offer.
     *
     * @param XDR
     * @return
     */
    @FormUrlEncoded
    @POST("manage-offer")
    Call<KuknosResponseModel<SubmitTransactionResponse>> buyOffer(@Field("xdr") String XDR);

    /**
     * this api make a request for payment and charge the account.
     *
     * @param publicKey
     * @param assetCode
     * @param assetAmount
     * @param totalPrice
     * @param description
     * @return
     */
    @FormUrlEncoded
    @POST("payment-request")
    Call<KuknosResponseModel<KuknosBankPayment>> buyAsset(@Field("public_key") String publicKey,
                                                          @Field("asset_code") String assetCode,
                                                          @Field("asset_count") String assetAmount,
                                                          @Field("amount") String totalPrice,
                                                          @Field("description") String description);

    /**
     * this api make a request for username availability.
     *
     * @param federationName
     * @return
     */
    @FormUrlEncoded
    @POST("account-existent")
    Call<KuknosResponseModel<KuknosUsernameStatus>> checkUsername(@Field("federation_name") String federationName);

    @FormUrlEncoded
    @POST("get-payment")
    Call<KuknosResponseModel<KuknosPaymentResponse>> getPaymentData(@Field("rrn") String RRN);

    /**
     * this api make a request for Fees.
     *
     * @return
     */
    @GET("get-fees")
    Call<KuknosResponseModel<KuknosFeeModel>> getFee();

    /*@FormUrlEncoded
    @POST("activate-account")
    Call<KuknosInfoM> activateAccount(@Field("initial_balance") int initialBalance,
                                      @Field("receipt_number") String recNumber,
                                      @Field("public_key") String publicKey,
                                      @Field("fee") int fee,
                                      @Field("description") String description);

    @FormUrlEncoded
    @POST("charge-wallet")
    Call<KuknosSubmitM> chargeWallet(@Field("xdr") String XDR,
                                     @Field("public_key") String publicKey,
                                     @Field("fee") int fee,
                                     @Field("description") String description);*/

}
