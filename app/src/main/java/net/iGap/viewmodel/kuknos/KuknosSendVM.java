package net.iGap.viewmodel.kuknos;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.model.kuknos.KuknosSendM;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.model.kuknos.Parsian.KuknosFederation;
import net.iGap.model.kuknos.Parsian.KuknosHash;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.PanelRepo;

import org.stellar.sdk.KeyPair;

import java.nio.charset.StandardCharsets;

public class KuknosSendVM extends BaseAPIViewModel {

    private KuknosSendM kuknosSendM;
    private MutableLiveData<KuknosError> errorM;
    private MutableLiveData<KuknosError> payResult;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> changeHint;
    private ObservableField<String> walletID = new ObservableField<>();
    private ObservableField<String> text = new ObservableField<>();
    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private ObservableField<Integer> federationProgressVisibility = new ObservableField<>(View.GONE);
    private KuknosBalance.Balance balanceInfoM;
    private MutableLiveData<Boolean> openQrScanner;
    private SingleLiveEvent<Boolean> goToPin = new SingleLiveEvent<>();
    private PanelRepo panelRepo = new PanelRepo();

    private ObservableField<String> walletIdResponse = new ObservableField<>("");
    private ObservableField<Integer> walletIdVisibility = new ObservableField<>(View.GONE);

    public enum Mode {PUBLIC_KEY, KUKNOS_ID}

    private Mode mode;

    public KuknosSendVM() {
        kuknosSendM = new KuknosSendM();
        errorM = new MutableLiveData<>();
        payResult = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
        openQrScanner = new MutableLiveData<>();
        openQrScanner.setValue(false);
        mode = Mode.PUBLIC_KEY;
        changeHint = new MutableLiveData<>(R.string.kuknos_send_walletAddress_Hint1);
    }

    public void QrcodeScan() {
        openQrScanner.setValue(true);
    }

    public void sendCredit() {

        if (!checkAmount() || !checkMemo() || !checkWalletID(true)) {
            return;
        }
        goToPin.setValue(true);

    }

    private boolean checkMemo() {
        String memo = "TRANSFER: " + (text.get() == null ? "" : text.get());
        if (memo.getBytes(StandardCharsets.UTF_8).length > 28) {
            errorM.setValue(new KuknosError(true, "Invalid Memo", "2", R.string.kuknos_send_memoError));
            return false;
        }
        return true;
    }

    public boolean checkWalletID(boolean isFromBtn) {
        if (walletID.get() == null) {
            errorM.setValue(new KuknosError(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError));
            return false;
        }
        if (walletID.get().isEmpty() || walletID.get().length() == 0) {
            errorM.setValue(new KuknosError(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError));
            return false;
        }
        if (mode == Mode.KUKNOS_ID) {
            convertFederation(isFromBtn);
            return false;
        }
        if (!checkKeyPairExsit()) {
            errorM.setValue(new KuknosError(true, "Invalid WalletID", "0", R.string.kuknos_send_walletIDError2));
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
            errorM.setValue(new KuknosError(true, "Invalid WalletID", "1", R.string.kuknos_send_AmountError));
            return false;
        }
        if (amount.get().isEmpty() || amount.get().length() == 0) {
            errorM.setValue(new KuknosError(true, "Invalid WalletID", "1", R.string.kuknos_send_AmountError));
            return false;
        }
        try {
            Double reqAmount = Double.valueOf(amount.get());
            Double maxCredit = Double.valueOf(balanceInfoM.getBalance());
            if (reqAmount < (maxCredit - 1)) {
                return true;
            } else {
                errorM.setValue(new KuknosError(true, "Not Enough Credit", "1", R.string.kuknos_send_CreditError));
                return false;
            }
        } catch (Exception e) {
            // Format is wrong
            errorM.setValue(new KuknosError(true, "Invalid Amount Format", "1", R.string.kuknos_send_AmountFromatError));
            return false;
        }
    }

    private void convertFederation(boolean isFromBtn) {
        String id = walletID.get();
        String domain = "pdpco.ir";
        if (walletID.get() == null) {
            return;
        }
        if (walletID.get().contains("*")) {
            id = walletID.get().substring(0, walletID.get().indexOf("*"));
            domain = walletID.get().substring(walletID.get().indexOf("*") + 1);
        }
        federationProgressVisibility.set(View.VISIBLE);
        panelRepo.convertFederation(id, domain, this, new ResponseCallback<KuknosResponseModel<KuknosFederation>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosFederation> data) {
                if (data.getData().getPublicKey() != null && data.getData().getPublicKey().length() > 50) {
                    walletIdResponse.set(data.getData().getPublicKey());
                    walletIdVisibility.set(View.VISIBLE);
                }
                federationProgressVisibility.set(View.GONE);
                if (isFromBtn)
                    goToPin.setValue(true);
            }

            @Override
            public void onError(String error) {
                federationProgressVisibility.set(View.GONE);
                errorM.setValue(new KuknosError(true, "Invalid WalletID", error, 0));
            }

            @Override
            public void onFailed() {
                federationProgressVisibility.set(View.GONE);
            }
        });
    }

    public void cancelFederation() {
        federationProgressVisibility.set(View.GONE);
    }

    public void sendDataServer() {
        kuknosSendM.setAmount(amount.get());
        kuknosSendM.setSrc(panelRepo.getUserRepo().getSeedKey());
        if (mode == Mode.KUKNOS_ID)
            kuknosSendM.setDest(walletIdResponse.get());
        else
            kuknosSendM.setDest(walletID.get());
        kuknosSendM.setAssetCode(balanceInfoM.getAssetCode());
        kuknosSendM.setAssetInssuer(balanceInfoM.getAssetIssuer());
        kuknosSendM.setMemo("TRANSFER: " + (text.get() == null ? "" : text.get()));

        progressState.setValue(true);
        panelRepo.paymentUser(kuknosSendM, this, new ResponseCallback<KuknosResponseModel<KuknosHash>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosHash> data) {
//                if (data.getData().isSuccess())
                payResult.setValue(new KuknosError(false, "", "", R.string.kuknos_send_successServer));
                /*else {
                    //TransactionResult.TransactionResultResult.
                    data.getData().getExtras().getResultCodes().getTransactionResultCode();
                }*/
                progressState.setValue(false);
            }

            @Override
            public void onError(String error) {
                if (error == null)
                    payResult.setValue(new KuknosError(true, "", "", R.string.kuknos_send_errorServer));
                else
                    payResult.setValue(new KuknosError(true, "", error, 0));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                payResult.setValue(new KuknosError(true, "", "", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

        });
    }

    public void onModeChange(int position) {
        if (position == 0 & mode != Mode.PUBLIC_KEY) {
            mode = Mode.PUBLIC_KEY;
            changeHint.setValue(R.string.kuknos_send_walletAddress_Hint1);
        } else if (position == 1 & mode != Mode.KUKNOS_ID) {
            mode = Mode.KUKNOS_ID;
            changeHint.setValue(R.string.kuknos_send_walletAddress_Hint2);
        }
    }

    // Setter and Getter

    public String getAssetCode() {
        return kuknosSendM.getAssetCode();
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

    public MutableLiveData<KuknosError> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<KuknosError> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<KuknosError> getPayResult() {
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

    public ObservableField<Integer> getFederationProgressVisibility() {
        return federationProgressVisibility;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public MutableLiveData<Integer> getChangeHint() {
        return changeHint;
    }

    public ObservableField<String> getWalletIdResponse() {
        return walletIdResponse;
    }

    public ObservableField<Integer> getWalletIdVisibility() {
        return walletIdVisibility;
    }

    public void setWalletIdVisibility(Integer walletIdVisibility) {
        this.walletIdVisibility.set(walletIdVisibility);
    }
}
