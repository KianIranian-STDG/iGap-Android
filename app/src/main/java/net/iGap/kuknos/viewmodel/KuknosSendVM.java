package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;
import android.util.Log;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;

public class KuknosSendVM extends ViewModel {

    private MutableLiveData<KuknosSendM> kuknosSendM;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<ErrorM> payResult;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> walletID = new ObservableField<>();
    private ObservableField<String> text = new ObservableField<>();
    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private KuknosWalletBalanceInfoM balanceInfoM;
    private MutableLiveData<Boolean> openQrScanner;

    public KuknosSendVM() {
        if (kuknosSendM == null)
            kuknosSendM = new MutableLiveData<>();
        if (errorM == null)
            errorM = new MutableLiveData<>();
        if (payResult == null)
            payResult = new MutableLiveData<>();
        if (progressState == null) {
            progressState = new MutableLiveData<>();
            progressState.setValue(false);
        }
        if (openQrScanner == null) {
            openQrScanner = new MutableLiveData<>();
            openQrScanner.setValue(false);
        }
    }

    public void QrcodeScan() {
        openQrScanner.setValue(true);
    }

    public void sendCredit() {

        if (!checkWalletID() || !checkAmount()) {
            return;
        }
        sendDataServer();

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
        return true;
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
            }
            else {
                errorM.setValue(new ErrorM(true, "Not Enough Credit", "1", R.string.kuknos_send_CreditError));
                return false;
            }
        }
        catch (Exception e) {
            // Format is wrong
            errorM.setValue(new ErrorM(true, "Invalid Amount Format", "1", R.string.kuknos_send_AmountFromatError));
            return false;
        }
    }

    private void sendDataServer() {
        progressState.setValue(true);
        // TODO: send data to server
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressState.setValue(false);

                //success
                payResult.setValue(new ErrorM(false, "", "", R.string.kuknos_send_successServer));
                //error
                //payResult.setValue(new ErrorM(true, "", "", R.string.kuknos_send_errorServer));
            }
        }, 1000);
    }

    // Setter and Getter

    public String getAssetCode() {
        return kuknosSendM.getValue().getAssetcode();
    }

    public void setAssetCode(String assetCode) {
        kuknosSendM.setValue(new KuknosSendM("","","",assetCode));
    }

    public MutableLiveData<KuknosSendM> getKuknosSendM() {
        return kuknosSendM;
    }

    public void setKuknosSendM(MutableLiveData<KuknosSendM> kuknosSendM) {
        this.kuknosSendM = kuknosSendM;
    }

    public ObservableField<String> getWalletID() {
        return walletID;
    }

    public void setWalletID(ObservableField<String> walletID) {
        this.walletID = walletID;
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

    public void setPayResult(MutableLiveData<ErrorM> payResult) {
        this.payResult = payResult;
    }

    public KuknosWalletBalanceInfoM getBalanceInfoM() {
        return balanceInfoM;
    }

    public void setBalanceInfoM(KuknosWalletBalanceInfoM balanceInfoM) {
        this.balanceInfoM = balanceInfoM;
        this.currency.set(balanceInfoM.getAssetCode());
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

    public void setOpenQrScanner(MutableLiveData<Boolean> openQrScanner) {
        this.openQrScanner = openQrScanner;
    }
}
