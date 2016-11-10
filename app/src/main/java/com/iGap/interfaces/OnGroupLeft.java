// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

public interface OnGroupLeft {

    void onGroupLeft(long roomId, long memberId);

    void onError(int majorCode, int minorCode);
}
