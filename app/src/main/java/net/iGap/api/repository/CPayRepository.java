package net.iGap.api.repository;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.CPayApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.cPay.CPayWalletAmountModel;
import net.iGap.model.cPay.ChargeWalletBodyModel;
import net.iGap.model.cPay.ChargeWalletModel;
import net.iGap.model.cPay.PlaqueBodyModel;
import net.iGap.model.cPay.PlaqueInfoModel;
import net.iGap.model.cPay.RegisterPlaqueBodyModel;
import net.iGap.model.cPay.RegisterPlaqueModel;
import net.iGap.model.cPay.UserPlaquesModel;

public class CPayRepository {

    private static CPayRepository instance;
    private CPayApi api;
    private MutableLiveData<Boolean> plaquesChangeListener = new MutableLiveData<>();

    private CPayRepository() {
        api = new RetrofitFactory().getCPayApi();
    }

    public static CPayRepository getInstance() {
        if (instance == null) {
            instance = new CPayRepository();
        }
        return instance;
    }

    public void getPlaqueInfo(PlaqueBodyModel body, ResponseCallback<PlaqueInfoModel> callback) {

        new ApiInitializer<PlaqueInfoModel>().initAPI(api.getPlaqueInfo(body), callback);

    }

    public void getAllUserPlaques(ResponseCallback<UserPlaquesModel> callback) {

        new ApiInitializer<UserPlaquesModel>().initAPI(api.getUserPlaques(), callback);

    }

    public void registerNewPlaque(RegisterPlaqueBodyModel body, ResponseCallback<RegisterPlaqueModel> callback) {

        new ApiInitializer<RegisterPlaqueModel>().initAPI(api.getRegisterNewPlaque(body), callback);

    }

    public void getWalletAmount(PlaqueBodyModel body, ResponseCallback<CPayWalletAmountModel> callback) {

        new ApiInitializer<CPayWalletAmountModel>().initAPI(api.getCPayWalletAmount(body), callback);

    }

    public void getChargeWallet(ChargeWalletBodyModel body, ResponseCallback<ChargeWalletModel> callback) {

        new ApiInitializer<ChargeWalletModel>().initAPI(api.getChargeWallet(body), callback);

    }

    public MutableLiveData<Boolean> getPlaquesChangeListener() {
        return plaquesChangeListener;
    }

}
