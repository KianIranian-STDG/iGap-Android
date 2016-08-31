package com.iGap.request;

import com.google.protobuf.ByteString;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoFileUpload;
import com.iGap.proto.ProtoRequest;

public class RequestFileUpload {

    public void fileUpload(String token, long offset, byte[] bytes, String fileHashAsIdentity) {

        ProtoFileUpload.FileUpload.Builder fileUploadInit = ProtoFileUpload.FileUpload.newBuilder();
        fileUploadInit.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        fileUploadInit.setToken(token);
        fileUploadInit.setOffset(offset);
        fileUploadInit.setBytes(ByteString.copyFrom(bytes));

        RequestWrapper requestWrapper = new RequestWrapper(702, fileUploadInit, fileHashAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
