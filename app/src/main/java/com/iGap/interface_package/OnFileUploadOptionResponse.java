// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interface_package;

import com.iGap.proto.ProtoResponse;

public interface OnFileUploadOptionResponse {

    void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response);

}
