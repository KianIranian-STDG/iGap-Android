package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankChequeBookListModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;

import java.util.List;

public class MobileBankChequesBookListViewModel extends BaseMobileBankViewModel {

    private ObservableInt showRetry = new ObservableInt(View.GONE);
    private ObservableInt noItemVisibility = new ObservableInt(View.GONE);
    private MutableLiveData<List<BankChequeBookListModel>> responseListener = new MutableLiveData<>();
    public List<BankChequeBookListModel> cheques;
    private String deposit;

    public void getCheques(String deposit) {
        this.deposit = deposit;
        setLoaderState(true);
        MobileBankRepository.getInstance().getChequeBookList(deposit, this, new ResponseCallback<BaseMobileBankResponse<List<BankChequeBookListModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankChequeBookListModel>> data) {
                showLoading.set(View.GONE);
                cheques = data.getData();
                responseListener.postValue(cheques);
            }

            @Override
            public void onError(String error) {
                setLoaderState(false);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                setLoaderState(false);

            }
        });
    }

    private void setLoaderState(boolean state) {
        noItemVisibility.set(View.GONE);
        if (state) {
            showRetry.set(View.GONE);
            showLoading.set(View.VISIBLE);
        } else {
            showRetry.set(View.VISIBLE);
            showLoading.set(View.GONE);
        }
    }

    public void onRetryClicked() {
        if (deposit != null) getCheques(deposit);
    }

    public MutableLiveData<List<BankChequeBookListModel>> getResponseListener() {
        return responseListener;
    }

    public ObservableInt getNoItemVisibility() {
        return noItemVisibility;
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }
}
