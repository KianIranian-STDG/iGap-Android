/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.G;
import net.iGap.messenger.ui.fragments.NearbyFragment;
import net.iGap.proto.ProtoGeoGetConfiguration;
import net.iGap.realm.RealmGeoGetConfiguration;

public class GeoGetConfigurationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

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
            NearbyFragment.mapUrls.add(tileServer.getBaseUrl());
        }

        if (builder.getTileServerList().size() == 0) {
            crashlytics.log("GeoGetConfigurationResponse -> TileServerList==0; time:" + System.currentTimeMillis());
        }

        RealmGeoGetConfiguration.putOrUpdate(builder.getCacheId());

        if (G.onGeoGetConfiguration != null) {
            G.onGeoGetConfiguration.onGetConfiguration();
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onGeoGetConfiguration != null) {
            G.onGeoGetConfiguration.getConfigurationTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
    }
}


