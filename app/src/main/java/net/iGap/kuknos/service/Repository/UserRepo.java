package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.mnemonic.Wallet;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.KuknosLoginM;
import net.iGap.kuknos.service.model.KuknosRealmM;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import org.stellar.sdk.KeyPair;

import io.realm.Realm;

public class UserRepo {

    private Realm realm;
    private RealmUserInfo userInfo;
    private KuknosRealmM kuknosRealmM;
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public UserRepo() {
        updateUserInfo();
    }

    // API

    public void checkUser(String phoneNum, String nID, ApiResponse<KuknosLoginM> apiResponse) {
        kuknosAPIRepository.getUserAuthentication(phoneNum, nID, apiResponse);
    }

    public void getUserInfo(ApiResponse<KuknosInfoM> apiResponse) {
        kuknosAPIRepository.getUserInfo(apiResponse);
    }

    // generate key pair

    public void generateMnemonic(){
        try {
            char[] mnemonicTemp = Wallet.generate12WordMnemonic();
            /*String[] mnemonic = String.valueOf(mnemonicTemp).split(" ");
            String mnemonicS = "";
            for (String temp : mnemonic) {
                mnemonicS = mnemonicS.concat(temp + " ");
            }*/
            kuknosRealmM.updateMnemonic(String.valueOf(mnemonicTemp));
            Log.d("amini", "generateMnemonic: " + kuknosRealmM.getKuknosMnemonic());
        }
        catch (Exception e) {
            kuknosRealmM.updateMnemonic("-1");
        }
    }

    public void generateKeyPair() {
        KeyPair pair = KeyPair.random();
        kuknosRealmM.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public void generateKeyPairWithMnemonic() throws WalletException {
        Log.d("amini", "generateKeyPairWithMnemonic: " + kuknosRealmM.getKuknosMnemonic());
        KeyPair pair = Wallet.createKeyPair(kuknosRealmM.getKuknosMnemonic().toCharArray(), null,0);
        kuknosRealmM.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
        Log.d("amini", "generateKeyPairWithMnemonic: seed :" + new String(pair.getSecretSeed()));
        Log.d("amini", "generateKeyPairWithMnemonic: public :" + pair.getAccountId());
    }

    public void generateKeyPairWithMnemonicAndPIN() throws WalletException {
        KeyPair pair = Wallet.createKeyPair(kuknosRealmM.getKuknosMnemonic().toCharArray(), kuknosRealmM.getKuknosPIN().toCharArray(),0);
        kuknosRealmM.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public void generateKeyPairWithSeed() {
        KeyPair pair = KeyPair.fromSecretSeed(kuknosRealmM.getKuknosSeedKey());
        kuknosRealmM.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    // setter and gettter

    public String getMnemonic() {
        return kuknosRealmM.getKuknosMnemonic();
    }

    public void setMnemonic(String mnemonic) {
        kuknosRealmM.updateMnemonic(mnemonic);
    }

    public String getSeedKey() {
        return kuknosRealmM.getKuknosSeedKey();
    }

    public void setSeedKey(String seed) {
        kuknosRealmM.updateSeedKey(seed);
    }

    public String getAccountID() {
        return kuknosRealmM.getKuknosPublicKey();
    }

    public void setPIN(String pin) {
        kuknosRealmM.updatePIN(pin);
    }

    public String getPIN() {
        return kuknosRealmM.getKuknosPIN();
    }

    public String getUserNum() {
        return userInfo.getUserInfo().getPhoneNumber();
    }

    public String getUserFirstName() {
        return userInfo.getUserInfo().getDisplayName();
    }

    public String getUserLastName() {
        return userInfo.getUserInfo().getLastName();
    }

    public String getUserEmail() {
        return userInfo.getEmail();
    }

    public KuknosRealmM getKuknosRealmM() {
        return kuknosRealmM;
    }

    // Realm and Data

    public void deleteAccount() {
        userInfo.deleteKuknos();
    }

    private void updateUserInfo() {
        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        closeRealm();

        kuknosRealmM = userInfo.getKuknosM();
        if (kuknosRealmM == null) {
            userInfo.createKuknos();
            updateUserInfo();
            return;
        }

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    private void closeRealm() {
        realm.close();
    }

}
