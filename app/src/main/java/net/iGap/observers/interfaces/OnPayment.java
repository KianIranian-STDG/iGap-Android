package net.iGap.observers.interfaces;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

public interface OnPayment {
    void onChargeToken(int status, String token, int expireTime, String Message);

    void onBillToken(int status, String token, int expireTime, String Message, int originalAmount, int discountedAmount);

}
