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

import net.iGap.G;
import net.iGap.module.GPSTracker;
import net.iGap.proto.ProtoGeoGetRegisterStatus;
import net.iGap.request.RequestGeoUpdatePosition;

public class GeoGetRegisterStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GeoGetRegisterStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();


        final ProtoGeoGetRegisterStatus.GeoGetRegisterStatusResponse.Builder builder = (ProtoGeoGetRegisterStatus.GeoGetRegisterStatusResponse.Builder) message;
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (builder.getEnable()) {
                    GPSTracker gps = new GPSTracker();
                    gps.detectLocation();
                    if (gps.canGetLocation()) {
                        new RequestGeoUpdatePosition().updatePosition(gps.getLatitude(), gps.getLongitude());
                    }
                }
            }
        });
        if (G.onMapRegisterState != null) {
            G.onMapRegisterState.onState(builder.getEnable());
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


