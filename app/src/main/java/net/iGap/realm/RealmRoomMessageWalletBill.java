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
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageWalletBill extends RealmObject {

    @PrimaryKey
    private long orderId;

    private long fromUserId;
    private String myToken;
    private long token;
    private long amount;
    private String payId;
    private String billId;
    private String billType;
    private String cardNumber;
    private String merchantName;
    private long terminalNo;
    private long rrn;
    private long traceNumber;
    private int requestTime;
    private boolean status;


    public static RealmRoomMessageWalletBill put(Realm realm, final ProtoGlobal.RoomMessageWallet.Bill input) {
        RealmRoomMessageWalletBill messageWallet = realm.where(RealmRoomMessageWalletBill.class).equalTo("orderId", input.getOrderId()).findFirst();
        if (messageWallet == null) {
            messageWallet = realm.createObject(RealmRoomMessageWalletBill.class, input.getOrderId());
        }

        messageWallet.setFromUserId(input.getFromUserId());
        messageWallet.setMyToken(input.getMyToken());
        messageWallet.setToken(input.getToken());
        messageWallet.setAmount(input.getAmount());
        messageWallet.setPayId(input.getPayId());
        messageWallet.setBillId(input.getBillId());
        messageWallet.setBillType(input.getBillType());
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

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
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
