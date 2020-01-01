package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;

import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.operations.OperationResponse;

public class KuknosWHistoryVM extends ViewModel {

    private MutableLiveData<Page<OperationResponse>> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosWHistoryVM() {
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
        /*panelRepo.getUserHistory(new ApiResponse<Page<OperationResponse>>() {
            @Override
            public void onResponse(Page<OperationResponse> operationResponsePage) {
                listMutableLiveData.setValue(operationResponsePage);
            }

            @Override
            public void onFailed(String error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });*/
    }

    public MutableLiveData<Page<OperationResponse>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void setListMutableLiveData(MutableLiveData<Page<OperationResponse>> listMutableLiveData) {
        this.listMutableLiveData = listMutableLiveData;
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
