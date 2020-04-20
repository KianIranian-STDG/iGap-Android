package net.iGap.realm;

import net.iGap.model.mobileBank.BankAccountModel;
import net.iGap.module.accountManager.DbManager;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMobileBankAccounts extends RealmObject {

    @PrimaryKey
    private String accountNumber;
    private String accountName;
    private String status;

    public RealmMobileBankAccounts() {
    }

    public RealmMobileBankAccounts(String number, String name, String status) {
        this.accountNumber = number;
        this.accountName = name;
        this.status = status;
    }

    public static void putOrUpdate(List<BankAccountModel> accounts, Realm.Transaction.OnSuccess onSuccess) {
        DbManager.getInstance().doRealmTask(realm1 -> {
            realm1.executeTransactionAsync(asyncRealm -> {
                for (BankAccountModel item : accounts) {

                    if (item.getAccountNumber() == null) continue;

                    RealmMobileBankAccounts object = asyncRealm.where(RealmMobileBankAccounts.class).equalTo(RealmMobileBankAccountsFields.ACCOUNT_NUMBER, item.getAccountNumber()).findFirst();
                    if (object == null)
                        object = asyncRealm.createObject(RealmMobileBankAccounts.class, item.getAccountNumber());

                    object.setAccountName(item.getTitle());
                    object.setStatus(item.getStatus());

                }

            }, onSuccess);
        });
    }

    public static void delete(String accountNumber) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmMobileBankAccounts object = realm.where(RealmMobileBankAccounts.class).equalTo(RealmMobileBankAccountsFields.ACCOUNT_NUMBER, accountNumber).findFirst();
            if (object != null) object.deleteFromRealm();
        });
    }

    public static void deleteAll(Realm.Transaction.OnSuccess callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                realm1.where(RealmMobileBankAccounts.class).findAll().deleteAllFromRealm();
            }, callback);
        });
    }

    public static List<RealmMobileBankAccounts> getAccounts() {
        return DbManager
                .getInstance()
                .doRealmTask((DbManager.RealmTaskWithReturn<List<RealmMobileBankAccounts>>)
                        realm -> realm.where(RealmMobileBankAccounts.class).findAll());
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
