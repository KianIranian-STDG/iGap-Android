package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;
import android.os.Handler;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;

import org.stellar.sdk.responses.AccountResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class KuknosTradeVM extends ViewModel {

    private MutableLiveData<ArrayList<AccountResponse.Balance>> kuknosOriginWalletsM;
    private MutableLiveData<ArrayList<AccountResponse.Balance>> kuknosDestinationWalletsM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> fetchProgressState;
    private MutableLiveData<Boolean> sendProgressState;
    private PanelRepo panelRepo = new PanelRepo();

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private ObservableField<String> originAmount = new ObservableField<>();
    private ObservableField<String> destAmount = new ObservableField<>();
    private int position = 0;

    public KuknosTradeVM() {
        //TODO clear Hard Code
        balance.set("...");
        currency.set("PMN");

        kuknosOriginWalletsM = new MutableLiveData<>();
        kuknosDestinationWalletsM = new MutableLiveData<>();
        error = new MutableLiveData<>();
        fetchProgressState = new MutableLiveData<>();
        fetchProgressState.setValue(false);
        sendProgressState = new MutableLiveData<>();
        sendProgressState.setValue(false);
    }

    public void getDataFromServer() {
        panelRepo.getAccountInfo(new ApiResponse<AccountResponse>() {
            @Override
            public void onResponse(AccountResponse accountResponse) {
                ArrayList<AccountResponse.Balance> temp = new ArrayList<>(Arrays.asList(accountResponse.getBalances()));
                kuknosOriginWalletsM.setValue(temp);
                originSpinnerSelect(0);
            }

            @Override
            public void onFailed(String errorM) {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", 0));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                fetchProgressState.setValue(visibility);
            }
        });
    }

    public void originSpinnerSelect(int position) {
        this.position = position;
        AccountResponse.Balance temp = Objects.requireNonNull(kuknosOriginWalletsM.getValue()).get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(temp.getBalance())))
                : df.format(Double.valueOf(temp.getBalance())));
        currency.set((temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode()));
        destSpinnerSelect(position);
    }

    private void destSpinnerSelect(int position) {
        ArrayList<AccountResponse.Balance> Ttemp = new ArrayList<>(Objects.requireNonNull(kuknosOriginWalletsM.getValue()));
        Ttemp.remove(position);
        kuknosDestinationWalletsM.setValue(Ttemp);
    }

    public void exchangeAction() {
        if (!checkEntry())
            return;
        sendDataServer();
    }

    private void sendDataServer() {
        sendProgressState.setValue(true);
        // TODO Hard code in here baby
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            sendProgressState.setValue(false);
            // success
            error.setValue(new ErrorM(false, "success submission", "2", R.string.kuknos_trade_success));
            // fail
            //error.setValue(new ErrorM(true, "fail during submission", "2", R.string.kuknos_trade_fail));
        }, 1000);
    }

    private boolean checkEntry() {
        if (originAmount.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty origin amount", "0", R.string.kuknos_trade_emptyOriginAmount));
            return false;
        }
        if (originAmount.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty origin amount", "0", R.string.kuknos_trade_emptyOriginAmount));
            return false;
        }
        if (Integer.parseInt(originAmount.get()) == 0) {
            error.setValue(new ErrorM(true, "zero origin fail", "0", R.string.kuknos_trade_zeroOriginAmount));
            return false;
        }
        if (destAmount.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty dest amount", "1", R.string.kuknos_trade_emptyDestAmount));
            return false;
        }
        if (destAmount.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty dest amount", "1", R.string.kuknos_trade_emptyDestAmount));
            return false;
        }
        if (Integer.parseInt(destAmount.get()) == 0) {
            error.setValue(new ErrorM(true, "zero dest fail", "1", R.string.kuknos_trade_zeroDestAmount));
            return false;
        }
        return true;
    }

    // getter and setter

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public ObservableField<String> getBalance() {
        return balance;
    }

    public void setBalance(ObservableField<String> balance) {
        this.balance = balance;
    }

    public ObservableField<String> getCurrency() {
        return currency;
    }

    public void setCurrency(ObservableField<String> currency) {
        this.currency = currency;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ObservableField<String> getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(ObservableField<String> originAmount) {
        this.originAmount = originAmount;
    }

    public ObservableField<String> getDestAmount() {
        return destAmount;
    }

    public void setDestAmount(ObservableField<String> destAmount) {
        this.destAmount = destAmount;
    }

    public MutableLiveData<Boolean> getFetchProgressState() {
        return fetchProgressState;
    }

    public void setFetchProgressState(MutableLiveData<Boolean> fetchProgressState) {
        this.fetchProgressState = fetchProgressState;
    }

    public MutableLiveData<Boolean> getSendProgressState() {
        return sendProgressState;
    }

    public void setSendProgressState(MutableLiveData<Boolean> sendProgressState) {
        this.sendProgressState = sendProgressState;
    }

    public MutableLiveData<ArrayList<AccountResponse.Balance>> getKuknosOriginWalletsM() {
        return kuknosOriginWalletsM;
    }

    public void setKuknosOriginWalletsM(MutableLiveData<ArrayList<AccountResponse.Balance>> kuknosOriginWalletsM) {
        this.kuknosOriginWalletsM = kuknosOriginWalletsM;
    }

    public MutableLiveData<ArrayList<AccountResponse.Balance>> getKuknosDestinationWalletsM() {
        return kuknosDestinationWalletsM;
    }

    public void setKuknosDestinationWalletsM(MutableLiveData<ArrayList<AccountResponse.Balance>> kuknosDestinationWalletsM) {
        this.kuknosDestinationWalletsM = kuknosDestinationWalletsM;
    }
}
