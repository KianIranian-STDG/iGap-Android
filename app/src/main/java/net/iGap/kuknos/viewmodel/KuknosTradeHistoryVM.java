package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.TradeRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosTradeHistoryM;
import net.iGap.kuknos.service.model.KuknosWHistoryM;

import org.stellar.sdk.responses.OfferResponse;
import org.stellar.sdk.responses.Page;

import java.util.ArrayList;
import java.util.List;

public class KuknosTradeHistoryVM extends ViewModel {

    private MutableLiveData<Page<OfferResponse>> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;
    private TradeRepo tradeRepo = new TradeRepo();
    private API mode;

    public enum API {
        OFFERS_LIST, TRADES_LIST
    }

    public KuknosTradeHistoryVM() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        if (errorM == null) {
            errorM = new MutableLiveData<>();
        }
        if (progressState == null) {
            progressState = new MutableLiveData<>();
            progressState.setValue(true);
        }
    }

    public void getDataFromServer() {
        if (mode == API.OFFERS_LIST) {
            tradeRepo.getOffersList(new ApiResponse<Page<OfferResponse>>() {
                @Override
                public void onResponse(Page<OfferResponse> offerResponsePage) {
                    listMutableLiveData.setValue(offerResponsePage);
                }

                @Override
                public void onFailed(String error) {

                }

                @Override
                public void setProgressIndicator(boolean visibility) {
                    progressState.setValue(visibility);
                }
            });
        }
        else {
            tradeRepo.getTradesList(new ApiResponse<Page<OfferResponse>>() {
                @Override
                public void onResponse(Page<OfferResponse> offerResponsePage) {
                    listMutableLiveData.setValue(offerResponsePage);
                }

                @Override
                public void onFailed(String error) {

                }

                @Override
                public void setProgressIndicator(boolean visibility) {
                    progressState.setValue(visibility);
                }
            });
        }
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

    public MutableLiveData<Page<OfferResponse>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void setListMutableLiveData(MutableLiveData<Page<OfferResponse>> listMutableLiveData) {
        this.listMutableLiveData = listMutableLiveData;
    }

    public API getMode() {
        return mode;
    }

    public void setMode(API mode) {
        this.mode = mode;
    }
}
