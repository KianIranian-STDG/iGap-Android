package net.iGap.fragments.giftStickers.enterNationalCode;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;

public class EnterNationalCodeViewModel extends BaseAPIViewModel {

    private ObservableBoolean hasError = new ObservableBoolean(false);
    private ObservableBoolean isEnabled = new ObservableBoolean(true);
    private ObservableInt errorMessage = new ObservableInt(R.string.empty_error_message);
    private ObservableInt showLoading = new ObservableInt(View.INVISIBLE);
    private SingleLiveEvent<Boolean> goNextStep = new SingleLiveEvent<>();

    public void onInquiryButtonClick(String nationalCode) {
        isEnabled.set(false);
        if (nationalCode.length() != 0) {
            if (nationalCode.length() == 10) {
                showLoading.set(View.VISIBLE);
                for (int i = 0; i < 100; i++) {

                }
                goNextStep.setValue(true);
            } else {
                hasError.set(true);
                errorMessage.set(R.string.elecBill_Entry_userIDLengthError);
                isEnabled.set(true);
            }
        } else {
            hasError.set(true);
            errorMessage.set(R.string.elecBill_Entry_userIDError);
            isEnabled.set(true);
        }
    }

    public ObservableBoolean getHasError() {
        return hasError;
    }

    public ObservableInt getErrorMessage() {
        return errorMessage;
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
}
