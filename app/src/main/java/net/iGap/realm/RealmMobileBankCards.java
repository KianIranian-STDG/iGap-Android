package net.iGap.realm;

import net.iGap.model.mobileBank.BankCardModel;
import net.iGap.module.accountManager.DbManager;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMobileBankCards extends RealmObject {

    @PrimaryKey
    private String cardNumber;
    private String cardName;
    private String bankName;
    private String expireDate;
    private String status;
    private boolean isOrigin;

    public RealmMobileBankCards() {
    }

    public RealmMobileBankCards(String cardNumber, String cardName, String bankName, String expireDate, String status, boolean isOrigin) {
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.bankName = bankName;
        this.expireDate = expireDate;
        this.status = status;
        this.isOrigin = isOrigin;
    }

    public static void putOrUpdate(List<BankCardModel> cards, Realm.Transaction.OnSuccess onSuccess) {
        DbManager.getInstance().doRealmTask(realm1 -> {
            realm1.executeTransactionAsync(asyncRealm -> {
                for (BankCardModel card : cards) {

                    if (card.getPan() == null) continue;

                    RealmMobileBankCards object = asyncRealm.where(RealmMobileBankCards.class).equalTo(RealmMobileBankCardsFields.CARD_NUMBER, card.getPan()).findFirst();
                    if (object == null)
                        object = asyncRealm.createObject(RealmMobileBankCards.class, card.getPan());

                    object.setCardName(card.getCustomerFirstName() + " " + card.getCustomerLastName());
                    object.setStatus(card.getCardStatus());
                    object.setBankName(card.getCardBankName() == null ? "پارسیان" : card.getCardBankName());
                    object.setExpireDate(card.getExpireDate());
                    object.setOrigin(card.getCardBankName() == null);
                }

            }, onSuccess);
        });
    }

    public static void putOrUpdate(String cardNumber, String cardName, String bankName, String expireDate, String status, boolean isOrigin) {
        DbManager.getInstance().doRealmTask(realm1 -> {

            RealmMobileBankCards object = realm1.where(RealmMobileBankCards.class).equalTo(RealmMobileBankCardsFields.CARD_NUMBER, cardNumber).findFirst();
            if (object == null) object = realm1.createObject(RealmMobileBankCards.class);

            object.setCardName(cardName);
            object.setCardNumber(cardNumber);
            object.setStatus(status);
            object.setBankName(bankName);
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

    public static void deleteAll(Realm.Transaction.OnSuccess callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                realm1.where(RealmMobileBankCards.class).findAll().deleteAllFromRealm();
            }, callback);
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
