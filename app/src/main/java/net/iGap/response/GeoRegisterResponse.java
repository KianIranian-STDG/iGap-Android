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

import net.iGap.G;
import net.iGap.messenger.ui.fragments.NearbyFragment;
import net.iGap.module.GPSTracker;
import net.iGap.proto.ProtoGeoRegister;

public class GeoRegisterResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GeoRegisterResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGeoRegister.GeoRegisterResponse.Builder builder = (ProtoGeoRegister.GeoRegisterResponse.Builder) message;
        NearbyFragment.mapRegistrationStatus = builder.getEnable();
        if (G.onMapRegisterState != null) {
            G.onMapRegisterState.onState(builder.getEnable());
        }

        if (!builder.getEnable()) {
            GPSTracker.getGpsTrackerInstance().stopUsingGPS();
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


