package net.iGap.kuknos.service.model;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmKuknos extends RealmObject {

    private String kuknosSeedKey;
    private String kuknosPublicKey;
    private String kuknosPIN;
    private String kuknosMnemonic;

    public RealmKuknos() {
    }

    public RealmKuknos(String kuknosSeedKey, String kuknosPublicKey, String kuknosPIN, String kuknosMnemonic) {
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
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.setKuknosMnemonic(kuknosMnemonic);
                    }
                }
            });
        }
    }

    public static void updatePIN(String kuknosPin) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.setKuknosPIN(kuknosPin);
                    }
                }
            });
        }
    }

    public static void updateKey(String kuknosSeed, String publicKey) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.setKuknosSeedKey(kuknosSeed);
                        realmUserInfo.setKuknosPublicKey(publicKey);
                    }
                }
            });
        }
    }

    public static void updateSeedKey(String kuknosSeed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmKuknos realmUserInfo = realm.where(RealmKuknos.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.setKuknosSeedKey(kuknosSeed);
                    }
                }
            });
        }
    }

}
