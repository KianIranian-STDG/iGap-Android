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

import net.iGap.observers.interfaces.OnInfoCountryResponse;
import net.iGap.proto.ProtoInfoCountry;

public class InfoCountryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public InfoCountryResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoInfoCountry.InfoCountryResponse.Builder infoCountryResponse = (ProtoInfoCountry.InfoCountryResponse.Builder) message;
        if (identity instanceof OnInfoCountryResponse) {
            ((OnInfoCountryResponse) identity).onInfoCountryResponse(infoCountryResponse.getCallingCode(), infoCountryResponse.getName(), infoCountryResponse.getPattern(), infoCountryResponse.getRegex());
        } else {
            throw new ClassCastException("identity must be : " + OnInfoCountryResponse.class.getName());
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (identity instanceof OnInfoCountryResponse) {
            ((OnInfoCountryResponse) identity).onError(majorCode, minorCode);
        } else {
            throw new ClassCastException("identity must be : " + OnInfoCountryResponse.class.getName());
        }
    }
}


