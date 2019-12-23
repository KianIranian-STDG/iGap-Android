package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import java.text.DecimalFormat;

public class KuknosPanelVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosBalance> kuknosWalletsM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> openPage;
    private PanelRepo panelRepo = new PanelRepo();

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private int position = 0;

    private static final String TAG = "KuknosPanelVM";

    public KuknosPanelVM() {
        //TODO clear Hard Code
        balance.set("...");
        currency.set("PMN");

        if (kuknosWalletsM == null) {
            kuknosWalletsM = new MutableLiveData<>();
            //kuknosWalletsM.setValue(new AccountResponse("", Long.getLong("0")));
        }
        if (error == null) {
            error = new MutableLiveData<ErrorM>();
        }
        if (progressState == null) {
            progressState = new MutableLiveData<Boolean>();
        }
        if (openPage == null) {
            openPage = new MutableLiveData<Integer>();
            openPage.setValue(-1);
        }
    }

    public void getDataFromServer() {
        progressState.setValue(true);
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                //todo fix it here
                kuknosWalletsM.setValue(data.getData());
                spinnerSelect(0);
                progressState.setValue(false);
            }

            @Override
            public void onError(ErrorModel errorM) {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", 0));
                progressState.setValue(false);
            }
        });
    }

    public String convertToJSON(int position) {
        Gson gson = new Gson();
        return gson.toJson(kuknosWalletsM.getValue().getAssets().get(position));
    }

    public void spinnerSelect(int position) {
        this.position = position;
        KuknosBalance.Balance temp = kuknosWalletsM.getValue().getAssets().get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(temp.getBalance()))) : df.format(Double.valueOf(temp.getBalance())));
        currency.set((temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode()));
    }

    public void receiveW() {
        openPage.setValue(0);
    }

    public void sendW() {
        openPage.setValue(1);
    }

    public void historyW() {
        openPage.setValue(2);
    }

    public void goToSetting() {
        openPage.setValue(3);
    }

    public void goToTrading() {
        openPage.setValue(5);
    }

    public void goTOBuyPMN() {
        openPage.setValue(4);
    }

    public void goToKYC() {

    }

    public void goToDetail() {
        openPage.setValue(1);
    }

    public String getPrivateKeyData() {
        return panelRepo.getUserInfo();
    }

    // getter and setter

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<Integer> getOpenPage() {
        return openPage;
    }

    public void setOpenPage(MutableLiveData<Integer> openPage) {
        this.openPage = openPage;
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

    public MutableLiveData<KuknosBalance> getKuknosWalletsM() {
        return kuknosWalletsM;
    }

    public void setKuknosWalletsM(MutableLiveData<KuknosBalance> kuknosWalletsM) {
        this.kuknosWalletsM = kuknosWalletsM;
    }
}
