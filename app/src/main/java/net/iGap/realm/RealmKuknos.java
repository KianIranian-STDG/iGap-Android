package net.iGap.realm;

import net.iGap.module.accountManager.DbManager;

import io.realm.RealmObject;

public class RealmKuknos extends RealmObject {

    private String kuknosSeedKey;
    private String kuknosPublicKey;
    private String kuknosPIN;
    private String kuknosMnemonic;
    private String iban;
    private String birthDate;

    public RealmKuknos() {
    }

    public RealmKuknos(String kuknosSeedKey, String kuknosPublicKey, String kuknosPIN, String kuknosMnemonic, String iban,String birthDate) {
        this.kuknosSeedKey = kuknosSeedKey;
        this.kuknosPublicKey = kuknosPublicKey;
        this.kuknosPIN = kuknosPIN;
        this.kuknosMnemonic = kuknosMnemonic;
        this.iban = iban;
        this.birthDate=birthDate;
    }

    public String getKuknosSeedKey() {
        return kuknosSeedKey;
    }

    private void setKuknosSeedKey(String kuknosSeedKey) {
        this.kuknosSeedKey = kuknosSeedKey;
    }

    public String getKuknosPIN() {
        return kuknosPIN;
    }

    private void setKuknosPIN(String kuknosPIN) {
        this.kuknosPIN = kuknosPIN;
    }

    public String getKuknosMnemonic() {
        return kuknosMnemonic;
    }

    private void setKuknosMnemonic(String kuknosMnemonic) {
        this.kuknosMnemonic = kuknosMnemonic;
    }

    public String getKuknosPublicKey() {
        return kuknosPublicKey;
    }

    private void setKuknosPublicKey(String kuknosPublicKey) {
        this.kuknosPublicKey = kuknosPublicKey;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public static void updateMnemonic(String kuknosMnemonic) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosMnemonic(kuknosMnemonic);
                }
            });
        }).start();
    }

    public static void updatePIN(String kuknosPin) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosPIN(kuknosPin);
                }
            });
        }).start();
    }

    public static void updateKey(String kuknosSeed, String publicKey) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosSeedKey(kuknosSeed);
                    realmUserInfo.setKuknosPublicKey(publicKey);
                }
            });
        }).start();
    }

    public static void updateSeedKey(String kuknosSeed) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosSeedKey(kuknosSeed);
                }
            });
        }).start();
    }

    public static void updateIban(String iban) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setIban(iban);
                }
            });
        }).start();
    }
    public static void updateKuknos(String seed, String publicKey, String mNemonic, String pin) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmKuknos realmUserInfo = realm.where(RealmUserInfo.class).findFirst().getKuknosM();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosSeedKey(seed);
                    realmUserInfo.setKuknosPublicKey(publicKey);
                    realmUserInfo.setKuknosMnemonic(mNemonic);
                    realmUserInfo.setKuknosPIN(pin);
                }
            });
        }).start();
    }
}
