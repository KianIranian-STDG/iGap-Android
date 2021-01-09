package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletMoneyTransfer;

public class MoneyTransferObject {
    public long fromUserId;
    public long toUserId;
    public long amount;
    public long traceNumber;
    public long invoiceNumber;
    public int payTime;
    public String description;
    public String cardNumber;
    public long rrn;


    public static MoneyTransferObject create(RealmRoomMessageWalletMoneyTransfer realmMoneyTransfer) {
        MoneyTransferObject moneyTransferObject = new MoneyTransferObject();
        moneyTransferObject.fromUserId = realmMoneyTransfer.getFromUserId();
        moneyTransferObject.toUserId = realmMoneyTransfer.getToUserId();
        moneyTransferObject.amount = realmMoneyTransfer.getAmount();
        moneyTransferObject.traceNumber = realmMoneyTransfer.getTraceNumber();
        moneyTransferObject.invoiceNumber = realmMoneyTransfer.getInvoiceNumber();
        moneyTransferObject.payTime = realmMoneyTransfer.getPayTime();
        moneyTransferObject.description = realmMoneyTransfer.getDescription();
        moneyTransferObject.cardNumber = realmMoneyTransfer.getCardNumber();
        moneyTransferObject.rrn = realmMoneyTransfer.getRrn();
        return moneyTransferObject;
    }

    public static MoneyTransferObject create(ProtoGlobal.RoomMessageWallet.MoneyTransfer protoMoneyTransfer) {
        MoneyTransferObject moneyTransferObject = new MoneyTransferObject();
        moneyTransferObject.fromUserId = protoMoneyTransfer.getFromUserId();
        moneyTransferObject.toUserId = protoMoneyTransfer.getToUserId();
        moneyTransferObject.amount = protoMoneyTransfer.getAmount();
        moneyTransferObject.traceNumber = protoMoneyTransfer.getTraceNumber();
        moneyTransferObject.invoiceNumber = protoMoneyTransfer.getInvoiceNumber();
        moneyTransferObject.payTime = protoMoneyTransfer.getPayTime();
        moneyTransferObject.description = protoMoneyTransfer.getDescription();
        moneyTransferObject.cardNumber = protoMoneyTransfer.getCardNumber();
        moneyTransferObject.rrn = protoMoneyTransfer.getRrn();
        return moneyTransferObject;
    }


}
