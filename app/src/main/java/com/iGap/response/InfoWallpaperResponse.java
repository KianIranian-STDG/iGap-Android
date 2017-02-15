package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoInfoWallpaper;
import java.util.List;

public class InfoWallpaperResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoWallpaperResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoInfoWallpaper.InfoWallpaperResponse.Builder builder = (ProtoInfoWallpaper.InfoWallpaperResponse.Builder) message;
        List<ProtoGlobal.Wallpaper> wallpaperList = builder.getWallpaperList();

        if (G.onGetWallpaper != null) {
            G.onGetWallpaper.onGetWallpaperList(wallpaperList);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


