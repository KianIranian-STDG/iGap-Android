package net.iGap.kuknos.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import java.text.DecimalFormat;

public class KuknosPanelVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosBalance> kuknosWalletsM;
    private KuknosAsset asset = null;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> openPage;
    private PanelRepo panelRepo = new PanelRepo();

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private int position = 0;
    private boolean inRialMode = false;

    public KuknosPanelVM() {
        //TODO clear Hard Code
        balance.set("...");
        currency.set("PMN");

        kuknosWalletsM = new MutableLiveData<>();
        //kuknosWalletsM.setValue(new AccountResponse("", Long.getLong("0")));
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        openPage = new MutableLiveData<>();
        openPage.setValue(-1);
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
            public void onError(String errorM) {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", 0));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", 0));
                progressState.setValue(false);
            }
        });
    }

    private void getAssetData(String assetCode) {
        asset = null;
        panelRepo.getAssetData(assetCode, this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                asset = data.getData();
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public boolean isPinSet() {
        return panelRepo.isPinSet();
    }

    public String convertToJSON(int position) {
        if (kuknosWalletsM.getValue() == null)
            return "";
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
        getAssetData(currency.get());
    }

    private void calculateToRial() {
        if (asset == null || asset.getAssets().size() == 0)
            return;
        KuknosBalance.Balance temp = kuknosWalletsM.getValue().getAssets().get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(
                        df.format(Double.valueOf(temp.getBalance()) * Double.valueOf(asset.getAssets().get(0).getSellRate()))) :
                (df.format(Double.valueOf(temp.getBalance()) * Double.valueOf(asset.getAssets().get(0).getSellRate()))));
        currency.set("Rial");
    }

    public void togglePrice() {
        if (inRialMode) {
            spinnerSelect(position);
            inRialMode = false;
        } else {
            calculateToRial();
            inRialMode = true;
        }
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

}
