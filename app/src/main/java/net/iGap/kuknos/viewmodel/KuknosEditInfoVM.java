package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.Owners;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmKuknos;

public class KuknosEditInfoVM extends BaseAPIViewModel {
    private MutableLiveData<String> responseState;
    private MutableLiveData<KuknosUserInfoResponse> userInfo = new MutableLiveData<>();
    private MutableLiveData<Owners> ibanInfo = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressState = new MutableLiveData<>();
    private PanelRepo panelRepo = new PanelRepo();


    public KuknosEditInfoVM() {
        responseState = new MutableLiveData<>();
    }

    public void getIbanInfo(String iban) {
        panelRepo.getIbanInfo(iban, this, new ResponseCallback<KuknosResponseModel<Owners>>() {
            @Override
            public void onSuccess(KuknosResponseModel<Owners> data) {
                ibanInfo.setValue(data.getData());
            }

            @Override
            public void onError(String errorM) {
                responseState.setValue(errorM);
                ibanInfo.setValue(null);
            }

            @Override
            public void onFailed() {
                responseState.setValue("onFailed");
                ibanInfo.setValue(null);
            }
        });
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

                responseState.setValue("true");
            }

            @Override
            public void onError(String error) {
                responseState.setValue(error);
            }

            @Override
            public void onFailed() {
                responseState.setValue("onFailed");
            }
        });
    }


    public MutableLiveData<Owners> getIbanInfo() {
        return ibanInfo;
    }

    public void setIbanInfo(MutableLiveData<Owners> ibanInfo) {
        this.ibanInfo = ibanInfo;
    }

    public MutableLiveData<KuknosUserInfoResponse> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(MutableLiveData<KuknosUserInfoResponse> userInfo) {
        this.userInfo = userInfo;
    }

    public MutableLiveData<String> getResponseState() {
        return responseState;
    }

    public void setResponseState(MutableLiveData<String> responseState) {
        this.responseState = responseState;
    }

}
