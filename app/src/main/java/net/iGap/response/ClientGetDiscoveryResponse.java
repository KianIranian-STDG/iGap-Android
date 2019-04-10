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

import android.util.Log;

import net.iGap.fragments.dashboard.OnDiscoveryList;
import net.iGap.proto.ProtoClientGetDiscovery;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class ClientGetDiscoveryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientGetDiscoveryResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientGetDiscovery.ClientGetDiscoveryResponse.Builder builder = (ProtoClientGetDiscovery.ClientGetDiscoveryResponse.Builder) message;
        ((OnDiscoveryList) identity).onDiscoveryListReady(new ArrayList<ProtoGlobal.Discovery>(builder.getDiscoveriesList()), builder.getTitle());
    }

    @Override
    public void timeOut() {
        super.timeOut();
        ((OnDiscoveryList) identity).onDiscoveryListReady(new ArrayList<>(), "");
    }

    @Override
    public void error() {
        super.error();
        ((OnDiscoveryList) identity).onDiscoveryListReady(new ArrayList<>(), "");
    }
}


