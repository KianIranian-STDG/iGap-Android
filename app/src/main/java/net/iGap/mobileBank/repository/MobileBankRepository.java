package net.iGap.mobileBank.repository;

import net.iGap.api.MobileBankApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.MobileBankApiInitializer;
import net.iGap.api.apiService.MobileBankExpiredTokenCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardBalance;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.ChequeModel;
import net.iGap.mobileBank.repository.model.LoginResponse;

import java.util.List;

public class MobileBankRepository {

    private static MobileBankRepository instance ;
    private MobileBankApi bankApi = new RetrofitFactory().getMobileBankRetrofit();
    private String accessToken;
    private static String TOKEN_PREFIX = "Bearer  ";

    private MobileBankRepository() {
        //use instance
    }

    public static MobileBankRepository getInstance(){
        if (instance == null) instance = new MobileBankRepository();
        return instance;
    }

    public void mobileBankLogin(String authentication, HandShakeCallback callback, ResponseCallback<BaseMobileBankResponse<LoginResponse>> responseCallback) {
        new ApiInitializer<BaseMobileBankResponse<LoginResponse>>().initAPI(new RetrofitFactory().getMobileBankLoginRetrofit().mobileBankLogin(authentication), callback, responseCallback);
    }

    public void getMobileBankCards(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankCardModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankCardModel>>>().initAPI(bankApi.getUserCards(getAccessToken() ,null, null, null, null), callback, responseCallback);
    }

    public void getChequeList(String deposit, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<ChequeModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<ChequeModel>>>().initAPI(bankApi.getCheques(getAccessToken(), deposit), callback, responseCallback);
    }

    public void getMobileBankAccounts(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankAccountModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankAccountModel>>>().initAPI(bankApi.getUserDeposits(getAccessToken(), null), callback, responseCallback);
    }

    public void getShebaNumber(String pan, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<String>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<String>>>().initAPI(bankApi.getShebaNumber(getAccessToken(), pan), callback, responseCallback);
    }

    public void getShebaNumberByDeposit(String deposit, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankShebaModel>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankShebaModel>>().initAPI(bankApi.getShebaNumberByDeposit(getAccessToken(), deposit), callback, responseCallback);
    }

    public void getAccountHistory(String depositNumber, Integer offset, String startDate, String endDate, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankHistoryModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankHistoryModel>>>().initAPI(bankApi.getAccountHistory(getAccessToken(), depositNumber, 30, offset, startDate, endDate), callback, responseCallback);
    }

    public void getCardBalance(String cardNumber, String cardData, String depositNumber, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankCardBalance>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankCardBalance>>().initAPI(bankApi.getCardBalance(getAccessToken(), cardNumber, cardData, depositNumber), callback, responseCallback);
    }

    public String getAccessToken() {
        return TOKEN_PREFIX + accessToken;
    }

    public void setAccessToken(String token) {
        accessToken = token;
    }
}
