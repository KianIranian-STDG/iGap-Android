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

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageWalletTopup extends RealmObject {

    @PrimaryKey
    private long orderId;

    private long fromUserId;
    private String myToken;
    private long token;
    private long amount;
    private String requestMobileNumber;
    private String chargeMobileNumber;
    private int topupType;
    private String cardNumber;
    private String merchantName;
    private long terminalNo;
    private long rrn;
    private long traceNumber;
    private int requestTime;
    private boolean status;


    public static RealmRoomMessageWalletTopup put(Realm realm, final ProtoGlobal.RoomMessageWallet.Topup input) {
        RealmRoomMessageWalletTopup messageWallet = realm.where(RealmRoomMessageWalletTopup.class).equalTo("orderId", input.getOrderId()).findFirst();
        if (messageWallet == null) {
            messageWallet = realm.createObject(RealmRoomMessageWalletTopup.class, input.getOrderId());
        }

        messageWallet.setFromUserId(input.getFromUserId());
        messageWallet.setMyToken(input.getMyToken());
        messageWallet.setToken(input.getToken());
        messageWallet.setAmount(input.getAmount());
        messageWallet.setRequestMobileNumber(input.getRequesterMobileNumber());
        messageWallet.setChargeMobileNumber(input.getChargeMobileNumber());
        messageWallet.setTopupType(input.getTopupType().getNumber());
        messageWallet.setCardNumber(input.getCardNumber());
        messageWallet.setMerchantName(input.getMerchantName());
        messageWallet.setTerminalNo(input.getTerminalNo());
        messageWallet.setRrn(input.getRrn());
        messageWallet.setTraceNumber(input.getTraceNumber());
        messageWallet.setRequestTime(input.getRequestTime());
        messageWallet.setStatus(input.getStatus());

        return messageWallet;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getMyToken() {
        return myToken;
    }

    public void setMyToken(String myToken) {
        this.myToken = myToken;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getRequestMobileNumber() {
        return requestMobileNumber;
    }

    public void setRequestMobileNumber(String requestMobileNumber) {
        this.requestMobileNumber = requestMobileNumber;
    }

    public String getChargeMobileNumber() {
        return chargeMobileNumber;
    }

    public void setChargeMobileNumber(String chargeMobileNumber) {
        this.chargeMobileNumber = chargeMobileNumber;
    }

    public int getTopupType() {
        return topupType;
    }

    public void setTopupType(int topupType) {
        this.topupType = topupType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public long getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(long terminalNo) {
        this.terminalNo = terminalNo;
    }

    public long getRrn() {
        return rrn;
    }

    public void setRrn(long rrn) {
        this.rrn = rrn;
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

    public void setRequestTime(int requestTime) {
        this.requestTime = requestTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
