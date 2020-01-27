package net.iGap.kuknos.service.Repository;

import net.iGap.api.KuknosApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.Parsian.IgapPayment;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosFeeModel;
import net.iGap.kuknos.service.model.Parsian.KuknosHash;
import net.iGap.kuknos.service.model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosTransactionResult;
import net.iGap.kuknos.service.model.Parsian.KuknosUserInfo;

import org.stellar.sdk.responses.SubmitTransactionResponse;

class KuknosAPIRepository {
    private KuknosApi apiService = new RetrofitFactory().getKuknosRetrofit();

    void registerUser(KuknosSignupM info, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel> apiResponse) {
        new ApiInitializer<KuknosResponseModel>()
                .initAPI(apiService.createAccount(info.getName(), "IGap", info.getPhoneNum(),
                        info.getNID(), info.getEmail(), info.getKeyString()),
                        handShakeCallback, apiResponse);
    }

    void getUserStatus(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfo>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosUserInfo>>().initAPI(apiService.accountStatus(userID), handShakeCallback, apiResponse);
    }

    void getUserAccount(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBalance>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosBalance>>().initAPI(apiService.getUserAsset(userID), handShakeCallback, apiResponse);
    }

    void getAssetData(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosAsset>>().initAPI(apiService.getAllAssets(assetCode), handShakeCallback, apiResponse);
    }

    /*void getUserAccount(String userID, ApiResponse<AccountResponse> apiResponse) {
        KuknosAPIAsync<AccountResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.USER_ACCOUNT);
        temp.execute(userID);
    }*/

    void paymentUser(KuknosSendM model, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosHash>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.PAYMENT_SEND, XDR -> new ApiInitializer<KuknosResponseModel<KuknosHash>>()
                .initAPI(apiService.payment(XDR), handShakeCallback, apiResponse))
                .execute(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo());
        /*new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.payment(new KuknosSDKRepo().paymentToOtherXDR(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo()))
                        , handShakeCallback, apiResponse);*/
    }

    /*void paymentUser(KuknosSendM model, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENT_SEND);
        temp.execute(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo());
    }*/

    void getUserHistory(String userID, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOperationResponse>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosOperationResponse>>().initAPI(apiService.getWalletHistory(userID, 100, "desc"), handShakeCallback, apiResponse);
    }

    /*void getUserHistory(String userID, ApiResponse<Page<OperationResponse>> apiResponse) {
        KuknosAPIAsync<Page<OperationResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENTS_ACCOUNT);
        temp.execute(userID);
    }*/

    void getAssets(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosAsset>>().initAPI(apiService.getAllAssets(), handShakeCallback, apiResponse);
    }

    void getSpecificAssets(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosAsset>>().initAPI(apiService.getAllAssets(assetCode), handShakeCallback, apiResponse);
    }

    /*void getAssets(ApiResponse<Page<AssetResponse>> apiResponse) {
        KuknosAPIAsync<Page<AssetResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.ASSETS);
        temp.execute();
    }*/

    void changeTrust(String accountSeed, String code, String issuer, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.CHANGE_TRUST, XDR -> new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.changeTrust(XDR), handShakeCallback, apiResponse))
                .execute(accountSeed, code, issuer);
        /*new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.changeTrust(new KuknosSDKRepo().trustlineXDR(accountSeed, code, issuer))
                        , handShakeCallback, apiResponse);*/
    }

    /*void changeTrust(String accountSeed, String code, String issuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.CHANGE_TRUST);
        temp.execute(accountSeed, code, issuer);
    }*/

    /*void getOffersList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.OFFERS_LIST);
        temp.execute(userID);
    }*/

    /*void getTradesList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.TRADES_LIST);
        temp.execute(userID);
    }*/

    /*void manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                     String counterCode, String counterIssuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.MANAGE_OFFER);
        temp.execute(accountSeed, sourceCode, sourceIssuer, counterCode, counterIssuer);
    }*/

    void manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                     String counterCode, String counterIssuer, String amount, String price,
                     HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.MANAGE_OFFER, XDR -> new ApiInitializer<KuknosResponseModel<SubmitTransactionResponse>>()
                .initAPI(apiService.buyOffer(XDR), handShakeCallback, apiResponse))
                .execute(accountSeed, sourceCode, sourceIssuer, counterCode, counterIssuer, amount, price);
        /*new ApiInitializer<KuknosResponseModel<SubmitTransactionResponse>>()
                .initAPI(apiService.buyOffer(new KuknosSDKRepo().manageOffer(accountSeed, sourceCode, sourceIssuer,
                        counterCode, counterIssuer, amount, price))
                        , handShakeCallback, apiResponse);*/
    }

    void buyAsset(String publicKey, String assetCode, String assetAmount, String totalPrice, String description, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<IgapPayment>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<IgapPayment>>().initAPI(apiService.buyAsset(publicKey, assetCode, assetAmount, totalPrice, description), handShakeCallback, apiResponse);
    }

    void getFees(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFeeModel>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosFeeModel>>().initAPI(apiService.getFee(), handShakeCallback, apiResponse);
    }
}
