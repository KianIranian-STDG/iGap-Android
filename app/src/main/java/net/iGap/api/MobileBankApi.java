package net.iGap.api;

import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MobileBankApi {

    @POST("auth/login")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<LoginResponse>> mobileBankLogin(@Field("authentication") String auth);

    @POST("card/get-cards")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankCardModel>>> getUserCards(@Header ("Authorization") String token ,
                                                                   @Field("card_status") String cardStatus,
                                                                   @Field("length") Integer length,
                                                                   @Field("offset") Integer offset,
                                                                   @Field("pan") String pan);

    @POST("deposit/get-deposits")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankAccountModel>>> getUserDeposits(@Header ("Authorization") String token ,
                                                                      @Field("deposit_numbers") String depositNumber);

}
