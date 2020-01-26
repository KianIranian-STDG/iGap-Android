package net.iGap.mobileBank.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.view.MobileBankHomeTabFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeTabViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<List<BankCardModel>> cardsData = new MutableLiveData<>();
    private MutableLiveData<List<BankAccountModel>> accountsData = new MutableLiveData<>();
    private MutableLiveData<List<String>> shebaListener = new MutableLiveData<>();
    private MutableLiveData<String> balance = new MutableLiveData<>();
    private MutableLiveData<String> OTPmessage = new MutableLiveData<>();
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
                //todo:// delete when account changed
                RealmMobileBankCards.deleteAll();
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

    public void getAccountBalance(String depositNumber) {
        // set bills
        MobileBankRepository.getInstance().getAccountHistory(depositNumber, 0,
                null, null, this, new ResponseCallback<BaseMobileBankResponse<List<BankHistoryModel>>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<List<BankHistoryModel>> data) {
                        if (data.getData() != null && data.getData().size() != 0) {
                            balance.setValue(CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getData().get(0).getBalance()))));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
    }

    private String decimalFormatter(Double entry) {
        DecimalFormat df = new DecimalFormat(",###");
        return df.format(entry);
    }

    private String CompatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    public void getOTP(String cardNumber) {
        MobileBankRepository.getInstance().getOTP(cardNumber, this, new ResponseCallback<ErrorModel>() {
            @Override
            public void onSuccess(ErrorModel data) {
                OTPmessage.setValue(data.getMessage());
            }

            @Override
            public void onError(String error) {
                OTPmessage.setValue(error);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void getShebaNumber(String cardNumber) {
        if (cardNumber == null) {
            shebaListener.postValue(null);
            return;
        }
        MobileBankRepository.getInstance().getShebaNumber(cardNumber, this, new ResponseCallback<BaseMobileBankResponse<List<String>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<String>> data) {
                shebaListener.postValue(data.getData());
            }

            @Override
            public void onError(String error) {
                shebaListener.postValue(null);
                showRequestErrorMessage.setValue(error);
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
                showRequestErrorMessage.setValue(error);
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

    public MutableLiveData<String> getBalance() {
        return balance;
    }

    public MutableLiveData<String> getOTPmessage() {
        return OTPmessage;
    }
}
