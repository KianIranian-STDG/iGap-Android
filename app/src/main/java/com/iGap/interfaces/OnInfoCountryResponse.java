// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

public interface OnInfoCountryResponse {

    void onInfoCountryResponse(int callingCode, String name, String pattern, String regex);

    void onError(int majorCode, int minorCode);
}
