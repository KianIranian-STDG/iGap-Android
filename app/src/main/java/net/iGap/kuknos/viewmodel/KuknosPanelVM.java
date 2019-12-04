package net.iGap.kuknos.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;

import org.stellar.sdk.responses.AccountResponse;

import java.text.DecimalFormat;

public class KuknosPanelVM extends ViewModel {

    private MutableLiveData<AccountResponse> kuknosWalletsM;
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> openPage;
    private PanelRepo panelRepo = new PanelRepo();

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    private int position = 0;

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
        /*panelRepo.getAccountInfo(new ApiResponse<AccountResponse>() {
            @Override
            public void onResponse(AccountResponse accountResponse) {
                kuknosWalletsM.setValue(accountResponse);
                spinnerSelect(0);
            }

            @Override
            public void onFailed(String errorM) {
                balance.set("0.0");
                currency.set("currency");
                error.setValue(new ErrorM(true, "Fail to get data", "0", 0));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });*/
    }

    public String convertToJSON(int position) {
        Gson gson = new Gson();
        return gson.toJson(kuknosWalletsM.getValue().getBalances()[position]);
    }

    public void spinnerSelect(int position) {
        this.position = position;
        AccountResponse.Balance temp = kuknosWalletsM.getValue().getBalances()[position];
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

    public MutableLiveData<AccountResponse> getKuknosWalletsM() {
        return kuknosWalletsM;
    }

    public void setKuknosWalletsM(MutableLiveData<AccountResponse> kuknosWalletsM) {
        this.kuknosWalletsM = kuknosWalletsM;
    }
}
