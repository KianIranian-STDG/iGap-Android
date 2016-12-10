package com.iGap.interfaces;

import com.iGap.module.FileUploadStructure;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/20/2016.
 */

public interface OnFileUploadForActivities {
    void onFileUploaded(FileUploadStructure uploadStructure, String identity);

    void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress);

    void onUploadStarted(FileUploadStructure struct);

    void onBadDownload(String token);

    void onFileTimeOut(String identity);
}
