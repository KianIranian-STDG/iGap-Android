package com.iGap.interface_package;

import com.iGap.module.StructContactInfo;

import java.util.ArrayList;

public interface OnSelectedList {

    void getSelectedList(boolean result, String message, int countForShowLastMessage, ArrayList<StructContactInfo> list);

}
