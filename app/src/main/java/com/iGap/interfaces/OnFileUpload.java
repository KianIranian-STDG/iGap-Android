package com.iGap.interfaces;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

import com.iGap.proto.ProtoResponse;

/**
 * use this interface for each activities which need callbacks
 */
public interface OnFileUpload {
    void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response);

    void OnFileUploadInit(String token, double progress, long offset, int limit, String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUpload(double progress, long nextOffset, int nextLimit, String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUploadComplete(String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUploadTimeOut(String identity);
}
