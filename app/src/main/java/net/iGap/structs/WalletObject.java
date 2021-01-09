package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWallet;

public class WalletObject {
    public String type;
    public MoneyTransfer moneyTransfer;
    public CardToCardObject cardToCard;
    public Topup topup;
    public Bill bill;

    public static WalletObject create(ProtoGlobal.RoomMessageWallet wallet) {
        WalletObject walletObject = new WalletObject();
        walletObject.type = wallet.getType().toString();
        switch (walletObject.type) {
            case "CARD_TO_CARD":
                walletObject.cardToCard = CardToCardObject.create(wallet.getCardToCard());
                break;

        }
        if (wallet.getBill() != null) {
            walletObject.bill = Bill.create(wallet.getBill());
        }
        if (wallet.getMoneyTransfer() != null) {
            walletObject.moneyTransfer = MoneyTransfer.create(wallet.getMoneyTransfer());
        }
        if (wallet.getTopup() != null) {
            walletObject.topup = Topup.create(wallet.getTopup());
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

        }
        if (wallet.getRealmRoomMessageWalletBill() != null) {
            walletObject.bill = Bill.create(wallet.getRealmRoomMessageWalletBill());
        }

        if (wallet.getRealmRoomMessageWalletMoneyTransfer() != null) {
            walletObject.moneyTransfer = MoneyTransfer.create(wallet.getRealmRoomMessageWalletMoneyTransfer());
        }
        if (wallet.getRealmRoomMessageWalletTopup() != null) {
            walletObject.topup = Topup.create(wallet.getRealmRoomMessageWalletTopup());
        }


        return walletObject;
    }
}
