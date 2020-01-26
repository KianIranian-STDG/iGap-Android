package net.iGap.api;

import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardBalance;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankChequeBookListModel;
import net.iGap.mobileBank.repository.model.BankChequeSingle;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BankServiceLoanDetailModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoanListModel;
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

    @POST("cheque/get-cheque")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankChequeSingle>>> getChequesList(@Header("Authorization") String token,
                                                                        @Field("deposit_number") String depositNumber,
                                                                        @Field("cheque_book_number") String chequeBookNumber,
                                                                        @Field("length") Integer length,
                                                                        @Field("offset") Integer offset,
                                                                        @Field("cheque_number") String chequeNumber,
                                                                        @Field("statuses") String status);

    @POST("cheque/get-cheque-book-list")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankChequeBookListModel>>> getChequesBookList(@Header("Authorization") String token,
                                                                                   @Field("deposit_number") String depositNumber);

    @POST("deposit/get-statements")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankHistoryModel>>> getAccountHistory(@Header("Authorization") String token,
                                                                           @Field("deposit_number") String depositNumber,
                                                                           @Field("length") Integer length,
                                                                           @Field("offset") Integer offset,
                                                                           @Field("fromDate") String startDate,
                                                                           @Field("toDate") String endDate);

    @POST("card/get-card-balance")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<BankCardBalance>> getCardBalance(@Header("Authorization") String token,
                                                                 @Field("pan") String cardNumber,
                                                                 @Field("auth_info") String cardData,
                                                                 @Field("deposit_number") String depositNumber);

    @POST("loan/get-loans")
    Call<BaseMobileBankResponse<List<LoanListModel>>> getLoansList(@Header("Authorization") String token
                                                                   /*@Field("branch_code") String branchCode,
                                                                   @Field("cb_loan_number") String loanNumber,
                                                                   @Field("currency") String currency*/);

    @POST("loan/get-loan-detail")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<BankServiceLoanDetailModel>> getLoanDetail(@Header("Authorization") String token,
                                                                           @Field("loan_number") String loanNumber,
                                                                           @Field("has_detail") Boolean hasDetail,
                                                                           @Field("offset") Integer offset,
                                                                           @Field("length") Integer length);

    @POST("parsian/otp")
    @FormUrlEncoded
    Call<ErrorModel> getOTP(@Field("cardNo") Long cardNumber,
                            @Field("mobile_number") String mobileNumber);

}
