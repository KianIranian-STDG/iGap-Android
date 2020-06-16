package net.iGap.repository;

import net.iGap.api.MobileBankApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.MobileBankApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperLog;
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
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.MobileBankExpiredTokenCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmUserInfo;

import java.util.List;

public class MobileBankRepository {

    private static MobileBankRepository instance ;
    private MobileBankApi bankApi = new RetrofitFactory().getMobileBankRetrofit();
    private String accessToken;
    private static String TOKEN_PREFIX = "Bearer  ";
    RealmUserInfo userInfo;

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

    public void getChequeBookList(String deposit, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankChequeBookListModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankChequeBookListModel>>>().initAPI(bankApi.getChequesBookList(getAccessToken(), deposit), callback, responseCallback);
    }

    public void getChequeList(String depositNumber, String chequeBookNumber, Integer length, Integer offset, String chequeNumber, String status, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankChequeSingle>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankChequeSingle>>>().initAPI(bankApi.getChequesList(getAccessToken(), depositNumber, chequeBookNumber, length, offset, chequeNumber, status), callback, responseCallback);
    }

    public void getLoanList(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<LoanListModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<LoanListModel>>>().initAPI(bankApi.getLoansList(getAccessToken()), callback, responseCallback);
    }

    public void getMobileBankAccounts(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankAccountModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankAccountModel>>>().initAPI(bankApi.getUserDeposits(getAccessToken(), null), callback, responseCallback);
    }

    public void getCardDeposits(String pan, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankCardDepositsModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankCardDepositsModel>>>().initAPI(bankApi.getCardsDeposits(getAccessToken(), pan), callback, responseCallback);
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

    public void getLoanDetail(String loanNumber, Integer offset, Integer length, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankServiceLoanDetailModel>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankServiceLoanDetailModel>>().initAPI(bankApi.getLoanDetail(getAccessToken(), loanNumber, true, offset, length), callback, responseCallback);
    }

    public void getPayLoan(String loanNumber , String customDeposit , String amount , String secondPass , String paymentMethod , boolean isSecPassNecessary, MobileBankExpiredTokenCallback callback , ResponseCallback<BaseMobileBankResponse<BankPayLoanModel>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankPayLoanModel>>().initAPI(bankApi.getPayLoan(getAccessToken(), loanNumber,customDeposit , paymentMethod , amount , secondPass , isSecPassNecessary ), callback, responseCallback);
    }

    public void hotCard(String cardNumber, String reason, String auth, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.hotCard(getAccessToken(), cardNumber, reason, auth), callback, responseCallback);
    }

    public void blockCheque(List<String> chequeNumbers, String depositNumber, String reason, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankBlockCheque>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankBlockCheque>>().initAPI(bankApi.blockCheque(getAccessToken(), chequeNumbers, depositNumber, reason), callback, responseCallback);
    }

    public void getRegisterCheque(String chequeNumber, String depositNumber, Long amount, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.registerCheque(getAccessToken(), depositNumber, chequeNumber, amount), callback, responseCallback);
    }

    public void getTakeTurn(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.getTakeTurn(getAccessToken()), callback, responseCallback);
    }

    public void changeNotifStatus(boolean activate, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse> responseCallback) {
        DbManager.getInstance().doRealmTask((DbManager.RealmTask) realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                String token = realmUserInfo.getPushNotificationToken();
                if (token != null && token.length() > 0) {
                    if (activate)
                        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.activateNotification(getAccessToken(), token), callback, responseCallback);
                    else
                        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.deactivateNotification(getAccessToken(), token), callback, responseCallback);
                } else {
                    HelperLog.getInstance().setErrorLog(new Exception("FCM Token is Empty!" + token));
                }
            }
        });
    }

    public void getNotifStatus(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankNotificationStatus>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankNotificationStatus>>().initAPI(bankApi.getNotificationStatus(getAccessToken()), callback, responseCallback);
    }

    public void getOTP(String cardNumber, MobileBankExpiredTokenCallback callback, ResponseCallback<ErrorModel> responseCallback) {
        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
            String phone = userInfo.getUserInfo().getPhoneNumber();
            if (phone.startsWith("98")) {
                phone = phone.replaceFirst("98", "0");
            }
            new MobileBankApiInitializer<ErrorModel>().initAPI(new RetrofitFactory().getMobileBankOTPRetrofit().getOTP(cardNumber, phone), callback, responseCallback);
        });
    }

    public String getAccessToken() {
        return TOKEN_PREFIX + accessToken;
    }

    public void setAccessToken(String token) {
        accessToken = token;
    }
}
