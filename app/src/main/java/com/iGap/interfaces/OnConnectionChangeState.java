package com.iGap.interfaces;

import com.iGap.module.enums.ConnectionState;

public interface OnConnectionChangeState {
    void onChangeState(ConnectionState connectionState);
}
