package net.iGap.kuknos.service.Repository;

import net.iGap.api.KuknosApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosTransactionResult;
import net.iGap.kuknos.service.model.Parsian.KuknosUserInfo;

import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.OfferResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

public class KuknosAPIRepository {
    private KuknosApi apiService = ApiServiceProvider.getKuknosClient();

    public void registerUser(KuknosSignupM info, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel> apiResponse) {
        new ApiInitializer<KuknosResponseModel>()
                .initAPI(apiService.createAccount(info.getName(), "IGap", info.getPhoneNum(),
                        info.getNID(), info.getEmail(), info.getKeyString()),
                        handShakeCallback, apiResponse);
    }

    public void getUserStatus(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfo>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosUserInfo>>().initAPI(apiService.accountStatus(userID), handShakeCallback, apiResponse);
    }

    public void getUserAccount(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBalance>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosBalance>>().initAPI(apiService.getUserAsset(userID), handShakeCallback, apiResponse);
    }

    public void getUserAccount(String userID, ApiResponse<AccountResponse> apiResponse) {
        KuknosAPIAsync<AccountResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.USER_ACCOUNT);
        temp.execute(userID);
    }

    public void paymentUser(KuknosSendM model, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.payment(new KuknosSDKRepo().paymentToOtherXDR(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo()))
                        , handShakeCallback, apiResponse);
    }

    public void paymentUser(KuknosSendM model, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENT_SEND);
        temp.execute(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo());
    }

    public void getUserHistory(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOperationResponse>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosOperationResponse>>().initAPI(apiService.getWalletHistory(userID, 100, "desc"), handShakeCallback, apiResponse);
    }

    public void getUserHistory(String userID, ApiResponse<Page<OperationResponse>> apiResponse) {
        KuknosAPIAsync<Page<OperationResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENTS_ACCOUNT);
        temp.execute(userID);
    }

    public void getAssets(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosAsset>>().initAPI(apiService.getAllAssets(), handShakeCallback, apiResponse);
    }

    public void getSpecificAssets(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosAsset>>().initAPI(apiService.getAllAssets(assetCode), handShakeCallback, apiResponse);
    }

    public void getAssets(ApiResponse<Page<AssetResponse>> apiResponse) {
        KuknosAPIAsync<Page<AssetResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.ASSETS);
        temp.execute();
    }

    public void changeTrust(String accountSeed, String code, String issuer, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.changeTrust(new KuknosSDKRepo().TrustlineXDR(accountSeed, code, issuer))
                        , handShakeCallback, apiResponse);
    }

    public void changeTrust(String accountSeed, String code, String issuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.CHANGE_TRUST);
        temp.execute(accountSeed, code, issuer);
    }

    public void getOffersList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.OFFERS_LIST);
        temp.execute(userID);
    }

    public void getTradesList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.TRADES_LIST);
        temp.execute(userID);
    }

    public void manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                            String counterCode, String counterIssuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.MANAGE_OFFER);
        temp.execute(accountSeed, sourceCode, sourceIssuer, counterCode, counterIssuer);
    }
}
