package net.iGap.kuknos.Repository;

import android.util.Log;

import net.iGap.kuknos.Model.KuknosPaymentResponse;
import net.iGap.kuknos.Model.KuknosSendM;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosBankPayment;
import net.iGap.kuknos.Model.Parsian.KuknosFederation;
import net.iGap.kuknos.Model.Parsian.KuknosFeeModel;
import net.iGap.kuknos.Model.Parsian.KuknosHash;
import net.iGap.kuknos.Model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.Model.Parsian.KuknosOptionStatus;
import net.iGap.kuknos.Model.Parsian.KuknosRefundHistory;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.KuknosUserRefundResponse;
import net.iGap.kuknos.Model.Parsian.KuknosVirtualRefund;
import net.iGap.kuknos.Model.Parsian.Owners;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.util.List;

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
        return "\nSeed Key is: " + userRepo.getSeedKey()
                + "\nPublic Key is: " + userRepo.getAccountID()
                /*+ "\nPIN is: " + (userRepo.getPIN() != null ? userRepo.getPIN() : "")*/
                + "\nmnemonic is: " + (userRepo.getMnemonic() != null ? userRepo.getMnemonic() : "");
    }

    public void getAccountInfo(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBalance>> apiResponse) {
        kuknosAPIRepository.getUserAccount(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }

    public void getAssetData(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosAsset>> apiResponse) {
        kuknosAPIRepository.getAssetData(assetCode, handShakeCallback, apiResponse);
    }

    public void getRefundData(String assetCode, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosRefundModel>> apiResponse) {
        kuknosAPIRepository.getRefundInfo(assetCode, handShakeCallback,apiResponse);
    }

    public void getVirtualRefund(String publicKey, String assetCode, float assetCount, int amount, float fee, String hash, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosVirtualRefund>> apiResponse) {
        kuknosAPIRepository.getVirtualRefund(publicKey,assetCode, assetCount, amount, fee, hash, handShakeCallback, apiResponse);
    }

    public void getUserRefunds(String publicKey, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosRefundHistory>> apiResponse) {
        kuknosAPIRepository.getUserRefunds(publicKey, handShakeCallback, apiResponse);
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
    public void getIbanInfo(String iban,HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<Owners>> apiResponse) {
        kuknosAPIRepository.getIbanInfo(iban, handShakeCallback, apiResponse);
    }
    public void getUserInfoResponse(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>> apiResponse) {
        kuknosAPIRepository.getUserInfo(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }
    public void getUserRefundDetail(int refundNo,HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserRefundResponse>> apiResponse) {
        kuknosAPIRepository.getUserRefundDetail(userRepo.getAccountID(),refundNo, handShakeCallback, apiResponse);
    }
    public void updateUserInfo(KuknosUserInfoResponse userInfo,HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>> apiResponse) {
        kuknosAPIRepository.updateUserInfo(userRepo.getAccountID(),userInfo, handShakeCallback, apiResponse);
    }
    public void buyAsset(String assetCode, String assetAmount, String totalPrice, String description, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBankPayment>> apiResponse) {
        kuknosAPIRepository.buyAsset(userRepo.getAccountID(), assetCode, assetAmount, totalPrice, description, handShakeCallback, apiResponse);
    }

    public void getFee(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFeeModel>> apiResponse) {
        kuknosAPIRepository.getFees(handShakeCallback, apiResponse);
    }

    public void convertFederation(String username, String domain, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosFederation>> apiResponse) {
        kuknosAPIRepository.convertFederation(username, domain, handShakeCallback, apiResponse);
    }

    public void getPaymentData(String RRA, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosPaymentResponse>> apiResponse) {
        kuknosAPIRepository.getPaymentData(RRA, handShakeCallback, apiResponse);
    }

    public void setOptions(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>> apiResponse) {
        kuknosAPIRepository.setOptions(userRepo.getSeedKey(), handShakeCallback, apiResponse);
    }

    public void getAccountOptionsStatus(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosOptionStatus>> apiResponse) {
        kuknosAPIRepository.getAccountOptionsStatus(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }

    public boolean isPinSet() {
        return userRepo.getPIN() != null && userRepo.getPIN().length() == 4;
    }

    public boolean isMnemonicAvailable() {
        return userRepo.getMnemonic() != null;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
