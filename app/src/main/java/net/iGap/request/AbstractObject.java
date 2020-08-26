package net.iGap.request;

import androidx.annotation.Nullable;

public abstract class AbstractObject {

    public Object getProtoObject() {
        return null;
    }

    public int getActionId() {
        return 0;
    }

    @Nullable
    public AbstractObject deserializeResponse(int constructor, Object protoObject) {
        return null;
    }

    public void readParams(Object message) {

    }
}
