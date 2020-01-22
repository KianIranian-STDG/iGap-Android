package net.iGap.fragments.giftStickers.enterNationalCode;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.R;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.module.SingleLiveEvent;
import net.iGap.realm.RealmUserInfo;

public class EnterNationalCodeViewModel extends BaseAPIViewModel {

    private ObservableBoolean hasError = new ObservableBoolean(false);
    private ObservableBoolean saveChecked = new ObservableBoolean(true);
    private ObservableBoolean isEnabled = new ObservableBoolean(true);
    private ObservableInt errorMessageNationalCode = new ObservableInt(R.string.empty_error_message);
    private ObservableField<String> nationalCodeField = new ObservableField<>();
    private ObservableInt showLoading = new ObservableInt(View.INVISIBLE);
    private MutableLiveData<String> requestError = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessageRequestFailed = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goNextStep = new SingleLiveEvent<>();

    public EnterNationalCodeViewModel() {

        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null && realmUserInfo.getNationalCode() != null && realmUserInfo.getNationalCode().length() == 10) {
                String nationalCode = realmUserInfo.getNationalCode();
                nationalCodeField.set(nationalCode);
            }
        });
    }

    public void onInquiryButtonClick(String nationalCode) {
        if (nationalCode.length() != 0) {
            if (nationalCode.length() == 10) {
                String phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber();

                if (saveChecked.get()) {
                    DbManager.getInstance().doRealmTask(realm -> {
                        realm.executeTransactionAsync(realm1 -> {
                            RealmUserInfo realmUserInfo = realm1.where(RealmUserInfo.class).findFirst();
                            if (realmUserInfo != null) {
                                realmUserInfo.setNationalCode(nationalCode);
                            }
                        });
                    });
                }

                if (phoneNumber.length() > 2 && phoneNumber.substring(0, 2).equals("98")) {
                    phoneNumber = "0" + phoneNumber.substring(2);
                    showLoading.set(View.VISIBLE);
                    isEnabled.set(false);
                    new ApiInitializer<CheckNationalCodeResponse>().initAPI(new RetrofitFactory().getShahkarRetrofit().checkNationalCode(nationalCode, phoneNumber), this, new ResponseCallback<CheckNationalCodeResponse>() {
                        @Override
                        public void onSuccess(CheckNationalCodeResponse data) {
                            showLoading.set(View.INVISIBLE);
                            isEnabled.set(true);
                            if (data.isSuccess()) {
                                goNextStep.setValue(true);
                            } else {
                                showErrorMessageRequestFailed.setValue(R.string.national_code_not_match_with_phone_number_error);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            showLoading.set(View.INVISIBLE);
                            isEnabled.set(true);
                            requestError.setValue(error);
                        }

                        @Override
                        public void onFailed() {
                            showErrorMessageRequestFailed.setValue(R.string.connection_error);
                            showLoading.set(View.INVISIBLE);
                            isEnabled.set(true);
                        }
                    });
                } else {
                    showErrorMessageRequestFailed.setValue(R.string.error);
                }
            } else {
                hasError.set(true);
                errorMessageNationalCode.set(R.string.elecBill_Entry_userIDLengthError);
            }
        } else {
            hasError.set(true);
            errorMessageNationalCode.set(R.string.elecBill_Entry_userIDError);
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

    public ObservableBoolean getSaveChecked() {
        return saveChecked;
    }

    public ObservableField getNationalCodeField() {
        return nationalCodeField;
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
