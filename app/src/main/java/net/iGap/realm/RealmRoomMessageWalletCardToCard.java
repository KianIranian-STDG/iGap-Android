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

import net.iGap.proto.ProtoGlobal;


import io.realm.Realm;
import io.realm.RealmObject;

public class RealmRoomMessageWalletCardToCard extends RealmObject {

    private long fromUserId;
    private long toUserId;
    private long amount;
    private String bankName;
    private String destBankName;
    private String cardOwnerName;
    private long orderId;
    private long traceNumber;
    private String token;
    private boolean status;
    private String sourceCardNumber;
    private String destCardNumber;
    private String rrn;
    private int requestTime;


    public static RealmRoomMessageWalletCardToCard put(Realm realm, final ProtoGlobal.RoomMessageWallet.CardToCard input) {
        RealmRoomMessageWalletCardToCard messageWallet = realm.createObject(RealmRoomMessageWalletCardToCard.class);

        messageWallet.setFromUserId(input.getFromUserId());
        messageWallet.setToUserId(input.getToUserId());
        messageWallet.setAmount(input.getAmount());
        messageWallet.setBankName(input.getBankName());
        messageWallet.setDestBankName(input.getDestBankName());
        messageWallet.setCardOwnerName(input.getCardOwnerName());
        messageWallet.setOrderId(input.getOrderId());
        messageWallet.setTraceNumber(Long.parseLong(input.getTraceNumber()));
        messageWallet.setToken(input.getToken());
        messageWallet.setStatus(input.getStatus());
        messageWallet.setSourceCardNumber(input.getSourceCardNumber());
        messageWallet.setDestCardNumber(input.getDestCardNumber());
        messageWallet.setRrn(input.getRrn());
        messageWallet.setRequestTime(input.getRequestTime());


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

    public int getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(int payTime) {
        this.requestTime = payTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDestBankName() {
        return destBankName;
    }

    public void setDestBankName(String destBankName) {
        this.destBankName = destBankName;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSourceCardNumber() {
        return sourceCardNumber;
    }

    public void setSourceCardNumber(String sourceCardNumber) {
        this.sourceCardNumber = sourceCardNumber;
    }

    public String getDestCardNumber() {
        return destCardNumber;
    }

    public void setDestCardNumber(String destCardNumber) {
        this.destCardNumber = destCardNumber;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardOwnerName() {
        return cardOwnerName;
    }

    public void setCardOwnerName(String cardOwnerName) {
        this.cardOwnerName = cardOwnerName;
    }
}
