package net.iGap.viewmodel.kuknos;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.PanelRepo;
import net.iGap.model.kuknos.ErrorM;
import net.iGap.model.kuknos.KuknosSendM;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.model.kuknos.Parsian.KuknosHash;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.module.SingleLiveEvent;

import org.stellar.sdk.KeyPair;

public class KuknosSendVM extends BaseAPIViewModel {

    private KuknosSendM kuknosSendM;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<ErrorM> payResult;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> walletID = new ObservableField<>();
    private ObservableField<String> text = new ObservableField<>();
    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private KuknosBalance.Balance balanceInfoM;
    private MutableLiveData<Boolean> openQrScanner;
    private SingleLiveEvent<Boolean> goToPin = new SingleLiveEvent<>();
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosSendVM() {
        kuknosSendM = new KuknosSendM();
        errorM = new MutableLiveData<>();
        payResult = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        openQrScanner = new MutableLiveData<>();
        openQrScanner.setValue(false);
    }

    public void QrcodeScan() {
        openQrScanner.setValue(true);
    }

    public void sendCredit() {

        if (!checkWalletID() || !checkAmount()) {
            return;
        }
        goToPin.setValue(true);

    }

    private boolean checkWalletID() {
        if (walletID.get() == null) {
            errorM.setValue(new ErrorM(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError));
            return false;
        }
        if (walletID.get().isEmpty() || walletID.get().length() == 0) {
            errorM.setValue(new ErrorM(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError));
            return false;
        }
        if (!checkKeyPairExsit()) {
            errorM.setValue(new ErrorM(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError2));
            return false;
        }
        return true;
    }

    private boolean checkKeyPairExsit() {
        try {
            KeyPair keyPair = KeyPair.fromAccountId(walletID.get());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkAmount() {
        if (amount.get() == null) {
            errorM.setValue(new ErrorM(true, "Invalid WalletID", "1", R.string.kuknos_send_AmountError));
            return false;
        }
        if (amount.get().isEmpty() || amount.get().length() == 0) {
            errorM.setValue(new ErrorM(true, "Invalid WalletID", "1", R.string.kuknos_send_AmountError));
            return false;
        }
        try {
            Double reqAmount = Double.valueOf(amount.get());
            Double maxCredit = Double.valueOf(balanceInfoM.getBalance());
            if (reqAmount < (maxCredit - 1)) {
                return true;
            } else {
                errorM.setValue(new ErrorM(true, "Not Enough Credit", "1", R.string.kuknos_send_CreditError));
                return false;
            }
        } catch (Exception e) {
            // Format is wrong
            errorM.setValue(new ErrorM(true, "Invalid Amount Format", "1", R.string.kuknos_send_AmountFromatError));
            return false;
        }
    }

    public void sendDataServer() {
        kuknosSendM.setAmount(amount.get());
        kuknosSendM.setSrc(panelRepo.getUserRepo().getSeedKey());
        kuknosSendM.setDest(walletID.get());
        kuknosSendM.setMemo((text.get() == null ? "" : text.get()));

        progressState.setValue(true);
        panelRepo.paymentUser(kuknosSendM, this, new ResponseCallback<KuknosResponseModel<KuknosHash>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosHash> data) {
//                if (data.getData().isSuccess())
                    payResult.setValue(new ErrorM(false, "", "", R.string.kuknos_send_successServer));
                /*else {
                    //TransactionResult.TransactionResultResult.
                    data.getData().getExtras().getResultCodes().getTransactionResultCode();
                }*/
                progressState.setValue(false);
            }

            @Override
            public void onError(String error) {
                if (error == null)
                    payResult.setValue(new ErrorM(true, "", "", R.string.kuknos_send_errorServer));
                else
                    payResult.setValue(new ErrorM(true, "", error, 0));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                payResult.setValue(new ErrorM(true, "", "", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

        });
    }

    // Setter and Getter

    public String getAssetCode() {
        return kuknosSendM.getAssetcode();
    }

    public ObservableField<String> getWalletID() {
        return walletID;
    }

    public ObservableField<String> getText() {
        return text;
    }

    public void setText(ObservableField<String> text) {
        this.text = text;
    }

    public ObservableField<String> getAmount() {
        return amount;
    }

    public void setAmount(ObservableField<String> amount) {
        this.amount = amount;
    }

    public MutableLiveData<ErrorM> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorM> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<ErrorM> getPayResult() {
        return payResult;
    }

    public void setBalanceInfoM(String balanceInfoM) {
        Gson gson = new Gson();
        this.balanceInfoM = gson.fromJson(balanceInfoM, KuknosBalance.Balance.class);
        this.currency.set((this.balanceInfoM.getAsset().getType().equals("native") ? "PMN" : this.balanceInfoM.getAssetCode()));
    }

    public ObservableField<String> getCurrency() {
        return currency;
    }

    public void setCurrency(ObservableField<String> currency) {
        this.currency = currency;
    }

    public MutableLiveData<Boolean> getOpenQrScanner() {
        return openQrScanner;
    }

    public SingleLiveEvent<Boolean> getGoToPin() {
        return goToPin;
    }
}
