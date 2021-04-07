package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletBill;

public class BillObject {
    public long orderId;
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

    public static BillObject create(RealmRoomMessageWalletBill realmBill) {
        BillObject billObject = new BillObject();
        billObject.orderId = realmBill.getOrderId();
        billObject.fromUserId = realmBill.getFromUserId();
        billObject.myToken = realmBill.getMyToken();
        billObject.token = realmBill.getToken();
        billObject.amount = realmBill.getAmount();
        billObject.payId = realmBill.getPayId();
        billObject.billId = realmBill.getBillId();
        billObject.billType = realmBill.getBillType();
        billObject.cardNumber = realmBill.getCardNumber();
        billObject.merchantName = realmBill.getMerchantName();
        billObject.terminalNo = realmBill.getTerminalNo();
        billObject.rrn = realmBill.getRrn();
        billObject.traceNumber = realmBill.getTraceNumber();
        billObject.requestTime = realmBill.getRequestTime();
        billObject.status = realmBill.isStatus();

        return billObject;
    }

    public static BillObject create(ProtoGlobal.RoomMessageWallet.Bill protoBill) {
        BillObject billObject = new BillObject();
        billObject.orderId=protoBill.getOrderId();
        billObject.fromUserId = protoBill.getFromUserId();
        billObject.myToken = protoBill.getMyToken();
        billObject.token = protoBill.getToken();
        billObject.amount = protoBill.getAmount();
        billObject.payId = protoBill.getPayId();
        billObject.billId = protoBill.getBillId();
        billObject.billType = protoBill.getBillType();
        billObject.cardNumber = protoBill.getCardNumber();
        billObject.merchantName = protoBill.getMerchantName();
        billObject.terminalNo = protoBill.getTerminalNo();
        billObject.rrn = protoBill.getRrn();
        billObject.traceNumber = protoBill.getTraceNumber();
        billObject.requestTime = protoBill.getRequestTime();
        billObject.status = protoBill.getStatus();


        return billObject;
    }

}
