package com.iGap.module;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

/**
 * use this class structure to put selected files for uploading
 * update token when provided by server
 */
public class FileUploadStructure {
    public String fileName;
    public long fileSize;
    public String filePath;
    public String fileHash;
    public String token;

    public FileUploadStructure(String fileName, long fileSize, String filePath, String fileHash) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileHash = fileHash;
    }
}
