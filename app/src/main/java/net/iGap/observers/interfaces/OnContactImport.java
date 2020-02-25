package net.iGap.observers.interfaces;

import net.iGap.proto.ProtoGlobal;

public interface OnContactImport {
    void onContactInfo(ProtoGlobal.RegisteredUser user);

    void onTimeOut();

    void onError(int majorCode, int minorCode);
}
