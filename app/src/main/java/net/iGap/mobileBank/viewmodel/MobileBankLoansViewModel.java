package net.iGap.mobileBank.viewmodel;


import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoanListModel;

import java.util.List;

public class MobileBankLoansViewModel extends BaseMobileBankViewModel {

    private ObservableInt showRetry = new ObservableInt(View.GONE);
    private ObservableInt noItemVisibility = new ObservableInt(View.GONE);
    private MutableLiveData<List<LoanListModel>> responseListener = new MutableLiveData<>();
    public List<LoanListModel> loans;


    public void getLoans() {
        setLoaderState(true);
        MobileBankRepository.getInstance().getLoanList(this, new ResponseCallback<BaseMobileBankResponse<List<LoanListModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<LoanListModel>> data) {
                setLoaderState(false);
                loans = data.getData();
                responseListener.postValue(loans);
            }

            @Override
            public void onError(String error) {
                setLoaderState(false);
                showRetry.set(View.VISIBLE);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                setLoaderState(false);
                showRetry.set(View.VISIBLE);
            }
        });
    }

    private void setLoaderState(boolean state) {
        if (state) {
            showRetry.set(View.GONE);
            showLoading.set(View.VISIBLE);
            noItemVisibility.set(View.GONE);
        } else {
            showRetry.set(View.GONE);
            showLoading.set(View.GONE);
            noItemVisibility.set(View.GONE);
        }
    }

    public void onRetryClicked() {
        getLoans();
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }

    public ObservableInt getNoItemVisibility() {
        return noItemVisibility;
    }

    public MutableLiveData<List<LoanListModel>> getResponseListener() {
        return responseListener;
    }
}
