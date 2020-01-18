package net.iGap.fragments.giftStickers.enterNationalCode;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.AccountManager;
import net.iGap.R;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.module.SingleLiveEvent;

public class EnterNationalCodeViewModel extends BaseAPIViewModel {

    private ObservableBoolean hasError = new ObservableBoolean(false);
    private ObservableBoolean isEnabled = new ObservableBoolean(true);
    private ObservableInt errorMessageNationalCode = new ObservableInt(R.string.empty_error_message);
    private ObservableInt showLoading = new ObservableInt(View.INVISIBLE);
    private MutableLiveData<String> requestError = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessageRequestFailed = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goNextStep = new SingleLiveEvent<>();

    public void onInquiryButtonClick(String nationalCode) {
        isEnabled.set(false);
        if (nationalCode.length() != 0) {
            if (nationalCode.length() == 10) {
                String phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber();
                if (phoneNumber.substring(0, 2).equals("98")) {
                    phoneNumber = "0" + phoneNumber.substring(2);
                    showLoading.set(View.VISIBLE);
                    new ApiInitializer<CheckNationalCodeResponse>().initAPI(new RetrofitFactory().getShahkarRetrofit().checkNationalCode(nationalCode, phoneNumber), this, new ResponseCallback<CheckNationalCodeResponse>() {
                        @Override
                        public void onSuccess(CheckNationalCodeResponse data) {
                            showLoading.set(View.GONE);
                            if (data.isSuccess()) {
                                goNextStep.setValue(true);
                            } else {
                                showErrorMessageRequestFailed.setValue(R.string.national_code_not_match_with_phone_number_error);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            showLoading.set(View.GONE);
                            requestError.setValue(error);
                        }

                        @Override
                        public void onFailed() {
                            showErrorMessageRequestFailed.setValue(R.string.connection_error);
                            showLoading.set(View.GONE);
                        }
                    });
                } else {
                    showErrorMessageRequestFailed.setValue(R.string.error);
                }
            } else {
                hasError.set(true);
                errorMessageNationalCode.set(R.string.elecBill_Entry_userIDLengthError);
                isEnabled.set(true);
            }
        } else {
            hasError.set(true);
            errorMessageNationalCode.set(R.string.elecBill_Entry_userIDError);
            isEnabled.set(true);
        }
    }

    public ObservableBoolean getHasError() {
        return hasError;
    }

    public ObservableInt getErrorMessage() {
        return errorMessageNationalCode;
    }

    public ObservableBoolean getIsEnabled() {
        return isEnabled;
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public SingleLiveEvent<Boolean> getGoNextStep() {
        return goNextStep;
    }

    public MutableLiveData<String> getRequestError() {
        return requestError;
    }

    public MutableLiveData<Integer> getShowErrorMessageRequestFailed() {
        return showErrorMessageRequestFailed;
    }
}
