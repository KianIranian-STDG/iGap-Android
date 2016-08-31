package com.iGap.module;

import com.iGap.interface_package.OnFileUpload;
import com.iGap.proto.ProtoResponse;
import com.iGap.request.RequestFileUploadOption;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

/**
 * use this util for uploading files
 */
public class UploaderUtil implements OnFileUpload {
    private OnFileUpload activityCallbacks;

    public void setActivityCallbacks(OnFileUpload activityCallbacks) {
        this.activityCallbacks = activityCallbacks;
    }

    public void startUploading(long fileSize, String fileHash) {
        // make first request
        new RequestFileUploadOption().fileUploadOption(fileSize, fileHash);
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response) {
        activityCallbacks.OnFileUploadOption(firstBytesLimit, lastBytesLimit, maxConnection, fileHashAsIdentity, response);
    }

    @Override
    public void OnFileUploadInit(String token, double progress, long offset, int limit, String server, String fileHashAsIdentity, ProtoResponse.Response response) {
        activityCallbacks.OnFileUploadInit(token, progress, offset, limit, server, fileHashAsIdentity, response);
    }

    @Override
    public void onFileUpload(double progress, long nextOffset, int nextLimit, String fileHashAsIdentity, ProtoResponse.Response response) {
        activityCallbacks.onFileUpload(progress, nextOffset, nextLimit, fileHashAsIdentity, response);
    }

    @Override
    public void onFileUploadComplete(String fileHashAsIdentity, ProtoResponse.Response response) {
        activityCallbacks.onFileUploadComplete(fileHashAsIdentity, response);
    }
}
