package net.iGap.kuknos.service.model;

import io.realm.Realm;
import io.realm.RealmObject;

public class KuknosRealmM extends RealmObject {

    private String kuknosSeedKey;
    private String kuknosPublicKey;
    private String kuknosPIN;
    private String kuknosMnemonic;

    public KuknosRealmM() {
    }

    public KuknosRealmM(String kuknosSeedKey, String kuknosPublicKey, String kuknosPIN, String kuknosMnemonic) {
        this.kuknosSeedKey = kuknosSeedKey;
        this.kuknosPublicKey = kuknosPublicKey;
        this.kuknosPIN = kuknosPIN;
        this.kuknosMnemonic = kuknosMnemonic;
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

    public static void updateMnemonic(String kuknosMnemonic) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                KuknosRealmM realmUserInfo = realm.where(KuknosRealmM.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosMnemonic(kuknosMnemonic);
                }
            }
        });
        realm.close();
    }

    public static void updatePIN(String kuknosPin) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                KuknosRealmM realmUserInfo = realm.where(KuknosRealmM.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosPIN(kuknosPin);
                }
            }
        });
        realm.close();
    }

    public static void updateKey(String kuknosSeed, String publicKey) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                KuknosRealmM realmUserInfo = realm.where(KuknosRealmM.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosSeedKey(kuknosSeed);
                    realmUserInfo.setKuknosPublicKey(publicKey);
                }
            }
        });
        realm.close();
    }

    public static void updateSeedKey(String kuknosSeed) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                KuknosRealmM realmUserInfo = realm.where(KuknosRealmM.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setKuknosSeedKey(kuknosSeed);
                }
            }
        });
        realm.close();
    }

}
