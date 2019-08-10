package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.apiService.ApiResponse;

import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

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
}
