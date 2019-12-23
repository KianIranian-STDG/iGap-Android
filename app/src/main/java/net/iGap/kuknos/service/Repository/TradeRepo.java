package net.iGap.kuknos.service.Repository;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import org.stellar.sdk.responses.OfferResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;

public class TradeRepo {

    private UserRepo userRepo = new UserRepo();
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public TradeRepo() {
    }

    public void getAssets(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        kuknosAPIRepository.getAssets(handShakeCallback, apiResponse);
    }

    public void changeTrustline(String code, String issuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        kuknosAPIRepository.changeTrust(userRepo.getSeedKey(), code, issuer, apiResponse);
    }

    public void getOffersList(ApiResponse<Page<OfferResponse>> apiResponse) {
        kuknosAPIRepository.getOffersList(userRepo.getAccountID(), apiResponse);
    }

    public void getTradesList(ApiResponse<Page<OfferResponse>> apiResponse) {
        kuknosAPIRepository.getTradesList(userRepo.getAccountID(), apiResponse);
    }

    public void manangeOffer(String accountSeed, String sourceCode, String sourceIssuer,
                             String counterCode, String counterIssuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        kuknosAPIRepository.manageOffer(userRepo.getSeedKey(), sourceCode, sourceIssuer, counterCode, counterIssuer, apiResponse);
    }
}
