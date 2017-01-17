package com.iGap.interfaces;

import com.iGap.proto.ProtoUserRegister;

import java.util.List;

public interface OnUserRegistration {

    void onRegister(String userName, long userId, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbers, String regex, int verifyCodeDigitCount, String authorHash);

    void onRegisterError(int majorCode, int minorCode, int getWait);
}
