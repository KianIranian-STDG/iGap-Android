package com.iGap.interfaces;

public interface OnChannelUpdateSignature {
    void onChannelUpdateSignatureResponse(long roomId, boolean signature);

    void onError(int majorCode, int minorCode);
}
