package net.iGap.mobileBank.viewmoedel;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.BuildConfig;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.view.MobileBankHomeTabFragment;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeTabViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<List<BankCardModel>> cardsData = new MutableLiveData<>();
    private MutableLiveData<List<BankAccountModel>> accountsData = new MutableLiveData<>();
    private MutableLiveData<List<String>> shebaListener = new MutableLiveData<>();
    private ObservableInt showRetry = new ObservableInt(View.GONE);
    public List<BankCardModel> cards;
    public List<BankAccountModel> accounts;
    private MobileBankHomeTabFragment.HomeTabMode mMode;

    public MobileBankHomeTabViewModel() {
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

    public void getShebaNumber(String cardNumber) {
        if (BuildConfig.DEBUG) Log.e("NazariSheba", cardNumber);
        if (cardNumber == null) {
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumber(cardNumber, this, new ResponseCallback<BaseMobileBankResponse<List<String>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<String>> data) {
                shebaListener.postValue(data.getData());
                if (BuildConfig.DEBUG) Log.e("NazariSheba", data.getData().get(0));
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

    public void onRetryClicked() {
        if (mMode == MobileBankHomeTabFragment.HomeTabMode.CARD) {
            getCardsByApi();
        } else if (mMode == MobileBankHomeTabFragment.HomeTabMode.DEPOSIT) {
            getDepositsByApi();
        }
    }

    public void getShebaNumberByDeposit(String deposit) {
        if (deposit == null) {
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumberByDeposit(deposit, this, new ResponseCallback<BaseMobileBankResponse<BankShebaModel>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankShebaModel> data) {
                List<String> shebaList = new ArrayList<>();
                shebaList.add(data.getData().getSheba());
                shebaListener.postValue(shebaList);
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

    public MutableLiveData<List<BankAccountModel>> getAccountsData() {
        return accountsData;
    }

    public MutableLiveData<List<String>> getShebaListener() {
        return shebaListener;
    }

    public MutableLiveData<List<BankCardModel>> getCardsData() {
        return cardsData;
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }

    public void setFragmentState(MobileBankHomeTabFragment.HomeTabMode mode) {
        mMode = mode;
        if (mode == MobileBankHomeTabFragment.HomeTabMode.CARD) {
            getCardsByApi();
        } else if (mode == MobileBankHomeTabFragment.HomeTabMode.DEPOSIT) {
            getDepositsByApi();
        } else {
            showLoading.set(View.GONE);
            showRetry.set(View.GONE);
        }
    }
}
