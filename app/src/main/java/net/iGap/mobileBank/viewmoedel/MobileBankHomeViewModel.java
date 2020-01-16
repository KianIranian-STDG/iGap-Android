package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

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

    private List<BankCardModel> cards;

    public MobileBankHomeViewModel() {
        //add init repository
        showLoading.set(View.VISIBLE);
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
            }

            @Override
            public void onFailed() {
                showLoading.set(View.GONE);
            }
        });
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
}
