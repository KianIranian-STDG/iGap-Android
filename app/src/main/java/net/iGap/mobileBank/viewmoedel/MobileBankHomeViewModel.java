package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;

import java.util.List;

public class MobileBankHomeViewModel extends BaseMobileBankViewModel {

    public SingleLiveEvent<Boolean> onMoneyTransferListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTempPassListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onShebaListener = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> onTransactionListener = new SingleLiveEvent<>();
    private MutableLiveData<List<BankCardModel>> cardsData = new MutableLiveData<>();
    private MutableLiveData<List<BankAccountModel>> accountsData = new MutableLiveData<>();
    private MutableLiveData<BankShebaModel> shebaListener = new MutableLiveData<>();
    private ObservableInt showRetry = new ObservableInt(View.GONE);
    public MutableLiveData<Boolean> onTabChangeListener = new MutableLiveData<>();
    public ObservableBoolean isCardsMode = new ObservableBoolean(true);

    public List<BankCardModel> cards;
    public List<BankAccountModel> accounts;

    public MobileBankHomeViewModel() {
        getCardsByApi();
        getDepositsByApi();
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

    private void getDepositsByApi() {
        showLoading.set(View.VISIBLE);
        showRetry.set(View.GONE);
        MobileBankRepository.getInstance().getMobileBankAccounts(this, new ResponseCallback<BaseMobileBankResponse<List<BankAccountModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankAccountModel>> data) {
                accounts = data.getData();
                accountsData.postValue(data.getData());
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

    public void getShebaNumber(String cardNumber){
        if (cardNumber == null){
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumber(cardNumber, this, new ResponseCallback<BaseMobileBankResponse<BankShebaModel>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankShebaModel> data) {
                shebaListener.postValue(data.getData());
            }

            @Override
            public void onError(String error) {
                shebaListener.postValue(null);
            }

            @Override
            public void onFailed() {
                shebaListener.postValue(null);
            }
        });
    }

    public void onRetryClicked(){
        if (cards == null) getCardsByApi();
        if (accounts == null) getDepositsByApi();
    }

    public void onTabsClick(boolean isCards) {
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
        onShebaListener.setValue(true);
    }

    public MutableLiveData<List<BankAccountModel>> getAccountsData() {
        return accountsData;
    }

    public MutableLiveData<BankShebaModel> getShebaListener() {
        return shebaListener;
    }

    public MutableLiveData<List<BankCardModel>> getCardsData() {
        return cardsData;
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }

}
