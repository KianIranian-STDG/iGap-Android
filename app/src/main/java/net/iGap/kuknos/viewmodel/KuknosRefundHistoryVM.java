package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.Parsian.KuknosRefundHistory;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.observers.interfaces.ResponseCallback;

public class KuknosRefundHistoryVM extends BaseAPIViewModel {

    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<KuknosRefundHistory> refundHistory;
    private MutableLiveData<String> errorState;
    private MutableLiveData<Boolean> progressState;

    public KuknosRefundHistoryVM() {
        this.refundHistory = new MutableLiveData<>();
        this.progressState = new MutableLiveData<>();
        this.errorState = new MutableLiveData<>();
        progressState.setValue(true);
    }

    public void getUserRefundsFromServer() {
        progressState.setValue(true);
        panelRepo.getUserRefunds(panelRepo.getUserRepo().getAccountID(), this, new ResponseCallback<KuknosResponseModel<KuknosRefundHistory>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosRefundHistory> data) {
                refundHistory.setValue(data.getData());
                progressState.setValue(false);
            }

            @Override
            public void onError(String error) {
                progressState.setValue(false);
                errorState.setValue(error);
            }

            @Override
            public void onFailed() {
                progressState.setValue(false);
                errorState.setValue("Process Failed");
            }
        });
    }

    public MutableLiveData<KuknosRefundHistory> getRefundHistory() {
        return refundHistory;
    }

    public MutableLiveData<String> getErrorState() {
        return errorState;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }
}
