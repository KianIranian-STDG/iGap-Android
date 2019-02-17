package net.iGap.fragments.emoji;

import net.iGap.G;
import net.iGap.interfaces.OnStickerDownloaded;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestFileDownload;

import java.io.File;

public class HelperDownloadSticker {

    public interface UpdateStickerListener {
        void OnProgress(String path, int progress);

        void OnError(String token);
    }

    public static void stickerDownload(String token, String extention, long avatarSize, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, UpdateStickerListener updateStickerListener) {

        try {
            String filePath = "";
            if (G.onStickerDownloaded == null)
                G.onStickerDownloaded = new OnStickerDownloaded() {
                    @Override
                    public void onStickerDownloaded(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, RequestFileDownload.TypeDownload type, int progress) {
                        if (updateStickerListener != null)
                            updateStickerListener.OnProgress(filePath, 100);
                    }

                    @Override
                    public void onError(int major, Object identity) {
                    }
                };

            filePath = createPathFile(token, extention);
            if (new File(filePath).exists()) {
                if (updateStickerListener != null)
                    updateStickerListener.OnProgress(filePath, 100);
                return;
            }
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
