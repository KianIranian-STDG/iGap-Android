/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import net.iGap.G;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.proto.ProtoGeoGetConfiguration;
import net.iGap.realm.RealmGeoGetConfiguration;

public class GeoGetConfigurationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GeoGetConfigurationResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGeoGetConfiguration.GeoGetConfigurationResponse.Builder builder = (ProtoGeoGetConfiguration.GeoGetConfigurationResponse.Builder) message;

        for (ProtoGeoGetConfiguration.GeoGetConfigurationResponse.TileServer tileServer : builder.getTileServerList()) {
            FragmentiGapMap.mapUrls.add(tileServer.getBaseUrl());
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmGeoGetConfiguration realmGeoGetConfiguration = realm.where(RealmGeoGetConfiguration.class).findFirst();
                if (realmGeoGetConfiguration == null) {
                    realmGeoGetConfiguration = realm.createObject(RealmGeoGetConfiguration.class);
                } else {
                    if (realmGeoGetConfiguration.getMapCache() != null && !realmGeoGetConfiguration.getMapCache().equals(builder.getCacheId())) {
                        FragmentiGapMap.deleteMapFileCash();
                    }
                }
                realmGeoGetConfiguration.setMapCache(builder.getCacheId());
            }
        });
        realm.close();

        if (G.onGeoGetConfiguration != null) {
            G.onGeoGetConfiguration.onGetConfiguration();
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


