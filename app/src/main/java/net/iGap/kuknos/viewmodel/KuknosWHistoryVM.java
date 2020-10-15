package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.KuknosError;
import net.iGap.kuknos.Model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.Repository.PanelRepo;

public class KuknosWHistoryVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosOperationResponse> listMutableLiveData;
    private MutableLiveData<KuknosError> errorM;
    private MutableLiveData<Boolean> progressState;
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosWHistoryVM() {
        listMutableLiveData = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(true);
    }

    public void getDataFromServer() {
        progressState.setValue(true);
        panelRepo.getUserHistory(this, new ResponseCallback<KuknosResponseModel<KuknosOperationResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosOperationResponse> data) {
                listMutableLiveData.setValue(data.getData());
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

    public MutableLiveData<KuknosOperationResponse> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public MutableLiveData<KuknosError> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<KuknosError> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
