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

import net.iGap.observers.interfaces.OnResponse;

public class RequestWrapper {

    public long time = 0;
    public Object identity;
    protected int actionId;
    private Object protoObject;
    private String randomId;
    public OnResponse onResponse;

    public RequestWrapper(int actionId, Object protoObject, Object identity) {
        this(actionId, protoObject, identity, null);
    }

    public RequestWrapper(int actionId, Object protoObject, Object identity, OnResponse onResponse) {
        this.actionId = actionId;
        this.protoObject = protoObject;
        this.identity = identity;
        this.onResponse = onResponse;
    }

    public RequestWrapper(int actionId, Object protoObject) {
        this(actionId, protoObject, null, null);
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getProtoObject() {
        return protoObject;
    }

    public void setProtoObject(Object protoObject) {
        this.protoObject = protoObject;
    }

    public Object getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getRandomId() {
        return randomId;
    }

    public void setRandomId(String randomId) {
        this.randomId = randomId;
    }

    public OnResponse getOnResponse() {
        return onResponse;
    }
}
