package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import com.google.protobuf.ByteString;

import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperSaveFile;
import net.iGap.model.LocationModel;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CountryListComparator;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.structs.StructCountry;
import net.iGap.observers.interfaces.OnQrCodeNewDevice;
import net.iGap.request.RequestQrCodeNewDevice;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentRegisterViewModel extends BaseViewModel {

    //view callback
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConditionErrorDialog = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> goNextStep = new SingleLiveEvent<>();
    public MutableLiveData<Integer> showEnteredPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showChooseCountryDialog = new MutableLiveData<>();
    public SingleLiveEvent<String> showConfirmPhoneNumberDialog = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> showConnectionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlock = new MutableLiveData<>();
    public SingleLiveEvent<Long> goToTwoStepVerificationPage = new SingleLiveEvent<>();
    public MutableLiveData<WaitTimeModel> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorMessageEmptyErrorPhoneNumberDialog = new MutableLiveData<>();
    public SingleLiveEvent<Integer> showDialogQrCode = new SingleLiveEvent<>();
    public MutableLiveData<Uri> shareQrCodeIntent = new MutableLiveData<>();
    public MutableLiveData<Boolean> hideDialogQRCode = new MutableLiveData<>();
    public MutableLiveData<Integer> showError = new MutableLiveData<>();


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
    private boolean termsAndConditionIsChecked = false;


    public String _resultQrCode;
    private Uri image_uriQrCode;

    private RegisterRepository repository;

    public FragmentRegisterViewModel(StringBuilder stringBuilder) {
        repository = RegisterRepository.getInstance();

        String list = stringBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        //Convert array
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
        repository.inRegisterMode(hideDialogQRCode, goToTwoStepVerificationPage);
        /*getTermsAndConditionData();*/
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
        repository.getCountryInfo(country.getAbbreviation(), new RegisterRepository.RepositoryCallback<LocationModel>() {
            @Override
            public void onSuccess(LocationModel data) {
                isShowLoading.set(View.GONE);
                callbackEdtCodeNumber.set("+" + data.getCountryCode());
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

    public void termsOnCheckChange(boolean isChecked) {
        termsAndConditionIsChecked = isChecked;
    }

    public void onClicksStart() {
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

/*
    public void onRetryClick() {
        getTermsAndConditionData();
    }
*/

/*
    private void getTermsAndConditionData() {
        isShowLoading.set(View.VISIBLE);
        showRetryView.set(View.GONE);
        if (getRequestManager().isSecure()) {
            isShowLoading.set(View.VISIBLE);
            showRetryView.set(View.GONE);
            repository.getTermsOfServiceBody(new RegisterRepository.RepositoryCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    if (data != null) {
                        agreementDescription = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                    }
                    isShowLoading.set(View.INVISIBLE);
                    showTermsAndConditionDialog.postValue(agreementDescription);
                    viewVisibility.set(View.VISIBLE);
                */
/*repository.getInfoLocation(new RegisterRepository.RepositoryCallback<LocationModel>() {
                    @Override
                    public void onSuccess(LocationModel data) {
                        repository.inRegisterMode(hideDialogQRCode, goToTwoStepVerificationPage);
                        isShowLoading.set(View.INVISIBLE);
                        viewVisibility.set(View.VISIBLE);
                        callbackEdtCodeNumber.set("+" + data.getCountryCode());
                        callbackBtnChoseCountry.set(data.getCountryName());
                        if (data.getPhoneMask() != null && !data.getPhoneMask().equals("")) {
                            edtPhoneNumberMask.set(data.getPhoneMask().replace("X", "#").replace(" ", "-"));
                        } else {
                            edtPhoneNumberMask.set("##################");
                        }
                    }

                    @Override
                    public void onError() {
                        isShowLoading.set(View.INVISIBLE);
                        showRetryView.set(View.VISIBLE);
                    }
                });*//*

                }

                @Override
                public void onError() {
                    isShowLoading.set(View.INVISIBLE);
                    //showRetryView.set(View.VISIBLE);
                }
            });
        } else {
            isShowLoading.set(View.GONE);
            showRetryView.set(View.VISIBLE);
            viewVisibility.set(View.INVISIBLE);
        }
    }
*/

    public void confirmPhoneNumber() {
        if (WebSocketClient.getInstance().isConnect()) { //connection ok
            btnStartEnable.set(false);
            isShowLoading.set(View.VISIBLE);
            if (AccountManager.getInstance().isExistThisAccount(callbackEdtCodeNumber.get().replace("+", "") + callBackEdtPhoneNumber.get().replace("-", ""))) {
                Log.wtf(this.getClass().getName(), "exist");
                btnStartEnable.set(true);
                isShowLoading.set(View.GONE);
                repository.getLoginExistUser().postValue(true);
            } else {
                if (WebSocketClient.getInstance().isConnect()) {
                    registerUser();
                } else {
                    showError.setValue(R.string.connection_error);
                    edtPhoneNumberEnable.set(true);
                    btnStartEnable.set(true);
                }
            }
        } else { // connection error
            edtPhoneNumberEnable.set(true);
            showConnectionErrorDialog.setValue(true);
        }
    }

    private void registerUser() {
        repository.registration(callBackEdtPhoneNumber.get(), new RegisterRepository.RepositoryCallbackWithError<ErrorWithWaitTime>() {
            @Override
            public void onSuccess() {
                isShowLoading.set(View.GONE);
                btnStartEnable.set(true);
                edtPhoneNumberEnable.set(true);
                goNextStep.postValue(true);
            }

            @Override
            public void onError(ErrorWithWaitTime error) {
                isShowLoading.set(View.GONE);
                edtPhoneNumberEnable.set(true);
                btnStartEnable.set(true);
                if (error.getMajorCode() == 100) {
                    if (error.getMinorCode() == 1) {
                        //Invalid countryCode
                        showError.postValue(R.string.country_code_not_valid);
                    } else if (error.getMinorCode() == 2) {
                        //Invalid phoneNumber
                        showError.postValue(R.string.please_enter_correct_phone_number);
                    }
                } else if (error.getMajorCode() == 10) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.IP_blocked, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 101) {
                    //Invalid phoneNumber
                    showError.postValue(R.string.please_enter_correct_phone_number);
                } else if (error.getMajorCode() == 135) {
                    showDialogUserBlock.postValue(true);
                } else if (error.getMajorCode() == 136) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 137) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 5 && error.getMinorCode() == 1) { // timeout
                    showError.postValue(R.string.connection_error);
                } else if (error.getMajorCode() == 1038 && error.getMinorCode() == 1) {
                    showError.postValue(R.string.error);
                }
            }
        });
    }

    public void shareQr() {
        if (_resultQrCode == null) {
            return;
        }
        try {
            File file = new File(_resultQrCode);
            if (file.exists()) {
                shareQrCodeIntent.setValue(Uri.fromFile(file));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void saveQr() {
        if (_resultQrCode == null) {
            return;
        }
        File file = new File(_resultQrCode);
        if (file.exists()) {
            HelperSaveFile.savePicToGallery(_resultQrCode, true);
        }
    }

    public void onClickQrCode() {
        isShowLoading.set(View.VISIBLE);
        //todo:move it to repository for  ehsan from baggi
        new RequestQrCodeNewDevice().qrCodeNewDevice(
                new OnQrCodeNewDevice() {
                    @Override
                    public void getQrCode(ByteString codeImage, int expireTime) {
                        _resultQrCode = G.DIR_TEMP + "/" + "QrCode" + ".jpg";
                        File f = new File(_resultQrCode);
                        if (f.exists()) {
                            f.delete();
                        }
                        AndroidUtils.writeBytesToFile(_resultQrCode, codeImage.toByteArray());
                        image_uriQrCode = Uri.parse("file://" + _resultQrCode);
                        G.handler.post(() -> isShowLoading.set(View.GONE));
                        showDialogQrCode.postValue(expireTime);
                    }

                    @Override
                    public void onError(int major, int minor) {
                        isShowLoading.set(View.GONE);
                        if (major == 5 && minor == 1) {
                            showError.postValue(R.string.connection_error);
                        } else {
                            showError.postValue(R.string.error);
                        }
                    }
                });
    }

    /*public void onTermsAndConditionClick() {
        if (agreementDescription == null || agreementDescription.isEmpty()) {
            getTermsAndConditionData();
        } else {
            showTermsAndConditionDialog.setValue(agreementDescription);
        }
    }*/

    public void timerFinished() {
        btnStartEnable.set(true);
        btnChoseCountryEnable.set(true);
        edtPhoneNumberEnable.set(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}