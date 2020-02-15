package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.KuknosBankPayment;
import net.iGap.kuknos.service.model.KuknosAsset;
import net.iGap.kuknos.service.model.KuknosBalance;
import net.iGap.kuknos.service.model.KuknosFederation;
import net.iGap.kuknos.service.model.KuknosFeeModel;
import net.iGap.kuknos.service.model.KuknosHash;
import net.iGap.kuknos.service.model.KuknosOperationResponse;
import net.iGap.kuknos.service.model.KuknosPaymentResponse;
import net.iGap.kuknos.service.model.KuknosResponseModel;

public class PanelRepo {

    private UserRepo userRepo = new UserRepo();
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public PanelRepo() {
        Log.d("amini", "PanelRepo: " + userRepo.getAccountID() + "\n" + userRepo.getMnemonic() + "\n" + userRepo.getSeedKey() + "\n" + userRepo.getPIN());
        /*userRepo.setPIN("0000");
        userRepo.setSeedKey("SAQO3N7T5GBDBFV5LEOPDR4NWMSCU6PMKVGXUXC6JJAGJTUXSHN5A4IX");
        userRepo.setMnemonic("mesh february noise come loud own hand quiz cabin torch assault bundle");
        try {
            userRepo.generateKeyPairWithMnemonicAndPIN();
        } catch (WalletException e) {
            e.printStackTrace();
        }*/
    }

    public String getUserInfo() {
        return /*"\nSeed Key is: " + userRepo.getSeedKey()
                + */"\nPublic Key is: " + userRepo.getAccountID()
                + "\nPIN is: " + userRepo.getPIN()
                + "\nmnemonic is: " + userRepo.getMnemonic();
    }

    public void getAccountInfo(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBalance>> apiResponse) {
        kuknosAPIRepository.getUserAccount(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }

    public void getAssetData(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        kuknosAPIRepository.getAssetData(assetCode, handShakeCallback, apiResponse);
    }

    public void paymentUser(KuknosSendM model, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosHash>> apiResponse) {
        kuknosAPIRepository.paymentUser(model, handShakeCallback, apiResponse);
    }

    public void getUserHistory(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOperationResponse>> apiResponse) {
        kuknosAPIRepository.getUserHistory(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }

    public void getSpecificAssets(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        kuknosAPIRepository.getSpecificAssets(assetCode, handShakeCallback, apiResponse);
    }

    public void buyAsset(String assetCode, String assetAmount, String totalPrice, String description, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBankPayment>> apiResponse) {
        kuknosAPIRepository.buyAsset(userRepo.getAccountID(), assetCode, assetAmount, totalPrice, description, handShakeCallback, apiResponse);
    }

    public void getFee(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFeeModel>> apiResponse) {
        kuknosAPIRepository.getFees(handShakeCallback, apiResponse);
    }

    public void convertFederation(String username, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFederation>> apiResponse) {
        kuknosAPIRepository.convertFederation(username, handShakeCallback, apiResponse);
    }

    public void getPaymentData(String RRA, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosPaymentResponse>> apiResponse) {
        kuknosAPIRepository.getPaymentData(RRA, handShakeCallback, apiResponse);
    }

    public boolean isPinSet() {
        return userRepo.getPIN() != null && userRepo.getPIN().length() == 4;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
