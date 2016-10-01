package com.iGap.request;

import com.iGap.proto.ProtoFileDownload;

public class RequestFileDownload {

    public void download(String token, long offset, int maxLimit, ProtoFileDownload.FileDownload.Selector selector, String identity) {
        ProtoFileDownload.FileDownload.Builder builder = ProtoFileDownload.FileDownload.newBuilder();
        builder.setToken(token);
        builder.setOffset(offset);
        builder.setMaxLimit(maxLimit);
        builder.setSelector(selector);

        RequestWrapper requestWrapper = new RequestWrapper(705, builder, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}