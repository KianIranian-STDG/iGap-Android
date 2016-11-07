package com.iGap.interfaces;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoResponse;

public interface OnFileUploadStatusResponse {
    void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status,
                            double progress, int recheckDelayMS, String fileHashAsIdentity,
                            ProtoResponse.Response response);
}
