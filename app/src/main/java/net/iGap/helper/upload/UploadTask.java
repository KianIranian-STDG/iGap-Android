package net.iGap.helper.upload;

import android.util.Log;

import net.iGap.G;
import net.iGap.helper.HelperDataUsage;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileUploadStatus;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestFileUpload;
import net.iGap.request.RequestFileUploadInit;
import net.iGap.request.RequestFileUploadOption;
import net.iGap.request.RequestFileUploadStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static net.iGap.G.handler;

public class UploadTask extends Thread implements RequestFileUploadOption.OnFileUploadOption, RequestFileUploadInit.OnFileUploadInit, RequestFileUpload.OnFileUpload, RequestFileUploadStatus.OnFileUploadStatus {
    public String identity;
    private OnUploadListener onUploadListener;
    private File file;

    private int progress;

    private byte[] fileHash;
    private String token;
    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;
    private ProtoGlobal.RoomMessageType uploadType;
    private long amountPendingUpload;
    private String lastRequestId;

    public UploadTask(String identity, File file, ProtoGlobal.RoomMessageType type, OnUploadListener onUploadListener) {
        this.identity = identity;
        this.file = file;
        this.onUploadListener = onUploadListener;
        this.uploadType = type;
        this.amountPendingUpload = 0L;
        this.progress = 1;
    }

    public UploadTask(RealmRoomMessage message, OnUploadListener onUploadListener) {
        this(message.getMessageId() + "", new File(message.getAttachment().getLocalFilePath()), message.getMessageType(), onUploadListener);
    }

    public UploadTask(RealmRoomMessage message, String compressedPass, OnUploadListener onUploadListener) {
        this(message.getMessageId() + "", new File(compressedPass), message.getMessageType(), onUploadListener);
    }

    public int getUploadProgress() {
        return progress;
    }


    @Override
    public void run() {
        super.run();
        try {
            openFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("bagi", file.getAbsolutePath());
        Log.d("bagi", file.length() + "fileLen");

        fileHash = AndroidUtils.getFileHashFromPath(file.getAbsolutePath());

        lastRequestId = new RequestFileUploadOption().fileUploadOption(file.length(), this);
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(String filePath) throws FileNotFoundException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(new File(filePath), "r");
        }
        if (fileChannel == null || !fileChannel.isOpen()) {
            fileChannel = randomAccessFile.getChannel();
        }
    }

    private void closeFile() throws IOException {
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

    private void onFinishUpload() {
        lastRequestId = new RequestFileUploadStatus().fileUploadStatus(this.token, this);
    }

    @Override
    public void onFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection) {
        try {
            byte[] bytesFromFirst = AndroidUtils.getBytesFromStart(fileChannel, firstBytesLimit);
            byte[] bytesFromLast = AndroidUtils.getBytesFromEnd(fileChannel, lastBytesLimit);
            lastRequestId = new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, file.length(), fileHash, file.getName(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadInit(String token, double progress, long offset, int limit) {
        this.progress = (int) progress;
        this.token = token;

        onUploadListener.onProgress(identity, this.progress);

        if (progress != 100.0) {
            upload(offset, limit);
        } else {
            onFinishUpload();
        }
    }

    private void upload(long offset, int limit) {
        try {
            byte[] bytes = AndroidUtils.getNBytesFromOffset(fileChannel, offset, limit);
            amountPendingUpload = bytes.length;
            lastRequestId = new RequestFileUpload().fileUpload(token, offset, bytes, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUpload(double progress, long nextOffset, int nextLimit) {
        this.progress = (int) progress;

        if (progress != 100.0) {
            onUploadListener.onProgress(identity, this.progress);
            upload(nextOffset, nextLimit);
        } else {
            onFinishUpload();
        }

        HelperDataUsage.progressUpload(amountPendingUpload, uploadType);
        if (progress == 100) {
            HelperDataUsage.increaseUploadFiles(uploadType);
        }
    }

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status, double progress, int recheckDelayMS) {
        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED) {
            this.progress = 100;
            onUploadListener.onFinish(identity, token);
            try {
                closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                notify();
            }
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING || (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.UPLOADING) && progress == 100D) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastRequestId = new RequestFileUploadStatus().fileUploadStatus(UploadTask.this.token, UploadTask.this);
                }
            }, recheckDelayMS);
        } else {
            lastRequestId = new RequestFileUploadOption().fileUploadOption(file.length(), this);
        }
    }

    @Override
    public void onFileUploadOptionError(int major, int minor) {
        Log.d("bagi", "onFileUploadOptionError major:" + major + "minor:" + minor);
        onUploadListener.onError(identity);
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void onFileUploadInitError(int major, int minor) {
        Log.d("bagi", "onFileUploadInitError major:" + major + "minor:" + minor);
        onUploadListener.onError(identity);
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void onFileUploadError(int major, int minor) {
        Log.d("bagi", "onFileUploadError major:" + major + "minor:" + minor);
        onUploadListener.onError(identity);
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void onFileUploadStatusError(int major, int minor) {
        Log.d("bagi", "onFileUploadStatusError major:" + major + "minor:" + minor);
        onUploadListener.onError(identity);
        synchronized (this) {
            notify();
        }

    }

    public void cancel() {
        if (lastRequestId != null) {
            G.requestQueueMap.remove(lastRequestId);
        }

        synchronized (this) {
            notify();
        }
    }
}