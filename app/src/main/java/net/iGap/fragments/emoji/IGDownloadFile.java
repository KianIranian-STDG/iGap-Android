package net.iGap.fragments.emoji;

import android.util.Log;

import net.iGap.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

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

        if (!fileHashMap.containsKey(fileStruct.id)) {
            fileHashMap.put(fileStruct.id, fileStruct);

            Log.i(TAG, "START DOWNLOAD size " + fileStruct.size + " id " + fileStruct.id + " path " + fileStruct.path);

            fileStruct.onStickerDownload = new OnStickerDownload() {
                @Override
                public void onStickerDownload(IGDownloadFileStruct igDownloadFileStruct) {
                    onSuccess(igDownloadFileStruct);
                }

                @Override
                public void onError(IGDownloadFileStruct igDownloadFileStruct) {
                    EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token);
                    fileHashMap.remove(igDownloadFileStruct.id);
                    Log.i(TAG, "ON DOWNLOAD FAILED: " + igDownloadFileStruct.id);
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

            sendRequest(fileStruct);
        } else {
            fileHashMap.remove(fileStruct.id);
            EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token);
            Log.i(TAG, "DOWNLOAD SUCCESSFULLY " + fileStruct.path);
        }
    }

}
