package net.iGap.fragments.giftStickers.giftCardDetail;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.giftStickers.enterNationalCode.CheckNationalCodeResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.realm.RealmUserInfo;

public class EnterNationalCodeForActivateGiftStickerViewModel extends BaseAPIViewModel {

    private ObservableBoolean hasError = new ObservableBoolean(false);
    private ObservableInt errorMessage = new ObservableInt(R.string.empty_error_message);
    private ObservableField<String> nationalCodeField = new ObservableField<>("");
    private ObservableBoolean isEnable = new ObservableBoolean(true);
    private ObservableInt isShowLoading = new ObservableInt(View.GONE);
    private SingleLiveEvent<Boolean> goToNextStep = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showRequestErrorMessage = new SingleLiveEvent<>();

    public EnterNationalCodeForActivateGiftStickerViewModel() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null && realmUserInfo.getNationalCode() != null && realmUserInfo.getNationalCode().length() == 10) {
                String nationalCode = realmUserInfo.getNationalCode();
                nationalCodeField.set(nationalCode);
            }
        });
    }

    public void onActiveButtonClicked(String nationalCode) {
        if (nationalCode.length() != 0) {
            if (nationalCode.length() == 10) {
                isShowLoading.set(View.VISIBLE);

                G.nationalCode = nationalCode;

                String phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber();

                if (phoneNumber.length() > 2 && phoneNumber.substring(0, 2).equals("98")) {
                    phoneNumber = "0" + phoneNumber.substring(2);
                    new ApiInitializer<CheckNationalCodeResponse>().initAPI(new RetrofitFactory().getShahkarRetrofit().checkNationalCode(nationalCode, phoneNumber), this, new ResponseCallback<CheckNationalCodeResponse>() {
                        @Override
                        public void onSuccess(CheckNationalCodeResponse data) {
                            isShowLoading.set(View.INVISIBLE);
                            isEnable.set(true);
                            if (data.isSuccess()) {
                                goToNextStep.setValue(true);
                            } else {
                                errorMessage.set(R.string.national_code_not_match_with_phone_number_error);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            isShowLoading.set(View.INVISIBLE);
                            isEnable.set(true);
                            errorMessage.set(R.string.national_code_not_match_with_phone_number_error);

                            goToNextStep.setValue(false);
                        }

                        @Override
                        public void onFailed() {
                            isShowLoading.set(View.INVISIBLE);
                            isEnable.set(true);
                        }
                    });
                } else {
                    hasError.set(true);
                    errorMessage.set(R.string.error);
                }
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

    public SingleLiveEvent<Boolean> getGoToNextStep() {
        return goToNextStep;
    }

    public SingleLiveEvent<Integer> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}
