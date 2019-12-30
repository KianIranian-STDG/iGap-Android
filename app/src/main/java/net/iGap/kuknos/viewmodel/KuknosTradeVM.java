package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.Repository.TradeRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class KuknosTradeVM extends BaseAPIViewModel {

    private MutableLiveData<ArrayList<KuknosBalance.Balance>> kuknosOriginWalletsM;
    private MutableLiveData<ArrayList<KuknosBalance.Balance>> kuknosDestinationWalletsM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> fetchProgressState;
    private MutableLiveData<Boolean> sendProgressState;
    private PanelRepo panelRepo = new PanelRepo();
    private TradeRepo tradeRepo = new TradeRepo();

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private ObservableField<String> originAmount = new ObservableField<>();
    private ObservableField<String> destAmount = new ObservableField<>();
    private int originPosition = 0;
    private int destPosition = 0;

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
        fetchProgressState.setValue(true);
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                ArrayList<KuknosBalance.Balance> temp = new ArrayList<>();
                temp.addAll(data.getData().getAssets());
                kuknosOriginWalletsM.setValue(temp);
                originSpinnerSelect(0);
                fetchProgressState.setValue(false);
            }

            @Override
            public void onError(ErrorModel errorM) {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_trade_emptyOriginAmount));
                fetchProgressState.setValue(false);
            }
        });
    }

    public void originSpinnerSelect(int position) {
        this.originPosition = position;
        KuknosBalance.Balance temp = Objects.requireNonNull(kuknosOriginWalletsM.getValue()).get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(temp.getBalance())))
                : df.format(Double.valueOf(temp.getBalance())));
        currency.set((temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode()));
        destSpinnerSelect(position);
    }

    private void destSpinnerSelect(int position) {
        this.destPosition=0;
        ArrayList<KuknosBalance.Balance> Temp = new ArrayList<>(Objects.requireNonNull(kuknosOriginWalletsM.getValue()));
        Temp.remove(position);
        kuknosDestinationWalletsM.setValue(Temp);
    }

    public void exchangeAction() {
        if (!checkEntry())
            return;
        sendDataServer();
    }

    private void sendDataServer() {
        sendProgressState.setValue(true);
        double price = Double.valueOf(originAmount.get())/Double.valueOf(destAmount.get());
        Log.d("amini", "sendDataServer: " + price + " ");
        tradeRepo.manangeOffer(
                kuknosOriginWalletsM.getValue().get(originPosition).getAsset().getType().equals("native") ? "PMN" : kuknosOriginWalletsM.getValue().get(originPosition).getAssetCode(),
                kuknosOriginWalletsM.getValue().get(originPosition).getAssetIssuer(),
                kuknosDestinationWalletsM.getValue().get(destPosition).getAsset().getType().equals("native") ? "PMN" : kuknosDestinationWalletsM.getValue().get(destPosition).getAssetCode(),
                kuknosDestinationWalletsM.getValue().get(destPosition).getAssetIssuer(),
                destAmount.get(), Double.toString(price), this,
                new ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<SubmitTransactionResponse> data) {
                error.setValue(new ErrorM(false, "success submission", "2", R.string.kuknos_trade_success));
                sendProgressState.setValue(false);
            }

            @Override
            public void onError(ErrorModel errorM) {
                error.setValue(new ErrorM(true, "fail during submission", errorM.getName(), R.string.kuknos_trade_fail));
                sendProgressState.setValue(false);
            }
        });
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

    public MutableLiveData<ArrayList<KuknosBalance.Balance>> getKuknosOriginWalletsM() {
        return kuknosOriginWalletsM;
    }

    public void setKuknosOriginWalletsM(MutableLiveData<ArrayList<KuknosBalance.Balance>> kuknosOriginWalletsM) {
        this.kuknosOriginWalletsM = kuknosOriginWalletsM;
    }

    public MutableLiveData<ArrayList<KuknosBalance.Balance>> getKuknosDestinationWalletsM() {
        return kuknosDestinationWalletsM;
    }

    public void setKuknosDestinationWalletsM(MutableLiveData<ArrayList<KuknosBalance.Balance>> kuknosDestinationWalletsM) {
        this.kuknosDestinationWalletsM = kuknosDestinationWalletsM;
    }

    public void setDestPosition(int destPosition) {
        this.destPosition = destPosition;
    }
}
