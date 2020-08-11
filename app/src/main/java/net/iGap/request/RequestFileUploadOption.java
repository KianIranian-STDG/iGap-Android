/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import net.iGap.proto.ProtoFileUploadOption;

public class RequestFileUploadOption {

    public interface OnFileUploadOption {
        void onFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection);

        void onFileUploadOptionError(int major, int minor);
    }

    public String fileUploadOption(long fileSize, OnFileUploadOption onFileUploadOption) {
        ProtoFileUploadOption.FileUploadOption.Builder fileUploadOption = ProtoFileUploadOption.FileUploadOption.newBuilder();
        fileUploadOption.setSize(fileSize);

        try {
            RequestWrapper requestWrapper = new RequestWrapper(700, fileUploadOption, onFileUploadOption);

            return RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return "";
    }
}
