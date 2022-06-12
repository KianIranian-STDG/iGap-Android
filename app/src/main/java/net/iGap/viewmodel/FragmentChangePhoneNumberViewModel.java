package net.iGap.viewmodel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.helper.FileLog;
import net.iGap.model.LocationModel;
import net.iGap.model.repository.ChangePhoneNumberRepository;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.CountryListComparator;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.structs.StructCountry;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.realm.RealmRoom;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentChangePhoneNumberViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Integer> showEnteredPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showChooseCountryDialog = new MutableLiveData<>();
    public SingleLiveEvent<String> showConfirmPhoneNumberDialog = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> showConnectionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlock = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorMessageEmptyErrorPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Integer> showError = new MutableLiveData<>();
    public MutableLiveData<Boolean>  goToActivation = new MutableLiveData<>();
    public MutableLiveData<Boolean> btnBackClick = new MutableLiveData<>();

    public ObservableField<String> callbackBtnChoseCountry = new ObservableField<>("Iran");
    public ObservableField<String> callbackEdtCodeNumber = new ObservableField<>("+98");
    public ObservableField<String> callBackEdtPhoneNumber = new ObservableField<>("");
    public ObservableField<String> edtPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableInt edtPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableInt isShowLoading = new ObservableInt(View.GONE);
    public ObservableInt showRetryView = new ObservableInt(View.GONE);
    public ObservableBoolean btnChoseCountryEnable = new ObservableBoolean(true);
    public ObservableBoolean edtPhoneNumberEnable = new ObservableBoolean(true);
    public ObservableBoolean btnStartEnable = new ObservableBoolean(true);
    public ObservableInt viewVisibility = new ObservableInt(View.VISIBLE);

    public ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();

    private ChangePhoneNumberRepository repository;

    public FragmentChangePhoneNumberViewModel(StringBuilder stringBuilder) {
        repository = ChangePhoneNumberRepository.getInstance();
        String list = stringBuilder.toString();
        String[] listArray = list.split("\\r?\\n");
        for (String s : listArray) {
            StructCountry structCountry = new StructCountry();
            String[] listItem = s.split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);
            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }
            structCountryArrayList.add(structCountry);
        }
        Collections.sort(structCountryArrayList, new CountryListComparator());
    }

    public void onTextChanged(String s) {
        if (s.startsWith("0")) {
            callBackEdtPhoneNumber.set("");
            showEnteredPhoneNumberStartWithZeroError.setValue(R.string.Toast_First_0);
        }
    }

    public void onClickChoseCountry() {
        showChooseCountryDialog.setValue(true);
    }

    public void setCountry(@NotNull StructCountry country) {
        isShowLoading.set(View.VISIBLE);
        repository.getCountryInfo(country.getAbbreviation(), new ChangePhoneNumberRepository.RepositoryCallback<LocationModel>() {
            @Override
            public void onSuccess(LocationModel data) {
                isShowLoading.set(View.GONE);
                callbackEdtCodeNumber.set("+" + data.getCountryCode());
                RegisterRepository.getInstance().setCountryCode("+" + data.getCountryCode());
                if (data.getPhoneMask().equals("")) {
                    edtPhoneNumberMask.set("##################");
                } else {
                    edtPhoneNumberMask.set(data.getPhoneMask().replace("X", "#").replace(" ", "-"));
                }
                repository.setIsoCode(country.getAbbreviation());
                callbackBtnChoseCountry.set(data.getCountryName());
                btnStartEnable.set(true);
            }

            @Override
            public void onError() {
                isShowLoading.set(View.GONE);
            }
        });
        callBackEdtPhoneNumber.set("");
    }

    public void onClickChangeNumber() {
        String phoneNumber = callBackEdtPhoneNumber.get();
        String regex = repository.getRegex();
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        if (phoneNumber.length() > 0 && regex.equals("") || (!regex.equals("") && phoneNumber.replace("-", "").matches(regex))) {
            showConfirmPhoneNumberDialog.setValue(callbackEdtCodeNumber.get() + callBackEdtPhoneNumber.get());
        } else {
            if (phoneNumber.length() == 0) {
                showErrorMessageEmptyErrorPhoneNumberDialog.setValue(true);
            } else {
                showEnteredPhoneNumberStartWithZeroError.setValue(R.string.Toast_Minimum_Characters);
            }
        }
    }

    public void onClickBack(){
        btnBackClick.setValue(true);
    }

    public void confirmPhoneNumber() {
        if (WebSocketClient.getInstance().isConnect()) {
            btnStartEnable.set(false);
            isShowLoading.set(View.VISIBLE);
            if (WebSocketClient.getInstance().isConnect()) {
                sendChangeNumberRequest();
            } else {
                showError.setValue(R.string.connection_error);
                edtPhoneNumberEnable.set(true);
                btnStartEnable.set(true);
            }
        } else {
            edtPhoneNumberEnable.set(true);
            showConnectionErrorDialog.setValue(true);
        }
    }

    private void sendChangeNumberRequest() {
        String phoneNumber = callBackEdtPhoneNumber.get();
        phoneNumber = phoneNumber.replace("-", "");
        repository.setPhoneNumber(phoneNumber);
        AbstractObject req = null;
        IG_RPC.Change_Phone_Number change_phone_number = new IG_RPC.Change_Phone_Number();
        change_phone_number.countryCode = repository.getIsoCode();
        change_phone_number.phoneNumber = Long.parseLong(phoneNumber);
        req = change_phone_number;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Change_Phone_Number res = (IG_RPC.Res_Change_Phone_Number) response;
                isShowLoading.set(View.GONE);
                goToActivation.postValue(true);
            }
            else{
                IG_RPC.Error e = (IG_RPC.Error) error;
                FileLog.e("Change Phone Number -> Major: " + e.major + " Minor: " + e.minor);
                if (e.major == 1100 && e.minor == 1) {
                    showError.postValue(R.string.invalid_phone_number);
                    callBackEdtPhoneNumber.set("");
                    btnStartEnable.set(true);
                    isShowLoading.set(View.GONE);
                }
            }
        });
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}