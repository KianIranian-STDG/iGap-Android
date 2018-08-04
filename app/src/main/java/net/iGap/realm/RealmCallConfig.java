/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.realm;

import android.util.Log;

import com.google.gson.Gson;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import net.iGap.G;
import net.iGap.module.SerializationUtils;
import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.proto.ProtoSignalingGetLog;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class RealmCallConfig extends RealmObject {

    private boolean voice_calling;
    private boolean video_calling;
    private boolean screen_sharing;

    private byte[] IceServer;

    private RealmList<RealmIceProto> realmIceProto = null;

    public static void updateSignalingConfiguration(final ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.Builder builder) {

        Realm realm = Realm.getDefaultInstance();

        final RealmCallConfig realmCall = realm.where(RealmCallConfig.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmCallConfig item;

                if (realmCall == null) {
                    RealmCallConfig _rc = new RealmCallConfig();
                    item = realm.copyToRealm(_rc);
                } else {
                    item = realmCall;
                }

                item.setVoice_calling(builder.getVoiceCalling());
                item.setVideo_calling(builder.getVideoCalling());
                item.setScreen_sharing(builder.getScreenSharing());

                item.setIceServer(realm, builder.getIceServerList());

                G.needGetSignalingConfiguration = false;
            }
        });

        realm.close();
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

    public RealmList<RealmIceProto> getIceServer() {

        return realmIceProto;
    }

    public void setIceServer(Realm realm, List<ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer> iceServer) {

        for (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer mIceService : iceServer) {
            RealmIceProto iceProto = realm.createObject(RealmIceProto.class);
            iceProto.setUrl(mIceService.getUrl());
            iceProto.setUsername(mIceService.getUsername());
            iceProto.setCredential(mIceService.getCredential());
            realmIceProto.add(iceProto);
        }


    }
}




