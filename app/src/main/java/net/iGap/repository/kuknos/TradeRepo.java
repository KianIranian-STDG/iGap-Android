package net.iGap.repository.kuknos;

import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosOfferResponse;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosTradeResponse;
import net.iGap.model.kuknos.Parsian.KuknosTransactionResult;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import org.stellar.sdk.responses.SubmitTransactionResponse;

public class TradeRepo {

    private UserRepo userRepo = new UserRepo();
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public TradeRepo() {
    }

    public void getAssets(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        kuknosAPIRepository.getAssets(handShakeCallback, apiResponse);
    }

    public void changeTrustline(String code, String issuer, String trustLineLimitByIssuer, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        kuknosAPIRepository.changeTrust(userRepo.getSeedKey(), code, issuer, trustLineLimitByIssuer, handShakeCallback, apiResponse);
    }

    /*public void getOffersList(ApiResponse<Page<OfferResponse>> apiResponse) {
        kuknosAPIRepository.getOffersList(userRepo.getAccountID(), apiResponse);
    }

    public void getTradesList(ApiResponse<Page<OfferResponse>> apiResponse) {
        kuknosAPIRepository.getTradesList(userRepo.getAccountID(), apiResponse);
    }

    public void manangeOffer(String sourceCode, String sourceIssuer,
                             String counterCode, String counterIssuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        kuknosAPIRepository.manageOffer(userRepo.getSeedKey(), sourceCode, sourceIssuer, counterCode, counterIssuer, apiResponse);
    }*/

    public void manangeOffer(String sourceCode, String sourceIssuer, String counterCode,
                             String counterIssuer, String amount, String price, String offerID,
                             HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>> apiResponse) {
        kuknosAPIRepository.manageOffer(userRepo.getSeedKey(), sourceCode, sourceIssuer, counterCode,
                counterIssuer, amount, price, offerID, handShakeCallback, apiResponse);
    }

    public void getOpenOffers(int cursor, int limit, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOfferResponse>> apiResponse) {
        kuknosAPIRepository.getOpenOffers(userRepo.getAccountID(), cursor, limit, handShakeCallback, apiResponse);
    }

    public void getTradesHistory(int cursor, int limit, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTradeResponse>> apiResponse) {
        kuknosAPIRepository.getTradesHistory(userRepo.getAccountID(), cursor, limit, handShakeCallback, apiResponse);
    }
}
