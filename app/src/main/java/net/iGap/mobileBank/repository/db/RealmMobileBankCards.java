package net.iGap.mobileBank.repository.db;

import net.iGap.DbManager;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMobileBankCards extends RealmObject {

    @PrimaryKey
    private String cardNumber;
    private String cardName;
    private String expireDate;
    private boolean isOrigin;

    public RealmMobileBankCards() {
    }

    public static void putOrUpdate(String cardNumber, String cardName, String expireDate, boolean isOrigin) {
        DbManager.getInstance().doRealmTask(realm1 -> {

            RealmMobileBankCards object = realm1.where(RealmMobileBankCards.class).equalTo(RealmMobileBankCardsFields.CARD_NUMBER, cardNumber).findFirst();
            if (object == null) object = realm1.createObject(RealmMobileBankCards.class);

            object.setCardName(cardName);
            object.setCardNumber(cardNumber);
            object.setExpireDate(expireDate);
            object.setOrigin(isOrigin);

        });
    }

    public static void delete(String cardNumber) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmMobileBankCards object = realm.where(RealmMobileBankCards.class).equalTo(RealmMobileBankCardsFields.CARD_NUMBER, cardNumber).findFirst();
            if (object != null) object.deleteFromRealm();
        });
    }

    public static List<RealmMobileBankCards> getCards() {
        return DbManager
                .getInstance()
                .doRealmTask((DbManager.RealmTaskWithReturn<List<RealmMobileBankCards>>)
                        realm -> realm.where(RealmMobileBankCards.class).findAll());
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isOrigin() {
        return isOrigin;
    }

    public void setOrigin(boolean origin) {
        isOrigin = origin;
    }
}
