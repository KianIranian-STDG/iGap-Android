package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletTopup;

public class TopupObject {

    public long orderId;
    public long fromUserId;
    public String myToken;
    public long token;
    public long amount;
    public String requestMobileNumber;
    public String chargeMobileNumber;
    public int topupType;
    public String cardNumber;
    public String merchantName;
    public long terminalNo;
    public long rrn;
    public long traceNumber;
    public int requestTime;
    public boolean status;


    public static TopupObject create(RealmRoomMessageWalletTopup realmTopup) {
        TopupObject topupObject = new TopupObject();
        topupObject.orderId = realmTopup.getOrderId();
        topupObject.fromUserId = realmTopup.getFromUserId();
        topupObject.myToken = realmTopup.getMyToken();
        topupObject.token = realmTopup.getToken();
        topupObject.amount = realmTopup.getAmount();
        topupObject.requestMobileNumber = realmTopup.getRequestMobileNumber();
        topupObject.chargeMobileNumber = realmTopup.getChargeMobileNumber();
        topupObject.topupType = realmTopup.getTopupType();
        topupObject.cardNumber = realmTopup.getCardNumber();
        topupObject.merchantName = realmTopup.getMerchantName();
        topupObject.terminalNo = realmTopup.getTerminalNo();
        topupObject.rrn = realmTopup.getRrn();
        topupObject.traceNumber = realmTopup.getTraceNumber();
        topupObject.requestTime = realmTopup.getRequestTime();
        topupObject.status = realmTopup.isStatus();
        return topupObject;
    }

    public static TopupObject create(ProtoGlobal.RoomMessageWallet.Topup protoTopup) {
        TopupObject topupObject = new TopupObject();
        topupObject.orderId = protoTopup.getOrderId();
        topupObject.fromUserId = protoTopup.getFromUserId();
        topupObject.myToken = protoTopup.getMyToken();
        topupObject.token = protoTopup.getToken();
        topupObject.amount = protoTopup.getAmount();
        topupObject.requestMobileNumber = protoTopup.getRequesterMobileNumber();
        topupObject.chargeMobileNumber = protoTopup.getChargeMobileNumber();
        topupObject.topupType = protoTopup.getTopupType().getNumber();
        topupObject.cardNumber = protoTopup.getCardNumber();
        topupObject.merchantName = protoTopup.getMerchantName();
        topupObject.terminalNo = protoTopup.getTerminalNo();
        topupObject.rrn = protoTopup.getRrn();
        topupObject.traceNumber = protoTopup.getTraceNumber();
        topupObject.requestTime = protoTopup.getRequestTime();
        topupObject.status = protoTopup.getStatus();
        return topupObject;

    }


}
