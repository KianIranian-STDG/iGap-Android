package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;

import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.util.ArrayList;

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
        /*progressState.setValue(true);
        // TODO Hard code in here baby
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                KuknosWalletBalanceInfoM temp5 = new KuknosWalletBalanceInfoM("",
                        "", "Add Asset",
                        "www.google.com");

                ArrayList<KuknosWalletBalanceInfoM> Ttemp = new ArrayList<>();
                Ttemp.add(temp);
                Ttemp.add(temp2);
                Ttemp.add(temp3);
                Ttemp.add(temp4);
                Ttemp.add(temp5);
                KuknosWalletsAccountM Atemp = new KuknosWalletsAccountM("", "", Ttemp);
                kuknosWalletsM.setValue(Atemp);
                spinnerSelect(0);

                progressState.setValue(false);
            }
        }, 1000);*/
        panelRepo.getAccountInfo(new ApiResponse<AccountResponse>() {
            @Override
            public void onResponse(AccountResponse accountResponse) {
                Log.d("amini", "onResponse: " + accountResponse.getBalances().length + " " + accountResponse.getBalances()[0].getBalance());
                kuknosWalletsM.setValue(accountResponse);
                //spinnerSelect(0);
            }

            @Override
            public void onFailed(String error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public String convertToJSON(int position) {
        Gson gson = new Gson();
        return gson.toJson(kuknosWalletsM.getValue().getBalances()[position]);
    }

    public void spinnerSelect(int position) {
        this.position = position;
        AccountResponse.Balance temp = kuknosWalletsM.getValue().getBalances()[position];
        balance.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp.getBalance()) : temp.getBalance());
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
