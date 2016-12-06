package com.iGap.module;

import android.support.annotation.Nullable;
import android.util.Log;

import com.iGap.G;
import com.iGap.interfaces.OnFileUpload;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnFileUploadStatusResponse;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestFileUploadOption;
import com.iGap.request.RequestFileUploadStatus;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.iGap.G.handler;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/30/2016.
 */

/**
 * use this util for uploading files
 */
public class UploaderUtil implements OnFileUpload, OnFileUploadStatusResponse {
    // selected files (paths)
    private static CopyOnWriteArrayList<FileUploadStructure> mSelectedFiles =
            new CopyOnWriteArrayList<>();
    private OnFileUploadForActivities activityCallbacks;

    public void setActivityCallbacks(OnFileUploadForActivities onFileUploadForActivities) {
        this.activityCallbacks = onFileUploadForActivities;
        G.onFileUploadStatusResponse = this;
    }

    public void startUploading(FileUploadStructure uploadStructure, String identity) {
        if (activityCallbacks != null) {
            activityCallbacks.onUploadStarted(uploadStructure);
        }
        mSelectedFiles.add(uploadStructure);
        // make first request
        new RequestFileUploadOption().fileUploadOption(uploadStructure, identity);
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection,
                                   String fileHashAsIdentity, ProtoResponse.Response response) {
        try {
            Log.d("INJARO", "INJARO response:" + response);
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
            // getting bytes from file as server said
            byte[] bytesFromFirst =
                    AndroidUtils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast =
                    AndroidUtils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast,
                    fileUploadStructure.fileSize, fileUploadStructure.fileHash,
                    Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void OnFileUploadInit(String token, final double progress, long offset, int limit,
                                 final String identity, ProtoResponse.Response response) {
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        fileUploadStructure.token = token;

        if (activityCallbacks != null) {
            activityCallbacks.onFileUploading(fileUploadStructure, identity, progress);
        }

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes =
                        AndroidUtils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, identity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    /**
     * does file exist in the list
     * useful for preventing from calling onFileUploadComplete() multiple for a file
     *
     * @param messageId file hash
     * @return boolean
     */
    private boolean isFileExistInList(long messageId) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.messageId == messageId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit,
                             final String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ identity : "
                    + identity
                    + "  ||  progress : "
                    + progress);
            Log.i("BreakPoint", identity + " > bad az update progress");

            if (progress != 100.0) {
                FileUploadStructure fileUploadStructure = getSelectedFile(identity);
                if (activityCallbacks != null) {
                    activityCallbacks.onFileUploading(fileUploadStructure, identity, progress);
                }
                Log.i("BreakPoint", identity + " > fileUploadStructure");
                byte[] bytes =
                        AndroidUtils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset,
                                nextLimit);

                Log.i("BreakPoint", identity + " > after bytes");
                // make request till uploading has finished

                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes,
                        identity);
                Log.i("BreakPoint", identity + " > after fileUpload request");
            } else {
                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            }
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadComplete(String fileHashAsIdentity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);

        new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token,
                fileHashAsIdentity);
    }

    @Override
    public void onFileUploadTimeOut(RealmRoomMessage roomMessage, long roomId) {
        if (activityCallbacks != null) {
            FileUploadStructure structure = getSelectedFile(Long.toString(roomMessage.getMessageId()));
            if (structure != null) {
                activityCallbacks.onFileUploadTimeOut(structure, roomId);
            }
        }
    }

    /**
     * get file with hash string
     *
     * @param identity file hash
     * @return FileUploadStructure
     */
    @Nullable
    private FileUploadStructure getSelectedFile(String identity) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.messageId == Long.parseLong(identity)) {
                return structure;
            }
        }
        return null;
    }

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status,
                                   double progress, int recheckDelayMS, final String identity,
                                   ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        if (fileUploadStructure == null) {
            return;
        }
        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED) {
            if (activityCallbacks != null) {
                activityCallbacks.onFileUploaded(fileUploadStructure, identity);
            }

            // remove from selected files to prevent calling this method multiple times
            // multiple calling may occurs because of the server
            try {
                // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
                //removeFromSelectedFiles(identity);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
                e.printStackTrace();
            }

            // close file into structure
            try {
                if (fileUploadStructure != null) {
                    fileUploadStructure.closeFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING
                || (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.UPLOADING)
                && progress == 100D) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token,
                            identity);
                }
            }, recheckDelayMS);
        } else {
            startUploading(fileUploadStructure, identity);
        }
    }
}
