package net.iGap.mobileBank.repository;

import net.iGap.api.MobileBankApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.MobileBankApiInitializer;
import net.iGap.api.apiService.MobileBankExpiredTokenCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoginResponse;

import java.util.List;

public class MobileBankRepository {

    private MobileBankApi bankApi = new RetrofitFactory().getMobileBankRetrofit();
    private String accessToken;

    public void mobileBankLogin(String username, String password, HandShakeCallback callback, ResponseCallback<BaseMobileBankResponse<LoginResponse>> responseCallback) {
        new ApiInitializer<BaseMobileBankResponse<LoginResponse>>().initAPI(bankApi.mobileBankLogin(username, password), callback, responseCallback);
    }

    public void getMobileBankCards(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankCardModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankCardModel>>>().initAPI(bankApi.getUserCards(null, null, null, null), callback, responseCallback);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
