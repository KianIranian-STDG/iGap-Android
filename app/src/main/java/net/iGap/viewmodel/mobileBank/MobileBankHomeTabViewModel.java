package net.iGap.viewmodel.mobileBank;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.mobileBank.MobileBankHomeTabFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.model.mobileBank.BankAccountModel;
import net.iGap.model.mobileBank.BankCardModel;
import net.iGap.model.mobileBank.BankHistoryModel;
import net.iGap.model.mobileBank.BankNotificationStatus;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmMobileBankAccounts;
import net.iGap.realm.RealmMobileBankCards;
import net.iGap.repository.MobileBankRepository;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class MobileBankHomeTabViewModel extends BaseMobileBankMainAndHistoryViewModel {

    private MutableLiveData<List<BankCardModel>> cardsData = new MutableLiveData<>();
    private MutableLiveData<List<BankAccountModel>> accountsData = new MutableLiveData<>();
    private SingleLiveEvent<String> takeTurnListener = new SingleLiveEvent<>();
    private MutableLiveData<String> balance = new MutableLiveData<>();
    private ObservableInt showRetry = new ObservableInt(View.GONE);
    public List<BankCardModel> cards;
    public List<BankAccountModel> accounts;
    private MobileBankHomeTabFragment.HomeTabMode mMode;
    /**
     * this is the Notification mode for user to get it via igap
     * -1 : Not Available / 0: not active / 1: active
     */
    private SingleLiveEvent<Integer> notificationMode = new SingleLiveEvent<>();

    public MobileBankHomeTabViewModel() {
        notificationMode.setValue(-1);
    }

    private void getCardsByApi() {
        showLoading.set(View.VISIBLE);
        showRetry.set(View.GONE);
        MobileBankRepository.getInstance().getMobileBankCards(this, new ResponseCallback<BaseMobileBankResponse<List<BankCardModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankCardModel>> data) {
                //todo: delete when cards changed
                RealmMobileBankCards.deleteAll(() -> {
                    cards = data.getData();
                    Collections.sort(cards, (card1, card2) -> {
                        String ok = "OK";
                        if (card1 == null || card2 == null || card1.getCardStatus() == null || card2.getCardStatus() == null) {
                            return 1;
                        } else if (card1.getCardStatus().equals(ok) && card2.getCardStatus().equals(ok)) {
                            return 0;
                        } else if (card1.getCardStatus().equals(ok) && !card2.getCardStatus().equals(ok)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });
                    cardsData.setValue(cards);
                    showLoading.set(View.GONE);
                });
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
                //todo: delete when accounts changed
                RealmMobileBankAccounts.deleteAll(() -> {
                    accounts = data.getData();
                    Collections.sort(accounts, (dep1, dep2) -> {
                        String ok = "OPEN";
                        if (dep1 == null || dep2 == null || dep1.getStatus() == null || dep2.getStatus() == null) {
                            return 1;
                        } else if (dep1.getStatus().equals(ok) && dep2.getStatus().equals(ok)) {
                            return 0;
                        } else if (dep1.getStatus().equals(ok) && !dep2.getStatus().equals(ok)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });
                    accountsData.postValue(data.getData());
                    showLoading.set(View.GONE);
                });
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
                        } else {
                            balance.setValue("-1");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        balance.setValue("-1");
                        showRequestErrorMessage.setValue(error);
                    }

                    @Override
                    public void onFailed() {
                        balance.setValue("-1");
                    }
                });
    }

    public void getTakeTurnFromParsianBranches(){
        MobileBankRepository.getInstance().getTakeTurn(this , new ResponseCallback<BaseMobileBankResponse>() {
            @Override
            public void onSuccess(BaseMobileBankResponse data) {
                takeTurnListener.postValue(data.getMessage());
            }

            @Override
            public void onError(String error) {
                takeTurnListener.postValue(error);
            }

            @Override
            public void onFailed() {
                takeTurnListener.postValue(null);
            }
        });
    }

    public void getNotificationStatus() {
        MobileBankRepository.getInstance().getNotifStatus(this, new ResponseCallback<BaseMobileBankResponse<BankNotificationStatus>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankNotificationStatus> data) {
                notificationMode.setValue((data.getData().isStatus() ? 1 : 0));
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void toggleNotificationStatus(boolean activate) {
        MobileBankRepository.getInstance().changeNotifStatus(activate, this, new ResponseCallback<BaseMobileBankResponse>() {
            @Override
            public void onSuccess(BaseMobileBankResponse data) {
                notificationMode.setValue((activate ? 1 : 0));
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

    public void onRetryClicked() {
        if (mMode == MobileBankHomeTabFragment.HomeTabMode.CARD) {
            getCardsByApi();
        } else if (mMode == MobileBankHomeTabFragment.HomeTabMode.DEPOSIT) {
            getDepositsByApi();
        }
    }

    public MutableLiveData<List<BankAccountModel>> getAccountsData() {
        return accountsData;
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

    public SingleLiveEvent<String> getTakeTurnListener() {
        return takeTurnListener;
    }

    public SingleLiveEvent<Integer> getNotificationMode() {
        return notificationMode;
    }
}
