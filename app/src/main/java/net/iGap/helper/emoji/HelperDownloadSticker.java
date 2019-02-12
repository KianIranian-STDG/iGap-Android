package net.iGap.helper.emoji;

import android.util.Log;

import net.iGap.G;
import net.iGap.interfaces.OnDownload;
import net.iGap.interfaces.OnStickerDownloaded;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmStickersDetails;
import net.iGap.request.RequestFileDownload;

import java.io.IOException;

public class HelperDownloadSticker {


    public static void stickerDownload(String token, String extention, long avatarSize, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type) {

        try {
            String filePath = "";
            if (G.onStickerDownloaded == null)
                G.onStickerDownloaded = new OnStickerDownloaded() {
                    @Override
                    public void onStickerDownloaded(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, int progress) {

                    }

                    @Override
                    public void onError(int major, Object identity) {
                    }
                };

            filePath = createPathFile(token, extention);
            new RequestFileDownload().download(token, 0, (int) avatarSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, avatarSize, 0, type));

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static String createPathFile(String token, String extension) {

        String _mimeType = ".png";
        int index = extension.lastIndexOf(".");
        if (index >= 0) {
            _mimeType = extension.substring(index);
        }
        return G.DIR_STICKER + "/" + token + _mimeType;
    }


}
