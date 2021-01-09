package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletBill;
import net.iGap.realm.RealmRoomMessageWalletTopup;

public class Topup {

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



    public static Topup create(RealmRoomMessageWalletTopup realmTopup) {
        Topup topup=new Topup();
        topup.fromUserId=realmTopup.getFromUserId();
        topup.myToken=realmTopup.getMyToken();
        topup.token=realmTopup.getToken();
        topup.amount=realmTopup.getAmount();
        topup.requestMobileNumber=realmTopup.getRequestMobileNumber();
        topup.chargeMobileNumber=realmTopup.getChargeMobileNumber();
        topup.topupType=realmTopup.getTopupType();
        topup.cardNumber=realmTopup.getCardNumber();
        topup.merchantName=realmTopup.getMerchantName();
        topup.terminalNo=realmTopup.getTerminalNo();
        topup.rrn=realmTopup.getRrn();
        topup.traceNumber=realmTopup.getTraceNumber();
        topup.requestTime=realmTopup.getRequestTime();
        topup.status=realmTopup.isStatus();
        return topup;
    }
    public static Topup create(ProtoGlobal.RoomMessageWallet.Topup protoTopup) {
        Topup topup=new Topup();
        topup.fromUserId=protoTopup.getFromUserId();
        topup.myToken=protoTopup.getMyToken();
        topup.token=protoTopup.getToken();
        topup.amount=protoTopup.getAmount();
        topup.requestMobileNumber=protoTopup.getRequesterMobileNumber();
        topup.chargeMobileNumber=protoTopup.getChargeMobileNumber();
        topup.topupType=protoTopup.getTopupType().getNumber();
        topup.cardNumber=protoTopup.getCardNumber();
        topup.merchantName=protoTopup.getMerchantName();
        topup.terminalNo=protoTopup.getTerminalNo();
        topup.rrn=protoTopup.getRrn();
        topup.traceNumber=protoTopup.getTraceNumber();
        topup.requestTime=protoTopup.getRequestTime();
        topup.status=protoTopup.getStatus();
        return topup;

    }



}
