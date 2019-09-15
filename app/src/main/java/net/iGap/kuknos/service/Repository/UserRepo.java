package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.mnemonic.Wallet;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.KuknosInfoM;
import net.iGap.kuknos.service.model.RealmKuknos;
import net.iGap.kuknos.service.model.KuknosSubmitM;
import net.iGap.kuknos.service.model.KuknoscheckUserM;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import org.stellar.sdk.KeyPair;

import io.realm.Realm;

public class UserRepo {

    private Realm realm;
    private RealmUserInfo userInfo;
    private RealmKuknos realmKuknos;
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public UserRepo() {
        updateUserInfo();
    }

    // API

    public void checkUser(String phoneNum, String nID, ApiResponse<KuknoscheckUserM> apiResponse) {
        kuknosAPIRepository.checkUser(phoneNum, nID, apiResponse);
    }

    public void getUserInfo(String publicKey, ApiResponse<KuknosInfoM> apiResponse) {
        kuknosAPIRepository.getUserInfo(publicKey, apiResponse);
    }

    public void registerUser(String token, String publicKey, String friendlyID, ApiResponse<KuknosSubmitM> apiResponse) {
        kuknosAPIRepository.registerUser(token, publicKey, friendlyID, apiResponse);
    }

    // generate key pair

    public void generateMnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate12WordMnemonic();
            /*String[] mnemonic = String.valueOf(mnemonicTemp).split(" ");
            String mnemonicS = "";
            for (String temp : mnemonic) {
                mnemonicS = mnemonicS.concat(temp + " ");
            }*/
            RealmKuknos.updateMnemonic(String.valueOf(mnemonicTemp));
            Log.d("amini", "generateMnemonic: " + realmKuknos.getKuknosMnemonic());
        } catch (Exception e) {
            RealmKuknos.updateMnemonic("-1");
        }
    }

    public void generateKeyPair() {
        KeyPair pair = KeyPair.random();
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public void generateKeyPairWithMnemonic() throws WalletException {
        Log.d("amini", "generateKeyPairWithMnemonic: " + realmKuknos.getKuknosMnemonic());
        KeyPair pair = Wallet.createKeyPair(realmKuknos.getKuknosMnemonic().toCharArray(), null, 0);
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
        Log.d("amini", "generateKeyPairWithMnemonic: seed :" + new String(pair.getSecretSeed()));
        Log.d("amini", "generateKeyPairWithMnemonic: public :" + pair.getAccountId());
    }

    public void generateKeyPairWithMnemonicAndPIN() throws WalletException {
        KeyPair pair = Wallet.createKeyPair(realmKuknos.getKuknosMnemonic().toCharArray(), realmKuknos.getKuknosPIN().toCharArray(), 0);
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public void generateKeyPairWithSeed() {
        KeyPair pair = KeyPair.fromSecretSeed(realmKuknos.getKuknosSeedKey());
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    // setter and gettter

    public String getMnemonic() {
        return realmKuknos.getKuknosMnemonic();
    }

    public void setMnemonic(String mnemonic) {
        RealmKuknos.updateMnemonic(mnemonic);
    }

    public String getSeedKey() {
        // if -1 it's sign out. if null it's first time
        return realmKuknos.getKuknosSeedKey();
    }

    public void setSeedKey(String seed) {
        RealmKuknos.updateSeedKey(seed);
    }

    public String getAccountID() {
        return realmKuknos.getKuknosPublicKey();
    }

    public void setPIN(String pin) {
        RealmKuknos.updatePIN(pin);
    }

    public String getPIN() {
        return realmKuknos.getKuknosPIN();
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

    public RealmKuknos getRealmKuknos() {
        return realmKuknos;
    }

    // Realm and Data

    public void deleteAccount() {
        userInfo.deleteKuknos();
    }

    private void updateUserInfo() {
        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        closeRealm();

        realmKuknos = userInfo.getKuknosM();
        if (realmKuknos == null) {
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
