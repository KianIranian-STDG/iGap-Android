package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoRequest;

public class RequestFileUploadStatus {

    public void fileUploadStatus(String token, String fileHashAsIdentity) {

        ProtoFileUploadStatus.FileUploadStatus.Builder builder = ProtoFileUploadStatus.FileUploadStatus.newBuilder();
        builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        builder.setToken(token);

        RequestWrapper requestWrapper = new RequestWrapper(703, builder, fileHashAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
