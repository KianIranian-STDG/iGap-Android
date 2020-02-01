package net.iGap.kuknos.service.Repository;

import android.util.Log;

import net.iGap.DbManager;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.kuknos.service.mnemonic.Wallet;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.KuknosSignupM;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosUserInfo;
import net.iGap.kuknos.service.model.Parsian.KuknosUsernameStatus;
import net.iGap.kuknos.service.model.RealmKuknos;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import org.stellar.sdk.KeyPair;

public class UserRepo {

    private RealmUserInfo userInfo;
    private RealmKuknos realmKuknos;
    private KuknosAPIRepository kuknosAPIRepository = new KuknosAPIRepository();

    public UserRepo() {
        updateUserInfo();
    }

    // API Call

    public void registerUser(KuknosSignupM info, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel> apiResponse) {
        kuknosAPIRepository.registerUser(info, handShakeCallback, apiResponse);
    }

    public void checkUsername(String username, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUsernameStatus>> apiResponse) {
        kuknosAPIRepository.checkUsername(username, handShakeCallback, apiResponse);
    }

    public void getUserStatus(HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfo>> apiResponse) {
        kuknosAPIRepository.getUserStatus(getAccountID(), handShakeCallback, apiResponse);
    }

    // generate key pair

    public void generateFa12Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate12FaWordMnemonic();
            RealmKuknos.updateMnemonic(String.valueOf(mnemonicTemp));
            Log.d("amini", "generateMnemonic: " + realmKuknos.getKuknosMnemonic());
        } catch (Exception e) {
            RealmKuknos.updateMnemonic("-1");
        }
    }

    public void generateFa24Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate24FaWordMnemonic();
            RealmKuknos.updateMnemonic(String.valueOf(mnemonicTemp));
            Log.d("amini", "generateMnemonic end: " + realmKuknos.getKuknosMnemonic());
        } catch (Exception e) {
            RealmKuknos.updateMnemonic("-1");
        }
    }

    public void generateEn12Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate12EnWordMnemonic();
            RealmKuknos.updateMnemonic(String.valueOf(mnemonicTemp));
            Log.d("amini", "generateMnemonic: " + realmKuknos.getKuknosMnemonic());
        } catch (Exception e) {
            RealmKuknos.updateMnemonic("-1");
        }
    }

    public void generateEn24Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate24EnWordMnemonic();
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
//        Log.d("amini", "generateKeyPairWithMnemonic: " + realmKuknos.getKuknosMnemonic());
//        Log.d("amini", "generateKeyPairWithMnemonic: seed :" + new String(pair.getSecretSeed()));
//        Log.d("amini", "generateKeyPairWithMnemonic: public :" + pair.getAccountId());
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

    // Realm and Data

    public void deleteAccount() {
        userInfo.deleteKuknos();
    }

    private void updateUserInfo() {
        userInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
        realmKuknos = userInfo.getKuknosM();
        if (realmKuknos == null) {
            userInfo.createKuknos();
            updateUserInfo();
            return;
        }

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

}
