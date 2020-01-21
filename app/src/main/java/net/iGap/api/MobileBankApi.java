package net.iGap.api;

import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.ChequeModel;
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
    Call<BaseMobileBankResponse<List<BankAccountModel>>> getUserDeposits(@Header("Authorization") String token,
                                                                         @Field("deposit_numbers") String depositNumber);

    @POST("card/convert-card-to-iban")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<String>>> getShebaNumber(@Header("Authorization") String token,
                                                              @Field("pan") String cardNumber);

    @POST("deposit/convert-deposit-to-iban")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<BankShebaModel>> getShebaNumberByDeposit(@Header("Authorization") String token,
                                                                         @Field("deposit_number") String depositNumber);

    @POST("cheque/get-cheque-book-list")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<ChequeModel>>> getCheques(@Header("Authorization") String token,
                                                               @Field("deposit_number") String depositNumber);

    @POST("deposit/get-statements")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankHistoryModel>>> getAccountHistory(@Header("Authorization") String token,
                                                                           @Field("deposit_number") String depositNumber,
                                                                           @Field("length") Integer length,
                                                                           @Field("offset") Integer offset,
                                                                           @Field("fromDate") String startDate,
                                                                           @Field("toDate") String endDate);
}
