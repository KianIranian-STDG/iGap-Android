package com.iGap.module;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

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
    public FileChannel fileChannel;
    public RandomAccessFile randomAccessFile;
    public long messageId;
    // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] for test purposes
    public long uploadStartTime;
    public long getNBytesTime;
    public long sendRequestsTime;
    public long elapsedInOnFileUpload;

    public FileUploadStructure(String fileName, long fileSize, String filePath, String fileHash) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileHash = fileHash;
    }

    public FileUploadStructure(String fileName, long fileSize, String filePath) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public FileUploadStructure(String fileName, long fileSize, String filePath, long messageId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.messageId = messageId;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public void openFile(String filePath) throws FileNotFoundException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(new File(filePath), "r");
        }
        if (fileChannel == null || !fileChannel.isOpen()) {
            fileChannel = randomAccessFile.getChannel();
        }
    }

    public void closeFile() throws IOException {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } finally {
            fileChannel = null;
            randomAccessFile = null;
        }
    }
}