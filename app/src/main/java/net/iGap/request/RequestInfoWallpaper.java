/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoInfoWallpaper;

public class RequestInfoWallpaper {

    public void infoWallpaper(net.iGap.proto.ProtoInfoWallpaper.InfoWallpaper.Type type) {
        ProtoInfoWallpaper.InfoWallpaper.Builder builder = ProtoInfoWallpaper.InfoWallpaper.newBuilder();
        builder.setFit(getFit());
        builder.setType(type);
        RequestWrapper requestWrapper = new RequestWrapper(504, builder, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private ProtoInfoWallpaper.InfoWallpaper.Fit getFit() {
        ProtoInfoWallpaper.InfoWallpaper.Fit fit = ProtoInfoWallpaper.InfoWallpaper.Fit.PHONE;
        if (G.context.getResources().getBoolean(R.bool.isTablet)) {
            fit = ProtoInfoWallpaper.InfoWallpaper.Fit.TABLET;
        }
        return fit;
    }
}
