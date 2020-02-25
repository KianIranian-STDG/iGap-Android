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

import net.iGap.observers.interfaces.RealmMoneyTransfer;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.net_iGap_realm_RealmRoomMessageWalletPaymentRealmProxy;

@Parcel(implementations = {net_iGap_realm_RealmRoomMessageWalletPaymentRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessageWalletPayment.class})
public class RealmRoomMessageWalletPayment extends RealmObject implements RealmMoneyTransfer {

    private long fromUserId;
    private long toUserId;
    private long amount;
    private long traceNumber;
    private long invoiceNumber;
    private int payTime;
    private String description;
    private String cardNumber;
    private long rrn;

    public static RealmRoomMessageWalletPayment put(Realm realm, final ProtoGlobal.RoomMessageWallet.MoneyTransfer input) {
        RealmRoomMessageWalletPayment messageWallet = realm.createObject(RealmRoomMessageWalletPayment.class);

        messageWallet.setFromUserId(input.getFromUserId());
        messageWallet.setToUserId(input.getToUserId());
        messageWallet.setAmount(input.getAmount());
        messageWallet.setTraceNumber(input.getTraceNumber());
        messageWallet.setInvoiceNumber(input.getInvoiceNumber());
        messageWallet.setPayTime(input.getPayTime());
        messageWallet.setDescription(input.getDescription());
        messageWallet.setCardNumber(input.getCardNumber());
        messageWallet.setRrn(input.getRrn());

        return messageWallet;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTraceNumber() {
        return traceNumber;
    }

    public void setTraceNumber(long traceNumber) {
        this.traceNumber = traceNumber;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getPayTime() {
        return payTime;
    }

    public void setPayTime(int payTime) {
        this.payTime = payTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public long getRrn() {
        return rrn;
    }

    public void setRrn(long rrn) {
        this.rrn = rrn;
    }
}
