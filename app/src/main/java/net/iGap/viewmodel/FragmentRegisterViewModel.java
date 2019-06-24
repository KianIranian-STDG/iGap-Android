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

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.text.HtmlCompat;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperSaveFile;
import net.iGap.model.LocationModel;
import net.iGap.model.repository.ErrorWithWaitTime;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTracker;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CountryListComparator;
import net.iGap.module.structs.StructCountry;
import net.iGap.request.RequestQrCodeNewDevice;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentRegisterViewModel extends ViewModel {

    //view callback
    public MutableLiveData<Boolean> closeKeyword = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConditionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goNextStep = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberStartWithZeroError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEnteredPhoneNumberError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showChooseCountryDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConfirmPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConnectionErrorDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogUserBlock = new MutableLiveData<>();
    public MutableLiveData<Long> goToTwoStepVerificationPage = new MutableLiveData<>();
    public MutableLiveData<WaitTimeModel> showDialogWaitTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> showErrorMessageEmptyErrorPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<Integer> showDialogQrCode = new MutableLiveData<>();
    public MutableLiveData<Uri> shareQrCodeIntent = new MutableLiveData<>();
    public MutableLiveData<Boolean> hideDialogQRCode = new MutableLiveData<>();

    public ObservableField<String> callbackBtnChoseCountry = new ObservableField<>("Iran");
    public ObservableField<String> callbackEdtCodeNumber = new ObservableField<>("+98");
    public ObservableField<String> callBackEdtPhoneNumber = new ObservableField<>("");
    public ObservableField<String> edtPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableInt edtPhoneNumberMaskMaxCount = new ObservableInt(11);
    public ObservableInt isShowLoading = new ObservableInt(View.VISIBLE);
    public ObservableBoolean btnChoseCountryEnable = new ObservableBoolean(true);
    public ObservableBoolean edtPhoneNumberEnable = new ObservableBoolean(true);
    public ObservableBoolean btnStartEnable = new ObservableBoolean(true);
    public ObservableInt viewVisibility = new ObservableInt(View.GONE);

    public ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();
    private boolean termsAndConditionIsChecked = false;
    private String agreementDescription;
    private int sendRequestRegister = 0;

    public String _resultQrCode;
    private Uri image_uriQrCode;

    private RegisterRepository repository;

    public FragmentRegisterViewModel(RegisterRepository repository, StringBuilder stringBuilder) {
        this.repository = repository;

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
        this.repository.getTermsOfServiceBody(new RegisterRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data != null) {
                    agreementDescription = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                }
                FragmentRegisterViewModel.this.repository.getInfoLocation(new RegisterRepository.RepositoryCallback<LocationModel>() {
                    @Override
                    public void onSuccess(LocationModel data) {
                        FragmentRegisterViewModel.this.repository.inRegisterMode(hideDialogQRCode, goToTwoStepVerificationPage);
                        isShowLoading.set(View.GONE);
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
                        isShowLoading.set(View.GONE);
                        //Todo: show Reload View
                    }
                });
            }

            @Override
            public void onError() {
                isShowLoading.set(View.GONE);
                //Todo:show Reload View
            }
        });
    }

    public String getAgreementDescription() {
        return agreementDescription;
    }

    public void onTextChanged(String s) {
        if (s.startsWith("0")) {
            callBackEdtPhoneNumber.set("");
            showEnteredPhoneNumberStartWithZeroError.setValue(true);
        }
    }

    public void onClickChoseCountry() {
        showChooseCountryDialog.setValue(true);
    }

    public void setCountry(StructCountry country) {
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
            if (termsAndConditionIsChecked) {
                showConfirmPhoneNumberDialog.setValue(true);
            } else {
                showConditionErrorDialog.setValue(true);
            }
        } else {
            if (phoneNumber.length() == 0) {
                showErrorMessageEmptyErrorPhoneNumberDialog.setValue(true);
            } else {
                showEnteredPhoneNumberStartWithZeroError.setValue(phoneNumber.replace("-", "").matches(regex));
            }
        }
    }

    public void confirmPhoneNumber() {
        if (G.socketConnection) { //connection ok
            btnStartEnable.set(false);
            isShowLoading.set(View.VISIBLE);
            if (G.socketConnection) {
                registerUser();
            } else {
                new Handler().postDelayed(this::registerUser, 1000);
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
                goNextStep.postValue(true);
                G.handler.post(() -> isShowLoading.set(View.GONE));
            }

            @Override
            public void onError(ErrorWithWaitTime error) {
                isShowLoading.set(View.GONE);
                if (error.getMajorCode() == 100) {
                    if (error.getMinorCode() == 1) {
                        //Invalid countryCode
                    } else if (error.getMinorCode() == 2) {
                        //Invalid phoneNumber
                    }
                } else if (error.getMajorCode() == 10) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.IP_blocked, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 101) {
                    //Invalid phoneNumber
                } else if (error.getMajorCode() == 135) {
                    showDialogUserBlock.postValue(true);
                } else if (error.getMajorCode() == 136) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 137) {
                    showDialogWaitTime.postValue(new WaitTimeModel(R.string.USER_VERIFY_MANY_TRIES_SEND, error.getWaitTime(), error.getMajorCode()));
                } else if (error.getMajorCode() == 5 && error.getMinorCode() == 1) { // timeout
                    if (sendRequestRegister <= 2) {
                        registerUser();
                        sendRequestRegister++;
                    }
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
        new RequestQrCodeNewDevice().qrCodeNewDevice((codeImage, expireTime) -> {
            _resultQrCode = G.DIR_TEMP + "/" + "QrCode" + ".jpg";
            File f = new File(_resultQrCode);
            if (f.exists()) {
                f.delete();
            }
            AndroidUtils.writeBytesToFile(_resultQrCode, codeImage.toByteArray());
            image_uriQrCode = Uri.parse("file://" + _resultQrCode);
            G.handler.post(() -> isShowLoading.set(View.GONE));
            showDialogQrCode.postValue(expireTime);
        });
    }

    public void timerFinished() {
        btnStartEnable.set(true);
        btnChoseCountryEnable.set(true);
        edtPhoneNumberEnable.set(true);
    }

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }
}