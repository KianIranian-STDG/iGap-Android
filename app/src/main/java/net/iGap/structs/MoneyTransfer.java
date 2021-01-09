package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletBill;
import net.iGap.realm.RealmRoomMessageWalletMoneyTransfer;

public class MoneyTransfer {
    public long fromUserId;
    public long toUserId;
    public long amount;
    public long traceNumber;
    public long invoiceNumber;
    public int payTime;
    public String description;
    public String cardNumber;
    public long rrn;


    public static MoneyTransfer create(RealmRoomMessageWalletMoneyTransfer realmMoneyTransfer) {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.fromUserId = realmMoneyTransfer.getFromUserId();
        moneyTransfer.toUserId = realmMoneyTransfer.getToUserId();
        moneyTransfer.amount = realmMoneyTransfer.getAmount();
        moneyTransfer.traceNumber = realmMoneyTransfer.getTraceNumber();
        moneyTransfer.invoiceNumber = realmMoneyTransfer.getInvoiceNumber();
        moneyTransfer.payTime = realmMoneyTransfer.getPayTime();
        moneyTransfer.description = realmMoneyTransfer.getDescription();
        moneyTransfer.cardNumber = realmMoneyTransfer.getCardNumber();
        moneyTransfer.rrn = realmMoneyTransfer.getRrn();
        return moneyTransfer;
    }

    public static MoneyTransfer create(ProtoGlobal.RoomMessageWallet.MoneyTransfer protoMoneyTransfer) {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.fromUserId = protoMoneyTransfer.getFromUserId();
        moneyTransfer.toUserId = protoMoneyTransfer.getToUserId();
        moneyTransfer.amount = protoMoneyTransfer.getAmount();
        moneyTransfer.traceNumber = protoMoneyTransfer.getTraceNumber();
        moneyTransfer.invoiceNumber = protoMoneyTransfer.getInvoiceNumber();
        moneyTransfer.payTime = protoMoneyTransfer.getPayTime();
        moneyTransfer.description = protoMoneyTransfer.getDescription();
        moneyTransfer.cardNumber = protoMoneyTransfer.getCardNumber();
        moneyTransfer.rrn = protoMoneyTransfer.getRrn();
        return moneyTransfer;
    }


}
