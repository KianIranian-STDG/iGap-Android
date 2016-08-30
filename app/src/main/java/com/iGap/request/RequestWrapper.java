package com.iGap.request;

public class RequestWrapper {

    protected Object protoObject;
    protected int actionId;
    public long time;
    public String identity;

    public RequestWrapper(int actionId, Object protoObject, String identity) {
        this.actionId = actionId;
        this.protoObject = protoObject;
        this.identity = identity;
    }

    public RequestWrapper(int actionId, Object protoObject) {
        this.actionId = actionId;
        this.protoObject = protoObject;
    }

    public int getActionId() {
        return actionId;
    }

    public long getTime() {
        return time;
    }

    public Object getProtoObject() {
        return protoObject;
    }


}
