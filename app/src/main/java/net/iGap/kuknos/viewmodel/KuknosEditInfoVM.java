package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmKuknos;

public class KuknosEditInfoVM extends BaseAPIViewModel {
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<KuknosUserInfoResponse> userInfo;
    private PanelRepo panelRepo = new PanelRepo();
    private SingleLiveEvent<Boolean> datePick = new SingleLiveEvent<>();

    public KuknosEditInfoVM() {
        progressState = new MutableLiveData<>();
        userInfo = new MutableLiveData<>();
        userInfo.setValue(null);
    }

    public void getInFoFromServerToCheckUserProfile() {
        progressState.setValue(true);
        panelRepo.getUserInfoResponse(this, new ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserInfoResponse> data) {
                if (data.getData().getIban() != null) {
                    RealmKuknos.updateIban(data.getData().getIban());
                }
                userInfo.setValue(data.getData());
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                userInfo.setValue(null);
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                userInfo.setValue(null);
                progressState.setValue(false);

            }
        });
    }

    public void sendUserInfo(KuknosUserInfoResponse userInfo) {
        panelRepo.updateUserInfo(userInfo, this, new ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserInfoResponse> data) {
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
    public void datePickerAction() {
        datePick.setValue(true);
    }
    public SingleLiveEvent<Boolean> getDatePick() {
        return datePick;
    }
    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public MutableLiveData<KuknosUserInfoResponse> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(MutableLiveData<KuknosUserInfoResponse> userInfo) {
        this.userInfo = userInfo;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
