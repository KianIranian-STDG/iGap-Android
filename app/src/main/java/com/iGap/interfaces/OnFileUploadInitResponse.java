// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoResponse;

public interface OnFileUploadInitResponse {

    void OnFileUploadInit(String token, double progress, long offset, int limit, String server,
                          ProtoResponse.Response response);
}
