package net.iGap.fragments.emoji;

import android.util.Log;

import net.iGap.G;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.emoji.add.StructIGSticker;
import net.iGap.interfaces.OnStickerDownloaded;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HelperDownloadSticker {


    public static ConcurrentHashMap<String, UpdateStickerListener> hashMap = new ConcurrentHashMap<>();
    private static HashMap<String, String> downloadingQueue = new HashMap<>();
    private static String TAG = "abbasiStickerDownloader";

    public interface UpdateStickerListener {
        void OnProgress(String path, String token, int progress);

        void OnError(String token);
    }

    public static void stickerDownload(String token, String extention, long avatarSize, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, UpdateStickerListener updateStickerListener) {

        Log.i(TAG, "stickerDownload: " + token + " " + extention);

        try {
            String filePath = "";
            hashMap.put(token, updateStickerListener);
            G.onStickerDownloaded = new OnStickerDownloaded() {

                @Override
                public void onStickerDownloaded(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, int progress) {
                    UpdateStickerListener updateStickerListener1 = hashMap.get(token);
                    if (updateStickerListener1 != null) {
                        updateStickerListener1.OnProgress(filePath, token, 100);
                        hashMap.remove(token);
                    }
                }

                @Override
                public void onError(int major, Object identity) {

                }
            };

            filePath = downloadStickerPath(token, extention);
            if (new File(filePath).exists()) {
                if (updateStickerListener != null)
                    updateStickerListener.OnProgress(filePath, token, 100);
                return;
            }
            new RequestFileDownload().download(token, 0, (int) avatarSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, avatarSize, 0, type));

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void downloadSticker(StructIGSticker structIGSticker) {
//        if (!downloadingQueue.containsKey(structIGSticker.getToken())) {
//            downloadingQueue.put(structIGSticker.getToken(), structIGSticker.getFileName());
        try {
            G.onStickerDownloaded = new OnStickerDownloaded() {
                @Override
                public void onStickerDownloaded(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, int progress) {
//                        downloadingQueue.remove(structIGSticker.getToken());
                    EventManager.getInstance().postEvent(EventManager.STICKER_DOWNLOAD, filePath, token);
                }

                @Override
                public void onError(int major, Object identity) {
                    downloadingQueue.remove(structIGSticker.getToken());
                    Log.i(TAG, "onError: " + major + identity.toString());
                }
            };

            new RequestFileDownload().download(structIGSticker.getToken(), 0, structIGSticker.getFileSize(), ProtoFileDownload.FileDownload.Selector.FILE,
                    new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, structIGSticker.getToken(), structIGSticker.getPath(), ProtoFileDownload.FileDownload.Selector.FILE, structIGSticker.getFileSize(), 0, RequestFileDownload.TypeDownload.STICKER));

        } catch (RuntimeException e) {
            e.printStackTrace();
//            }
        }
    }
/*
    public static String stickerInternalPath(StructIGSticker structIGSticker){
        String finalPath = structIGSticker.getToken();
        if (structIGSticker.getType() == StructIGSticker.ANIMATED_STICKER)
            finalPath += ".json";
        else
            finalPath.endsWith()
        return  finalPath;
    }*/

    public static String createPathFile(String token, String extension) {

        String _mimeType = ".png";
        int index = extension.lastIndexOf(".");
        if (index >= 0) {
            _mimeType = extension.substring(index);
        }
        return G.DIR_STICKER + "/" + token + _mimeType;
    }

    public static String downloadStickerPath(String token, String extension) {

        String mimeType = ".png";
        int index = extension.lastIndexOf(".");
        if (index >= 0) {
            mimeType = extension.substring(index);
        }

        Log.i(TAG, "createPathFile: " + G.downloadDirectoryPath + "/" + token + mimeType);

        return G.downloadDirectoryPath + "/" + token + mimeType;
    }


}
