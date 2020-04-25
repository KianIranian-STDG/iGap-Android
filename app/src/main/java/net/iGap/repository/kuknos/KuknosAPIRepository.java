package net.iGap.repository.kuknos;

import net.iGap.api.KuknosApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.kuknos.KuknosPaymentResponse;
import net.iGap.model.kuknos.KuknosSendM;
import net.iGap.model.kuknos.KuknosSignupM;
import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.model.kuknos.Parsian.KuknosBankPayment;
import net.iGap.model.kuknos.Parsian.KuknosFederation;
import net.iGap.model.kuknos.Parsian.KuknosFeeModel;
import net.iGap.model.kuknos.Parsian.KuknosHash;
import net.iGap.model.kuknos.Parsian.KuknosOfferResponse;
import net.iGap.model.kuknos.Parsian.KuknosOperationResponse;
import net.iGap.model.kuknos.Parsian.KuknosOptionStatus;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosTradeResponse;
import net.iGap.model.kuknos.Parsian.KuknosTransactionResult;
import net.iGap.model.kuknos.Parsian.KuknosUserInfo;
import net.iGap.model.kuknos.Parsian.KuknosUsernameStatus;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import org.stellar.sdk.responses.SubmitTransactionResponse;

class KuknosAPIRepository {
    private KuknosApi apiService = new RetrofitFactory().getKuknosRetrofit();

    void registerUser(KuknosSignupM info, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel> apiResponse) {
        new ApiInitializer<KuknosResponseModel>()
                .initAPI(apiService.createAccount(info.getName(), "IGap", info.getUsername(), info.getPhoneNum(),
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
                .execute(model.getSrc(), model.getDest(), model.getAssetCode(), model.getAssetInssuer(), model.getAmount(), model.getMemo());
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

    void changeTrust(String accountSeed, String code, String issuer, String trustLineLimitByIssuer, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.CHANGE_TRUST, XDR -> new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.changeTrust(XDR), handShakeCallback, apiResponse))
                .execute(accountSeed, code, issuer, trustLineLimitByIssuer);
        /*new ApiInitializer<KuknosResponseModel<KuknosTransactionResult>>()
                .initAPI(apiService.changeTrust(new KuknosSDKRepo().trustlineXDR(accountSeed, code, issuer))
                        , handShakeCallback, apiResponse);*/
    }

    void setOptions(String accountSeed, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.SET_OPTION, XDR -> new ApiInitializer<KuknosResponseModel<SubmitTransactionResponse>>()
                .initAPI(apiService.setOption(XDR), handShakeCallback, apiResponse))
                .execute(accountSeed);
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
                     String counterCode, String counterIssuer, String amount, String price, String offerID,
                     HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>> apiResponse) {
        new KuknosSDKRepo(KuknosSDKRepo.API.MANAGE_OFFER, XDR -> new ApiInitializer<KuknosResponseModel<SubmitTransactionResponse>>()
                .initAPI(apiService.buyOffer(XDR), handShakeCallback, apiResponse))
                .execute(accountSeed, sourceCode, sourceIssuer, counterCode, counterIssuer, amount, price, offerID);
        /*new ApiInitializer<KuknosResponseModel<SubmitTransactionResponse>>()
                .initAPI(apiService.buyOffer(new KuknosSDKRepo().manageOffer(accountSeed, sourceCode, sourceIssuer,
                        counterCode, counterIssuer, amount, price))
                        , handShakeCallback, apiResponse);*/
    }

    void buyAsset(String publicKey, String assetCode, String assetAmount, String totalPrice, String description, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBankPayment>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosBankPayment>>().initAPI(apiService.buyAsset(publicKey, assetCode, assetAmount, totalPrice, description), handShakeCallback, apiResponse);
    }

    void getAccountOptionsStatus(String publicKey, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOptionStatus>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosOptionStatus>>().initAPI(apiService.accountOptionsStatus(publicKey), handShakeCallback, apiResponse);
    }

    void checkUsername(String username, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUsernameStatus>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosUsernameStatus>>().initAPI(apiService.checkUsername(username), handShakeCallback, apiResponse);
    }

    void getOpenOffers(String userID, int cursor, int limit, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOfferResponse>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosOfferResponse>>().initAPI(apiService.getOpenOffers(userID, limit, cursor, null), handShakeCallback, apiResponse);
    }

    void getTradesHistory(String userID, int cursor, int limit, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTradeResponse>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosTradeResponse>>().initAPI(apiService.getTradesHistory(userID, limit, cursor, null), handShakeCallback, apiResponse);
    }

    void convertFederation(String username, String domain, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFederation>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosFederation>>().initAPI(apiService.convertFederation(username, domain), handShakeCallback, apiResponse);
    }

    void getFees(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFeeModel>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosFeeModel>>().initAPI(apiService.getFee(), handShakeCallback, apiResponse);
    }

    void getPaymentData(String RRA, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosPaymentResponse>> apiResponse) {
        new ApiInitializer<KuknosResponseModel<KuknosPaymentResponse>>().initAPI(apiService.getPaymentData(RRA), handShakeCallback, apiResponse);
    }
}
