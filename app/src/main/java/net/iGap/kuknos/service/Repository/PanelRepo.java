package net.iGap.kuknos.service.Repository;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.KuknosSendM;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosTransactionResult;

import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.operations.OperationResponse;

public class PanelRepo {

    private UserRepo userRepo = new UserRepo();
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public PanelRepo() {
    }

    public String getUserInfo() {
        return "\nSeed Key is: " + userRepo.getSeedKey()
                + "\nPublic Key is: " + userRepo.getAccountID()
                + "\nPIN is: " + userRepo.getPIN()
                + "\nmnemonic is: " + userRepo.getMnemonic();
    }

    public void getAccountInfo(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosBalance>> apiResponse) {
        kuknosAPIRepository.getUserAccount(userRepo.getAccountID(), handShakeCallback, apiResponse);
    }

    public void paymentUser(KuknosSendM model, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosTransactionResult>> apiResponse) {
        kuknosAPIRepository.paymentUser(model, handShakeCallback, apiResponse);
    }

    public void getUserHistory(ApiResponse<Page<OperationResponse>> apiResponse) {
        kuknosAPIRepository.getUserHistory(userRepo.getAccountID(), apiResponse);
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
