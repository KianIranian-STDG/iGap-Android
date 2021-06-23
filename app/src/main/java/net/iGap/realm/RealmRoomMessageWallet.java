/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;


import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.BILL;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.TOPUP;

public class RealmRoomMessageWallet extends RealmObject {

    @PrimaryKey
    private long id;
    private String type;
    private RealmRoomMessageWalletCardToCard realmRoomMessageWalletCardToCard;
    private RealmRoomMessageWalletTopup realmRoomMessageWalletTopup;
    private RealmRoomMessageWalletBill realmRoomMessageWalletBill;
    private RealmRoomMessageWalletPayment realmRoomMessageWalletPayment;
    private RealmRoomMessageWalletMoneyTransfer realmRoomMessageWalletMoneyTransfer;

    public static RealmRoomMessageWallet put(Realm realm, final ProtoGlobal.RoomMessageWallet input) {
        RealmRoomMessageWallet messageWallet;
        messageWallet = realm.createObject(RealmRoomMessageWallet.class, AppUtils.makeRandomId());

        messageWallet.setType(input.getType().toString());

        if (input.getType() == CARD_TO_CARD) {
            messageWallet.setRealmRoomMessageWalletCardToCard(RealmRoomMessageWalletCardToCard.put(realm, input.getCardToCard()));
        } else if (input.getType() == TOPUP) {
            messageWallet.setRealmRoomMessageWalletTopup(RealmRoomMessageWalletTopup.put(realm, input.getTopup()));
        } else if (input.getType() == BILL) {
            messageWallet.setRealmRoomMessageWalletBill(RealmRoomMessageWalletBill.put(realm, input.getBill()));
        } else if (input.getType() == MONEY_TRANSFER) {
            messageWallet.setRealmRoomMessageWalletMoneyTransfer(RealmRoomMessageWalletMoneyTransfer.put(realm, input.getMoneyTransfer()));
        } else if (input.getType() == PAYMENT) {
            messageWallet.setRealmRoomMessageWalletPayment(RealmRoomMessageWalletPayment.put(realm, input.getMoneyTransfer()));
        } else {

        }


        return messageWallet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmRoomMessageWalletCardToCard getRealmRoomMessageWalletCardToCard() {
        return realmRoomMessageWalletCardToCard;
    }

    public void setRealmRoomMessageWalletCardToCard(RealmRoomMessageWalletCardToCard realmRoomMessageWalletCardToCard) {
        this.realmRoomMessageWalletCardToCard = realmRoomMessageWalletCardToCard;
    }


    public RealmRoomMessageWalletTopup getRealmRoomMessageWalletTopup() {
        return realmRoomMessageWalletTopup;
    }

    public void setRealmRoomMessageWalletTopup(RealmRoomMessageWalletTopup realmRoomMessageWalletTopup) {
        this.realmRoomMessageWalletTopup = realmRoomMessageWalletTopup;
    }

    public RealmRoomMessageWalletBill getRealmRoomMessageWalletBill() {
        return realmRoomMessageWalletBill;
    }

    public void setRealmRoomMessageWalletBill(RealmRoomMessageWalletBill realmRoomMessageWalletBill) {
        this.realmRoomMessageWalletBill = realmRoomMessageWalletBill;
    }

    public RealmRoomMessageWalletPayment getRealmRoomMessageWalletPayment() {
        return realmRoomMessageWalletPayment;
    }

    public void setRealmRoomMessageWalletPayment(RealmRoomMessageWalletPayment realmRoomMessageWalletPayment) {
        this.realmRoomMessageWalletPayment = realmRoomMessageWalletPayment;
    }

    public RealmRoomMessageWalletMoneyTransfer getRealmRoomMessageWalletMoneyTransfer() {
        return realmRoomMessageWalletMoneyTransfer;
    }

    public void setRealmRoomMessageWalletMoneyTransfer(RealmRoomMessageWalletMoneyTransfer realmRoomMessageWalletMoneyTransfer) {
        this.realmRoomMessageWalletMoneyTransfer = realmRoomMessageWalletMoneyTransfer;
    }
}
