package net.iGap.api;

import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.mobileBank.BankAccountModel;
import net.iGap.model.mobileBank.BankBlockCheque;
import net.iGap.model.mobileBank.BankCardBalance;
import net.iGap.model.mobileBank.BankCardDepositsModel;
import net.iGap.model.mobileBank.BankCardModel;
import net.iGap.model.mobileBank.BankChequeBookListModel;
import net.iGap.model.mobileBank.BankChequeSingle;
import net.iGap.model.mobileBank.BankHistoryModel;
import net.iGap.model.mobileBank.BankNotificationStatus;
import net.iGap.model.mobileBank.BankPayLoanModel;
import net.iGap.model.mobileBank.BankServiceLoanDetailModel;
import net.iGap.model.mobileBank.BankShebaModel;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.model.mobileBank.LoanListModel;
import net.iGap.model.mobileBank.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @POST("card/get-deposits")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<List<BankCardDepositsModel>>> getCardsDeposits(@Header("Authorization") String token,
                                                                             @Field("pan") String cardNumber);

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

    @POST("loan/pay-loan")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<BankPayLoanModel>> getPayLoan(@Header("Authorization") String token,
                                                              @Field("loan_number") String loanNumber,
                                                              @Field("custom_deposit_number") String customDeposit,
                                                              @Field("payment_method") String paymentMethod,
                                                              @Field("amount") String amount,
                                                              @Field("second_password") String secondPassword,
                                                              @Field("second_password_necessity") Boolean secondaryPassNecessary);

    @POST("cheque/block-cheque")
    @FormUrlEncoded
    Call<BaseMobileBankResponse<BankBlockCheque>> blockCheque(@Header("Authorization") String token,
                                                              @Field("cheque_numbers") List<String> chequeNumbers,
                                                              @Field("deposit_number") String depositNumber,
                                                              @Field("blocked_reason") String reason);

    @POST("cheque/register-cheque")
    @FormUrlEncoded
    Call<BaseMobileBankResponse> registerCheque(@Header("Authorization") String token,
                                                @Field("deposit_number") String depositNumber,
                                                @Field("number") String number,
                                                @Field("amount") Long amount);

    @POST("book-turn")
    Call<BaseMobileBankResponse> getTakeTurn(@Header("Authorization") String token);

    @POST("card/hot-card")
    @FormUrlEncoded
    Call<BaseMobileBankResponse> hotCard(@Header("Authorization") String token,
                                         @Field("pan") String cardNumber,
                                         @Field("reason") String reason,
                                         @Field("auth_info") String auth);

    @POST("parsian/otp")
    @FormUrlEncoded
    Call<ErrorModel> getOTP(@Field("cardNo") String cardNumber,
                            @Field("mobile_number") String mobileNumber);

    @POST("notif/activate")
    @FormUrlEncoded
    Call<BaseMobileBankResponse> activateNotification(@Header("Authorization") String token,
                                                      @Field("token") String FcmToken);

    @POST("notif/deactivate")
    @FormUrlEncoded
    Call<BaseMobileBankResponse> deactivateNotification(@Header("Authorization") String token,
                                                        @Field("token") String FcmToken);

    @GET("notif/get-status")
    Call<BaseMobileBankResponse<BankNotificationStatus>> getNotificationStatus(@Header("Authorization") String token);
}
