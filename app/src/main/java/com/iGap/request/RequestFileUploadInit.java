package com.iGap.request;

import com.google.protobuf.ByteString;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoFileUpload;
import com.iGap.proto.ProtoRequest;

import java.io.UnsupportedEncodingException;

public class RequestFileUploadInit {

    public void fileUploadInit(byte[] firstBytes, byte[] lastBytes, long size, String fileHash, String fileName) throws UnsupportedEncodingException {

        ProtoFileUpload.FileUploadInit.Builder fileUploadInit = ProtoFileUpload.FileUploadInit.newBuilder();
        fileUploadInit.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        fileUploadInit.setFirstBytes(ByteString.copyFrom(firstBytes));
        fileUploadInit.setLastBytes(ByteString.copyFrom(lastBytes));
        fileUploadInit.setSize(size);
        fileUploadInit.setFileHash(ByteString.copyFrom(fileHash, "UTF-8"));
        fileUploadInit.setFileName(fileName);

        RequestWrapper requestWrapper = new RequestWrapper(701, fileUploadInit, fileHash);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
