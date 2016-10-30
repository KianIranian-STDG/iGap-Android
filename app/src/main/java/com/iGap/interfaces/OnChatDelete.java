// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

public interface OnChatDelete {

    void onChatDelete(long roomId);

    void onChatDeleteError(int majorCode, int minorCode);
}
