package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoFileUploadOption;
import com.iGap.proto.ProtoRequest;

public class RequestFileUploadOption {

    public void fileUploadOption(long size, String identity) {

        ProtoFileUploadOption.FileUploadOption.Builder fileUploadOption =
            ProtoFileUploadOption.FileUploadOption.newBuilder();
        fileUploadOption.setRequest(
            ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        fileUploadOption.setSize(size);

        try {
            RequestWrapper requestWrapper = new RequestWrapper(700, fileUploadOption, identity);
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
