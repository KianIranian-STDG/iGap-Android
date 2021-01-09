package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletBill;

public class Bill {
    public long fromUserId;
    public String myToken;
    public long token;
    public long amount;
    public String payId;
    public String billId;
    public String billType;
    public String cardNumber;
    public String merchantName;
    public long terminalNo;
    public long rrn;
    public long traceNumber;
    public int requestTime;
    public boolean status;

    public static Bill create(RealmRoomMessageWalletBill realmBill) {
        Bill bill = new Bill();
        bill.fromUserId = realmBill.getFromUserId();
        bill.myToken = realmBill.getMyToken();
        bill.token = realmBill.getToken();
        bill.amount = realmBill.getAmount();
        bill.payId = realmBill.getPayId();
        bill.billId = realmBill.getBillId();
        bill.billType = realmBill.getBillType();
        bill.cardNumber = realmBill.getCardNumber();
        bill.merchantName = realmBill.getMerchantName();
        bill.terminalNo = realmBill.getTerminalNo();
        bill.rrn = realmBill.getRrn();
        bill.traceNumber = realmBill.getTraceNumber();
        bill.requestTime = realmBill.getRequestTime();
        bill.status = realmBill.isStatus();

        return bill;
    }
    public static Bill create(ProtoGlobal.RoomMessageWallet.Bill protoBill) {
        Bill bill = new Bill();
        bill.fromUserId = protoBill.getFromUserId();
        bill.myToken = protoBill.getMyToken();
        bill.token = protoBill.getToken();
        bill.amount = protoBill.getAmount();
        bill.payId = protoBill.getPayId();
        bill.billId = protoBill.getBillId();
        bill.billType = protoBill.getBillType();
        bill.cardNumber = protoBill.getCardNumber();
        bill.merchantName = protoBill.getMerchantName();
        bill.terminalNo = protoBill.getTerminalNo();
        bill.rrn = protoBill.getRrn();
        bill.traceNumber = protoBill.getTraceNumber();
        bill.requestTime = protoBill.getRequestTime();
        bill.status = protoBill.getStatus();

        return bill;
    }

}
