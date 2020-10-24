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

    public KuknosRefundHistoryVM() {
        this.refundHistory = new MutableLiveData<>();
    }

    public void getUserRefundsFromServer() {
        panelRepo.getUserRefunds(panelRepo.getUserRepo().getAccountID(), this, new ResponseCallback<KuknosResponseModel<KuknosRefundHistory>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosRefundHistory> data) {
                refundHistory.setValue(data.getData());
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public MutableLiveData<KuknosRefundHistory> getRefundHistory() {
        return refundHistory;
    }
}
