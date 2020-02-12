package net.iGap.realm;

import net.iGap.module.accountManager.DbManager;
import net.iGap.model.mobileBank.BankCardModel;

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
    private boolean isOrigin;

    public RealmMobileBankCards() {
    }

    public RealmMobileBankCards(String cardNumber, String cardName, String bankName, String expireDate, boolean isOrigin) {
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.bankName = bankName;
        this.expireDate = expireDate;
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
                    //object.setCardNumber(card.getPan());
                    object.setBankName(card.getCardBankName() == null ? "پارسیان" : card.getCardBankName());
                    object.setExpireDate(card.getExpireDate());
                    object.setOrigin(card.getCardBankName() == null);
                }

            }, onSuccess);
        });
    }

    public static void putOrUpdate(String cardNumber, String cardName, String bankName, String expireDate, boolean isOrigin) {
        DbManager.getInstance().doRealmTask(realm1 -> {

            RealmMobileBankCards object = realm1.where(RealmMobileBankCards.class).equalTo(RealmMobileBankCardsFields.CARD_NUMBER, cardNumber).findFirst();
            if (object == null) object = realm1.createObject(RealmMobileBankCards.class);

            object.setCardName(cardName);
            object.setCardNumber(cardNumber);
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

    public static void deleteAll() {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.where(RealmMobileBankCards.class).findAll().deleteAllFromRealm();
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
}
