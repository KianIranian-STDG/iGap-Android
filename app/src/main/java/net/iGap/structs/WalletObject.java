package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;

public class WalletObject {
    public static WalletObject create(ProtoGlobal.RoomMessageWallet wallet) {
        WalletObject walletObject = new WalletObject();
        ProtoGlobal.RoomMessageWallet.Bill bill = wallet.getBill();
        ProtoGlobal.RoomMessageWallet.CardToCard cardToCard = wallet.getCardToCard();
        ProtoGlobal.RoomMessageWallet.MoneyTransfer moneyTransfer = wallet.getMoneyTransfer();
        ProtoGlobal.RoomMessageWallet.Topup topup = wallet.getTopup();
        int typeValue = wallet.getTypeValue();

        return walletObject;
    }
}
