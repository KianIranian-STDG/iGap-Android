package net.iGap.viewmodel.mobileBank;

import android.view.View;

import androidx.databinding.ObservableInt;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.MobileBankExpiredTokenCallback;
import net.iGap.module.SingleLiveEvent;

public abstract class BaseMobileBankViewModel extends BaseAPIViewModel implements MobileBankExpiredTokenCallback {

    private SingleLiveEvent<Boolean> goToLoginPage = new SingleLiveEvent<>();

    protected ObservableInt showLoading = new ObservableInt(View.GONE);
    protected SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();

    @Override
    public void onExpired() {
        goToLoginPage.setValue(true);
    }

    public SingleLiveEvent<Boolean> getGoToLoginPage() {
        return goToLoginPage;
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public SingleLiveEvent<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}
