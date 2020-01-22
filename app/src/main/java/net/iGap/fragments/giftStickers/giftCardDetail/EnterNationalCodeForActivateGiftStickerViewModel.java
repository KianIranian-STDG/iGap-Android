package net.iGap.fragments.giftStickers.giftCardDetail;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.module.SingleLiveEvent;

public class EnterNationalCodeForActivateGiftStickerViewModel extends ViewModel {

    private ObservableBoolean hasError = new ObservableBoolean(false);
    private ObservableInt errorMessage = new ObservableInt(R.string.empty_error_message);
    private ObservableField<String> nationalCodeField = new ObservableField<>("");
    private ObservableBoolean isEnable = new ObservableBoolean(true);
    private ObservableInt isShowLoading = new ObservableInt(View.GONE);
    private SingleLiveEvent<String> goToNextStep = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showRequestErrorMessage = new SingleLiveEvent<>();

    public void onActiveButtonClicked(String nationalCode) {
        if (nationalCode.length() != 0) {
            if (nationalCode.length() == 10) {

            } else {
                hasError.set(true);
                errorMessage.set(R.string.elecBill_Entry_userIDLengthError);
            }
        } else {
            hasError.set(true);
            errorMessage.set(R.string.elecBill_Entry_userIDError);
        }
    }

    public ObservableBoolean getHasError() {
        return hasError;
    }

    public ObservableInt getErrorMessage() {
        return errorMessage;
    }

    public ObservableField<String> getNationalCodeField() {
        return nationalCodeField;
    }

    public ObservableBoolean getIsEnable() {
        return isEnable;
    }

    public ObservableInt getIsShowLoading() {
        return isShowLoading;
    }

    public SingleLiveEvent<String> getGoToNextStep() {
        return goToNextStep;
    }

    public SingleLiveEvent<Integer> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}
