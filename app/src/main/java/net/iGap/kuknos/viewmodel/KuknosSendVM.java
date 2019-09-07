package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.xdr.TransactionResult;
import org.stellar.sdk.xdr.TransactionResultCode;

import java.io.IOException;

public class KuknosSendVM extends ViewModel {

    private KuknosSendM kuknosSendM;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<ErrorM> payResult;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> walletID = new ObservableField<>();
    private ObservableField<String> text = new ObservableField<>();
    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private AccountResponse.Balance balanceInfoM;
    private MutableLiveData<Boolean> openQrScanner;
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosSendVM() {
        if (kuknosSendM == null)
            kuknosSendM = new KuknosSendM();
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
        }
        catch (Exception e) {
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
        kuknosSendM.setAmount(amount.get());
        kuknosSendM.setSrc(panelRepo.getUserRepo().getSeedKey());
        kuknosSendM.setDest(walletID.get());
        kuknosSendM.setMemo((text.get()==null ? "" : text.get()));

        Gson gson = new Gson();

        panelRepo.paymentUser(kuknosSendM, new ApiResponse<SubmitTransactionResponse>() {
            @Override
            public void onResponse(SubmitTransactionResponse accountResponse) {
                if (accountResponse.isSuccess())
                    payResult.setValue(new ErrorM(false, "", "", R.string.kuknos_send_successServer));
                else {
                    //TransactionResult.TransactionResultResult.
                    accountResponse.getExtras().getResultCodes().getTransactionResultCode();
                }
            }

            @Override
            public void onFailed(String error) {
                if (error == null || error.length() == 0)
                    payResult.setValue(new ErrorM(true, "", "", R.string.kuknos_send_errorServer));
                else
                    payResult.setValue(new ErrorM(true, "", "", Integer.parseInt(error)));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    // Setter and Getter

    public String getAssetCode() {
        return kuknosSendM.getAssetcode();
    }

    public void setAssetCode(String assetCode) {
        kuknosSendM = new KuknosSendM();
    }

    public KuknosSendM getKuknosSendM() {
        return kuknosSendM;
    }

    public void setKuknosSendM(KuknosSendM kuknosSendM) {
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

    public void setBalanceInfoM(String balanceInfoM) {
        Gson gson = new Gson();
        this.balanceInfoM = gson.fromJson(balanceInfoM, AccountResponse.Balance.class);
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

    public void setOpenQrScanner(MutableLiveData<Boolean> openQrScanner) {
        this.openQrScanner = openQrScanner;
    }
}
