package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;

import java.util.List;

public class MobileBankHomeViewModel extends BaseMobileBankViewModel {

    public SingleLiveEvent<Boolean> onMoneyTransferListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTempPassListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTransactionListener = new SingleLiveEvent<>();
    private MutableLiveData<List<BankCardModel>> cardsData = new MutableLiveData<>();
    private ObservableInt showRetry = new ObservableInt(View.GONE);
    public MutableLiveData<Boolean> onTabChangeListener = new MutableLiveData<>();
    public ObservableBoolean isCardsMode = new ObservableBoolean(true);

    private List<BankCardModel> cards;

    public MobileBankHomeViewModel() {
        getCardsByApi();
    }

    private void getCardsByApi() {
        showLoading.set(View.VISIBLE);
        showRetry.set(View.GONE);
        MobileBankRepository.getInstance().getMobileBankCards(this, new ResponseCallback<BaseMobileBankResponse<List<BankCardModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankCardModel>> data) {
                cards = data.getData();
                cardsData.setValue(cards);
                showLoading.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
                showLoading.set(View.GONE);
                showRetry.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                showLoading.set(View.GONE);
                showRetry.set(View.VISIBLE);
            }
        });
    }

    public void onRetryClicked(){
        getCardsByApi();
    }

    public void onTabsClick(boolean isCards){
        isCardsMode.set(isCards);
        onTabChangeListener.setValue(isCards);
    }

    public void OnTransferMoneyClicked() {
        onMoneyTransferListener.setValue(true);
    }

    public void OnTransactionsClicked() {
        onTransactionListener.setValue(true);
    }

    public void OnTemporaryPassClicked() {
        onTempPassListener.postValue(true);
    }

    public void OnShebaClicked() {
    }

    public MutableLiveData<List<BankCardModel>> getCardsData() {
        return cardsData;
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }

}
