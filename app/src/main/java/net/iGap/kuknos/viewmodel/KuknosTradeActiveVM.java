package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.Repository.TradeRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosOfferResponse;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import org.stellar.sdk.responses.SubmitTransactionResponse;

public class KuknosTradeActiveVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosOfferResponse> offerList;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;
    private TradeRepo tradeRepo = new TradeRepo();

    public KuknosTradeActiveVM() {
        offerList = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(true);
    }

    public void getDataFromServer() {
        progressState.setValue(true);
        tradeRepo.getOpenOffers(0, 100, this, new ResponseCallback<KuknosResponseModel<KuknosOfferResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosOfferResponse> data) {
                offerList.setValue(data.getData());
                progressState.setValue(false);
            }

            @Override
            public void onError(String error) {
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
            }
        });
    }

    public void deleteTrade(int position) {
        progressState.setValue(true);
        KuknosOfferResponse.OfferResponse deleteItem = offerList.getValue().getOffers().get(position);
        tradeRepo.manangeOffer(
                deleteItem.getSelling().getAsset().getType().equals("native") ? "PMN" : deleteItem.getSelling().getAssetCode(),
                deleteItem.getSelling().getAssetIssuer(),
                deleteItem.getBuying().getAsset().getType().equals("native") ? "PMN" : deleteItem.getBuying().getAssetCode(),
                deleteItem.getBuying().getAssetIssuer(),
                "0", "0", String.valueOf(deleteItem.getId()), this,
                new ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>>() {
                    @Override
                    public void onSuccess(KuknosResponseModel<SubmitTransactionResponse> data) {
                        KuknosOfferResponse temp = offerList.getValue();
                        temp.getOffers().remove(position);
                        offerList.setValue(temp);
                        progressState.setValue(false);
                    }

                    @Override
                    public void onError(String error) {
                        errorM.setValue(new ErrorM(true, "", "", 0));
                        progressState.setValue(false);
                    }

                    @Override
                    public void onFailed() {
                        errorM.setValue(new ErrorM(true, "", "", 0));
                        progressState.setValue(false);
                    }

                });
    }

    public MutableLiveData<ErrorM> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorM> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<KuknosOfferResponse> getOfferList() {
        return offerList;
    }
}
