package com.iGap.interface_package;

import com.iGap.proto.ProtoUserRegister;

public interface OnUserRegistration {

    void onRegister(String userName, long userId, ProtoUserRegister.UserRegisterResponse.Method methodValue);

}
