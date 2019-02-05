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

                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                String _newPath = filePath.replace(G.DIR_TEMP, G.DIR_STICKER);
                                try {
                                    AndroidUtils.cutFromTemp(filePath, _newPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.i("CCCCCCCCCC", " type: " + type);

                                switch (type) {
                                    case STICKER:
                                        RealmStickers.updateUri(token, _newPath);
                                        break;
                                    case STICKER_DETAIL:
                                        RealmStickersDetails.updateUriStickersDetails(token, _newPath);
                                        break;
                                }
                            }
                        }, 1000);

                    }

                    @Override
                    public void onError(int major, Object identity) {
                        Log.i("CCCCCCCCCC", " onStickerDownloaded onError: " + major);
                    }
                };

            filePath = AndroidUtils.getFilePathWithCashId(token, extention, G.DIR_TEMP, false);
            new RequestFileDownload().download(token, 0, (int) avatarSize, selector, new RequestFileDownload.IdentityFileDownload(ProtoGlobal.RoomMessageType.IMAGE, token, filePath, selector, avatarSize, 0, type));

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


}
