package com.iGap.interface_package;

import com.iGap.module.StructContactInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 9/21/2016.
 */
public interface OnSelectedList {

    void getSelectedList(boolean result, String message, ArrayList<StructContactInfo> list);

}
