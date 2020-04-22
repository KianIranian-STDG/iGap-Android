package net.iGap.repository.kuknos;

import android.util.Log;

import net.iGap.model.kuknos.KuknosSignupM;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosUserInfo;
import net.iGap.model.kuknos.Parsian.KuknosUsernameStatus;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.kuknos.mnemonic.Wallet;
import net.iGap.module.kuknos.mnemonic.WalletException;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmKuknos;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import org.stellar.sdk.KeyPair;

public class UserRepo {

    private RealmUserInfo userInfo;
    //    private RealmKuknos realmKuknos;
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

    public void getUserStatus(String publicKey, HandShakeCallback handShakeCallback, ResponseCallback<KuknosResponseModel<KuknosUserInfo>> apiResponse) {
        kuknosAPIRepository.getUserStatus(publicKey, handShakeCallback, apiResponse);
    }

    // generate key pair

    public String generateFa12Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate12FaWordMnemonic();
            return generateMnemonic(mnemonicTemp);
        } catch (Exception e) {
            e.printStackTrace();
            return generateMnemonic(null);
        }
    }

    public String generateFa24Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate24FaWordMnemonic();
            return generateMnemonic(mnemonicTemp);
        } catch (Exception e) {
            e.printStackTrace();
            return generateMnemonic(null);
        }
    }

    public String generateEn12Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate12EnWordMnemonic();
            return generateMnemonic(mnemonicTemp);
        } catch (Exception e) {
            e.printStackTrace();
            return generateMnemonic(null);
        }
    }

    public String generateEn24Mnemonic() {
        try {
            char[] mnemonicTemp = Wallet.generate24EnWordMnemonic();
            return generateMnemonic(mnemonicTemp);
        } catch (Exception e) {
            e.printStackTrace();
            return generateMnemonic(null);
        }
    }

    private String generateMnemonic(char[] mnemonic) {
        if (mnemonic == null) {
            RealmKuknos.updateMnemonic("-1");
        } else {
            RealmKuknos.updateMnemonic(String.valueOf(mnemonic));
            Log.d("amini", "generateMnemonic: " + userInfo.getKuknosM().getKuknosMnemonic());
        }
        return String.valueOf(mnemonic);
    }

    public void generateKeyPair() {
        KeyPair pair = KeyPair.random();
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public String generateKeyPairWithMnemonic(String Mnemonic, String pin) throws WalletException {
        Log.d("amini", "generateKeyPairWithMnemonic: " + Mnemonic);
        KeyPair pair = Wallet.createKeyPair(Mnemonic.toCharArray(), null, 0);
        RealmKuknos.updateKuknos(new String(pair.getSecretSeed()), pair.getAccountId(), Mnemonic, pin);
//        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
        Log.d("amini", "generateKeyPairWithMnemonic: seed :" + new String(pair.getSecretSeed()));
        Log.d("amini", "generateKeyPairWithMnemonic: public :" + pair.getAccountId());
        return pair.getAccountId();
    }

    public void generateKeyPairWithMnemonicAndPIN() throws WalletException {
        KeyPair pair = Wallet.createKeyPair(userInfo.getKuknosM().getKuknosMnemonic().toCharArray(), userInfo.getKuknosM().getKuknosPIN().toCharArray(), 0);
        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
    }

    public String generateKeyPairWithSeed(String seed, String Mnemonic, String pin) {
        KeyPair pair = KeyPair.fromSecretSeed(seed);
        RealmKuknos.updateKuknos(new String(pair.getSecretSeed()), pair.getAccountId(), Mnemonic, pin);
//        RealmKuknos.updateKey(new String(pair.getSecretSeed()), pair.getAccountId());
        Log.d("amini", "generateKeyPairWithSeed: seed :" + new String(pair.getSecretSeed()));
        Log.d("amini", "generateKeyPairWithSeed: public :" + pair.getAccountId());
        return pair.getAccountId();
    }

    // setter and gettter

    public String getMnemonic() {
        return userInfo.getKuknosM().getKuknosMnemonic();
    }

    public void updateMnemonicRealm(String mnemonic) {
        RealmKuknos.updateMnemonic(mnemonic);
    }

    public String getSeedKey() {
        // if -1 it's sign out. if null it's first time
        if (userInfo.getKuknosM() != null && userInfo.getKuknosM().isValid())
            return userInfo.getKuknosM().getKuknosSeedKey();
        else
            return null;
    }

    public void setSeedKey(String seed) {
        RealmKuknos.updateSeedKey(seed);
    }

    public String getAccountID() {
        if (userInfo.getKuknosM().isValid())
            return userInfo.getKuknosM().getKuknosPublicKey();
        else
            return "-1";
    }

    public void setPIN(String pin) {
        RealmKuknos.updatePIN(pin);
    }

    public String getPIN() {
        return userInfo.getKuknosM().getKuknosPIN();
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
        if (userInfo == null) {
            userInfo = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmUserInfo.class).findFirst();
            });
        }
//        realmKuknos = userInfo.getKuknosM();
        if (userInfo.getKuknosM() == null) {
            userInfo.createKuknos();
        }

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

    public boolean isMnemonicAvailable() {
        return userInfo.getKuknosM().getKuknosMnemonic() != null;
    }

    public RealmUserInfo getUserInfo() {
        return userInfo;
    }
}
