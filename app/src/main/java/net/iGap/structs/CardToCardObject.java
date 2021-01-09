package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletCardToCard;

public class CardToCardObject {
    public long fromUserId;
    public long toUserId;
    public long amount;
    public String bankName;
    public String destBankName;
    public String cardOwnerName;
    public long orderId;
    public long traceNumber;
    public String token;
    public boolean status;
    public String sourceCardNumber;
    public String destCardNumber;
    public String rrn;
    public int requestTime;


    public static CardToCardObject create(RealmRoomMessageWalletCardToCard realmCardToCard) {
        CardToCardObject cardToCard = new CardToCardObject();
        cardToCard.fromUserId = realmCardToCard.getFromUserId();
        cardToCard.toUserId = realmCardToCard.getToUserId();
        cardToCard.amount = realmCardToCard.getAmount();
        cardToCard.bankName = realmCardToCard.getBankName();
        cardToCard.destBankName = realmCardToCard.getDestBankName();
        cardToCard.cardOwnerName = realmCardToCard.getCardOwnerName();
        cardToCard.orderId = realmCardToCard.getOrderId();
        cardToCard.traceNumber = realmCardToCard.getTraceNumber();
        cardToCard.token = realmCardToCard.getToken();
        cardToCard.status = realmCardToCard.isStatus();
        cardToCard.sourceCardNumber = realmCardToCard.getSourceCardNumber();
        cardToCard.destCardNumber = realmCardToCard.getDestCardNumber();
        cardToCard.rrn = realmCardToCard.getRrn();
        cardToCard.requestTime = realmCardToCard.getRequestTime();

        return cardToCard;

    }

    public static CardToCardObject create(ProtoGlobal.RoomMessageWallet.CardToCard protoToCard) {
        CardToCardObject cardToCard = new CardToCardObject();
        cardToCard.fromUserId = protoToCard.getFromUserId();
        cardToCard.toUserId = protoToCard.getToUserId();
        cardToCard.amount = protoToCard.getAmount();
        cardToCard.bankName = protoToCard.getBankName();
        cardToCard.destBankName = protoToCard.getDestBankName();
        cardToCard.cardOwnerName = protoToCard.getCardOwnerName();
        cardToCard.orderId = protoToCard.getOrderId();
        cardToCard.traceNumber = Long.parseLong(protoToCard.getTraceNumber());
        cardToCard.token = protoToCard.getToken();
        cardToCard.status = protoToCard.getStatus();
        cardToCard.sourceCardNumber = protoToCard.getSourceCardNumber();
        cardToCard.destCardNumber = protoToCard.getDestCardNumber();
        cardToCard.rrn = protoToCard.getRrn();
        cardToCard.requestTime = protoToCard.getRequestTime();

        return cardToCard;

    }

}
