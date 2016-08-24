package com.iGap.request;

/**
 * Created by android on 2016-08-07.
 */
public class RequestWrapper {

    protected Object protoObject;
    protected int actionId;
    public long time;

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
