package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;
import android.util.Log;

import net.iGap.R;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;

import java.util.ArrayList;

public class KuknosTradeVM extends ViewModel {

    private MutableLiveData<KuknosWalletsAccountM> kuknosOriginWalletsM;
    private MutableLiveData<KuknosWalletsAccountM> kuknosDestinationWalletsM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> fetchProgressState;
    private MutableLiveData<Boolean> sendProgressState;

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private ObservableField<String> originAmount = new ObservableField<>();
    private ObservableField<String> destAmount = new ObservableField<>();
    private int position = 0;

    public KuknosTradeVM() {
        //TODO clear Hard Code
        balance.set("...");
        currency.set("PMN");

        if (kuknosOriginWalletsM == null) {
            kuknosOriginWalletsM = new MutableLiveData<KuknosWalletsAccountM>();
            kuknosOriginWalletsM.setValue(new KuknosWalletsAccountM());
        }
        if (kuknosDestinationWalletsM == null) {
            kuknosDestinationWalletsM = new MutableLiveData<KuknosWalletsAccountM>();
            kuknosDestinationWalletsM.setValue(new KuknosWalletsAccountM());
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (fetchProgressState == null) {
            fetchProgressState = new MutableLiveData<Boolean>();
            fetchProgressState.setValue(false);
        }
        if (sendProgressState == null) {
            sendProgressState = new MutableLiveData<Boolean>();
            sendProgressState.setValue(false);
        }
    }

    private void generateFakedata() {
        // hard code
        KuknosWalletBalanceInfoM temp = new KuknosWalletBalanceInfoM("200.00",
                "curr1", "PMN",
                "www.google.com");
        KuknosWalletBalanceInfoM temp2 = new KuknosWalletBalanceInfoM("300.00",
                "curr1", "Doller",
                "https://cdn1.vectorstock.com/i/1000x1000/45/45/dollar-sign-icon-usd-currency-symbol-vector-2874545.jpg");
        KuknosWalletBalanceInfoM temp3 = new KuknosWalletBalanceInfoM("220.00",
                "curr1", "Euro",
                "www.google.com");
        KuknosWalletBalanceInfoM temp4 = new KuknosWalletBalanceInfoM("110.00",
                "curr1", "SHHL",
                "https://png.pngtree.com/element_our/20190528/ourlarge/pngtree-url-small-icon-opened-in-the-browser-image_1132270.jpg");

        ArrayList<KuknosWalletBalanceInfoM> Ttemp = new ArrayList<>();
        Ttemp.add(temp);
        Ttemp.add(temp2);
        Ttemp.add(temp3);
        Ttemp.add(temp4);
        KuknosWalletsAccountM Atemp = new KuknosWalletsAccountM("", "", Ttemp);
        kuknosOriginWalletsM.setValue(Atemp);
        originSpinnerSelect(0);
    }

    public void getDataFromServer() {
        fetchProgressState.setValue(true);
        // TODO Hard code in here baby
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateFakedata();
                fetchProgressState.setValue(false);
            }
        }, 1000);
    }

    public void originSpinnerSelect(int position) {
        Log.d("amini", "originSpinnerSelect: " + position);
        this.position = position;
        KuknosWalletBalanceInfoM temp = kuknosOriginWalletsM.getValue().getBalanceInfo().get(position);
        balance.set(temp.getBalance());
        currency.set(temp.getAssetCode());
        destSpinnerSelect(position);
    }

    public void destSpinnerSelect(int position) {
        ArrayList<KuknosWalletBalanceInfoM> Ttemp = new ArrayList<>();
        Ttemp.addAll(kuknosOriginWalletsM.getValue().getBalanceInfo());
        KuknosWalletsAccountM Atemp = new KuknosWalletsAccountM("", "", Ttemp);
        Atemp.getBalanceInfo().remove(position);
        kuknosDestinationWalletsM.setValue(Atemp);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendProgressState.setValue(false);
                // success
                error.setValue(new ErrorM(false, "success submission", "2", R.string.kuknos_trade_success));
                // fail
                //error.setValue(new ErrorM(true, "fail during submission", "2", R.string.kuknos_trade_fail));
            }
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

    public MutableLiveData<KuknosWalletsAccountM> getKuknosOriginWalletsM() {
        return kuknosOriginWalletsM;
    }

    public void setKuknosOriginWalletsM(MutableLiveData<KuknosWalletsAccountM> kuknosOriginWalletsM) {
        this.kuknosOriginWalletsM = kuknosOriginWalletsM;
    }

    public MutableLiveData<KuknosWalletsAccountM> getKuknosDestinationWalletsM() {
        return kuknosDestinationWalletsM;
    }

    public void setKuknosDestinationWalletsM(MutableLiveData<KuknosWalletsAccountM> kuknosDestinationWalletsM) {
        this.kuknosDestinationWalletsM = kuknosDestinationWalletsM;
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
}
