package com.iGap.interfaces;

import java.io.IOException;

public interface OnGetPermission {

    void Allow() throws IOException;

    void deny();
}
