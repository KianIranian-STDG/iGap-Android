package net.iGap.helper.downloadFile;

import net.iGap.G;
import net.iGap.fragments.emoji.OnStickerDownload;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.util.HashMap;

public class IGDownloadFile {
    private static IGDownloadFile IGDownloadFile;

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
            return;
        }

        if (new File(fileStruct.path).exists()) {
            return;
        }

        if (!fileHashMap.containsKey(fileStruct.id)) {
            fileHashMap.put(fileStruct.id, fileStruct);

            fileStruct.onStickerDownload = new OnStickerDownload() {
                @Override
                public void onStickerDownload(IGDownloadFileStruct igDownloadFileStruct) {
                    onSuccess(igDownloadFileStruct);
                }

                @Override
                public void onError(IGDownloadFileStruct igDownloadFileStruct, int majorCode, int minorCode) {
                    AndroidUtils.deleteFile(new File(igDownloadFileStruct.path));
                    G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token));
                    if (fileStruct.listener != null)
                        fileStruct.listener.onDownloadFailed(fileStruct);
                    fileHashMap.remove(igDownloadFileStruct.id);
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
            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.STICKER_DOWNLOAD, fileStruct.path, fileStruct.token));
            if (fileStruct.listener != null)
                fileStruct.listener.onDownloadComplete(fileStruct);

        }
    }

}
