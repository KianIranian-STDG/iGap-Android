package net.iGap.fragments.emoji;

import android.util.Log;

import net.iGap.G;
import net.iGap.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

import java.util.HashMap;

public class IGDownloadFile implements OnStickerDownload {
    private static IGDownloadFile IGDownloadFile;
    private String TAG = "abbasiStickerDownload";

    private HashMap<String, IGDownloadFileStruct> fileHashMap = new HashMap<>();

    public static IGDownloadFile getInstance() {
        if (IGDownloadFile == null)
            IGDownloadFile = new IGDownloadFile();
        return IGDownloadFile;
    }

    private IGDownloadFile() {
        G.onStickerDownload = this;
    }

    public void startDownload(IGDownloadFileStruct sticker) {
        fileHashMap.put(sticker.id, sticker);
        Log.i(TAG, " ------------------------------- ");
        Log.i(TAG, "id          " + sticker.id);
        Log.i(TAG, "progress    " + sticker.progress);
        Log.i(TAG, "offset      " + sticker.offset);
        Log.i(TAG, "next offset " + sticker.nextOffset);
        Log.i(TAG, "size        " + sticker.size);
        new RequestFileDownload().download(sticker.token, sticker.offset, 0, ProtoFileDownload.FileDownload.Selector.FILE, sticker);
    }

    @Override
    public void onStickerDownload(IGDownloadFileStruct sticker) {
        if (sticker.progress < 100) {
            sticker.offset = sticker.nextOffset;
            startDownload(sticker);
//            Log.i(TAG, "send request again " + sticker.id + " progress -> " + sticker.progress);
        } else {
            fileHashMap.remove(sticker.id);
            EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, sticker.path, sticker.token);
            Log.i(TAG, "POST EVENT FROM DOWNLOAD MANAGER " + sticker.token);
        }
    }
}
