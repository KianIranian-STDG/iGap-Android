package net.iGap.viewmodel.kuknos;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.TradeRepo;
import net.iGap.model.kuknos.ErrorM;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosTradeResponse;

public class KuknosTradeHistoryVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosTradeResponse> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;
    private TradeRepo tradeRepo = new TradeRepo();

    public KuknosTradeHistoryVM() {
        listMutableLiveData = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(true);
    }

    public void getDataFromServer() {
        progressState.setValue(true);
        tradeRepo.getTradesHistory(0, 100, this, new ResponseCallback<KuknosResponseModel<KuknosTradeResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosTradeResponse> data) {
                listMutableLiveData.setValue(data.getData());
                progressState.setValue(true);
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

    public MutableLiveData<KuknosTradeResponse> getListMutableLiveData() {
        return listMutableLiveData;
    }
}
