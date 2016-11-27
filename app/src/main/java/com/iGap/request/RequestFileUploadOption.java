package com.iGap.request;

import com.iGap.G;
import com.iGap.helper.HelperString;
import com.iGap.module.FileUploadStructure;
import com.iGap.proto.ProtoFileUploadOption;
import com.iGap.proto.ProtoRequest;

public class RequestFileUploadOption {

    public void fileUploadOption(FileUploadStructure fileUploadStructure, String identity) {

        ProtoFileUploadOption.FileUploadOption.Builder fileUploadOption =
                ProtoFileUploadOption.FileUploadOption.newBuilder();
        fileUploadOption.setRequest(
                ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        fileUploadOption.setSize(fileUploadStructure.fileSize);


        try {
            RequestWrapper requestWrapper = new RequestWrapper(700, fileUploadOption, identity);

            G.currentUploadAndDownloadFiles.put(fileUploadStructure.messageId, requestWrapper);

            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
