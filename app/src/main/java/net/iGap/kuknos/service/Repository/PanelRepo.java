package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.model.KuknosSendM;

import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import java.io.IOException;

import io.realm.Realm;

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

    public void getAccountInfo(ApiResponse<AccountResponse> apiResponse){
        kuknosAPIRepository.getUserAccount(userRepo.getAccountID(), apiResponse);
    }

    public void paymentUser(KuknosSendM model, ApiResponse<SubmitTransactionResponse> apiResponse){
        kuknosAPIRepository.paymentUser(model, apiResponse);
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
