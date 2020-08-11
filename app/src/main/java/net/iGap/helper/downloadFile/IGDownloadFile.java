package net.iGap.helper.downloadFile;

import android.util.Log;

import net.iGap.fragments.emoji.OnStickerDownload;
import net.iGap.module.AndroidUtils;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.util.HashMap;

public class IGDownloadFile {
    private static IGDownloadFile IGDownloadFile;
    private String TAG = "abbasiDownloadFile";

    private HashMap<String, IGDownloadFileStruct> fileHashMap = new HashMap<>();

    public static IGDownloadFile getInstance() {
        if (IGDownloadFile == null)
            IGDownloadFile = new IGDownloadFile();
        return IGDownloadFile;
    }

    private IGDownloadFile() {

    }

    public void startDownload(IGDownloadFileStruct fileStruct) {

        if (fileStruct.size <= 0) {
            Log.e(TAG, "download with size 0 can not be start " + fileStruct.id);
            return;
        }

        if (new File(fileStruct.path).exists()) {
            Log.e(TAG, "this file exist " + fileStruct.path);
            return;
        }

        if (!fileHashMap.containsKey(fileStruct.id)) {
            fileHashMap.put(fileStruct.id, fileStruct);

            Log.i(TAG, "*" +
                    "\n\n ****** START DOWNLOAD ******" +
                    "\n id    -> " + fileStruct.id +
                    "\n size  -> " + fileStruct.size +
                    "\n path  -> " + fileStruct.path +
                    "\n token -> " + fileStruct.token +
                    "\n ***************************\n");

            fileStruct.onStickerDownload = new OnStickerDownload() {
                @Override
                public void onStickerDownload(IGDownloadFileStruct igDownloadFileStruct) {
                    onSuccess(igDownloadFileStruct);
                }

                @Override
                public void onError(IGDownloadFileStruct igDownloadFileStruct, int majorCode, int minorCode) {
                    AndroidUtils.deleteFile(new File(igDownloadFileStruct.path));
                    EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token);
                    if (fileStruct.listener != null)
                        fileStruct.listener.onDownloadFailed(fileStruct);
                    fileHashMap.remove(igDownloadFileStruct.id);

                    Log.e(TAG, "*" +
                            "\n\n ****** DOWNLOAD FAILED ******" +
                            "\n id         -> " + fileStruct.id +
                            "\n size       -> " + fileStruct.size +
                            "\n path       -> " + fileStruct.path +
                            "\n token      -> " + fileStruct.token +
                            "\n progress   -> " + fileStruct.progress +
                            "\n downloading size -> " + fileHashMap.size() +
                            "\n major Code -> " + majorCode +
                            "\n minor Code -> " + minorCode +
                            "\n ***************************\n");
                }
            };

            sendRequest(fileStruct);
        }
    }

    private void sendRequest(IGDownloadFileStruct fileStruct) {
        new RequestFileDownload().download(fileStruct.token, fileStruct.offset, 0, ProtoFileDownload.FileDownload.Selector.FILE, fileStruct);
    }

    private void onSuccess(IGDownloadFileStruct fileStruct) {
        if (fileStruct.progress < 100) {
            fileStruct.offset = fileStruct.nextOffset;
            Log.i(TAG, "PROGRESS: " + fileStruct.progress + " id -> " + fileStruct.id);
            sendRequest(fileStruct);
        } else {
            fileHashMap.remove(fileStruct.id);
            EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token);
            if (fileStruct.listener != null)
                fileStruct.listener.onDownloadComplete(fileStruct);

            Log.i(TAG, "*" +
                    "\n\n ****** DOWNLOAD SUCCESSFULLY ******" +
                    "\n id       -> " + fileStruct.id +
                    "\n size     -> " + fileStruct.size +
                    "\n path     -> " + fileStruct.path +
                    "\n token    -> " + fileStruct.token +
                    "\n progress -> " + fileStruct.progress +
                    "\n downloading size -> " + fileHashMap.size() +
                    "\n ***********************************\n");
        }
    }

}
