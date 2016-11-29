package com.iGap.request;

import com.iGap.G;
import com.iGap.proto.ProtoFileDownload;

public class RequestFileDownload {

    public void download(String token, long offset, int maxLimit,
                         ProtoFileDownload.FileDownload.Selector selector, long fakeMessageID, String identity) {
        ProtoFileDownload.FileDownload.Builder builder =
                ProtoFileDownload.FileDownload.newBuilder();

        if (token == null)
            return;

        builder.setToken(token);
        builder.setOffset(offset);
        builder.setMaxLimit(maxLimit);
        builder.setSelector(selector);

        try {
            RequestWrapper requestWrapper = new RequestWrapper(705, builder, identity);

            G.currentUploadAndDownloadFiles.put(fakeMessageID, requestWrapper);

            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}