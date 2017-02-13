package com.iGap.request;

import com.iGap.proto.ProtoInfoWallpaper;

public class RequestInfoWallpaper {

    public void infoWallpaper() {
        ProtoInfoWallpaper.InfoWallpaper.Builder builder = ProtoInfoWallpaper.InfoWallpaper.newBuilder();
        RequestWrapper requestWrapper = new RequestWrapper(504, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
