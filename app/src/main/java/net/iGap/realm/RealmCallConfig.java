/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.G;
import net.iGap.proto.ProtoSignalingGetConfiguration;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;


public class RealmCallConfig extends RealmObject {

    private boolean voice_calling;
    private boolean video_calling;
    private boolean screen_sharing;
    private RealmList<RealmIceServer> realmIceServer = null;

    public static RealmCallConfig putOrUpdate(Realm asyncRealm, ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.Builder builder) {

        RealmCallConfig callConfig = asyncRealm.where(RealmCallConfig.class).findFirst();

        if (callConfig == null) {
            callConfig = asyncRealm.createObject(RealmCallConfig.class);
        }

        callConfig.setVoice_calling(builder.getVoiceCalling());
        callConfig.setVideo_calling(builder.getVideoCalling());
        callConfig.setScreen_sharing(builder.getScreenSharing());

        callConfig.setIceServer(asyncRealm, builder.getIceServerList());

        G.needGetSignalingConfiguration = false;

        return callConfig;
    }

    public boolean isVoice_calling() {
        return voice_calling;
    }

    public void setVoice_calling(boolean voice_calling) {
        this.voice_calling = voice_calling;
    }

    public boolean isVideo_calling() {
        return video_calling;
    }

    public void setVideo_calling(boolean video_calling) {
        this.video_calling = video_calling;
    }

    public boolean isScreen_sharing() {
        return screen_sharing;
    }

    public void setScreen_sharing(boolean screen_sharing) {
        this.screen_sharing = screen_sharing;
    }

    public RealmList<RealmIceServer> getIceServer() {

        return realmIceServer;
    }

    private void setIceServer(Realm realm, List<ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer> iceServer) {
        for (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer mIceService : iceServer) {
            RealmIceServer iceProto = realm.createObject(RealmIceServer.class);
            iceProto.setUrl(mIceService.getUrl());
            iceProto.setUsername(mIceService.getUsername());
            iceProto.setCredential(mIceService.getCredential());
            realmIceServer.add(iceProto);
        }
    }
}




