package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.internal.$Gson$Types;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;

public class KuknosEditInfoVM extends BaseAPIViewModel {

    private static final String TAG = "KuknosEditInfoVM";
    private ObservableField<String> nationalId = new ObservableField<>();
    private ObservableField<String> firstName = new ObservableField<>();
    private ObservableField<String> lastName = new ObservableField<>();
    private SingleLiveEvent<Boolean> saveEdit = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> datePick = new SingleLiveEvent<>();
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosEditInfoVM() {
        panelRepo.getUserInfoResponse(this, new ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserInfoResponse> data) {
                nationalId.set(data.getData().getNationalCode());
                firstName.set(data.getData().getFirstName());
                lastName.set(data.getData().getLastName());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error );
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "onFailed: " );
            }
        });

    }

    public void datePickerAction() {
        datePick.setValue(true);
    }
    public void saveAccountInfo(){
        saveEdit.setValue(true);
    }

    public SingleLiveEvent<Boolean> getDatePick() {
        return datePick;
    }

    public ObservableField<String> getNationalId() {
        return nationalId;
    }

    public void setNationalId(ObservableField<String> nationalId) {
        this.nationalId = nationalId;
    }

    public ObservableField<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(ObservableField<String> firstName) {
        this.firstName = firstName;
    }

    public ObservableField<String> getLastName() {
        return lastName;
    }

    public void setLastName(ObservableField<String> lastName) {
        this.lastName = lastName;
    }

    public SingleLiveEvent<Boolean> getSaveEdit() {
        return saveEdit;
    }
}
