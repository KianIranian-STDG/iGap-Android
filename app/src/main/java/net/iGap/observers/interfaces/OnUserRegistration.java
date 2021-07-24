/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.observers.interfaces;

import net.iGap.proto.ProtoUserRegister;

import java.util.List;

public interface OnUserRegistration {

    void onRegister(String userNameR, long userIdR, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, String authorHashR, boolean callMethodSupported, long resendCodeDelay);

    void onRegisterError(int majorCode, int minorCode, int getWait);
}
