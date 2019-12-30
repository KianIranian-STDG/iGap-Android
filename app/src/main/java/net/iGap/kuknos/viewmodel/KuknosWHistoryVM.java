package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

public class KuknosWHistoryVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosOperationResponse> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
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
            public void onError(ErrorModel error) {
                progressState.setValue(false);
            }
        });
    }

    public MutableLiveData<KuknosOperationResponse> getListMutableLiveData() {
        return listMutableLiveData;
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
}
