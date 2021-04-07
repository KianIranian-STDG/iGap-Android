package net.iGap.repository;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.CPayApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
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

    public void getPlaqueInfo(PlaqueBodyModel body, HandShakeCallback handShakeCallback, ResponseCallback<PlaqueInfoModel> callback) {

        new ApiInitializer<PlaqueInfoModel>().initAPI(api.getPlaqueInfo(body), handShakeCallback, callback);

    }

    public void getAllUserPlaques(HandShakeCallback handShakeCallback, ResponseCallback<UserPlaquesModel> callback) {

        new ApiInitializer<UserPlaquesModel>().initAPI(api.getUserPlaques(), handShakeCallback, callback);

    }

    public void registerNewPlaque(RegisterPlaqueBodyModel body, HandShakeCallback handShakeCallback, ResponseCallback<RegisterPlaqueModel> callback) {

        new ApiInitializer<RegisterPlaqueModel>().initAPI(api.getRegisterNewPlaque(body), handShakeCallback, callback);

    }

    public void getWalletAmount(PlaqueBodyModel body, HandShakeCallback handShakeCallback, ResponseCallback<CPayWalletAmountModel> callback) {

        new ApiInitializer<CPayWalletAmountModel>().initAPI(api.getCPayWalletAmount(body), handShakeCallback, callback);

    }

    public void getChargeWallet(ChargeWalletBodyModel body, HandShakeCallback handShakeCallback, ResponseCallback<ChargeWalletModel> callback) {

        new ApiInitializer<ChargeWalletModel>().initAPI(api.getChargeWallet(body), handShakeCallback, callback);

    }

    public MutableLiveData<Boolean> getPlaquesChangeListener() {
        return plaquesChangeListener;
    }

}
