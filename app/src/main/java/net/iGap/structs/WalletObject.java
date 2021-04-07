package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWallet;

public class WalletObject {
    public String type;
    public MoneyTransferObject moneyTransferObject;
    public CardToCardObject cardToCard;
    public TopupObject topupObject;
    public BillObject billObject;
    public PaymentObject paymentObject;

    public static WalletObject create(ProtoGlobal.RoomMessageWallet wallet) {
        WalletObject walletObject = new WalletObject();
        walletObject.type = wallet.getType().toString();
        switch (walletObject.type) {
            case "CARD_TO_CARD":
                walletObject.cardToCard = CardToCardObject.create(wallet.getCardToCard());
                break;
            case "BILL":
                walletObject.billObject = BillObject.create(wallet.getBill());
                break;
            case "TOPUP":
                walletObject.topupObject = TopupObject.create(wallet.getTopup());
                break;
            case "PAYMENT":
                walletObject.paymentObject = PaymentObject.create(wallet.getMoneyTransfer());
                break;
            case "MONEY_TRANSFER":
                walletObject.moneyTransferObject = MoneyTransferObject.create(wallet.getMoneyTransfer());
                break;
        }

        return walletObject;
    }

    public static WalletObject create(RealmRoomMessageWallet wallet) {
        WalletObject walletObject = new WalletObject();
        if (wallet.getType() != null) {
            walletObject.type = wallet.getType();
        }
        switch (wallet.getType()) {
            case "CARD_TO_CARD":
                walletObject.cardToCard = CardToCardObject.create(wallet.getRealmRoomMessageWalletCardToCard());
                break;
            case "BILL":
                walletObject.billObject = BillObject.create(wallet.getRealmRoomMessageWalletBill());
                break;
            case "TOPUP":
                walletObject.topupObject = TopupObject.create(wallet.getRealmRoomMessageWalletTopup());
                break;
            case "PAYMENT":
                walletObject.paymentObject = PaymentObject.create(wallet.getRealmRoomMessageWalletPayment());
                break;
            case "MONEY_TRANSFER":
                walletObject.moneyTransferObject = MoneyTransferObject.create(wallet.getRealmRoomMessageWalletMoneyTransfer());
                break;
        }

        return walletObject;
    }
}
